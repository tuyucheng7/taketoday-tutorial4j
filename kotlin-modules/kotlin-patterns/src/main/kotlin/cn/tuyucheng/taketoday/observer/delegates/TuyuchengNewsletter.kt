package cn.tuyucheng.taketoday.observer.delegates

import kotlin.properties.Delegates

class TuyuchengNewsletter {
	val newestArticleObservers = mutableListOf<(String) -> Unit>()
	var newestArticleUrl: String by Delegates.observable("") { _, _, newValue ->
		newestArticleObservers.forEach { it(newValue) }
	}
}