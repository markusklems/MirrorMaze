package edu.kit.aifb.mirrormaze.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.RecordComponentPoolingMode;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.SummaryFunction;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import edu.kit.aifb.mirrormaze.client.datasources.AmisDataSource;
import edu.kit.aifb.mirrormaze.client.model.LoginInfo;
import edu.kit.aifb.mirrormaze.client.model.Member;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MirrorMaze implements EntryPoint {

	public enum Repository {
		ALL("all", "all"), US_EAST1("ec2.us-east-1.amazonaws.com", "us-east-1"), US_WEST_1(
				"ec2.us-west-1.amazonaws.com", "us-west-1"), US_WEST_2(
				"ec2.us-west-2.amazonaws.com", "us-west-2"), EU_1(
				"ec2.eu-west-1.amazonaws.com", "eu-west-1"), SOUTH_ASIA_EAST_1(
				"ec2.ap-southeast-1.amazonaws.com", "ap-southeast-1"), NORTH_ASIA_EAST_1(
				"ec2.ap-northeast-1.amazonaws.com", "ap-northeast-1"), SOUTH_AMERICA_EAST_1(
				"ec2.sa-east-1.amazonaws.com", "sa-east-1");
		final String name;
		final String shortName;

		Repository(final String name, final String shortName) {
			this.name = name;
			this.shortName = shortName;
		}

		public String getName() {
			return name;
		}

		public String getShortName() {
			return shortName;
		}

		public static Repository findByName(String repository) {
			for (Repository r : values())
				if (r.getName().equals(repository))
					return r;
			return null;
		}

	}

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final MirrorMazeServiceAsync mirrorMazeService = GWT
			.create(MirrorMazeService.class);

	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);

	/**
	 * Data
	 */

	private Member member;

	private Label welcomeLabel = new Label("Checking status of ");
	private Anchor loginAnchor = new Anchor(" login");

	private PieChart pieAMIOwners;
	private boolean pieAMIOwnersReady = false;

	private PieChart pieSoftwarePackages;
	private boolean pieSoftwarePackagesReady = false;

	private Label amiNumber = new Label("0");
	private ListGrid amis = new ListGrid();

	private TabSet tabs = new TabSet();

	private Map<String, Long> amiOwnersPieData = new HashMap<String, Long>();
	private Map<String, Long> softwarePackagesPieData = new HashMap<String, Long>();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						welcomeLabel.setContents("");
						loginAnchor.setHref("/_ah/login");
						loginAnchor.setText("Hey fellow, login!");
					}

					public void onSuccess(LoginInfo result) {
						member = result.getMember();
						if (member != null)
							amis.getCriteria().setAttribute("memberId",
									member.getEmail());
						loginAnchor.setEnabled(true);
						if (result.isLoggedIn() && member != null) {
							welcomeLabel.setContents("Welcome, "
									+ member.getNickname() + "! ");
							loginAnchor.setHref(result.getLogoutUrl());
							loginAnchor.setText(" (logout)");
						} else {
							welcomeLabel.setContents("");
							loginAnchor.setHref(result.getLoginUrl());
							loginAnchor.setText("Hey fellow, login!");
						}

					}
				});

		final Layout masterLayout = new VLayout();

		HLayout loginLayout = new HLayout();
		welcomeLabel.setAutoWidth();
		welcomeLabel.setWrap(false);
		loginLayout.addMember(welcomeLabel);
		loginAnchor.setWordWrap(false);
		loginLayout.addMember(loginAnchor);
		masterLayout.addMember(loginLayout);
		
		HLayout searchLayout = new HLayout();
		DynamicForm searchForm = new DynamicForm();
		final TextItem searchQuery = new TextItem("Search");
		searchQuery.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if("Enter".equals(event.getKeyName())) {
					amis.getCriteria().setAttribute("query", searchQuery.getValueAsString());
					refresh();
				}
					
			}
		});
		searchForm.setFields(searchQuery);
		searchLayout.addMember(searchForm);
		masterLayout.addMember(searchLayout);
		

		tabs.setWidth100();
		tabs.setHeight100();
		tabs.setBackgroundColor("white");

		VLayout amiLayout = new VLayout();

		DynamicForm amiFilter = new DynamicForm();
		final ComboBoxItem regionFilter = new ComboBoxItem();
		regionFilter.setTitle("Select AWS Region");
		regionFilter.setHint("Select of which AWS Region all AMIs are shown");
		LinkedHashMap<String, String> regions = new LinkedHashMap<String, String>();
		regions.put("all", "all");
		for (Repository repo : Repository.values())
			regions.put(repo.getName(), repo.name());
		regionFilter.setValueMap(regions);
		regionFilter.setDefaultValue(Repository.EU_1.getName());
		regionFilter.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				try {
					amis.setCriteria(new Criteria("region", Repository.valueOf(
							(String) regionFilter.getDisplayValue()).getName()));
					refresh();
				} catch (Exception e) {
				}
			}
		});
		regionFilter.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				try {
					if (event.getKeyName().equals("Enter"))
						amis.getCriteria().setAttribute(
								"region",
								Repository
										.valueOf(
												(String) regionFilter
														.getDisplayValue())
										.getName());

					refresh();
				} catch (Exception e) {
				}
			}
		});
		amiFilter.setFields(regionFilter);
		amiLayout.addMember(amiFilter);

		amis.setWidth100();
		amis.setHeight100();
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
		description.setCanEdit(true);
		ListGridField executeLink = new ListGridField("executeLink", "Launch");
		executeLink.setType(ListGridFieldType.LINK);
		executeLink.setLinkText(Canvas.imgHTML("[SKINIMG]actions/forward.png",
				16, 16, "execute", "align=center", null));

		amis.setFields(id, amiId, repository, name, location, architecture,
				ownerAlias, ownerId, description, executeLink);
		amis.setCanResizeFields(true);
		// amis.setShowGridSummary(true);
		// amis.setShowGroupSummary(true);
		amis.setDataSource(new AmisDataSource());
		amis.setCriteria(new Criteria("region", Repository.EU_1.getName()));
		amis.setAutoFetchData(true);
		amis.setRecordComponentPoolingMode(RecordComponentPoolingMode.RECYCLE);
		amis.setDataPageSize(20);
		amiLayout.addMember(amis);

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
		 * (event.getKeyName().equals("Enter")) mirrorMazeService.saveAmi(null,
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
		amiNumber.setWrap(false);
		amiNumber.setAutoFit(true);
		amiInfo.addMember(amiNumber);
		amiLayout.addMember(amiInfo);

		Tab amiTable = new Tab("AMI List");
		amiTable.setPane(amiLayout);
		tabs.addTab(amiTable);

		final Tab statsTab = new Tab("Statistics");
		statsTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				mirrorMazeService.getSoftwarePackagesPieData(amis.getCriteria()
						.getAttribute("region"),
						new AsyncCallback<Map<String, Long>>() {

							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(Map<String, Long> result) {
								softwarePackagesPieData = result;
								refreshPie();
							}
						});
				mirrorMazeService.getAmiOwnersPieData(amis.getCriteria()
						.getAttribute("region"),
						new AsyncCallback<Map<String, Long>>() {

							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(Map<String, Long> result) {
								amiOwnersPieData = result;
								refreshPie();
							}
						});
			}
		});
		tabs.addTab(statsTab);
		final VLayout pieLayout = new VLayout();
		Runnable onLoadCallback = new Runnable() {
			public void run() {

				// Create a pie chart visualization.
				pieAMIOwners = new PieChart(
						(AbstractDataTable) DataTable.create(),
						PieChart.createOptions());
				pieSoftwarePackages = new PieChart(
						(AbstractDataTable) DataTable.create(),
						PieChart.createOptions());

				pieAMIOwners.setHeight("500px");
				pieSoftwarePackages.setHeight("500px");
				// pie.addSelectHandler(createSelectHandler(pie));

				pieLayout.addMember(pieAMIOwners);
				pieLayout.addMember(pieSoftwarePackages);

				statsTab.setPane(pieLayout);
				// pieLayout.draw();

				pieAMIOwnersReady = true;
				pieSoftwarePackagesReady = true;
				refreshPie();
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				PieChart.PACKAGE);

		tabs.addTab(new Tab("Scan AMI"));

		masterLayout.addMember(tabs);

		if ("standalone".equals(Window.Location.getParameter("mode"))) {
			RootPanel.get().getElement().getElementsByTagName("div").getItem(0)
					.removeFromParent();
			masterLayout.setWidth100();
			masterLayout.setHeight100();
			masterLayout.draw();
		} else {
			masterLayout.setWidth(970);
			masterLayout.setHeight(700);
			RootPanel.get("main").add(masterLayout);
		}

		refresh();
	}

	private void refresh() {
		amis.fetchData(amis.getCriteria());
		refreshAMINumber();
	}

	@SuppressWarnings("unchecked")
	private void refreshAMINumber() {
		mirrorMazeService.getNumberAmis((Map<String,Object>) amis.getCriteria().getValues(), new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(Integer result) {
				if (result != null)
					amiNumber.setContents(" " + result.toString());
			}
		});
	}

	private void refreshPie() {
		if (pieAMIOwnersReady)
			pieAMIOwners.draw(getAMIOwnersPieData(), getAMIOwnersPieOptions());
		if (pieSoftwarePackagesReady)
			pieSoftwarePackages.draw(getSoftwarePackagesPieData(),
					getSoftwarePackagesPieOptions());
	}

	private AbstractDataTable getAMIOwnersPieData() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Owner Id");
		data.addColumn(ColumnType.NUMBER, "#");

		Map<String, Long> owners = new HashMap<String, Long>(amiOwnersPieData);
		data.addRows(owners.size());
		int i = 0;
		for (String owner : owners.keySet()) {
			data.setValue(i, 0, owner);
			data.setValue(i, 1, owners.get(owner).intValue());
			i++;
		}
		return data;
	}

	private Options getAMIOwnersPieOptions() {
		Options options = Options.create();
		options.setWidth(400);
		options.setHeight(240);
		options.setTitle("AMI Owners");
		return options;
	}

	private AbstractDataTable getSoftwarePackagesPieData() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Software Package");
		data.addColumn(ColumnType.NUMBER, "#");

		Map<String, Long> packages = new HashMap<String, Long>(
				softwarePackagesPieData);
		data.addRows(packages.size());
		int i = 0;
		for (String software : packages.keySet()) {
			data.setValue(i, 0, software);
			data.setValue(i, 1, packages.get(software).intValue());
			i++;
		}

		return data;
	}

	private Options getSoftwarePackagesPieOptions() {
		Options options = Options.create();
		options.setWidth(400);
		options.setHeight(240);
		options.setTitle("Software Packages");
		return options;
	}

}
