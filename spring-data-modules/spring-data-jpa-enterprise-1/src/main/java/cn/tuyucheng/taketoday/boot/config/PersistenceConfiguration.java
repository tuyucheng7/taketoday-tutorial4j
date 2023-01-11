package cn.tuyucheng.taketoday.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.tuyucheng.taketoday.boot.services.BarService;
import cn.tuyucheng.taketoday.boot.services.impl.BarSpringDataJpaService;

@Configuration
@Profile("!tc")
@EnableTransactionManagement
@EnableJpaAuditing
public class PersistenceConfiguration {

    @Bean
    public BarService barSpringDataJpaService() {
        return new BarSpringDataJpaService();
    }
}