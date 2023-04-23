package cn.tuyucheng.taketoday.mybatis.spring;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.mybatis"}, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {PersistenceConfig.class})
})
public class PersistenceAutoConfig {
}