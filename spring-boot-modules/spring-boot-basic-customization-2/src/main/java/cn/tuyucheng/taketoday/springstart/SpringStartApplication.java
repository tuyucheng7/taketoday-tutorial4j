package cn.tuyucheng.taketoday.springstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class, JvmMetricsAutoConfiguration.class,
//        LogbackMetricsAutoConfiguration.class, MetricsAutoConfiguration.class})
public class SpringStartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringStartApplication.class, args);
	}
}