package com.familytree.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.familytree.FamilyTreeServer;
import com.familytree.model.Family;

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
		addRequestPattern("getFamilyByName", 
				new Class<?>[] {String.class},
				"family/{name}");
		
		
		server = new FamilyTreeServer(getInitProperties("neo4j."));
	}
	
	public Family getFamilyByName(String name) {
		return server.getFamilyByName(name);
	}
	
	

}
