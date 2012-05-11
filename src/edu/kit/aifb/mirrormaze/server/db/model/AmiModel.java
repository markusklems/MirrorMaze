package edu.kit.aifb.mirrormaze.server.db.model;

public interface AmiModel {
	
//	public String getKey();
//	public void setKey(String repositoryId, String imageId);
	public String getRepository();
	public void setRepository(String repository);
	public String getImageId();
	public void setImageId(String imageId);
	public String getImageLocation();
	public void setImageLocation(String imageLocation);
	public String getImageOwnerAlias();
	public void setImageOwnerAlias(String imageOwnerAlias);
	public String getOwnerId();
	public void setOwnerId(String ownerId);
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public String getArchitecture();
	public void setArchitecture(String architecture);
	public String getPlatform();
	public void setPlatform(String platform);
	public String getImageType();
	public void setImageType(String imageType);

}
