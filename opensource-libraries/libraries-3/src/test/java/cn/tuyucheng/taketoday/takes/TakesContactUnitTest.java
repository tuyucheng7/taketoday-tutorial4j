package cn.tuyucheng.taketoday.takes;

import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

import static org.junit.Assert.assertEquals;

public class TakesContactUnitTest {

	@Test
	public void givenTake_whenInvokeActMethod_thenRespond() throws Exception {
		final String resp = new RsPrint(new TakesContact().act(new RqFake())).printBody();
		assertEquals("Contact us at https://www.baeldung.com", resp);
	}

}
