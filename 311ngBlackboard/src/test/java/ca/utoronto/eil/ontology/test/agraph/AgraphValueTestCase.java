package ca.utoronto.eil.ontology.test.agraph;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.dao.AGraphBaseDao;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class AgraphValueTestCase extends TestCase {

	@Autowired
	@Qualifier("AGraphBaseDao")
	private AGraphBaseDao baseDao;

	@Test
	public void testRDFType() throws URISyntaxException {
		String rdfType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
		assertEquals(rdfType, RDF.TYPE.toString());
	}

	@Test
	public void testParseLiteralIntoURL() {
		String literal = "aValue";
		boolean valid = true;

		try {
			URL uri = new URL(literal);
		} catch (MalformedURLException e) {
			assertFalse(valid);
		}
	}
}
