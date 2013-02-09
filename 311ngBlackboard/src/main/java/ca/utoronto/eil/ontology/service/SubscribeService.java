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
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;

@Service
public class SubscribeService {

	private static final Logger logger = Logger.getLogger(SubscribeService.class);
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired @Qualifier("system_fact") private Properties systemProps;
	@Autowired @Qualifier("AGraphDao") private AGraphDao dao;
	
	public SubscribeService() {
		
	}
	
	/**
	 * Establish subscription relationship between subject identified by iriStr and classes identified by classesStr
	 * 
	 * @param iriStr subject IRI in format <IRI>
	 * @param classesStr formatted classes in format <class 1>,<class 2>, ..., <class n>
	 * @param uuid uuid used to identify web request
	 * @param test flag indicating whether the business logic should be run in test mode
	 * 
	 * @throws ServiceException anything that went wrong during service period.
	 */
	public void doSubscribe(String iriStr, String classesStr, String uuid, Boolean test) throws ServiceException {
		IRI identifier = null;
		List<IRI> classes = new ArrayList<IRI>(); 
		
		//Ensure not null
		if (iriStr == null || classesStr == null) {
			logger.error("[" + uuid + "] input is null.");
			throw new ServiceException("code:E0011");
		}
		
		//Validate input iriStr
		try {
			identifier = new IRI(iriStr, uuid);
			logger.info("[" + uuid + "] " + iriStr + " is valid");
		} catch (ParameterException e) {
			throw new ServiceException(e.getMessage());
		}
		
		//Validate input classesStr
		String[] classesArray = classesStr.split(",");
		for (String each : classesArray) {
			try {
				IRI eachClass = new IRI(each, uuid);
				logger.info("[" + uuid + "] " + each + " is valid");
				classes.add(eachClass);
			} catch (ParameterException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		
		//Form a quad to call insert
		List<Quad> quads = new ArrayList<Quad>();
		for (IRI eachClass : classes) {
			String rawString = "<" + systemProps.getProperty("agraph.server.graph.subscribe") + ">" 
								+ "<" + identifier.getIri() + ">"
								+ "<" + systemProps.getProperty("agraph.server.graph.subscribe.predicate") + ">"
								+ "<" + eachClass.getIri() + ">";
			try {
				quads.add(new Quad(rawString, uuid));
			} catch (ParameterException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		
		//Call data access objects
		try {
			dao.insertQuads(quads, uuid, test);
		} catch (AGraphDataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * Return a list of subscribers' IRI to the immediate class of the instance identified by graph and subject
	 * 
	 * @param graph graph name
	 * @param subject subject IRI
	 * @param uuid uuid to identify the web request
	 * 
	 * @return list of subscribers' IRI
	 * 
	 * @throws ServiceException anything that went wrong
	 */
	public List<String> getImmediateSubscribers(String graph, String subject, String uuid) throws ServiceException {
		List<String> classes = null;
		List<String> subscribers = null;
		
		//Step 1: Get all immediate super classes
		try {
			IRI instanceIRI = new IRI("<" + subject + ">", uuid);
			classes = dao.getImmediateClass(instanceIRI, graph, uuid);
		} catch (ParameterException e) {
			throw new ServiceException(e.getMessage());
		} catch (AGraphDataAccessException e1) {
			throw new ServiceException(e1.getMessage());
		}
		
		//Step 2: Get all the subscriber of those classes
		if (classes != null) {
			for (String eachClass : classes) {
				try {
					subscribers = dao.getSubscriberIRI(eachClass, uuid);
				} catch (AGraphDataAccessException e) {
					throw new ServiceException(e.getMessage());
				}
			}
		}
		
		return subscribers;
	}
}
