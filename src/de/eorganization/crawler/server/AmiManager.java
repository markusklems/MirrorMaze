package de.eorganization.crawler.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.Language;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.model.Software;
import de.eorganization.crawler.client.model.UserRole;
import de.eorganization.crawler.server.db.dao.MazeDAO;
import de.eorganization.crawler.server.db.dao.ShardedCounter;

public class AmiManager {

	private static Logger log = Logger.getLogger(AmiManager.class.getName());

	private static MazeDAO dao = new MazeDAO();

	public static ListResponse<Ami> findAmis(String memberId, String query,
			String region, int startRow, int endRow) {
		int limit = -1;
		if (!UserRole.ADMIN.equals(getMember(memberId).getRole()))
			limit = 10;

		return dao.findAmis(memberId, query, region, limit, startRow, endRow);
	}

	public static Member findMemberByFilter(Map<String, Object> filter) {
		return dao.findMemberByFilter(filter);
	}

	public static Member findMemberBySocialId(String socialId) throws Exception {
		return dao.findMemberBySocialId(socialId);
	}

	public static long getAmiCount(String region) {
		return dao.getAmiCount(region);
	}

	public static ShardedCounter getAmiCounter(String region) {
		return dao.getAmiCounter(region);
	}

	public static Map<String, Long> getAmiOwnersPieData(String region) {
		Map<String, Long> ownerCount = new LinkedHashMap<String, Long>();
		QueryResultIterator<Ami> iterator = dao.ofy().query(Ami.class)
				.filter("repository", region).chunkSize(10000).fetch()
				.iterator();

		while (iterator.hasNext()) {
			Ami a = iterator.next();

			ownerCount.put(
					a.getOwnerId(),
					ownerCount.get(a.getOwnerId()) != null ? ownerCount.get(a
							.getOwnerId()) + 1 : 1);
		}

		return ownerCount;
	}

	public static ListResponse<Ami> getAmis() {
		return getAmis(UserRole.ADMIN.getDefaultMemberId(), "all", 0, -1);
	}

	public static ListResponse<Ami> getAmis(String memberId) {
		return getAmis(memberId, "all", 0, -1);
	}

	public static ListResponse<Ami> getAmis(String memberId,
			Map<String, Object> criteria) {
		return getAmis(memberId, criteria, 0, -1);
	}

	public static ListResponse<Ami> getAmis(String memberId,
			Map<String, Object> criteria, int startRow, int endRow) {
		log.info("getting amis for criteria " + criteria);
		String region = (String) criteria.get("region");
		if (criteria.get("query") != null) {
			String query = (String) criteria.get("query");
			return findAmis(memberId, query, region, startRow, endRow);
		}
		if (criteria.get("softwareCriteria") != null) {
			Splitter splitter = Splitter.on(",");
			List<String> requiredSoftware = new ArrayList<String>();
			for (String criterion : splitter.split((String) criteria
					.get("softwareCriteria")))
				requiredSoftware.add(criterion);
			return getAmisBySoftware(memberId, region, requiredSoftware,
					startRow, endRow);
		}

		return getAmis(memberId, region, startRow, endRow);

	}

	public static ListResponse<Ami> getAmis(String memberId, String region) {
		return getAmis(memberId, region, 0, -1);
	}

	public static ListResponse<Ami> getAmis(String memberId, String region,
			int startRow, int endRow) {

		try {
			log.info("request for Amis in region " + region + " from "
					+ startRow + " to " + endRow);

			List<Ami> amis = null;

			long total = getNumberAmis(memberId, region, null);
			endRow = endRow > total ? new Long(total).intValue() : endRow;
			int size = endRow - startRow > -1 ? endRow - startRow : 0;
			if (size > 0)
				amis = dao.getAmis(region, startRow, size);

			log.info("fetched Amis for region " + region + " # " + size
					+ " from a total of " + total + " records.");

			return amis != null ? new ListResponse<Ami>(total,
					new ArrayList<Ami>(amis)) : new ListResponse<Ami>(total,
					new ArrayList<Ami>());
		} catch (Exception e) {
			return new ListResponse<Ami>(0, new ArrayList<Ami>());
		}
	}

