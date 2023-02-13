package cn.tuyucheng.taketoday.typeconversion.config;

import cn.tuyucheng.taketoday.typeconversion.converter.GenericBigDecimalConverter;
import cn.tuyucheng.taketoday.typeconversion.converter.StringToEmployeeConverter;
import cn.tuyucheng.taketoday.typeconversion.converter.StringToEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToEmployeeConverter());
		registry.addConverterFactory(new StringToEnumConverterFactory());
		registry.addConverter(new GenericBigDecimalConverter());
	}
}