package cn.tuyucheng.taketoday.patterns.intercepting.filter.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class FilterChainImpl implements FilterChain {
	private Iterator<Filter> filters;

	public FilterChainImpl(Filter... filters) {
		this.filters = Arrays.asList(filters).iterator();
	}

	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response
	) throws IOException, ServletException {
		if (filters.hasNext()) {
			Filter filter = filters.next();
			filter.doFilter(request, response, this);
		}
	}
}
