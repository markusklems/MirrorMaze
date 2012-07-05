package edu.kit.aifb.mirrormaze.server.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;

import com.google.appengine.api.search.AddResponse;
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

		int amiCount = AmiManager.getNumberAmis(
				UserRole.ADMIN.getDefaultUserId(), null);

		for (int i = 0; i < amiCount - 1; i++) {
			List<Ami> amis = AmiManager.getAmis(
					UserRole.ADMIN.getDefaultUserId(), null, i, i + 1)
					.getList();

			for (Ami ami : amis) {
				AddResponse adr = idx.add(Document
						.newBuilder()
						.setId(ami.getRepository() + "-" + ami.getImageId())
						.addField(
								Field.newBuilder().setName("repository")
										.setText(ami.getRepository()))
						.addField(
								Field.newBuilder().setName("content")
										.setHTML(ami.getDescription()))
						.addField(
								Field.newBuilder().setName("name")
										.setText(ami.getName())).build());
				Log.info("added documents to index: " + ami.getRepository()
						+ "-" + ami.getImageId());
				resp.getWriter().println(
						adr + " (id: " + ami.getRepository() + "-"
								+ ami.getImageId() + ")");

			}
		}
	}

}
