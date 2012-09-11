package de.eorganization.crawler.client.datasources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Ami;
import de.eorganization.crawler.client.model.Repository;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;

public class AmisDataSource extends GwtRpcDataSource {

	private Logger log = Logger.getLogger(AmisDataSource.class.getName());

	private String memberId;

	private Map<String, Object> criteria = new HashMap<String, Object>();

	/**
	 * @param region
	 */
	public AmisDataSource(String memberId, Map<String, Object> criteria) {
		super();
		this.memberId = memberId;
		this.criteria.putAll(criteria);
	}

	public ListGridRecord[] createListGridRecords(List<Ami> amis) {

		ListGridRecord[] result = new ListGridRecord[amis.size()];

		int i = 0;

		for (Ami ami : amis) {
			result[i] = new ListGridRecord();
			result[i].setAttribute("id", ami.getId());
			result[i].setAttribute("name", ami.getName());
			result[i].setAttribute("amiId", ami.getImageId());
			result[i].setAttribute("repository", ami.getRepository());
			result[i].setAttribute("location", ami.getImageLocation());
			result[i].setAttribute("architecture", ami.getArchitecture());
			result[i].setAttribute("ownerAlias", ami.getImageOwnerAlias());
			result[i].setAttribute("ownerId", ami.getOwnerId());
			result[i].setAttribute("description", ami.getDescription());
			Repository repo = Repository.findByName(ami.getRepository());
			result[i]
					.setAttribute(
							"executeLink",
							repo != null ? "https://console.aws.amazon.com/ec2/home?region="
									+ repo.getShortName()
									+ "#launchAmi="
									+ ami.getImageId()
									: "");

			i++;
		}

		return result;
	}

	@Override
	protected void executeFetch(final String requestId, DSRequest request,
			final DSResponse response) {
		CrawlerServiceAsync mirrorMazeService = GWT
				.create(CrawlerService.class);
		int start = request.getStartRow() != null ? request.getStartRow()
				.intValue() : 0;
		int end = request.getEndRow() != null ? request.getEndRow().intValue()
				: 0;

		log.info("retrieving amis...");
		mirrorMazeService.getAmis(memberId, criteria, start, end,
				new AsyncCallback<ListResponse<Ami>>() {

					@Override
					public void onSuccess(ListResponse<Ami> result) {
						log.info("got result for ami data source " + result);
						response.setData(createListGridRecords(result.getList()));
						response.setTotalRows(new Long(result.getTotalRecords())
								.intValue());
						processResponse(requestId, response);
					}

					@Override
					public void onFailure(Throwable caught) {
						log.log(Level.WARNING, caught.getLocalizedMessage(),
								caught);
						SC.warn("error retrieving AMIs " + caught.getLocalizedMessage());
					}
				});
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void executeRemove(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub

	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the criteria
	 */
	public Map<String, Object> getCriteria() {
		return criteria;
	}

}