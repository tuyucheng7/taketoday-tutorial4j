package cn.tuyucheng.taketoday.springsoap;

import cn.tuyucheng.taketoday.springsoap.client.gen.GetCountryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationIntegrationTest {

   private Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

   @LocalServerPort
   private int port = 0;

   @BeforeEach
   void init() throws Exception {
      marshaller.setPackagesToScan(ClassUtils.getPackageName(GetCountryRequest.class));
      marshaller.afterPropertiesSet();
   }

   @Test
   void whenSendRequest_thenResponseIsNotNull() {
      WebServiceTemplate ws = new WebServiceTemplate(marshaller);
      GetCountryRequest request = new GetCountryRequest();
      request.setName("Spain");

      Object response = ws.marshalSendAndReceive(STR."http://localhost:\{port}/ws", request);

      assertThat(response).isNotNull();
   }
}