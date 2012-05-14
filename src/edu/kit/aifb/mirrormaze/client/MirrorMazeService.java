package edu.kit.aifb.mirrormaze.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.kit.aifb.mirrormaze.client.model.Ami;

@RemoteServiceRelativePath("maze")
public interface MirrorMazeService extends RemoteService {

	public void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType);
	
	public List<Ami> getAmis();
	
	public boolean importJSONFromS3(String S3Bucket); 
}
