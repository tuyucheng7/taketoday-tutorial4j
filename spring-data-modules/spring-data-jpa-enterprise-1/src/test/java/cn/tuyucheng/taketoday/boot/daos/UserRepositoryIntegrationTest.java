package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext
class UserRepositoryIntegrationTest extends UserRepositoryCommon {

	@Test
	@Transactional
	void givenUsersInDBWhenUpdateStatusForNameModifyingQueryAnnotationNativeThenModifyMatchingUsers() {
		userRepository.save(new User("SAMPLE", LocalDate.now(), USER_EMAIL, ACTIVE_STATUS));
		userRepository.save(new User("SAMPLE1", LocalDate.now(), USER_EMAIL2, ACTIVE_STATUS));
		userRepository.save(new User("SAMPLE", LocalDate.now(), USER_EMAIL3, ACTIVE_STATUS));
		userRepository.save(new User("SAMPLE3", LocalDate.now(), USER_EMAIL4, ACTIVE_STATUS));
		userRepository.flush();

		int updatedUsersSize = userRepository.updateUserSetStatusForNameNative(INACTIVE_STATUS, "SAMPLE");

		assertThat(updatedUsersSize).isEqualTo(2);
	}
}