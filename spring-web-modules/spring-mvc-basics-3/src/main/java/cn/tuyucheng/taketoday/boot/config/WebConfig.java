package cn.tuyucheng.taketoday.boot.config;

import java.util.List;

import cn.tuyucheng.taketoday.boot.converter.GenericBigDecimalConverter;
import cn.tuyucheng.taketoday.boot.converter.StringToAbstractEntityConverterFactory;
import cn.tuyucheng.taketoday.boot.converter.StringToEmployeeConverter;
import cn.tuyucheng.taketoday.boot.converter.StringToEnumConverter;
import cn.tuyucheng.taketoday.boot.web.resolver.HeaderVersionArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new HeaderVersionArgumentResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEmployeeConverter());
        registry.addConverter(new StringToEnumConverter());
        registry.addConverterFactory(new StringToAbstractEntityConverterFactory());
        registry.addConverter(new GenericBigDecimalConverter());
    }
}
