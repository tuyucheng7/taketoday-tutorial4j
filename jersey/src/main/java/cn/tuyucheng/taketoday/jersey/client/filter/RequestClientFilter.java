package cn.tuyucheng.taketoday.jersey.client.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class RequestClientFilter implements ClientRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(RequestClientFilter.class);

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOG.info("Request client filter");

		requestContext.setProperty("test", "test client request filter");
	}

}
