package ca.utoronto.eil.ontology.test.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.model.Response;
import ca.utoronto.eil.ontology.model.ResponseImpl;

import com.google.gson.Gson;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class ResponseModelTestCase extends TestCase {

	@Autowired
	private Gson gson;
	
	@Test
	public void testConvertJson() {
		ResponseImpl response = new ResponseImpl();
		response.setState("fail");
		response.addObject("op.msg", "hello world");
		
		String jsonStr = gson.toJson(response);
		
		Response response2 = gson.fromJson(jsonStr, ResponseImpl.class);
		
		assertEquals("fail", response.getState());
		assertEquals("hello world", (String) response.getObject("op.msg"));
	}
}
