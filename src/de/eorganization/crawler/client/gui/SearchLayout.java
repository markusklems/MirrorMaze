/**
 * 
 */
package de.eorganization.crawler.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

import de.eorganization.crawler.client.gui.handler.AmiCriteriaHandler;
import de.eorganization.crawler.client.gui.handler.RefreshHandler;
import de.eorganization.crawler.client.model.Repository;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;

/**
 * @author mugglmenzel
 * 
 */
public class SearchLayout extends HLayout {

	private Logger log = Logger.getLogger(SearchLayout.class.getName());

	private RefreshHandler refreshHandler;
	private AmiCriteriaHandler amiCriteriaHandler;

	private SelectItem softwareCriterionSelect = new SelectItem(
			"Required Library");
	final SelectItem softwareCriteriaSelect = new SelectItem("Library Criteria");

	private List<String> softwareCriteria = new ArrayList<String>();

	/**
	 * 
	 */
	public SearchLayout(RefreshHandler refreshHandler,
			AmiCriteriaHandler amiCriteriaHandler) {
		this.refreshHandler = refreshHandler;
		this.amiCriteriaHandler = amiCriteriaHandler;

		createSearchLayout();
		refresh();
	}

	private void createSearchLayout() {

		setWidth100();
		setHeight(150);
		setBackgroundColor("#ffffff");

		SectionStack searchSections = new SectionStack();
		searchSections.setVisibilityMode(VisibilityMode.MUTEX);
		searchSections.setWidth100();
		searchSections.setHeight100();

		SectionStackSection searchSection = new SectionStackSection("Search");

		HLayout searchLayout = new HLayout();

		DynamicForm searchForm = new DynamicForm();
		searchForm.setAutoWidth();
		final TextItem searchQuery = new TextItem("Search");
		searchQuery.setWidth(300);
		searchQuery.setWrapTitle(false);
		searchQuery.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("Enter".equals(event.getKeyName())) {
					amiCriteriaHandler.putCriterion("query",
							searchQuery.getValueAsString());
					refreshHandler.refresh();
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
					amiCriteriaHandler.putCriterion("region", Repository
							.valueOf((String) regionFilter.getDisplayValue())
							.getName());
					refreshHandler.refresh();
				} catch (Exception e) {
				}
			}
		});
		regionFilter.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				try {
					if (event.getKeyName().equals("Enter")) {
						amiCriteriaHandler.putCriterion(
								"region",
								Repository
										.valueOf(
												(String) regionFilter
														.getDisplayValue())
										.getName());
						refreshHandler.refresh();
					}
				} catch (Exception e) {
				}
			}
		});
		searchForm.setFields(searchQuery, regionFilter);
		IButton searchButton = new IButton("Search", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				amiCriteriaHandler.putCriterion("query",
						searchQuery.getValueAsString());
				refreshHandler.refresh();
			}
		});

		searchLayout.addMember(searchForm);
		searchLayout.addMember(searchButton);

		searchSection.addItem(searchLayout);

		SectionStackSection filterSection = new SectionStackSection("Filter");

		HLayout filterLayout = new HLayout();

		DynamicForm criteriaForm = new DynamicForm();

		softwareCriterionSelect.setMultiple(false);
		softwareCriterionSelect
				.setMultipleAppearance(MultipleAppearance.PICKLIST);
		softwareCriterionSelect.setDefaultToFirstOption(true);

		IButton addCriterionButton = new IButton("add", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				softwareCriteria.add(softwareCriterionSelect.getValueAsString());
				updateCriteriaSelect();
			}
		});

		softwareCriteriaSelect.setMultiple(true);
		softwareCriteriaSelect.setMultipleAppearance(MultipleAppearance.GRID);
		softwareCriteriaSelect.setWidth(150);

		IButton deleteCriteriaButton = new IButton("delete",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						for (String value : softwareCriteriaSelect.getValues())
							softwareCriteria.remove(value);
						updateCriteriaSelect();
					}
				});
		IButton clearCriteriaButton = new IButton("clear", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				log.info("Clicked clear button.");
				softwareCriteria.clear();
				updateCriteriaSelect();
				amiCriteriaHandler.removeCriterion("softwareCriteria");
				refreshHandler.refresh();
			}
		});

		IButton filterCriteriaButton = new IButton("filter",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						log.info("Clicked filter button.");
						Joiner joiner = Joiner.on(",");
						amiCriteriaHandler.putCriterion("softwareCriteria",
								joiner.join(softwareCriteria));
						refreshHandler.refresh();
						log.info("refreshed after filter.");
					}
				});

		criteriaForm.setFields(softwareCriterionSelect, softwareCriteriaSelect);

		VLayout softwareCriteriaButtonLayout = new VLayout();
		softwareCriteriaButtonLayout.addMember(addCriterionButton);
		softwareCriteriaButtonLayout.addMember(deleteCriteriaButton);
		softwareCriteriaButtonLayout.addMember(clearCriteriaButton);
		softwareCriteriaButtonLayout.addMember(filterCriteriaButton);

		filterLayout.addMember(criteriaForm);
		filterLayout.addMember(softwareCriteriaButtonLayout);

		filterSection.addItem(filterLayout);

		searchSections.addSection(searchSection);
		searchSections.addSection(filterSection);

		addMember(searchSections);

	}

	public void refresh() {
		CrawlerServiceAsync crawler = GWT.create(CrawlerService.class);
		crawler.getSoftwareNames(new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
				for (String value : result)
					valueMap.put(value, value);
				softwareCriterionSelect.setValueMap(valueMap);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	private void updateCriteriaSelect() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		for (String value : softwareCriteria)
			valueMap.put(value, value);
		softwareCriteriaSelect.setValueMap(valueMap);
	}

}
