package cn.tuyucheng.taketoday.exceptions.stackoverflowerror;

import org.junit.jupiter.api.Test;

public class CyclicDependancyManualTest {
   @Test(expected = StackOverflowError.class)
   public void whenInstanciatingClassOne_thenThrowsException() {
      ClassOne obj = new ClassOne();
   }
}
