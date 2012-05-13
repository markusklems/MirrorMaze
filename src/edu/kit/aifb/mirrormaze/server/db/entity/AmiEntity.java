package edu.kit.aifb.mirrormaze.server.db.entity;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import edu.kit.aifb.mirrormaze.server.db.model.AmiModel;

@PersistenceCapable(table = "AmiEntity")
public class AmiEntity implements AmiModel {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

    @Persistent
    @Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
    private String keyName;

	// attributes
	@Persistent
	private String repository;
	@Persistent
	private String imageId;
	@Persistent
	private String imageLocation;
	@Persistent
	private String imageOwnerAlias;
	@Persistent
	private String ownerId;
	@Persistent
	private String name;
	@Persistent
	private String description;
	@Persistent
	private String architecture;
	@Persistent
	private String platform;
	@Persistent
	private String imageType;

	public AmiEntity(String repository, String imageId, String imageLocation,
			String imageOwnerAlias, String ownerId, String name,
			String description, String architecture, String platform,
			String imageType) {
		// super();
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

	public String getKeyName() {
		return keyName;
	}



	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}



	public String getEncodedKey() {
		return encodedKey;
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
