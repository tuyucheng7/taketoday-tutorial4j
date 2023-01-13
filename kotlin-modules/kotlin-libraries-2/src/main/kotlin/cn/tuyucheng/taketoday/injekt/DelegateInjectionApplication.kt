package cn.tuyucheng.taketoday.injekt

import org.slf4j.LoggerFactory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addFactory
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import uy.kohesive.injekt.injectValue
import java.util.*

class DelegateInjectionApplication {
	companion object : InjektMain() {
		private val LOG =
			LoggerFactory.getLogger(cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication::class.java)

		@JvmStatic
		fun main(args: Array<String>) {
			cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication().run()
		}

		override fun InjektRegistrar.registerInjectables() {
			addFactory {
				val value = FactoryInstance("Factory" + UUID.randomUUID().toString())
				LOG.info("Constructing instance: {}", value)
				value
			}

			addSingletonFactory {
				val value = SingletonInstance("Singleton" + UUID.randomUUID().toString())
				LOG.info("Constructing singleton instance: {}", value)
				value
			}

			addSingletonFactory { App() }
		}
	}

	data class FactoryInstance(val value: String)
	data class SingletonInstance(val value: String)

	class App {
		private val instance: cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.FactoryInstance by injectValue()
		private val lazyInstance: cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.FactoryInstance by injectLazy()
		private val singleton: cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.SingletonInstance by injectValue()
		private val lazySingleton: cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.SingletonInstance by injectLazy()

		fun run() {
			for (i in 1..5) {
				cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.Companion.LOG.info(
					"Instance {}: {}",
					i,
					instance
				)
			}
			for (i in 1..5) {
				cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.Companion.LOG.info(
					"Lazy Instance {}: {}",
					i,
					lazyInstance
				)
			}
			for (i in 1..5) {
				cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.Companion.LOG.info(
					"Singleton {}: {}",
					i,
					singleton
				)
			}
			for (i in 1..5) {
				cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.Companion.LOG.info(
					"Lazy Singleton {}: {}",
					i,
					lazySingleton
				)
			}
		}
	}

	fun run() {
		Injekt.get<cn.tuyucheng.taketoday.injekt.DelegateInjectionApplication.App>().run()
	}
}
