package cn.tuyucheng.taketoday.jersey.server.http;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import cn.tuyucheng.taketoday.jersey.server.config.ViewApplicationConfig;

public class EmbeddedHttpServer {

	public static final URI BASE_URI = URI.create("http://localhost:8082/");

	public static void main(String[] args) {
		try {
			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new ViewApplicationConfig(), false);

			Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

			server.start();

			System.out.println(String.format("Application started.\nTry out %s\nStop the application using CTRL+C", BASE_URI + "fruit"));
		} catch (IOException ex) {
			Logger.getLogger(EmbeddedHttpServer.class.getName())
				.log(Level.SEVERE, null, ex);
		}

	}

	public static HttpServer startServer(URI url) {
		final ResourceConfig rc = new ResourceConfig().packages("cn.tuyucheng.taketoday.jersey.server");
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(url.toString()), rc);
	}
}
