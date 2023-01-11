package cn.tuyucheng.taketoday.serializeentityid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SerializeEntityIdApplication {

	private final PersonRepository repository;

	public SerializeEntityIdApplication(PersonRepository repository) {
		this.repository = repository;
	}

	@PostConstruct
	void onStart() {
		final Person person1 = new Person();
		person1.setName("John Doe");
		final Person person2 = new Person();
		person2.setName("Markus Boe");

		this.repository.save(person1);
		this.repository.save(person2);
	}

	public static void main(String[] args) {
		SpringApplication.run(SerializeEntityIdApplication.class, args);
	}
}