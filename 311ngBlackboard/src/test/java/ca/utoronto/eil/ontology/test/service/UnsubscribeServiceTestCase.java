package ca.utoronto.eil.ontology.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;
import ca.utoronto.eil.ontology.service.SubscribeService;
import ca.utoronto.eil.ontology.service.UnsubscribeService;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class UnsubscribeServiceTestCase extends TestCase {

	@Autowired private UnsubscribeService unsubService;
	@Autowired private SubscribeService subService;
	
	private static final String RAW_STR_1 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testClass1>"
			+ "<http://www.test.org/testPredicatex>" + "<object>";
	private static final String RAW_STR_2 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testClass2>"
			+ "<http://www.test.org/testPredicatey>" + "<object>";
	private static final String INSTANCE_1 = "http://www.test.org/testInstance1";
	private static final String INSTANCE_2 = "http://www.test.org/testInstance2";
	private static final String INSTANCE_3 = "http://www.test.org/testInstance3";
	private static final String MOCK_UUID = "mark3";
	
	@Autowired
	private AGraphDao dao;
	@Autowired @Qualifier("system_fact") 
	private Properties systemProps;
	private List<Quad> quads;
	
	@Before
	public void prepare() throws ParameterException, AGraphDataAccessException, ServiceException {
		this.quads = new ArrayList<Quad>();
		this.quads.add(new Quad(RAW_STR_1, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_2, MOCK_UUID));
		this.dao.insertQuads(quads, MOCK_UUID, false);
		
		subService.doSubscribe("<" + INSTANCE_1 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
		subService.doSubscribe("<" + INSTANCE_2 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
		subService.doSubscribe("<" + INSTANCE_3 + ">", "<http://www.test.org/testClass2>", MOCK_UUID, false);
	}
	
//	@Test
//	public void testDoUnsubscribe() throws ServiceException, AGraphDataAccessException {
//		
//		unsubService.doUnsubscribe("<" + INSTANCE_1 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
//		List<String> subscribers = this.dao.getSubscriberIRI("http://www.test.org/testClass1", MOCK_UUID);
//		assertNotNull(subscribers);
//		assertEquals(1, subscribers.size());
//		
//		unsubService.doUnsubscribe("<" + INSTANCE_2 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
//		subscribers = this.dao.getSubscriberIRI("http://www.test.org/testClass1", MOCK_UUID);
//		assertTrue(subscribers == null || subscribers.size() == 0);
//		
//		unsubService.doUnsubscribe("<" + INSTANCE_3 + ">", "<http://www.test.org/testClass2>", MOCK_UUID, false);
//		subscribers = this.dao.getSubscriberIRI("http://www.test.org/testClass2", MOCK_UUID);
//		assertTrue(subscribers == null || subscribers.size() == 0);
//	}
	
	@After
	public void cleanup() throws AGraphDataAccessException {
		this.dao.removeQuads(quads, MOCK_UUID, false);
	}
}
