package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.entity.Pet;
import cn.tuyucheng.taketoday.repository.PetRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class TestConnectionsConfiguration {

	@Bean
	ApplicationRunner neo4jRunner(PetRepository repository) {
		return args -> {
			var fido = repository.save(new Pet(null, "Fido"));
			System.out.println("fido:  " + fido);
		};
	}

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDBContainer() {
		return new MongoDBContainer("mongo:4.0.10");
	}
}