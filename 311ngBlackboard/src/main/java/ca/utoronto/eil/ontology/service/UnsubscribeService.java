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
import ca.utoronto.eil.ontology.model.GraphClassPair;
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;

@Service
public class UnsubscribeService {

	private static final Logger logger = Logger.getLogger(UnsubscribeService.class);
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired @Qualifier("system_fact") private Properties systemProps;
	@Autowired @Qualifier("AGraphDao") private AGraphDao dao;

	public UnsubscribeService() {

	}

	/**
	 * <p>
	 * Release subscription relationship between subject identified by {@code iriStr}
	 * and classes identified by {@code classesStr}
	 * 
	 * @param iriStr	 subject IRI in format <IRI>
	 * @param classesStr formatted classes in format <class 1>,<class 2>, ..., <class n>
	 * @param uuid uuid used to identify web request
	 * @param test flag indicating whether the business logic should be run in test mode
	 * 
	 * @throws ServiceException anything that went wrong during service period.
	 */
	public void doUnsubscribe(String iriStr, String classesStr, String uuid,
			Boolean test) throws ServiceException {
		IRI identifier = null;
		List<IRI> classes = new ArrayList<IRI>();
		List<GraphClassPair> pairs = new ArrayList<GraphClassPair>();
		
		// Ensure not null
		if (iriStr == null || classesStr == null) {
			logger.error("[" + uuid + "] input is null.");
			throw new ServiceException("code:E0011");
		}

		// Validate input iriStr
		try {
			identifier = new IRI(iriStr, uuid);
			logger.info("[" + uuid + "] " + iriStr + " is valid");
		} catch (ParameterException e) {
			throw new ServiceException(e.getMessage());
		}
		
		//Validate input classesStr
		String[] pairsArray = classesStr.split(",");
		for (String each : pairsArray) {
			try {
				GraphClassPair eachPair = new GraphClassPair(each, uuid);
				logger.info("[" + uuid + "] " + each + " is valid");
				pairs.add(eachPair);
			} catch (ParameterException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		
		//Form a quad to call remove
		List<Quad> quads = new ArrayList<Quad>();
		for (GraphClassPair eachPair : pairs) {
			String rawString = "<" + eachPair.getGraph() + ">" 
								+ "<" + identifier.getIri() + ">"
								+ "<" + systemProps.getProperty("agraph.server.graph.subscribe.predicate") + ">"
								+ "<" + eachPair.getClassIRI() + ">";
			try {
				quads.add(new Quad(rawString, uuid));
			} catch (ParameterException e) {
				throw new ServiceException(e.getMessage());
			}
		}

		// Call data access objects
		try {
			dao.removeQuads(quads, uuid, test);
		} catch (AGraphDataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
