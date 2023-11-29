package cn.tuyucheng.taketoday.mockito.fluentapi;

import cn.tuyucheng.taketoday.mockito.fluentapi.Pizza.PizzaBuilder;
import cn.tuyucheng.taketoday.mockito.fluentapi.Pizza.PizzaSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PizzaServiceUnitTest {

   @Mock
   private Pizza expectedPizza;

   @Mock(answer = Answers.RETURNS_DEEP_STUBS)
   private PizzaBuilder anotherbuilder;

   @Captor
   private ArgumentCaptor<String> stringCaptor;
   @Captor
   private ArgumentCaptor<Pizza.PizzaSize> sizeCaptor;

   @Test
   public void givenTraditionalMocking_whenServiceInvoked_thenPizzaIsBuilt() {
      PizzaBuilder nameBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
      PizzaBuilder sizeBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
      PizzaBuilder firstToppingBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
      PizzaBuilder secondToppingBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
      PizzaBuilder stuffedBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
      PizzaBuilder willCollectBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
      PizzaBuilder discountBuilder = Mockito.mock(Pizza.PizzaBuilder.class);

      PizzaBuilder builder = Mockito.mock(Pizza.PizzaBuilder.class);
      when(builder.name(anyString())).thenReturn(nameBuilder);
      when(nameBuilder.size(any(Pizza.PizzaSize.class))).thenReturn(sizeBuilder);
      when(sizeBuilder.withExtraTopping(anyString())).thenReturn(firstToppingBuilder);
      when(firstToppingBuilder.withStuffedCrust(anyBoolean())).thenReturn(stuffedBuilder);
      when(stuffedBuilder.withExtraTopping(anyString())).thenReturn(secondToppingBuilder);
      when(secondToppingBuilder.willCollect(anyBoolean())).thenReturn(willCollectBuilder);
      when(willCollectBuilder.applyDiscount(anyInt())).thenReturn(discountBuilder);
      when(discountBuilder.build()).thenReturn(expectedPizza);

      PizzaService service = new PizzaService(builder);
      assertEquals(expectedPizza, service.orderHouseSpecial(), "Expected Pizza");

      verify(builder).name(stringCaptor.capture());
      assertEquals("Special", stringCaptor.getValue(), "Pizza name: ");

      verify(nameBuilder).size(sizeCaptor.capture());
      assertEquals(PizzaSize.LARGE, sizeCaptor.getValue(), "Pizza size: ");
   }

   @Test
   public void givenDeepStubs_whenServiceInvoked_thenPizzaIsBuilt() {
      Mockito.when(anotherbuilder.name(anyString())
                  .size(any(Pizza.PizzaSize.class))
                  .withExtraTopping(anyString())
                  .withStuffedCrust(anyBoolean())
                  .withExtraTopping(anyString())
                  .willCollect(anyBoolean())
                  .applyDiscount(anyInt())
                  .build())
            .thenReturn(expectedPizza);

      PizzaService service = new PizzaService(anotherbuilder);
      assertEquals(expectedPizza, service.orderHouseSpecial(), "Expected Pizza");
   }
}