package cn.tuyucheng.taketoday.camel.boot.exception;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExceptionHandlingWithDoTryRoute extends RouteBuilder {

   @Override
   public void configure() throws Exception {

      from("direct:start-handling-exception")
            .routeId("exception-handling-route")
            .doTry()
            .process(new IllegalArgumentExceptionThrowingProcessor())
            .to("mock:received")
            .doCatch(IOException.class, IllegalArgumentException.class)
            .to("mock:caught")
            .doFinally()
            .to("mock:finally")
            .end();

   }
}