package cn.tuyucheng.taketoday.unnamed.variables;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.unnamed.variables.UnnamedPatterns.getEngineTypeWithNamedPattern;
import static cn.tuyucheng.taketoday.unnamed.variables.UnnamedPatterns.getEngineTypeWithUnnamedPattern;
import static cn.tuyucheng.taketoday.unnamed.variables.UnnamedPatterns.getObjectsColorWithNamedPattern;
import static cn.tuyucheng.taketoday.unnamed.variables.UnnamedPatterns.getObjectsColorWithSwitchAndNamedPattern;
import static cn.tuyucheng.taketoday.unnamed.variables.UnnamedPatterns.getObjectsColorWithSwitchAndUnnamedPattern;
import static cn.tuyucheng.taketoday.unnamed.variables.UnnamedPatterns.getObjectsColorWithUnnamedPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnnamedPatternsUnitTest extends CarScenario {

   @Test
   public void whenExtractingColorWithNamedPatterns_thenReturnBlue() {
      assertEquals("blue", getObjectsColorWithNamedPattern(cars.get(0)));
   }

   @Test
   public void whenExtractingColorWithUnnamedPatterns_thenReturnBlue() {
      assertEquals("blue", getObjectsColorWithUnnamedPattern(cars.get(0)));
   }

   @Test
   public void whenExtractingColorWithSwitchAndNamedPatterns_thenReturnBlue() {
      assertEquals("blue", getObjectsColorWithSwitchAndNamedPattern(cars.get(0)));
   }

   @Test
   public void whenExtractingColorWithSwitchAndUnnamedPatterns_thenReturnBlue() {
      assertEquals("blue", getObjectsColorWithSwitchAndUnnamedPattern(cars.get(0)));
   }

   @Test
   public void whenExtractingEngineTypeWithNamedPatterns_thenReturnGas() {
      assertEquals("gas", getEngineTypeWithNamedPattern(cars.get(0)));
   }

   @Test
   public void whenExtractingEngineTypeWithUnnamedPatterns_thenReturnGas() {
      assertEquals("gas", getEngineTypeWithUnnamedPattern(cars.get(0)));
   }
}