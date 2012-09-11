package de.eorganization.crawler.server.services;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.model.Software;
import de.eorganization.crawler.client.model.UserRole;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.server.AmiManager;

@SuppressWarnings("serial")
public class CrawlerServiceImpl extends RemoteServiceServlet implements
		CrawlerService {

	private Logger log = Logger.getLogger(CrawlerServiceImpl.class.getName());

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
	public ListResponse<Ami> getAmis(String memberId,
			Map<String, Object> criteria, int startRow, int endRow) throws Exception {
		log.info("getAmis with " + memberId + ", " + criteria + ", " + startRow
				+ ", " + endRow);
		return AmiManager.getAmis(memberId, criteria, startRow, endRow);
	}

	@Override
	public long getNumberAmis(String memberId, Map<String, Object> criteria) {
		return AmiManager.getNumberAmis(memberId, criteria);
	}

	@Override
	public long getNumberAllAmis(Map<String, Object> criteria) {
		return AmiManager.getNumberAmis(UserRole.ADMIN.getDefaultMemberId(),
				criteria);
	}

	@Override
	public Map<String, Long> getAmiOwnersPieData(String region) {
		return AmiManager.getAmiOwnersPieData(region);
	}

	@Override
	public Map<String, Long> getSoftwarePackagesPieData(String region) {
		return AmiManager.getSoftwarePackagesPieData(region);
	}

	@Override
	public Member updateMember(Member member) {
		return AmiManager.updateMember(member);
	}

	@Override
	public void resetAmiCounters() {
		AmiManager.resetAmiCounters();
	}

	@Override
	public ListResponse<Software> getAmiSoftware(String memberId, Long amiId,
			Map<String, Object> criteria, int startRow, int endRow) {
		return AmiManager.getAmiSoftware(memberId, amiId, criteria, startRow,
				endRow);
	}

	@Override
	public Member registerMember(Member member) {
		return AmiManager.registerMember(member);
	}

	@Override
	public List<String> getSoftwareNames() {
		return AmiManager.getSoftwareNames();
	}

	@Override
	public void updateSoftwareNames() {
		AmiManager.updateSoftwareNamesViaServlet();
	}

}
