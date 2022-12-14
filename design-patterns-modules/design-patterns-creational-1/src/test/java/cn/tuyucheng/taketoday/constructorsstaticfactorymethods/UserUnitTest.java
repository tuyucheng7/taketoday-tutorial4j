package cn.tuyucheng.taketoday.constructorsstaticfactorymethods;

import cn.tuyucheng.taketoday.constructorsstaticfactorymethods.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUnitTest {

	@Test
	void givenUserClass_whenCalledcreateWithDefaultCountry_thenCorrect() {
		assertThat(User.createWithDefaultCountry("John", "john@domain.com")).isInstanceOf(User.class);
	}

	@Test
	void givenUserIntanceCreatedWithcreateWithDefaultCountry_whenCalledgetName_thenCorrect() {
		User user = User.createWithDefaultCountry("John", "john@domain.com");
		assertThat(user.getName()).isEqualTo("John");
	}

	@Test
	void givenUserIntanceCreatedWithcreateWithDefaultCountry_whenCalledgetEmail_thenCorrect() {
		User user = User.createWithDefaultCountry("John", "john@domain.com");
		assertThat(user.getEmail()).isEqualTo("john@domain.com");
	}

	@Test
	void givenUserIntanceCreatedWithcreateWithDefaultCountry_whenCalledgetCountry_thenCorrect() {
		User user = User.createWithDefaultCountry("John", "john@domain.com");
		assertThat(user.getCountry()).isEqualTo("Argentina");
	}

	@Test
	void givenUserInstanceCreatedWithcreateWithInstantiationTime_whenCalledcreateWithInstantiationTime_thenCorrect() {
		assertThat(User.createWithLoggedInstantiationTime("John", "john@domain.com", "Argentina")).isInstanceOf(User.class);
	}

	@Test
	void givenUserInstanceCreatedWithgetSingletonIntance_whenCalledgetSingletonInstance_thenCorrect() {
		User user1 = User.getSingletonInstance("John", "john@domain.com", "Argentina");
		User user2 = User.getSingletonInstance("John", "john@domain.com", "Argentina");
		assertThat(user1).isEqualTo(user2);
	}
}