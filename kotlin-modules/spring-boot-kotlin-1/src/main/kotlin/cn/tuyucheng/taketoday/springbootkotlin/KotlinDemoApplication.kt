package cn.tuyucheng.taketoday.springbootkotlin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

@SpringBootApplication(scanBasePackages = arrayOf("cn.tuyucheng.taketoday.springbootkotlin"), exclude = arrayOf(SecurityAutoConfiguration::class))
class KotlinDemoApplication

fun main(args: Array<String>) {
   SpringApplication.run(KotlinDemoApplication::class.java, *args)
}
