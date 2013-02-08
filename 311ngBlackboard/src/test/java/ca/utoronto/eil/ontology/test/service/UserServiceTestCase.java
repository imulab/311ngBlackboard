package ca.utoronto.eil.ontology.test.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.utoronto.eil.ontology.model.AuthenticationException;
import ca.utoronto.eil.ontology.model.Response;
import ca.utoronto.eil.ontology.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:config/bootstrap.xml"})
public class UserServiceTestCase {

	@Autowired private UserService service;
	
	@Test
	public void testSuccessfulAuth() {
		
		Response response = new Response();
		
		String username = "weinanqiu";
		String password = "19890117";
		boolean auth = false;
		
		try {
			service.doAuthenticate(username, password, response.getUuid());
			auth = true;
		} catch (AuthenticationException e) {
			//e.printStackTrace();
			auth = false;
		}
		
		assertTrue(auth);
	}
	
	@Test
	public void testFailAuthWrongUsername() {
		
		Response response = new Response();
		
		String username = "weinanqiu_wrong";
		String password = "19890117";
		boolean auth = false;
		
		try {
			service.doAuthenticate(username, password, response.getUuid());
			auth = true;
		} catch (AuthenticationException e) {
			//e.printStackTrace();
			auth = false;
			assertEquals("code:E0004", e.getMessage());
		}
		
		assertFalse(auth);
	}
	
	@Test
	public void testFailAuthWrongPassword() {
		Response response = new Response();
		
		String username = "weinanqiu";
		String password = "19890117_wrong";
		boolean auth = false;
		
		try {
			service.doAuthenticate(username, password, response.getUuid());
			auth = true;
		} catch (AuthenticationException e) {
			//e.printStackTrace();
			auth = false;
			assertEquals("code:E0003", e.getMessage());
		}
		
		assertFalse(auth);
	}
	
}
