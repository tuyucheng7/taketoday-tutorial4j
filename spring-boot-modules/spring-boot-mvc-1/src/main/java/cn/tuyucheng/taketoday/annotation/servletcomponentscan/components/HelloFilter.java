package cn.tuyucheng.taketoday.annotation.servletcomponentscan.components;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

@WebFilter(urlPatterns = "/hello", description = "a filter for hello servlet", initParams = {@WebInitParam(name = "msg", value = "filtering ")}, filterName = "hello filter", servletNames = {"echo servlet"})
public class HelloFilter implements Filter {

	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("filter init");
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		servletResponse.getOutputStream().print(filterConfig.getInitParameter("msg"));
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
		System.out.println("filter destroy");
	}
}