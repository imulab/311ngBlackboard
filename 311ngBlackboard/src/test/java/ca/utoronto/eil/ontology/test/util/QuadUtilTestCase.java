package ca.utoronto.eil.ontology.test.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import ca.utoronto.eil.ontology.model.ParameterException;
import ca.utoronto.eil.ontology.model.Quad;
import ca.utoronto.eil.ontology.util.QuadUtil;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/bootstrap.xml"})
public class QuadUtilTestCase extends TestCase {

	private List<Quad> quads;
	
	private static final String RAW_STR_1 = "<graph><subject1><predicatex><object>";
	private static final String RAW_STR_2 = "<graph><subject1><predicatex><object>";
	private static final String RAW_STR_3 = "<graph><subject2><predicatex><object>";
	private static final String RAW_STR_4 = "<graph><subject2><predicatex><object>";
	private static final String RAW_STR_5 = "<graph><subject2><predicatex><object>";
	private static final String RAW_STR_6 = "<graph><subject3><predicatex><object>";
	private static final String MOCK_UUID = "test";
	
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
	public void testGroupBySubject() {
		assertNotNull(this.quads);
		
		List<List<Quad>> converted = QuadUtil.groupBySubject(quads);
		
		assertNotNull(converted);
		assertEquals(3, converted.size());
	}
	
	@Test
	public void testConvertString() {
		assertNotNull(this.quads);
		
		String converted = QuadUtil.convertToString(quads);
		
		assertNotNull(converted);
		assertTrue(converted.startsWith("<"));
		assertTrue(converted.endsWith(">"));
		
		assertEquals(5, StringUtils.countOccurrencesOf(converted, ","));
	}
}
