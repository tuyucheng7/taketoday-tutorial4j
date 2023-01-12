package cn.tuyucheng.taketoday.metrics.servlets;

import cn.tuyucheng.taketoday.metrics.healthchecks.DatabaseHealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

public class MyHealthCheckServletContextListener extends HealthCheckServlet.ContextListener {
	public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();

	static {
		HEALTH_CHECK_REGISTRY.register("db", new DatabaseHealthCheck());
	}

	@Override
	protected HealthCheckRegistry getHealthCheckRegistry() {
		return HEALTH_CHECK_REGISTRY;
	}
}
