plugins {
	id("org.springframework.boot") version "2.7.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("java")
}

group = "cn.tuyucheng.taketoday.gradle"
version = "1.0.0"

repositories {
	maven {
		url = uri("https://maven.aliyun.com/repository/public/")
	}
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:2.7.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.5")
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}