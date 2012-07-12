package de.eorganization.crawler.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.eorganization.crawler.client.model.LoginInfo;


public interface LoginServiceAsync {

	void login(String requestUri, AsyncCallback<LoginInfo> callback);

}
