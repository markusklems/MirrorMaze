package de.eorganization.crawler.client.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfNull;

@Entity
@Cached
@Indexed
public class Ami implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2525689513157353594L;

	@Id
	private Long id;

	private String repository;

	private String imageId;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String imageLocation;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String imageOwnerAlias;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String ownerId;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String name;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String description;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String architecture;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String platform;

	@Unindexed({ IfNull.class, IfEmptyString.class })
	private String imageType;

	/**
	 * 
	 */
	public Ami() {
		super();
	}

	/**
	 * @param id
	 */
	public Ami(Long id) {
		super();
		this.setId(id);
	}

	/**
	 * @param id
	 * @param repository
	 * @param imageId
	 * @param imageLocation
	 * @param imageOwnerAlias
	 * @param ownerId
	 * @param name
	 * @param description
	 * @param architecture
	 * @param platform
	 * @param imageType
	 */
	public Ami(Long id, String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		super();
		this.setId(id);
		this.repository = repository;
		this.imageId = imageId;
		this.imageLocation = imageLocation;
		this.imageOwnerAlias = imageOwnerAlias;
		this.ownerId = ownerId;
		this.name = name;
		this.description = description;
		this.architecture = architecture;
		this.platform = platform;
		this.imageType = imageType;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}

	public String getImageOwnerAlias() {
		return imageOwnerAlias;
	}

	public void setImageOwnerAlias(String imageOwnerAlias) {
		this.imageOwnerAlias = imageOwnerAlias;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

}
