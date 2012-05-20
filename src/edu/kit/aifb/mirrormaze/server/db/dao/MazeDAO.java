/**
 * 
 */
package edu.kit.aifb.mirrormaze.server.db.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import edu.kit.aifb.mirrormaze.client.model.Ami;
import edu.kit.aifb.mirrormaze.client.model.Software;

/**
 * @author mugglmenzel
 * 
 */
public class MazeDAO extends DAOBase {
	static {
		ObjectifyService.register(Ami.class);
	}

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

	public Ami getOrCreateAmi(Long id, String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		Ami found = id != null ? ofy().find(Ami.class, id) : null;
		found = ofy().get(
				ofy().query(Ami.class).filter("imageId", imageId).fetchKeys()
						.iterator().next());
		if (found == null) {
			Ami ami = new Ami(id, repository, imageId, imageLocation,
					imageOwnerAlias, ownerId, name, description, architecture,
					platform, imageType);
			ofy().put(ami);
			return ami;
		} else
			return found;
	}

	public Software getOrCreateSoftware(Long id, String amiId, String name,
			String version) {
		Software found = id != null ? ofy().find(Software.class, id) : null;
		if (found == null) {
			Key<Ami> amiKey = ofy().query(Ami.class).filter("imageId", "amiId")
					.fetchKeys().iterator().next();
			Software software = new Software(amiKey, name, version);
			ofy().put(software);
			return software;
		} else
			return found;
	}
}
