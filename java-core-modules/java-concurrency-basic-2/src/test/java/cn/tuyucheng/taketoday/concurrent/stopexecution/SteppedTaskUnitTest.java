package cn.tuyucheng.taketoday.concurrent.stopexecution;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SteppedTaskUnitTest {

	@Test
	void run() throws InterruptedException {
		List<Step> steps = Stream.of(
				new Step(1),
				new Step(2),
				new Step(3))
			.collect(Collectors.toList());

		Thread thread = new Thread(new SteppedTask(steps));
		thread.start();
		thread.interrupt();
		thread.join();
	}
}