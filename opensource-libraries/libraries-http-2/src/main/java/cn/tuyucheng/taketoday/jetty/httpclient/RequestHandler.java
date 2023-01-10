package cn.tuyucheng.taketoday.jetty.httpclient;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestHandler extends AbstractHandler {

	@Override
	public void handle(String target, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		jettyRequest.setHandled(true);
		response.setContentType(request.getContentType());
		IO.copy(request.getInputStream(), response.getOutputStream());
	}
}