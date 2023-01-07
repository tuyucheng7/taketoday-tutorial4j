package cn.tuyucheng.taketoday.initializationguide;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserUnitTest {

	@Test
	public void givenUserInstance_whenIntializedWithNew_thenInstanceIsNotNull() {
		User user = new User("Alice", 1);
		assertThat(user).isNotNull();
	}

	@Test
	public void givenUserInstance_whenInitializedWithReflection_thenInstanceIsNotNull() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		User user = User.class.getConstructor(String.class, int.class)
			.newInstance("Alice", 2);
		assertThat(user).isNotNull();
	}

	@Test
	public void givenUserInstance_whenCopiedWithClone_thenExactMatchIsCreated() throws CloneNotSupportedException {
		User user = new User("Alice", 3);
		User clonedUser = (User) user.clone();
		assertThat(clonedUser).isEqualTo(user);
	}

	@Test
	public void givenUserInstance_whenValuesAreNotInitialized_thenUserNameAndIdReturnDefault() {
		User user = new User();
		assertThat(user.getName()).isNull();
		assertThat(user.getId() == 0);
	}
}
