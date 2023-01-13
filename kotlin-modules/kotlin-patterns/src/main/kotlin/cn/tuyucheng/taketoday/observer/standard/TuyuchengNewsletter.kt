package cn.tuyucheng.taketoday.observer.standard

import cn.tuyucheng.taketoday.observer.IObservable
import cn.tuyucheng.taketoday.observer.IObserver

class TuyuchengNewsletter : IObservable {
	override val observers: ArrayList<IObserver> = ArrayList()
	var newestArticleUrl = ""
		set(value) {
			field = value
			sendUpdateEvent()
		}
}