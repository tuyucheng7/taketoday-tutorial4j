package cn.tuyucheng.taketoday.api;

import cn.tuyucheng.taketoday.domain.Product;
import cn.tuyucheng.taketoday.domain.ProductRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
@Testcontainers
@AutoConfigureMockMvc
class ProductControllerLiveTest {

   @Container
   @ServiceConnection
   static PostgreSQLContainer<?> postgres =
         new PostgreSQLContainer<>("postgres:15.2-alpine");

   @Container
   @ServiceConnection
   static KafkaContainer kafka =
         new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

   // @DynamicPropertySource
   // static void overrideProperties(DynamicPropertyRegistry registry) {
   //    registry.add("spring.datasource.url", postgres::getJdbcUrl);
   //    registry.add("spring.datasource.username", postgres::getUsername);
   //    registry.add("spring.datasource.password", postgres::getPassword);
   //    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
   // }

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