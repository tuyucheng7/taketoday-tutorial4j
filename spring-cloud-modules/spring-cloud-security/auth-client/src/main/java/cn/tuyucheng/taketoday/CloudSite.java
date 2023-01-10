package cn.tuyucheng.taketoday;


import cn.tuyucheng.taketoday.filters.SimpleFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class CloudSite {
	public static void main(String[] args) {
		SpringApplication.run(CloudSite.class, args);
	}

	@Bean
	public SimpleFilter simpleFilter() {
		return new SimpleFilter();
	}

}
