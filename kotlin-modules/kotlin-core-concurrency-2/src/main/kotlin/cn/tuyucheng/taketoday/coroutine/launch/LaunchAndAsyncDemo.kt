package cn.tuyucheng.taketoday.coroutine.launch

import kotlinx.coroutines.*
import java.util.concurrent.Executors.newSingleThreadExecutor

class LaunchAndAsyncDemo(
      executor: CoroutineScope = CoroutineScope(newSingleThreadExecutor().asCoroutineDispatcher())
) : CoroutineScope by executor {

   fun simpleLaunch() {
      val job: Job = launch {
         println("${Thread.currentThread().name}: I am executed, but you only see the side-effects")
      }
      runBlocking { job.join() }
   }

   @OptIn(DelicateCoroutinesApi::class)
   fun launchSeveralCoroutinesInDifferentScopes() {

      val custom = launch {
         println("${Thread.currentThread().name}: I am launched in a class scope")
      }

      val global = GlobalScope.launch {
         println("${Thread.currentThread().name}: I am launched in a global scope")
      }
   }

   fun launchCoroutineForAResult() {
      val futureResult: Deferred<String> = async {
         "Hello, world!"
      }

      runBlocking {
         println(futureResult.await())
      }
   }

   fun asyncWithLazy() {
      val first = async(start = CoroutineStart.LAZY) {
         println("${Thread.currentThread().name}: I am lazy and launched only now")
         "Hello, "
      }

      val second = async {
         println("${Thread.currentThread().name}: I am eager!")
         "world!"
      }
      runBlocking {
         println(first.await() + second.await())
      }
   }

}