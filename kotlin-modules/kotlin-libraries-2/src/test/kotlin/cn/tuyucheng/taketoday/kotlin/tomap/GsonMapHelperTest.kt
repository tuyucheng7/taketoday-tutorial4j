package cn.tuyucheng.taketoday.kotlin.tomap

import cn.tuyucheng.taketoday.kotlin.tomap.GsonMapHelper.GSON_MAPPER
import cn.tuyucheng.taketoday.kotlin.tomap.GsonMapHelper.KOTLIN_GSON_MAPPER
import cn.tuyucheng.taketoday.kotlin.tomap.ToMapTestFixture.PROJECT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GsonMapHelperTest {

	private val mapWithoutCreatedDate = mapOf(
		"name" to "test1",
		"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION.name,
		"repository" to mapOf(
			"url" to "http://test.baeldung.com/test1"
		),
		"deleted" to false,
		"owner" to null,
		"description" to "a new project"
	)

	private val expected = mapOf(
		"name" to "test1",
		"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION.name,
		"createdDate" to cn.tuyucheng.taketoday.kotlin.tomap.MapHelper.DATE_FORMAT.format(PROJECT.createdDate),
		"repository" to mapOf(
			"url" to "http://test.baeldung.com/test1"
		),
		"deleted" to false,
		"owner" to null,
		"description" to "a new project"
	)

	@Test
	fun whenConvertToMapWithDateFormat_thenGetExpectedDate() {
		assertEquals(
			expected,
			GSON_MAPPER.fromJson(GSON_MAPPER.toJson(PROJECT), Map::class.java)
		)
	}

	@Test
	fun whenConvertFromMap_thenGetExpectedObject() {
		assertEquals(
			PROJECT,
			GSON_MAPPER.fromJson(GSON_MAPPER.toJson(expected), cn.tuyucheng.taketoday.kotlin.tomap.Project::class.java)
		)
	}

	@Test
	fun whenConvertFromMapMissingProperty_thenGetIncorrectObjectWithNullValueForNonNullableProperty() {
		val newProject = GSON_MAPPER.fromJson(
			GSON_MAPPER.toJson(mapWithoutCreatedDate),
			cn.tuyucheng.taketoday.kotlin.tomap.Project::class.java
		)
		assertNull(newProject.createdDate)
	}

	@Test
	fun whenConvertFromMapMissingProperty_thenThrowException() {
		val exception = assertThrows<IllegalArgumentException> {
			KOTLIN_GSON_MAPPER.fromJson(
				KOTLIN_GSON_MAPPER.toJson(mapWithoutCreatedDate),
				cn.tuyucheng.taketoday.kotlin.tomap.Project::class.java
			)
		}
		assertEquals(
			"Value of non-nullable property [${cn.tuyucheng.taketoday.kotlin.tomap.Project::createdDate.name}] cannot be null",
			exception.message
		)
	}
}