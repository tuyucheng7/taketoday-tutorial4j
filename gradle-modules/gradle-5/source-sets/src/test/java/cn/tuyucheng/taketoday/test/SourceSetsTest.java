package cn.tuyucheng.taketoday.test;

import cn.tuyucheng.taketoday.main.SourceSetsObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SourceSetsTest {

	@Test
	public void whenRun_ThenSuccess() {
		SourceSetsObject underTest = new SourceSetsObject("lorem", "ipsum");

		assertThat(underTest.getUser(), is("lorem"));
		assertThat(underTest.getPassword(), is("ipsum"));
	}
}