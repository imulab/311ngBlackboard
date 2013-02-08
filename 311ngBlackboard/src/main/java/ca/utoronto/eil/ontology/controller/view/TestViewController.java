package ca.utoronto.eil.ontology.controller.view;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/test")
public class TestViewController {

	private static final Logger logger = Logger.getLogger(TestViewController.class);
	
	@RequestMapping(value="/main", method=RequestMethod.GET)
	public ModelAndView mainTestViewHandler() {
		logger.info("rest/test/main view requested");
		return new ModelAndView("testMain");
	}
	
	@RequestMapping(value="/subscribe", method=RequestMethod.GET)
	public ModelAndView subscribeTestViewHandler() {
		logger.info("rest/test/subscribe view requested");
		return new ModelAndView("testSubscribe");
	}
	
	@RequestMapping(value="/unsubscribe", method=RequestMethod.GET)
	public ModelAndView unsubscribeTestViewHandler() {
		logger.info("rest/test/unsubscribe view requested");
		return new ModelAndView("testUnsubscribe");
	}
	
	@RequestMapping(value="/publish", method=RequestMethod.GET)
	public ModelAndView publishTestViewHandler() {
		logger.info("rest/test/publish view requested");
		return new ModelAndView("testPublish");
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public ModelAndView queryTestViewHandler() {
		logger.info("rest/test/query view requested");
		return new ModelAndView("testQuery");
	}
}
