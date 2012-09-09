/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import de.eorganization.crawler.client.gui.ImageUtil;
import de.eorganization.crawler.client.gui.handler.MemberUpdatedHandler;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.services.CrawlerService;
import de.eorganization.crawler.client.services.CrawlerServiceAsync;

/**
 * @author mugglmenzel
 * 
 */
public class ProfileWindow extends Window {

	private Member member;

	private MemberUpdatedHandler updatedHandler;

	private DynamicForm form = new DynamicForm();

	private TextItem emailItem = new TextItem("email", "Email");

	private TextItem firstNameItem = new TextItem("firstName", "First Name");

	private TextItem lastNameItem = new TextItem("lastName", "Last Name");

	private TextItem AWSSecretItem = new TextItem("awsSecret", "AWS Secret Key");

	private TextItem AWSAccessItem = new TextItem("awsAccess", "AWS Access Key");

	private IButton saveButton = new IButton("Save");

	/**
	 * 
	 */
	public ProfileWindow(Member member, MemberUpdatedHandler handler) {
		this.member = member;
		this.updatedHandler = handler;
		createWindowLayout();
	}

	private void createWindowLayout() {
		setWidth(500);
		setHeight(500);
		setTitle("Profile");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		setAutoCenter(true);
		setDismissOnOutsideClick(true);
		setShowShadow(true);
		setShadowOffset(0);
		setShadowSoftness(10);

		addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		Img profileImg = new Img(member.getProfilePic(), 100,
				ImageUtil.getScaledImageHeight(member.getProfilePic(), 100));
		profileImg.setImageType(ImageStyle.STRETCH);

		emailItem.setValue(member.getEmail());
		emailItem.setDisabled(true);
		firstNameItem.setValue(member.getFirstname());
		firstNameItem.setRequired(true);
		lastNameItem.setValue(member.getLastname());
		lastNameItem.setRequired(true);
		AWSSecretItem.setValue(member.getAWSSecretKey());
		AWSAccessItem.setValue(member.getAWSAccessKey());

		form.setFields(emailItem, firstNameItem, lastNameItem, AWSSecretItem,
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
				if (form.validate()) {
					member.setEmail(emailItem.getValueAsString());
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
									updatedHandler.updated(result);
									destroy();
									SC.say("Saved.");
								}

								@Override
								public void onFailure(Throwable caught) {
									SC.warn("Something went wrong!");
								}
							});
				}
			}
		});

		buttons.addMember(saveButton);
		buttons.addMember(cancelButton);

		VLayout windowLayout = new VLayout();
		windowLayout.setMargin(10);
		windowLayout.setMembersMargin(15);
		windowLayout.addMember(profileImg);
		windowLayout.addMember(form);
		windowLayout.addMember(buttons);

		addItem(windowLayout);

	}

}
