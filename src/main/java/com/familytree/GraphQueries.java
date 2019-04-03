package com.familytree;

/**
 * statically declared set of Cypher queries
 * @author ppoddar
 *
 */
public class GraphQueries {
	/**
	 * adds a person to database
	 */
	public static final String ADD_PERSON = "MATCH (p:Person)"
			+ " MATCH (f:Family {name:{name}})"
			+ " MERGE (p)-[:FAMILY]-(f)";
	public static final String FETCH_PERSON = "";
	
	/**
	 * adds a family to database
	 */
	public static final String ADD_FAMILY = 
			" MERGE (f:Family {name:{name}})";
	
	public static final String GET_PERSONS_BY_FAMILY_NAME
		= "START n=node(*)"
		+ " MATCH (n)-[:FAMILY]-(f) "
		+ " WHERE f.name= {name}"
		+ " RETURN n";
	
	public static final String COUNT_PERSON = "MATCH (e:Person)"
			+ " RETURN count(e) as count";
	public static final String COUNT_FAMILY = "MATCH (e:Family)"
			+ " RETURN count(e) as count";
}
