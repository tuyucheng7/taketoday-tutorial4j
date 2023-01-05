package cn.tuyucheng.taketoday.vertxspring;

import cn.tuyucheng.taketoday.vertxspring.verticles.ArticleRecipientVerticle;
import cn.tuyucheng.taketoday.vertxspring.verticles.ServerVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configuration
@EnableJpaRepositories("cn.tuyucheng.taketoday.vertxspring.repository")
@EntityScan("cn.tuyucheng.taketoday.vertxspring.entity")
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday"})
public class VertxSpringApplication {

	@Autowired
	private ServerVerticle serverVerticle;

	@Autowired
	private ArticleRecipientVerticle serviceVerticle;

	public static void main(String[] args) {
		SpringApplication.run(VertxSpringApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		final Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(serverVerticle);
		vertx.deployVerticle(serviceVerticle);
	}
}