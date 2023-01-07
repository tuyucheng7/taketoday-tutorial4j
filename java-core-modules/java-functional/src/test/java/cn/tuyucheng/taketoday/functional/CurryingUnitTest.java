package cn.tuyucheng.taketoday.functional;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurryingUnitTest {

    @Test
    public void testWeightOnEarth() {

        assertEquals(588.6, Currying.weightOnEarth(60.0), 0.1);

    }

    @Test
    public void testWeightOnMars() {

        assertEquals(225.0, Currying.weightOnMars(60.0), 0.1);

    }
}
