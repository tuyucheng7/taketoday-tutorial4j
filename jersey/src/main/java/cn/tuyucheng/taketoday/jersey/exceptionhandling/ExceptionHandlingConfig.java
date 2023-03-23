package cn.tuyucheng.taketoday.jersey.exceptionhandling;

import org.glassfish.jersey.server.ResourceConfig;

import cn.tuyucheng.taketoday.jersey.exceptionhandling.rest.exceptions.IllegalArgumentExceptionMapper;
import cn.tuyucheng.taketoday.jersey.exceptionhandling.rest.exceptions.ServerExceptionMapper;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/exception-handling/*")
public class ExceptionHandlingConfig extends ResourceConfig {

	public ExceptionHandlingConfig() {
		packages("cn.tuyucheng.taketoday.jersey.exceptionhandling.rest");
		register(IllegalArgumentExceptionMapper.class);
		register(ServerExceptionMapper.class);
	}

}
