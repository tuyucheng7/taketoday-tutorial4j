package cn.tuyucheng.taketoday.springbootcrudapp.application.tests;

import cn.tuyucheng.taketoday.springbootcrudapp.application.controllers.UserController;
import cn.tuyucheng.taketoday.springbootcrudapp.application.entities.User;
import cn.tuyucheng.taketoday.springbootcrudapp.application.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerUnitTest {

	private static UserController userController;
	private static UserRepository mockedUserRepository;
	private static BindingResult mockedBindingResult;
	private static Model mockedModel;

	@BeforeAll
	static void setUpUserControllerInstance() {
		mockedUserRepository = mock(UserRepository.class);
		mockedBindingResult = mock(BindingResult.class);
		mockedModel = mock(Model.class);
		userController = new UserController(mockedUserRepository);
	}

	@Test
	void whenCalledShowSignUpForm_thenCorrect() {
		User user = new User("John", "john@domain.com");

		assertThat(userController.showSignUpForm(user)).isEqualTo("add-user");
	}

	@Test
	void whenCalledAddUserAndValidUser_thenCorrect() {
		User user = new User("John", "john@domain.com");

		when(mockedBindingResult.hasErrors()).thenReturn(false);

		assertThat(userController.addUser(user, mockedBindingResult, mockedModel)).isEqualTo("index");
	}

	@Test
	void whenCalledAddUserAndInValidUser_thenCorrect() {
		User user = new User("John", "john@domain.com");

		when(mockedBindingResult.hasErrors()).thenReturn(true);

		assertThat(userController.addUser(user, mockedBindingResult, mockedModel)).isEqualTo("add-user");
	}

	@Test
	void whenCalledShowUpdateForm_thenIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class,
			() -> assertThat(userController.showUpdateForm(0, mockedModel)).isEqualTo("update-user"));
	}

	@Test
	void whenCalledUpdateUserAndValidUser_thenCorrect() {
		User user = new User("John", "john@domain.com");

		when(mockedBindingResult.hasErrors()).thenReturn(false);

		assertThat(userController.updateUser(1L, user, mockedBindingResult, mockedModel)).isEqualTo("index");
	}

	@Test
	void whenCalledUpdateUserAndInValidUser_thenCorrect() {
		User user = new User("John", "john@domain.com");

		when(mockedBindingResult.hasErrors()).thenReturn(true);

		assertThat(userController.updateUser(1L, user, mockedBindingResult, mockedModel)).isEqualTo("update-user");
	}

	@Test
	void whenCalledDeleteUser_thenIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class,
			() -> assertThat(userController.deleteUser(1L, mockedModel)).isEqualTo("index"));
	}
}