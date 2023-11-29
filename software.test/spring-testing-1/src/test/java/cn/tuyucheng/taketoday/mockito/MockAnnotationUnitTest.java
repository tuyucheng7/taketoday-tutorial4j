package cn.tuyucheng.taketoday.mockito;

import cn.tuyucheng.taketoday.mockito.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class MockAnnotationUnitTest {

   @Mock
   UserRepository mockRepository;

   @Test
   void givenCountMethodMocked_WhenCountInvoked_ThenMockValueReturned() {
      Mockito.when(mockRepository.count()).thenReturn(123L);

      long userCount = mockRepository.count();

      assertEquals(123L, userCount);
      Mockito.verify(mockRepository).count();
   }

   @Test
   void givenCountMethodMocked_WhenCountInvoked_ThenMockedValueReturned() {
      UserRepository localMockRepository = Mockito.mock(UserRepository.class);
      Mockito.when(localMockRepository.count()).thenReturn(111L);

      long userCount = localMockRepository.count();

      assertEquals(111L, userCount);
      Mockito.verify(localMockRepository).count();
   }
}