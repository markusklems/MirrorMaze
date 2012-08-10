/**
 * 
 */
package de.eorganization.crawler.server.servlets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import de.eorganization.crawler.server.OAuth2Provider;

/**
 * @author mugglmenzel
 * 
 */
public class OAuth2LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2204905605308611910L;

	private Logger log = Logger.getLogger(OAuth2LoginServlet.class.getName());

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

		req.getSession().setAttribute("http.referer", req.getHeader("referer"));

		OAuth2Provider provider = OAuth2Provider.GOOGLE;
		try {
			provider = OAuth2Provider
					.valueOf(req.getParameter("signInService"));
			req.getSession().setAttribute("oauth.service",
					req.getParameter("signInService"));
		} catch (Exception e) {
			resp.sendError(401, "SignIn Service not available!");
			return;
		}

		OAuthService service = provider.getOAuthService();

		String confirmUrl = "";
		if (provider.getApi() instanceof DefaultApi20) {
			confirmUrl = service.getAuthorizationUrl(null);
			log.info("OAuth 2.0 redirecting to " + confirmUrl);
		} else if (provider.getApi() instanceof DefaultApi10a) {
			Token requestToken = service.getRequestToken();
			req.getSession().setAttribute("oauth.request_token", requestToken);
			confirmUrl = service.getAuthorizationUrl(requestToken);
			log.info("OAuth 1.0a redirecting to " + confirmUrl);
		}

		try {
			resp.sendRedirect(resp.encodeRedirectURL(confirmUrl));
		} catch (Exception e) {
			log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

}
