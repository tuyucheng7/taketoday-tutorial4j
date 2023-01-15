package cn.tuyucheng.taketoday.jmockit;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class PerformerIntegrationTest {

	@Injectable
	private Collaborator collaborator;

	@Tested
	private Performer performer;

	@Test
	public void testThePerformMethod(@Mocked Model model) {
		new Expectations() {{
			model.getInfo();
			result = "bar";
			collaborator.collaborate("bar");
			result = true;
		}};

		performer.perform(model);

		new Verifications() {{
			collaborator.receive(true);
		}};
	}

}
