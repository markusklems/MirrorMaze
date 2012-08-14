package de.eorganization.crawler.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
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
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import de.eorganization.crawler.client.gui.AmisTab;
import de.eorganization.crawler.client.gui.TopLayout;
import de.eorganization.crawler.client.gui.canvas.LoginWindow;
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

	private PieChart pieAMIOwners;
	private boolean pieAMIOwnersReady = false;

	private PieChart pieSoftwarePackages;
	private boolean pieSoftwarePackagesReady = false;

	private TabSet tabs = new TabSet();
	private AmisTab amisTab;

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

						if (!loginInfo.isLoggedIn()) {
							createWelcomeLayout();
							if (getMember() != null) {
								new RegisterWindow(getMember()).show();
							}
						} else
							createMasterLayout();

					}
				});
	}

	private void createWelcomeLayout() {
		final Layout masterLayout = new VLayout();

		masterLayout.addMember(new TopLayout(loginInfo));

		VLayout welcomeInfo = new VLayout(10);
		welcomeInfo.setDefaultLayoutAlign(Alignment.CENTER);
		welcomeInfo.setAlign(VerticalAlignment.TOP);
		welcomeInfo.setHeight100();
		welcomeInfo.setWidth100();
		welcomeInfo.setBackgroundColor("#ffffff");

		Label welcomeLabel = new Label(
				"<h1 style=\"font-size: 40pt\">Welcome!</h1><p style=\"font-size: 20pt\"><span style=\"font-weight: bolder;\">The Crawler</span> <span style=\"font-style: italic; font-weight: bolder;\">n.</span> is an enhanced, continuously growing database of Amazon Machine Images (AMI). By permanently crawling public AMIs from the Amazon EC2 service, the Crawler collects information about the contents of AMIs, i.e., software libraries and versions. The large database supports DevOps, Cloud Developers and Cloud users with insights to compare and understand AMIs better. A search engine allows to find AMIs with a configuration that meets standards and requirements.</p><p style=\"font-size: 20pt\">Feel free to play with the Crawler or <a href=\"http://myownthemepark.com/prices-table/\">contact us for a premium account</a>. Simply sign in with a social user account (Facebook, Twitter, Google+).</p>");
		welcomeLabel.setWidth(600);
		welcomeLabel.setStyleName("welcome");

		Label loginAnchor = new Label(
				"<span style=\"font-size: 35pt; cursor: pointer; text-decoration: underline;\">Login</span>");
		loginAnchor.setAutoFit(true);
		loginAnchor.setWrap(false);
		loginAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new LoginWindow(loginInfo.getLoginUrl()).show();
			}
		});

		welcomeInfo.addMember(welcomeLabel);
		welcomeInfo.addMember(loginAnchor);

		masterLayout.addMember(welcomeInfo);

		masterLayout.setWidth100();
		masterLayout.setHeight100();
		masterLayout.setMaxHeight(700);
		masterLayout.draw();

	}

	private void createMasterLayout() {
		final Layout masterLayout = new VLayout();

		masterLayout.addMember(new TopLayout(loginInfo));
		masterLayout.addMember(createSearchLayout());
		masterLayout.addMember(createTabLayout());

		masterLayout.setWidth100();
		masterLayout.setHeight100();
		masterLayout.setMaxHeight(700);
		masterLayout.draw();

		refresh();
	}

	private Layout createSearchLayout() {
		HLayout searchLayout = new HLayout();
		searchLayout.setWidth100();
		searchLayout.setBackgroundColor("#ffffff");
		DynamicForm searchForm = new DynamicForm();
		searchForm.setAutoWidth();
		final TextItem searchQuery = new TextItem("Search");
		searchQuery.setWrapTitle(false);
		searchQuery.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("Enter".equals(event.getKeyName())) {
					amisTab.getAmis()
							.getCriteria()
							.setAttribute("query",
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
					amisTab.getAmis()
							.getCriteria()
							.setAttribute(
									"region",
									Repository.valueOf(
											(String) regionFilter
													.getDisplayValue())
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
						amisTab.getAmis()
								.getCriteria()
								.setAttribute(
										"region",
										Repository.valueOf(
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
				amisTab.getAmis().getCriteria()
						.setAttribute("query", searchQuery.getValueAsString());
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

		amisTab = new AmisTab(loginInfo);

		tabs.addTab(amisTab);
		tabs.addTab(new Tab("Compare AMIs", "[SKINIMG]actions/view.png"));
		tabs.addTab(new Tab("Scan AMI", "[SKINIMG]actions/search.png"));
		tabs.addTab(createStatisticsTab());
		if (getMember() != null && UserRole.ADMIN.equals(getMember().getRole()))
			tabs.addTab(createAdminTab());

		return tabs;
	}

	private Tab createStatisticsTab() {
		final Tab statsTab = new Tab("Statistics", "[SKINIMG]actions/edit.png");
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
					crawlerService.getSoftwarePackagesPieData(amisTab.getAmis()
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
					crawlerService.getAmiOwnersPieData(amisTab.getAmis()
							.getCriteria().getAttribute("region"),
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

	private Tab createAdminTab() {
		Tab adminTab = new Tab("Admin");
		adminTab.setDisabled(!(getMember() != null && UserRole.ADMIN
				.equals(getMember().getRole())));
		VLayout adminLayout = new VLayout();
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

		adminTab.setPane(adminLayout);
		return adminTab;
	}

	private void refresh() {
		amisTab.refresh();

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
