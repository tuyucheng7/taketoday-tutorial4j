package cn.tuyucheng.taketoday.logging.log4j2.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.Random;

class LambdaExpressionsIntegrationTest {

   private static final Logger logger = LogManager.getRootLogger();

   @Test
   void whenCheckLogMessage_thenOk() {
      if (logger.isTraceEnabled()) {
         logger.trace("Number is {}", getRandomNumber());
      }
   }

   @Test
   void whenUseLambdaExpression_thenOk() {
      logger.trace("Number is {}", this::getRandomNumber);
      logger.trace("Name is {} and age is {}", this::getName, this::getRandomNumber);
   }

   private int getRandomNumber() {
      return (new Random()).nextInt(10);
   }

   private String getName() {
      return "John";
   }
}