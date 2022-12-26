package cn.tuyucheng.taketoday.maxhttpheadersize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("cn.tuyucheng.taketoday.maxhttpheadersize")
public class MaxHttpHeaderSizeApplication {

	public static void main(final String[] args) {
		SpringApplication.run(MaxHttpHeaderSizeApplication.class, args);
	}
}