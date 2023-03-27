package cn.tuyucheng.taketoday.constants

import cn.tuyucheng.taketoday.constants.ConstantsBestPractices.Companion.CONSTANT_IN_COMPANION_OBJECT
import org.assertj.core.api.Assertions
import org.junit.Test

class ConstantInCompanionObjectTest {

	@Test
	fun whenAccessingConstantInCompanionObject_thenItWorks() {
		Assertions.assertThat(CONSTANT_IN_COMPANION_OBJECT).isEqualTo("constant at in companion object")
	}
}