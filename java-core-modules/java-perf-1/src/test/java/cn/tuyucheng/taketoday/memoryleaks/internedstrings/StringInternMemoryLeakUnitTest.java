package cn.tuyucheng.taketoday.memoryleaks.internedstrings;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StringInternMemoryLeakUnitTest {
   @Test
   @Disabled // Test deliberately ignored as memory leak tests consume lots of resources
   public void givenJava6OrBelow_whenInterningLargeStrings_thenPermgenIncreases() {
      new InternedString().readString();
      System.out.print("Debug Point - VisuaLVM");
   }

   @Test
   @Disabled // Test deliberately ignored as memory leak tests consume lots of resources
   public void givenJava6OrBelow_whenNotInterningLargeStrings_thenPermgenDoesntIncrease() {
      new StringObject().readString();
      System.out.print("Debug Point - VisuaLVM");
   }
}
