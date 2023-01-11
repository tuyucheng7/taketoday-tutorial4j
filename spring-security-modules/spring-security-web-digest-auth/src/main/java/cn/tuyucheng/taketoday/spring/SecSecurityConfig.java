package cn.tuyucheng.taketoday.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:webSecurityConfig.xml"})
@ComponentScan("cn.tuyucheng.taketoday.security")
public class SecSecurityConfig {

    public SecSecurityConfig() {
        super();
    }

}
