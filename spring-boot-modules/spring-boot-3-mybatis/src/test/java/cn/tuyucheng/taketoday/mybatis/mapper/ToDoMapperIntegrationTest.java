package cn.tuyucheng.taketoday.mybatis.mapper;

import cn.tuyucheng.taketoday.mybatis.model.TODO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
public class ToDoMapperIntegrationTest {

	@Autowired
	private ToDoMapper todoMapper;

	@BeforeEach
	void setUp() {
		Stream.of(new TODO(1L, "TITLE 1", "BODY 1"),
				new TODO(2L, "TITLE 2", "BODY 2"),
				new TODO(3L, "TITLE 3", "BODY 3"))
			.forEach(todoMapper::createNew);
	}

	@Test
	void whenFindAllTodo_thenResultSizeIsCorrect() {
		List<TODO> todoList = todoMapper.findAll();
		assertThat(todoList).hasSize(3);
	}

	@Test
	void whenFindById_thenReturnTodo() {
		TODO resultTodo = todoMapper.findById(1L);

		assertThat(resultTodo.getTitle()).isEqualTo("TITLE 1");
		assertThat(resultTodo.getBody()).isEqualTo("BODY 1");
	}

	@Test
	void whenSaveNewTodo_thenResultIdShouldCorrect() {
		TODO newTodo = new TODO(4L, "TITLE 4", "BODY 4");
		int saveFlag = todoMapper.createNew(newTodo);

		assertThat(saveFlag).isEqualTo(1);
	}

	@Test
	void whenDeleteByIdCalled_thenSizeShouldCorrect() {
		int deletedFlag = todoMapper.deleteById(3L);

		assertThat(deletedFlag).isEqualTo(1);
		assertThat(todoMapper.findAll()).hasSize(2);
	}

	@Test
	void whenUpdateTodoWithId4_thenShouldCorrect() {
		TODO updatedTodo = new TODO(3L, "TITLE 3 UPDATED", "BODY 3 UPDATED");
		int updateFlag = todoMapper.update(updatedTodo);

		assertThat(updateFlag).isEqualTo(1);
		assertThat(todoMapper.findById(3L).getTitle()).isEqualTo("TITLE 3 UPDATED");
	}

	@AfterEach
	void tearDown() {
		todoMapper.deleteAll();
	}
}