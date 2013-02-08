package ca.utoronto.eil.ontology.model;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = 5320359836954085077L;

	public AuthenticationException() {
		super();
	}
	
	public AuthenticationException(String message) {
		super(message);
	}
	
	public AuthenticationException(String message, Throwable th) {
		super(message, th);
	}
}
