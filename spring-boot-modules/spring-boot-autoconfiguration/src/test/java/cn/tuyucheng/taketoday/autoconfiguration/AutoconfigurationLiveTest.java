package cn.tuyucheng.taketoday.autoconfiguration;

import cn.tuyucheng.taketoday.autoconfiguration.example.AutoconfigurationApplication;
import cn.tuyucheng.taketoday.autoconfiguration.example.MyUser;
import cn.tuyucheng.taketoday.autoconfiguration.example.MyUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AutoconfigurationApplication.class)
@EnableJpaRepositories(basePackages = {"cn.tuyucheng.taketoday.autoconfiguration.example"})
class AutoconfigurationLiveTest {

	@Autowired
	private MyUserRepository userRepository;

	@Test
	void whenSaveUser_thenOk() {
		MyUser user = new MyUser("user@email.com");
		userRepository.save(user);
	}
}