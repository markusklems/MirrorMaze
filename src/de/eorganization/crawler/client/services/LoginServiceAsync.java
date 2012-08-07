package de.eorganization.crawler.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.eorganization.crawler.client.model.LoginInfo;


public interface LoginServiceAsync {

	void login(String requestUri, AsyncCallback<LoginInfo> callback);

}
