package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecialStackUnitTest {

    @Test
    @DisplayName("givenStack_whenPushItemsAndGetMin_thenReturnCorrect")
    void givenStack_whenPushItemsAndGetMin_thenReturnCorrect() {
        SpecialStack stack = new SpecialStack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        assertEquals(4, stack.pop());
        assertEquals(1, stack.getMin());
    }
}