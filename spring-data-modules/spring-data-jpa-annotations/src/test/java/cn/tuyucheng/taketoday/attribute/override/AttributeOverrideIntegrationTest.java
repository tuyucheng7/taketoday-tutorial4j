package cn.tuyucheng.taketoday.attribute.override;

import cn.tuyucheng.taketoday.Application;
import cn.tuyucheng.taketoday.attribute.override.entity.Address;
import cn.tuyucheng.taketoday.attribute.override.entity.Brand;
import cn.tuyucheng.taketoday.attribute.override.entity.Car;
import cn.tuyucheng.taketoday.attribute.override.repository.CarRepository;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
class AttributeOverrideIntegrationTest {

	private static final LocalDate FORD_FOUNDATION_DATE = LocalDate.parse("1903-06-16");

	@Autowired
	CarRepository carRepository;

	@Test
	@Transactional
	void whenInsertingCar_thenEmbeddedAndMappedFieldsArePopulated() {

		Car fordMustang = createMustang();

		carRepository.save(fordMustang);
		Car actualCar = carRepository.getOne(fordMustang.getId());

		Assertions.assertThat(actualCar).isEqualTo(fordMustang);
	}

	@NotNull
	private Car createMustang() {
		Address address = new Address();
		address.setName("Ford United States");
		address.setCity("Dearborn");

		Brand ford = new Brand();
		ford.setName("Ford");
		ford.setFoundationDate(FORD_FOUNDATION_DATE);

		Car fordMustang = new Car();
		fordMustang.setIdentifier("WP1AB29P88LA47599");
		fordMustang.setModel("Ford");
		fordMustang.setName("My car");
		fordMustang.setBrand(ford);
		return fordMustang;
	}
}