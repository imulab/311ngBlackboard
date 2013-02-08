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
	 * Handle request from <application url>/rest/request/publish
	 * 
	 * @param username username of requestor
	 * @param password password of requestor
	 * @param quads <graph name><subject><property><object> separated by commas
	 * @param test optional variable to indicate test mode is on (will walk through business logic but will not commit)
	 * 
	 * @return uniform response in JSON format
	 */
	@RequestMapping(value="/publish", method=RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String subscribe(
			@RequestParam(value="username", required=true) String username,
			@RequestParam(value="password", required=true) String password,
			@RequestParam(value="quads", required=true) String quads,
			@RequestParam(value="test", required=false) Boolean test) {
		
		Response response = new Response();
		logger.info("publish request received, assigned UUID = [" + response.getUuid() + "]");
		
		//Authenticate user
		try {
			userService.doAuthenticate(username, password, response.getUuid());
		} catch (AuthenticationException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}
		
		//Publish
		try {
			publishService.doPublish(quads, response.getUuid(), (test == null) ? (false) : (test));
		} catch (ServiceException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}
		
		logger.info("[" + response.getUuid() + "] request complete");
		response.setState("success");
		return gson.toJson(response);
	}
}