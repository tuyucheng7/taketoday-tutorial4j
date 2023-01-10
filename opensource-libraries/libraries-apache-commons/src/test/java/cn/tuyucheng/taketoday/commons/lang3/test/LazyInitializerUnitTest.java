package cn.tuyucheng.taketoday.commons.lang3.test;

import cn.tuyucheng.taketoday.commons.lang3.beans.User;
import cn.tuyucheng.taketoday.commons.lang3.beans.UserInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LazyInitializerUnitTest {

	@Test
	public void givenLazyInitializerInstance_whenCalledget_thenCorrect() throws ConcurrentException {
		UserInitializer userInitializer = new UserInitializer();
		assertThat(userInitializer.get()).isInstanceOf(User.class);
	}
}
