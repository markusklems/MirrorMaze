/**
 * 
 */
package de.eorganization.crawler.client.model;

import java.io.Serializable;

/**
 * @author menzel
 * 
 */
public class LoginInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3899611515207036151L;

	private boolean loggedIn = false;

	private String loginUrl;

	private String logoutUrl;

	private Member member;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	/**
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * @param member
	 *            the member to set
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (getMember() != null ? getMember().getEmail() + "/"
				+ getMember().getNickname() + "[" + getMember().getRole() + "]"
				: "Unknown Member")
				+ ", "
				+ (isLoggedIn() ? "logged in, URL: " + getLogoutUrl()
						: "not logged in, URL: " + getLoginUrl());
	}

}
