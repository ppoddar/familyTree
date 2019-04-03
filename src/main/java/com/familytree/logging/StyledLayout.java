package com.familytree.logging;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class StyledLayout extends PatternLayout {
	boolean ansi_terminal = System.getenv("TERM") != null;
	static String ANSI_RESET = "\u001b[0m";
	static Map<Level, String> STYLES = new HashMap<Level, String>();
	static {
		STYLES.put(Level.TRACE, "\u001b[37m");
		STYLES.put(Level.DEBUG, "\u001b[37m");
		STYLES.put(Level.INFO,  "\u001b[32m");
		STYLES.put(Level.WARN,  "\u001b[31m");
		STYLES.put(Level.ERROR, "\u001b[0m\u001b[31m");
	}
	public String format(LoggingEvent event) {
		String msg = super.format(event);
		Level level = event.getLevel();
		if (!ansi_terminal) return msg;
		if (!STYLES.containsKey(level)) return msg;
		String ansi_style = STYLES.get(level);
		return ansi_style + msg + ANSI_RESET;
	}

}