	public static ListResponse<Ami> getAmisBySoftware(String memberId,
			String region, List<String> requiredSoftware, int startRow,
			int endRow) {
		ListResponse<Ami> amis = null;

		int size = endRow - startRow > -1 ? endRow - startRow : 0;
		if (size > 0)
			amis = dao.getAmisBySoftware(region, requiredSoftware, startRow,
					size);
		return amis != null ? amis : new ListResponse<Ami>(0,
				new ArrayList<Ami>());
	}

	public static ListResponse<Software> getAmiSoftware(String memberId,
			Long amiId, Map<String, Object> criteria, int startRow, int endRow) {

		try {
			log.info("request for Software of Ami " + amiId + " from "
					+ startRow + " to " + endRow);

			List<Software> software = null;

			long total = getNumberAmiSoftware(memberId, amiId);
			endRow = endRow > total ? new Long(total).intValue() : endRow;
			int size = endRow - startRow > -1 ? endRow - startRow : 0;
			if (size > 0)
				software = dao.getAmiSoftware(amiId, startRow, size);

			log.info("fetched Software for Ami " + amiId + " # " + size
					+ " from a total of " + total + " records.");

			return software != null ? new ListResponse<Software>(total,
					new ArrayList<Software>(software))
					: new ListResponse<Software>(0, new ArrayList<Software>());
		} catch (Exception e) {
			return new ListResponse<Software>(0, new ArrayList<Software>());
		}

	}

	public static Member getMember(String memberId) {
		if (memberId == UserRole.ADMIN.getDefaultMemberId())
			return new Member("", UserRole.ADMIN);
		try {
			return memberId != null ? dao.ofy().get(Member.class, memberId)
					: null;
		} catch (NotFoundException e) {
			log.log(Level.WARNING, e.getLocalizedMessage(), e);
			return null;
		}
	}

	public static long getNumberAmis(String memberId,
			Map<String, Object> criteria) {
		String region = (String) criteria.get("region");
		String query = (String) criteria.get("query");
		return getNumberAmis(memberId, region, query);
	}

	public static long getNumberAmis(String memberId, String region,
			String query) {

		if (getMember(memberId) == null
				|| getMember(memberId).getRole() == UserRole.USER)
			return 10;

		try {

			if (query == null)
				return dao.getNumberAmis(region);

			SearchService ss = SearchServiceFactory.getSearchService();
			Index idx = ss.getIndex(IndexSpec.newBuilder().setName("amiIndex")
					.build());
			Results<ScoredDocument> results = idx.search(Query
					.newBuilder()
					.setOptions(
							QueryOptions.newBuilder().setReturningIdsOnly(true)
									.build()).build(query));

			return results.getNumberReturned();

		} catch (Exception e) {
			return 0;
		}

	}

	private static long getNumberAmiSoftware(String memberId, Long amiId) {
		if (getMember(memberId) == null
				|| getMember(memberId).getRole() == UserRole.USER)
			return 10;

		return dao.getNumberAmiSoftware(amiId);
	}

	public static Map<String, Long> getSoftwarePackagesPieData(String region) {
		Map<String, Long> softwareCount = new LinkedHashMap<String, Long>();
		QueryResultIterator<Software> iterator = dao.ofy()
				.query(Software.class).chunkSize(10000).fetch().iterator();

		while (iterator.hasNext()) {
			Software s = iterator.next();
			Ami a = dao.ofy().get(s.getAmi());
			if (region.equals(a.getRepository()))
				softwareCount.put(
						s.getName(),
						softwareCount.get(s.getName()) != null ? softwareCount
								.get(s.getName()) + 1 : 1);
		}

		return softwareCount;
	}

