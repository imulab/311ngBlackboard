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
import ca.utoronto.eil.ontology.service.PublishService;
import ca.utoronto.eil.ontology.service.UserService;

import com.google.gson.Gson;

@Controller
@RequestMapping("/request")
public class PublishRequestController {

	private static final Logger logger = Logger.getLogger(PublishRequestController.class);
	@Autowired private Gson gson;
	@Autowired private UserService userService;
	@Autowired @Qualifier("codes") private Properties codes;
	@Autowired private PublishService publishService;
	
	/**
	 * <p>
	 * Handle request from <application url>/rest/request/publish
	 * 
	 * <p>
	 * This servlet function checks the user identified by parameter {@code username} and parameter {@code password}
	 * using {@link ca.utoronto.eil.ontology.service.UserService#doAuthenticate(String, String, String) doAutenticate}.
	 * If credentials check out correct, it will forward the request to 
	 * {@link ca.utoronto.eil.ontology.service.PublishService#doPublish(String, String, Boolean, Boolean) doPublish}
	 * to process.
	 * 
	 * @param username username of requestor
	 * @param password password of requestor
	 * @param quads <graph name><subject><predicate><object> separated by commas
	 * @param test optional variable to indicate test mode is on (will walk through business logic but will not commit)
	 * 
	 * @return uniform response in JSON format
	 */
	@RequestMapping(value="/publish", method=RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String subscribe(
			@RequestParam(value="username", required=true) String username,
			@RequestParam(value="password", required=true) String password,
			@RequestParam(value="quads", required=true) String quads,
			@RequestParam(value="test", required=false) Boolean test,
			@RequestParam(value="enableNotify", required=false) Boolean enableNotify) {
		
		// Initialize response structure
		ResponseImpl response = new ResponseImpl();
		logger.info("publish request received, assigned UUID = [" + response.getUuid() + "]");
		
		// Authenticate user
		try {
			userService.doAuthenticate(username, password, response.getUuid());
		} catch (AuthenticationException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}
		
		// Handle publish, default test mode is off, default notify mode is on
		try {
			publishService.doPublish(quads, 
					response.getUuid(), 
					(test == null) ? (false) : (test), 
					(enableNotify == null) ? (true) : (enableNotify));
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
