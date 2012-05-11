package edu.kit.aifb.mirrormaze.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.kit.aifb.mirrormaze.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) throws IllegalArgumentException {
		AmiManager.saveAmi(repository, imageId, imageLocation, imageOwnerAlias, ownerId, name, description, architecture, platform, imageType);
	}


}
