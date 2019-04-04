package familyTree;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.LoadStrategy;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.familytree.FamilyTreeServer;
import com.familytree.model.Family;
import com.familytree.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * assumes a Neo4j database has been running and populated
 * with sample data.
 * 
 * @author ppoddar
 *
 */
public class TestNeo4j {
	ObjectMapper mapper;
	static FamilyTreeServer server;
	static int NFamily = 1;
	static int NMember = 5;
	
	@BeforeClass
	public static void init() {
		Properties props = new Properties();
		props.setProperty("URI", "bolt://localhost");
		props.setProperty("username", "neo4j");
		props.setProperty("password", "passw0rd");
		
		server = new FamilyTreeServer(props);
	}

	@Test
	public void testDatabaseHasPersons() {
		assertEquals(NMember, server.countPerson());
	}
	
	@Test
	public void testDatabaseHasFamilies() {
		assertEquals(NFamily, server.countFamily());
	}
	
	@Test
	public void testGetFamilies() {
		Iterable<Family> families = server.getFamilies();
		
		int i = iterate(families, true);
		
		assertEquals(NFamily, i);
	}
	
	@Test
	public void testGetFamilyByName() {
		String fName = "Poddars";
		Family family = server.getFamilyByName(fName);
				
		assertEquals(fName, family.getName());
	}
	
	@Test
	public void testGetFamilyMembers() {
		String fName = "Poddars";
		List<Person> members = server.getFamilyMembers(fName);
				
		assertEquals(NMember, members.size());
	}
	
	@Test
	public void testGetPersonsByFirstName() {
		String fName = "pinaki";
		Iterable<Person> persons = server.getPersonsByFirstName(fName);
				
		int i = iterate(persons, true);
		assertEquals(1, i);
	}
	
	@Test
	public void testGetPersonsByLastName() {
		String lName = "poddar";
		Iterable<Person> persons = server.getPersonsByLastName(lName);
				
		int i = iterate(persons, true);
		assertEquals(NMember, i);
	}

	<T> int iterate(Iterable<T> coll, boolean print) {
		int i = 0;
		for (T e : coll) {
			if (print) System.err.println(e);
			i++;
		}
		return i;
	}

}
