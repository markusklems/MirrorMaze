/**
 * 
 */
package edu.kit.aifb.mirrormaze.server.db.dao;

import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import edu.kit.aifb.mirrormaze.client.model.Ami;

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

	public Ami getOrCreateAmi(Long id) {
		Ami found = id != null ? ofy().find(Ami.class, id) : null;
		if (found == null) {
			Ami ami = new Ami(id);
			ofy().put(ami);
			System.out.println("DAO saved " + ami + " with " + ami.getId());
			return ami;
		} else
			return found;
	}

	public Ami getOrCreateAmiFull(Long id, String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		System.out.println("ofy is " + ofy());
		Ami found = id != null ? ofy().find(Ami.class, id) : null;
		if (found == null) {
			Ami ami = new Ami(id, repository, imageId, imageLocation,
					imageOwnerAlias, ownerId, name, description, architecture,
					platform, imageType);
			ofy().put(ami);
			System.out.println("DAO saved " + ami + " with " + ami.getId());
			return ami;
		} else
			return found;
	}
}
