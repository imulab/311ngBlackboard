package ca.utoronto.eil.ontology.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;

@Service
public class PublishService {

	private static final Logger logger = Logger.getLogger(PublishService.class);
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired private NotificationService notificationService;
	@Autowired private SubscribeService subscribeService;
	
	public PublishService() {
		
	}
	
	/**
	 * Publish the instances identified by quadsStr to the database, then retrieve a list of
	 * subscribers and notify them what has just been published.
	 * 
	 * @param quadsStr instances
	 * @param uuid uuid to identify web request
	 * @param test flag to indicate whether test mode is on
	 * 
	 * @throws ServiceException anything that went wrong
	 */
	public void doPublish(String quadsStr, String uuid, Boolean test) throws ServiceException {
		List<Quad> quads = new ArrayList<Quad>();
		
		//Ensure quadsStr is not null
		if (quadsStr == null) {
			logger.error("[" + uuid + "] input is null.");
			throw new ServiceException("code:E0011");
		}
		
		//Validate quadsStr
		String[] quadArray = quadsStr.split(",");
		for (String each : quadArray) {
			try {
				Quad eachQuad = new Quad(each, uuid);
				logger.info("[" + uuid + "] " + each + " is valid");
				quads.add(eachQuad);
			} catch (ParameterException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		
		//[TODO] Call data access object to insert
		
		//For each quad, send notifications
		for (Quad each : quads) {
			List<String> subscriberIRIs = subscribeService.getImmediateSubscribers(each.getGraph(), each.getSubject(), uuid);
			if (subscriberIRIs != null) {
				for (String eachIRI : subscriberIRIs) {
					notificationService.doNotification(eachIRI, each.toString(), uuid, test);
				}
			}
		}// End of send notifications
		
	}
}
