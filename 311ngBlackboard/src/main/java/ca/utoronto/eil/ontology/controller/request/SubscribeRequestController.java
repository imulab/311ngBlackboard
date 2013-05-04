package ca.utoronto.eil.ontology.controller.request;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.utoronto.eil.ontology.model.AuthenticationException;
import ca.utoronto.eil.ontology.model.Response;
import ca.utoronto.eil.ontology.model.ResponseImpl;
import ca.utoronto.eil.ontology.model.ServiceException;
import ca.utoronto.eil.ontology.service.SubscribeService;
import ca.utoronto.eil.ontology.service.UserService;

import com.google.gson.Gson;

@Controller
@RequestMapping("/request")
public class SubscribeRequestController {

	private static final Logger logger = Logger.getLogger(SubscribeRequestController.class);
	@Autowired private Gson gson;
	@Autowired private UserService userService;
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired private SubscribeService subscribeService;
	
	/**
	 * <p>
	 * Handle request from <application url>/rest/request/subscribe.
	 * 
	 * <p>
	 * This servlet function checks the user identified by parameter {@code username} and parameter {@code password}
	 * using {@link ca.utoronto.eil.ontology.service.UserService#doAuthenticate(String, String, String) doAutenticate}.
	 * If credentials check out correct, it will forward the request to 
	 * {@link ca.utoronto.eil.ontology.service.SubscribeService#doSubscribe(String, String, String, Boolean) doSubscribe}
	 * to process.
	 * 
	 * <p>
	 * It will return a JSON format of {@link ca.utoronto.eil.ontology.model.ResponseImpl response structure}. The details
	 * of the request processing will be recorded inside.
	 * 
	 * @param username username of requestor
	 * @param password password of requestor
	 * @param iri Internal Resource Identifier of the subscriber
	 * @param classes pairs of <graph name><class or individual> separated by commas
	 * @param test optional variable to indicate test mode is on (will walk through business logic but will not commit)
	 * 
	 * @return uniform response in JSON format
	 */
	@RequestMapping(value="/subscribe", method=RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String subscribe(
			@RequestParam(value="username", required=true) String username,
			@RequestParam(value="password", required=true) String password,
			@RequestParam(value="IRI", required=true) String iri,
			@RequestParam(value="classes", required=true) String classes,
			@RequestParam(value="test", required=false) Boolean test) {
		
		// Initialize response structure
		ResponseImpl response = new ResponseImpl();
		logger.info("subscribe request received, assigned UUID = [" + response.getUuid() + "]");
		
		// Authenticate user
		try {
			userService.doAuthenticate(username, password, response.getUuid());
		} catch (AuthenticationException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}
		
		// Handle subscription
		try {
			subscribeService.doSubscribe(iri, classes, response.getUuid(), (test == null) ? (false) : (test));
		} catch (ServiceException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}
		
		// Proper return of response structure
		logger.info("[" + response.getUuid() + "] request complete");
		response.setState("success");
		return gson.toJson(response);
	}
}
