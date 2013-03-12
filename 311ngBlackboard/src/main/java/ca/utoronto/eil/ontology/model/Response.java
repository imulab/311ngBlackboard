package ca.utoronto.eil.ontology.model;

import java.io.Serializable;

public interface Response extends Serializable {

	public String getState();
	
	public String getUuid();
	
	public Object getObject(String key);
	
}
