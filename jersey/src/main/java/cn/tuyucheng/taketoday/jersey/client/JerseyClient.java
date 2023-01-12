package cn.tuyucheng.taketoday.jersey.client;

import cn.tuyucheng.taketoday.jersey.client.filter.RequestClientFilter;
import cn.tuyucheng.taketoday.jersey.client.filter.ResponseClientFilter;
import cn.tuyucheng.taketoday.jersey.client.interceptor.RequestClientWriterInterceptor;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class JerseyClient {

	private static final String URI_GREETINGS = "http://localhost:8080/jersey/greetings";

	public static String getHelloGreeting() {
		return createClient().target(URI_GREETINGS)
			.request()
			.get(String.class);
	}

	public static String getHiGreeting() {
		return createClient().target(URI_GREETINGS + "/hi")
			.request()
			.get(String.class);
	}

	public static Response getCustomGreeting() {
		return createClient().target(URI_GREETINGS + "/custom")
			.request()
			.post(Entity.text("custom"));
	}

	private static Client createClient() {
		ClientConfig config = new ClientConfig();
		config.register(RequestClientFilter.class);
		config.register(ResponseClientFilter.class);
		config.register(RequestClientWriterInterceptor.class);

		return ClientBuilder.newClient(config);
	}

}
