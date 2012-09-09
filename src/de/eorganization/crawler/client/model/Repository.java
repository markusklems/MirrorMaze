package de.eorganization.crawler.client.model;

public enum Repository {
	ALL("all", "all"), US_EAST1("ec2.us-east-1.amazonaws.com", "us-east-1"), US_WEST_1(
			"ec2.us-west-1.amazonaws.com", "us-west-1"), US_WEST_2(
			"ec2.us-west-2.amazonaws.com", "us-west-2"), EU_1(
			"ec2.eu-west-1.amazonaws.com", "eu-west-1"), SOUTH_ASIA_EAST_1(
			"ec2.ap-southeast-1.amazonaws.com", "ap-southeast-1"), NORTH_ASIA_EAST_1(
			"ec2.ap-northeast-1.amazonaws.com", "ap-northeast-1"), SOUTH_AMERICA_EAST_1(
			"ec2.sa-east-1.amazonaws.com", "sa-east-1");
	final String name;
	final String shortName;

	Repository(final String name, final String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public static Repository findByName(String repository) {
		for (Repository r : values())
			if (r.getName().equals(repository))
				return r;
		return null;
	}

}