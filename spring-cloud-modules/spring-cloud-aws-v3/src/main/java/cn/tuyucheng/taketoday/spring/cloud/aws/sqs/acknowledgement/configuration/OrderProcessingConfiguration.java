package cn.tuyucheng.taketoday.spring.cloud.aws.sqs.acknowledgement.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({EventsQueuesProperties.class, ProductIdProperties.class})
@Configuration
public class OrderProcessingConfiguration {
}