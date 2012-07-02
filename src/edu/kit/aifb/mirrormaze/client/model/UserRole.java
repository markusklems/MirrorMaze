/**
 * 
 */
package edu.kit.aifb.mirrormaze.client.model;

/**
 * @author mugglmenzel
 *
 */
public enum UserRole {

	USER("user", 0L), ADMIN("admin", -1L);
	
	private String roleName;
	
	private Long defaultUserId;

	/**
	 * @param roleName
	 */
	private UserRole(String roleName, Long defaultUserId) {
		this.setRoleName(roleName);
		this.setDefaultUserId(defaultUserId);
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
	public Long getDefaultUserId() {
		return defaultUserId;
	}

	/**
	 * @param defaultUserId the defaultUserId to set
	 */
	public void setDefaultUserId(Long defaultUserId) {
		this.defaultUserId = defaultUserId;
	}
	
	
}
