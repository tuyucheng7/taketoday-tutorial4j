package cn.tuyucheng.taketoday.injekt

import org.slf4j.LoggerFactory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class ModularApplication {
	class ConfigModule(private val port: Int) : InjektModule {
		override fun InjektRegistrar.registerInjectables() {
			addSingleton(Config(port))
		}
	}

	object ServerModule : InjektModule {
		override fun InjektRegistrar.registerInjectables() {
			addSingletonFactory { Server(Injekt.get()) }
		}
	}

	companion object : InjektMain() {
		private val LOG = LoggerFactory.getLogger(cn.tuyucheng.taketoday.injekt.ModularApplication.Server::class.java)

		@JvmStatic
		fun main(args: Array<String>) {
			cn.tuyucheng.taketoday.injekt.ModularApplication().run()
		}

		override fun InjektRegistrar.registerInjectables() {
			importModule(ConfigModule(12345))
			importModule(ServerModule)
		}
	}

	data class Config(
		val port: Int
	)

	class Server(private val config: cn.tuyucheng.taketoday.injekt.ModularApplication.Config) {

		fun start() {
			cn.tuyucheng.taketoday.injekt.ModularApplication.Companion.LOG.info("Starting server on ${config.port}")
		}
	}

	fun run() {
		Injekt.get<cn.tuyucheng.taketoday.injekt.ModularApplication.Server>().start()
	}
}
