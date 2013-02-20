package ca.utoronto.eil.ontology.test.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;

import junit.framework.TestCase;

/**
 * Since we didn't implement standard get methods in DAO, we will have to
 * log into web console to confirm result.
 * However, since we are inserting 6 quads and them removing them, the
 * repository should stay the same.
 * 
 * @author daividimu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/bootstrap.xml"})
public class AgraphDaoTestCase extends TestCase {

	private static final String RAW_STR_1 = "<http://www.test.org/devGraph><http://www.test.org/testSubject1><http://www.test.org/testPredicatex><object>";
	private static final String RAW_STR_2 = "<http://www.test.org/devGraph><http://www.test.org/testSubject1><http://www.test.org/testPredicatey><object>";
	private static final String RAW_STR_3 = "<http://www.test.org/devGraph><http://www.test.org/testSubject2><http://www.test.org/testPredicatex><object>";
	private static final String RAW_STR_4 = "<http://www.test.org/devGraph><http://www.test.org/testSubject2><http://www.test.org/testPredicatey><object>";
	private static final String RAW_STR_5 = "<http://www.test.org/devGraph><http://www.test.org/testSubject2><http://www.test.org/testPredicatez><object>";
	private static final String RAW_STR_6 = "<http://www.test.org/devGraph><http://www.test.org/testSubject3><http://www.test.org/testPredicatex><object>";
	private static final String MOCK_UUID = "test";
	
	@Autowired private AGraphDao dao;
	private List<Quad> quads;
	
	@Before
	public void prepare() throws ParameterException {
		this.quads = new ArrayList<Quad>();
		this.quads.add(new Quad(RAW_STR_1, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_2, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_3, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_4, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_5, MOCK_UUID));
		this.quads.add(new Quad(RAW_STR_6, MOCK_UUID));
	}
	
	@Test
	public void testInsertQuads() throws AGraphDataAccessException {
		assertNotNull(dao);
		dao.insertQuads(quads, MOCK_UUID, false);
		//Since we didn't implement standard get method, please log into web console to confirm result
	}
	
	@Test
	public void testRemoveQuads() throws AGraphDataAccessException {
		assertNotNull(dao);
		dao.removeQuads(quads, MOCK_UUID, false);
		//Since we didn't implement standard get method, please log into web console to confirm result
	}
}
