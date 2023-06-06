package cn.tuyucheng.taketoday.messaging.postgresql.config;

import java.util.Properties;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.tuyucheng.taketoday.messaging.postgresql.service.NotifierService;
import com.zaxxer.hikari.util.DriverDataSource;

@Configuration
public class NotifierConfiguration {

   @Bean
   NotifierService notifier(DataSourceProperties props) {
      DriverDataSource ds = new DriverDataSource(
            props.determineUrl(),
            props.determineDriverClassName(),
            new Properties(),
            props.determineUsername(),
            props.determinePassword());

      JdbcTemplate tpl = new JdbcTemplate(ds);

      return new NotifierService(tpl);
   }
}