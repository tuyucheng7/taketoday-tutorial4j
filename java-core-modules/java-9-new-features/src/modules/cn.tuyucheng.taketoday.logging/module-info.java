module cn.tuyucheng.taketoday.logging {
	provides java.lang.System.LoggerFinder
			with cn.tuyucheng.taketoday.logging.CustomLoggerFinder;
	exports cn.tuyucheng.taketoday.logging;
}