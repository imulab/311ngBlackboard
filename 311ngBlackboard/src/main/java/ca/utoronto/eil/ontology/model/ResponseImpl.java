package ca.utoronto.eil.ontology.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.springframework.stereotype.Component;

public class ResponseImpl implements Response {

	private static final long serialVersionUID = -7122783225218498324L;
	private String state;
	private String uuid;
	private Map<String, Object> objectMap;
	
	public ResponseImpl() {
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

	public ResponseImpl setState(String state) {
		this.state = state;
		return this;
	}
	
	public ResponseImpl addObject(String key, Object object) {
		this.objectMap.put(key, object);
		return this;
	}
	
	public ResponseImpl removeObject(String key) {
		this.objectMap.remove(key);
		return this;
	}
	
	public Object getObject(String key) {
		return this.objectMap.get(key);
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
