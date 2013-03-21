package ca.utoronto.eil.ontology.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;

public interface QueryResult extends Serializable {

	public boolean getQueryExecutionStatus();
	
	public String getExceptionMessage();
	
	public List<String> getBindingNames();
	
	public List<Map<String, String>> getResults();
	
	public Map<String, String> getBindingSetByIndex(int index);
	
	public int getResultCount();
}
