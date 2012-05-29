package edu.kit.aifb.mirrormaze.server;

import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.kit.aifb.mirrormaze.client.MirrorMazeService;
import edu.kit.aifb.mirrormaze.client.datasources.responseModel.ListResponse;
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
	public ListResponse<Ami> getAmis(String region, int startRow, int endRow) {
		return AmiManager.getAmis(region, startRow, endRow);
	}
	
	@Override
	public Map<String, Long> getAmiOwnersPieData(String region) {
		return AmiManager.getAmiOwnersPieData(region);
	}

	
	@Override
	public Map<String, Long> getSoftwarePackagesPieData(String region) {
		return AmiManager.getSoftwarePackagesPieData(region);
	}

}
