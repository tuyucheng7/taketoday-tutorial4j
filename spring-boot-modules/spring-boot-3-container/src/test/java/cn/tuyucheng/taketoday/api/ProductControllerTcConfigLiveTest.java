package cn.tuyucheng.taketoday.api;

import cn.tuyucheng.taketoday.TestcontainersConfiguration;
import cn.tuyucheng.taketoday.domain.Product;
import cn.tuyucheng.taketoday.domain.ProductRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProductControllerTcConfigLiveTest {

   @Autowired
   private ProductRepository productRepository;

   @Autowired
   private MockMvc mockMvc;

   @BeforeEach
   void setUp() {
      productRepository.deleteAll();
      productRepository.save(new Product(null, "P10", "macbook", "laptop", BigDecimal.TEN));
   }

   @Test
   void shouldReturnProducts() throws Exception {
      mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code", CoreMatchers.equalTo("P10")))
            .andExpect(jsonPath("$[0].name", CoreMatchers.equalTo("macbook")))
            .andExpect(jsonPath("$[0].description", CoreMatchers.equalTo("laptop")))
            .andExpect(jsonPath("$[0].price", CoreMatchers.equalTo(10.0)));
   }

   @Test
   void shouldCreateProductSuccessfully() throws Exception {
      mockMvc.perform(post("/api/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                            {
                                "code": "P99",
                                "name": "New Product",
                                "description": "My new product",
                                "price": 25.50
                            }
                        """))
            .andExpect(status().isOk());

      await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() ->
            assertThat(productRepository.findByCode("P99")).isNotEmpty());
   }
}