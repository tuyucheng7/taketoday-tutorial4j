package cn.tuyucheng.taketoday.exists;

import cn.tuyucheng.taketoday.QueryApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QueryApplication.class})
class CarRepositoryIntegrationTest {

	@Autowired
	private CarRepository repository;
	private int searchId;

	@BeforeEach
	void setup() {
		List<Car> cars = repository.saveAll(Arrays.asList(new Car(200, "BMW"), new Car(300, "Audi")));
		searchId = cars.get(0).getId();
	}

	@AfterEach
	void teardown() {
		repository.deleteAll();
	}

	@Test
	void whenIdIsCorrect_thenExistsShouldReturnTrue() {
		assertThat(repository.existsById(searchId)).isTrue();
	}

	@Test
	void givenExample_whenExists_thenIsTrue() {
		ExampleMatcher modelMatcher = ExampleMatcher.matching()
				.withIgnorePaths("id") // must explicitly ignore -> PK
				.withMatcher("model", ignoreCase());
		Car probe = new Car();
		probe.setModel("bmw");

		Example<Car> example = Example.of(probe, modelMatcher);

		assertThat(repository.exists(example)).isTrue();
	}

	@Test
	void givenPower_whenExists_thenIsFalse() {
		assertThat(repository.existsCarByPower(200)).isTrue();
		assertThat(repository.existsCarByPower(800)).isFalse();
	}

	@Test
	void existsByDerivedQuery_byModel() {
		assertThat(repository.existsCarByModel("Audi")).isTrue();
		assertThat(repository.existsCarByModel("audi")).isFalse();
		assertThat(repository.existsCarByModel("AUDI")).isFalse();
		assertThat(repository.existsCarByModel("")).isFalse();
	}

	@Test
	void givenModelName_whenExistsExact_thenIsTrue() {
		assertThat(repository.existsCarExactCustomQuery("BMW")).isTrue();
		assertThat(repository.existsCarExactCustomQuery("Bmw")).isFalse();
		assertThat(repository.existsCarExactCustomQuery("bmw")).isFalse();
		assertThat(repository.existsCarExactCustomQuery("")).isFalse();
	}

	@Test
	void givenModelName_whenExistsLike_thenIsTrue() {
		assertThat(repository.existsCarLikeCustomQuery("BMW")).isTrue();
		assertThat(repository.existsCarLikeCustomQuery("Bmw")).isTrue();
		assertThat(repository.existsCarLikeCustomQuery("bmw")).isTrue();
		assertThat(repository.existsCarLikeCustomQuery("")).isFalse();
	}
}