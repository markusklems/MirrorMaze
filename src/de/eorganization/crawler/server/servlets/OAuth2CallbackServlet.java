/**
 * 
 */
package de.eorganization.crawler.server.servlets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import de.eorganization.crawler.server.OAuth2Provider;

/**
 * @author mugglmenzel
 * 
 */
public class OAuth2CallbackServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5067517688143381518L;

	private Logger log = Logger
			.getLogger(OAuth2CallbackServlet.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		log.info("GET got parameters: " + req.getParameterMap());
		log.info("HTTP Session: " + req.getSession().getAttributeNames());

		HttpSession httpsession = req.getSession();

		try {

			OAuth2Provider provider = OAuth2Provider
					.valueOf((String) httpsession.getAttribute("oauth.service"));
			log.info("Got provider: " + provider);

			String oauthVerifier = "";
			Token requestToken = null;
			Token accessToken = new Token("", provider.getSecret());
			OAuthService service = provider.getOAuthService();

			if (provider.getApi() instanceof DefaultApi20) {
				oauthVerifier = req.getParameter("code");
				log.info("got OAuth 2.0 authorization code: " + oauthVerifier);

			} else if (provider.getApi() instanceof DefaultApi10a) {
				oauthVerifier = req.getParameter("oauth_verifier");
				log.info("got OAuth 1.0a verifier: " + oauthVerifier);
				requestToken = req.getParameter("oauth_token") != null ? new Token(
						(String) req.getParameter("oauth_token"),
						provider.getSecret()) : (Token) httpsession
						.getAttribute("oauth.requestToken");
			}

			Verifier verifier = new Verifier(oauthVerifier);
			accessToken = service.getAccessToken(requestToken, verifier);
			log.info("Got a OAuth access token: " + accessToken.getToken()
					+ ", " + accessToken.getSecret());

			Cookie accessTokenCookie = new Cookie("oauth.accessToken",
					accessToken.getToken());
			accessTokenCookie.setMaxAge(14 * 24 * 60 * 60);
			accessTokenCookie.setPath("/");
			resp.addCookie(accessTokenCookie);
			Cookie serviceCookie = new Cookie("oauth.service",
					provider.toString());
			serviceCookie.setPath("/");
			serviceCookie.setMaxAge(14 * 24 * 60 * 60);
			resp.addCookie(serviceCookie);
			Cookie secretCookie = new Cookie("oauth.secret",
					accessToken.getSecret());
			secretCookie.setPath("/");
			secretCookie.setMaxAge(14 * 24 * 60 * 60);
			resp.addCookie(secretCookie);

			resp.sendRedirect((String) req.getSession().getAttribute(
					"http.referer"));

		} catch (Exception e) {
			log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.info("POST got parameters: " + req.getParameterMap());

	}

}
