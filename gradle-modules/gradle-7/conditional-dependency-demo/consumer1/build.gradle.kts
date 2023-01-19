plugins {
	id("java")
}

group = "cn.tuyucheng.taketoday.gradle"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

	if (project.hasProperty("isLocal")) {
		implementation("cn.tuyucheng.taketoday.gradle:provider1")
	} else {
		implementation("cn.tuyucheng.taketoday.gradle:provider2")
	}
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}