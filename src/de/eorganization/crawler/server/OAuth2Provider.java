/**
 * 
 */
package de.eorganization.crawler.server;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi20;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthConstants;
import org.scribe.oauth.OAuthService;

/**
 * @author mugglmenzel
 * 
 */
public enum OAuth2Provider {

	GOOGLE("Google", "468842765661.apps.googleusercontent.com",
			"3E6clkyZOMil4w4WkOqPe4A8",
			"https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/plus.me",
			"http://crawlerride.appspot.com/login/oauthcallback",
			new GoogleApi20()), TWITTER("Twitter", "Kbp5FxjWtTBzsAxcBLRdyg",
			"1TBvJJuaFzOodHalzk9JQ6Y5mwcc2ybvNCALWQJKMww", null,
			"http://crawlerride.appspot.com/login/oauthcallback",
			new TwitterApi()), FACEBOOK("Facebook", "245033985617800",
			"3c682b41d1d072386d056eda7890e2e8", "email",
			"http://crawlerride.appspot.com/login/oauthcallback",
			new FacebookApi());

	private String name;

	private String key;

	private String secret;

	private String scope;

	private String callback;

	private Api api;

	/**
	 * @param name
	 * @param key
	 * @param secret
	 * @param scope
	 */
	private OAuth2Provider(String name, String key, String secret,
			String scope, String callback, Api api) {
		this.name = name;
		this.key = key;
		this.secret = secret;
		this.scope = scope;
		this.callback = callback;
		this.api = api;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @return the callback
	 */
	public String getCallback() {
		return callback;
	}

	/**
	 * @return the api
	 */
	public Api getApi() {
		return api;
	}

	public OAuthService getOAuthService() {
		ServiceBuilder sb = new ServiceBuilder().provider(getApi())
				.apiKey(getKey()).apiSecret(getSecret())
				.callback(getCallback());
		if (getScope() != null)
			sb.scope(getScope());
		if (getApi() instanceof DefaultApi20)
			sb.grantType(OAuthConstants.AUTHORIZATION_CODE);

		return sb.build();
	}

}
