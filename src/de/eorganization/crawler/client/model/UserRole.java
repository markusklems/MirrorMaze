/**
 * 
 */
package de.eorganization.crawler.client.model;

/**
 * @author mugglmenzel
 *
 */
public enum UserRole {

	USER("user", "user@myownthemepark.appspot.com"), ADMIN("admin", "admin@myownthemepark.appspot.com");
	
	private String roleName;
	
	private String defaultMemberId;

	/**
	 * @param roleName
	 */
	private UserRole(String roleName, String defaultMemberId) {
		this.setRoleName(roleName);
		this.setDefaultMemberId(defaultMemberId);
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the defaultUserId
	 */
	public String getDefaultMemberId() {
		return defaultMemberId;
	}

	/**
	 * @param defaultUserId the defaultUserId to set
	 */
	public void setDefaultMemberId(String defaultMemberId) {
		this.defaultMemberId = defaultMemberId;
	}
	
	
}
