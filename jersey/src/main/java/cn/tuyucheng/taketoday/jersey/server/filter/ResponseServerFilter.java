package cn.tuyucheng.taketoday.jersey.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class ResponseServerFilter implements ContainerResponseFilter {

	private static final Logger LOG = LoggerFactory.getLogger(ResponseServerFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		LOG.info("Response server filter");

		responseContext.getHeaders()
			.add("X-Test", "Filter test");
	}
}
