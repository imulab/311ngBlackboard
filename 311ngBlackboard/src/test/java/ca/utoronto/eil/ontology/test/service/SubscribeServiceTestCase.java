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

import ca.utoronto.eil.ontology.dao.AGraphBaseDao;
import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.ServiceException;
import ca.utoronto.eil.ontology.service.SubscribeService;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class SubscribeServiceTestCase extends TestCase {

	@Autowired private SubscribeService service;
	
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
	String subClassInstance1 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testSubscribeInstance1>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test.org/testClass1>";
	String subClassInstance2 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testSubscribeInstance2>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test.org/testClass2>";
	
	@Autowired
	private AGraphDao dao;
	@Autowired @Qualifier("system_fact") 
	private Properties systemProps;
	private List<Quad> quads;
	private List<Quad> subClassQuads;
	
	@Before
	public void prepare() throws ParameterException, AGraphDataAccessException {
		this.quads = new ArrayList<Quad>();
		this.quads.add(new Quad(RAW_STR_1, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_2, MOCK_UUID));
		this.dao.insertQuads(quads, MOCK_UUID, false);
		
		this.subClassQuads = new ArrayList<Quad>();
		this.subClassQuads.add(new Quad(subClassInstance1, MOCK_UUID));
		this.subClassQuads.add(new Quad(subClassInstance2, MOCK_UUID));
	}
	
//	@Test
//	public void testDoSubscribe() throws ServiceException, AGraphDataAccessException {
//		service.doSubscribe("<" + INSTANCE_1 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
//		service.doSubscribe("<" + INSTANCE_2 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
//		service.doSubscribe("<" + INSTANCE_3 + ">", "<http://www.test.org/testClass2>", MOCK_UUID, false);
//		
//		List<String> superClasses1 = dao.getSubscriberIRI("http://www.test.org/testClass1", MOCK_UUID);
//		assertNotNull(superClasses1);
//		assertEquals(2, superClasses1.size());
//		
//		List<String> superClasses2 = dao.getSubscriberIRI("http://www.test.org/testClass2", MOCK_UUID);
//		assertNotNull(superClasses2);
//		assertEquals(1, superClasses2.size());
//	}
	
	@Test
	public void testGetImmediateSubscribers() throws ServiceException, AGraphDataAccessException {
		service.doSubscribe("<" + INSTANCE_1 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
		service.doSubscribe("<" + INSTANCE_2 + ">", "<http://www.test.org/testClass1>", MOCK_UUID, false);
		service.doSubscribe("<" + INSTANCE_3 + ">", "<http://www.test.org/testClass2>", MOCK_UUID, false);
		
		this.dao.insertQuads(subClassQuads, MOCK_UUID, false);
		
		String graphName = systemProps.getProperty("agraph.server.graph.subscribe");
		
		List<String> subscribers = service.getImmediateSubscribers(graphName, "http://www.test.org/testSubscribeInstance1", MOCK_UUID);
		assertNotNull(subscribers);
		assertEquals(2, subscribers.size());
		
		subscribers = service.getImmediateSubscribers(graphName, "http://www.test.org/testSubscribeInstance2", MOCK_UUID);
		assertNotNull(subscribers);
		assertEquals(1, subscribers.size());
		
		//local clean up
		this.dao.removeQuads(subClassQuads, MOCK_UUID, false);
	}
	
	@After
	public void cleanup() throws AGraphDataAccessException, ParameterException {
		String rawString1 = "<" + systemProps.getProperty("agraph.server.graph.subscribe") + ">" 
				+ "<" + INSTANCE_1 + ">"
				+ "<" + systemProps.getProperty("agraph.server.graph.subscribe.predicate") + ">"
				+ "<http://www.test.org/testClass1>";
		String rawString2 = "<" + systemProps.getProperty("agraph.server.graph.subscribe") + ">" 
				+ "<" + INSTANCE_2 + ">"
				+ "<" + systemProps.getProperty("agraph.server.graph.subscribe.predicate") + ">"
				+ "<http://www.test.org/testClass1>";
		String rawString3 = "<" + systemProps.getProperty("agraph.server.graph.subscribe") + ">" 
				+ "<" + INSTANCE_3 + ">"
				+ "<" + systemProps.getProperty("agraph.server.graph.subscribe.predicate") + ">"
				+ "<http://www.test.org/testClass2>";
		List<Quad> quadsToRemove = new ArrayList<Quad>();
		quadsToRemove.add(new Quad(rawString1, MOCK_UUID));
		quadsToRemove.add(new Quad(rawString2, MOCK_UUID));
		quadsToRemove.add(new Quad(rawString3, MOCK_UUID));
		this.dao.removeQuads(quads, MOCK_UUID, false);
		this.dao.removeQuads(quadsToRemove, MOCK_UUID, false);
		
	}
}
