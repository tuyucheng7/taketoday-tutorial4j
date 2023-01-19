package cn.tuyucheng.taketoday.daopattern.test;

import cn.tuyucheng.taketoday.daopattern.daos.UserDao;
import cn.tuyucheng.taketoday.daopattern.entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoUnitTest {

	private static UserDao userDao;


	@BeforeAll
	static void setUpUserDaoInstance() {
		userDao = new UserDao();
	}

	@Test
	void givenUserDaoInstance_whenCalledget_thenOneAssertion() {
		assertThat(userDao.get(0)).isInstanceOf(Optional.class);
	}

	@Test
	void givenUserDaoInstance_whenCalledgetAll_thenOneAssertion() {
		assertThat(userDao.getAll()).isInstanceOf(List.class);
	}

	@Test
	void givenUserDaoInstance_whenCalledupdate_thenTwoAssertions() {
		User user = new User("Julie", "julie@domain.com");
		userDao.update(user, new String[]{"Julie", "julie@domain.com"});
		assertThat(userDao.get(2).get().getName()).isEqualTo("Julie");
		assertThat(userDao.get(2).get().getEmail()).isEqualTo("julie@domain.com");
	}

	@Test
	void givenUserDaoInstance_whenCalledsave_thenTwoAssertions() {
		User user = new User("Julie", "julie@domain.com");
		userDao.save(user);
		assertThat(userDao.get(2).get().getName()).isEqualTo("Julie");
		assertThat(userDao.get(2).get().getEmail()).isEqualTo("julie@domain.com");
	}

	@Test
	void givenUserDaoInstance_whenCalleddelete_thenOneAssertion() {
		User user = new User("Julie", "julie@domain.com");
		userDao.delete(user);
		assertThat(userDao.getAll()).hasSize(2);
	}
}