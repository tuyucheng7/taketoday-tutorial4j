package cn.tuyucheng.taketoday.toggle;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

@Configuration
@EnableJpaRepositories("cn.tuyucheng.taketoday.toggle")
@EntityScan("cn.tuyucheng.taketoday.toggle")
public class ToggleConfiguration {

	@Bean
	public FeatureProvider featureProvider() {
		return new EnumBasedFeatureProvider(MyFeatures.class);
	}
}