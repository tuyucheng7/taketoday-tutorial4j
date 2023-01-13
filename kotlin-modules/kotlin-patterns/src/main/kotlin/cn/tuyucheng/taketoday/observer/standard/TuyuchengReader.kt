package cn.tuyucheng.taketoday.observer.standard

import cn.tuyucheng.taketoday.observer.IObserver

class TuyuchengReader(private var newsletter: TuyuchengNewsletter) : IObserver {
	override fun update() {
		println("New Baeldung article: ${newsletter.newestArticleUrl}")
	}
}