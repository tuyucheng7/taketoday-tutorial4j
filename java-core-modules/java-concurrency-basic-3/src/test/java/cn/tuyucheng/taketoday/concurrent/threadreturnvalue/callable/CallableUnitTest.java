package cn.tuyucheng.taketoday.concurrent.threadreturnvalue.callable;

import cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.callable.CallableExecutor;
import cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.callable.CallableFactorialTask;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallableUnitTest {

   private final CallableExecutor callableExecutor = new CallableExecutor();

   @Test
   void givenCallableExecutor_whenExecuteFactorial_thenResultOk() {
      BigInteger result = callableExecutor.execute(Arrays.asList(new CallableFactorialTask(5), new CallableFactorialTask(3)));
      assertEquals(BigInteger.valueOf(126), result);
   }
}