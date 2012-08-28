/**
 * 
 */
package de.eorganization.crawler.client.gui;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * @author mugglmenzel
 * 
 */
public class IntrospectionTab extends Tab {

	/**
	 * 
	 */
	public IntrospectionTab() {
		createIntrospectionTab();
	}

	private void createIntrospectionTab() {
		setIcon("[SKINIMG]actions/search.png");
		setTitle("Crawl AMI");

		VLayout masterLayout = new VLayout(10);
		masterLayout.setAlign(Alignment.CENTER);

		HLayout amiFormLayout = new HLayout(5);
		amiFormLayout.setAlign(Alignment.CENTER);
		amiFormLayout.setAutoHeight();
		
		DynamicForm df = new DynamicForm();
		TextItem amiIdItem = new TextItem("amiId", "AMI ID");
		amiIdItem.setWrapTitle(false);
		df.setFields(amiIdItem);
		df.setAutoHeight();
		IButton crawlButton = new IButton(
				"<span style=\"font-size: 20pt\">Crawl!</span>",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						SC.warn("Sorry, this feature is not yet activated! We will come up with a pricing in the near future.");

					}
				});
		crawlButton.setWidth(120);
		crawlButton.setHeight(50);
		crawlButton.setWrap(false);
		amiFormLayout.addMember(df);
		amiFormLayout.addMember(crawlButton);

		masterLayout.addMember(amiFormLayout);
		masterLayout.addMember(new Label(
				"<span style=\"font-size: 14pt\">Pricing</span>"));

		setPane(masterLayout);
	}

}
