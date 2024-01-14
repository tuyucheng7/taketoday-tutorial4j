package cn.tuyucheng.taketoday.instanceof_alternative_test;

import cn.tuyucheng.taketoday.instanceofalternative.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetClassUnitTest {

   @Test
   public void givenADinosaurSpecie_whenUsingGetClass_thenGetMovementOfAnatotitan() {

      assertEquals("running", moveDinosaurUsingGetClass(new Anatotitan()));
   }

   @Test
   public void givenADinosaurSpecie_whenUsingGetClass_thenGetMovementOfEuraptor() {
      assertEquals("flying", moveDinosaurUsingGetClass(new Euraptor()));
   }

   public static String moveDinosaurUsingGetClass(Dinosaur dinosaur) {

      if (dinosaur.getClass()
            .equals(Anatotitan.class)) {

         Anatotitan anatotitan = (Anatotitan) dinosaur;
         return anatotitan.run();
      } else if (dinosaur.getClass()
            .equals(Euraptor.class)) {
         Euraptor euraptor = (Euraptor) dinosaur;
         return euraptor.flies();
      }
      return "";
   }

}
