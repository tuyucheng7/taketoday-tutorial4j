module cn.tuyucheng.taketoday.logging.slf4j {
	requires org.slf4j;
	provides java.lang.System.LoggerFinder
			with cn.tuyucheng.taketoday.logging.slf4j.Slf4jLoggerFinder;
	exports cn.tuyucheng.taketoday.logging.slf4j;
}