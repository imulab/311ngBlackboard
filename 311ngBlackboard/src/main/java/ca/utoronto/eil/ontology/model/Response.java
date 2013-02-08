package ca.utoronto.eil.ontology.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.springframework.stereotype.Component;

public class Response {

	private String state;
	private String uuid;
	private Map<String, Object> objectMap;
	
	public Response() {
		this.state = new String();
		this.uuid = UUID.randomUUID().toString();
		this.objectMap = new HashMap<String, Object>();
	}

	public String getState() {
		return state;
	}

	public String getUuid() {
		return uuid;
	}

	public Response setState(String state) {
		this.state = state;
		return this;
	}
	
	public Response addObject(String key, Object object) {
		this.objectMap.put(key, object);
		return this;
	}
	
	public Response removeObject(String key) {
		this.objectMap.remove(key);
		return this;
	}
	
	public void resolveException(Exception e, Properties codes) {
		this.setState("fail");
		
		String exceptionMsg = e.getMessage();
		if (exceptionMsg.startsWith("msg:")) {
			this.addObject("op.msg", exceptionMsg.substring("msg:".length()));
		} else if (exceptionMsg.startsWith("code:")) {
			String code = exceptionMsg.substring("code:".length());
			this.addObject("op.msg", "[" + code + "] " + codes.getProperty(code));
		}
	}
}
