package ca.utoronto.eil.ontology.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.base.RepositoryConnectionBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.franz.agraph.jena.AGGraph;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.jena.AGModel;
import com.franz.agraph.jena.AGQuery;
import com.franz.agraph.jena.AGQueryExecutionFactory;
import com.franz.agraph.jena.AGQueryFactory;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGTupleQuery;
import com.franz.agraph.repository.AGValueFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.model.QueryResult;
import ca.utoronto.eil.ontology.model.QueryResultImpl;

@Repository("AGraphDao")
public class AGraphDao extends AGraphBaseDao {

	private static final Logger logger = Logger.getLogger(AGraphBaseDao.class);
	@Autowired
	@Qualifier("codes")
	private Properties codes;
	@Autowired
	@Qualifier("system_fact")
	private Properties systemProps;
	
	private static final String GET_IMMEDIATE_CLASS= "SELECT ?o { <?s> <?p> ?o }";
	private static final String GET_SUBSCRIBER= "SELECT ?s { ?s <?p> <?o> }";
	
	public AGraphDao() {
	}
	
	/**
	 * Insert a list of quads into the connected repository.
	 * 
	 * @param quads
	 *            quads to be inserted
	 * @param uuid
	 *            uuid of web request
	 * @param test
	 *            whether we want to really commit
	 * 
	 * @throws AGraphDataAccessException
	 *             anything went wrong during processing
	 */
	public void insertQuads(List<Quad> quads, String uuid, Boolean test)
			throws AGraphDataAccessException {
		AGRepositoryConnection conn = super.getConnection(uuid);
		AGValueFactory vf = conn.getRepository().getValueFactory();

		// Have to ensure the connection is writable
		try {
			this.ensureWritable(conn.getRepository(), true, uuid);
		} catch (AGraphDataAccessException e) {
			super.closeConnection(conn, uuid);
			throw e;
		}

		// Prepare
		try {
			conn.setAutoCommit(false);
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] disable autocommit failed.");
			super.closeConnection(conn, uuid);
			throw new AGraphDataAccessException("code:E0020");
		}

		URI graph = null, subject = null, predicate = null, objectAsURI = null;
		Literal objectAsLit = null;
		Boolean objectIsLiteral = null;

		// Add each quad
		for (Quad each : quads) {

			// Create graph URI
			if (each.getGraph() != null && each.getGraph().trim().length() > 0) {
				graph = vf.createURI(each.getGraph());
			}

			// Create subject URI
			subject = vf.createURI(each.getSubject());

			// Create predicate URI
			predicate = vf.createURI(each.getProperty());

			/*
			 * The processing of object depends on its value being URL or
			 * Literal
			 */
			try {
				URL testURL = new URL(each.getObject());
				objectIsLiteral = Boolean.FALSE;
			} catch (MalformedURLException e) {
				objectIsLiteral = Boolean.TRUE;
			}

			// Add to connection
			if (objectIsLiteral) {
				objectAsLit = vf.createLiteral(each.getObject());
			} else {
				objectAsURI = vf.createURI(each.getObject());
			}

			try {
				logger.info("[" + uuid + "] Inserting quad...");
				conn.add(subject, predicate, ((objectIsLiteral) ? (objectAsLit)
						: (objectAsURI)), graph);
			} catch (RepositoryException e) {
				logger.error("[" + uuid + "] insert quad failed", e);
				super.closeConnection(conn, uuid);
				throw new AGraphDataAccessException("msg:[E0021] "
						+ codes.getProperty("E0021")
						+ " [Native Exception Message] " + e.getMessage());
			}
		}

