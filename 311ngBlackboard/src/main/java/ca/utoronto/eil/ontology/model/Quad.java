package ca.utoronto.eil.ontology.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Quad {

	private static final Logger logger = Logger.getLogger(Quad.class);
	private String graph;
	private String subject;
	private String property;
	private String object;
	
	/**
	 * Construct a quad object using raw input of format '<graph><subject or class><parameter or attribute><object>'
	 * 
	 * @param rawString raw string with format
	 * @param uuid uuid of web request (used for logging)
	 * 
	 * @throws ParameterException any validation or cast exception
	 */
	public Quad(String rawString, String uuid) throws ParameterException {
		if (!rawString.startsWith("<") ||
				!rawString.endsWith(">") ||
				StringUtils.countMatches(rawString, "><") != 3) {
			logger.error("[" + uuid + "] " + rawString + " is in bad format");
			throw new ParameterException("code:E0009");
		}
		
		try {
			this.graph = rawString.split("><")[0].substring(1);
			this.subject = rawString.split("><")[1];
			this.property = rawString.split("><")[2];
			this.object = rawString.split("><")[3];
			this.object = this.object.substring(0, this.object.length() - 1);
		} catch (Exception e) {
			logger.fatal("[" + uuid + "] string manipulation exception occured!", e);
			throw new ParameterException("code:E0010");
		}
	}
	
	@Override
	public String toString() {
		return "<" + this.graph + "><" + this.subject + "><" + this.property + "><" + this.object + ">";
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
}
