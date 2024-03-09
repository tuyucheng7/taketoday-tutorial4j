package cn.tuyucheng.taketoday.firstitemfromset;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetFirstItemFromSetUnitTest {

   @Test
   public void givenASet_whenUsingIterator_thenRetrieveAnItem() {
      Set<Integer> set = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5));
      Iterator iterator = set.iterator();
      if (iterator.hasNext()) {
         int retrieved = (int) iterator.next();
         assertEquals(retrieved, 1);
      }
   }

   @Test
   public void givenASet_whenUsingStreams_thenRetrieveAnItem() {
      Set<Integer> set = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5));
      Optional<Integer> optional = set.stream().findFirst();
      if (optional.isPresent()) {
         int retrieved = optional.get();
         assertEquals(retrieved, 1);
      }
   }

}
