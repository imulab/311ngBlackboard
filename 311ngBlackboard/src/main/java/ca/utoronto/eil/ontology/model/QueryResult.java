package ca.utoronto.eil.ontology.model;

import java.io.Serializable;
import java.util.List;

import org.openrdf.query.BindingSet;

public interface QueryResult extends Serializable {

	public boolean getQueryExecutionStatus();
	
	public String getExceptionMessage();
	
	public List<BindingSet> getBindingSets();
	
	public BindingSet getBindingSetByIndex(int index);
	
	public int getResultCount();
}
