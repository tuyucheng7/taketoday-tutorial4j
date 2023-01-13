package cn.tuyucheng.taketoday.kotlin.tomap

import cn.tuyucheng.taketoday.kotlin.tomap.ReflectionMapHelper.toMapWithOnlyPrimaryConstructorProperties
import cn.tuyucheng.taketoday.kotlin.tomap.ToMapTestFixture.PROJECT
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class ReflectionMapHelperTest {

	@Test
	fun whenConvertToMap_thenGetMapWithAllProperties() {
		assertEquals(
			mapOf(
				"name" to "test1",
				"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION,
				"createdDate" to Date(1000),
				"repository" to mapOf(
					"url" to "http://test.baeldung.com/test1"
				),
				"deleted" to false,
				"owner" to null,
				"description" to "a new project"
			),
			cn.tuyucheng.taketoday.kotlin.tomap.ReflectionMapHelper.toMap(PROJECT)
		)
	}

	@Test
	fun whenConvertToMapWithPrimaryConstructorProperties_thenGetMapWithExpectedProperties() {
		assertEquals(
			mapOf(
				"name" to "test1",
				"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION,
				"createdDate" to Date(1000),
				"repository" to mapOf(
					"url" to "http://test.baeldung.com/test1"
				),
				"deleted" to false,
				"owner" to null
			),
			toMapWithOnlyPrimaryConstructorProperties(PROJECT)
		)
	}
}