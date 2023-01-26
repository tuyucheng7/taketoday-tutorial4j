package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackUnitTest {

    @Test
    @DisplayName("givenStack_whenPushSameItems_themPopShouldCorrect")
    void givenStack_whenPushSameItems_themPopShouldCorrect() {
        Stack stack = new Stack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.top());
        stack.pop();
        assertEquals(2, stack.top());
        assertEquals(2, stack.size());
    }
}