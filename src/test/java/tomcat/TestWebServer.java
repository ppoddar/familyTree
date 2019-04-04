package tomcat;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.familytree.web.RequestPattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class TestWebServer {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static JsonNode RESPONSE_SCHEMA;
	private static final String BASE_URL = "http://localhost:10000/";
	private static final Logger logger = LoggerFactory.getLogger("test");
	
	@BeforeClass
	public static void init() throws Exception {
		String schemaRsrc = "schema.json";
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(schemaRsrc);
		assertNotNull(in);
		RESPONSE_SCHEMA = mapper.readValue(in, JsonNode.class);
	}
	@Test
	public void testPattternWithoutVaraible() {
		genericMatchingTest("family/name", "family/name", true);
	}
	
	@Test
	public void testPattternWithVariable() {
		genericMatchingTest("family/{name}", "family/xyz", true);
	}
	
	@Test
	public void testFamilyResponse() throws Exception {
		String familyName = "Poddars";
		JsonNode response = genericRequestTest("family/" + familyName);
		validateResponse("family", response);
	}
	
	@Test
	public void testPersonResponse() throws Exception {
		String familyName = "Poddars";
		JsonNode response = genericRequestTest("family/" + familyName + "/members");
		assertTrue(response.isArray());
		assertTrue(response.size()>0);
		for (JsonNode r : response) {
			validateResponse("person", r);
		}
	}

	
	JsonNode genericRequestTest(String path) throws Exception {
		String url = BASE_URL + path;
		InputStream in = new URL(url).openStream();
		
		logger.debug("sending request to " + url);
		
		return mapper.readValue(in, JsonNode.class);
	}
	
	
	void genericMatchingTest(String pat, String path, boolean shouldMatch) {
		RequestPattern pattern = new RequestPattern(null, pat);
		boolean match = pattern.matches(path);
		if (match) {
			if (!shouldMatch) {
				fail(pat + " matches " + path + " whereas it should not");
			}
		} else {
			if (shouldMatch) {
				fail(pat + " does not match " + path + " whereas it should");
			}
		}
		
	}
	
	void validateResponse(String path, JsonNode response) throws Exception {
		logger.debug("validating response against schema " + path);
		logger.debug("Response:" +  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
		JsonSchema schema = getSchema(path);
		ProcessingReport report = schema.validate(response);
		
		if (!report.isSuccess()) {
			Iterator<ProcessingMessage> msgs = report.iterator();
			while (msgs.hasNext()) {
				ProcessingMessage msg = msgs.next();
				System.err.println(msg);
				fail();
			}
		}
	}
	
	JsonSchema getSchema(String path) throws Exception {
		JsonNode schemaNode = RESPONSE_SCHEMA.path(path);
		assertFalse(path + " does not exist in schema", schemaNode.isMissingNode());
		((ObjectNode)schemaNode).put("$schema", "http://json-schema.org/draft-04/schema#");
		JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		return factory.getJsonSchema(schemaNode);
	}

}
