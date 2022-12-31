package cn.tuyucheng.taketoday.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineContextUnitTest {

	@Test
	fun whenRunBlocking_thenCoroutineContextIsAvailable() {
		runBlocking {
			Assertions.assertNotNull(coroutineContext)
		}
	}

	@Test
	fun whenRunBlocking_thenJobIsAvailableInContext() {
		runBlocking {
			Assertions.assertNotNull(coroutineContext[Job])
		}
	}

	@Test
	fun givenEmptyContext_whenAddCoroutineName_thenNewContextContainsCoroutineName() {
		val context = EmptyCoroutineContext
		val newContext = context + CoroutineName("tuyucheng")
		Assertions.assertTrue(newContext != context)
		Assertions.assertEquals("tuyucheng", newContext[CoroutineName]!!.name)
	}

	@Test
	fun givenCoroutineNameAsContext_whenRemoveCoroutineName_thenEmptyContext() {
		val context = CoroutineName("tuyucheng")
		val newContext = context.minusKey(CoroutineName)
		Assertions.assertNull(newContext[CoroutineName])
	}

	@Test
	fun givenNoDispatcher_whenLaunch_thenDispatcherIsEventLoop() {
		runBlocking {
			launch {
				Assertions.assertTrue(
					coroutineContext[ContinuationInterceptor]!!
						.javaClass
						.name.contains("EventLoop")
				)
			}
		}
	}

	@Test
	fun givenDefaultDispatcher_whenLaunch_thenDispatcherIsEventLoop() {
		runBlocking {
			launch(Dispatchers.Default) {
				Assertions.assertTrue(
					coroutineContext[ContinuationInterceptor]!!
						.javaClass
						.name.contains("DefaultScheduler")
				)
			}
		}
	}


	@Test
	fun givenSingleThreadAsDispatcher_whenLaunch_thenDispatcherIsSingleThread() {
		runBlocking {
			launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
				Assertions.assertTrue(
					coroutineContext[ContinuationInterceptor]!!
						.javaClass
						.name.contains("ExecutorCoroutineDispatcher")
				)
			}
		}
	}

	@Test
	fun givenUnconfinedDispatcher_whenAfterResume_thenThreadChanged() {
		runBlocking {
			launch(Dispatchers.Unconfined) {
				Assertions.assertTrue(Thread.currentThread().name.startsWith("main"))
				delay(10)
				Assertions.assertTrue(!Thread.currentThread().name.startsWith("main"))
			}
		}
	}

	@Test
	fun givenCustomDispatcherForTopCoroutine_whenNestedCall_thenChildInheritsDispatcher() {
		runBlocking(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
			launch {
				Assertions.assertTrue(
					coroutineContext[ContinuationInterceptor]!!
						.javaClass
						.name.contains("ExecutorCoroutineDispatcher")
				)
				Assertions.assertTrue(Thread.currentThread().name.startsWith("pool"))
			}
		}
	}
}