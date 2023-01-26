package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KStackUnitTest {

    @Test
    @DisplayName("givenKStack_whenPushSameItemsAndPop_thenCorrect")
    void givenKStack_whenPushSameItemsAndPop_thenCorrect() {
        KStack stack = new KStack(3, 10);
        stack.push(15, 2);
        stack.push(45, 2);
        stack.push(17, 1);
        stack.push(49, 1);
        stack.push(39, 1);
        stack.push(11, 0);
        stack.push(9, 0);
        stack.push(7, 0);
        assertEquals(7, stack.pop(0));
        assertEquals(39, stack.pop(1));
        assertEquals(45, stack.pop(2));
    }
}