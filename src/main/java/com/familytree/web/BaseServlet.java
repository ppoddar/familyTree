package com.familytree.web;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.familytree.FamilyTreeServer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * provides common {@link HttpServlet} facilities and reflectively
 * invokes servlet methods.
 * <p>
 * Theory of operation: A servlet can {@link #addRequestPattern(String, Class[], String)
 * register} its methods and invocation paths.
 * 
 * @author ppoddar
 *
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {
	private Serde serde;
	private List<RequestPattern> patterns = new ArrayList<>();
	
	private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
	private static Class<?>[] NO_ARG_TYPES = new Class<?>[0];
	private static String JSON_MIME_TYPE = "application/json";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		serde = new Serde();
	}
	/**
	 * register given method with its arguments to a path. 
	 * @param method
	 * @param argTypes can be null to imply that method takes no arguments.
	 * @param pathPattern
	 */
	protected void addRequestPattern(String methodName, String pathPattern) {
		Method m = findMethod(methodName);
		
		RequestPattern pattern = new RequestPattern(m, pathPattern);
		patterns.add(pattern);
	}
	
	Method findMethod(String name) {
		Method[] methods = getClass().getMethods();
		Method result = null;
		for (Method m : methods) {
			if (m.getName().equals(name)) {
				if (result != null) {
					
				} else {
					result = m;
				}
			}
		}
		return result;
	}
	
	/**
	 * finds a {@link RequestPattern#matches(String) matching} pattern
	 * and invokes it.
	 * The method invoked returns typed object.
	 * The return value is converted to response.
	 *  
	 * @param req a HTTP request
	 * @param res HTTP response.
	 */
	void matchPathAndInvoke(HttpServletRequest req, HttpServletResponse res) {
		String uri = req.getRequestURI();
		RequestPattern path = PatternMatcher.match(uri, patterns);
		if (path == null) {
			logger.warn("no registered path matches [" + uri + "]");
			error(404, "no path matches [" + req.getRequestURI() + "]", res);
		} else {
			try {
				Object result = path.invoke(this, req, res);
				res.setContentType(JSON_MIME_TYPE);
				res.setStatus(200);
				serde.serialize(res.getWriter(), result);
			} catch (InvocationTargetException ex) {
				error(ex.getTargetException(), res);
			} catch (Exception ex) {
				error(ex, res);
			}
		}
	}
	
	protected void error(int sc, String msg, HttpServletResponse res) {
		try {
			res.setStatus(sc);
			Writer writer = res.getWriter();
			if (writer == null) {
				System.err.println("response writer is null");
			} else {
				writer.write(msg);
				writer.flush();
			}
		} catch (Exception e) {
			System.err.println("error writing error response!");
			e.printStackTrace();
		}
	}
	
	protected void error(Throwable ex, HttpServletResponse res) {
		System.err.println("internal server threw following exception:");
		ex.printStackTrace();
		error(500, ex.getMessage(), res);
	}
	
	Properties getInitProperties(String prefix) {
		Properties props = new Properties();
		Enumeration<String> keys = getServletConfig().getInitParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = getServletConfig().getInitParameter(key);
			if (prefix != null) {
				key = key.substring(prefix.length());
			}
			props.setProperty(key, value);
		}
		
		logger.info("initailization properties with prefix [" + prefix + "]");
		logger.info(""+props);
		return props;
	}
	
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException {
		matchPathAndInvoke(req, res);
	}
	


}
