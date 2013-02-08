package ca.utoronto.eil.ontology.test.misc;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:config/bootstrap.xml"})
public class UtilAutowiredTestCase extends TestCase {

	@Autowired @Qualifier("system_fact") private Properties systemProps;
	
	@Test
	public void testSystemProp() {
		assertNotNull(systemProps);
		assertEquals("user_cache", systemProps.getProperty("cache.name"));
	}
}
