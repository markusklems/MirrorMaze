package de.eorganization.crawler.server.services;

import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.eorganization.crawler.client.CrawlerService;
import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.UserRole;
import de.eorganization.crawler.server.AmiManager;


@SuppressWarnings("serial")
public class CrawlerServiceImpl extends RemoteServiceServlet implements
		CrawlerService {

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
	public ListResponse<Ami> getAmis(Map<String, Object> criteria,
			int startRow, int endRow) {
		return AmiManager.getAmis(criteria, startRow, endRow);
	}

	@Override
	public int getNumberAmis(Map<String, Object> criteria) {
		return AmiManager.getNumberAmis(criteria);
	}

	@Override
	public int getNumberAllAmis(Map<String, Object> criteria) {
		criteria.put("memberId", UserRole.ADMIN.getDefaultMemberId());
		return AmiManager.getNumberAmis(criteria);
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
