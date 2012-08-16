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
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.eorganization.crawler.client.datasources.responseModel.ListResponse;
import de.eorganization.crawler.client.model.Software;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;

public class AmiSoftwareDataSource extends GwtRpcDataSource {

	private Logger log = Logger
			.getLogger(AmiSoftwareDataSource.class.getName());

	private String memberId;

	private Long amiId;

	/**
	 * @param region
	 */
	public AmiSoftwareDataSource(String memberId, Long amiId) {
		super();
		this.memberId = memberId;
		this.amiId = amiId;
	}

	private ListGridRecord[] createListGridRecords(List<Software> software) {

		ListGridRecord[] result = new ListGridRecord[software.size()];

		int i = 0;

		for (Software soft : software) {
			result[i] = new ListGridRecord();
			result[i].setAttribute("id", soft.getId());
			result[i].setAttribute("name", soft.getName());
			result[i].setAttribute("version", soft.getVersion());

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

		Map<String, Object> criteria = new HashMap<String, Object>();
		if (request.getCriteria() != null)
			for (String attribute : request.getCriteria().getAttributes())
				criteria.put(attribute,
						request.getCriteria().getValues().get(attribute));

		mirrorMazeService.getAmiSoftware(memberId, amiId, criteria, start, end,
				new AsyncCallback<ListResponse<Software>>() {

					@Override
					public void onSuccess(ListResponse<Software> result) {
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

}