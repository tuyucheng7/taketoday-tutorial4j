package cn.tuyucheng.taketoday.jersey.server.config;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class ServerConfig extends ResourceConfig {

	public ServerConfig() {
		packages("cn.tuyucheng.taketoday.jersey.server");
	}

}
