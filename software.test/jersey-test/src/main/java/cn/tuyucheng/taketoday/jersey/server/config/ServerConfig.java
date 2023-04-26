package cn.tuyucheng.taketoday.jersey.server.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/*")
public class ServerConfig extends ResourceConfig {

	public ServerConfig() {
		packages("cn.tuyucheng.taketoday.jersey.server");
	}
}