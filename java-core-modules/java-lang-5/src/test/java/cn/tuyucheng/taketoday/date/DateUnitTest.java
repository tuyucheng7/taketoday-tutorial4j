package cn.tuyucheng.taketoday.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class DateUnitTest {

   @Test
   public void whenUsingFullyQualifiedClassNames() {

      java.util.Date javaDate = new java.util.Date();
      cn.tuyucheng.taketoday.date.Date tuyuchengDate = new cn.tuyucheng.taketoday.date.Date(javaDate.getTime());

      Assertions.assertEquals(javaDate.getTime(), tuyuchengDate.getTime());
   }

   @Test
   public void whenImportTheMostUsedOne() {

      Date javaDate = new Date();
      cn.tuyucheng.taketoday.date.Date tuyuchengDate = new cn.tuyucheng.taketoday.date.Date(javaDate.getTime());

      Assertions.assertEquals(javaDate.getTime(), tuyuchengDate.getTime());
   }
}
