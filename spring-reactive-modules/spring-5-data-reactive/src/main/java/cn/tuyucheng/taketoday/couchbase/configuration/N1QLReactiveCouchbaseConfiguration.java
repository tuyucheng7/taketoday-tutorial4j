package cn.tuyucheng.taketoday.couchbase.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

@Configuration
@EnableReactiveCouchbaseRepositories("cn.tuyucheng.taketoday.couchbase.domain.repository.n1ql")
@Primary
public class N1QLReactiveCouchbaseConfiguration extends ReactiveCouchbaseConfiguration {

	public N1QLReactiveCouchbaseConfiguration(CouchbaseProperties couchbaseProperties) {
		super(couchbaseProperties);
	}
}