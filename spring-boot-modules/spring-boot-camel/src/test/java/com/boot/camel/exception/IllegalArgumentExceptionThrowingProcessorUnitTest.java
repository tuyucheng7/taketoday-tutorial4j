package com.boot.camel.exception;

import cn.tuyucheng.taketoday.camel.boot.exception.IllegalArgumentExceptionThrowingProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class IllegalArgumentExceptionThrowingProcessorUnitTest {

   @Test
   void whenProcessed_thenIllegalArgumentExceptionRaisedSuccessfully() {
      assertThrows(IllegalArgumentException.class, () -> {
         new IllegalArgumentExceptionThrowingProcessor().process(null);
      });
   }

}
