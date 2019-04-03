package tomcat;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import com.familytree.web.RequestPattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestWebServer {
	private static final String BASE_URL = "http://localhost:10000/";
	
	@Test
	public void testPattternWithoutVaraible() {
		genericMatchingTest("family/name", "family/name", true);
	}
	
	@Test
	public void testPattternWithVariable() {
		genericMatchingTest("family/{name}", "family/xyz", true);
	}
	
	@Test
	public void testURL() throws Exception {
		genericRequestTest("family/name");
	}
	
	void genericRequestTest(String path) throws Exception {
		String url = BASE_URL + path;
		InputStream in = new URL(url).openStream();
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.readValue(in, JsonNode.class);
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

}
