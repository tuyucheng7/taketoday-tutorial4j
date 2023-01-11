package cn.tuyucheng.taketoday.springmultipledatasources;

import cn.tuyucheng.taketoday.springmultipledatasources.todos.Todo;
import cn.tuyucheng.taketoday.springmultipledatasources.todos.TodoRepository;
import cn.tuyucheng.taketoday.springmultipledatasources.topics.TopicRepository;
import cn.tuyucheng.taketoday.springmultipledatasources.topics.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("multipledatasources")
@DataJpaTest // no test database!
class MultipleDatasourcesIntegrationTest {

	@Autowired
	TodoRepository todoRepo;
	@Autowired
	TopicRepository topicRepo;

	@Test
	void shouldSaveTodoToTodoDB() {
		Todo todo = new Todo("test");
		Todo saved = todoRepo.save(todo);
		Optional<Todo> result = todoRepo.findById(saved.getId());
		assertThat(result).isPresent();
	}

	@Test
	void shouldSaveTopicToTopicDB() {
		Topic todo = new Topic("test");
		Topic saved = topicRepo.save(todo);
		Optional<Topic> result = topicRepo.findById(saved.getId());
		assertThat(result).isPresent();
	}
}