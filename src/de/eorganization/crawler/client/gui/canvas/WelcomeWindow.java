/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

import de.eorganization.crawler.client.model.LoginInfo;

/**
 * @author mugglmenzel
 * 
 */
public class WelcomeWindow extends Window {

	private LoginInfo loginInfo;

	/**
	 * 
	 */
	public WelcomeWindow(final LoginInfo loginInfo) {
		this.setLoginInfo(loginInfo);

		setTitle("Welcome");
		setIsModal(true);
		setAutoCenter(true);
		setAutoSize(true);
		setDismissOnOutsideClick(true);
		setDismissOnEscape(true);
		setShowMinimizeButton(false);
		addCloseClickHandler(new CloseClickHandler() {

			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});
		setWidth(660);

		VLayout windowLayout = new VLayout();
		windowLayout.setStyleName("welcome");
		windowLayout.setAlign(Alignment.CENTER);
		windowLayout.setWidth100();
		VLayout welcomeInfo = new VLayout(10);
		welcomeInfo.setDefaultLayoutAlign(Alignment.CENTER);
		welcomeInfo.setAlign(VerticalAlignment.TOP);
		welcomeInfo.setHeight100();
		welcomeInfo.setWidth100();
		welcomeInfo.setBackgroundColor("#ffffff");

		Label welcomeLabel = new Label(
				"<h1 style=\"font-size: 40pt\">Welcome!</h1><p style=\"font-size: 20pt\"><span style=\"font-weight: bolder;\">The Crawler</span> <span style=\"font-style: italic; font-weight: bolder;\">n.</span> is an enhanced, continuously growing database of Amazon Machine Images (AMI). By permanently crawling public AMIs from the Amazon EC2 service, the Crawler collects information about the contents of AMIs, i.e., software libraries and versions. The large database supports DevOps, Cloud Developers and Cloud users with insights to compare and understand AMIs better. A search engine allows to find AMIs with a configuration that meets standards and requirements.</p><p style=\"font-size: 20pt\">Feel free to ride the Crawler and <a href=\"http://myownthemepark.com/prices-table/\">contact us for a premium account</a>. Simply sign in with a social user account (Facebook, Twitter, Google+).</p>");
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

		windowLayout.addMember(welcomeInfo);

		addItem(windowLayout);
	}


	/**
	 * @return the loginInfo
	 */
	public LoginInfo getLoginInfo() {
		return loginInfo;
	}


	/**
	 * @param loginInfo the loginInfo to set
	 */
	public void setLoginInfo(LoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

}
