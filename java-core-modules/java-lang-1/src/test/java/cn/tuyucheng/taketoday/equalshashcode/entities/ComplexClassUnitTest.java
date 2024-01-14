package cn.tuyucheng.taketoday.equalshashcode.entities;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ComplexClassUnitTest {

   @Test
   public void testEqualsAndHashcodes() {
      List<String> strArrayList = new ArrayList<String>();
      strArrayList.add("abc");
      strArrayList.add("def");
      ComplexClass aObject = new ComplexClass(strArrayList, new HashSet<Integer>(45, 67));
      ComplexClass bObject = new ComplexClass(strArrayList, new HashSet<Integer>(45, 67));

      List<String> strArrayListD = new ArrayList<String>();
      strArrayListD.add("lmn");
      strArrayListD.add("pqr");
      ComplexClass dObject = new ComplexClass(strArrayListD, new HashSet<Integer>(45, 67));

      Assert.assertTrue(aObject.equals(bObject) && bObject.equals(aObject));

      Assert.assertTrue(aObject.hashCode() == bObject.hashCode());

      Assert.assertFalse(aObject.equals(dObject));
      Assert.assertFalse(aObject.hashCode() == dObject.hashCode());
   }

}
