package cn.tuyucheng.taketoday.annotation.servletcomponentscan;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.annotation.servletcomponentscan.components")
public class SpringBootPlainApp {

	public static void main(String[] args) {
	}
}