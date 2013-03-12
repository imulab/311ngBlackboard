package ca.utoronto.eil.ontology.test.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.model.GraphSubjectPair;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Response;
import ca.utoronto.eil.ontology.model.ResponseImpl;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:config/bootstrap.xml"})
public class GraphSubjectPairTestCase extends TestCase {

	@Test
	public void testValidModel() {
		String rawString = new String("<a graph><http://www.test.org/notification>");
		boolean valid = false;
		GraphSubjectPair model = null;
		
		try {
			model = testModel(rawString);
			valid = true;
		} catch (ParameterException pe) {
			valid = false;
		}
		
		assertTrue(valid);
		
		assertNotNull(model);
		assertEquals("a graph", model.getGraph());
		assertEquals("http://www.test.org/notification", model.getSubject());
	}
	
	@Test
	public void testInvalidModel() {
		String[] rawStrings = new String[] {
				"<a graph><http://www.test.org/notification",
				"a graph><http://www.test.org/notification>",
				"<a graph>,<http://www.test.org/notification>",
				"<a graph><http://www.test.org/notification><something else>"
		};
		
		for (String each : rawStrings) {
			boolean valid = false;
			
			try {
				testModel(each);
				valid = true;
			} catch (ParameterException pe) {
				valid = false;
			}
			
			assertFalse(valid);
		}
	}
	
	private GraphSubjectPair testModel(String rawString) throws ParameterException {
		Response response = new ResponseImpl();
		GraphSubjectPair model = new GraphSubjectPair(rawString, response.getUuid());
		return model;
	}
}
