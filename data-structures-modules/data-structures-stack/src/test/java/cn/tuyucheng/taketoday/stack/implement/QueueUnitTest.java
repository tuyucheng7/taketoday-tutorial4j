package cn.tuyucheng.taketoday.stack.implement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueueUnitTest {

    @Test
    @DisplayName("givenTwoStack_whenImplementQueue_thenShouldSuccess")
    void givenTwoStack_whenImplementQueue_thenShouldSuccess() {
        Queue queue = new Queue();
        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);
        assertEquals(1, queue.deQueue());
        assertEquals(2, queue.deQueue());
        assertEquals(3, queue.deQueue());
    }
}