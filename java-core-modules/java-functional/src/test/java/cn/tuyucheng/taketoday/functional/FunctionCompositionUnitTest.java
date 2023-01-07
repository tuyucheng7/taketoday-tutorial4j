package cn.tuyucheng.taketoday.functional;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionCompositionUnitTest {

    @Test
    public void testLogThenSqrt() {

        assertEquals(1.07, FunctionComposition.logThenSqrt(3.14), 0.01);

    }

    @Test
    public void testSqrtThenLog() {

        assertEquals(0.57, FunctionComposition.sqrtThenLog(3.14), 0.01);

    }

}
