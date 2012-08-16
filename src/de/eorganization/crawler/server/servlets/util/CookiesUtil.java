/**
 * 
 */
package de.eorganization.crawler.server.servlets.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

/**
 * @author mugglmenzel
 * 
 */
public class CookiesUtil {

	public static Map<String, String> getCookiesStringMap(Cookie[] cookies) {
		Map<String, String> result = new HashMap<String, String>();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				result.put(cookie.getName(), cookie.getValue());
			}
		}
		return result;
	}

	public static Map<String, Cookie> getCookiesMap(Cookie[] cookies) {
		Map<String, Cookie> result = new HashMap<String, Cookie>();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				result.put(cookie.getName(), cookie);
			}
		}
		return result;
	}
}
