package ca.utoronto.eil.ontology.test.agraph;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGValueFactory;

import ca.utoronto.eil.ontology.dao.AGraphBaseDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class AGraphInsertionTestCase extends TestCase {

	@Autowired
	@Qualifier("AGraphBaseDao")
	private AGraphBaseDao baseDao;

	@Test
	public void testInsertion() throws AGraphDataAccessException {
		String graph = "http://test.org/graph/devGraph";
		String subject = "http://test.org";
		String property = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
		String object = "http://test.org/link";

		AGRepositoryConnection conn = baseDao.getConnection("test");
		AGValueFactory vf = conn.getRepository().getValueFactory();

		URI subjectURI = vf.createURI(subject);
		URI propertyURI = vf.createURI(property);
		URI objectURI = vf.createURI(object);
		URI graphURI = vf.createURI(graph);
		// Resource graphLit = new Resource("");

		try {
			conn.setAutoCommit(false);
			conn.add(subjectURI, propertyURI, objectURI, graphURI);
			conn.commit();
			conn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
