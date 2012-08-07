/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;

/**
 * @author mugglmenzel
 * 
 */
public class ProfileWindow extends Window {

	private Member member;

	private DynamicForm form = new DynamicForm();

	private TextItem firstNameItem = new TextItem("firstName", "First Name");

	private TextItem lastNameItem = new TextItem("lastName", "Last Name");

	private TextItem AWSSecretItem = new TextItem("awsSecret", "AWS Secret Key");

	private TextItem AWSAccessItem = new TextItem("awsAccess", "AWS Access Key");

	private IButton saveButton = new IButton("Save");

	/**
	 * 
	 */
	public ProfileWindow(Member member) {
		this.member = member;
		createWindowLayout();
	}

	private void createWindowLayout() {
		setWidth(500);
		setHeight("70%");
		setTitle("Profile");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		firstNameItem.setValue(member.getFirstname());
		lastNameItem.setValue(member.getLastname());
		AWSSecretItem.setValue(member.getAWSSecretKey());
		AWSAccessItem.setValue(member.getAWSAccessKey());

		form.setFields(firstNameItem, lastNameItem, AWSSecretItem,
				AWSAccessItem);
		form.setAutoFocus(true);

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(15);
		buttons.setAlign(Alignment.CENTER);

		IButton cancelButton = new IButton("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				member.setFirstname(firstNameItem.getValueAsString());
				member.setLastname(lastNameItem.getValueAsString());
				member.setAWSSecretKey(AWSSecretItem.getValueAsString());
				member.setAWSAccessKey(AWSAccessItem.getValueAsString());
				
				CrawlerServiceAsync crawlerService = GWT
						.create(CrawlerService.class);
				crawlerService.updateMember(member,
						new AsyncCallback<Member>() {

							@Override
							public void onSuccess(Member result) {
								destroy();
								SC.say("Saved.");
							}

							@Override
							public void onFailure(Throwable caught) {
								SC.warn("Something went wrong!");
							}
						});
			}
		});

		buttons.addMember(saveButton);
		buttons.addMember(cancelButton);

		VLayout windowLayout = new VLayout();
		windowLayout.setMargin(10);
		windowLayout.setMembersMargin(15);
		windowLayout.addMember(form);
		windowLayout.addMember(buttons);

		addItem(windowLayout);

	}

}
