package edu.kit.aifb.mirrormaze.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.kit.aifb.mirrormaze.client.model.LoginInfo;

public interface LoginServiceAsync {

	void login(String requestUri, AsyncCallback<LoginInfo> callback);

}
