package ca.utoronto.eil.ontology.test2.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/bootstrap.xml" })
public class PublishTestCase extends TestCase {

	private static final String USERNAME = "david";
	private static final String PASSWORD = "19890117";
	
	private static final String GRAPH = "<http://localhost:8080/311ngBlackboard/graph/testGraph>";
	private static final String SUBJECT = "<http://localhost:8080/311ngBlackboard/instance/testInstance1>";
	private static final String PREDICATE1 = "<http://localhost:8080/311ngBlackboard/predicate/testPredicate1>";
	private static final String PREDICATE2 = "<http://localhost:8080/311ngBlackboard/predicate/testPredicate2>";
	private static final String OBJECT = "<testObject>";
	
	private static final String QUAD1 = GRAPH + SUBJECT + PREDICATE1 + OBJECT;
	private static final String QUAD2 = GRAPH + SUBJECT + PREDICATE2 + OBJECT;
	
	@Test
	public void testTestQueryStr1() {
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("localhost:8080/311ngBlackboard")
				.setPath("/rest/request/publish")
				.setParameter("username", USERNAME)
				.setParameter("password", PASSWORD)
				.setParameter("quads", QUAD1 + "," + QUAD2);
		
		URI uri = null;
		try {
			uri = builder.build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		HttpClient client = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(uri);
		HttpResponse response = null;
		try {
			response = client.execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertNotNull(response);
		assertNotNull(response.getEntity());
		
		try {
			String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			
			System.out.println(body);
			
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

}
