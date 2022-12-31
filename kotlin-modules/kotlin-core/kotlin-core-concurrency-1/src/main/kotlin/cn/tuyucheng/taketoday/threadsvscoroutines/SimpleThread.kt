package cn.tuyucheng.taketoday.threadsvscoroutines

class SimpleThread : Thread() {

	override fun run() {
		println("${Thread.currentThread()} has run.")
	}
}