package ca.utoronto.eil.ontology.test.agraph;

import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;

import junit.framework.TestCase;

public class AgraphConnectionTestCase extends TestCase {

	private static final String SERVER_URL = "http://10.168.122.113:10035";
	private static final String USERNAME = "msf";
	private static final String PASSWORD = "ykramAgraph";
	
	@Test
	public void testServerConnection() {
		AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
		assertNotNull(server);
	}
	
	@Test
	public void testRepositoryConnection() {
		AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
		assertNotNull(server);
		
		AGRepository devRepo = null;
		try {
			devRepo = server.getRootCatalog().openRepository("dev");
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		assertNotNull(devRepo);
		
		AGRepositoryConnection conn = null;
		try {
			conn = devRepo.getConnection();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		assertNotNull(conn);
		
		try {
			conn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
