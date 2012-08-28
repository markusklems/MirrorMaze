package de.eorganization.crawler.client.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;

@Entity
@Cached
@Indexed
public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String email;

	private String socialId;

	private String profilePic;

	private String nickname;

	private UserRole role = UserRole.USER;

	private String firstname;

	private String lastname;

	private String AWSSecretKey;

	private String AWSAccessKey;
	
	private boolean showWelcomeInfo = true;

	/**
	 * 
	 */
	public Member() {
		super();
	}

	/**
	 * 
	 */
	public Member(String email, UserRole role) {
		super();
		this.email = email;
		this.role = role;
	}

	/**
	 * @param email
	 * @param nickname
	 * @param role
	 */
	public Member(String email, String nickname, UserRole role) {
		super();
		this.email = email;
		this.nickname = nickname;
		this.role = role;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the role
	 */
	public UserRole getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(UserRole role) {
		this.role = role;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @param lastname
	 *            the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the aWSSecretKey
	 */
	public String getAWSSecretKey() {
		return AWSSecretKey;
	}

	/**
	 * @return the aWSAccessKey
	 */
	public String getAWSAccessKey() {
		return AWSAccessKey;
	}

	/**
	 * @param aWSSecretKey
	 *            the aWSSecretKey to set
	 */
	public void setAWSSecretKey(String aWSSecretKey) {
		AWSSecretKey = aWSSecretKey;
	}

	/**
	 * @param aWSAccessKey
	 *            the aWSAccessKey to set
	 */
	public void setAWSAccessKey(String aWSAccessKey) {
		AWSAccessKey = aWSAccessKey;
	}

	/**
	 * @return the socialId
	 */
	public String getSocialId() {
		return socialId;
	}

	/**
	 * @param socialId
	 *            the socialId to set
	 */
	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}

	/**
	 * @return the profilePic
	 */
	public String getProfilePic() {
		return profilePic;
	}

	/**
	 * @param profilePic
	 *            the profilePic to set
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	/**
	 * @return the showWelcomeInfo
	 */
	public boolean isShowWelcomeInfo() {
		return showWelcomeInfo;
	}

	/**
	 * @param showWelcomeInfo the showWelcomeInfo to set
	 */
	public void setShowWelcomeInfo(boolean showWelcomeInfo) {
		this.showWelcomeInfo = showWelcomeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Member ["
				+ (email != null ? "email=" + email + ", " : "")
				+ (socialId != null ? "socialId=" + socialId + ", " : "")
				+ (profilePic != null ? "profilePic=" + profilePic + ", " : "")
				+ (nickname != null ? "nickname=" + nickname + ", " : "")
				+ (role != null ? "role=" + role + ", " : "")
				+ (firstname != null ? "firstname=" + firstname + ", " : "")
				+ (lastname != null ? "lastname=" + lastname + ", " : "")
				+ (AWSSecretKey != null ? "AWSSecretKey=" + AWSSecretKey + ", "
						: "")
				+ (AWSAccessKey != null ? "AWSAccessKey=" + AWSAccessKey : "")
				+ "]";
	}

}
