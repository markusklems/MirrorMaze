package edu.kit.aifb.mirrormaze.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void saveAmi(String repository, String imageId,
			String imageLocation, String imageOwnerAlias, String ownerId,
			String name, String description, String architecture,
			String platform, String imageType, AsyncCallback<Void> callback);
}
