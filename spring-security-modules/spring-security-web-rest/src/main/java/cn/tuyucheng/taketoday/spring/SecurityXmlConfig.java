package cn.tuyucheng.taketoday.spring;

import org.springframework.context.annotation.ComponentScan;

// @Configuration
// @ImportResource({ "classpath:webSecurityConfig.xml" })
@ComponentScan("cn.tuyucheng.taketoday.security")
public class SecurityXmlConfig {

    public SecurityXmlConfig() {
        super();
    }

}
