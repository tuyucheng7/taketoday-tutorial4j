package cn.tuyucheng.taketoday.extendandimplement

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

open class Person(private val firstName: String, private val lastName: String, val age: Int) {
	fun fullName(): String {
		return "$firstName $lastName"
	}
}

interface TuyuchengReader {
	fun readArticles(): String
}

interface TuyuchengAuthor {
	fun writeArticles(): String
}

class Developer(firstName: String, lastName: String, age: Int, private val skills: Set<String>) :
	Person(firstName, lastName, age),
	TuyuchengAuthor, TuyuchengReader {
	override fun readArticles(): String {
		return "${fullName()} enjoys reading articles in these categories: $skills"
	}

	override fun writeArticles(): String {
		return "${fullName()} writes articles in these categories: $skills"
	}
}

class ExtendClassAndImplementInterfaceUnitTest {
	@Test
	fun `given a developer object when calling functions in two interfaces should get expected result`() {
		val developer: Developer = Developer("James", "Bond", 42, setOf("Kotlin", "Java", "Linux"))
		developer.apply {
			assertThat(this).isInstanceOf(Person::class.java)
				.isInstanceOf(TuyuchengReader::class.java)
				.isInstanceOf(TuyuchengAuthor::class.java)
			assertThat(fullName()).isEqualTo("James Bond")
			assertThat(readArticles()).isEqualTo("James Bond enjoys reading articles in these categories: [Kotlin, Java, Linux]")
			assertThat(writeArticles()).isEqualTo("James Bond writes articles in these categories: [Kotlin, Java, Linux]")
		}
	}
}