package de.eorganization.crawler.client;

import java.util.HashMap;
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
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import de.eorganization.crawler.client.gui.AmisTab;
import de.eorganization.crawler.client.gui.IntrospectionTab;
import de.eorganization.crawler.client.gui.SearchLayout;
import de.eorganization.crawler.client.gui.TopLayout;
import de.eorganization.crawler.client.gui.canvas.IntroductionWindow;
import de.eorganization.crawler.client.gui.canvas.RegisterWindow;
import de.eorganization.crawler.client.gui.canvas.WelcomeWindow;
import de.eorganization.crawler.client.gui.handler.AmiCriteriaHandler;
import de.eorganization.crawler.client.gui.handler.MemberUpdatedHandler;
import de.eorganization.crawler.client.gui.handler.RefreshHandler;
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
						DOM.setStyleAttribute(RootPanel.get("loading")
								.getElement(), "display", "none");

						if (error instanceof OutOfQuotaException)
							SC.warn("Sorry! We are out of quota. Please donate or purchase a premium account to support our work and avoid over quota outages.");
						else
							SC.warn("Sorry! We are having technical diffculties.<br/>"
									+ "Error: " + error.getLocalizedMessage());
					}

					public void onSuccess(LoginInfo result) {
						DOM.setStyleAttribute(RootPanel.get("loading")
								.getElement(), "display", "none");
						loginInfo = result;
	
						if (!loginInfo.isLoggedIn()) {
							new WelcomeWindow(loginInfo).show();
							if (getMember() != null) {
								new RegisterWindow(getMember()).show();
							}
						} else {							
							if (getMember() != null
									&& getMember().isShowWelcomeInfo())
								new IntroductionWindow(getMember(),
										new MemberUpdatedHandler() {

											@Override
											public void updated(Member member) {
												loginInfo.setMember(member);
											}
										}).show();
						}
						
						createMasterLayout();

					}
				});
	}

	private void createMasterLayout() {
		final Layout masterLayout = new VLayout();

		masterLayout.addMember(new TopLayout(loginInfo));
		masterLayout.addMember(new SearchLayout(new RefreshHandler() {

			@Override
			public void refresh() {
				amisTab.refresh();
			}
		}, new AmiCriteriaHandler() {

			@Override
			public void removeCriterion(String name) {
				amisTab.getCriteria().remove(name);
			}

			@Override
			public void putCriterion(String name, Object value) {
				amisTab.getCriteria().put(name, value);
			}
		}));
		masterLayout.addMember(createTabLayout());

		masterLayout.setWidth100();
		masterLayout.setHeight100();
		masterLayout.setMaxHeight(700);
		masterLayout.draw();

		refresh();
	}

	private TabSet createTabLayout() {
		tabs.setWidth100();
		tabs.setHeight100();
		tabs.setBackgroundColor("white");

		amisTab = new AmisTab(loginInfo);

		tabs.addTab(amisTab);
		Tab compareTab = new Tab("Compare AMIs", "[SKINIMG]actions/view.png");
		compareTab.setDisabled(true);
		tabs.addTab(compareTab);
		tabs.addTab(new IntrospectionTab());
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
															SC.say("Ami counters reset.");
														}
													});
									}
								});

					}
				});
		resetAmiCountersButton.setAutoWidth();

		IButton updateSoftwareNamesButton = new IButton(
				"Update Software Names List", new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						SC.confirm(
								"Are you sure? Updating Software Names is database operation intensive and might affect billing!",
								new BooleanCallback() {

									@Override
									public void execute(Boolean value) {
										crawlerService
												.updateSoftwareNames(new AsyncCallback<Void>() {

													@Override
													public void onFailure(
															Throwable caught) {
														SC.warn("Could not update Software Names List!");
													}

													@Override
													public void onSuccess(
															Void result) {
														SC.say("Software Names List updated.");
													}
												});
									}
								});
					}
				});
		updateSoftwareNamesButton.setAutoWidth();

		adminLayout.addMember(resetAmiCountersButton);
		adminLayout.addMember(updateSoftwareNamesButton);

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
