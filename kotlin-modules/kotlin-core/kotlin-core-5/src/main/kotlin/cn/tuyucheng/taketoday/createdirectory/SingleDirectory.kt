package cn.tuyucheng.taketoday.createdirectory

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
	println("Running Files.createDirectory")
	Files.createDirectory(Paths.get("./java_createDirectory"))

	try {
		println("Running Files.createDirectory 2nd time")
		Files.createDirectory(Paths.get("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/createDirectory"))
	} catch (e: Exception) {
		println("Files.createDirectory throws error on rerun (because directory already exists)")
	}

	try {
		println("Running Files.createDirectory where some directories do not exist")
		Files.createDirectory(Paths.get("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/createDirectory_2/inner"))
	} catch (e: Exception) {
		println("Files.createDirectory throws an error, use `createDirectories` instead")
	}

	println("Running File(...).mkdir")
	val resultMkdir =
		File("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/mkdir").mkdir()
	println("Running File(...).mkdir result: $resultMkdir")

	println("Running File(...).mkdir 2nd time")
	val resultMkdirRerun =
		File("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/mkdir").mkdir()
	println("Running File(...).mkdir 2nd time result: $resultMkdirRerun")

	println("Running File(...).mkdir where some directories do not exist")
	val resultMkdirNested =
		File("./kotlin-modules/kotlin-core/kotlin-core-5/src/main/kotlin/cn/tuyucheng/taketoday/createdirectory/mkdir_2/inner").mkdir()
	println("Running File(...).mkdir where some directories do not exist result: $resultMkdirNested")
}