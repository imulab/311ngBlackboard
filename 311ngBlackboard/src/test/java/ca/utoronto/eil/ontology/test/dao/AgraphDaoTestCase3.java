package ca.utoronto.eil.ontology.test.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class AgraphDaoTestCase3 extends TestCase {

	private static final String RAW_STR_1 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testClass1>"
			+ "<http://www.test.org/testPredicatex>" + "<object>";
	private static final String RAW_STR_2 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance1>" + "<"
			+ "http://ontology.eil.utoronto.ca/311ngBlackboard#subscribes" + ">"
			+ "<http://www.test.org/testClass1>";
	private static final String RAW_STR_3 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance2>" + "<"
			+ "http://ontology.eil.utoronto.ca/311ngBlackboard#subscribes" + ">"
			+ "<http://www.test.org/testClass1>";
	private static final String RAW_STR_4 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testClass2>"
			+ "<http://www.test.org/testPredicatey>" + "<object>";
	private static final String RAW_STR_5 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance3>" + "<"
			+ "http://ontology.eil.utoronto.ca/311ngBlackboard#subscribes" + ">"
			+ "<http://www.test.org/testClass2>";
	private static final String RAW_STR_6 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance4>" + "<"
			+ "http://ontology.eil.utoronto.ca/311ngBlackboard#subscribes" + ">"
			+ "<http://www.test.org/testClass2>";
	private static final String MOCK_UUID = "mark2";

	@Autowired
	private AGraphDao dao;
	private List<Quad> quads;

	@Before
	public void prepare() throws ParameterException, AGraphDataAccessException {
		this.quads = new ArrayList<Quad>();
		this.quads.add(new Quad(RAW_STR_1, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_2, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_3, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_4, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_5, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_6, MOCK_UUID));

		dao.insertQuads(quads, MOCK_UUID, false);
	}
	
//	@Test
//	public void testGetSubscribers() throws ParameterException,
//			AGraphDataAccessException {
//		
//		String class1 = "http://www.test.org/testClass1";
//		String class2 = "http://www.test.org/testClass2";
//		String instance1 = "http://www.test.org/testInstance1";
//		String instance2 = "http://www.test.org/testInstance2";
//		String instance3 = "http://www.test.org/testInstance3";
//		String instance4 = "http://www.test.org/testInstance4";
//		List<String> subscribers = null;
//
//		// Test1
//		subscribers = null;
//		subscribers = dao.getSubscriberIRI(class1, MOCK_UUID);
//		assertNotNull(subscribers);
//		assertEquals(2, subscribers.size());
//		assertEquals(instance1, subscribers.get(0));
//		assertEquals(instance2, subscribers.get(1));
//
//		// Test2
//		subscribers = null;
//		subscribers = dao.getSubscriberIRI(class2, MOCK_UUID);
//		assertNotNull(subscribers);
//		assertEquals(2, subscribers.size());
//		assertEquals(instance3, subscribers.get(0));
//		assertEquals(instance4, subscribers.get(1));
//	}

	@After
	public void cleanup() throws AGraphDataAccessException {
		dao.removeQuads(quads, MOCK_UUID, false);
	}
}
