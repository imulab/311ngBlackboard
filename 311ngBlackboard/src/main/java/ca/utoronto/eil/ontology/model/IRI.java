package ca.utoronto.eil.ontology.model;

import org.apache.log4j.Logger;

public class IRI {

	private static final Logger logger = Logger.getLogger(IRI.class);
	private String iri;
	
	/**
	 * Construct a IRI object using raw input of format '<IRI>'
	 * 
	 * @param rawString raw string with format
	 * @param uuid uuid of web request (used for logging)
	 * 
	 * @throws ParameterException any validation or cast exception
	 */
	public IRI(String rawString, String uuid) throws ParameterException {
		if (!rawString.startsWith("<") || !rawString.endsWith(">")) {
			logger.error("[" + uuid + "] " + rawString + " is in bad format");
			throw new ParameterException("code:E0007");
		}
		
		try {
			this.iri = rawString.substring(1);
			this.iri = this.iri.substring(0, this.iri.length() - 1);
		} catch (Exception e) {
			logger.fatal("[" + uuid + "] string manipulation exception occured!", e);
			throw new ParameterException("code:E0008");
		}
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}
}
