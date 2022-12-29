package cn.tuyucheng.taketoday.static

class ConsoleUtils {
	companion object {
		@JvmStatic
		fun debug(debugMessage: String) {
			println("[DEBUG] $debugMessage")
		}
	}
}