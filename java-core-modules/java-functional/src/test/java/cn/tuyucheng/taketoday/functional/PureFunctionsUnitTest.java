package cn.tuyucheng.taketoday.functional;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PureFunctionsUnitTest {

    @Test
    public void testSortingWithoutLambda() {

        assertEquals(new Integer(18), PureFunctions.sum(Arrays.asList(new Integer(10), new Integer(8))));

    }

}
