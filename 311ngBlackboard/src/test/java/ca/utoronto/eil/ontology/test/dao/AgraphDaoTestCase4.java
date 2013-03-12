package ca.utoronto.eil.ontology.test.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class AgraphDaoTestCase4 extends TestCase {

	@Autowired
	private AGraphDao dao;
	
	@Test
	public void testGetImmediateClass() throws ParameterException, AGraphDataAccessException {
		IRI iriInstance = new IRI("<http://www.test.org/testInstance1>", "mockuuid");
		List<String> classes = dao.getImmediateClass(iriInstance, null, "mockuuid");
		assertNotNull(classes);
		assertTrue(classes.size() > 0);
		System.out.println(classes.size());
	}
}
