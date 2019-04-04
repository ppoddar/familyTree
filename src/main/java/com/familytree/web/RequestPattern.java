package com.familytree.web;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.familytree.FamilyTreeServer;

public class RequestPattern {
	private final String pathPattern;
	private final Method method;
	Map<String, String> params = new LinkedHashMap<>();
	private static String FORWARD_SLASH = "/";
	private static String OPEN_CURLY_BRACE = "{";
	private static String CLOSE_CURLY_BRACE = "}";
	private static String WILDCARD = "*";
	private static final Logger logger = LoggerFactory.getLogger(RequestPattern.class);

	/**
	 * create a request path pattern with a method to invoke and a path.
	 * 
	 * 
	 * @param m a method of a servlet
	 * @param pat a path pattern that indicates variable with {} markers
	 */
	public RequestPattern(Method m, String pat) {
		pathPattern = (pat.startsWith(FORWARD_SLASH)) ? pat.substring(1) : pat;
		method = m;
	}
	
	/**
	 * invokes method of this path with given servlet.
	 * The path variable are used a s method parameters.
	 * @param servlet
	 * @param req
	 * @param res
	 */
	public Object invoke(HttpServlet servlet, HttpServletRequest req, HttpServletResponse res)
	   throws Exception {
		logger.debug("invoke " + this + " with parameters " + params);
		return method.invoke(servlet, toArgs(params));
	}
	
	public int getSegmentCount() {
		return pathPattern.split(FORWARD_SLASH).length;
	}
	
	public boolean matches(String path) {
		if (path.startsWith(FORWARD_SLASH)) path = path.substring(1);
		String[] patterns = pathPattern.split(FORWARD_SLASH);
		String[] segments = path.split(FORWARD_SLASH);
		int i = 0;
		for (; i < patterns.length; i++) {
			if (i >= segments.length) {
				logger.debug(this + " does not match shorter [" + path + "]");
				return false;
			}
			String pattern = patterns[i];
			String seg = segments[i];
			if (isVariable(pattern)) {
				params.put(getVariable(pattern), seg);
			} else if (isAny(pattern) || pattern.equals(seg)) {
				continue;
			} else {
				logger.debug(this + " does not match [" + path + "] "
						+ "" + i + "-th segment [" + seg + "]");
				return false;
			}
		}
		if (i < segments.length) {
			logger.debug(this + " does not match longer [" + path + "]");
			return false;
		}
		logger.debug(this + " matches [" + path + "]");
		return true; // all segments matched
	}
	
	public Map<String,Object> getParameters() {
		return Collections.unmodifiableMap(params);
	}
	
	boolean isVariable(String s) {
		return s != null
		&& s.startsWith(OPEN_CURLY_BRACE) 
		&& s.endsWith(CLOSE_CURLY_BRACE);
	}
	
	String getVariable(String s) {
		return s.substring(1, s.length()-1);
	}
	
	boolean isAny(String s) {
		return WILDCARD.equals(s);
	}
	
	Object[] toArgs(Map<String,String> params) {
		Object[] result = new Object[params.size()];
		int i = 0;
		Class<?>[] types = method.getParameterTypes();
		for (String key : params.keySet()) {
			String value = params.get(key);
			Class<?> type = types[i];
			result[i] = convert(value, type);
		}
		return result;
	}
	
	Object convert(String value, Class<?> type) {
		if (type == String.class) return value;
		if (type == int.class) return Integer.parseInt(value);
		if (type == Integer.class) return Integer.parseInt(value);
		throw new RuntimeException("can not convert " + value + " to " + type);
	}
	
	public String toString() {
		return pathPattern;
	}
}
