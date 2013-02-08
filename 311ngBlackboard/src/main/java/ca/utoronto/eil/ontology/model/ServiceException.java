package ca.utoronto.eil.ontology.model;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 1938869097056916294L;

	public ServiceException() {
		super();
	}
	
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Throwable th) {
		super(message, th);
	}
}
