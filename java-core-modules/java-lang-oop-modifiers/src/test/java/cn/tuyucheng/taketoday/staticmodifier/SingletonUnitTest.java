package cn.tuyucheng.taketoday.staticmodifier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingletonUnitTest {

   @Test
   public void givenStaticInnerClass_whenMultipleTimesInstanceCalled_thenOnlyOneTimeInitialized() {
      Singleton object1 = Singleton.getInstance();
      Singleton object2 = Singleton.getInstance();

      Assertions.assertSame(object1, object2);
   }
}
