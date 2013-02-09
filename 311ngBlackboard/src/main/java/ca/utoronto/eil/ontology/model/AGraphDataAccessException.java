package ca.utoronto.eil.ontology.model;

public class AGraphDataAccessException extends Exception {

	private static final long serialVersionUID = -6414934118166636516L;

	public AGraphDataAccessException() {
		super();
	}
	
	public AGraphDataAccessException(String message) {
		super(message);
	}
	
	public AGraphDataAccessException(String message, Throwable th) {
		super(message, th);
	}
}
