package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Queue2UnitTest {

    @Test
    @DisplayName("givenTwoStack_whenImplementQueue_thenShouldSuccess")
    void givenTwoStack_whenImplementQueue_thenShouldSuccess() {
        Queue2 queue = new Queue2();
        queue.enQueue(queue, 1);
        queue.enQueue(queue, 2);
        queue.enQueue(queue, 3);
        assertEquals(1, queue.deQueue(queue));
        assertEquals(2, queue.deQueue(queue));
        assertEquals(3, queue.deQueue(queue));
    }
}