package cn.tuyucheng.taketoday.mockito.annotations;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class MockitoAnnotationsUninitializedUnitTest {

   @Mock
   List<String> mockedList;

   @Test
   void whenMockitoAnnotationsUninitialized_thenNPEThrown() {
      assertThrows(NullPointerException.class, () -> {
         when(mockedList.size()).thenReturn(1);
      });
   }
}