package cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.callable;

import cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.FactorialCalculator;

import java.math.BigInteger;
import java.util.concurrent.Callable;

public class CallableFactorialTask implements Callable<BigInteger> {

   private final Integer value;

   public CallableFactorialTask(int value) {
      this.value = value;
   }

   @Override
   public BigInteger call() {
      return FactorialCalculator.factorial(BigInteger.valueOf(value));
   }
}
