package cn.tuyucheng.taketoday.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionsEmpty {

   @Test
   public void givenArrayList_whenAddingElement_addsNewElement() {
      ArrayList<String> mutableList = new ArrayList<>();
      mutableList.add("test");

      Assertions.assertEquals(mutableList.size(), 1);
      Assertions.assertEquals(mutableList.get(0), "test");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void givenCollectionsEmptyList_whenAddingElement_throwsUnsupportedOperationException() {
      List<String> immutableList = Collections.emptyList();
      immutableList.add("test");
   }

}