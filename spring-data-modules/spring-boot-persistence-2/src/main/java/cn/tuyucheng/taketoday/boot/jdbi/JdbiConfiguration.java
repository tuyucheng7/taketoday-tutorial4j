package cn.tuyucheng.taketoday.boot.jdbi;

import java.util.List;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import cn.tuyucheng.taketoday.boot.jdbi.dao.CarMakerDao;
import cn.tuyucheng.taketoday.boot.jdbi.dao.CarModelDao;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JdbiConfiguration {
   @Bean
   public Jdbi jdbi(DataSource ds, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
      TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
      Jdbi jdbi = Jdbi.create(proxy);

      // Register all available plugins
      LOGGER.info("[I27] Installing plugins... ({} found)", jdbiPlugins.size());
      jdbiPlugins.forEach(jdbi::installPlugin);

      // Register all available rowMappers
      LOGGER.info("[I31] Installing rowMappers... ({} found)", rowMappers.size());
      rowMappers.forEach(jdbi::registerRowMapper);

      return jdbi;
   }

   @Bean
   public JdbiPlugin sqlObjectPlugin() {
      return new SqlObjectPlugin();
   }

   @Bean
   public CarMakerDao carMakerDao(Jdbi jdbi) {
      return jdbi.onDemand(CarMakerDao.class);
   }

   @Bean
   public CarModelDao carModelDao(Jdbi jdbi) {
      return jdbi.onDemand(CarModelDao.class);
   }
}