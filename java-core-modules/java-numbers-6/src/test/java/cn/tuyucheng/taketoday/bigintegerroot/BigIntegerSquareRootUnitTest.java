package cn.tuyucheng.taketoday.bigintegerroot;


import cn.tuyucheng.taketoday.bigintegerroot.algorithms.Newton;
import cn.tuyucheng.taketoday.bigintegerroot.algorithms.NewtonPlus;
import com.google.common.math.BigIntegerMath;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BigIntegerSquareRootUnitTest {

   @ParameterizedTest
   @ValueSource(strings = {
         BigIntegerHolder.BIG_NUMBER,
         BigIntegerHolder.VERY_BIG_NUMBER,
         BigIntegerHolder.VERY_BIG_NUMBER
   })
   void squareRootTest(String number) {
      final BigInteger bigInteger = new BigInteger(number);
      final BigInteger javaRoot = bigInteger.sqrt();
      final BigInteger guavaRoot = BigIntegerMath.sqrt(bigInteger, RoundingMode.DOWN);
      final BigInteger newtonRoot = Newton.sqrt(bigInteger);
      final BigInteger newtonPlusRoot = NewtonPlus.sqrt(bigInteger);

      assertTrue(Stream.of(
            new Pair<>(javaRoot, guavaRoot),
            new Pair<>(guavaRoot, newtonRoot),
            new Pair<>(newtonRoot, newtonPlusRoot)
      ).allMatch(pair -> pair.getFirst().equals(pair.getSecond())));
   }
}