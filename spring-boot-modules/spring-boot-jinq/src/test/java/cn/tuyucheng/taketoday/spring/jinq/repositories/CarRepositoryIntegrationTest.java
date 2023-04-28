package cn.tuyucheng.taketoday.spring.jinq.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CarRepositoryIntegrationTest {

	@Autowired
	private CarRepository repository;

	@Test
	public void givenACar_whenFilter_thenShouldBeFound() {
		assertThat(repository.findByModel("model1")
			.isPresent()).isFalse();
	}

	@Test
	public void givenACar_whenMultipleFilters_thenShouldBeFound() {
		assertThat(repository.findByModelAndDescription("model1", "desc")
			.isEmpty()).isTrue();
	}

	@Test
	public void whenUseASelectClause() {
		assertThat(repository.findWithModelYearAndEngine()
			.isEmpty()).isTrue();
	}

	@Test
	public void whenUsingOneToOneRelationship() {
		assertThat(repository.findManufacturerByModel("model1")).isNotNull();
	}
}
