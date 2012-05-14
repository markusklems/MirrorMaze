package edu.kit.aifb.mirrormaze.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MirrorMazeServiceAsync {

	void saveAmi(String repository, String imageId, String imageLocation,
			String imageOwnerAlias, String ownerId, String name,
			String description, String architecture, String platform,
			String imageType, AsyncCallback<Void> callback);

	void importJSONFromS3(String S3Bucket, AsyncCallback<Boolean> callback);

}
