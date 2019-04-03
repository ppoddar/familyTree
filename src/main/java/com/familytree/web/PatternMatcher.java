package com.familytree.web;

public class PatternMatcher {
	
	public static RequestPattern match(String path, Iterable<RequestPattern> patterns) {
		RequestPattern matched = null;
		for (RequestPattern pattern : patterns) {
			boolean match = pattern.matches(path);
			if (!match) continue;
			if (matched != null) {
				throw new RuntimeException("conflicting match: both " + matched
						+ " and " + pattern  + " matches " + path);
			} else {
				matched = pattern;
			}
		}
		return matched;
	}
}
