package cn.tuyucheng.taketoday.instanceof_alternative_test;

import cn.tuyucheng.taketoday.instanceofalternative.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolymorphismUnitTest {

   @Test
   public void givenADinosaurSpecie_whenUsingPolymorphism_thenGetMovementOfAnatotitan() {

      assertEquals("running", moveDinosaurUsingPolymorphism(new Anatotitan()));
   }

   @Test
   public void givenADinosaurSpecie_whenUsingPolymorphism_thenGetMovementOfEuraptor() {
      assertEquals("flying", moveDinosaurUsingPolymorphism(new Euraptor()));
   }

   public static String moveDinosaurUsingPolymorphism(Dinosaur dinosaur) {
      return dinosaur.move();
   }

}
