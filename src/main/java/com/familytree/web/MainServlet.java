package com.familytree.web;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.familytree.FamilyTreeServer;
import com.familytree.model.Family;
import com.familytree.model.Person;

/**
 * a servlet derived from {@link BaseServlet} has to 
 * {@link BaseServlet#addRequestPattern(String, Class[], String) register}
 * its paths
 * 
 * @author ppoddar
 *
 */
@SuppressWarnings("serial")
public class MainServlet extends BaseServlet {
	FamilyTreeServer server;
	
	@Override 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		addRequestPattern("getFamilyByName",  "family/{name}");
		addRequestPattern("getFamilyMembers", "family/{name}/members");
		server = new FamilyTreeServer(getInitProperties("neo4j."));
	}
	
	public Family getFamilyByName(String name) {
		return server.getFamilyByName(name);
	}
	
	public List<Person> getFamilyMembers(String name) {
		return server.getFamilyMembers(name);
	}
	

}
