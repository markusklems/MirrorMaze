package de.eorganization.crawler.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.eorganization.crawler.client.model.LoginInfo;


@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

	public LoginInfo login(String requestUri);

}
