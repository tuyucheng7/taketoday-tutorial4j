package cn.tuyucheng.taketoday.nopropertyfound.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

   @Bean
   @ConfigurationProperties(prefix = "h2.datasource")
   public DataSource dataSource() {
      return DataSourceBuilder.create()
            .build();
   }
}