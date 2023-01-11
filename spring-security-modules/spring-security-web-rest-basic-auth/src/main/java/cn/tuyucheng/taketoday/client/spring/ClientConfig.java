package cn.tuyucheng.taketoday.client.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.client")
public class ClientConfig {

    public ClientConfig() {
        super();
    }
    // beans
}