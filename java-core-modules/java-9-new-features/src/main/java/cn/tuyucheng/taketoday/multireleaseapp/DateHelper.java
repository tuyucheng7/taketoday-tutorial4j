package cn.tuyucheng.taketoday.multireleaseapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateHelper {

	private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

	public static boolean checkIfLeapYear(String dateStr) throws Exception {
		logger.info("Checking for leap year using Java 1 calendar API");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
		int year = cal.get(Calendar.YEAR);
		return (new GregorianCalendar()).isLeapYear(year);
	}
}