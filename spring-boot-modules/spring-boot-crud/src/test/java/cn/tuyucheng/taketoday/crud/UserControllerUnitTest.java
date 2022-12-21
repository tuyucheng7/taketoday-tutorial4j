package cn.tuyucheng.taketoday.crud;

import cn.tuyucheng.taketoday.crud.controllers.UserController;
import cn.tuyucheng.taketoday.crud.entities.User;
import cn.tuyucheng.taketoday.crud.repositories.UserRepository;
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
    void whenCalledIndex_thenCorrect() {
        assertThat(userController.showUserList(mockedModel)).isEqualTo("index");
    }

    @Test
    void whenCalledshowSignUpForm_thenCorrect() {
        User user = new User("John", "john@domain.com");

        assertThat(userController.showSignUpForm(user)).isEqualTo("add-user");
    }

    @Test
    void whenCalledaddUserAndValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");

        when(mockedBindingResult.hasErrors()).thenReturn(false);

        assertThat(userController.addUser(user, mockedBindingResult, mockedModel)).isEqualTo("redirect:/index");
    }

    @Test
    void whenCalledaddUserAndInValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");

        when(mockedBindingResult.hasErrors()).thenReturn(true);

        assertThat(userController.addUser(user, mockedBindingResult, mockedModel)).isEqualTo("add-user");
    }

    @Test
    void whenCalledshowUpdateForm_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> assertThat(userController.showUpdateForm(0, mockedModel)).isEqualTo("update-user"));
    }

    @Test
    void whenCalledupdateUserAndValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");

        when(mockedBindingResult.hasErrors()).thenReturn(false);

        assertThat(userController.updateUser(1L, user, mockedBindingResult, mockedModel)).isEqualTo("redirect:/index");
    }

    @Test
    void whenCalledupdateUserAndInValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");

        when(mockedBindingResult.hasErrors()).thenReturn(true);

        assertThat(userController.updateUser(1L, user, mockedBindingResult, mockedModel)).isEqualTo("update-user");
    }

    @Test
    void whenCalleddeleteUser_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> assertThat(userController.deleteUser(1L, mockedModel)).isEqualTo("redirect:/index"));
    }
}