/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
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
		setHeight("70%");
		setShowMinimizeButton(false);
		setShowCloseButton(false);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		VLayout windowLayout = new VLayout();
		windowLayout.setMargin(10);
		windowLayout.setMembersMargin(15);

		Label welcomeLabel = new Label(
				"<span style=\"font-size: 20px\">Please Login to access the Crawler.</span>");
		// Anchor loginAnchor = new Anchor("Login with Google Account",
		// loginURL, "_top");
		Anchor googleOAuth2 = new Anchor(
				"<img src=\"/images/signin/google.png\" alt=\"sign in with google\"/ style=\"height: 30px\">",
				true, GWT.getModuleBaseURL()
						+ "login/oauth2?signInService=GOOGLE", "_top");

		Anchor twitterOAuth2 = new Anchor(
				"<img src=\"/images/signin/twitter.png\" alt=\"sign in with twitter\"/ style=\"height: 30px\">",
				true, GWT.getModuleBaseURL()
						+ "login/oauth2?signInService=TWITTER", "_top");

		windowLayout.addMember(welcomeLabel);
		// windowLayout.addMember(loginAnchor);
		windowLayout.addMember(googleOAuth2);
		windowLayout.addMember(twitterOAuth2);

		addItem(windowLayout);

	}

}
