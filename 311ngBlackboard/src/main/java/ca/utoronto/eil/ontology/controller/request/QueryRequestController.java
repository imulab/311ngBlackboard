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
import ca.utoronto.eil.ontology.service.UserService;

import com.google.gson.Gson;

@Controller
@RequestMapping("/request")
public class QueryRequestController {

	private static final Logger logger = Logger.getLogger(QueryRequestController.class);
	@Autowired private Gson gson;
	@Autowired private UserService userService;
	@Autowired @Qualifier("codes") private Properties codes;
	
	/**
	 * Handle request from <application url>/rest/request/query
	 * 
	 * @param username username of requestor
	 * @param password password of requestor
	 * @param query SPARQL query string
	 * 
	 * @return uniform response in JSON format. (Query result location = response.objectMap['query.result'])
	 */
	@RequestMapping(value="/query", method=RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String subscribe(
			@RequestParam(value="username", required=true) String username,
			@RequestParam(value="password", required=true) String password,
			@RequestParam(value="query", required=true) String query) {
		
		Response response = new Response();
		logger.info("query request received, assigned UUID = [" + response.getUuid() + "]");
		
		//Authenticate user
		try {
			userService.doAuthenticate(username, password, response.getUuid());
		} catch (AuthenticationException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}
		
		logger.info("[" + response.getUuid() + "] request complete");
		return gson.toJson(response);
	}
}
