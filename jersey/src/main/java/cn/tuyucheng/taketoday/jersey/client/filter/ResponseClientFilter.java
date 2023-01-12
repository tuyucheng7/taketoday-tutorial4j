package cn.tuyucheng.taketoday.jersey.client.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ResponseClientFilter implements ClientResponseFilter {

	private static final Logger LOG = LoggerFactory.getLogger(ResponseClientFilter.class);

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		LOG.info("Response client filter");

		responseContext.getHeaders()
			.add("X-Test-Client", "Test response client filter");
	}

}
