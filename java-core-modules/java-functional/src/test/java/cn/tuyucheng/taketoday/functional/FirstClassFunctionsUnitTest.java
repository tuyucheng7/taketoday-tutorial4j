package cn.tuyucheng.taketoday.functional;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstClassFunctionsUnitTest {

    @Test
    public void testSortingWithoutLambda() {

        assertEquals(new Integer(8), FirstClassFunctions.sortWithoutLambda(Arrays.asList(new Integer(10), new Integer(8)))
            .get(0));

    }

    @Test
    public void testSortingWithLambda() {

        assertEquals(new Integer(8), FirstClassFunctions.sortWithLambda(Arrays.asList(new Integer(10), new Integer(8)))
            .get(0));

    }

}
