package cn.tuyucheng.taketoday.resilientapp;

import org.jobrunr.autoconfigure.JobRunrAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {JobRunrAutoConfiguration.class})
public class ResilientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResilientApplication.class, args);
	}
}