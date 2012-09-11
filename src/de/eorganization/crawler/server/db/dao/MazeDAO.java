/**
 * 
 */
package de.eorganization.crawler.server.db.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.QueryOptions.Builder;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

import de.eorganization.crawler.client.OutOfQuotaException;
import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.Language;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.model.Software;
import de.eorganization.crawler.client.model.SoftwareName;

/**
 * @author mugglmenzel
 * 
 */
public class MazeDAO extends DAOBase {
	static {
		ObjectifyService.register(Ami.class);
		ObjectifyService.register(Language.class);
		ObjectifyService.register(Software.class);
		ObjectifyService.register(SoftwareName.class);
		ObjectifyService.register(Member.class);
	}

	private static Logger log = Logger.getLogger(MazeDAO.class.getName());

	/**
	 * 
	 */
	public MazeDAO() {
		this(new ObjectifyOpts().setSessionCache(true));
	}

	/**
	 * @param opts
	 */
	public MazeDAO(ObjectifyOpts opts) {
		super(opts);
	}

	public Ami getAmi(Long id) {
		return id != null ? ofy().find(Ami.class, id) : null;
	}

	public Ami getAmi(Key<Ami> key) {
		return key != null ? ofy().get(key) : null;
	}

	public boolean createAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {

		if (findAmiByImageIdAndRepository(imageId, repository) == null) {
			Ami ami = new Ami(null, repository, imageId, imageLocation,
					imageOwnerAlias, ownerId, name, description, architecture,
					platform, imageType);
			getAmiCounter(repository).increment();
			ofy().put(ami);
			return true;
		} else
			return false;

	}

	public Ami getOrCreateAmi(Long id, String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		Ami found = id != null ? ofy().find(Ami.class, id) : null;
		Key<Ami> foundKey = findAmiByImageIdAndRepository(imageId, repository);
		found = found == null && foundKey != null ? ofy().get(foundKey) : found;
		if (found == null) {
			Ami ami = new Ami(id, repository, imageId, imageLocation,
					imageOwnerAlias, ownerId, name, description, architecture,
					platform, imageType);
			getAmiCounter(repository).increment();
			ofy().put(ami);
			return ami;
		} else
			return found;
	}

	public Key<Ami> findAmiByImageIdAndRepository(String amiId,
			String repository) {
		return ofy().query(Ami.class).filter("imageId", amiId)
				.filter("repository", repository).getKey();
	}

	public Software getOrCreateSoftware(Long id, Key<Ami> amiKey, String name,
			String version) {
		Software found = id != null ? ofy().find(Software.class, id) : null;
		found = found != null ? found : ofy().query(Software.class)
				.ancestor(ofy().get(amiKey)).filter("name", name).get();
		log.finer("Found software " + found);
		if (found == null) {
			Software software = new Software(amiKey, name, version);
			ofy().put(software);
			log.fine("Saved software " + software);
			return software;
		} else
			return found;
	}

	public Software getOrCreateSoftware(Long id, Key<Ami> amiKey, String name,
			String version, Map<String, String> attributes) {
		Software found = id != null ? ofy().find(Software.class, id) : null;
		found = found != null ? found : ofy().query(Software.class)
				.ancestor(ofy().get(amiKey)).filter("name", name).get();
		log.finer("Found software " + found);
		if (found == null) {
			Software software = new Software(amiKey, name, version, attributes);
			ofy().put(software);
			log.fine("Saved software " + software);
			return software;
		} else
			return found;
	}

	public void updateSoftware(Software software) {
		ofy().put(software);
	}

	public Language getOrCreateLanguage(Long id, Key<Ami> amiKey, String name,
			String version) {
		Language found = id != null ? ofy().find(Language.class, id) : null;
		found = found != null ? found : ofy().query(Language.class)
				.ancestor(ofy().get(amiKey)).filter("name", name).get();
		log.finer("Found language " + found);
		if (found == null) {
			Language language = new Language(amiKey, name, version);
			ofy().put(language);
			log.fine("Saved language " + language);
			return language;
		} else
			return found;
	}

	public Language getOrCreateLanguage(Long id, Key<Ami> amiKey, String name,
			String version, Map<String, String> attributes) {
		Language found = id != null ? ofy().find(Language.class, id) : null;
		found = found != null ? found : ofy().query(Language.class)
				.ancestor(ofy().get(amiKey)).filter("name", name).get();
		log.finer("Found language " + found);
		if (found == null) {
			Language language = new Language(amiKey, name, version, attributes);
			ofy().put(language);
			log.fine("Saved language " + language);
			return language;
		} else
			return found;
	}

	public void updateLanguage(Language language) {
		ofy().put(language);
	}

	public ShardedCounter getAmiCounter(String region) {
		return isAmiAllOrRegion(region) ? new ShardedCounter("Ami")
				: new ShardedCounter("Ami_" + region);
	}

	/**
	 * Counter-based AMI Count that saves Database Operations
	 * 
	 * @param region
	 * @return Number of AMIs from Counter
	 */
	public long getNumberAmis(String region) {
		return getAmiCounter(region).getCount();
	}

	/**
	 * Native AMI Count retrieved from expensive Database Query
	 * 
	 * @param region
	 * @return Number of AMIs from Database Query
	 */
	public long getAmiCount(String region) {
		return isAmiAllOrRegion(region) ? ofy().query(Ami.class).count()
				: ofy().query(Ami.class).filter("repository", region).count();
	}

