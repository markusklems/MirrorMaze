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
import com.google.appengine.api.search.Consistency;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

public class AMICreateSearchIndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2148876175516251077L;

	private Logger log = Logger.getLogger(AMICreateSearchIndexServlet.class
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

		IndexSpec indexSpec = IndexSpec.newBuilder().setName("amiIndex")
				.setConsistency(Consistency.PER_DOCUMENT).build();
		Index idx = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		/*
		 * Iterator<Document> docs = idx.listDocuments(
		 * ListRequest.newBuilder().setReturningIdsOnly(true).build())
		 * .iterator(); while (docs.hasNext())
		 * idx.removeAsync(docs.next().getId()); log.info("Cleared index " +
		 * idx.getNamespace() + "/" + idx.getName());
		 */

		log.info("Adding documents to index " + idx.getNamespace() + "/"
				+ idx.getName());

		resp.setContentType("text/html");

		AWSCredentials credentials = new PropertiesCredentials(this.getClass()
				.getResourceAsStream("../AwsCredentials.properties"));

		AmazonEC2Client ec2 = new AmazonEC2Client(credentials);
		int addedDocs = 0;
		for (Region repo : ec2.describeRegions().getRegions()) {
			try {
				ec2.setEndpoint(repo.getEndpoint());
				log.info("crawling repo " + repo.getRegionName() + " at "
						+ repo.getEndpoint());
				resp.getWriter().println(
						"crawling repo " + repo.getRegionName() + " at "
								+ repo.getEndpoint());
				DescribeImagesResult result = ec2
						.describeImages(new DescribeImagesRequest());
				for (Image img : result.getImages()) {
					if (repo.getEndpoint() != null && img.getImageId() != null)
						idx.addAsync(Document
								.newBuilder()
								.setId(repo.getEndpoint() + "+"
										+ img.getImageId())
								.addField(
										Field.newBuilder()
												.setName("repository")
												.setText(repo.getEndpoint()))
								.addField(
										Field.newBuilder().setName("imageId")
												.setText(img.getImageId()))
								.addField(
										Field.newBuilder()
												.setName("content")
												.setHTML(
														img.getDescription() != null ? img
																.getDescription()
																: ""))
								.addField(
										Field.newBuilder()
												.setName("architecture")
												.setText(
														img.getArchitecture() != null ? img
																.getArchitecture()
																: ""))
								.addField(
										Field.newBuilder()
												.setName("owner")
												.setText(
														img.getImageOwnerAlias() != null ? img
																.getImageOwnerAlias()
																: ""))
								.addField(
										Field.newBuilder()
												.setName("name")
												.setText(
														img.getName() != null ? img
																.getName() : ""))
								.build());
					addedDocs++;
					System.gc();
				}
			} catch (Exception e) {
				log.log(Level.WARNING, e.getLocalizedMessage(), e);
			}

		}

		log.info("Added " + addedDocs + " documents to index " + idx.getName());
	}
}
