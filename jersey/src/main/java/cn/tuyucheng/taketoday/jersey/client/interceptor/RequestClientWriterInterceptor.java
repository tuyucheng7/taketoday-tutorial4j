package cn.tuyucheng.taketoday.jersey.client.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

@Provider
public class RequestClientWriterInterceptor implements WriterInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(RequestClientWriterInterceptor.class);

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		LOG.info("request writer interceptor in the client side");

		context.getOutputStream()
			.write(("Message added in the writer interceptor in the client side").getBytes());

		context.proceed();
	}

}
