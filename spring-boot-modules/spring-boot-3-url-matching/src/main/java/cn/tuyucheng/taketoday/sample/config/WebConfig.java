package cn.tuyucheng.taketoday.sample.config;

import cn.tuyucheng.taketoday.sample.filters.TrailingSlashRedirectFilterReactive;
import jakarta.servlet.Filter;
import org.springframework.web.server.WebFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.tuyucheng.taketoday.sample.filters.TrailingSlashRedirectFilter;

@Configuration
public class WebConfig {

	@Bean
	public WebFilter trailingSlashRedirectReactiveFilter() {
		return new TrailingSlashRedirectFilterReactive();
	}

	@Bean
	public Filter trailingSlashRedirectFilter() {
		return new TrailingSlashRedirectFilter();
	}

	@Bean
	public FilterRegistrationBean<Filter> trailingSlashFilter() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(trailingSlashRedirectFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}