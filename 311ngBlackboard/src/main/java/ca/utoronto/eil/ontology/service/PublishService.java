package ca.utoronto.eil.ontology.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;
import ca.utoronto.eil.ontology.util.QuadUtil;

@Service
public class PublishService {

	private static final Logger logger = Logger.getLogger(PublishService.class);
	@Autowired
	@Qualifier("codes")
	private Properties codes;
	@Autowired
	@Qualifier("system_fact")
	private Properties systemProps;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private SubscribeService subscribeService;
	@Autowired
	@Qualifier("AGraphDao")
	private AGraphDao dao;

	public PublishService() {

	}

	/**
	 * <p>
	 * Publish the instances identified by {@code quadsStr} to the database. After publish is complete,
	 * go up the class hierarchy and get a list subscribers of each level. All subscribers will be consolidated
	 * and recorded in a single list to avoid duplicate notification. Also note that the quads identified by
	 * {@code quadsStr} and seperated by its subject in advance of searching for subscribers. This
	 * is done to ensure a complete search for subscribers in the case that quads about multiple subjects are published
	 * at the same time.
	 * 
	 * <p>
	 * For details of searching for subscribers, please consult 
	 * {@link ca.utoronto.eil.ontology.service.SubscribeService#getImmediateSubscribers(String, String, String) getImmediateSubscribers}
	 * 
	 * <p>
	 * For details of notification, please consult
	 * {@link ca.utoronto.eil.ontology.service.NotificationService#doNotification(String, String, String, Boolean) doNotification}
	 * 
	 * @param quadsStr instances
	 * @param uuid uuid to identify web request
	 * @param test flag to indicate whether test mode is on
	 * @param enableNotify flag to indicate if service should continue to notify subscribers when publish is done
	 * 
	 * @throws ServiceException anything that went wrong
	 */
	public void doPublish(String quadsStr, String uuid, Boolean test,
			Boolean enableNotify) throws ServiceException {
		List<Quad> quads = new ArrayList<Quad>();

		// Ensure quadsStr is not null
		if (quadsStr == null) {
			logger.error("[" + uuid + "] input is null.");
			throw new ServiceException("code:E0011");
		}

		// Validate quadsStr
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

		// Call data access object to insert
		try {
			dao.insertQuads(quads, uuid, test);
		} catch (AGraphDataAccessException e) {
			throw new ServiceException(e.getMessage());
		}

		// Only do notification when notification mode is on. (This mode is on by default, by can be override)
		if (enableNotify) {
			
			// Split the list of quads into groups by subjects
			List<List<Quad>> groupByQuads = QuadUtil.groupBySubject(quads);
			logger.info("[" + uuid + "] the list of quads are seperated to "
					+ groupByQuads.size() + " based on subjects");

			// For each group of quads with the same subject
			for (List<Quad> eachGroupBy : groupByQuads) {
				
				if (eachGroupBy.size() == 0)
					continue;
				
				// Record the graph name to search in (assuming the subscription quads reside in the same graph context)
				String graphToLook = eachGroupBy.get(0).getGraph();
				logger.info("[" + uuid + "]" + " Looking for subscribers of " + eachGroupBy.get(0).getSubject());
				
				// Call subscription service to get the list of subscribers for the entire super class hierarchy.
				List<String> subscriberIRIs = subscribeService
						.getImmediateSubscribers(graphToLook,
								eachGroupBy.get(0).getSubject(), uuid);
				logger.info("[" + uuid + "] found " + subscriberIRIs.size()
						+ " subscribers for "
						+ eachGroupBy.get(0).getSubject());
				
				// Send notification
				if (subscriberIRIs != null && subscriberIRIs.size() > 0) {
					
					String quadListStr = QuadUtil.convertToString(eachGroupBy);
					
					// Notify each subscriber using the notification service
					for (String eachSubscriber : subscriberIRIs) {
						notificationService.doNotification(eachSubscriber,
								quadListStr, uuid, test);
					}
				}
			}
		}
	}
}
