package cn.tuyucheng.taketoday.map.keysetValuesEntrySet;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValuesExampleUnitTest {

   @Test
   public void givenHashMap_whenValuesApplied_thenShouldReturnCollectionOfValues() {
      Map<String, Integer> map = new HashMap<>();
      map.put("one", 1);
      map.put("two", 2);

      Collection<Integer> actualValues = map.values();

      assertEquals(2, actualValues.size());
      assertTrue(actualValues.contains(1));
      assertTrue(actualValues.contains(2));
   }

}
