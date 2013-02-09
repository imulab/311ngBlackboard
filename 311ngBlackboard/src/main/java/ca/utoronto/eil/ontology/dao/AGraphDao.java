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
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.base.RepositoryConnectionBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGValueFactory;

import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.IRI;
import ca.utoronto.eil.ontology.model.Quad;

@Repository("AGraphDao")
public class AGraphDao extends AGraphBaseDao {

	private static final Logger logger = Logger.getLogger(AGraphBaseDao.class);
	@Autowired
	@Qualifier("codes")
	private Properties codes;
	@Autowired
	@Qualifier("system_fact")
	private Properties systemProps;

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
				conn.add(subject, predicate, ((objectIsLiteral) ? (objectAsLit)
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
		AGValueFactory vf = conn.getRepository().getValueFactory();
		List<String> classes = new ArrayList<String>();

		// Prepare
		URI subject = vf.createURI(instanceSubject.getIri());
		URI graph = null;

		if (graphName != null && graphName.trim().length() > 0) {
			graph = vf.createURI(graphName);
		}

		// Query
		try {
			RepositoryResult<Statement> results = conn.getStatements(subject,
					RDFS.SUBCLASSOF, null, false, graph);
			logger.info("[" + uuid + "] " + results.asList().size()
					+ " query results fetched");
			for (Statement st : results.asList()) {
				classes.add(st.getObject().toString());
			}
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] error getting immediate class", e);
			super.closeConnection(conn, uuid);
			throw new AGraphDataAccessException("msg:[E0024] "
					+ codes.getProperty("E0024")
					+ " [Native Exception Message] " + e.getMessage());
		}

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
	public List<String> getSubscriberIRI(String className, String uuid)
			throws AGraphDataAccessException {
		AGRepositoryConnection conn = super.getConnection(uuid);
		AGValueFactory vf = conn.getRepository().getValueFactory();
		List<String> subscribers = new ArrayList<String>();

		// Prepare
		URI object = vf.createURI(className);
		URI predicate = vf.createURI(systemProps
				.getProperty("agraph.server.graph.subscribe.predicate"));
		URI graph = vf.createURI(systemProps
				.getProperty("agraph.server.graph.subscribe"));

		// Query
		try {
			RepositoryResult<Statement> results = conn.getStatements(null,
					predicate, object, false, graph);
			logger.info("[" + uuid + "] " + results.asList().size()
					+ " query results fetched");
			for (Statement st : results.asList()) {
				subscribers.add(st.getSubject().toString());
			}
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] error getting subsriber IRI", e);
			super.closeConnection(conn, uuid);
			throw new AGraphDataAccessException("msg:[E0025] "
					+ codes.getProperty("E0025")
					+ " [Native Exception Message] " + e.getMessage());
		}

		// close connection and return
		super.closeConnection(conn, uuid);
		return subscribers;
	}
}
