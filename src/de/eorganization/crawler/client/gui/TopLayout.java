/**
 * 
 */
package de.eorganization.crawler.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;

import de.eorganization.crawler.client.gui.canvas.LoginWindow;
import de.eorganization.crawler.client.gui.canvas.ProfileWindow;
import de.eorganization.crawler.client.gui.handler.MemberUpdatedHandler;
import de.eorganization.crawler.client.model.LoginInfo;
import de.eorganization.crawler.client.model.Member;

/**
 * @author mugglmenzel
 * 
 */
public class TopLayout extends HLayout {

	/**
	 * 
	 */
	public TopLayout(final LoginInfo loginInfo) {

		if (loginInfo == null)
			return;

		setWidth100();
		setBackgroundImage("/images/clouds.png");
		setBackgroundPosition("bottom");
		setBackgroundRepeat(BkgndRepeat.REPEAT_X);
		setAlign(Alignment.LEFT);

		Anchor crawlerLogo = new Anchor(new SafeHtmlBuilder()
				.appendHtmlConstant(
						Canvas.imgHTML("/images/crawler_logo.png", 397, 150))
				.toSafeHtml(), GWT.getHostPageBaseURL(), "_top");

		HLayout login = new HLayout();
		login.setStyleName("login");
		login.setMembersMargin(15);
		login.setAlign(Alignment.RIGHT);

		Img profileImg = new Img();
		

		Label welcomeLabel = new Label(
				"<span style=\"font-size: 20pt\">Checking status of login...</span>");
		welcomeLabel.setAutoWidth();
		welcomeLabel.setWrap(false);
		welcomeLabel.setStyleName("login");

		Anchor profileAnchor = new Anchor(
				"<span style=\"font-size: 20pt\"> </span>", true);
		profileAnchor.setWordWrap(false);
		profileAnchor.setStyleName("login");

		Label loginDivider = new Label(
				"<span style=\"font-size: 25px\">|</span>");
		loginDivider.setAutoWidth();
		loginDivider.setStyleName("login");

		Anchor loginAnchor = new Anchor(
				"<span style=\"font-size: 20pt\">Login</span>", true);
		loginAnchor.setWordWrap(false);
		loginAnchor.setStyleName("login");

		if (loginInfo.isLoggedIn() && loginInfo.getMember() != null) {
			if (loginInfo.getMember().getProfilePic() != null) {
				profileImg.setSrc(loginInfo.getMember().getProfilePic());
				profileImg.setHeight(30);
				profileImg.setImageHeight(30);
				profileImg.setImageType(ImageStyle.STRETCH);
				profileImg.setWidth(ImageUtil.getScaledImageWidth(loginInfo
						.getMember().getProfilePic(), 30));
			}

			welcomeLabel.setContents("");
			welcomeLabel.setWidth(0);
			welcomeLabel.setVisible(false);

			profileAnchor.setHTML("<span style=\"font-size: 20pt\">"
					+ loginInfo.getMember().getNickname() + "</span>");
			profileAnchor
					.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

						@Override
						public void onClick(
								com.google.gwt.event.dom.client.ClickEvent event) {
							new ProfileWindow(loginInfo.getMember(),
									new MemberUpdatedHandler() {

										@Override
										public void updated(Member member) {
											loginInfo.setMember(member);
										}
									}).show();
						}
					});

			loginAnchor.setHref(loginInfo.getLogoutUrl());
			loginAnchor
					.setHTML("<span style=\"font-size: 20pt\">Logout</span>");
		} else {
			profileImg.setVisible(false);
			welcomeLabel
					.setContents("<span style=\"font-size: 20pt\">Not logged in</span>");
			profileAnchor.setEnabled(false);
			profileAnchor.setVisible(false);
			profileAnchor.setWidth("0px");

			loginAnchor
					.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

						@Override
						public void onClick(
								com.google.gwt.event.dom.client.ClickEvent event) {
							new LoginWindow(loginInfo.getLoginUrl())
									.show();
						}
					});
			loginAnchor.setHTML("<span style=\"font-size: 20pt\">Login</span>");

		}
		login.addMember(profileImg);
		login.addMember(welcomeLabel);
		login.addMember(profileAnchor);
		login.addMember(loginDivider);
		login.addMember(loginAnchor);

		addMember(crawlerLogo);
		addMember(login);

	}

}