	/**
	 * Is given region parameter describing a region or all regions
	 * 
	 * @param region
	 * @return true - if region describes all regions, false - if regions is a
	 *         certain region
	 */

	private boolean isAmiAllOrRegion(String region) {
		return "all".equals(region) || "".equals(region) || region == null;
	}

	public Member updateMember(Member member) {
		Key<Member> mbrKey = ofy().put(member);
		return ofy().get(mbrKey);
	}

	public List<Software> getAmiSoftware(Long amiId, int start, int size) {
		return ofy().query(Software.class)
				.ancestor(ofy().get(Ami.class, amiId)).offset(start)
				.limit(size).chunkSize(size).list();
	}

	public List<Ami> getAmis(String region, int start, int size) {
		return isAmiAllOrRegion(region) ? ofy().query(Ami.class).offset(start)
				.limit(size).chunkSize(size).list() : ofy().query(Ami.class)
				.filter("repository", region).offset(start).limit(size)
				.chunkSize(size).list();
	}

	public long getNumberAmiSoftware(Long amiId) {
		return ofy().query(Software.class)
				.ancestor(ofy().get(Ami.class, amiId)).count();

	}

	public Member findMemberByFilter(Map<String, Object> filter) {
		Query<Member> query = ofy().query(Member.class);
		for (String key : filter.keySet()) {
			query.filter(key, filter.get(key));
		}
		return query.get();
	}

	public Member findMemberBySocialId(String socialId) throws Exception {
		try {
			return ofy().query(Member.class).filter("socialId", socialId).get();
		} catch (OverQuotaException oqe) {
			throw new OutOfQuotaException(oqe);
		}
	}

	public Member registerMember(Member member) {
		try {
			Key<Member> key = ofy().put(member);
			if (key != null)
				return ofy().get(key);
		} catch (Exception e) {
			log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		return null;
	}

	public ListResponse<Ami> findAmis(String memberId, String query,
			String region, int limit, int startRow, int endRow) {
		List<Ami> amis = new ArrayList<Ami>();
		List<ScoredDocument> results = new ArrayList<ScoredDocument>();
		try {
			log.info("searching for amis with query " + query + " in region "
					+ region);
			SearchService ss = SearchServiceFactory.getSearchService();
			Index idx = ss.getIndex(IndexSpec.newBuilder().setName("amiIndex")
					.build());
			Builder queryOptionsBuilder = QueryOptions.newBuilder();
			if (limit > -1)
				queryOptionsBuilder.setLimit(10);

			results.addAll(idx.search(
					com.google.appengine.api.search.Query
							.newBuilder()
							.setOptions(queryOptionsBuilder.build())
							.build((isAmiAllOrRegion(region) ? ""
									: "repository:" + region + " ") + query))
					.getResults());

			startRow = startRow < results.size() ? startRow : results.size();
			endRow = endRow < results.size() ? endRow : results.size();
			List<ScoredDocument> resultsSub = results.subList(startRow, endRow);
			for (ScoredDocument sd : resultsSub)
				amis.add(ofy().query(Ami.class)
						.filter("repository", sd.getId().split("\\+")[0])
						.filter("imageId", sd.getId().split("\\+")[1]).get());
		} catch (Exception e) {
			log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		return new ListResponse<Ami>(results.size(), amis);
	}

	public ListResponse<Ami> getAmisBySoftware(String region,
			List<String> requiredSoftware, int startRow, int size) {
		List<Ami> amis = new ArrayList<Ami>();
		Set<Key<Ami>> amiKeys = null;
		for (String name : requiredSoftware) {
			Set<Key<Ami>> keys = ofy().query(Software.class)
					.filter("name", name).fetchParentKeys();
			if (amiKeys == null)
				amiKeys = keys;
			else
				amiKeys.retainAll(keys);
		}

		amis.addAll(ofy().get(amiKeys).values());
		startRow = startRow < amis.size() ? startRow : amis.size();
		int endRow = startRow + size < amis.size() ? startRow + size : amis
				.size();
		List<Ami> amisSub = new ArrayList<Ami>(amis.subList(startRow, endRow));
		return new ListResponse<Ami>(amis.size(), amisSub);
	}

	public void updateSoftwareNames() {
		Set<String> softwareNamesSet = new HashSet<String>();
		Iterator<Software> it = ofy().query(Software.class).prefetchSize(10000)
				.chunkSize(10000).fetch().iterator();
		while (it.hasNext()) {
			Software sw = it.next();
			softwareNamesSet.add(sw.getName());
		}
		for (String name : softwareNamesSet)
			ofy().put(new SoftwareName(name));

	}

	public List<String> getSoftwareNames() {
		List<String> result = new ArrayList<String>();
		Iterator<SoftwareName> it = ofy().query(SoftwareName.class)
				.prefetchSize(100).chunkSize(100).fetch().iterator();
		while (it.hasNext()) {
			SoftwareName sw = it.next();
			result.add(sw.getName());
		}

		Collections.sort(result);
		return result;
	}

	public void updateSoftwareNames(List<String> names) {
		Set<String> softwareNamesSet = new HashSet<String>(names);
		for (String name : softwareNamesSet)
			ofy().put(new SoftwareName(name));
	}
}
