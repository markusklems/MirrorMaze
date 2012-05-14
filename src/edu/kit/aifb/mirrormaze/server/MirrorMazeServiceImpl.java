package edu.kit.aifb.mirrormaze.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.kit.aifb.mirrormaze.client.MirrorMazeService;
import edu.kit.aifb.mirrormaze.client.model.Ami;

@SuppressWarnings("serial")
public class MirrorMazeServiceImpl extends RemoteServiceServlet implements
		MirrorMazeService {

	@Override
	public void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) throws IllegalArgumentException {
		AmiManager.saveAmi(repository, imageId, imageLocation, imageOwnerAlias,
				ownerId, name, description, architecture, platform, imageType);
	}

	@Override
	public boolean importJSONFromS3(String S3Bucket) {
		return AmiManager.importJSONFromS3(S3Bucket);
	}

	@Override
	public List<Ami> getAmis() {
		return AmiManager.getAmis();
	}

}
