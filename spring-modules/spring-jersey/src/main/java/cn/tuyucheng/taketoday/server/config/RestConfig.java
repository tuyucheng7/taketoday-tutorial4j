package cn.tuyucheng.taketoday.server.config;

import cn.tuyucheng.taketoday.server.exception.AlreadyExistsExceptionHandler;
import cn.tuyucheng.taketoday.server.exception.NotFoundExceptionHandler;
import cn.tuyucheng.taketoday.server.rest.EmployeeResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/resources")
public class RestConfig extends Application {
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(EmployeeResource.class, NotFoundExceptionHandler.class, AlreadyExistsExceptionHandler.class));
	}
}