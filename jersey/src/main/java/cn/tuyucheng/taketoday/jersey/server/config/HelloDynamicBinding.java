package cn.tuyucheng.taketoday.jersey.server.config;

import cn.tuyucheng.taketoday.jersey.server.Greetings;
import cn.tuyucheng.taketoday.jersey.server.filter.ResponseServerFilter;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class HelloDynamicBinding implements DynamicFeature {

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {

		if (Greetings.class.equals(resourceInfo.getResourceClass()) && resourceInfo.getResourceMethod()
			.getName()
			.contains("HiGreeting")) {
			context.register(ResponseServerFilter.class);
		}

	}

}
