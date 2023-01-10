package cn.tuyucheng.taketoday.tomcat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by adi on 1/14/18.
 */
@WebFilter(urlPatterns = "/my-servlet/*")
public class MyFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("Filtering stuff...");
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.addHeader("myHeader", "myHeaderValue");
		chain.doFilter(request, httpResponse);
	}

	@Override
	public void destroy() {

	}
}
