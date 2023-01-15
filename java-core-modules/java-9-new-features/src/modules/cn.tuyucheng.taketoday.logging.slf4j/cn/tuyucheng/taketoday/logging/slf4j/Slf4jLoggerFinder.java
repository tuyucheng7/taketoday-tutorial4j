package cn.tuyucheng.taketoday.logging.slf4j;

public class Slf4jLoggerFinder extends System.LoggerFinder {

	@Override
	public System.Logger getLogger(String name, Module module) {
		return new Slf4jLogger(name);
	}
}