package ca.utoronto.eil.ontology.model;

public class LoadUserException extends Exception {

	private static final long serialVersionUID = 8072361439148898296L;

	public LoadUserException() {
		super();
	}
	
	public LoadUserException(String message) {
		super(message);
	}
	
	public LoadUserException(String message, Throwable th) {
		super(message, th);
	}
}
