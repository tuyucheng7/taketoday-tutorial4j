package cn.tuyucheng.taketoday.crud;

import cn.tuyucheng.taketoday.crud.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUnitTest {

    @Test
    void whenCalledGetName_thenCorrect() {
        User user = new User("Julie", "julie@domain.com");

        assertThat(user.getName()).isEqualTo("Julie");
    }

    @Test
    void whenCalledGetEmail_thenCorrect() {
        User user = new User("Julie", "julie@domain.com");

        assertThat(user.getEmail()).isEqualTo("julie@domain.com");
    }

    @Test
    void whenCalledSetName_thenCorrect() {
        User user = new User("Julie", "julie@domain.com");

        user.setName("John");

        assertThat(user.getName()).isEqualTo("John");
    }

    @Test
    void whenCalledSetEmail_thenCorrect() {
        User user = new User("Julie", "julie@domain.com");

        user.setEmail("john@domain.com");

        assertThat(user.getEmail()).isEqualTo("john@domain.com");
    }

    @Test
    void whenCalledtoString_thenCorrect() {
        User user = new User("Julie", "julie@domain.com");
        assertThat(user.toString()).isEqualTo("User{id=0, name=Julie, email=julie@domain.com}");
    }
}