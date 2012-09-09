package de.eorganization.crawler.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import de.eorganization.crawler.client.model.Language;
import de.eorganization.crawler.client.model.Software;
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

			String amiId = req.getHeader("AMI-ID");
			log.info("Got AMI ID:" + amiId);
			String repository = req.getHeader("REPO");
			log.info("Got REPO:" + repository);
			if (amiId == null || repository == null || "".equals(amiId)
					|| "".equals(repository)) {
				log.severe("AMI ID and REPO HTTP Header required.");
				return;
			}

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
				log.fine("Parsed Json " + json);
				log.finer("Json has keys: " + json.names());
			} catch (JSONException e) {
				log.severe("No JSON sent or wrong format.");
				return;
			}

			JSONObject software = json.optJSONObject("software");
			if (software != null) {
				log.finer("Software JSON " + software);

				JSONArray softwareNames = software.names();
				log.finer("Key Array JSON " + softwareNames);
				for (int i = 0; i < softwareNames.length(); i++) {
					log.fine("software "
							+ softwareNames.getString(i)
							+ " with version "
							+ software
									.getJSONObject(softwareNames.getString(i))
									.getString("version"));

					Map<String, String> softAttributes = new HashMap<String, String>();
					JSONArray softwareAttributes = software.getJSONObject(
							softwareNames.getString(i)).names();
					for (int j = 0; j < softwareAttributes.length(); j++)
						softAttributes.put(
								softwareAttributes.getString(j),
								software.getJSONObject(
										softwareNames.getString(i)).getString(
										softwareAttributes.getString(j)));

					Software soft = AmiManager.saveSoftware(amiId, repository,
							softwareNames.getString(i),
							software.getJSONObject(softwareNames.getString(i))
									.getString("version"), softAttributes);
					if (soft != null) {
						log.fine("Saved/restored software " + soft);
						// soft.getAttributes().putAll(softAttributes);
						// AmiManager.updateSoftware(soft);
						// log.fine("Saved object " + soft);
					} else
						log.severe("Not able to save software information for given Ami Id "
								+ amiId + "!");

				}
				List<String> names = new ArrayList<String>();
				for (int i = 0; i < softwareNames.length(); i++)
					names.add(softwareNames.getString(i));
				AmiManager.updateSoftwareNames(names);
				log.info("Saved " + softwareNames.length()
						+ " software objects");

			}

			JSONObject languages = json.optJSONObject("languages");
			if (languages != null) {
				log.finer("Languages JSON " + languages);

				JSONArray languagesNames = languages.names();
				log.finer("Key Array JSON " + languagesNames);
				for (int i = 0; i < languagesNames.length(); i++) {
					log.fine("languages "
							+ languagesNames.getString(i)
							+ " with version "
							+ languages.getJSONObject(
									languagesNames.getString(i)).getString(
									"version"));

					Map<String, String> langAttributes = new HashMap<String, String>();
					JSONArray languageAttributes = languages.getJSONObject(
							languagesNames.getString(i)).names();
					for (int j = 0; j < languageAttributes.length(); j++)
						langAttributes.put(
								languageAttributes.getString(j),
								languages.getJSONObject(
										languagesNames.getString(i)).getString(
										languageAttributes.getString(j)));

					Language lang = AmiManager.saveLanguage(amiId, repository,
							languagesNames.getString(i), languages
									.getJSONObject(languagesNames.getString(i))
									.getString("version"), langAttributes);
					if (lang != null) {
						log.fine("Saved/restored programming language " + lang);
						lang.getAttributes().putAll(langAttributes);
						AmiManager.updateLanguage(lang);
						log.fine("Saved object " + lang);
					} else
						log.severe("Not able to save programming language information for given Ami Id "
								+ amiId + "!");

				}
				log.info("Saved " + languagesNames.length()
						+ " programming language objects");
			}

			resp.getWriter().println(
					"Saving software packages and programming languages for "
							+ amiId + " (" + repository + ").");

		} catch (JSONException e) {
			log.severe("Error while parsing JSON upload" + e.getMessage()
					+ ", trace: " + e.getStackTrace());
			log.throwing(JSONImportServlet.class.getName(), "doPost", e);
			e.printStackTrace();

		} catch (Exception ex) {
			log.severe("Unexpected error" + ex.getMessage() + ", trace: "
					+ ex.getStackTrace());
			log.throwing(JSONImportServlet.class.getName(), "doPost", ex);
			ex.printStackTrace();
		}

		// log.info("returning to referer " + req.getHeader("referer"));
		// resp.sendRedirect(req.getHeader("referer") != null &&
		// !"".equals(req.getHeader("referer")) ? req.getHeader("referer") :
		// "localhost:8088");
	}
}
