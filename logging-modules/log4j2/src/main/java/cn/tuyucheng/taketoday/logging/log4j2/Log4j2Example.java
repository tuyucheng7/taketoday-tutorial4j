package cn.tuyucheng.taketoday.logging.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j2Example {

	private static final Logger logger = LogManager.getLogger(Log4j2Example.class);

	public static void main(String[] args) {
		logger.debug("Debug log message");
		logger.info("Info log message");
		logger.error("Error log message");
		logger.warn("Warn log message");
		logger.fatal("Fatal log message");
		logger.trace("Trace log message");
	}

}
