/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

import de.eorganization.crawler.client.model.Member;

/**
 * @author mugglmenzel
 * 
 */
public class LoginWindow extends Window {

	private String loginURL;

	/**
	 * 
	 */
	public LoginWindow(String loginURL) {
		this.loginURL = loginURL;

		createWindowLayout();
	}

	private void createWindowLayout() {

		setTitle("Login required");
		setWidth(700);
		setHeight(500);
		setAutoCenter(true);
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		setDismissOnOutsideClick(true);
		setShowShadow(true);
		setShadowOffset(0);
		setShadowSoftness(10);

		addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		final SectionStack sectionStack = new SectionStack();
		sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
		sectionStack.setWidth100();
		sectionStack.setHeight100();

		VLayout windowLayout = new VLayout();
		windowLayout.setMargin(10);
		windowLayout.setMembersMargin(15);

		Label welcomeLabel = new Label(
				"<span style=\"font-size: 20px\">Please Login or Register to access the Crawler.</span>");
		welcomeLabel.setHeight(50);
		welcomeLabel.setWrap(false);

		SectionStackSection registerSection = new SectionStackSection(
				"Register");
		registerSection.setExpanded(true);
		registerSection.setCanCollapse(false);
		sectionStack.addSection(registerSection);

		HLayout registerLayout = new HLayout();
		registerLayout.setMargin(10);
		registerLayout.setAlign(Alignment.CENTER);
		Label register = new Label(
				"<span style=\"font-size: 18px; font-weight: bolder; text-decoration: underline; cursor: pointer;\">Register</span>");
		register.setHeight(40);
		register.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				destroy();
				new RegisterWindow(new Member()).show();
			}
		});
		registerLayout.addMember(register);

		registerSection.addItem(registerLayout);

		SectionStackSection socialSection = new SectionStackSection(
				"Social Login");
		socialSection.setExpanded(true);
		socialSection.setCanCollapse(false);
		sectionStack.addSection(socialSection);

		HLayout socialLogin1 = new HLayout(0);
		socialLogin1.setWidth(400);
		socialLogin1.setAutoHeight();
		socialLogin1.setMargin(10);
		socialLogin1.setAlign(Alignment.CENTER);
		HLayout socialLogin2 = new HLayout(0);
		socialLogin2.setWidth(400);
		socialLogin2.setAutoHeight();
		socialLogin2.setMargin(10);
		socialLogin2.setAlign(Alignment.CENTER);

		Label facebookOAuth2 = new Label(
				"<a href=\""
						+ GWT.getHostPageBaseURL()
						+ "login/oauth?signInService=FACEBOOK\" target=\"_top\" class=\"zocial facebook\">Sign in with Facebook</a>");
		facebookOAuth2.setWidth(200);
		facebookOAuth2.setAutoHeight();

		Label twitterOAuth2 = new Label(
				"<a href=\""
						+ GWT.getHostPageBaseURL()
						+ "login/oauth?signInService=TWITTER\" target=\"_top\" class=\"zocial twitter\">Sign in with Twitter</a>");
		twitterOAuth2.setWidth(200);
		twitterOAuth2.setAutoHeight();
		// <img src=\"/images/signin/twitter.png\" alt=\"Sign in with Twitter\"/
		// style=\"width: 200px\">

		Label googleOAuth2 = new Label(
				"<a href=\""
						+ GWT.getHostPageBaseURL()
						+ "login/oauth?signInService=GOOGLE\" target=\"_top\" class=\"zocial googleplus\">Sign in with Google+</a>");
		googleOAuth2.setWidth(200);
		googleOAuth2.setAutoHeight();

		Label loginAnchor = new Label(
				"<a href=\""
						+ loginURL
						+ "\" target=\"_top\" class=\"zocial google\">Sign in with Google</a>");
		loginAnchor.setWidth(200);
		loginAnchor.setAutoHeight();

		socialLogin1.addMember(facebookOAuth2);
		socialLogin1.addMember(twitterOAuth2);
		socialLogin2.addMember(googleOAuth2);
		socialLogin2.addMember(loginAnchor);

		socialSection.addItem(socialLogin1);
		socialSection.addItem(socialLogin2);

		SectionStackSection loginFormSection = new SectionStackSection(
				"Login Form");
		loginFormSection.setExpanded(true);
		loginFormSection.setCanCollapse(false);
		sectionStack.addSection(loginFormSection);

		DynamicForm loginForm = new DynamicForm();
		loginForm.setMargin(10);
		HeaderItem header = new HeaderItem();
		header.setDefaultValue("Alternative Login Form");
		TextItem emailItem = new TextItem("email", "Email");
		emailItem.setRequired(true);
		PasswordItem passwordItem = new PasswordItem("password", "Password");
		passwordItem.setRequired(true);
		ButtonItem submitButton = new ButtonItem("loginButton", "Login");
		loginForm.setFields(header, emailItem, passwordItem, submitButton);

		loginFormSection.addItem(loginForm);

		windowLayout.addMember(welcomeLabel);
		windowLayout.addMember(sectionStack);

		addItem(windowLayout);
	}

}
