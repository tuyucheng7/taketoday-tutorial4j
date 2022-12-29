package cn.tuyucheng.taketoday.kotlin.objects

class StaticClass {
	companion object {
		@JvmStatic
		val staticField = 42
	}
}