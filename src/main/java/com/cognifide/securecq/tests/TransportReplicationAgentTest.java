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
import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;

/**
 * Check if the replication and reverse replication agents are activated on author
 * 
 * @author sebastienveluz
 * 
 */
public class TransportReplicationAgentTest extends AbstractTest implements AuthorTest {

	public TransportReplicationAgentTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String transportAgentUrl = url + "/etc/replication/agents.author/publish/jcr:content.json";
		String reverseAgentUrl = url + "/etc/replication/agents.author/publish_reverse/jcr:content.json";
		
		Boolean isActivatedTransport=isAgentActivated(transportAgentUrl);
		Boolean isActivatedReverse=isAgentActivated(reverseAgentUrl);
		
		if(isActivatedTransport){
			addInfoMessage("Transport agent on %s is activated %s", instanceName, transportAgentUrl);
		} else {
			addErrorMessage("You need to activate the transport agent on %s: %s", instanceName, transportAgentUrl);
		}
		
		if(isActivatedReverse){
			addInfoMessage("Reverse replication agent on %s is activated %s", instanceName, reverseAgentUrl);
		} else {
			addErrorMessage("You need to activate the reverse replication agent on %s: %s", instanceName, reverseAgentUrl);
		}
		
		return isActivatedTransport && isActivatedReverse;
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
