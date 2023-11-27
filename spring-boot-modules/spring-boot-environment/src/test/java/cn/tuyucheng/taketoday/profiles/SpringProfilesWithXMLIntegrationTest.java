package cn.tuyucheng.taketoday.profiles;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SpringProfilesWithXMLIntegrationTest {

   private ClassPathXmlApplicationContext classPathXmlApplicationContext;

   @Test
   void testSpringProfilesForDevEnvironment() {
      classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:springProfiles-config.xml");
      final ConfigurableEnvironment configurableEnvironment = classPathXmlApplicationContext.getEnvironment();
      configurableEnvironment.setActiveProfiles("dev");
      classPathXmlApplicationContext.refresh();
      final DatasourceConfig datasourceConfig = classPathXmlApplicationContext.getBean("devDatasourceConfig", DatasourceConfig.class);

      assertInstanceOf(DevDatasourceConfig.class, datasourceConfig);
   }

   @Test
   void testSpringProfilesForProdEnvironment() {
      classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:springProfiles-config.xml");
      final ConfigurableEnvironment configurableEnvironment = classPathXmlApplicationContext.getEnvironment();
      configurableEnvironment.setActiveProfiles("production");
      classPathXmlApplicationContext.refresh();
      final DatasourceConfig datasourceConfig = classPathXmlApplicationContext.getBean("productionDatasourceConfig", DatasourceConfig.class);

      assertInstanceOf(ProductionDatasourceConfig.class, datasourceConfig);
   }
}