package cn.tuyucheng.taketoday.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// @Configuration
@EnableTransactionManagement
@ComponentScan({"cn.tuyucheng.taketoday.persistence"})
@ImportResource({"classpath:jpaConfig.xml"})
public class PersistenceJPAConfigXml {

   public PersistenceJPAConfigXml() {
      super();
   }
}