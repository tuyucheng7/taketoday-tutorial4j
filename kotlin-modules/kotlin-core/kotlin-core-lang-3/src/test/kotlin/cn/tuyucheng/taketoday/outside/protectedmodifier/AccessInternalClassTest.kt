package cn.tuyucheng.taketoday.outside.protectedmodifier

import cn.tuyucheng.taketoday.protectedmodifier.InternalClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class AccessInternalClassTest {

	@Test
	fun whenCallInternalClass_thenItWorks() {
		val internalClass = InternalClass()
		assertThat(internalClass.helloFromInternalFunction()).isEqualTo("Hello")
	}
}