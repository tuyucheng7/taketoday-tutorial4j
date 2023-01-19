package cn.tuyucheng.taketoday.configurationproperties

import cn.tuyucheng.taketoday.configurationproperties.config.ApiConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ApiConfiguration::class)
class SpringBootV2Application

fun main(args: Array<String>) {
	runApplication<SpringBootV2Application>(*args)
}