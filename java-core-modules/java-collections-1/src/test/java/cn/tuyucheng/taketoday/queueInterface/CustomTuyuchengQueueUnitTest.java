package cn.tuyucheng.taketoday.queueInterface;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomTuyuchengQueueUnitTest {

    private CustomTuyuchengQueue<Integer> customQueue;

    @Before
    public void setUp() throws Exception {
        customQueue = new CustomTuyuchengQueue<>();
    }

    @Test
    public void givenQueueWithTwoElements_whenElementsRetrieved_checkRetrievalCorrect() {
        customQueue.add(7);
        customQueue.add(5);

        int first = customQueue.poll();
        int second = customQueue.poll();

        assertEquals(7, first);
        assertEquals(5, second);
    }
}