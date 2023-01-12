package cn.tuyucheng.taketoday.jersey.server.filter;

import cn.tuyucheng.taketoday.jersey.server.config.HelloBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHORIZATION)
@HelloBinding
public class RestrictedOperationsRequestFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(RestrictedOperationsRequestFilter.class);

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		LOG.info("Restricted operations filter");
		if (ctx.getLanguage() != null && "EN".equals(ctx.getLanguage()
			.getLanguage())) {
			LOG.info("Aborting request");
			ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
				.entity("Cannot access")
				.build());
		}

	}
}
