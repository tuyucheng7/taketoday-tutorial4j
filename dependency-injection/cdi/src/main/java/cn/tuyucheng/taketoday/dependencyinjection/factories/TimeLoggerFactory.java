package cn.tuyucheng.taketoday.dependencyinjection.factories;

import cn.tuyucheng.taketoday.dependencyinjection.loggers.TimeLogger;

import javax.enterprise.inject.Produces;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeLoggerFactory {

	@Produces
	public TimeLogger getTimeLogger() {
		return new TimeLogger(new SimpleDateFormat("HH:mm"), Calendar.getInstance());
	}
}