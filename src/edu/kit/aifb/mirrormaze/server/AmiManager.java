package edu.kit.aifb.mirrormaze.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;

import edu.kit.aifb.mirrormaze.client.datasources.responseModel.ListResponse;
import edu.kit.aifb.mirrormaze.client.model.Ami;
import edu.kit.aifb.mirrormaze.client.model.Language;
import edu.kit.aifb.mirrormaze.client.model.Software;
import edu.kit.aifb.mirrormaze.client.model.User;
import edu.kit.aifb.mirrormaze.client.model.UserRole;
import edu.kit.aifb.mirrormaze.server.db.dao.MazeDAO;

public class AmiManager {

	private static Logger log = Logger.getLogger(AmiManager.class.getName());

	private static MazeDAO dao = new MazeDAO();

	/**
	 * 
	 */
	public AmiManager() {
		super();
	}

	public static boolean saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {

		// Check for duplicate AMIs? TODO
		Ami newAmi = dao.getOrCreateAmi(null, repository, imageId,
				imageLocation, imageOwnerAlias, ownerId, name, description,
				architecture, platform, imageType);
		if (newAmi != null && newAmi.getId() != null)
			return true;
		else
			return false;
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

	public static void updateSoftware(Software software) {
		dao.updateSoftware(software);
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

	public static void updateLanguage(Language language) {
		dao.updateLanguage(language);
	}

	public static ListResponse<Ami> getAmis() {
		return getAmis(UserRole.ADMIN.getDefaultUserId(), "all", 0, -1);
	}
	
	public static ListResponse<Ami> getAmis(Long userId) {
		return getAmis(userId, "all", 0, -1);
	}

	public static ListResponse<Ami> getAmis(Long userId, String region) {
		return getAmis(userId, region, 0, -1);
	}

	public static ListResponse<Ami> getAmis(Long userId, String region,
			int startRow, int endRow) {

		log.finer("request for region " + region + " from " + startRow + " to "
				+ endRow);

		QueryResultIterable<Key<Ami>> keys = null;
		boolean regionAll = "all".equals(region) || "".equals(region)
				|| region == null;

		int total = getNumberAmis(userId, region);
		endRow = endRow > total ? total : endRow;
		int size = endRow - startRow > -1 ? endRow - startRow : 0;
		if (size > 0)
			keys = regionAll ? dao.ofy().query(Ami.class).offset(startRow)
					.limit(size).chunkSize(size).fetchKeys() : dao.ofy()
					.query(Ami.class).filter("repository", region)
					.offset(startRow).limit(size).chunkSize(size).fetchKeys();

		log.fine("fetched for region " + region + " # " + size
				+ " from a total of " + total + " records: " + keys);

		return keys != null ? new ListResponse<Ami>(total, new ArrayList<Ami>(
				dao.ofy().get(keys).values())) : new ListResponse<Ami>(total,
				new ArrayList<Ami>());
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

	public static User getUser(Long userId) {
		if(userId == UserRole.ADMIN.getDefaultUserId())
			return new User("", UserRole.ADMIN);
		return userId != null ? dao.ofy().get(User.class, userId) : null;
	}

	public static int getNumberAmis(Long userId, String region) {
		return getUser(userId) == null
				|| getUser(userId).getRole() == UserRole.USER ? 10 : "all"
				.equals(region) || "".equals(region) || region == null ? dao
				.ofy().query(Ami.class).count() : dao.ofy().query(Ami.class)
				.filter("repository", region).count();

	}
}
