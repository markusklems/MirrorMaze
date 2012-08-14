/**
 * 
 */
package de.eorganization.crawler.server.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.eorganization.crawler.server.servlets.util.CookiesUtil;

/**
 * @author mugglmenzel
 * 
 */
public class OAuth2LogoutServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6581829169712523821L;

	private Logger log = Logger.getLogger(OAuth2LogoutServlet.class.getName());
	
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

		Map<String, Cookie> cookiesMap = CookiesUtil.getCookiesMap(req
				.getCookies());
		log.info("Got cookies " + cookiesMap);

		Cookie serviceCookie = cookiesMap.get("oauth.service") != null ? cookiesMap
				.get("oauth.service") : new Cookie("oauth.service", "");
		serviceCookie.setMaxAge(0);
		serviceCookie.setPath("/");
		resp.addCookie(serviceCookie);
		
		Cookie secretCookie = cookiesMap.get("oauth.secret") != null ? cookiesMap
				.get("oauth.secret") : new Cookie("oauth.secret", "");
		secretCookie.setMaxAge(0);
		secretCookie.setPath("/");
		resp.addCookie(secretCookie);

		Cookie accessTokenCookie = cookiesMap.get("oauth.accessToken") != null ? cookiesMap
				.get("oauth.accessToken") : new Cookie("oauth.accessToken", "");
		accessTokenCookie.setMaxAge(0);
		accessTokenCookie.setPath("/");
		resp.addCookie(accessTokenCookie);

		resp.sendRedirect(req.getHeader("referer"));
	}

}
