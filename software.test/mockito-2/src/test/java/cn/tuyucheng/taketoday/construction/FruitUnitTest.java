package cn.tuyucheng.taketoday.construction;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FruitUnitTest {

   @Test
   void givenMockedConstructor_whenFruitCreated_thenMockIsReturned() {
      assertEquals("Apple", new Fruit().getName());
      assertEquals("Red", new Fruit().getColour());

      try (MockedConstruction<Fruit> mock = mockConstruction(Fruit.class)) {

         Fruit fruit = new Fruit();
         when(fruit.getName()).thenReturn("Banana");
         when(fruit.getColour()).thenReturn("Yellow");

         assertEquals("Banana", fruit.getName());
         assertEquals("Yellow", fruit.getColour());

         List<Fruit> constructed = mock.constructed();
         assertEquals(1, constructed.size());
      }
   }

   @Test
   void givenMockedConstructorWithNewDefaultAnswer_whenFruitCreated_thenRealMethodInvoked() {
      try (MockedConstruction<Fruit> mock = mockConstruction(Fruit.class, withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {

         Fruit fruit = new Fruit();

         assertEquals("Apple", fruit.getName());
         assertEquals("Red", fruit.getColour());

         List<Fruit> constructed = mock.constructed();
         assertEquals(1, constructed.size());
      }
   }
}