package cn.tuyucheng.taketoday.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WhenUsingHashSet {

   @Test
   public void whenAddingElement_shouldAddElement() {
      Set<String> hashset = new HashSet<>();
      Assertions.assertTrue(hashset.add("String Added"));
   }

   @Test
   public void whenCheckingForElement_shouldSearchForElement() {
      Set<String> hashsetContains = new HashSet<>();
      hashsetContains.add("String Added");
      Assertions.assertTrue(hashsetContains.contains("String Added"));
   }

   @Test
   public void whenCheckingTheSizeOfHashSet_shouldReturnThesize() {
      Set<String> hashSetSize = new HashSet<>();
      hashSetSize.add("String Added");
      Assertions.assertEquals(1, hashSetSize.size());
   }

   @Test
   public void whenCheckingForEmptyHashSet_shouldCheckForEmpty() {
      Set<String> emptyHashSet = new HashSet<>();
      Assertions.assertTrue(emptyHashSet.isEmpty());
   }

   @Test
   public void whenRemovingElement_shouldRemoveElement() {
      Set<String> removeFromHashSet = new HashSet<>();
      removeFromHashSet.add("String Added");
      Assertions.assertTrue(removeFromHashSet.remove("String Added"));
   }

   @Test
   public void whenClearingHashSet_shouldClearHashSet() {
      Set<String> clearHashSet = new HashSet<>();
      clearHashSet.add("String Added");
      clearHashSet.clear();
      Assertions.assertTrue(clearHashSet.isEmpty());
   }

   @Test
   public void whenIteratingHashSet_shouldIterateHashSet() {
      Set<String> hashset = new HashSet<>();
      hashset.add("First");
      hashset.add("Second");
      hashset.add("Third");
      Iterator<String> itr = hashset.iterator();
      while (itr.hasNext()) {
         System.out.println(itr.next());
      }
   }

   @Test(expected = ConcurrentModificationException.class)
   public void whenModifyingHashSetWhileIterating_shouldThrowException() {
      Set<String> hashset = new HashSet<>();
      hashset.add("First");
      hashset.add("Second");
      hashset.add("Third");
      Iterator<String> itr = hashset.iterator();
      while (itr.hasNext()) {
         itr.next();
         hashset.remove("Second");
      }
   }

   @Test
   public void whenRemovingElementUsingIterator_shouldRemoveElement() {
      Set<String> hashset = new HashSet<>();
      hashset.add("First");
      hashset.add("Second");
      hashset.add("Third");
      Iterator<String> itr = hashset.iterator();
      while (itr.hasNext()) {
         String element = itr.next();
         if (element.equals("Second"))
            itr.remove();
      }
      Assertions.assertEquals(2, hashset.size());
   }
}
