package com.familytree;

public class GraphQueries {
	public static final String ADD_PERSON = "MATCH (p:Person)"
			+ " MATCH (f:Family {name:{name}})"
			+ " MERGE (p)-[:FAMILY]-(f)";
	public static final String FETCH_PERSON = "";
	
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
