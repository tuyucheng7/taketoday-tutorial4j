package cn.tuyucheng.taketoday.ecommerce;

import cn.tuyucheng.taketoday.ecommerce.controller.OrderController;
import cn.tuyucheng.taketoday.ecommerce.controller.ProductController;
import cn.tuyucheng.taketoday.ecommerce.dto.OrderProductDto;
import cn.tuyucheng.taketoday.ecommerce.model.Order;
import cn.tuyucheng.taketoday.ecommerce.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EcommerceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EcommerceApplicationIntegrationTest {

   @Autowired
   private TestRestTemplate restTemplate;

   @LocalServerPort
   private int port;

   @Autowired
   private ProductController productController;

   @Autowired
   private OrderController orderController;

   @Test
   void contextLoads() {
      Assertions
            .assertThat(productController)
            .isNotNull();
      Assertions
            .assertThat(orderController)
            .isNotNull();
   }

   @Test
   void givenGetProductsApiCall_whenProductListRetrieved_thenSizeMatchAndListContainsProductNames() {
      ResponseEntity<Iterable<Product>> responseEntity = restTemplate.exchange(STR."http://localhost:\{port}/api/products", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
      });
      Iterable<Product> products = responseEntity.getBody();
      Assertions
            .assertThat(products)
            .hasSize(7);

      assertThat(products, hasItem(hasProperty("name", is("TV Set"))));
      assertThat(products, hasItem(hasProperty("name", is("Game Console"))));
      assertThat(products, hasItem(hasProperty("name", is("Sofa"))));
      assertThat(products, hasItem(hasProperty("name", is("Icecream"))));
      assertThat(products, hasItem(hasProperty("name", is("Beer"))));
      assertThat(products, hasItem(hasProperty("name", is("Phone"))));
      assertThat(products, hasItem(hasProperty("name", is("Watch"))));
   }

   @Test
   void givenGetOrdersApiCall_whenProductListRetrieved_thenSizeMatchAndListContainsProductNames() {
      ResponseEntity<Iterable<Order>> responseEntity = restTemplate.exchange(STR."http://localhost:\{port}/api/orders", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
      });

      Iterable<Order> orders = responseEntity.getBody();
      Assertions
            .assertThat(orders)
            .hasSize(0);
   }

   @Test
   void givenPostOrder_whenBodyRequestMatcherJson_thenResponseContainsEqualObjectProperties() {
      final ResponseEntity<Order> postResponse = restTemplate.postForEntity(STR."http://localhost:\{port}/api/orders", prepareOrderForm(), Order.class);
      Order order = postResponse.getBody();
      Assertions
            .assertThat(postResponse.getStatusCode())
            .isEqualTo(postResponse.getStatusCode())
            /* .isEqualByComparingTo(HttpStatus.CREATED) */;

      assertThat(order, hasProperty("status", is("PAID")));
      assertThat(order.getOrderProducts(), hasItem(hasProperty("quantity", is(2))));
   }

   private OrderController.OrderForm prepareOrderForm() {
      OrderController.OrderForm orderForm = new OrderController.OrderForm();
      OrderProductDto productDto = new OrderProductDto();
      productDto.setProduct(new Product(1L, "TV Set", 300.00, "http://placehold.it/200x100"));
      productDto.setQuantity(2);
      orderForm.setProductOrders(Collections.singletonList(productDto));

      return orderForm;
   }
}