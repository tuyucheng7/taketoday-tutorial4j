package cn.tuyucheng.taketoday.concurrent.delayqueue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
class DelayQueueIntegrationTest {

	@Test
	void givenDelayQueue_whenProduceElement_thenShouldConsumeAfterGivenDelay() throws InterruptedException {
		// given
		ExecutorService executor = Executors.newFixedThreadPool(2);
		BlockingQueue<DelayObject> queue = new DelayQueue<>();
		int numberOfElementsToProduce = 2;
		int delayOfEachProducedMessageMilliseconds = 500;
		DelayQueueConsumer consumer = new DelayQueueConsumer(queue, numberOfElementsToProduce);
		DelayQueueProducer producer = new DelayQueueProducer(queue, numberOfElementsToProduce, delayOfEachProducedMessageMilliseconds);

		// when
		executor.submit(producer);
		executor.submit(consumer);

		// then
		executor.awaitTermination(5, TimeUnit.SECONDS);
		executor.shutdown();

		assertEquals(consumer.numberOfConsumedElements.get(), numberOfElementsToProduce);
	}

	@Test
	void givenDelayQueue_whenProduceElementWithHugeDelay_thenConsumerWasNotAbleToConsumeMessageInGivenTime() throws InterruptedException {
		// given
		ExecutorService executor = Executors.newFixedThreadPool(2);
		BlockingQueue<DelayObject> queue = new DelayQueue<>();
		int numberOfElementsToProduce = 1;
		int delayOfEachProducedMessageMilliseconds = 10_000;
		DelayQueueConsumer consumer = new DelayQueueConsumer(queue, numberOfElementsToProduce);
		DelayQueueProducer producer = new DelayQueueProducer(queue, numberOfElementsToProduce, delayOfEachProducedMessageMilliseconds);

		// when
		executor.submit(producer);
		executor.submit(consumer);

		// then
		executor.awaitTermination(5, TimeUnit.SECONDS);
		executor.shutdown();

		assertEquals(consumer.numberOfConsumedElements.get(), 0);
	}

	@Test
	void givenDelayQueue_whenProduceElementWithNegativeDelay_thenConsumeMessageImmediately() throws InterruptedException {
		// given
		ExecutorService executor = Executors.newFixedThreadPool(2);
		BlockingQueue<DelayObject> queue = new DelayQueue<>();
		int numberOfElementsToProduce = 1;
		int delayOfEachProducedMessageMilliseconds = -10_000;
		DelayQueueConsumer consumer = new DelayQueueConsumer(queue, numberOfElementsToProduce);
		DelayQueueProducer producer = new DelayQueueProducer(queue, numberOfElementsToProduce, delayOfEachProducedMessageMilliseconds);

		// when
		executor.submit(producer);
		executor.submit(consumer);

		// then
		executor.awaitTermination(1, TimeUnit.SECONDS);
		executor.shutdown();

		assertEquals(consumer.numberOfConsumedElements.get(), 1);
	}
}