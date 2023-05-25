package cn.tuyucheng.taketoday

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(cn.tuyucheng.taketoday.configurationproperties.config.ApiConfiguration::class)
class SpringBootV2Application

fun main(args: Array<String>) {
   runApplication<cn.tuyucheng.taketoday.SpringBootV2Application>(*args)
}
