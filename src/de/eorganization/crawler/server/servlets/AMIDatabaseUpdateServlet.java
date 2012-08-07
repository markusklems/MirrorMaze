/**
 * 
 */
package de.eorganization.crawler.server.servlets;

import java.io.IOException;
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
import com.amazonaws.services.ec2.model.Region;

import de.eorganization.crawler.server.AmiManager;


/**
 * @author mugglmenzel
 * 
 */
public class AMIDatabaseUpdateServlet extends HttpServlet {

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

		AWSCredentials credentials = new PropertiesCredentials(this.getClass()
				.getResourceAsStream("../AwsCredentials.properties"));

		AmazonEC2Client ec2 = new AmazonEC2Client(credentials);
		int addedImages = 0;
		for (Region repo : ec2.describeRegions().getRegions()) {
			try {
				ec2.setEndpoint(repo.getEndpoint());
				log.info("crawling repo " + repo.getRegionName() + " at "
						+ repo.getEndpoint());
				resp.getWriter().println(
						"crawling repo " + repo.getRegionName() + " at "
								+ repo.getEndpoint());
				DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
				DescribeImagesResult result = ec2
						.describeImages(describeImagesRequest);
				for (Image img : result.getImages()) {

					boolean added = AmiManager.saveAmi(repo.getEndpoint(),
							img.getImageId(), img.getImageLocation(),
							img.getImageOwnerAlias(), img.getOwnerId(),
							img.getName(), img.getDescription(),
							img.getArchitecture(), img.getPlatform(),
							img.getImageType());

					if (added)
						addedImages++;

				}
				System.gc();
			} catch (Exception e) {
				log.log(Level.WARNING, e.getLocalizedMessage(), e.getCause());
			}
		}
		log.info("added " + addedImages + " images to database.");
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
