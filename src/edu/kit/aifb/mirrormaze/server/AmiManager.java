package edu.kit.aifb.mirrormaze.server;

import edu.kit.aifb.mirrormaze.server.db.dao.DaoFactory;

public class AmiManager {

	public static boolean saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		if (repository != null && imageId != null) {
			// Check for duplicate AMIs? TODO

			if (DaoFactory.getAmiDao().create(repository, imageId,
					imageLocation, imageOwnerAlias, ownerId, name, description,
					architecture, platform, imageType) != null) {
				return true;
			}
		} else {
			return false;
		}
		return false;

	}

}
