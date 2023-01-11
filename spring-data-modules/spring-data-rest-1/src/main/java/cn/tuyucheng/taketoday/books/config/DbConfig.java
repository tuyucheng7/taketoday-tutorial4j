package cn.tuyucheng.taketoday.books.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "cn.tuyucheng.taketoday.books.repositories")
// @PropertySource("persistence-h2.properties")
// @PropertySource("persistence-hsqldb.properties")
// @PropertySource("persistence-derby.properties")
// @PropertySource("persistence-sqlite.properties")
public class DbConfig {

    public static final String HIBERNATE_HBM_2_DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("driverClassName")));
        dataSource.setUrl(env.getProperty("url"));
        dataSource.setUsername(env.getProperty("user"));
        dataSource.setPassword(env.getProperty("password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("cn.tuyucheng.taketoday.books.models");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(additionalProperties());
        return em;
    }

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        if (env.getProperty(HIBERNATE_HBM_2_DDL_AUTO) != null) {
            hibernateProperties.setProperty(HIBERNATE_HBM_2_DDL_AUTO, env.getProperty(HIBERNATE_HBM_2_DDL_AUTO));
        }
        if (env.getProperty(HIBERNATE_DIALECT) != null) {
            hibernateProperties.setProperty(HIBERNATE_DIALECT, env.getProperty(HIBERNATE_DIALECT));
        }
        if (env.getProperty(HIBERNATE_SHOW_SQL) != null) {
            hibernateProperties.setProperty(HIBERNATE_SHOW_SQL, env.getProperty(HIBERNATE_SHOW_SQL));
        }
        return hibernateProperties;
    }
}

@Configuration
@Profile("h2")
@PropertySource("classpath:persistence-h2.properties")
class H2Config {
}

@Configuration
@Profile("hsqldb")
@PropertySource("classpath:persistence-hsqldb.properties")
class HsqldbConfig {
}

@Configuration
@Profile("derby")
@PropertySource("classpath:persistence-derby.properties")
class DerbyConfig {
}

@Configuration
@Profile("sqlite")
@PropertySource("classpath:persistence-sqlite.properties")
class SqliteConfig {
}