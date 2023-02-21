package cn.tuyucheng.taketoday.camel.apache.file.cfg;

import cn.tuyucheng.taketoday.camel.apache.file.ContentBasedFileRouter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ContentBasedFileRouterConfig extends CamelConfiguration {

	@Bean
	ContentBasedFileRouter getContentBasedFileRouter() {
		return new ContentBasedFileRouter();
	}

	@Override
	public List<RouteBuilder> routes() {
		return Arrays.asList(getContentBasedFileRouter());
	}

}
