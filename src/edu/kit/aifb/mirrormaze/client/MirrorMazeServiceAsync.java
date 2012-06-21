package edu.kit.aifb.mirrormaze.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.kit.aifb.mirrormaze.client.datasources.responseModel.ListResponse;
import edu.kit.aifb.mirrormaze.client.model.Ami;

public interface MirrorMazeServiceAsync {

	void saveAmi(String repository, String imageId, String imageLocation,
			String imageOwnerAlias, String ownerId, String name,
			String description, String architecture, String platform,
			String imageType, AsyncCallback<Void> callback);

	void importJSONFromS3(String S3Bucket, AsyncCallback<Boolean> callback);

	void getAmis(String region, int startRow, int endRow, AsyncCallback<ListResponse<Ami>> callback);

	void getSoftwarePackagesPieData(String region,
			AsyncCallback<Map<String, Long>> callback);

	void getAmiOwnersPieData(String region,
			AsyncCallback<Map<String, Long>> callback);

	void getNumberAmis(String region, AsyncCallback<Integer> callback);

}
