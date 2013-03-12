package ca.utoronto.eil.ontology.test.agraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.franz.agraph.jena.AGGraph;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.jena.AGModel;
import com.franz.agraph.jena.AGQuery;
import com.franz.agraph.jena.AGQueryExecutionFactory;
import com.franz.agraph.jena.AGQueryFactory;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGValueFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import ca.utoronto.eil.ontology.dao.AGraphBaseDao;
import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/bootstrap.xml"})
public class AgraphListStmtTestCase extends TestCase {

	private static final String RAW_STR_1 = "<http://www.test2.org/devGraph>"
			+ "<http://www.test2.org/testClass1>"
			+ "<http://www.test2.org/testPredicatex>" + "<object>";
	private static final String RAW_STR_2 = "<http://www.test2.org/devGraph>"
			+ "<http://www.test2.org/testInstance1>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test2.org/testClass1>";
	private static final String RAW_STR_3 = "<http://www.test2.org/devGraph>"
			+ "<http://www.test2.org/testInstance2>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test2.org/testClass1>";
	private static final String RAW_STR_4 = "<http://www.test2.org/devGraph>"
			+ "<http://www.test2.org/testClass2>"
			+ "<http://www.test2.org/testPredicatey>" + "<object>";
	private static final String RAW_STR_5 = "<http://www.test2.org/devGraph>"
			+ "<http://www.test2.org/testInstance3>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test2.org/testClass2>";
	private static final String RAW_STR_6 = "<http://www.test2.org/devGraph>"
			+ "<http://www.test2.org/testInstance4>" + "<"
			+ RDFS.SUBCLASSOF.toString() + ">"
			+ "<http://www.test2.org/testClass2>";
	private static final String MOCK_UUID = "mark";

	@Autowired @Qualifier("AGraphDao")
	private AGraphDao dao;
	@Autowired @Qualifier("AGraphBaseDao")
	private AGraphBaseDao baseDao;
	private List<Quad> quads;

//	@Before
//	public void prepare() throws ParameterException, AGraphDataAccessException {
//		this.quads = new ArrayList<Quad>();
//		this.quads.add(new Quad(RAW_STR_1, MOCK_UUID));
//		this.quads.add(new Quad(RAW_STR_2, MOCK_UUID));
//		this.quads.add(new Quad(RAW_STR_3, MOCK_UUID));
//		this.quads.add(new Quad(RAW_STR_4, MOCK_UUID));
//		this.quads.add(new Quad(RAW_STR_5, MOCK_UUID));
//		this.quads.add(new Quad(RAW_STR_6, MOCK_UUID));
//
//		dao.insertQuads(quads, MOCK_UUID, false);
//	}
	
	@Test
	public void testListStatements() throws AGraphDataAccessException, RepositoryException {
		AGRepositoryConnection conn = baseDao.getConnection(MOCK_UUID);
		AGGraphMaker graphMaker = new AGGraphMaker(conn);
		
		System.out.println(graphMaker.getRepositoryConnection().getRepository().getSpec());
		
		AGGraph graph = graphMaker.openGraph();
		AGModel model = new AGModel(graph);
		String queryStr = "SELECT ?o WHERE {GRAPH <http://www.test2.org/devGraph> { <http://www.test2.org/testInstance2> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o }}";
		String queryStr2 = "SELECT ?o WHERE { <http://www.test2.org/testInstance2> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o }";
		String queryStr3 = "SELECT ?g ?s ?p ?o { GRAPH ?g { ?s ?p ?o } }";
		//String queryStr = "SELECT ?s ?p ?o {?s ?p ?o}";
		AGQuery sparql = AGQueryFactory.create(queryStr3);
		
		//StmtIterator itr = model.listStatements();
		
//		while(itr.hasNext()) {
//			System.out.println(itr.next());
//		}
		
		QueryExecution qe = AGQueryExecutionFactory.create(sparql, model);
		ResultSet results = qe.execSelect();
		
		System.out.println(results.hasNext());
		while (results.hasNext()) {
			QuerySolution result = results.next();
			RDFNode g = result.get("g");
			RDFNode p = result.get("p");
			RDFNode s = result.get("s");
			RDFNode o = result.get("o");
			System.out.println("<" + g + "><" + s + "><" + p + "><" + o + ">");
		}
	}
	
//	@After
//	public void cleanup() throws AGraphDataAccessException {
//		dao.removeQuads(quads, MOCK_UUID, false);
//	}
}
