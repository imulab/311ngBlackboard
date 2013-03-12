package ca.utoronto.eil.ontology.test.dao;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGTupleQuery;
import com.google.gson.Gson;

import ca.utoronto.eil.ontology.dao.AGraphBaseDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class AgraphDaoTestCase5 extends TestCase {

	@Autowired @Qualifier("AGraphBaseDao")
	private AGraphBaseDao dao;
	@Autowired
	private Gson gson;
	
	@Test
	public void testSerializable() throws AGraphDataAccessException, QueryEvaluationException {
		AGRepositoryConnection conn = dao.getConnection("test");
		String queryString = "SELECT ?s ?p ?o  WHERE {?s ?p ?o}";
		
		AGTupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            
            System.out.println("Binding Set: " + gson.toJson(bindingSet));
            
            //Value s = bindingSet.getValue("s");
            //Value p = bindingSet.getValue("p");
            //Value o = bindingSet.getValue("o");
            //System.out.format("%s %s %s\n", s, p, o);
        }
		
		String jsonString = gson.toJson(result);
		
		System.out.println(jsonString);
	}

}
