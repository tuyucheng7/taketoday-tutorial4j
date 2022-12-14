package cn.tuyucheng.taketoday.flyweight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Engine for a vehicle.
 *
 * @author tuyucheng
 */
public class Engine {

	private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

	public void start() {
		LOG.info("Engine is starting!");
	}

	public void stop() {
		LOG.info("Engine is stopping!");
	}
}