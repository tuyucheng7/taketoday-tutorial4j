package cn.tuyucheng.taketoday.onceperrequestfilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.onceperrequestfilter")
public class OncePerRequestFilterApp {

	public static void main(String[] args) {
		SpringApplication.run(OncePerRequestFilterApp.class, args);
	}
}