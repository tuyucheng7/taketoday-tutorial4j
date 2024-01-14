package cn.tuyucheng.taketoday.instanceof_alternative_test;

import cn.tuyucheng.taketoday.instanceofalternative.visitorspattern.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisitorsPatternUnitTest {

   @Test
   public void givenADinosaurSpecie_whenUsingVisitorPattern_thenGetMovementOfAnatotitan() {

      assertEquals("running", moveDinosaurUsingVisitorPattern((Dino) new Anatotitan()));
   }

   @Test
   public void givenADinosaurSpecie_whenUsingVisitorPattern_thenGetMovementOfEuraptor() {

      assertEquals("flying", moveDinosaurUsingVisitorPattern((Dino) new Euraptor()));
   }

   public static String moveDinosaurUsingVisitorPattern(Dino dinosaur) {
      Visitor visitor = new DinoVisitorImpl();

      return dinosaur.move(visitor);
   }

}
