/**
 * 
 */
package de.eorganization.crawler.client.gui;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.RecordComponentPoolingMode;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.SummaryFunction;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

import de.eorganization.crawler.client.datasources.AmisDataSource;
import de.eorganization.crawler.client.gui.canvas.AmiSoftwareDetailsWindow;
import de.eorganization.crawler.client.model.LoginInfo;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;

/**
 * @author mugglmenzel
 * 
 */
public class AmisTab extends Tab {

	private ListGrid amis = new ListGrid();

	private Label amiNumber = new Label("0");

	private LoginInfo loginInfo;

	private AmisDataSource amisDataSource = new AmisDataSource(null,
			new HashMap<String, Object>());

	/**
	 * 
	 */
	public AmisTab(final LoginInfo loginInfo) {

		if (loginInfo == null)
			return;

		this.loginInfo = loginInfo;
		this.amisDataSource.setMemberId(getMemberId());

		setTitle("AMI List");
		setIcon("[SKINIMG]DatabaseBrowser/data.png");

		VLayout amiLayout = new VLayout();

		getAmis().setWidth100();
		getAmis().setHeight100();
		ListGridField id = new ListGridField("id", "Id");
		ListGridField amiId = new ListGridField("amiId", "AMI Id");
		amiId.setIncludeInRecordSummary(false);
		amiId.setSummaryFunction(SummaryFunctionType.COUNT);
		ListGridField repository = new ListGridField("repository", "Region");
		ListGridField name = new ListGridField("name", "Name");
		ListGridField location = new ListGridField("location", "Location");
		ListGridField architecture = new ListGridField("architecture",
				"Architecture");
		ListGridField ownerAlias = new ListGridField("ownerAlias",
				"Owner (alias)");
		ListGridField ownerId = new ListGridField("ownerId", "Owner (id)");
		ownerId.setShowGridSummary(true);
		ownerId.setSummaryFunction(new SummaryFunction() {
			public Object getSummaryValue(Record[] records, ListGridField field) {
				Set<String> uniqueOwners = new HashSet<String>();

				for (int i = 0; i < records.length; i++) {
					Record record = records[i];
					uniqueOwners.add((record).getAttribute("ownerId"));
				}
				return uniqueOwners.size() + " Owners";
			}
		});
		ListGridField description = new ListGridField("description",
				"Description");
		description.setType(ListGridFieldType.TEXT);
		// description.setCanEdit(true);
		ListGridField executeLink = new ListGridField("executeLink", "Launch");
		executeLink.setType(ListGridFieldType.LINK);
		executeLink.setLinkText(Canvas.imgHTML("[SKINIMG]actions/forward.png",
				16, 16, "execute", "align=center", null));

		getAmis().setFields(id, amiId, repository, name, location,
				architecture, ownerAlias, ownerId, description, executeLink);
		getAmis().setCanResizeFields(true);
		// amis.setShowGridSummary(true);
		// amis.setShowGroupSummary(true);

		getAmis().setDataSource(amisDataSource);
		getAmis().setAutoFetchData(true);
		getAmis().setRecordComponentPoolingMode(
				RecordComponentPoolingMode.RECYCLE);
		getAmis().setDataPageSize(10);
		/*getAmis().addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				new AmiSoftwareDetailsWindow(loginInfo.getMember(), getAmis()
						.getSelectedRecord().getAttributeAsLong("id")).show();

			}
		});*/
		getAmis().addRecordDoubleClickHandler(new RecordDoubleClickHandler() {

			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				
				if (event.isLeftButtonDown()) {					
					new AmiSoftwareDetailsWindow(loginInfo.getMember(), event
							.getRecord().getAttributeAsLong("id")).show();
				}
			}
		});
		amiLayout.addMember(getAmis());

		/*
		 * DynamicForm addAmi = new DynamicForm();
		 * 
		 * HeaderItem addAmiHeader = new HeaderItem("addAmiHeader", "Add AMI");
		 * addAmiHeader.setValue("Add AMI");
		 * 
		 * final TextItem addAmiId = new TextItem("addAmiId", "Ami Id");
		 * addAmiId.addKeyPressHandler(new KeyPressHandler() {
		 * 
		 * @Override public void onKeyPress(KeyPressEvent event) { if
		 * (event.getKeyName().equals("Enter")) crawlerService.saveAmi(null,
		 * addAmiId.getDisplayValue(), "", "", "", "Test AMI (" + new
		 * Date().getTime() + ")", "", "", "", "", new AsyncCallback<Void>() {
		 * 
		 * @Override public void onSuccess(Void result) { refresh(); }
		 * 
		 * @Override public void onFailure(Throwable caught) { } }); } });
		 * addAmi.setItems(addAmiHeader, addAmiId); amiLayout.addMember(addAmi);
		 */

		HLayout amiInfo = new HLayout();
		Label totalAMIs = new Label("AMIs available in this Region: ");
		totalAMIs.setWrap(false);
		totalAMIs.setAutoFit(true);
		amiInfo.addMember(totalAMIs);

		getAmiNumber().setWrap(false);
		getAmiNumber().setAutoFit(true);
		amiInfo.addMember(getAmiNumber());
		amiLayout.addMember(amiInfo);

		setPane(amiLayout);

	}

	/**
	 * @return the amis
	 */
	public ListGrid getAmis() {
		return amis;
	}

	/**
	 * @return the amiNumber
	 */
	public Label getAmiNumber() {
		return amiNumber;
	}

	public void refresh() {
		getAmis().setCriteria(
				new Criteria("criteria" + new Date().getTime(), getCriteria()
						.toString()));
		getAmis().invalidateCache();
		if (getAmis().willFetchData(getAmis().getCriteria()))
			getAmis().fetchData();

		CrawlerServiceAsync crawlerService = GWT.create(CrawlerService.class);
		crawlerService.getNumberAllAmis(getCriteria(),
				new AsyncCallback<Long>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("error " + caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Long result) {
						if (result != null)
							getAmiNumber().setContents(" " + result.toString());
					}
				});
	}

	private String getMemberId() {
		return loginInfo.getMember() != null ? loginInfo.getMember().getEmail()
				: null;
	}

	/**
	 * @return the criteria
	 */
	public Map<String, Object> getCriteria() {
		return amisDataSource.getCriteria();
	}
}
