package cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor;

import cn.tuyucheng.taketoday.autoconfiguration.MySQLAutoconfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration(exclude = {MySQLAutoconfiguration.class})
@ComponentScan(basePackageClasses = {DatabaseProperties.class})
public class AnnotationProcessorApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AnnotationProcessorApplication.class).run();
	}
}