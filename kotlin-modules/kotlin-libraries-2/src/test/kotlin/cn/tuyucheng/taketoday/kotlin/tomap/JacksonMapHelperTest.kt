package cn.tuyucheng.taketoday.kotlin.tomap

import cn.tuyucheng.taketoday.kotlin.tomap.JacksonMapHelper.DEFAULT_JACKSON_MAPPER
import cn.tuyucheng.taketoday.kotlin.tomap.JacksonMapHelper.JACKSON_MAPPER_WITH_DATE_FORMAT
import cn.tuyucheng.taketoday.kotlin.tomap.MapHelper.DATE_FORMAT
import cn.tuyucheng.taketoday.kotlin.tomap.ToMapTestFixture.PROJECT
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class JacksonMapHelperTest {

	private val expected = mapOf(
		"name" to "test1",
		"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION.name,
		"createdDate" to DATE_FORMAT.format(PROJECT.createdDate),
		"repository" to mapOf(
			"url" to "http://test.baeldung.com/test1"
		),
		"deleted" to false,
		"owner" to null,
		"description" to "a new project"
	)

	@Test
	fun whenConvertToMapWithDefaultMapper_thenGetMapWithLongTypeDate() {
		assertEquals(
			mapOf(
				"name" to "test1",
				"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION.name,
				"createdDate" to 1000L,
				"repository" to mapOf(
					"url" to "http://test.baeldung.com/test1"
				),
				"deleted" to false,
				"owner" to null,
				"description" to "a new project"
			), DEFAULT_JACKSON_MAPPER.convertValue(PROJECT, Map::class.java)
		)
	}

	@Test
	fun whenConvertToMapWithDateFormat_thenGetMapWithFormattedDate() {
		assertEquals(expected, JACKSON_MAPPER_WITH_DATE_FORMAT.convertValue(PROJECT, Map::class.java))
	}

	@Test
	fun whenConvertFromMapWithDateFormat_thenGetCorrectObject() {
		assertEquals(
			PROJECT,
			JACKSON_MAPPER_WITH_DATE_FORMAT.convertValue(
				expected,
				cn.tuyucheng.taketoday.kotlin.tomap.Project::class.java
			)
		)
	}

	@Test
	fun whenConvertMapButMissingProperty_thenThrowException() {
		val mapWithoutCreatedDate = mapOf(
			"name" to "test1",
			"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION.name,
			"repository" to mapOf(
				"url" to "http://test.baeldung.com/test1"
			),
			"deleted" to false,
			"owner" to null,
			"description" to "a new project"
		)
		assertThrows<IllegalArgumentException> {
			DEFAULT_JACKSON_MAPPER.convertValue(
				mapWithoutCreatedDate,
				cn.tuyucheng.taketoday.kotlin.tomap.Project::class.java
			)
		}
	}
}