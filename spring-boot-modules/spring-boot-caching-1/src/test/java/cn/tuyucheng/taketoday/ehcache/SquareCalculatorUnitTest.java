package cn.tuyucheng.taketoday.ehcache;

import cn.tuyucheng.taketoday.ehcache.calculator.SquaredCalculator;
import cn.tuyucheng.taketoday.ehcache.config.CacheHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SquareCalculatorUnitTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(SquareCalculatorUnitTest.class);

   private final SquaredCalculator squaredCalculator = new SquaredCalculator();
   private final CacheHelper cacheHelper = new CacheHelper();

   @BeforeEach
   void setup() {
      squaredCalculator.setCache(cacheHelper);
   }

   @Test
   void whenCalculatingSquareValueOnce_thenCacheDontHaveValues() {
      for (int i = 10; i < 15; i++) {
         assertFalse(cacheHelper.getSquareNumberCache().containsKey(i));
         LOGGER.debug("Square value of {} is: {}", i, squaredCalculator.getSquareValueOfNumber(i));
      }
   }

   @Test
   void whenCalculatingSquareValueAgain_thenCacheHasAllValues() {
      for (int i = 10; i < 15; i++) {
         assertFalse(cacheHelper.getSquareNumberCache().containsKey(i));
         LOGGER.debug("Square value of {} is: {}", i, squaredCalculator.getSquareValueOfNumber(i));
      }

      for (int i = 10; i < 15; i++) {
         assertTrue(cacheHelper.getSquareNumberCache().containsKey(i));
         LOGGER.debug("Square value of {} is: {}", i, squaredCalculator.getSquareValueOfNumber(i) + "\n");
      }
   }
}