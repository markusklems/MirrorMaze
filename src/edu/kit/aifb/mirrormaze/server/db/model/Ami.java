package edu.kit.aifb.mirrormaze.server.db.model;

import javax.persistence.Id;

public class Ami {

	@Id
	private String id;
	
	private String repository;

	private String imageId;

	private String imageLocation;

	private String imageOwnerAlias;

	private String ownerId;

	private String name;

	private String description;

	private String architecture;

	private String platform;

	private String imageType;
	
	
	/**
	 * @param id
	 */
	public Ami(String id) {
		super();
		this.id = id;
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
	public Ami(String id, String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		super();
		this.id = id;
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
