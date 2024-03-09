package cn.tuyucheng.taketoday.map.keysetValuesEntrySet;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeySetExampleUnitTest {

   @Test
   public void givenHashMap_whenKeySetApplied_thenShouldReturnSetOfKeys() {
      Map<String, Integer> map = new HashMap<>();
      map.put("one", 1);
      map.put("two", 2);

      Set<String> actualValues = map.keySet();

      assertEquals(2, actualValues.size());
      assertTrue(actualValues.contains("one"));
      assertTrue(actualValues.contains("two"));
   }

}
