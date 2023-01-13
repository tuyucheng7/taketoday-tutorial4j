package cn.tuyucheng.taketoday.injekt

import org.slf4j.LoggerFactory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class SimpleApplication {
	companion object : InjektMain() {
		private val LOG = LoggerFactory.getLogger(cn.tuyucheng.taketoday.injekt.SimpleApplication.Server::class.java)

		@JvmStatic
		fun main(args: Array<String>) {
			cn.tuyucheng.taketoday.injekt.SimpleApplication().run()
		}

		override fun InjektRegistrar.registerInjectables() {
			addSingleton(Config(12345))
			addSingletonFactory { Server(Injekt.get()) }
		}
	}

	data class Config(
		val port: Int
	)

	class Server(private val config: cn.tuyucheng.taketoday.injekt.SimpleApplication.Config) {

		fun start() {
			cn.tuyucheng.taketoday.injekt.SimpleApplication.Companion.LOG.info("Starting server on ${config.port}")
		}
	}

	fun run() {
		Injekt.get<cn.tuyucheng.taketoday.injekt.SimpleApplication.Server>().start()
	}
}
