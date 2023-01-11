package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.entity.Fruit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FruitPopulatorIntegrationTest {

	@Autowired
	private FruitRepository fruitRepository;

	@Test
	void givenFruitJsonPopulatorThenShouldInsertRecordOnStart() {

		List<Fruit> fruits = fruitRepository.findAll();
		assertEquals(2, fruits.size(), "record count is not matching");

		fruits.forEach(fruit -> {
			if (1 == fruit.getId()) {
				assertEquals("apple", fruit.getName());
				assertEquals("red", fruit.getColor());
			} else if (2 == fruit.getId()) {
				assertEquals("guava", fruit.getName());
				assertEquals("green", fruit.getColor());
			}
		});
	}
}