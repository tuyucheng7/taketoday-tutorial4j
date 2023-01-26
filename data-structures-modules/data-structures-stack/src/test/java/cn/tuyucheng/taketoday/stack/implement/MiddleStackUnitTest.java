package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiddleStackUnitTest {

    @Test
    @DisplayName("givenMiddleStack_whenPushSameItemsAndFindMiddle_thenReturnCorrect")
    void givenMiddleStack_whenPushSameItemsAndFindMiddle_thenReturnCorrect() {
        MiddleStack stack = new MiddleStack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);
        assertEquals(3, stack.findMiddle());
        assertEquals(5, stack.pop());
        assertEquals(3, stack.findMiddle());
        stack.deleteMiddle();
        assertEquals(2, stack.findMiddle());
    }
}