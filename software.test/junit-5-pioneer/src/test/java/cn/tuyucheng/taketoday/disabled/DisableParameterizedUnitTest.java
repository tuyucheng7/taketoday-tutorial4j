package cn.tuyucheng.taketoday.disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import static org.junit.jupiter.api.Assertions.fail;

public class DisableParameterizedUnitTest {

   // disable invocations whose display name contains "disable"
   @DisableIfDisplayName(contains = "disable")
   @ParameterizedTest(name = "See if enabled with {0}")
   @ValueSource(
         // Disabled: 1,2,3,4,5; Not disabled: 6
         strings = {
               "disable who",
               "you, disable you",
               "why am I disabled",
               "what has been disabled must stay disabled",
               "fine disable me all you want",
               "not those one, though!"
         })
   void testExecutionDisabled(String reason) {
      if (reason.contains("disable"))
         fail("Test should've been disabled " + reason);
   }

   @DisableIfDisplayName(contains = {"1", "2"})
   @ParameterizedTest(name = "See if enabled with {0}")
   @ValueSource(ints = {1, 2, 3, 4, 5})
   void testDisplayNameString(int num) {
      if (num == 1 || num == 2)
         fail("Test should've been disabled for " + num);
   }

   // disable invocations whose display name contains "disable " or "disabled "
   @DisableIfDisplayName(matches = ".*disabled?\\s.*")
   @ParameterizedTest(name = "See if enabled with {0}")
   @ValueSource(
         // Disabled: 1,2,4,5; Not disabled: 3,6
         strings = {
               "disable who",
               "you, disable you",
               "why am I disabled",
               "what has been disabled must stay disabled",
               "fine disable me all you want",
               "not those one, though!"
         })
   void single(String reason) {
      // ...
   }
}