package cn.tuyucheng.taketoday.mockito.mockfinal;

import cn.tuyucheng.taketoday.mockito.FinalList;
import cn.tuyucheng.taketoday.mockito.MyList;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class MockFinalsUnitTest {

   @Test
   void whenMockFinalMethod_thenMockWorks() {

      MyList mock = mock(MyList.class);
      when(mock.finalMethod()).thenReturn(1);

      assertThat(mock.finalMethod()).isNotZero();
   }

   @Test
   void whenMockFinalClass_thenMockWorks() {

      FinalList mock = mock(FinalList.class);
      when(mock.size()).thenReturn(2);

      assertThat(mock.size()).isNotEqualTo(1);
   }

   @Test
   public void whenMockFinalMethodMockWorks_withInlineMockMaker() {
      MyList myList = new MyList();

      MyList mock = mock(MyList.class, withSettings().mockMaker(MockMakers.INLINE));
      when(mock.finalMethod()).thenReturn(1);

      assertThat(mock.finalMethod()).isNotEqualTo(myList.finalMethod());
   }
}
