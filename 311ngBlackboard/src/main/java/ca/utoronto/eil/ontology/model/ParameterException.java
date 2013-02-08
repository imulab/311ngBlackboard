package ca.utoronto.eil.ontology.model;

public class ParameterException extends Exception {

	private static final long serialVersionUID = 3566704266359371007L;

	public ParameterException() {
		super();
	}
	
	public ParameterException(String message) {
		super(message);
	}
	
	public ParameterException(String message, Throwable th) {
		super(message, th);
	}
}
