package cn.tuyucheng.taketoday.guava;

import cn.tuyucheng.taketoday.guava.entity.Administrator;
import cn.tuyucheng.taketoday.guava.entity.Player;
import cn.tuyucheng.taketoday.guava.entity.User;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class MoreObjectsUnitTest {

	@Test
	public void whenToString_shouldIncludeAllFields() throws Exception {
		User user = new User(12L, "John Doe", 25);

		Assert.assertThat(user.toString(), equalTo("User{id=12, name=John Doe, age=25}"));
	}

	@Test
	public void whenPlayerToString_shouldCallParentToString() throws Exception {
		User user = new Player(12L, "John Doe", 25);

		Assert.assertThat(user.toString(), equalTo("User{id=12, name=John Doe, age=25}"));
	}

	@Test
	public void whenAdministratorToString_shouldExecuteAdministratorToString() throws Exception {
		User user = new Administrator(12L, "John Doe", 25);

		Assert.assertThat(user.toString(), equalTo("Administrator{id=12, name=John Doe, age=25}"));
	}
}
