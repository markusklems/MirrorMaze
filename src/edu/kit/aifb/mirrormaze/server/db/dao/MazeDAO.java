/**
 * 
 */
package edu.kit.aifb.mirrormaze.server.db.dao;

import java.util.Map;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import edu.kit.aifb.mirrormaze.client.model.Ami;
import edu.kit.aifb.mirrormaze.client.model.Language;
import edu.kit.aifb.mirrormaze.client.model.Software;
import edu.kit.aifb.mirrormaze.client.model.User;

/**
 * @author mugglmenzel
 * 
 */
public class MazeDAO extends DAOBase {
	static {
		ObjectifyService.register(Ami.class);
		ObjectifyService.register(Language.class);
		ObjectifyService.register(Software.class);
		ObjectifyService.register(User.class);
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
}
