package ca.utoronto.eil.ontology.service;

import java.util.Iterator;
import java.util.Properties;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.utoronto.eil.ontology.model.AuthenticationException;
import ca.utoronto.eil.ontology.model.LoadUserException;
import ca.utoronto.eil.ontology.model.User;

@Service
public class UserService {
	
	private static final Logger logger = Logger.getLogger(UserService.class);
	private static Boolean USER_LOADED_IN_CACHE = Boolean.FALSE;
	
	@Autowired @Qualifier("system_fact") private Properties systemProps;
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired private CacheManager cacheManager;
	
	public UserService() {
		
	}
	
	/**
	 * Try to load user authentication data from the xml file located at the path identifiied by 
	 * key 'user.file.location' in system_fact properties.
	 * The function will parse the file and cache the data for later reference.
	 * The function will also mark the USER_LOADED_IN_CACHE flag to its corresponding status
	 * 
	 * @throws LoadUserException anything went wrong during parsing, 
	 * usually identifier either by error codes or messages directly
	 */
	public void loadUserDetailsIntoCache(String uuid) throws LoadUserException {
		
		logger.info("[" + uuid + "] loading user details");
		
		Cache cache = cacheManager.getCache(systemProps.getProperty("cache.name"));
		if (cache == null) {
			logger.fatal("[" + uuid + "]cache is NULL");
			
			USER_LOADED_IN_CACHE = Boolean.FALSE;
			throw new LoadUserException("code:E0001");
		}
		
		logger.info("[" + uuid + "]parsing user auth info");
		try {
			
			//Read in user authentication file
			SAXReader reader = new SAXReader();
			Document document = reader.read(systemProps.getProperty("user.file.location"));
			
			//Parse root element
			org.dom4j.Element root = document.getRootElement();
			
			//Parse each 'user' node
			for (Iterator i = root.elementIterator("user"); i.hasNext();) {
				org.dom4j.Element userElem = (org.dom4j.Element) i.next();
				User user = new User();
				
				//Parse 'username' and 'password' inside 'user' node
				for (Iterator j = userElem.elementIterator(); j.hasNext();) {
					org.dom4j.Element attrElem = (org.dom4j.Element) j.next();
					if (attrElem.getName().equals("username")) {
						user.setUsername((String) attrElem.getData());
					} else if (attrElem.getName().equals("password")) {
						user.setPassword((String) attrElem.getData());
					}
				}// End for parsing 'username' and 'password'
			
				cache.put(new Element(user.getUsername(), user.getPassword()));
				
			} //End of parsing 'user'
			
			USER_LOADED_IN_CACHE = Boolean.TRUE;
			logger.info("[" + uuid + "] parse finished. user auth info loaded into cache");
		} catch (DocumentException de) {
			logger.fatal("[" + uuid + "] cannot read user auth file");
			
			USER_LOADED_IN_CACHE = Boolean.FALSE;
			throw new LoadUserException("msg:[E0002] " + codes.getProperty("E0002") + " Please check location: " + systemProps.getProperty("user.file.location"));
		}
	}
	
	/**
	 * Perform authentication check for user. 
	 * The function will load the user information into cache if necessary (usually just expected for first time op)
	 * 
	 * @param username user name
	 * @param password password
	 * 
	 * @throws AuthenticationException anything went wrong in the process.
	 */
	public void doAuthenticate(String username, String password, String uuid) throws AuthenticationException {
		logger.info("[" + uuid + "] auth requested for [" + username + "," + password + "]");
		
		//Load user information into cache if necessary
		if (!USER_LOADED_IN_CACHE) {
			try {
				loadUserDetailsIntoCache(uuid);
			} catch (LoadUserException le) {
				throw new AuthenticationException(le.getMessage(), le.getCause());		//Simply pass up
			}
		}
		
		Cache cache = cacheManager.getCache(systemProps.getProperty("cache.name"));
		if (cache == null) {
			logger.fatal("[" + uuid + "] cache is NULL");
			USER_LOADED_IN_CACHE = Boolean.FALSE;
			throw new AuthenticationException("code:E0001");
		}
		
		if (cache.get(username) == null) {
			logger.error("[" + uuid + "] " + username + " does not exist");
			throw new AuthenticationException("code:E0004");
		}
		
		if (!cache.get(username).getObjectValue().equals(password)) {
			logger.error("[" + uuid + "] password mismatch");
			throw new AuthenticationException("code:E0003");
		}
		
		logger.info("[" + uuid + "] authentication successful");
	}
}
