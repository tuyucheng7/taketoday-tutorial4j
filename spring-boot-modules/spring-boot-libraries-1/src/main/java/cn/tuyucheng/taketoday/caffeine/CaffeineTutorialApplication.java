package cn.tuyucheng.taketoday.caffeine;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CaffeineTutorialApplication {


	public static void main(String[] args) {
		new SpringApplicationBuilder(CaffeineTutorialApplication.class).run(args);
	}
}