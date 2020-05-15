package net.sf.jetro.transform.logging;

import java.lang.reflect.Method;
import java.util.Objects;

import org.slf4j.Logger;

public enum LogLevel {
	DEBUG, ERROR, INFO, TRACE, WARN;
	
	public void logAt(final Logger logger, String message) {
		Objects.requireNonNull(logger, "logger must not be null");
		
		try {
			Method loggingMethod = logger.getClass().getMethod(name().toLowerCase(), String.class);
			loggingMethod.invoke(logger, message);
			System.out.println(message);
		} catch (Exception e) {
			logger.error("Something went wrong when invoking the logger reflectively.", e);
		}
	}
}
