package edu.kit.aifb.mirrormaze.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.kit.aifb.mirrormaze.client.datasources.responseModel.ListResponse;
import edu.kit.aifb.mirrormaze.client.model.Ami;

@RemoteServiceRelativePath("maze")
public interface MirrorMazeService extends RemoteService {

	public void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType);
	
	public boolean importJSONFromS3(String S3Bucket);

	public ListResponse<Ami> getAmis(String memberId, String region, int startRow, int endRow);

	public Map<String, Long> getSoftwarePackagesPieData(String region);

	public Map<String, Long> getAmiOwnersPieData(String region);

	public int getNumberAmis(String memberId, String region); 
}
