package de.eorganization.crawler.server.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.client.ui.FileUpload;

import de.eorganization.crawler.server.AmiManager;


/**
 * @author mugglmenzel
 * @author svens0n
 * 
 *         Author: Michael Menzel (mugglmenzel), Sven Frauen (svens0n)
 * 
 *         Last Change:
 * 
 *         By Author: $Author: mugglmenzel $
 * 
 *         Revision: $Revision: 165 $
 * 
 *         Date: $Date: 2011-08-05 15:45:22 +0200 (Fri, 05 Aug 2011) $
 * 
 *         License:
 * 
 *         Copyright 2011 Forschungszentrum Informatik FZI / Karlsruhe Institute
 *         of Technology
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 * 
 *         SVN URL: $HeadURL:
 *         https://aotearoadecisions.googlecode.com/svn/trunk/
 *         src/main/java/de/fzi
 *         /aotearoa/shared/model/ahp/configuration/Alternative.java $
 * 
 */
public class CSVImportServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(FileUpload.class
			.getName());

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			resp.setContentType("text/plain");

			InputStream stream = req.getInputStream();

			log.info("Got Upload " + req.getContentType() + " ("
					+ req.getContentLength() + " bytes) from "
					+ req.getRemoteAddr());

			String csvString = "";
			String line = "";
			String[] header = new String[] {};
			int i = 0;
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				csvString.concat(line);
				String[] items = line.split(",");
				if (i == 0)
					header = items;
				else {
					Map<String, String> itemsMap = new HashMap<String, String>();
					for (int j = 0; j < header.length; j++)
						itemsMap.put(header[j], items[j]);
					boolean result = AmiManager
							.saveAmi(itemsMap.get("repository"),
									itemsMap.get("imageId"),
									itemsMap.get("imageLocation"),
									itemsMap.get("imageOwnerAlias"),
									itemsMap.get("ownerId"),
									itemsMap.get("name"),
									itemsMap.get("description"),
									itemsMap.get("architecture"),
									itemsMap.get("platform"),
									itemsMap.get("imageType"));
					log.info("save Ami" + itemsMap + " was successful: "
							+ result);
				}

				i++;
			}

			log.info("Saving " + i + " Amis.");
			resp.getWriter().println("Saving " + i + " Amis.");

		} catch (Exception ex) {
			log.severe("Unexpected error" + ex.getMessage() + ", trace: "
					+ ex.getStackTrace());
			log.throwing(CSVImportServlet.class.getName(), "doPost", ex);
			ex.printStackTrace();
		}

		// log.info("returning to referer " + req.getHeader("referer"));
		// resp.sendRedirect(req.getHeader("referer") != null &&
		// !"".equals(req.getHeader("referer")) ? req.getHeader("referer") :
		// "localhost:8088");
	}

}
