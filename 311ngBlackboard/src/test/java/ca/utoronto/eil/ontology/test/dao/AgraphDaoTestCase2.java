package ca.utoronto.eil.ontology.test.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class AgraphDaoTestCase2 extends TestCase {

	private static final String RAW_STR_1 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testClass1>"
			+ "<http://www.test.org/testPredicatex>" + "<object>";
	private static final String RAW_STR_2 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance1>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test.org/testClass1>";
	private static final String RAW_STR_3 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance2>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test.org/testClass1>";
	private static final String RAW_STR_4 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testClass2>"
			+ "<http://www.test.org/testPredicatey>" + "<object>";
	private static final String RAW_STR_5 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance3>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test.org/testClass2>";
	private static final String RAW_STR_6 = "<http://www.test.org/devGraph>"
			+ "<http://www.test.org/testInstance4>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test.org/testClass2>";
	private static final String MOCK_UUID = "mark";

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
	
	@Test
	public void testGetImmediateSuperClass() throws ParameterException,
			AGraphDataAccessException {
		IRI instanceIRI1 = new IRI("<http://www.test.org/testInstance1>",
				MOCK_UUID);
		IRI instanceIRI2 = new IRI("<http://www.test.org/testInstance2>",
				MOCK_UUID);
		IRI instanceIRI3 = new IRI("<http://www.test.org/testInstance3>",
				MOCK_UUID);
		IRI instanceIRI4 = new IRI("<http://www.test.org/testInstance4>",
				MOCK_UUID);
		String graphName = "http://www.test.org/devGraph";
		String class1 = "http://www.test.org/testClass1";
		String class2 = "http://www.test.org/testClass2";
		List<String> immediateClass = null;

		// Test1
		immediateClass = null;
		immediateClass = dao.getImmediateClass(instanceIRI1, graphName,
				MOCK_UUID);
		assertNotNull(immediateClass);
		assertEquals(1, immediateClass.size());
		assertEquals(class1, immediateClass.get(0));

		// Test2
		immediateClass = null;
		immediateClass = dao.getImmediateClass(instanceIRI2, graphName,
				MOCK_UUID);
		assertNotNull(immediateClass);
		assertEquals(1, immediateClass.size());
		assertEquals(class1, immediateClass.get(0));

		// Test3
		immediateClass = null;
		immediateClass = dao.getImmediateClass(instanceIRI3, graphName,
				MOCK_UUID);
		assertNotNull(immediateClass);
		assertEquals(1, immediateClass.size());
		assertEquals(class2, immediateClass.get(0));

		// Test4
		immediateClass = null;
		immediateClass = dao.getImmediateClass(instanceIRI4, graphName,
				MOCK_UUID);
		assertNotNull(immediateClass);
		assertEquals(1, immediateClass.size());
		assertEquals(class2, immediateClass.get(0));
	}

	@After
	public void cleanup() throws AGraphDataAccessException {
		dao.removeQuads(quads, MOCK_UUID, false);
	}
}
