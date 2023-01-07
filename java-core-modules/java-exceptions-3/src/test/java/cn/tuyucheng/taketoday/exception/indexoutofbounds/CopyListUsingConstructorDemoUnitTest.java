package cn.tuyucheng.taketoday.exception.indexoutofbounds;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CopyListUsingConstructorDemoUnitTest {

    @Test
    void whenCopyListUsingConstructor_thenMakeACopyOfList() {
        List<Integer> source = Arrays.asList(11, 22, 33);

        assertEquals(source, CopyListUsingConstructorDemo.copyList(source));
    }
}
