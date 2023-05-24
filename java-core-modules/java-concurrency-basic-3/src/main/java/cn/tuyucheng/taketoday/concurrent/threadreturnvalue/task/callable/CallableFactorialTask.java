package cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.callable;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import static cn.tuyucheng.taketoday.concurrent.threadreturnvalue.task.FactorialCalculator.factorial;

public class CallableFactorialTask implements Callable<BigInteger> {

   private final Integer value;

   public CallableFactorialTask(int value) {
      this.value = value;
   }

   @Override
   public BigInteger call() {
      return factorial(BigInteger.valueOf(value));
   }
}