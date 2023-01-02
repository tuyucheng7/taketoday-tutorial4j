package cn.tuyucheng.taketoday.maven.it;

import org.glassfish.jersey.server.ResourceConfig;

public class EndpointConfig extends ResourceConfig {
	public EndpointConfig() {
		register(RestEndpoint.class);
	}
}