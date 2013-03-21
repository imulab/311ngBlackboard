package ca.utoronto.eil.ontology.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;

public class QueryResultImpl implements QueryResult  {

	private static final long serialVersionUID = 1651084859659106880L;
	private List<Map<String, String>> results;
	private List<String> bindingNames;
	private int resultCount;
	private boolean queryExecutionStatus;
	private String excptionMessage;
	
	public QueryResultImpl() {
		results = new ArrayList<Map<String, String>>();
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
	
	public void addResult(Map<String, String> newResult) {
		results.add(newResult);
		resultCount++;
	}
	
	public Map<String, String> getBindingSetByIndex(int index) {
		return this.results.get(index);
	}
	
	public int getResultCount() {
		return this.resultCount;
	}

	@Override
	public List<String> getBindingNames() {
		return this.bindingNames;
	}
	
	public void setBindingNames(List<String> bindingNames) {
		this.bindingNames = bindingNames;
	}

	@Override
	public List<Map<String, String>> getResults() {
		return this.results;
	}

}
