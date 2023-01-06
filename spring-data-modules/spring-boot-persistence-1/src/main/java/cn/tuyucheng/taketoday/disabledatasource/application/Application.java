package cn.tuyucheng.taketoday.disabledatasource.application;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ExcludeFromJacocoGeneratedReport
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}