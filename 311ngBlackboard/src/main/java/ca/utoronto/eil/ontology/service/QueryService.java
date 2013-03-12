package ca.utoronto.eil.ontology.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.utoronto.eil.ontology.dao.AGraphDao;
import ca.utoronto.eil.ontology.model.AGraphDataAccessException;
import ca.utoronto.eil.ontology.model.QueryResult;
import ca.utoronto.eil.ontology.model.QueryResultImpl;
import ca.utoronto.eil.ontology.model.ServiceException;

import com.google.gson.Gson;

@Service
public class QueryService {

	private static final Logger logger = Logger.getLogger(QueryService.class);
	@Autowired private AGraphDao dao;
	
	/**
	 * Run a query and return result
	 * Consult result format in ca.utoronto.eil.ontology.model.QueryResult
	 * 
	 * @param query query string
	 * @param uuid uuid used to track request
	 * @return query result as in ca.utoronto.eil.ontology.model.QueryResult
	 * 
	 * @throws ServiceException
	 */
	public QueryResult doQuery(String query, String uuid) throws ServiceException {
		
		if (query == null) {
			throw new ServiceException("code:E0028");
		}
		
		QueryResult result = null;
		
		// Call dao to run the query
		try {
			result = dao.runQuery(query, uuid);
		} catch (AGraphDataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
		
		// Cover up if something went wrong
		if (result == null) {
			logger.error("[" + uuid + "] Something went wrong! We are returning an empty query result to cover up :p");
			return new QueryResultImpl();
		}
		
		// return
		return result;
	}
}
