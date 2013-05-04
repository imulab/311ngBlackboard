package ca.utoronto.eil.ontology.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

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
public class SubscribeService {

	private static final Logger logger = Logger.getLogger(SubscribeService.class);
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired @Qualifier("system_fact") private Properties systemProps;
	@Autowired @Qualifier("AGraphDao") private AGraphDao dao;
	
	public SubscribeService() {
		
	}
	
	/**
	 * <p>
	 * Establish subscription relationship between subject identified by {@code iriStr} and classes identified by {@code classesStr}.
	 * The predicate that will be inserted into the database is defined at {@link classpath:property/system_fact.properties} by the key
	 * {@code agraph.server.graph.subscribe.predicate}.
	 * 
	 * @param iriStr subject IRI in format <IRI>
	 * @param classesStr formatted <graph><class> pairs in format <graph><class>,<graph><class>,...,<graph><class>
	 * @param uuid uuid used to identify web request
	 * @param test flag indicating whether the business logic should be run in test mode
	 * 
	 * @throws ServiceException anything that went wrong during service period.
	 */
	public void doSubscribe(String iriStr, String classesStr, String uuid, Boolean test) throws ServiceException {
		IRI identifier = null;
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
		
		// Validate input classesStr
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
		
		// Form a quad to call insert
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
			dao.insertQuads(quads, uuid, test);
		} catch (AGraphDataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * <p>
	 * Search and return a list of subscribers' IRI to the entire super class hierarchy of the instance
	 * identified by {@code graph} and {@code subject}.
	 * 
	 * <p>
	 * For example, if we have the following triples in the default graph: <br>
	 * 1. classB rdfs:subClassOf classA <br>
	 * 2. classC rdfs:subClassOf classB <br>
	 * 3. instance1 rdf:type classC <br>
	 * 4. iri1 subscribes classC <br>
	 * 5. iri2 subscribes classB <br>
	 * 6. iri3 subscribes classB <br>
	 * 7. iri1 subscribes classA <br>
	 * A search on subscribers for instance1 in the default graph will return iri1, iri2, iri3.
	 * 
	 * <p>
	 * Although class hierarchy generally follows a tree structure, this function can handle graphs structure class hierarchies.
	 * For instance, "classC rdfs:subClassOf classB","classC rdfs:subClassOf classA" is allowed. 
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
		
		// Initialize variables
		List<String> classes = null;
		Queue<String> classQueue = new LinkedList<String>();
		Queue<String> lookupQueue = new LinkedList<String>();
		List<String> subscribers = new ArrayList<String>();
		
		/*
		 * Get the immediate class of the instance. The data access object
		 * searches with the rdf:type predicate. Fill the result into classQueue.
		 */
		try {
			IRI instanceIRI = new IRI("<" + subject + ">", uuid);
			logger.info("[" + uuid + "] Getting immediate class for instance " + subject);
			
			classes = dao.getImmediateClass(instanceIRI, graph, uuid);
			logger.info("[" + uuid + "] Found " + classes.size() + " immediate class for instance " + subject);
			
		} catch (ParameterException e) {
			throw new ServiceException(e.getMessage());
		} catch (AGraphDataAccessException e1) {
			throw new ServiceException(e1.getMessage());
		}
		
		for (String eachClass : classes) {
			if (!classQueue.contains(eachClass)) {
				classQueue.offer(eachClass);
			}
		}
		
		/*
		 * For each of the classes we have, search for the super classes using the rdfs:subClassOf
		 * predicate. Search results will be checked for existence. New results will be added back
		 * to the classQueue for search of its own super class later. The search will stop when
		 * classQueue is depleted. We will have reference to the entire super class hierarchy of
		 * the requested instance after this while loop.
		 */
		while(!classQueue.isEmpty()) {
			
			String firstElem = classQueue.poll();
			List<String> superClasses = null;
			
			// skip the search if head is NULL
			if (firstElem == null)
				continue;
			
			// skip the search if it has already be done (lookupQueue is where we dump the non-duplicate super classes)
			if (lookupQueue.contains(firstElem))
				continue;
			
			try {
				IRI classIRI = new IRI("<" + firstElem + ">", uuid);
				logger.info("[" + uuid + "] Getting super class for class " + firstElem);
				
				// call dao to get the super class of the current class
				superClasses = dao.getSuperClass(classIRI, graph, uuid);
				
				// offer the head to lookupQueue if no more super classes
				if (superClasses == null || superClasses.size() == 0) {
					lookupQueue.offer(firstElem);
					continue;
				}
				
				logger.info("[" + uuid + "] Found " + superClasses.size() + " super class for class " + firstElem);
				
				// add all retrieved super classes to classQueue for later super class search
				for (String eachSuperClass : superClasses)
					classQueue.offer(eachSuperClass);
				
			} catch (ParameterException e) {
				throw new ServiceException(e.getMessage());
			} catch (AGraphDataAccessException e1) {
				throw new ServiceException(e1.getMessage());
			}
			
			// Since we have completed the super class search for this head, add it to lookupQueue for later subscriber search.
			if (!lookupQueue.contains(firstElem)) {
				lookupQueue.offer(firstElem);
			}
			
		} // each of while loop
		
		
		/*
		 * Merge the content of array list "classes" and lookpQueue to ensure we do not miss any classes.
		 * Also maintain the distinctness while merging. Use array list for the output of merge for convenience
		 * of subscriber search later.
		 */
		for (String each : classes) {
			if (!lookupQueue.contains(each))
				lookupQueue.offer(each);
		}
		classes = new ArrayList<String>();
		while (!lookupQueue.isEmpty()) {
			String nextElem = lookupQueue.poll();
			
			if (!classes.contains(nextElem))
				classes.add(nextElem);
		}
		
		logger.info("[" + uuid + "] We will search subscribers for " + classes.size() + " classes.");
		
		
		/*
		 * For each class, search for subscribers. Maintain distinctness while dumping results.
		 */
		if (classes != null) {
			
			for (String eachClass : classes) {
				List<String> eachSubscriber = null;
				
				try {
					
					logger.info("[" + uuid + "] Looking for subscribers for class " + eachClass);
					
					// call dao to search for subscribers.
					eachSubscriber = dao.getSubscriberIRI(graph, eachClass, uuid);
					logger.info("[" + uuid + "] Found " + eachSubscriber.size() + " subscriber for class " + eachClass);
					
					// Dump to subscribers if not already existed
					if (eachSubscriber != null) {
						for (String each : eachSubscriber) {
							if (!subscribers.contains(each))
								subscribers.add(each);
						}
					}
					
				} catch (AGraphDataAccessException e) {
					throw new ServiceException(e.getMessage());
				}
			}
		}
		
		/*
		 * Return final result of searh
		 */
		return subscribers;
	}
}
