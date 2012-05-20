package edu.kit.aifb.mirrormaze.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.client.ui.FileUpload;

import edu.kit.aifb.mirrormaze.client.model.Software;
import edu.kit.aifb.mirrormaze.server.AmiManager;

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
public class JSONImportServlet extends HttpServlet {

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

			// Gson gson = new Gson();
			// log.info("Parsed Gson " + gson.fromJson(new
			// InputStreamReader(stream), JsonObject.class));

			String jsonString = IOUtils.toString(stream);
			log.info("Got JSON String "
					+ jsonString.substring(0, jsonString.length() > 128 ? 128
							: jsonString.length() - 1));
			JSONObject json = new JSONObject();
			try {
				json = new JSONObject(jsonString);
				log.info("Parsed Json " + json);
				log.info("Json has keys: " + json.names());
			} catch (JSONException e) {
				log.severe("No JSON sent or wrong format.");
				return;
			}

			JSONObject software = json.getJSONObject("software");
			log.info("Software JSON " + software);

			JSONArray softwareNames = software.names();
			log.info("Key Array JSON " + softwareNames);
			for (int i = 0; i < softwareNames.length(); i++) {
				log.info("software "
						+ softwareNames.getString(i)
						+ " with version "
						+ software.getJSONObject(softwareNames.getString(i))
								.getString("version"));
				/*log.info("Saved object "
						+ AmiManager.saveSoftware(
								"",
								softwareNames.getString(i),
								software.getJSONObject(
										softwareNames.getString(i)).getString(
										"version")));*/
			}

			// store

		} catch (JSONException e) {

			e.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// log.info("returning to referer " + req.getHeader("referer"));
		// resp.sendRedirect(req.getHeader("referer") != null &&
		// !"".equals(req.getHeader("referer")) ? req.getHeader("referer") :
		// "localhost:8088");
	}
}
