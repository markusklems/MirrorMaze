package edu.kit.aifb.mirrormaze.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType) throws IllegalArgumentException;
}
