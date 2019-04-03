package com.familytree.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Family {
	@Id @GeneratedValue
	Long id;
	String name;
	
	
	public Family() {
	}
	
	public Family(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "family:" + name;
	}
}
