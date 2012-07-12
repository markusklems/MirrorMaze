package de.eorganization.crawler.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;


@RemoteServiceRelativePath("crawler")
public interface CrawlerService extends RemoteService {

	public void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType);

	public boolean importJSONFromS3(String S3Bucket);

	public ListResponse<Ami> getAmis(Map<String,Object> criteria, int startRow, int endRow);

	public Map<String, Long> getSoftwarePackagesPieData(String region);

	public Map<String, Long> getAmiOwnersPieData(String region);

	public int getNumberAmis(Map<String,Object> criteria);

	public int getNumberAllAmis(Map<String, Object> criteria);

}
