package cn.tuyucheng.taketoday.itest;

import cn.tuyucheng.taketoday.main.SourceSetsObject;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SourceSetsItest {

	@Test
	public void givenImmutableList_whenRun_ThenSuccess() {
		SourceSetsObject underTest = new SourceSetsObject("lorem", "ipsum");
		List<String> someStrings = ImmutableList.of("Tuyucheng", "is", "cool");

		assertThat(underTest.getUser(), is("lorem"));
		assertThat(underTest.getPassword(), is("ipsum"));
		assertThat(someStrings.size(), is(3));
	}
}