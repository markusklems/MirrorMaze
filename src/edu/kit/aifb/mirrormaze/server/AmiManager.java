package edu.kit.aifb.mirrormaze.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;

import edu.kit.aifb.mirrormaze.server.db.dao.DaoFactory;
import edu.kit.aifb.mirrormaze.server.db.model.AmiModel;

public class AmiManager {

	private static Logger log = Logger.getLogger(AmiManager.class.getName());

	public static boolean saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) {
		if (repository != null && imageId != null) {
			// Check for duplicate AMIs? TODO

			if (DaoFactory.getAmiDao().create(repository, imageId,
					imageLocation, imageOwnerAlias, ownerId, name, description,
					architecture, platform, imageType) != null) {
				return true;
			}
		} else {
			return false;
		}
		return false;

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
						AmiModel test = gson.fromJson(new InputStreamReader(
								jsonFile.getObjectContent()), AmiModel.class);
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
