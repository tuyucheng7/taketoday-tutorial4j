package cn.tuyucheng.taketoday.enummapping.config;

import cn.tuyucheng.taketoday.enummapping.converters.StringToLevelConverter;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumMappingConfig implements WebMvcConfigurer {
	@Override
	public void addFormatters(FormatterRegistry registry) {
		ApplicationConversionService.configure(registry);
		registry.addConverter(new StringToLevelConverter());
	}
}