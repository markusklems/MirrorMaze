/**
 * 
 */
package edu.kit.aifb.mirrormaze.server.servlets;

import gwtupload.server.exceptions.UploadActionException;
import gwtupload.server.gae.AppEngineUploadAction;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

/**
 * @author mugglmenzel
 *
 */
public class JSONBulkUploadServlet extends AppEngineUploadAction {

	/* (non-Javadoc)
	 * @see gwtupload.server.UploadAction#executeAction(javax.servlet.http.HttpServletRequest, java.util.List)
	 */
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		
		
		String ret = "";
	    for (FileItem item : sessionFiles) {
	      if (!item.isFormField()) {
	        // Do anything with the file.
	        System.out.println("Processing file: " + item.getName() + "(size: " + item.getSize() + ")");
	        // Update the string to return;
	        ret += "server message";
	      }
	    }
	    super.removeSessionFileItems(request);
	    return ret;
	}

}
