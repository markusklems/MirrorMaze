package edu.kit.aifb.mirrormaze.server.servlets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.search.Consistency;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

import edu.kit.aifb.mirrormaze.client.model.Ami;
import edu.kit.aifb.mirrormaze.client.model.UserRole;
import edu.kit.aifb.mirrormaze.server.AmiManager;

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

		log.info("Adding documents to index " + idx.getNamespace() + "/"
				+ idx.getName());

		int amiCount = AmiManager.getNumberAmis(
				UserRole.ADMIN.getDefaultMemberId(), null, null);

		int addedDocs = 0;

		int start = new Long(Math.round(Math.random() * amiCount)).intValue();
		start = start < amiCount - 100 ? start : amiCount - 100;

		List<Ami> amis = AmiManager.getAmis(
				UserRole.ADMIN.getDefaultMemberId(), null, start, start + 50)
				.getList();
		amis.addAll(AmiManager.getAmis(UserRole.ADMIN.getDefaultMemberId(), null, amiCount - 50, amiCount).getList());

		for (Ami ami : amis) {
			idx.addAsync(Document
					.newBuilder()
					.setId(ami.getId().toString())
					.addField(
							Field.newBuilder().setName("repository")
									.setText(ami.getRepository()))
					.addField(
							Field.newBuilder().setName("imageId")
									.setText(ami.getImageId()))
					.addField(
							Field.newBuilder().setName("content")
									.setHTML(ami.getDescription()))
					.addField(
							Field.newBuilder().setName("owner")
									.setText(ami.getImageOwnerAlias()))
					.addField(
							Field.newBuilder().setName("name")
									.setText(ami.getName())).build());
			addedDocs++;

		}

		log.info("Added " + addedDocs + " documents to index " + idx.getName());
	}
}
