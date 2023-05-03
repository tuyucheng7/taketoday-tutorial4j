package com.boot.camel.conditional;

import cn.tuyucheng.taketoday.camel.boot.Application;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(classes = Application.class)
@CamelSpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ConditionalBodyRouterUnitTest {

   @Autowired
   private ProducerTemplate template;

   @EndpointInject("mock:result-body")
   private MockEndpoint mock;

   @Test
   @DirtiesContext
   void whenSendBodyWithTuyucheng_thenGoodbyeMessageReceivedSuccessfully() throws InterruptedException {
      mock.expectedBodiesReceived("Goodbye, Tuyucheng!");

      template.sendBody("direct:start-conditional", "Hello Tuyucheng Readers!");

      mock.assertIsSatisfied();
   }

}
