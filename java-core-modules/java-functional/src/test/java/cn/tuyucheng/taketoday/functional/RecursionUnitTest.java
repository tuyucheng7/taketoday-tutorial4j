package cn.tuyucheng.taketoday.functional;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecursionUnitTest {

    @Test
    public void testHeadRecursion() {

        assertEquals(120, Recursion.headRecursion(5));

    }

    @Test
    public void testTailRecursion() {

        assertEquals(120, Recursion.tailRecursion(5, 1));

    }

}
