package de.eorganization.crawler.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
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
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.RecordComponentPoolingMode;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
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
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import de.eorganization.crawler.client.datasources.AmisDataSource;
import de.eorganization.crawler.client.gui.MemberUpdatedHandler;
import de.eorganization.crawler.client.gui.canvas.AmiSoftwareDetailsWindow;
import de.eorganization.crawler.client.gui.canvas.LoginWindow;
import de.eorganization.crawler.client.gui.canvas.ProfileWindow;
import de.eorganization.crawler.client.gui.canvas.RegisterWindow;
import de.eorganization.crawler.client.model.LoginInfo;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.model.UserRole;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;
import de.eorganization.crawler.client.services.LoginService;
import de.eorganization.crawler.client.services.LoginServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Crawler implements EntryPoint {

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
	private final CrawlerServiceAsync crawlerService = GWT
			.create(CrawlerService.class);

	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);

	/**
	 * Data
	 */

	private LoginInfo loginInfo;

	private Label welcomeLabel = new Label(
			"<span style=\"font-size: 20pt\">Checking status of login...</span>");
	Anchor profileAnchor = new Anchor(
			"<span style=\"font-size: 20pt\"> </span>", true);
	private Anchor loginAnchor = new Anchor(
			"<span style=\"font-size: 20pt\">Login</span>", true);

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
					}

					public void onSuccess(LoginInfo result) {
						DOM.setStyleAttribute(RootPanel.get("loading")
								.getElement(), "display", "none");
						loginInfo = result;

						if (getMember() != null
								&& getMember().getEmail() == null)
							new RegisterWindow(getMember(),
									new MemberUpdatedHandler() {

										@Override
										public void updated(Member member) {
											loginInfo.setMember(member);
										}
									}).show();
						else
							createMasterLayout();

					}
				});
	}

	private void createMasterLayout() {
		final Layout masterLayout = new VLayout();

		masterLayout.addMember(createTopLayout());
		masterLayout.addMember(createSearchLayout());
		masterLayout.addMember(createTabLayout());

		if (getMember() != null && UserRole.ADMIN.equals(getMember().getRole()))
			masterLayout.addMember(createAdminlayout());

		masterLayout.setWidth100();
		masterLayout.setHeight100();
		masterLayout.setMaxHeight(700);
		masterLayout.draw();

		refresh();
	}

	private Layout createTopLayout() {
		final Layout top = new HLayout();
		top.setWidth100();
		top.setBackgroundImage("/images/clouds.png");
		top.setBackgroundPosition("bottom");
		top.setBackgroundRepeat(BkgndRepeat.REPEAT_X);
		top.setAlign(Alignment.LEFT);

		Anchor crawlerLogo = new Anchor(new SafeHtmlBuilder()
				.appendHtmlConstant(
						Canvas.imgHTML("/images/crawler_logo.png", 397, 150))
				.toSafeHtml(), GWT.getHostPageBaseURL(), "_top");

		HLayout login = new HLayout();
		login.setMembersMargin(15);
		login.setAlign(Alignment.RIGHT);

		Img profileImg = new Img();
		profileImg.setHeight(20);
		// profileImg.setMaxWidth(50);

		welcomeLabel.setAutoWidth();
		welcomeLabel.setWrap(false);

		profileAnchor.setWordWrap(false);

		Label loginDivider = new Label(
				"<span style=\"font-size: 25px\">|</span>");
		loginDivider.setAutoWidth();

		loginAnchor.setWordWrap(false);

		if (loginInfo.isLoggedIn() && getMember() != null) {
			profileImg.setSrc(getMember().getProfilePic());
			welcomeLabel.setContents("");
			profileAnchor.setHTML("<span style=\"font-size: 20pt\">"
					+ getMember().getNickname() + "</span>");
			profileAnchor
					.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

						@Override
						public void onClick(
								com.google.gwt.event.dom.client.ClickEvent event) {
							new ProfileWindow(getMember(),
									new MemberUpdatedHandler() {

										@Override
										public void updated(Member member) {
											loginInfo.setMember(member);
										}
									}).show();
						}
					});

			loginAnchor.setHref(loginInfo.getLogoutUrl());
			loginAnchor
					.setHTML("<span style=\"font-size: 20pt\">Logout</span>");
		} else {

			welcomeLabel
					.setContents("<span style=\"font-size: 20pt\">Not logged in</span>");
			profileAnchor.setEnabled(false);
			profileAnchor.setVisible(false);

			loginAnchor
					.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

						@Override
						public void onClick(
								com.google.gwt.event.dom.client.ClickEvent event) {
							new LoginWindow(loginInfo.getLoginUrl()).show();
						}
					});
			loginAnchor.setHTML("<span style=\"font-size: 20pt\">Login</span>");

		}
		login.addMember(profileImg);
		login.addMember(welcomeLabel);
		login.addMember(profileAnchor);
		login.addMember(loginDivider);
		login.addMember(loginAnchor);

		top.addMember(crawlerLogo);
		top.addMember(login);

		return top;
	}

	private Layout createSearchLayout() {
		HLayout searchLayout = new HLayout();
		searchLayout.setWidth100();
		searchLayout.setBackgroundColor("#ffffff");
		DynamicForm searchForm = new DynamicForm();
		searchForm.setWidth(400);
		final TextItem searchQuery = new TextItem("Search");
		searchQuery.setWrapTitle(false);
		searchQuery.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("Enter".equals(event.getKeyName())) {
					amis.getCriteria().setAttribute("query",
							searchQuery.getValueAsString());
					refresh();
				}

			}
		});

		final ComboBoxItem regionFilter = new ComboBoxItem();
		regionFilter.setWrapTitle(false);
		regionFilter.setTitle("Select AWS Region");
		regionFilter
				.setTooltip("Select of which AWS Region all AMIs are shown");
		LinkedHashMap<String, String> regions = new LinkedHashMap<String, String>();
		regions.put("all", "all");
		for (Repository repo : Repository.values())
			regions.put(repo.getName(), repo.name());
		regionFilter.setValueMap(regions);
		regionFilter.setDefaultValue(Repository.US_EAST1.getName());
		regionFilter.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				try {
					amis.getCriteria().setAttribute(
							"region",
							Repository.valueOf(
									(String) regionFilter.getDisplayValue())
									.getName());
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
		searchForm.setFields(searchQuery, regionFilter);
		IButton searchButton = new IButton("Search", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				amis.getCriteria().setAttribute("query",
						searchQuery.getValueAsString());
				refresh();
			}
		});

		searchLayout.addMember(searchForm);
		searchLayout.addMember(searchButton);

		return searchLayout;
	}

	private TabSet createTabLayout() {
		tabs.setWidth100();
		tabs.setHeight100();
		tabs.setBackgroundColor("white");

		tabs.addTab(createAmisTab());
		tabs.addTab(new Tab("Compare AMIs"));
		tabs.addTab(new Tab("Scan AMI"));
		tabs.addTab(createStatisticsTab());

		return tabs;
	}

	private Tab createAmisTab() {
		VLayout amiLayout = new VLayout();

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

		amis.setDataSource(new AmisDataSource(getMember() != null ? getMember()
				.getEmail() : null));
		amis.setCriteria(new Criteria("region", Repository.EU_1.getName()));
		amis.setAutoFetchData(true);
		amis.setRecordComponentPoolingMode(RecordComponentPoolingMode.RECYCLE);
		amis.setDataPageSize(20);
		amis.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {

			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				if (event.isLeftButtonDown()) {
					new AmiSoftwareDetailsWindow(getMember(), event.getRecord()
							.getAttributeAsLong("id")).show();
				}
			}
		});
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
		amiNumber.setWrap(false);
		amiNumber.setAutoFit(true);
		amiInfo.addMember(amiNumber);
		amiLayout.addMember(amiInfo);

		Tab amiTable = new Tab("AMI List");
		amiTable.setPane(amiLayout);

		return amiTable;
	}

	private Tab createStatisticsTab() {
		final Tab statsTab = new Tab("Statistics");
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

		statsTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (loginInfo.isLoggedIn()) {
					crawlerService.getSoftwarePackagesPieData(amis
							.getCriteria().getAttribute("region"),
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
					crawlerService.getAmiOwnersPieData(amis.getCriteria()
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
			}
		});

		return statsTab;
	}

	private Layout createAdminlayout() {
		HLayout adminLayout = new HLayout();
		adminLayout.setVisible(getMember() != null
				&& UserRole.ADMIN.equals(getMember().getRole()));
		IButton resetAmiCountersButton = new IButton("Reset Ami Counters",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						SC.confirm(
								"Are you sure? Resetting AMI counters is database operation intensive and might affect billing!",
								new BooleanCallback() {

									@Override
									public void execute(Boolean value) {
										if (value)
											crawlerService
													.resetAmiCounters(new AsyncCallback<Void>() {

														@Override
														public void onFailure(
																Throwable caught) {
															SC.warn("Could not reset Ami counters!");
														}

														@Override
														public void onSuccess(
																Void result) {
															SC.say("Reset Ami counters.");
														}
													});
									}
								});

					}
				});
		adminLayout.addMember(resetAmiCountersButton);

		return adminLayout;
	}

	private void refresh() {
		amis.fetchData(amis.getCriteria());
		refreshAMINumber();
	}

	private void refreshAMINumber() {
		Map<String, Object> criteria = new HashMap<String, Object>();
		for (String attribute : amis.getCriteria().getAttributes())
			criteria.put(attribute,
					amis.getCriteria().getValues().get(attribute));

		crawlerService.getNumberAllAmis(criteria, new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(Long result) {
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

	private Member getMember() {
		return loginInfo != null ? loginInfo.getMember() : null;
	}

}
