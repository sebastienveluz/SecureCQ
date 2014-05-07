package com.cognifide.securecq.tests;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.cognifide.securecq.markers.AuthorTest;
import com.cognifide.securecq.markers.PublishTest;
import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;

/**
 * Check if the dispatcher flush agent is activated on author and publish
 * 
 * @author sebastienveluz
 * 
 */
public class DispatcherFlushTest extends AbstractTest implements AuthorTest, PublishTest {

	public DispatcherFlushTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String flushAgentUrl = url 
				+ (instanceName.equals("publish") ? "/etc/replication/agents.publish/flush/jcr:content.json" : "/etc/replication/agents.author/flush/jcr:content.json");
		if (isAgentActivated(flushAgentUrl)){
			addInfoMessage("Dispatcher flush agent on %s is activated %s", instanceName, flushAgentUrl);
			return true;
		} else {
			addErrorMessage("You need to activate the dispatcher flush agent on %s: %s", instanceName, flushAgentUrl);
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private Boolean isAgentActivated(String url) throws URISyntaxException, ClientProtocolException,
			IOException, AuthenticationException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(url);
		request.addHeader(new BasicScheme().authenticate(creds, request));
		HttpResponse response = authorizedClient.execute(request);
		return httpHelper.getJsonBooleanValue(
				EntityUtils.toString(response.getEntity()), "enabled");
	}

}
