package ca.utoronto.eil.ontology.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.ServiceException;

@Service
public class UnsubscribeService {

	private static final Logger logger = Logger.getLogger(UnsubscribeService.class);
	@Autowired @Qualifier("codes") private Properties codes;
	
	public UnsubscribeService() {
		
	}
	
	/**
	 * Release subscription relationship between subject identified by iriStr and classes identified by classesStr
	 * 
	 * @param iriStr subject IRI in format <IRI>
	 * @param classesStr formatted classes in format <class 1>,<class 2>, ..., <class n>
	 * @param uuid uuid used to identify web request
	 * @param test flag indicating whether the business logic should be run in test mode
	 * 
	 * @throws ServiceException anything that went wrong during service period.
	 */
	public void doUnsubscribe(String iriStr, String classesStr, String uuid, Boolean test) throws ServiceException {
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
		
		//[TODO] Call data access objects
	}
}
