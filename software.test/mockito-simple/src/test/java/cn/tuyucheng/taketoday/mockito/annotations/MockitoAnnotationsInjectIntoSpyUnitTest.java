package cn.tuyucheng.taketoday.mockito.annotations;

import cn.tuyucheng.taketoday.mockito.MyDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class MockitoAnnotationsInjectIntoSpyUnitTest {

   @Mock
   private Map<String, String> wordMap;
   @InjectMocks
   private MyDictionary dic = new MyDictionary();
   private MyDictionary spyDic;

   @BeforeEach
   public void init() {
      openMocks(this);
      spyDic = spy(new MyDictionary(wordMap));
   }

   @Test
   void whenUseInjectMocksAnnotation_thenCorrect() {
      when(wordMap.get("aWord")).thenReturn("aMeaning");

      assertEquals("aMeaning", spyDic.getMeaning("aWord"));
   }
}