package ca.utoronto.eil.ontology.test.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Response;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:config/bootstrap.xml"})
public class IRITestCase extends TestCase {

	@Test
	public void testValidModel() {
		String rawString = new String("<http://www.google.com>");
		boolean valid = false;
		IRI model = null;
		
		try {
			model = testModel(rawString);
			valid = true;
		} catch (ParameterException pe) {
			valid = false;
		}
		
		assertTrue(valid);
		
		assertNotNull(model);
		assertEquals("http://www.google.com", model.getIri());
	}
	
	@Test
	public void testInvalidModel() {
		String[] rawStrings = new String[] {
				"http:www.google.com",
				"<http:www.google.com",
				"http:www.google.com>",
				">http:www.google.com<"
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
	
	private IRI testModel(String rawString) throws ParameterException {
		Response response = new Response();
		IRI model = new IRI(rawString, response.getUuid());
		return model;
	}
}
