package cn.tuyucheng.taketoday.systemin;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import static cn.tuyucheng.taketoday.systemin.Application.NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class SystemRulesUnitTest {

   @Rule
   public final TextFromStandardInputStream systemIn = emptyStandardInputStream();

   @Test
   public void givenName_whenReadWithSystemRules_thenReturnCorrectResult() {
      systemIn.provideLines("Tuyucheng");
      assertEquals(NAME.concat("Tuyucheng"), Application.readName());
   }
}