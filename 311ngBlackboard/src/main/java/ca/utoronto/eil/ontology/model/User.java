package ca.utoronto.eil.ontology.model;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1110700340062939024L;

	private String username;
	private String password;
	
	public User() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Boolean hasUsername(String username) {
		return this.username.equals(username);
	}
	
	public Boolean hasPassword(String password) {
		return this.password.equals(password);
	}
}
