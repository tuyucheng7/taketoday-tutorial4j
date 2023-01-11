package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.entity.Fruit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FruitRepositoryIntegrationTest {

	@Autowired
	private FruitRepository fruitRepository;

	@Transactional
	@Test
	@Sql(scripts = {"/test-fruit-data.sql"})
	void givenFruits_WhenDeletedByColor_ThenDeletedFruitsShouldReturn() {
		List<Fruit> fruits = fruitRepository.deleteByColor("green");

		assertEquals(2, fruits.size(), "number of fruits are not matching");
		fruits.forEach(fruit -> assertEquals("green", fruit.getColor(), "Its not a green fruit"));
	}

	@Transactional
	@Test
	@Sql(scripts = {"/test-fruit-data.sql"})
	void givenFruits_WhenDeletedByName_ThenDeletedFruitCountShouldReturn() {
		Long deletedFruitCount = fruitRepository.deleteByName("apple");

		assertEquals(1, deletedFruitCount.intValue(), "deleted fruit count is not matching");
	}

	@Transactional
	@Test
	@Sql(scripts = {"/test-fruit-data.sql"})
	void givenFruits_WhenRemovedByColor_ThenDeletedFruitsShouldReturn() {
		List<Fruit> fruits = fruitRepository.removeByColor("green");

		assertEquals(2, fruits.size(), "number of fruits are not matching");
		fruits.forEach(fruit -> assertEquals("green", fruit.getColor(), "Its not a green fruit"));
	}

	@Transactional
	@Test
	@Sql(scripts = {"/test-fruit-data.sql"})
	void givenFruits_WhenRemovedByName_ThenDeletedFruitCountShouldReturn() {
		Long deletedFruitCount = fruitRepository.removeByName("apple");

		assertEquals(1, deletedFruitCount.intValue(), "deleted fruit count is not matching");
	}

	@Transactional
	@Test
	@Sql(scripts = {"/test-fruit-data.sql"})
	void givenFruits_WhenDeletedByColorOrName_ThenDeletedFruitsShouldReturn() {
		int deletedCount = fruitRepository.deleteFruits("apple", "green");

		assertEquals(3, deletedCount, "number of fruits are not matching");
	}
}