package cn.tuyucheng.taketoday.daopattern.application;

import cn.tuyucheng.taketoday.daopattern.config.JpaEntityManagerFactory;
import cn.tuyucheng.taketoday.daopattern.daos.Dao;
import cn.tuyucheng.taketoday.daopattern.daos.JpaUserDao;
import cn.tuyucheng.taketoday.daopattern.entities.User;
import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.Optional;

@ExcludeFromJacocoGeneratedReport
public class UserApplication {

	private static JpaUserDao jpaUserDao;

	public static void main(String[] args) {
		User user1 = getUser(1);
		System.out.println(user1);
		updateUser(user1, new String[]{"John", "john@domain.com"});
		saveUser(new User("Monica", "monica@domain.com"));
		deleteUser(getUser(2));
		getAllUsers().forEach(user -> System.out.println(user.getName()));
	}

	private static class JpaUserDaoHolder {
		private static final JpaUserDao jpaUserDao = new JpaUserDao(new JpaEntityManagerFactory().getEntityManager());
	}

	public static Dao getJpaUserDao() {
		return JpaUserDaoHolder.jpaUserDao;
	}

	public static User getUser(long id) {
		Optional<User> user = getJpaUserDao().get(id);
		return user.orElseGet(() -> new User("non-existing user", "no-email"));
	}

	public static List<User> getAllUsers() {
		return getJpaUserDao().getAll();
	}

	public static void updateUser(User user, String[] params) {
		getJpaUserDao().update(user, params);
	}

	public static void saveUser(User user) {
		getJpaUserDao().save(user);
	}

	public static void deleteUser(User user) {
		getJpaUserDao().delete(user);
	}
}