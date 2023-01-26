package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Stack2UnitTest {

    @Test
    @DisplayName("givenStack_whenPushSameItems_themPopShouldCorrect")
    void givenStack_whenPushSameItems_themPopShouldCorrect() {
        Stack2 stack = new Stack2();
        stack.add(1);
        stack.add(2);
        stack.add(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.top());
        stack.remove();
        assertEquals(2, stack.top());
        assertEquals(2, stack.size());
    }
}