package cn.tuyucheng.taketoday.scribejava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication
@ServletComponentScan
public class ScribejavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScribejavaApplication.class, args);
	}


}
