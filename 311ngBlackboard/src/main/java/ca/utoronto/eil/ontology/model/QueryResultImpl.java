package ca.utoronto.eil.ontology.model;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;

public class QueryResultImpl implements QueryResult  {

	private static final long serialVersionUID = 1651084859659106880L;
	private List<BindingSet> bindingSets;
	private int resultCount;
	private boolean queryExecutionStatus;
	private String excptionMessage;
	
	public QueryResultImpl() {
		bindingSets = new ArrayList<BindingSet>();
		resultCount = 0;
		queryExecutionStatus = true;
		excptionMessage = new String();
	}
	
	public String getExceptionMessage() {
		return this.excptionMessage;
	}
	
	public void setExceptionMessage(String message) {
		this.excptionMessage = message;
	}
	
	public boolean getQueryExecutionStatus() {
		return this.queryExecutionStatus;
	}
	
	public void setQueryExecutionStatus(boolean status) {
		this.queryExecutionStatus = status;
	}
	
	public void addResult(BindingSet set) {
		bindingSets.add(set);
		resultCount++;
	}
	
	public List<BindingSet> getBindingSets() {
		return this.bindingSets;
	}
	
	public BindingSet getBindingSetByIndex(int index) {
		return this.bindingSets.get(index);
	}
	
	public int getResultCount() {
		return this.resultCount;
	}

}
