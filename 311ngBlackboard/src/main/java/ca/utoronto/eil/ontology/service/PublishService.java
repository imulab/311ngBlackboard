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
	 * Publish the instances identified by quadsStr to the database, then
	 * retrieve a list of subscribers and notify them what has just been
	 * published.
	 * 
	 * @param quadsStr
	 *            instances
	 * @param uuid
	 *            uuid to identify web request
	 * @param test
	 *            flag to indicate whether test mode is on
	 * @param enableNotify
	 *            flag to indicate if service should continue to notify
	 *            subscribers when publish is done
	 * 
	 * @throws ServiceException
	 *             anything that went wrong
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

		if (enableNotify) {
			// Split the list of quads into groups by subjects
			List<List<Quad>> groupByQuads = QuadUtil.groupBySubject(quads);
			logger.info("[" + uuid + "] the list of quads are seperated to "
					+ groupByQuads.size() + " based on subjects");

			for (List<Quad> eachGroupBy : groupByQuads) {
				if (eachGroupBy.size() == 0)
					continue;
				
				String graphToLook = eachGroupBy.get(0).getGraph();
				
				logger.info("[" + uuid + "]" + " Looking for subscribers of " + eachGroupBy.get(0).getSubject());
				
				List<String> subscriberIRIs = subscribeService
						.getImmediateSubscribers(graphToLook,
								eachGroupBy.get(0).getSubject(), uuid);
				
				logger.info("[" + uuid + "] found " + subscriberIRIs.size()
						+ " immediate subscribers for "
						+ eachGroupBy.get(0).getSubject());

				if (subscriberIRIs != null && subscriberIRIs.size() > 0) {
					String quadListStr = QuadUtil.convertToString(eachGroupBy);

					// Notify each subscriber
					for (String eachSubscriber : subscriberIRIs) {
						notificationService.doNotification(eachSubscriber,
								quadListStr, uuid, test);
					}
				}
			}
		}
	}
}
