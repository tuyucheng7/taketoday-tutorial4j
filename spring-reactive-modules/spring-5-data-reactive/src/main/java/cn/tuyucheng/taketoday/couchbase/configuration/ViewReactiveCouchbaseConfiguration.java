package cn.tuyucheng.taketoday.couchbase.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

@Configuration
@EnableReactiveCouchbaseRepositories("cn.tuyucheng.taketoday.couchbase.domain.repository.view")
public class ViewReactiveCouchbaseConfiguration extends ReactiveCouchbaseConfiguration {

	public ViewReactiveCouchbaseConfiguration(CouchbaseProperties couchbaseProperties) {
		super(couchbaseProperties);
	}
}