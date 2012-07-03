/**
 * 
 */
package edu.kit.aifb.mirrormaze.server.servlets;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Image;

import edu.kit.aifb.mirrormaze.client.model.Ami;
import edu.kit.aifb.mirrormaze.server.AmiManager;

/**
 * @author mugglmenzel
 * 
 */
public class AMIDatabaseUpdateServlet extends HttpServlet {

	public enum Repository {
		US_EAST1("ec2.us-east-1.amazonaws.com"), US_WEST_1(
				"ec2.us-west-1.amazonaws.com"), US_WEST_2(
				"ec2.us-west-2.amazonaws.com"), EU_1(
				"ec2.eu-west-1.amazonaws.com"), SOUTH_ASIA_EAST_1(
				"ec2.ap-southeast-1.amazonaws.com"), NORTH_ASIA_EAST_1(
				"ec2.ap-southeast-1.amazonaws.com"), SOUTH_AMERICA_EAST_1(
				"ec2.sa-east-1.amazonaws.com");

		final String name;

		Repository(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static int getPos(String repo) {

			for (int i = 0; i < Repository.values().length; i++)
				if (Repository.values()[i].getName().equals(repo))
					return i;
			return -1;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4757081487515699446L;

	private Logger log = Logger.getLogger(AMIDatabaseUpdateServlet.class
			.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html");

		SortedSet<String> availableImages = new TreeSet<String>();

		for (Ami ami : AmiManager.getAmis().getList()) {
			availableImages.add(ami.getRepository() + "" + ami.getImageId());
		}

		AWSCredentials credentials = new PropertiesCredentials(this.getClass()
				.getResourceAsStream("AwsCredentials.properties"));

		AmazonEC2Client ec2 = new AmazonEC2Client(credentials);
		for (Repository repo : Repository.values()) {
			try {
				ec2.setEndpoint(repo.getName());
				DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
				DescribeImagesResult result = ec2
						.describeImages(describeImagesRequest);
				for (Image img : result.getImages()) {
					if (!availableImages.contains(repo.getName() + ""
							+ img.getImageId())) {
						AmiManager.saveAmi(repo.getName(), img.getImageId(),
								img.getImageLocation(),
								img.getImageOwnerAlias(), img.getOwnerId(),
								img.getName(), img.getDescription(),
								img.getArchitecture(), img.getPlatform(),
								img.getImageType());
						log.fine("added " + img.getImageId() + " to database.");
						resp.getWriter().println(
								"added " + img.getImageId() + " to database.");
					}
				}
			} catch (Exception e) {
				log.log(Level.WARNING, e.getLocalizedMessage(), e.getCause());
			}
		}
	}

	public static void main(String[] arg) {
		try {
			new AMIDatabaseUpdateServlet().doGet(null, null);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