		// Commit
		try {
			if (!test)
				conn.commit();
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] commit failed", e);
			super.closeConnection(conn, uuid);
			throw new AGraphDataAccessException("msg:[E0022] "
					+ codes.getProperty("E0022")
					+ " [Native Exception Message] " + e.getMessage());
		}

		logger.info("[" + uuid + "] change(s) successful "
				+ ((test) ? ("but not") : ("and")) + " committed");

		// Close connection
		super.closeConnection(conn, uuid);
	}

	/**
	 * Removes a list of quads into the connected repository.
	 * 
	 * @param quads
	 *            quads to be removed
	 * @param uuid
	 *            uuid of web request
	 * @param test
	 *            whether we want to really commit
	 * 
	 * @throws AGraphDataAccessException
	 *             anything went wrong during processing
	 */
	public void removeQuads(List<Quad> quads, String uuid, Boolean test)
			throws AGraphDataAccessException {
		AGRepositoryConnection conn = super.getConnection(uuid);
		AGValueFactory vf = conn.getRepository().getValueFactory();

		// Have to ensure the connection is writable
		try {
			this.ensureWritable(conn.getRepository(), true, uuid);
		} catch (AGraphDataAccessException e) {
			super.closeConnection(conn, uuid);
			throw e;
		}

		// Prepare
		try {
			conn.setAutoCommit(false);
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] disable autocommit failed.");
			super.closeConnection(conn, uuid);
			throw new AGraphDataAccessException("code:E0020");
		}

		URI graph = null, subject = null, predicate = null, objectAsURI = null;
		Literal objectAsLit = null;
		Boolean objectIsLiteral = null;

		// Remove each quad
		for (Quad each : quads) {

			// Create graph URI
			if (each.getGraph() != null && each.getGraph().trim().length() > 0) {
				graph = vf.createURI(each.getGraph());
			}

			// Create subject URI
			subject = vf.createURI(each.getSubject());

			// Create predicate URI
			predicate = vf.createURI(each.getProperty());

			/*
			 * The processing of object depends on its value being URL or
			 * Literal
			 */
			try {
				URL testURL = new URL(each.getObject());
				objectIsLiteral = Boolean.FALSE;
			} catch (MalformedURLException e) {
				objectIsLiteral = Boolean.TRUE;
			}

			// Remove from connection
			if (objectIsLiteral) {
				objectAsLit = vf.createLiteral(each.getObject());
			} else {
				objectAsURI = vf.createURI(each.getObject());
			}

			try {
				logger.info("[" + uuid + "] Removing quad...");
				conn.remove(subject, predicate, ((objectIsLiteral) ? (objectAsLit)
						: (objectAsURI)), graph);
			} catch (RepositoryException e) {
				logger.error("[" + uuid + "] remove quad failed", e);
				super.closeConnection(conn, uuid);
				throw new AGraphDataAccessException("msg:[E0023] "
						+ codes.getProperty("E0023")
						+ " [Native Exception Message] " + e.getMessage());
			}
		}

		// Commit
		try {
			if (!test)
				conn.commit();
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] commit failed", e);
			super.closeConnection(conn, uuid);
			throw new AGraphDataAccessException("msg:[E0022] "
					+ codes.getProperty("E0022")
					+ " [Native Exception Message] " + e.getMessage());
		}

		logger.info("[" + uuid + "] change(s) successful "
				+ ((test) ? ("but not") : ("and")) + " committed");

		// Close connection
		super.closeConnection(conn, uuid);
	}

	/**
	 * Returns the immediate class IRI in string format of the subjet IRI
	 * provided. Expects a relationship of <instance
	 * subject><RDFS.SUBCLASSOF><class>.
	 * 
	 * @param instanceSubject
	 *            subject instance
	 * @param graphName
	 *            graph name to search in, if null, will search in default graph
	 * @param uuid
	 *            uuid of web request
	 * 
	 * @return query result in string list
	 * 
	 * @throws AGraphDataAccessException
	 *             anything that went wrong in query
	 */
	public List<String> getImmediateClass(IRI instanceSubject,
			String graphName, String uuid) throws AGraphDataAccessException {
		AGRepositoryConnection conn = super.getConnection(uuid);
		AGGraphMaker graphMaker = new AGGraphMaker(conn);
		AGGraph graph = null;
		List<String> classes = new ArrayList<String>();
		
		// Open Graph
		if (graphName == null || graphName.trim().length() == 0) {
			graph = graphMaker.openGraph(); // Open default
		} else {
			graph = graphMaker.openGraph(graphName); // Open named
		}
		
		AGModel model = new AGModel(graph);
		
		//Construct query
		String query = GET_IMMEDIATE_CLASS
				.replace("?s", instanceSubject.getIri())
				.replace("?p", RDFS.SUBCLASSOF.toString());
		AGQuery sparql = AGQueryFactory.create(query);
		
		//Execute query
		QueryExecution qe = AGQueryExecutionFactory.create(sparql, model);
		ResultSet results = qe.execSelect();
		
		//Convert result
		int numOfResults = 0;
		while (results.hasNext()) {
			QuerySolution result = results.next();
			RDFNode o = result.get("o");
			classes.add(o.toString());
			numOfResults++;
		}
		logger.info("[" + uuid + "] " + numOfResults + " results fetched");
		
		// close connection and return
		super.closeConnection(conn, uuid);
		return classes;
	}

	/**
	 * Returns the subscriber IRIs of the class provided by the className
	 * 
	 * @param className class name
	 * @param uuid uuid of web request
	 * @return a list of subscribers IRI in string format
	 * 
	 * @throws AGraphDataAccessException
	 */
	public List<String> getSubscriberIRI(String graphName, String className, String uuid)
			throws AGraphDataAccessException {
		AGRepositoryConnection conn = super.getConnection(uuid);
		AGGraphMaker graphMaker = new AGGraphMaker(conn);
		List<String> subscribers = new ArrayList<String>();
		String SUBSCRIBES = systemProps
				.getProperty("agraph.server.graph.subscribe.predicate");
		AGGraph graph = graphMaker.openGraph(graphName);
		AGModel model = new AGModel(graph);
		
		//Construct query
		String query = GET_SUBSCRIBER
				.replace("?p", SUBSCRIBES)
				.replace("?o", className);
		AGQuery sparql = AGQueryFactory.create(query);

		//Execute query
		QueryExecution qe = AGQueryExecutionFactory.create(sparql, model);
		ResultSet results = qe.execSelect();
		
		//Convert result
		int numOfResults = 0;
		while (results.hasNext()) {
			QuerySolution result = results.next();
			RDFNode s = result.get("s");
			subscribers.add(s.toString());
			numOfResults++;
		}
		logger.info("[" + uuid + "] " + numOfResults + " results fetched");
		
		// close connection and return
		super.closeConnection(conn, uuid);
		return subscribers;
	}
	
	public QueryResult runQuery(String query, String uuid) throws AGraphDataAccessException {
		QueryResultImpl qResult = new QueryResultImpl();
		
		logger.info("[" + uuid + "] " + "Running query: " + query);
		
		AGRepositoryConnection conn = super.getConnection(uuid);
		AGTupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		
		try {
			TupleQueryResult result = tupleQuery.evaluate();
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				qResult.addResult(bindingSet);
			}
			qResult.setQueryExecutionStatus(true);
			logger.info("[" + uuid + "] " + "Got and converted " + qResult.getResultCount() + " results");
		} catch (QueryEvaluationException e) {
			logger.error("[" + uuid + "] " + "Query error: " + e.getMessage(), e);
			qResult.setQueryExecutionStatus(false);
			qResult.setExceptionMessage(e.getMessage());
		}
		
		// close connection and return
		super.closeConnection(conn, uuid);
		return qResult;
	}
}
