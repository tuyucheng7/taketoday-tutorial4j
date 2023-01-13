package cn.tuyucheng.taketoday.injekt

import org.slf4j.LoggerFactory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addPerKeyFactory
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class KeyedApplication {
	companion object : InjektMain() {
		private val LOG = LoggerFactory.getLogger(cn.tuyucheng.taketoday.injekt.KeyedApplication::class.java)

		@JvmStatic
		fun main(args: Array<String>) {
			cn.tuyucheng.taketoday.injekt.KeyedApplication().run()
		}

		override fun InjektRegistrar.registerInjectables() {
			val configs = mapOf(
				"google" to Config("googleClientId", "googleClientSecret"),
				"twitter" to Config("twitterClientId", "twitterClientSecret")
			)
			addPerKeyFactory<cn.tuyucheng.taketoday.injekt.KeyedApplication.Config, String> { key -> configs[key]!! }

			addSingletonFactory { App() }
		}
	}

	data class Config(val clientId: String, val clientSecret: String)

	class App {
		fun run() {
			cn.tuyucheng.taketoday.injekt.KeyedApplication.Companion.LOG.info(
				"Google config: {}",
				Injekt.get<cn.tuyucheng.taketoday.injekt.KeyedApplication.Config>("google")
			)
			cn.tuyucheng.taketoday.injekt.KeyedApplication.Companion.LOG.info(
				"Twitter config: {}",
				Injekt.get<cn.tuyucheng.taketoday.injekt.KeyedApplication.Config>("twitter")
			)
		}
	}

	fun run() {
		Injekt.get<cn.tuyucheng.taketoday.injekt.KeyedApplication.App>().run()
	}
}
