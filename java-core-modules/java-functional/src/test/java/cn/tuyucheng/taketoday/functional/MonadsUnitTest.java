package cn.tuyucheng.taketoday.functional;

import org.junit.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonadsUnitTest {

    @Test
    public void testOptionalAdd() {

        assertEquals(5, Monads.add(Optional.of(new Integer(2)), Optional.of(new Integer(3)))
            .get());

    }

}
