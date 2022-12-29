package cn.tuyucheng.taketoday.constants

import org.assertj.core.api.Assertions
import org.junit.Test

class ConstantAtTopLevelTest {
	@Test
	fun whenAccessingConstantAtTopLevel_thenItWorks() {
		Assertions.assertThat(CONSTANT_AT_TOP_LEVEL).isEqualTo("constant at top level")
	}
}