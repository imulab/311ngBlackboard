package ca.utoronto.eil.ontology.dao;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.utoronto.eil.ontology.model.AGraphDataAccessException;

import com.franz.agraph.repository.AGAbstractRepository;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;

@Repository("AGraphBaseDao")
public class AGraphBaseDao {

	private static final Logger logger = Logger.getLogger(AGraphBaseDao.class);
	@Autowired
	@Qualifier("codes")
	private Properties codes;
	@Autowired
	@Qualifier("system_fact")
	private Properties systemProps;

	public AGraphBaseDao() {
	}

	/**
	 * Ensure the repository is writable
	 * 
	 * @param repo
	 * @throws AGraphDataAccessException
	 */
	protected void ensureWritable(AGAbstractRepository repo, Boolean enforced,
			String uuid) throws AGraphDataAccessException {
		try {
			if (!repo.isWritable() && enforced) {
				logger.error("[" + uuid + "] Repository is NOT writable.");
				throw new AGraphDataAccessException("code:E0017");
			} else {
				logger.debug("[" + uuid + "] Repository "
						+ ((repo.isWritable()) ? ("is") : ("is NOT"))
						+ " writable.");
			}
		} catch (RepositoryException e) {
			logger.error("[" + uuid
					+ "] Error occurred while accessing repository", e);
			throw new AGraphDataAccessException("code:E0018");
		}
	}

	/**
	 * Closes a connection
	 * 
	 * @param connection
	 * @param uuid
	 * 
	 * @throws AGraphDataAccessException
	 */
	protected void closeConnection(AGRepositoryConnection connection,
			String uuid) throws AGraphDataAccessException {
		// Close connection
		try {
			connection.close();
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] Error when closing repo connection.",
					e);
			throw new AGraphDataAccessException("code:E0019");
		}
	}

	/**
	 * Establish a connection to the repository specified by the parameters in
	 * the system fact configuration under namespace 'server'
	 * 
	 * @param uuid
	 *            uuid of web request
	 * 
	 * @return An agraph repository connection
	 * 
	 * @throws AGraphDataAccessException
	 *             anything that went wrong during connection attempt
	 */
	public AGRepositoryConnection getConnection(String uuid)
			throws AGraphDataAccessException {
		AGServer server = new AGServer(
				systemProps.getProperty("agraph.server.url"),
				systemProps.getProperty("agraph.server.username"),
				systemProps.getProperty("agraph.server.password"));

		logger.debug("[" + uuid + "] Connecting to "
				+ systemProps.getProperty("agraph.server.url")
				+ " using account "
				+ systemProps.getProperty("agraph.server.username"));

		/*
		 * We choose to fallback to root catalog, if no catalog is defined in
		 * application configuration
		 */
		AGRepository repo = null;
		String catalogName = systemProps.getProperty("agraph.server.catalog");
		try {
			if (catalogName == null || catalogName.trim().length() == 0) {
				logger.debug("[" + uuid + "] Getting repository "
						+ systemProps.getProperty("agraph.server.repo")
						+ " in root catalog");
				repo = server.getRootCatalog().openRepository(
						systemProps.getProperty("agraph.server.repo"));
			} else {
				logger.debug("[" + uuid + "] Getting repository "
						+ systemProps.getProperty("agraph.server.repo")
						+ " in catalog " + catalogName);
				repo = server.getCatalog("catalogName").openRepository(
						systemProps.getProperty("agraph.server.repo"));
			}

		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] Get repository failed", e);
			throw new AGraphDataAccessException("code:E0015", e);
		}

		AGRepositoryConnection conn = null;
		try {
			logger.debug("[" + uuid + "] Establishing connection to repository");
			conn = repo.getConnection();
		} catch (RepositoryException e) {
			logger.error("[" + uuid + "] Connect repository failed.", e);
			throw new AGraphDataAccessException("code:E0016", e);
		}

		logger.debug("[" + uuid + "] Successfully connected to repository");
		return conn;
	}
}
