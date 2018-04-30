package sailpoint.services.idn.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.status.StatusLogger;

public class Log4jUtils {
	
	static ConsoleAppender appender = null;
	
	/**
	 * Bootstrap log4j 2 at runtime for a console application / main class with no log4j.
	 * 
	 * @param desiredLevel
	 */
	public static synchronized void boostrapLog4j (Level desiredLevel) {

		// Squelch the not-so informative default message from log4j 2 of:
		//  ERROR StatusLogger No Log4j 2 configuration file found. Using default configuration 
		//  (logging only errors to the console), or user programmatically provided configurations. 
		//  Set system property 'log4j2.debug' to show Log4j 2 internal initialization logging. 
		//  See https://logging.apache.org/log4j/2.x/manual/configuration.html for instructions on 
		//  how to configure Log4j 2
		StatusLogger.getLogger().setLevel(Level.OFF);
		
		setThreshold(desiredLevel);
	}
	
	/**
	 * Adjust log4j levels at runtime.
	 * @param desiredLevel
	 */
	public static void setThreshold (Level desiredLevel) {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(desiredLevel);
		ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
	}

}
