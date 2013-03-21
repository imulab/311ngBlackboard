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
import ca.utoronto.eil.ontology.model.QueryResult;
import ca.utoronto.eil.ontology.model.Response;
import ca.utoronto.eil.ontology.model.ResponseImpl;
import ca.utoronto.eil.ontology.model.ServiceException;
import ca.utoronto.eil.ontology.service.QueryService;
import ca.utoronto.eil.ontology.service.UserService;

import com.google.gson.Gson;

@Controller
@RequestMapping("/request")
public class QueryRequestController {

	private static final Logger logger = Logger
			.getLogger(QueryRequestController.class);
	@Autowired
	private Gson gson;
	@Autowired
	private UserService userService;
	@Autowired
	@Qualifier("codes")
	private Properties codes;
	@Autowired
	private QueryService queryService;

	/**
	 * Handle request from <application url>/rest/request/query.
	 * 
	 * @param username
	 *            username of requestor
	 * @param password
	 *            password of requestor
	 * @param query
	 *            SPARQL query string
	 * 
	 * @return uniform response in JSON format. (Query result location =
	 *         response.objectMap['query.result'])
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String subscribe(
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "query", required = true) String query) {

		ResponseImpl response = new ResponseImpl();
		logger.info("query request received, assigned UUID = ["
				+ response.getUuid() + "]");

		System.out.println("Received!!!");
		
		// Authenticate user
		try {
			userService.doAuthenticate(username, password, response.getUuid());
		} catch (AuthenticationException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}

		// Query
		try {
			QueryResult qResult = queryService.doQuery(query, response.getUuid());
			response.addObject("query.result", qResult);
			
			if (qResult.getQueryExecutionStatus())
				response.setState("success");
			else
				response.setState("fail");
			
		} catch (ServiceException e) {
			response.resolveException(e, codes);
			logger.info("[" + response.getUuid() + "] request aborted");
			return gson.toJson(response);
		}

		logger.info("[" + response.getUuid() + "] request complete");
		return gson.toJson(response);
	}
}
