package ca.utoronto.eil.ontology.test.agraph;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
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

import ca.utoronto.eil.ontology.dao.AGraphBaseDao;
import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/bootstrap.xml"})
public class BindsetExtractionTestCase extends TestCase {

	private static final String QUERY = "SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }";
	private static final String QUERY2 = "SELECT ?s ?p ?o {?s ?p ?o}";
	
	@Autowired @Qualifier("AGraphBaseDao") private AGraphBaseDao dao;
	
	@Test
	public void testGetBindingSet() throws AGraphDataAccessException {
		AGRepositoryConnection conn = dao.getConnection("test");
		AGTupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, QUERY2);
		
		try {
			TupleQueryResult result = tupleQuery.evaluate();
			List<String> bindingNames = result.getBindingNames();
			
			for (String eachName : bindingNames) {
				System.out.println("BindingNames: " + eachName);
			}
			
			while (result.hasNext()) {
				BindingSet eachSet = result.next();
				
				for (String eachName : bindingNames) {
					System.out.println(eachName + " : " + eachSet.getBinding(eachName).getValue().stringValue());
					System.out.println("");
				}
			}
			
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
