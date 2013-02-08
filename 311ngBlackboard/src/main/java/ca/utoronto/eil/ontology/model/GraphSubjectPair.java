package ca.utoronto.eil.ontology.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class GraphSubjectPair {

	private static final Logger logger = Logger.getLogger(GraphSubjectPair.class);
	private String graph;
	private String subject;

	/**
	 * Construct a graph-subject pair object using raw input of format '<graph><subject or class>'
	 * 
	 * @param rawString raw formatted string
	 * @param uuid uuid of web request (used for logging)
	 * 
	 * @throws ParameterException any validation or cast exception
	 */
	public GraphSubjectPair(String rawString, String uuid) throws ParameterException {
		if (!rawString.startsWith("<") || 
				!rawString.endsWith(">") || 
				!rawString.contains("><") || 
				StringUtils.countMatches(rawString, "><") > 1) {
			logger.error("[" + uuid + "] " + rawString + " is in bad format");
			throw new ParameterException("code:E0005");
		}
		
		try {
			this.graph = rawString.split("><")[0].substring(1);
			this.subject = rawString.split("><")[1];
			this.subject = this.subject.substring(0, this.subject.length() - 1);
		} catch (Exception e) {
			logger.fatal("[" + uuid + "] string manipulation exception occured!", e);
			throw new ParameterException("code:E0006");
		}
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
}
