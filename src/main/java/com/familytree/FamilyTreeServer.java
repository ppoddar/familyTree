package com.familytree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.familytree.model.Family;
import com.familytree.model.Person;


public class FamilyTreeServer {
	Session session;
	private static final String PACKAGES = "com.familytree.model";
	private static final Logger logger = LoggerFactory.getLogger(FamilyTreeServer.class);
	
	/**
	 * creates a server with given configuration resource.
	 * The resource specifies Neo4j database properties. 
	 * @param configRsrc
	 */
	public FamilyTreeServer(String configRsrc) {
		logger.debug("creating from configuration resource [" + configRsrc + "]");
		if (configRsrc == null) 
			throw new IllegalArgumentException("can not create server with null configuration");
		ClasspathConfigurationSource source = new ClasspathConfigurationSource(configRsrc);
		createSession(source);
	}
	
	/**
	 * creates a server with given properties.
	 * The properties specify Neo4j database properties. 
	 * @param configRsrc
	 */
	public FamilyTreeServer(Properties props) {
		logger.debug("creating from properties [" + props + "]");
		ConfigurationSource source = new ConfigurationSource() {
			@Override
			public Properties properties() {
				return props;
			}
		};
		createSession(source);
	}
	
	private void createSession(ConfigurationSource source) {
		Configuration config = new Configuration.Builder(source).build();
		SessionFactory factory = new SessionFactory(config, PACKAGES);
		setSession(factory.openSession());
	}

	
	
	public Session getSession() {
		return session;
	}
	
	public FamilyTreeServer setSession(Session s) {
		session = s;
		return this;
	}
	
	
	/**
	 * add person.
	 * 
	 * @param p must not be null. 
	 */
	public <T> void addPerson(Person p) {
		session.save(p);
	}
	
	
	public <T> void addFamily(Family f) {
		assert f != null;
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", f.getName());
		session.query(GraphQueries.ADD_FAMILY, parameters);
//		session.save(f);
	}
	
	/**
	 * find family by given name.
	 * @param name
	 * @return
	 */
	public Family getFamilyByName(String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		Filter filter = new Filter("name", ComparisonOperator.EQUALS, name);
		Collection<Family> f = session.loadAll(Family.class, filter);
		Iterator<Family> it = f.iterator();
		if (it.hasNext()) return it.next();
		throw new RuntimeException("family with name [" + name + "] not found");
	}
	
	/**
	 * find person(s) by given first name.
	 * @param name
	 * @return
	 */
	public Iterable<Person> getPersonsByFirstName(String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", name);
		
		Filter filter = new Filter("firstName", ComparisonOperator.EQUALS, name);
		Collection<Person> persons = session.loadAll(Person.class, filter, 2);
		return persons;
	}
	
	/**
	 * find person(s) by given last name.
	 * @param name
	 * @return
	 */
	public Iterable<Person> getPersonsByLastName(String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("lastName", name);
		
		Filter filter = new Filter("lastName", ComparisonOperator.EQUALS, name);
		Collection<Person> persons = session.loadAll(Person.class, filter, 2);
		return persons;
	}


	/**
	 * get all persons belong to family of given name.
	 * @param name
	 * @return
	 */
	public Iterable<Person> getFamilyMembers(String name) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", name);
		String cypher = GraphQueries.GET_PERSONS_BY_FAMILY_NAME;
		return session.query(Person.class, cypher, parameters);
	}
	
	/**
	 * get all families.
	 * @return
	 */
	public Iterable<Family> getFamilies() {
		return session.loadAll(Family.class);
	}
	
	
	/**
	 * count number of families
	 * @return
	 */
	public int countFamily() {
		return count(GraphQueries.COUNT_FAMILY);
	}
	/**
	 * count number of persons
	 * @return
	 */
	public int countPerson() {
		return count(GraphQueries.COUNT_PERSON);
	}
	
	int count(String cypher) {
		Result result = session.query(cypher, new HashMap<>());
		Object value = result.queryResults().iterator().next().get("count");
		return Long.class.cast(value).intValue();
	}
	


}
