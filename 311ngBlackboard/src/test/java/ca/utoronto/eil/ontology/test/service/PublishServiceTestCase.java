package ca.utoronto.eil.ontology.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;
import ca.utoronto.eil.ontology.service.PublishService;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class PublishServiceTestCase extends TestCase {

	@Autowired @Qualifier("system_fact") 
	private Properties systemProps;
	@Autowired private PublishService service;
	@Autowired private AGraphDao dao;
	
	private static final String RAW_STR_1 = 
			"<http://www.test.org/devGraph2><http://www.test.org/testClass1><" + RDFS.SUBCLASSOF.toString() + "><http://www.test.org/testClass2>";
	private static final String RAW_STR_2 = 
			"<http://www.test.org/devGraph2><http://www.test.org/testInstance1><?p><http://www.test.org/testClass1>";
	private static final String RAW_STR_TOTAL = RAW_STR_1 + "," + RAW_STR_2;
	private static final String MOCK_UUID = "mark4";
	private String strToPub = "";
	
	@Before
	public void prepare() {
		this.strToPub = RAW_STR_TOTAL.replace("?p", systemProps.getProperty("agraph.server.graph.subscribe.predicate"));
	}
	
//	@Test
//	public void testDoPublishWithoutNotification() throws ServiceException, AGraphDataAccessException, ParameterException {
//		service.doPublish(strToPub, MOCK_UUID, false, false);
//		
//		List<String> superClass = this.dao.getImmediateClass(
//				new IRI("<http://www.test.org/testClass1>", MOCK_UUID), 
//				"http://www.test.org/devGraph2", 
//				MOCK_UUID);
//		assertNotNull(superClass);
//		assertEquals(1, superClass.size());
//		assertEquals("http://www.test.org/testClass2", superClass.get(0));
//		
//		List<String> subscribers = this.dao.getSubscriberIRI("http://www.test.org/testClass1", MOCK_UUID);
//		assertNotNull(subscribers);
//		assertEquals(1, subscribers.size());
//		assertEquals("http://www.test.org/testInstance1", subscribers.get(0));
//	}
	
	@After
	public void cleanup() throws ParameterException, AGraphDataAccessException {
		List<Quad> quads = new ArrayList<Quad>();
		quads.add(new Quad(RAW_STR_1, MOCK_UUID));
		quads.add(new Quad(RAW_STR_2.replace("?p", systemProps.getProperty("agraph.server.graph.subscribe.predicate")), MOCK_UUID));
		this.dao.removeQuads(quads, MOCK_UUID, false);
	}
}
