package cn.tuyucheng.taketoday.springdatageode.app;

import cn.tuyucheng.taketoday.springdatageode.controller.AppController;
import cn.tuyucheng.taketoday.springdatageode.domain.Author;
import cn.tuyucheng.taketoday.springdatageode.repo.AuthorRepository;
import cn.tuyucheng.taketoday.springdatageode.service.AuthorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableIndexing;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@SpringBootApplication
@ClientCacheApplication(subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackageClasses = Author.class)
@EnableIndexing
@EnableGemfireRepositories(basePackageClasses = AuthorRepository.class)
@ComponentScan(basePackageClasses = {AppController.class, AuthorService.class})
@EnableClusterConfiguration(useHttp = true, requireHttps = false)
@EnableContinuousQueries
public class ClientCacheApp {

    public static void main(String[] args) {
        SpringApplication.run(ClientCacheApp.class, args);
    }
}