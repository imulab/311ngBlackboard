package ca.utoronto.eil.ontology.controller.request.test;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.utoronto.eil.ontology.model.Response;

import com.google.gson.Gson;

@Controller
@RequestMapping("/request/test")
public class NotificationReceiveRequestController {

	private static final Logger logger = Logger.getLogger(NotificationReceiveRequestController.class);
	@Autowired private Gson gson;
	
	/**
	 * Handles request from <application url>/rest/request/test/notification
	 * Test method to print out the notification part in the publish action business logic
	 * 
	 * @param quads quads to be published
	 * @return uniform response in JSON format with state field marked 'success'
	 */
	@RequestMapping(value="/notification", method=RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String receiveNotification(
			@RequestParam(value="quads", required=true) String quads) {
		
		Response response = new Response();
		response.setState("success");
		
		logger.info(">>> notification test request received, assigned UUID = [" + response.getUuid() + "]");
		
		logger.info(">>> quads=" + ((quads.length() > 10) ? (quads.substring(1, 10) + "...") : (quads)) + "; length=" + quads.length() );
		
		return gson.toJson(response);
	}
}