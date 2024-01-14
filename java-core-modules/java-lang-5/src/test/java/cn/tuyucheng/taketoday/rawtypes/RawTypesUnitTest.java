package cn.tuyucheng.taketoday.rawtypes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RawTypesUnitTest {
   @Test
   public void shouldCreateListUsingRawTypes() {
      @SuppressWarnings("rawtypes")
      List myList = new ArrayList();
      myList.add(new Object());
      myList.add("2");
      myList.add(new Integer(1));
   }
}
