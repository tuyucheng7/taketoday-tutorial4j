package com.boot.camel.boot.testing;

import cn.tuyucheng.taketoday.camel.boot.Application;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(classes = Application.class)
@CamelSpringBootTest
@MockEndpoints("file:output")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class GreetingsFileRouterUnitTest {

   @Autowired
   private ProducerTemplate template;

   @EndpointInject("mock:file:output")
   private MockEndpoint mock;

   @Test
   @DirtiesContext
   void whenSendBody_thenGreetingReceivedSuccessfully() throws InterruptedException {
      mock.expectedBodiesReceived("Hello Tuyucheng Readers!");

      template.sendBody("direct:start", null);

      mock.assertIsSatisfied();
   }

}
