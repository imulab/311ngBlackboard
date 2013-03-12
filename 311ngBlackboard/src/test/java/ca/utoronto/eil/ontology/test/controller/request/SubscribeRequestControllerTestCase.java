package ca.utoronto.eil.ontology.test.controller.request;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.controller.request.SubscribeRequestController;

import junit.framework.TestCase;

/**
 * Not doing any complicated in-container testing here.
 * Make sure app deployed in tomcat container before running test
 * @author daividimu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class SubscribeRequestControllerTestCase extends TestCase {

	@Autowired private SubscribeRequestController controller;
	
	private static final String TEST_USER_NAME = "david";
	private static final String TEST_PASSWORD = "19890117";
	
	@Before
	public void prepare() {
		
	}
	
	@After
	public void cleanup() {
		
	}
}
