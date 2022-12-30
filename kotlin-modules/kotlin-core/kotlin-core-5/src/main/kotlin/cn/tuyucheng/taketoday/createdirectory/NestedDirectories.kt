package cn.tuyucheng.taketoday.createdirectory

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
	println("Running Files.createDirectories")
	Files.createDirectories(Paths.get("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/createDirectories/inner"))
	println("Running Files.createDirectories 2nd time does nothing")
	Files.createDirectories(Paths.get("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/createDirectories/inner"))

	println("Running File(...).mkdirs")
	val resultMkdirs: Boolean =
		File("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/mkdirs/inner").mkdirs()
	println("Running File(...).mkdirs result: $resultMkdirs")

	println("Running File(...).mkdirs 2nd")
	val resultMkdirsRerun: Boolean =
		File("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/mkdirs/inner").mkdirs()
	println("Running File(...).mkdirs 2nd result: $resultMkdirsRerun")
}