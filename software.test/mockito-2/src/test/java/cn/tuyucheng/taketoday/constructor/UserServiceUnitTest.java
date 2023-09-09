package cn.tuyucheng.taketoday.constructor;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class UserServiceUnitTest {

   @Test
   void whenConstructorInvokedWithInitializer_ThenMockObjectShouldBeCreated() {
      try (MockedConstruction<UserService> mockUserService = Mockito.mockConstruction(UserService.class, (mock, context) -> when(mock.getUserName()).thenReturn("John Doe"))) {
         User user = new User();
         assertEquals(1, mockUserService.constructed().size());
         assertEquals("John Doe", user.getUserName());
      }
   }

   @Test
   void whenConstructorInvokedWithoutInitializer_ThenMockObjectShouldBeCreatedWithNullFields() {
      try (MockedConstruction<UserService> mockUserService = Mockito.mockConstruction(UserService.class)) {
         User user = new User();
         assertEquals(1, mockUserService.constructed().size());
         assertNull(user.getUserName());
      }
   }

   @Test
   void whenConstructorInvokedWithParameters_ThenMockObjectShouldBeCreated() {
      try (MockedConstruction<UserService> mockUserService = Mockito.mockConstruction(UserService.class, (mock, context) -> when(mock.getUserName()).thenReturn("John Doe"))) {
         User user = new User("Mike");
         assertEquals(1, mockUserService.constructed().size());
         assertEquals("John Doe", user.getUserName());
      }
   }

   @Test
   void whenMultipleConstructorsInvoked_ThenMultipleMockObjectsShouldBeCreated() {
      try (MockedConstruction<UserService> mockUserService = Mockito.mockConstruction(UserService.class)) {
         User user = new User();
         User secondUser = new User();
         User thirdUser = new User("Mike");

         when(mockUserService.constructed().get(0).getUserName()).thenReturn("John Doe");
         when(mockUserService.constructed().get(1).getUserName()).thenReturn("Steve Smith");

         assertEquals(3, mockUserService.constructed().size());
         assertEquals("John Doe", user.getUserName());
         assertEquals("Steve Smith", secondUser.getUserName());
         assertNull(thirdUser.getUserName());
      }
   }
}