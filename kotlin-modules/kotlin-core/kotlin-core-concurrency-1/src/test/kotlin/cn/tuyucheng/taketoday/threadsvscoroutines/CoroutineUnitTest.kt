package cn.tuyucheng.taketoday.threadsvscoroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CoroutineUnitTest {

	@Test
	fun whenCreateCoroutineWithLaunchWithoutContext_thenRun() = runBlocking {
		val job = launch {
			println("${Thread.currentThread()} has run.")
		}

	}

	@Test
	fun whenCreateCoroutineWithLaunchWithDefaultContext_thenRun() = runBlocking {
		val job = launch(Dispatchers.Default) {
			println("${Thread.currentThread()} has run.")
		}
	}

	@Test
	fun whenCreateCoroutineWithLaunchWithUnconfinedContext_thenRun() = runBlocking {
		val job = launch(Dispatchers.Unconfined) {
			println("${Thread.currentThread()} has run.")
		}
	}

	@Test
	fun whenCreateCoroutineWithLaunchWithDedicatedThread_thenRun() = runBlocking {
		val job = launch(newSingleThreadContext("dedicatedThread")) {
			println("${Thread.currentThread()} has run.")
		}

	}

	@Test
	fun whenCreateAsyncCoroutine_thenRun() = runBlocking {
		val deferred = async(Dispatchers.IO) {
			return@async "${Thread.currentThread()} has run."
		}

		val result = deferred.await()
		println(result)
	}
}