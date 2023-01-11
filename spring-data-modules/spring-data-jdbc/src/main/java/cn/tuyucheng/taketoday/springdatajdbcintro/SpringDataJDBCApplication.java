package cn.tuyucheng.taketoday.springdatajdbcintro;

import ch.qos.logback.classic.Logger;
import cn.tuyucheng.taketoday.springdatajdbcintro.entity.Person;
import cn.tuyucheng.taketoday.springdatajdbcintro.repository.PersonRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

import java.util.Optional;

@SpringBootApplication
public class SpringDataJDBCApplication implements CommandLineRunner {

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(SpringDataJDBCApplication.class);

	@Autowired
	private PersonRepository repository;
	@Autowired
	private DatabaseSeeder dbSeeder;

	public static void main(String[] args) {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "jdbcintro");
		SpringApplication.run(SpringDataJDBCApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		LOGGER.info("@@ Inserting Data....");
		dbSeeder.insertData();

		LOGGER.info("@@ findAll() call...");
		repository.findAll().forEach(person -> LOGGER.info(person.toString()));

		LOGGER.info("@@ findById() call...");
		Optional<Person> optionalPerson = repository.findById(1L);
		optionalPerson.ifPresent(person -> LOGGER.info(person.toString()));

		LOGGER.info("@@ save() call...");
		Person newPerson = new Person("Franz", "Kafka");
		Person result = repository.save(newPerson);

		LOGGER.info(result.toString());
		LOGGER.info("@@ delete");
		optionalPerson.ifPresent(person -> repository.delete(person));

		LOGGER.info("@@ findAll() call...");
		repository.findAll().forEach(person -> LOGGER.info(person.toString()));
		LOGGER.info("@@ findByFirstName() call...");

		repository.findByFirstName("Franz").forEach(person -> LOGGER.info(person.toString()));
		LOGGER.info("@@ updateByFirstName() call...");

		repository.updateByFirstName(2L, "Date Inferno");
		repository.findAll().forEach(person -> LOGGER.info(person.toString()));
	}
}