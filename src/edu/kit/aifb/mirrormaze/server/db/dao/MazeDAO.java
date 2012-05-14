/**
 * 
 */
package edu.kit.aifb.mirrormaze.server.db.dao;

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

	public Ami getOrCreateAmi(String id) {
		Ami found = ofy().find(Ami.class, id);
		if (found == null)
			return new Ami(id);
		else
			return found;
	}

	public Ami getOrCreateAmiFull(String id, String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		Ami found = ofy().find(Ami.class, id);
		if (found == null)
			return new Ami(id, repository, imageId, imageLocation,
					imageOwnerAlias, ownerId, name, description, architecture,
					platform, imageType);
		else
			return found;
	}
}
