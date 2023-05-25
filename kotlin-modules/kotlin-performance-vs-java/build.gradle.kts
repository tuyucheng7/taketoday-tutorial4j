import jdk.internal.vm.vector.VectorSupport.test
import jdk.tools.jlink.resources.plugins

plugins {
   java
   kotlin("jvm") version "1.6.10"
   id("me.champeau.jmh") version "0.6.6"
}

group = "cn.tuyucheng.taketoday"
version = "1.0.0"

repositories {
   mavenCentral()
   mavenCentral()
}

dependencies {
   implementation("org.jetbrains.kotlin:kotlin-stdlib")
   testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
   testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}
tasks {
   test {
      useJUnitPlatform()
   }
}