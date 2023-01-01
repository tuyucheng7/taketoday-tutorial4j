package cn.tuyucheng.taketoday.annotation.servletcomponentscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * using the following annotations are equivalent:
 * <ul><li>
 * <code>@ServletComponentScan</code>
 * </li><li>
 * <code>@ServletComponentScan(basePackages = "cn.tuyucheng.taketoday.annotation.servletcomponentscan.components")</code>
 * </li><li>
 * <code>@ServletComponentScan(basePackageClasses = {AttrListener.class, HelloFilter.class, HelloServlet.class, EchoServlet.class})</code>
 * </li></ul>
 */
@SpringBootApplication
@ServletComponentScan("cn.tuyucheng.taketoday.annotation.servletcomponentscan.components")
public class SpringBootAnnotatedApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAnnotatedApp.class, args);
	}
}