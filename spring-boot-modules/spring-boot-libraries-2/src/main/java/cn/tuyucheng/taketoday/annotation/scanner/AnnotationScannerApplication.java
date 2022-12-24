package cn.tuyucheng.taketoday.annotation.scanner;

import org.jobrunr.autoconfigure.JobRunrAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {JobRunrAutoConfiguration.class})
public class AnnotationScannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnotationScannerApplication.class, args);
	}
}