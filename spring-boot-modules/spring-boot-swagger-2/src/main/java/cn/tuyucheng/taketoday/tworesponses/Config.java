package cn.tuyucheng.taketoday.tworesponses;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ExcludeFromJacocoGeneratedReport
public class Config {

	@Bean
	SpringDocConfiguration springDocConfiguration() {
		return new SpringDocConfiguration();
	}

	@Bean
	SpringDocConfigProperties springDocConfigProperties() {
		return new SpringDocConfigProperties();
	}

	@Bean
	ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties) {
		return new ObjectMapperProvider(springDocConfigProperties);
	}
}