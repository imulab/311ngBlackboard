package ca.utoronto.eil.ontology.service;

import info.aduna.io.IOUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.utoronto.eil.ontology.model.ServiceException;

@Service
public class NotificationService {

	private static final Logger logger = Logger.getLogger(NotificationService.class);
	@Autowired @Qualifier("codes") private Properties codes;
	
	public NotificationService() {
		
	}
	
	/**
	 * Sends notification to the client-specified URI
	 * 
	 * @param destinationIRI URI to send notifications to
	 * @param quadsStr notification content
	 * 
	 * @throws ServiceException anything that went wrong during the process
	 */
	public void doNotification(String destinationIRI, String quadsStr, String uuid, Boolean test) throws ServiceException {
		try {
			
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http").setHost("localhost:8080/311ngBlackboard")
					.setPath("/rest/request/test/notification")
					.setParameter("quads", quadsStr);
			
			URI uri = null;
			try {
				uri = builder.build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			HttpClient client = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(uri);
			HttpResponse response = null;
			try {
				response = client.execute(httppost);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				
			logger.info("[" + uuid + "] Response: " + body);
			
		
		} catch (IllegalArgumentException e) {
			logger.error("[" + uuid + "] illegal argument: " + destinationIRI + ".");
			throw new ServiceException("msg:[E0012] " + codes.getProperty("E0012") + " Problem URI: " + destinationIRI);
		} catch (ClientProtocolException e1) {
			logger.error("[" + uuid + "] client protocol violation in sending notification");
			throw new ServiceException("msg:[E0013] " + codes.getProperty("E0013") + " Problem URI: " + destinationIRI);
		} catch (IOException e2) {
			logger.error("[" + uuid + "] I/O violation in sending notification");
			throw new ServiceException("msg:[E0014] " + codes.getProperty("E0014") + " Problem URI: " + destinationIRI);
		}
	}
}
