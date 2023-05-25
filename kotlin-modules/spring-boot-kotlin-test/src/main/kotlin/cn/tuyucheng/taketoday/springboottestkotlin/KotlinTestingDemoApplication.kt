package cn.tuyucheng.taketoday.springboottestkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinTestingDemoApplication

fun main(args: Array<String>) {
   runApplication<KotlinTestingDemoApplication>(*args)
}
