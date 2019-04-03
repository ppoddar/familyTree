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

/**
 * Builds and manages a family tree which is a graph of {@link Person}.
 * This server interacts with Neo4j (remote) database in a 
 * {@link Session session}. 
 * The database session is configured either with a properties file or
 * properties directly. 
 * 
 * @author ppoddar
 *
 */
public class FamilyTreeServer {
	private Session session;
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

	
	/**
	 * gets session used by this receiver
	 * @return
	 */
	public Session getSession() {
		return session;
	}
	
	/**
	 * sets session to be used by this receiver
	 * @return
	 */
	public FamilyTreeServer setSession(Session s) {
		session = s;
		return this;
	}
	
	
	/**
	 * add given person to a family.
	 * 
	 * @param p must not be null. 
	 */
	public <T> void addPerson(Person p) {
		session.save(p);
	}
	
	
	/**
	 * add given family. The family is merged by name. 
	 * So if this method is invoked multiple times with family of same name,
	 * a single family would be created.
	 * 
	 * @param p must not be null. 
	 * @see GraphQueries#ADD_FAMILY
	 */
	public <T> void addFamily(Family f) {
		assert f != null;
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", f.getName());
		session.query(GraphQueries.ADD_FAMILY, parameters);
	}
	
	/**
	 * find family by given name.
	 * @param name
	 * @return a family
	 * @throws RuntimeException is no family of given name exists
	 */
	public Family getFamilyByName(String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		Filter filter = new Filter("name", ComparisonOperator.EQUALS, name);
		Collection<Family> f = session.loadAll(Family.class, filter);
		if (f.isEmpty()) {
			throw new RuntimeException("family with name [" + name + "] not found");
		}
		return f.iterator().next();
	}
	
	/**
	 * find person(s) by given first name.
	 * @param firstName 
	 * @return zero or more Person with given first name
	 */
	public Iterable<Person> getPersonsByFirstName(String firstName) {
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", firstName);
		
		Filter filter = new Filter("firstName", ComparisonOperator.EQUALS, firstName);
		Collection<Person> persons = session.loadAll(Person.class, filter, 2);
		return persons;
	}
	
	/**
	 * find person(s) by given last name.
	 * @param name
	 * @return
	 */
	public Iterable<Person> getPersonsByLastName(String lastName) {
		Map<String, Object> params = new HashMap<>();
		params.put("lastName", lastName);
		
		Filter filter = new Filter("lastName", ComparisonOperator.EQUALS, lastName);
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
