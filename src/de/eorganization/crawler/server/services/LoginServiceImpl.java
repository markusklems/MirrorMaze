package de.eorganization.crawler.server.services;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;

import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.eorganization.crawler.client.OutOfQuotaException;
import de.eorganization.crawler.client.model.LoginInfo;
import de.eorganization.crawler.client.model.Member;
import de.eorganization.crawler.client.services.LoginService;
import de.eorganization.crawler.server.AmiManager;
import de.eorganization.crawler.server.OAuth2Provider;
import de.eorganization.crawler.server.servlets.util.CookiesUtil;

/**
 * 
 * @author mugglmenzel
 * 
 *         Author: Michael Menzel (mugglmenzel)
 * 
 *         Last Change:
 * 
 *         By Author: $Author: mugglmenzel $
 * 
 *         Revision: $Revision: 170 $
 * 
 *         Date: $Date: 2011-08-05 16:48:05 +0200 (Fr, 05 Aug 2011) $
 * 
 *         License:
 * 
 *         Copyright 2011 Forschungszentrum Informatik FZI / Karlsruhe Institute
 *         of Technology
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 * 
 *         SVN URL: $HeadURL:
 *         https://aotearoadecisions.googlecode.com/svn/trunk/
 *         src/main/java/de/fzi/aotearoa/server/services/LoginServiceImpl.java $
 * 
 */

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1447714256662875710L;

	private Logger log = Logger.getLogger(LoginServiceImpl.class.getName());

	private UserService userService = UserServiceFactory.getUserService();

	@Override
	public LoginInfo login(String requestUri) throws Exception {
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setLoggedIn(false);
		loginInfo.setLoginUrl(userService.createLoginURL(requestUri));

		Map<String, String> cookies = CookiesUtil
				.getCookiesStringMap(getThreadLocalRequest().getCookies());
		log.info("Got cookies " + cookies);
		String oauthService = cookies.get("oauth.service");

		log.info("Logging in with OAuth service " + oauthService);

		if (oauthService != null) {
			try {
				String accessTokenString = cookies.get("oauth.accessToken");
				String accessSecret = cookies.get("oauth.secret");
				if (accessTokenString == null)
					return loginInfo;

				log.info("Retrieved access token " + accessTokenString);
				Token accessToken = new Token(accessTokenString, accessSecret);
				log.info("Token object " + accessToken.getToken() + ", "
						+ accessToken.getSecret());

				OAuth2Provider provider = OAuth2Provider.valueOf(oauthService);
				OAuthService service = provider.getOAuthService();

				Cookie serviceTokenCookie = new Cookie("oauth.service",
						provider.toString());
				serviceTokenCookie.setMaxAge(14 * 24 * 60 * 60);
				serviceTokenCookie.setPath("/");
				getThreadLocalResponse().addCookie(serviceTokenCookie);
				Cookie accessTokenCookie = new Cookie("oauth.accessToken",
						accessTokenString);
				accessTokenCookie.setMaxAge(14 * 24 * 60 * 60);
				accessTokenCookie.setPath("/");
				getThreadLocalResponse().addCookie(accessTokenCookie);
				Cookie accessSecretCookie = new Cookie("oauth.secret",
						accessSecret);
				accessSecretCookie.setMaxAge(14 * 24 * 60 * 60);
				accessSecretCookie.setPath("/");
				getThreadLocalResponse().addCookie(accessSecretCookie);

				if (OAuth2Provider.GOOGLE.equals(provider)) {
					OAuthRequest req = new OAuthRequest(Verb.GET,
							"https://www.googleapis.com/oauth2/v1/userinfo");
					service.signRequest(accessToken, req);
					Response response = req.send();
					log.info("Requested user info from google: "
							+ response.getBody());

					JSONObject googleUserInfo = new JSONObject(
							response.getBody());
					log.info("got user info: "
							+ googleUserInfo.getString("given_name") + ", "
							+ googleUserInfo.getString("family_name"));

					Member tempMember = AmiManager
							.findMemberBySocialId(googleUserInfo
									.getString("id"));

					if (tempMember == null) {
						tempMember = new Member();

						tempMember.setSocialId(googleUserInfo.getString("id"));
						tempMember.setFirstname(googleUserInfo
								.getString("given_name"));
						tempMember.setLastname(googleUserInfo
								.getString("family_name"));
						tempMember
								.setNickname(googleUserInfo.getString("name"));
						tempMember.setProfilePic(googleUserInfo
								.getString("picture"));

						req = new OAuthRequest(Verb.GET,
								"https://www.googleapis.com/plus/v1/people/me");
						service.signRequest(accessToken, req);
						response = req.send();
						log.info("Requested more user info from google: "
								+ response.getBody());

						JSONObject googleUserInfo2 = new JSONObject(
								response.getBody());
						log.info("got user info: "
								+ googleUserInfo2.getString("nickname") + ", "
								+ googleUserInfo2.getString("displayName"));
						if (googleUserInfo2 != null
								&& googleUserInfo2.getJSONArray("emails") != null)
							for (int i = 0; i < googleUserInfo2.getJSONArray(
									"emails").length(); i++) {
								JSONObject emailInfo = googleUserInfo2
										.getJSONArray("emails")
										.optJSONObject(i);
								if (emailInfo != null
										&& emailInfo.getBoolean("primary")) {
									tempMember.setEmail(emailInfo
											.getString("value"));
									tempMember = AmiManager
											.registerMember(tempMember);
									loginInfo.setLoggedIn(true);
									break;
								}
							}
					} else
						loginInfo.setLoggedIn(true);

					loginInfo.setMember(tempMember);

				} else if (OAuth2Provider.TWITTER.equals(provider)) {
					OAuthRequest req = new OAuthRequest(Verb.GET,
							"https://api.twitter.com/1/account/verify_credentials.json");
					service.signRequest(accessToken, req);
					log.info("Requesting from twitter " + req.getCompleteUrl());
					Response response = req.send();
					log.info("Requested user info from twitter: "
							+ response.getBody());
					JSONObject twitterUserInfo = new JSONObject(
							response.getBody());
					log.info("got user info: "
							+ twitterUserInfo.getString("name") + ", "
							+ twitterUserInfo.getString("screen_name"));

					Member tempMember = AmiManager
							.findMemberBySocialId(new Integer(twitterUserInfo
									.getInt("id")).toString());
					if (tempMember == null) {
						tempMember = new Member();
						tempMember.setSocialId(new Integer(twitterUserInfo
								.getInt("id")).toString());
						tempMember.setFirstname(twitterUserInfo.getString(
								"name").split(" ")[0]);
						tempMember.setLastname(twitterUserInfo
								.getString("name").split(" ", 2)[1]);
						tempMember.setNickname(twitterUserInfo
								.getString("screen_name"));
						tempMember.setProfilePic(twitterUserInfo
								.getString("profile_image_url"));
					} else
						loginInfo.setLoggedIn(true);
					loginInfo.setMember(tempMember);

				} else if (OAuth2Provider.FACEBOOK.equals(provider)) {
					OAuthRequest req = new OAuthRequest(Verb.GET,
							"https://graph.facebook.com/me");
					service.signRequest(accessToken, req);
					log.info("Requesting from facebook " + req.getCompleteUrl());
					Response response = req.send();
					log.info("Requested user info from facebook: "
							+ response.getBody());
					JSONObject facebookUserInfo = new JSONObject(
							response.getBody());
					log.info("got user info: "
							+ facebookUserInfo.getString("name") + ", "
							+ facebookUserInfo.getString("username"));

					Member tempMember = AmiManager
							.findMemberBySocialId(facebookUserInfo
									.getString("id"));
					if (tempMember == null) {
						tempMember = new Member();
						tempMember.setSocialId(new Integer(facebookUserInfo
								.getString("id")).toString());
						tempMember.setFirstname(facebookUserInfo
								.getString("first_name"));
						tempMember.setLastname(facebookUserInfo
								.getString("last_name"));
						tempMember.setNickname(facebookUserInfo
								.getString("username"));
						tempMember.setProfilePic("https://graph.facebook.com/"
								+ facebookUserInfo.getString("id")
								+ "/picture?type=large");
						tempMember
								.setEmail(facebookUserInfo.getString("email"));
						tempMember = AmiManager.registerMember(tempMember);
					}

					loginInfo.setLoggedIn(true);
					loginInfo.setMember(tempMember);
				}
				loginInfo.setLogoutUrl("/logout/oauth");
				log.info("Set loginInfo to " + loginInfo);
				return loginInfo;
			} catch (OverQuotaException oqe) {
				log.log(Level.WARNING, oqe.getLocalizedMessage(), oqe);
				throw new OutOfQuotaException("Out of Quota!", oqe);
			} catch (Exception e) {
				log.log(Level.WARNING, e.getLocalizedMessage(), e);
			}
		} else {

			User user = userService.getCurrentUser();

			if (userService.isUserLoggedIn() && user != null) {
				loginInfo.setLoggedIn(true);
				loginInfo.setMember(AmiManager.saveOrGetMember(user));
				loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			}
			log.info("Logged in with google services " + loginInfo);
		}

		return loginInfo;

	}
}