	public static boolean importJSONFromS3(String S3Bucket) {
		Gson gson = new Gson();

		try {
			AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(
					AmiManager.class
							.getResourceAsStream("AwsCredentials.properties")));
			if (!s3.doesBucketExist(S3Bucket)) {
				log.warning("S3 bucket doesn't exist. Creating.");
				s3.createBucket(S3Bucket);
			}

			List<S3ObjectSummary> jsonFiles = s3.listObjects(
					new ListObjectsRequest().withBucketName(S3Bucket)
							.withPrefix("ami-")).getObjectSummaries();
			if (jsonFiles.size() > 0) {
				try {
					for (S3ObjectSummary s3JSON : jsonFiles) {
						S3Object jsonFile = s3.getObject(new GetObjectRequest(
								S3Bucket, s3JSON.getKey()));
						Ami test = gson.fromJson(
								new InputStreamReader(jsonFile
										.getObjectContent()), Ami.class);
						System.out.println(test);
					}

				} catch (Exception e) {
					log.severe("Couldn't read files from s3 bucket " + S3Bucket
							+ "!");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Can't access S3 due to missing credentials file.");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.severe("Accessing S3 failed.");
		}

		Collection<String> jsons = new ArrayList<String>();
		for (String json : jsons) {
			System.out.println(json);
		}

		return true;
	}

	public static Member registerMember(Member member) {
		return dao.registerMember(member);
	}

	public static void resetAmiCounters() {
		Queue queue = QueueFactory.getQueue("ami-crawler-queue");
		queue.add(withUrl("/crawler/resetCounterAMI").header(
				"Host",
				BackendServiceFactory.getBackendService().getBackendAddress(
						"ami-crawler")));
	}

	public static boolean saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {

		// Check for duplicate AMIs? TODO

		return dao.createAmi(repository, imageId, imageLocation,
				imageOwnerAlias, ownerId, name, description, architecture,
				platform, imageType);
	}

	public static Language saveLanguage(String amiId, String repository,
			String name, String version) {
		Language newLanguage = null;
		Key<Ami> amiKey = dao.findAmiByImageIdAndRepository(amiId, repository);

		if (amiKey != null)
			newLanguage = dao.getOrCreateLanguage(null, amiKey, name, version);

		return newLanguage;
	}

	public static Language saveLanguage(String amiId, String repository,
			String name, String version, Map<String, String> attributes) {
		Language newLanguage = null;
		Key<Ami> amiKey = dao.findAmiByImageIdAndRepository(amiId, repository);

		if (amiKey != null)
			newLanguage = dao.getOrCreateLanguage(null, amiKey, name, version,
					attributes);

		return newLanguage;
	}

	public static Ami saveOrGetAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {

		// Check for duplicate AMIs? TODO
		return dao.getOrCreateAmi(null, repository, imageId, imageLocation,
				imageOwnerAlias, ownerId, name, description, architecture,
				platform, imageType);
	}

	public static Member saveOrGetMember(User user) {
		if (user == null)
			return null;
		try {
			return dao.ofy().get(Member.class, user.getEmail());
		} catch (NotFoundException e) {
			try {
				return dao.ofy().get(
						dao.ofy().put(
								new Member(user.getEmail(), user.getNickname(),
										UserRole.USER)));
			} catch (Exception ex) {
				return new Member(user.getEmail(), user.getNickname(),
						UserRole.USER);
			}
		}

	}

	public static Software saveSoftware(String amiId, String repository,
			String name, String version) {
		Software newSoftware = null;
		Key<Ami> amiKey = dao.findAmiByImageIdAndRepository(amiId, repository);

		if (amiKey != null)
			newSoftware = dao.getOrCreateSoftware(null, amiKey, name, version);

		return newSoftware;
	}

	public static Software saveSoftware(String amiId, String repository,
			String name, String version, Map<String, String> attributes) {
		Software newSoftware = null;
		Key<Ami> amiKey = dao.findAmiByImageIdAndRepository(amiId, repository);

		if (amiKey != null)
			newSoftware = dao.getOrCreateSoftware(null, amiKey, name, version,
					attributes);

		return newSoftware;
	}

	public static void updateLanguage(Language language) {
		dao.updateLanguage(language);
	}

	public static Member updateMember(Member member) {
		return dao.updateMember(member);
	}

	public static void updateSoftware(Software software) {
		dao.updateSoftware(software);
	}

	/**
	 * 
	 */
	public AmiManager() {
		super();
	}

	public static List<String> getSoftwareNames() {
		return dao.getSoftwareNames();
	}

	public static void updateSoftwareNames() {
		dao.updateSoftwareNames();
	}

	public static void updateSoftwareNamesViaServlet() {
		Queue queue = QueueFactory.getQueue("ami-crawler-queue");
		queue.add(withUrl("/crawler/updateSoftwareNames").header(
				"Host",
				BackendServiceFactory.getBackendService().getBackendAddress(
						"ami-crawler")));
	}

	public static void updateSoftwareNames(List<String> names) {
		dao.updateSoftwareNames(names);
	}

}
