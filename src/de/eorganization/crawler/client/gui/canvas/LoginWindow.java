/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

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
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		VLayout windowLayout = new VLayout();
		windowLayout.setMargin(10);
		windowLayout.setMembersMargin(10);

		DynamicForm loginForm = new DynamicForm();
		TextItem emailItem = new TextItem("email", "Email");
		emailItem.setRequired(true);
		PasswordItem passwordItem = new PasswordItem("password", "Password");
		passwordItem.setRequired(true);
		ButtonItem submitButton = new ButtonItem("loginButton", "Login");
		loginForm.setFields(emailItem, passwordItem, submitButton);

		Label welcomeLabel = new Label(
				"<span style=\"font-size: 20px\">Please Login to access the Crawler.</span>");
		HTMLFlow facebookOAuth2 = new HTMLFlow(
				"<a href=\""
						+ GWT.getModuleBaseURL()
						+ "login/oauth2?signInService=FACEBOOK\" target=\"_top\" class=\"zocial facebook\">Sign in with Facebook</a>");
		facebookOAuth2.setWidth(200);

		HTMLFlow twitterOAuth2 = new HTMLFlow(
				"<a href=\""
						+ GWT.getModuleBaseURL()
						+ "login/oauth2?signInService=TWITTER\" target=\"_top\"><img src=\"/images/signin/twitter.png\" alt=\"Sign in with Twitter\"/ style=\"width: 200px\"></a>");
		twitterOAuth2.setWidth(200);
		twitterOAuth2.setHeight(32);

		HTMLFlow googleOAuth2 = new HTMLFlow(
				"<a href=\""
						+ GWT.getModuleBaseURL()
						+ "login/oauth2?signInService=GOOGLE\" target=\"_top\" class=\"zocial googleplus\">Sign in with Google+</a>");
		googleOAuth2.setWidth(200);

		HTMLFlow loginAnchor = new HTMLFlow(
				"<a href=\""
						+ loginURL
						+ "\" target=\"_top\" class=\"zocial google\">Sign in with Google</a>");
		loginAnchor.setWidth(200);

		windowLayout.addMember(welcomeLabel);
		windowLayout.addMember(loginForm);
		windowLayout.addMember(new Label("<hr/>"));
		windowLayout.addMember(facebookOAuth2);
		windowLayout.addMember(twitterOAuth2);
		windowLayout.addMember(googleOAuth2);
		windowLayout.addMember(loginAnchor);

		addItem(windowLayout);

	}

}
