package cn.tuyucheng.taketoday.boot.bootstrapmode;

import cn.tuyucheng.taketoday.boot.bootstrapmode.domain.Todo;
import cn.tuyucheng.taketoday.boot.bootstrapmode.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BootstrapmodeDefaultIntegrationTest {

   @Autowired
   private TodoRepository todoRepository;

   @Test
   void givenBootstrapmodeValueIsDefault_whenCreatingTodo_shouldSuccess() {
      Todo todo = new Todo("Something to be done");

      assertThat(todoRepository.save(todo)).hasNoNullFieldsOrProperties();
   }
}
