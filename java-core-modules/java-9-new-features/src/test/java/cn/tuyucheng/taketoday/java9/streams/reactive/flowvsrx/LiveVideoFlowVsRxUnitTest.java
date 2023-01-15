package cn.tuyucheng.taketoday.java9.streams.reactive.flowvsrx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LiveVideoFlowVsRxUnitTest {

	private final static long SLOW_CONSUMER_DELAY = 30;
	private final static long FAST_CONSUMER_DELAY = 1;
	private final static long PRODUCER_DELAY = 1;
	private final static int BUFFER_SIZE = 10;
	private final static long AWAIT = 1000;

	@Test
	void givenSlowVideoPlayer_whenSubscribedToFlowApiLiveVideo_thenExpectErrorOnBackPressure() {
		AtomicLong errors = new AtomicLong();

		FlowApiLiveVideo.streamLiveVideo(PRODUCER_DELAY, SLOW_CONSUMER_DELAY, BUFFER_SIZE, errors::incrementAndGet);

		await().atMost(AWAIT, TimeUnit.MILLISECONDS)
				.untilAsserted(() -> Assertions.assertTrue(errors.get() > 0));
	}

	@Test
	void givenFastVideoPlayer_whenSubscribedToFlowApiLiveVideo_thenExpectNoErrorOnBackPressure() throws InterruptedException {
		AtomicLong errors = new AtomicLong();

		FlowApiLiveVideo.streamLiveVideo(PRODUCER_DELAY, FAST_CONSUMER_DELAY, BUFFER_SIZE, errors::incrementAndGet);

		Thread.sleep(AWAIT);
		assertEquals(0, errors.get());
	}

	@Test
	void givenSlowVideoPlayer_whenSubscribedToRxJavaLiveVideo_thenExpectErrorOnBackPressure() {
		AtomicLong errors = new AtomicLong();

		RxJavaLiveVideo.streamLiveVideo(PRODUCER_DELAY, SLOW_CONSUMER_DELAY, BUFFER_SIZE, errors::incrementAndGet);

		await().atMost(AWAIT, TimeUnit.MILLISECONDS)
				.untilAsserted(() -> Assertions.assertTrue(errors.get() > 0));
	}

	@Test
	void givenFastVideoPlayer_whenSubscribedToRxJavaLiveVideo_thenExpectNoErrorOnBackPressure() throws InterruptedException {
		AtomicLong errors = new AtomicLong();

		RxJavaLiveVideo.streamLiveVideo(PRODUCER_DELAY, FAST_CONSUMER_DELAY, BUFFER_SIZE, errors::incrementAndGet);

		Thread.sleep(AWAIT);
		assertEquals(0, errors.get());
	}
}