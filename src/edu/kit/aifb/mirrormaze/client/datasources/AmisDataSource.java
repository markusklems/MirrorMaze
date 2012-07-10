package edu.kit.aifb.mirrormaze.client.datasources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.kit.aifb.mirrormaze.client.MirrorMaze.Repository;
import edu.kit.aifb.mirrormaze.client.MirrorMazeService;
import edu.kit.aifb.mirrormaze.client.MirrorMazeServiceAsync;
import edu.kit.aifb.mirrormaze.client.datasources.responseModel.ListResponse;
import edu.kit.aifb.mirrormaze.client.model.Ami;

public class AmisDataSource extends GwtRpcDataSource {

	/**
	 * @param region
	 */
	public AmisDataSource() {
		super();
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
		MirrorMazeServiceAsync mirrorMazeService = GWT
				.create(MirrorMazeService.class);
		int start = request.getStartRow() != null ? request.getStartRow()
				.intValue() : 0;
		int end = request.getEndRow() != null ? request.getEndRow().intValue()
				: 0;

		Map<String, Object> criteria = new HashMap<String, Object>();
		for (String attribute : request.getCriteria().getAttributes())
			criteria.put(attribute,
					request.getCriteria().getAttributeAsObject(attribute));

		mirrorMazeService.getAmis(criteria, start, end,
				new AsyncCallback<ListResponse<Ami>>() {

					@Override
					public void onSuccess(ListResponse<Ami> result) {
						response.setData(createListGridRecords(result.getList()));
						response.setTotalRows(result.getTotalRecords());
						processResponse(requestId, response);
					}

					@Override
					public void onFailure(Throwable caught) {
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