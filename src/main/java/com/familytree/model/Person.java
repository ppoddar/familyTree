package com.familytree.model;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Person must have a first and last name.
 * 
 * Annotated with Neo4j-OGM annotations.
 * 
 * @author ppoddar
 *
 */
@NodeEntity
public class Person {
	@Id @GeneratedValue
	Long id;
	@Property
	String firstname;
	@Property
	String lastname;
	@Property
	boolean male;
	@Relationship(type="FAMILY")
	Family family;
	@Relationship(type="FATHER")
	Person father;
	@Relationship(type="MOTHER")
	Person mother;
	@Relationship(type="SPOUSE")
	Person spouse;

	public Person() {
		
	}
	
	/**
	 * create a person with given properties.
	 * 
	 * @param family family to which this person belongs.
	 * @param node properties of person. must have first-name and last-name
	 */
	public Person(JsonNode node) {
		assert node.has("first-name") : "data " + node + " does not have first-name";
		assert node.has("last-name");
		this.firstname = node.path("first-name").asText();
		this.lastname  = node.path("last-name").asText();
		String gender = node.path("gender").asText();
		this.male = "MALE".equals(gender.toUpperCase())
     			 || "M".equals(gender.toUpperCase());
	}
	
	/**
	 * adds child. The parent is automatically set
	 * @param child must not be null. must not have parents set.
	 * must have family set to same
	 */
	public void addChild(Person child) {
		assert child != null;
		assert this.getFamily().equals(child.getFamily());
		assert child.father == null;
		assert child.mother == null;
		
		if (isMale()) {
			child.father = this;
		} else {
			child.mother = this;
		}
	}
	
	public Long getId() {
		return id;
	}
	
	public Person setFamily(Family f) {
		this.family = f;
		return this;
	}
	
	public Family getFamily() {
		return family;
	}
	public Person getFather() {
		return father;
	}
	
	public Person getMother() {
		return mother;
	}
	
	public boolean isMale() {
		return male;
	}
	
	public void setFirstname(String f) {
		firstname = f;
		
	}
	public void setLastname(String l) {
		lastname = l;
		
	}
	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}
	
	public String toString() {
		return firstname + " " + lastname;
	}
	
}
