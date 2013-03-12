package ca.utoronto.eil.ontology.model;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

public class GraphClassPair {

	private static final Logger logger = Logger.getLogger(GraphClassPair.class);
	private String classIRI;
	private String graph;
	
	public GraphClassPair(String rawString, String uuid) throws ParameterException {
		if (StringUtils.countOccurrencesOf(rawString, "<") != 2 ||
				StringUtils.countOccurrencesOf(rawString, ">") != 2 ||
				StringUtils.countOccurrencesOf(rawString, "><") != 1) {
			logger.error("[" + uuid + "] " + rawString + " is in bad format");
			throw new ParameterException("code:E0026");
		}
		
		try {
			String graphStr = rawString.split("><")[0];
			this.graph = graphStr.substring(1);
			
			String classIRIStr = rawString.split("><")[1];
			this.classIRI = classIRIStr.substring(0, classIRIStr.length() - 1);
		} catch (Exception e) {
			logger.error("[" + uuid + "] string manipulation exception occured!", e);
			throw new ParameterException("code:E0027");
		}
	}
	
	public String getClassIRI() {
		return classIRI;
	}
	
	public String getGraph() {
		return graph;
	}
	
	
	
}
