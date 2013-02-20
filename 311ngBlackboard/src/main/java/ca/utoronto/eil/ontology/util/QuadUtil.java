package ca.utoronto.eil.ontology.util;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.eil.ontology.model.Quad;

public class QuadUtil {

	/**
	 * Groups the list of quads by their subjects
	 * 
	 * @param quads input quads
	 * 
	 * @return quads grouped by subjects
	 */
	public static List<List<Quad>> groupBySubject(List<Quad> quads) {
		List<List<Quad>> groupByResult = new ArrayList<List<Quad>>();
		
		loopEachQuad:
		for (Quad each : quads) {
			
			//Iterate through existing groupByResult
			for (List<Quad> eachGroupBy : groupByResult) {
				if (eachGroupBy != null && eachGroupBy.size() > 0) {
					if (eachGroupBy.get(0).getSubject().equals(each.getSubject())) {
						//Add to groupBy
						eachGroupBy.add(each);
						continue loopEachQuad;
						
					}
				}
			}
			
			//Construct a new groupByItem
			List<Quad> newGroupBy = new ArrayList<Quad>();
			newGroupBy.add(each);
			groupByResult.add(newGroupBy);
		}
		
		return groupByResult;
	}
	
	/**
	 * Convert a list of quads to string format, for easiness to send out in http requests
	 * 
	 * @param quads a list of quads (ideally with same subject)
	 * 
	 * @return string representation of the list of quads
	 */
	public static String convertToString(List<Quad> quads) {
		String str = "";
		for (Quad each : quads) {
			str += "," + each.toString();
		}
		return str.substring(1);
	}
}
