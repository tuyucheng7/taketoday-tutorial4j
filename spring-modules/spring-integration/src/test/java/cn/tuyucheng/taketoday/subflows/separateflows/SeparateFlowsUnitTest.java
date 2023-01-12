package cn.tuyucheng.taketoday.subflows.separateflows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SeparateFlowsExample.class})
public class SeparateFlowsUnitTest {
	@Autowired
	private QueueChannel multipleOfThreeChannel;
	@Autowired
	private QueueChannel remainderIsOneChannel;
	@Autowired
	private QueueChannel remainderIsTwoChannel;

	@Autowired
	private SeparateFlowsExample.NumbersClassifier numbersClassifier;

	@Test
	public void whenSendMessagesToMultipleOf3Flow_thenOutputMultiplesOf3() {

		numbersClassifier.multipleofThree(Arrays.asList(1, 2, 3, 4, 5, 6));

		Message<?> outMessage = multipleOfThreeChannel.receive(0);

		assertEquals(outMessage.getPayload(), 3);

		outMessage = multipleOfThreeChannel.receive(0);

		assertEquals(outMessage.getPayload(), 6);
		outMessage = multipleOfThreeChannel.receive(0);
		assertNull(outMessage);
	}

	@Test
	public void whenSendMessagesToRemainderIs1Flow_thenOutputRemainderIs1() {

		numbersClassifier.remainderIsOne(Arrays.asList(1, 2, 3, 4, 5, 6));

		Message<?> outMessage = remainderIsOneChannel.receive(0);

		assertEquals(outMessage.getPayload(), 1);

		outMessage = remainderIsOneChannel.receive(0);

		assertEquals(outMessage.getPayload(), 4);

	}

	@Test
	public void whenSendMessagesToRemainderIs2Flow_thenOutputRemainderIs2() {

		numbersClassifier.remainderIsTwo(Arrays.asList(1, 2, 3, 4, 5, 6));

		Message<?> outMessage = remainderIsTwoChannel.receive(0);

		assertEquals(outMessage.getPayload(), 2);

		outMessage = remainderIsTwoChannel.receive(0);

		assertEquals(outMessage.getPayload(), 5);

	}

}
