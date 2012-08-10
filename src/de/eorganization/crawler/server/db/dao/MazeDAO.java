/**
 * 
 */
package de.eorganization.crawler.server.db.dao;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.Language;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.model.Software;

/**
 * @author mugglmenzel
 * 
 */
public class MazeDAO extends DAOBase {
	static {
		ObjectifyService.register(Ami.class);
		ObjectifyService.register(Language.class);
		ObjectifyService.register(Software.class);
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
}
