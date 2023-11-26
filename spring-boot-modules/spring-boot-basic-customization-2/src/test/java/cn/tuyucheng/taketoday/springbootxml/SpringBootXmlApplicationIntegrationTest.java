package cn.tuyucheng.taketoday.springbootxml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootXmlApplication.class)
class SpringBootXmlApplicationIntegrationTest {

   @Autowired
   private Pojo pojo;
   @Value("${sample}")
   private String sample;

   @Test
   void whenCallingGetter_thenPrintingProperty() {
      assertThat(pojo.getField())
            .isNotBlank()
            .isEqualTo(sample);
   }
}