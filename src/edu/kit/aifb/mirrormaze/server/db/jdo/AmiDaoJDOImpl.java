package edu.kit.aifb.mirrormaze.server.db.jdo;

import edu.kit.aifb.mirrormaze.server.db.dao.AmiDao;
import edu.kit.aifb.mirrormaze.server.db.entity.AmiEntity;
import edu.kit.aifb.mirrormaze.server.db.model.AmiModel;

public class AmiDaoJDOImpl extends DaoJDOImpl<AmiModel, String> implements
		AmiDao {

	private static AmiDaoJDOImpl instance = new AmiDaoJDOImpl();

	private AmiDaoJDOImpl() {
		super(AmiEntity.class);
	}

	public static AmiDaoJDOImpl getInstance() {
		return instance;
	}

	@Override
	public AmiModel create(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		AmiModel toReturn = new AmiEntity(repository, imageId, imageLocation,
				imageOwnerAlias, ownerId, name, description, architecture,
				platform, imageType);
		persist(toReturn);
		return toReturn;
	}
}