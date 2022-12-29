package cn.tuyucheng.taketoday.late

import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LateInitUnitTest {

	private lateinit var answer: String

	@Test
	fun givenLateInit_WhenNotInitialized_ShouldThrowAnException() {
		assertFailsWith(UninitializedPropertyAccessException::class) {
			answer.length
		}
	}

	@Test
	fun givenLateInit_TheIsInitialized_ReturnsTheInitializationStatus() {
		assertFalse { this::answer.isInitialized }
		answer = "42"
		assertTrue { this::answer.isInitialized }
	}
}