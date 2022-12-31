plugins {
	kotlin("jvm") version "1.6.10"
}

repositories {
	maven {
		url = uri("https://maven.aliyun.com/repository/public/")
	}
}

kotlin {
	explicitApiWarning()
}