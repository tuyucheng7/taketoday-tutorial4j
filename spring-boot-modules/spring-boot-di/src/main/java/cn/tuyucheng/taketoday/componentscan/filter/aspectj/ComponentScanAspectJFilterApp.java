package cn.tuyucheng.taketoday.componentscan.filter.aspectj;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ,
      pattern = "cn.tuyucheng.taketoday.componentscan.filter.aspectj.* "
            + "&& !(cn.tuyucheng.taketoday.componentscan.filter.aspectj.L* "
            + "|| cn.tuyucheng.taketoday.componentscan.filter.aspectj.C*)"))
public class ComponentScanAspectJFilterApp {

    public static void main(String[] args) {
    }
}