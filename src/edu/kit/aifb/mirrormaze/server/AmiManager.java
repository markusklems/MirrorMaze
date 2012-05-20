package edu.kit.aifb.mirrormaze.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;

import edu.kit.aifb.mirrormaze.client.model.Ami;
import edu.kit.aifb.mirrormaze.client.model.Language;
import edu.kit.aifb.mirrormaze.client.model.Software;
import edu.kit.aifb.mirrormaze.server.db.dao.MazeDAO;

public class AmiManager {

	private static Logger log = Logger.getLogger(AmiManager.class.getName());

	private static MazeDAO dao = new MazeDAO();

	/**
	 * 
	 */
	public AmiManager() {
		super();
	}

	public static boolean saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {

		// Check for duplicate AMIs? TODO
		Ami newAmi = dao.getOrCreateAmi(null, repository, imageId,
				imageLocation, imageOwnerAlias, ownerId, name, description,
				architecture, platform, imageType);
		if (newAmi != null && newAmi.getId() != null)
			return true;
		else
			return false;
	}

	public static Software saveSoftware(String amiId, String name,
			String version) {
		Software newSoftware = null;
		Key<Ami> amiKey = dao.findAmiByImageId(amiId);

		if (amiKey != null)
			newSoftware = dao.getOrCreateSoftware(null, amiKey, name, version);

		return newSoftware;
	}

	public static void updateSoftware(Software software) {
		dao.updateSoftware(software);
	}

	public static Language saveLanguage(String amiId, String name,
			String version) {
		Language newLanguage = null;
		Key<Ami> amiKey = dao.findAmiByImageId(amiId);

		if (amiKey != null)
			newLanguage = dao.getOrCreateLanguage(null, amiKey, name, version);

		return newLanguage;
	}

	public static void updateLanguage(Language language) {
		dao.updateLanguage(language);
	}

	public static List<Ami> getAmis() {
		return new ArrayList<Ami>(dao.ofy()
				.get(dao.ofy().query(Ami.class).chunkSize(10000).fetchKeys())
				.values());
	}

	public static boolean importJSONFromS3(String S3Bucket) {
		Gson gson = new Gson();

		try {
			AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(
					AmiManager.class
							.getResourceAsStream("AwsCredentials.properties")));
			if (!s3.doesBucketExist(S3Bucket)) {
				log.warning("S3 bucket doesn't exist. Creating.");
				s3.createBucket(S3Bucket);
			}

			List<S3ObjectSummary> jsonFiles = s3.listObjects(
					new ListObjectsRequest().withBucketName(S3Bucket)
							.withPrefix("ami-")).getObjectSummaries();
			if (jsonFiles.size() > 0) {
				try {
					for (S3ObjectSummary s3JSON : jsonFiles) {
						S3Object jsonFile = s3.getObject(new GetObjectRequest(
								S3Bucket, s3JSON.getKey()));
						Ami test = gson.fromJson(
								new InputStreamReader(jsonFile
										.getObjectContent()), Ami.class);
						System.out.println(test);
					}

				} catch (Exception e) {
					log.severe("Couldn't read files from s3 bucket " + S3Bucket
							+ "!");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Can't access S3 due to missing credentials file.");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.severe("Accessing S3 failed.");
		}

		Collection<String> jsons = new ArrayList<String>();
		for (String json : jsons) {
			System.out.println(json);
		}

		return true;
	}

}
