package cn.tuyucheng.taketoday.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"cn.tuyucheng.taketoday.persistence.dao", "cn.tuyucheng.taketoday.persistence.service"})
@ImportResource({"classpath:hibernate4Config.xml"})
public class PersistenceXmlConfig {

    public PersistenceXmlConfig() {
        super();
    }
}