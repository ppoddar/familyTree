package familyTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.familytree.FamilyTreeServer;
import com.familytree.model.Family;
import com.familytree.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Tests FamilyTree.
 * Each test uses a clean database.
 * 
 * @author ppoddar
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFamilyTree {
    private static FamilyTreeServer server;
    
    @BeforeClass
    public static void initDatabase() throws Exception {
    		Properties props = new Properties();
    		props.setProperty("URI", "bolt://localhost:7687/");
    		props.setProperty("username", "neo4j");
    		props.setProperty("password", "passw0rd");
    		
    	    server = new FamilyTreeServer(props);
    }
    
    @Before
    public void purgeDatabase() {
	    server.getSession().purgeDatabase();
    }
    
    @Test
    public void test01FamiliesAreMergedByName() {
    		// save three instances but two have same name
		Family f1 = new Family("f1");
		Family f2 = new Family("f1");
		Family f3 = new Family("f3");
		server.addFamily(f1);
		server.addFamily(f2);
		server.addFamily(f3);
		// should be only two instance, not three
		assertEquals(2, server.countFamily());
    }
    
    @Test
    public void test02FamilyCanBeFound() {
    		String fname = "test-family";
       	Family f = new Family(fname);
       	server.addFamily(f);
       	
    		Family f2 = server.getFamilyByName(fname);
    		assertNotNull(f2);
    		assertEquals(fname, f.getName());
    }
	
	@Test
	public void test03CreateMultiplePersonInSameFamily() {
		String familyName = "Joint-Family";
		Family family = new Family(familyName);
		ObjectMapper mapper = new ObjectMapper();
	    int Nmember = 5;
		for (int i = 0; i < Nmember; i++) {
			ObjectNode node = mapper.createObjectNode();
			node.put("first-name", "person-" + i);
			node.put("last-name", "surname");
		
			Person p = new Person(node).setFamily(family);
		
			server.addPerson(p);
		}
//		Iterable<Person> members = server.getFamilyMembers(familyName);
//		
//		assertEquals(Nmember, iterate(members, false));
		assertEquals(Nmember, server.countPerson());
		assertEquals(1, server.countFamily());
		
	}
	
	
	
	@Test
	public void test06RelationsSaveClosure() {
		String familyName = "Related-Family";
		Family family = new Family(familyName);
		Person mother = createPerson("M", family);
		Person child = createPerson("C", family);
		mother.addChild(child);
		
		server.addPerson(mother);
		
		assertEquals(2, server.countPerson());
		
		Person m = server.getPersonsByFirstName("M").iterator().next();
		Person c = server.getPersonsByFirstName("C").iterator().next();
		
		assertEquals(c.getMother(), m);
		
		
	}
	
	Person createPerson(String firstName, Family family) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode a = mapper.createObjectNode();
		a.put("first-name", firstName);
		a.put("last-name", "");
		Person p = new Person(a).setFamily(family);
		return p;
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
