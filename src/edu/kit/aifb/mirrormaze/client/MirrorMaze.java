package edu.kit.aifb.mirrormaze.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.ReadyHandler;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ScrolledEvent;
import com.smartgwt.client.widgets.events.ScrolledHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.SummaryFunction;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import edu.kit.aifb.mirrormaze.client.datasources.AmisDataSource;
import edu.kit.aifb.mirrormaze.client.model.Ami;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MirrorMaze implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private Logger log = Logger.getLogger(MirrorMaze.class.getName());

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final MirrorMazeServiceAsync mirrorMazeService = GWT
			.create(MirrorMazeService.class);

	/**
	 * Data sources
	 */
	private final AmisDataSource amisDataSource = new AmisDataSource();

	private PieChart pie;
	private boolean pieReady = false;

	private ListGrid amis = new ListGrid();

	private TabSet tabs = new TabSet();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		final Layout masterLayout = new VLayout();
		masterLayout.setWidth100();
		masterLayout.setHeight100();

		tabs.setWidth100();
		tabs.setHeight100();
		tabs.setBackgroundColor("white");
		
		/*
		DynamicForm s3bucket = new DynamicForm();

		final TextItem s3bucketName = new TextItem("s3bucketName",
				"S3 Bucket Name");
		s3bucketName.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Enter"))
					mirrorMazeService.importJSONFromS3(
							s3bucketName.getDisplayValue(),
							new AsyncCallback<Boolean>() {

								@Override
								public void onSuccess(Boolean result) {
									System.out.println("import success.");
								}

								@Override
								public void onFailure(Throwable caught) {
									System.out.println("import failed.");
								}
							});
			}
		});
		s3bucket.setItems(s3bucketName);
		masterLayout.addMember(s3bucket);
		*/

		VLayout amiLayout = new VLayout();
		
		amis.setWidth100();
		amis.setHeight100();
		ListGridField id = new ListGridField("id", "Id");
		ListGridField amiId = new ListGridField("amiId", "AMI Id");
		amiId.setIncludeInRecordSummary(false);
		amiId.setSummaryFunction(SummaryFunctionType.COUNT);
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
		description.setCanEdit(true);
		ListGridField executeLink = new ListGridField("executeLink", "Launch");
		executeLink.setType(ListGridFieldType.LINK);
		executeLink.setLinkText(Canvas.imgHTML("[SKINIMG]actions/forward.png",
				16, 16, "execute", "align=center", null));

		amis.setFields(id, amiId, name, location, architecture, ownerAlias,
				ownerId, description, executeLink);
		amis.setCanResizeFields(true);
		amis.setShowGridSummary(true);
		amis.setShowGroupSummary(true);

		amiLayout.addMember(amis);
		

		DynamicForm addAmi = new DynamicForm();

		HeaderItem addAmiHeader = new HeaderItem("addAmiHeader", "Add AMI");
		addAmiHeader.setValue("Add AMI");
		
		final TextItem addAmiId = new TextItem("addAmiId", "Ami Id");
		addAmiId.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Enter"))
					mirrorMazeService.saveAmi(null, addAmiId.getDisplayValue(),
							"", "", "", "Test AMI (" + new Date().getTime()
									+ ")", "", "", "", "",
							new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									System.out.println("Create AMI success.");
									refresh();
								}

								@Override
								public void onFailure(Throwable caught) {
									System.out.println("Create AMI failed.");
								}
							});
			}
		});
		addAmi.setItems(addAmiHeader, addAmiId);
		amiLayout.addMember(addAmi);
		
		Tab amiTable = new Tab("AMI List");
		amiTable.setPane(amiLayout);
		tabs.addTab(amiTable);

		final Tab statsTab = new Tab("Statistics");
		statsTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				refreshPie();				
			}
		});
		tabs.addTab(statsTab);
		final VLayout pieLayout = new VLayout();
		Runnable onLoadCallback = new Runnable() {
			public void run() {

				// Create a pie chart visualization.
				pie = new PieChart((AbstractDataTable) DataTable.create(),
						PieChart.createOptions());

				// pie.addSelectHandler(createSelectHandler(pie));
				pieLayout.addMember(pie);
				statsTab.setPane(pieLayout);
				pieLayout.draw();

				pieReady = true;
				refreshPie();
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				PieChart.PACKAGE);

		// masterLayout.addMember(new UploadTestsView().getContent());

		masterLayout.addMember(tabs);

		masterLayout.draw();
		refresh();
	}

	private void refresh() {

		mirrorMazeService.getAmis(new AsyncCallback<List<Ami>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<Ami> result) {
				amisDataSource.setAmis(result);
				amis.setData(amisDataSource.createListGridRecords());
				refreshPie();
			}

		});
	}

	private void refreshPie() {
		if (pieReady)
			pie.draw(getPieData(amis.getDataAsRecordList()), getPieOptions());
	}

	private AbstractDataTable getPieData(RecordList amiList) {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Owner Id");
		data.addColumn(ColumnType.NUMBER, "#");

		Set<String> uniqueOwners = new HashSet<String>();

		for (int i = 0; i < amiList.getLength(); i++) {
			Record record = amiList.get(i);
			uniqueOwners.add((record).getAttribute("ownerId"));
		}
		data.addRows(uniqueOwners.size());
		List<String> uniqueOwnersList = new ArrayList<String>(uniqueOwners);

		for (int i = 0; i < uniqueOwnersList.size(); i++) {
			data.setValue(i, 0, uniqueOwnersList.get(i));
			data.setValue(i, 1,
					amiList.findAll("ownerId", uniqueOwnersList.get(i)).length);
		}
		return data;
	}
	
	private Options getPieOptions() {
		Options options = Options.create();
		options.setWidth(400);
		options.setHeight(240);
		options.setTitle("AMI Owners");
		return options;
	}

}
