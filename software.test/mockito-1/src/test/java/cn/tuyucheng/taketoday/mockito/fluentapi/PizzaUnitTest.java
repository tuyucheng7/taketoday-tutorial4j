package cn.tuyucheng.taketoday.mockito.fluentapi;

import cn.tuyucheng.taketoday.mockito.fluentapi.Pizza.PizzaSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PizzaUnitTest {

   @Test
   public void givenFluentPizzaApi_whenBuilt_thenPizzaHasCorrectAttributes() {
      Pizza pizza = new Pizza.PizzaBuilder()
            .name("Margherita")
            .size(PizzaSize.LARGE)
            .withExtraTopping("Mushroom")
            .withStuffedCrust(false)
            .willCollect(true)
            .applyDiscount(20)
            .build();

      assertEquals(pizza.getName(), "Margherita", "Pizza name: ");
      assertEquals(PizzaSize.LARGE, pizza.getSize(), "Pizza size: ");
      assertEquals("Mushroom", pizza.getToppings()
            .get(0), "Extra toppings: ");
      assertFalse(pizza.isStuffedCrust(), "Has stuffed crust: ");
      assertTrue(pizza.isCollecting(), "Will collect: ");
      assertEquals(Integer.valueOf(20), pizza.getDiscount(), "Discounts: ");
   }
}