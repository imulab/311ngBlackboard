package ca.utoronto.eil.ontology.test.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.Response;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:config/bootstrap.xml"})
public class QuadTestCase extends TestCase {

	@Test
	public void testValidModel() {
		String rawString = new String("<a graph><http://www.test.org/notification><a property><http://www.test.org/sample#value>");
		boolean valid = false;
		Quad model = null;
		
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
		assertEquals("a property", model.getProperty());
		assertEquals("http://www.test.org/sample#value", model.getObject());
	}
	
	@Test
	public void testInvalidModel() {
		String[] rawStrings = new String[] {
				"<a graph><http://www.test.org/notification>$%<a property><http://www.test.org/sample#value>",
				"a graph><http://www.test.org/notification><a property><http://www.test.org/sample#value>",
				"<a graph><http://www.test.org/notification><a property><http://www.test.org/sample#value",
				"<a graph><http://www.test.org/notification><a property><http://www.test.org/sample#value><extra>",
				"<a graph><http://www.test.org/notification><a property>"
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
	
	private Quad testModel(String rawString) throws ParameterException {
		Response response = new Response();
		Quad model = new Quad(rawString, response.getUuid());
		return model;
	}
}
