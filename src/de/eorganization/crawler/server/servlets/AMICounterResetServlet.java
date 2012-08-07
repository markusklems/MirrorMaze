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
import com.amazonaws.services.ec2.model.Region;

import de.eorganization.crawler.server.AmiManager;

/**
 * @author mugglmenzel
 * 
 */
public class AMICounterResetServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4757081487515699446L;

	private Logger log = Logger.getLogger(AMICounterResetServlet.class
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

		AmiManager.getAmiCounter(null).reset(AmiManager.getAmiCount(null));
		log.info("reset AMI counter for all regions.");

		try {

			AWSCredentials credentials = new PropertiesCredentials(this
					.getClass().getResourceAsStream(
							"../AwsCredentials.properties"));
			AmazonEC2Client ec2 = new AmazonEC2Client(credentials);

			for (Region repo : ec2.describeRegions().getRegions()) {
				AmiManager.getAmiCounter(repo.getEndpoint()).reset(
						AmiManager.getAmiCount(repo.getEndpoint()));
				log.info("reset AMI counter for region " + repo.getEndpoint() + ".");
			}
		} catch (IOException e) {
			log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		log.info("reset AMI counters.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
