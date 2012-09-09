/**
 * 
 */
package de.eorganization.crawler.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.util.SC;
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

	private RefreshHandler refreshHandler;
	private AmiCriteriaHandler amiCriteriaHandler;

	private SelectItem softwareCriterionSelect = new SelectItem(
			"Required Library");

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
		setBackgroundColor("#ffffff");

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

		DynamicForm criteriaForm = new DynamicForm();

		softwareCriterionSelect.setMultiple(false);
		softwareCriterionSelect
				.setMultipleAppearance(MultipleAppearance.PICKLIST);

		IButton addCriterionButton = new IButton("add", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				softwareCriteria.add(softwareCriterionSelect.getValueAsString());
				updateCriteriaSelect();
			}
		});

		final SelectItem softwareCriteriaSelect = new SelectItem(
				"Library Criteria");
		softwareCriteriaSelect.setMultiple(true);
		softwareCriteriaSelect.setMultipleAppearance(MultipleAppearance.GRID);

		IButton deleteCriterionButton = new IButton("delete",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						for (String value : softwareCriteriaSelect.getValues())
							softwareCriteria.remove(value);
						updateCriteriaSelect();
					}
				});

		criteriaForm.setFields(softwareCriterionSelect, softwareCriteriaSelect);

		addMember(searchForm);
		addMember(searchButton);
		addMember(criteriaForm);
		addMember(addCriterionButton);
		addMember(deleteCriterionButton);
	}

	public void refresh() {
		CrawlerServiceAsync crawler = GWT.create(CrawlerService.class);
		crawler.getSoftwareNames(new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				softwareCriteria.clear();
				softwareCriteria.addAll(result);
				updateCriteriaSelect();
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
		softwareCriterionSelect.setValueMap(valueMap);
		SC.say("updated with " + valueMap);
	}

}
