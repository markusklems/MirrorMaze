package de.eorganization.crawler.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.model.Software;

public interface CrawlerServiceAsync {

	void saveAmi(String repository, String imageId, String imageLocation,
			String imageOwnerAlias, String ownerId, String name,
			String description, String architecture, String platform,
			String imageType, AsyncCallback<Void> callback);

	void importJSONFromS3(String S3Bucket, AsyncCallback<Boolean> callback);

	void getAmis(String memberId, Map<String,Object> criteria, int startRow, int endRow,
			AsyncCallback<ListResponse<Ami>> callback);

	void getSoftwarePackagesPieData(String region,
			AsyncCallback<Map<String, Long>> callback);

	void getAmiOwnersPieData(String region,
			AsyncCallback<Map<String, Long>> callback);

	void getNumberAmis(String memberId, Map<String, Object> criteria,
			AsyncCallback<Long> callback);

	void getNumberAllAmis(Map<String, Object> criteria,
			AsyncCallback<Long> callback);

	void updateMember(Member member, AsyncCallback<Member> callback);

	void resetAmiCounters(AsyncCallback<Void> callback);

	void getAmiSoftware(String memberId, Long amiId,
			Map<String, Object> criteria, int startRow, int endRow,
			AsyncCallback<ListResponse<Software>> callback);

	void registerMember(Member member, AsyncCallback<Member> callback);

	void getSoftwareNames(AsyncCallback<List<String>> callback);

	void updateSoftwareNames(AsyncCallback<Void> callback);

}
