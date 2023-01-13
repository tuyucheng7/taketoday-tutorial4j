package cn.tuyucheng.taketoday.kotlin.tomap

import cn.tuyucheng.taketoday.kotlin.tomap.KotlinSerializationMapHelper.JSON
import cn.tuyucheng.taketoday.kotlin.tomap.ToMapTestFixture.SERIALIZABLE_PROJECT
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinSerializationMapHelperTest {

	@Test
	fun whenConvertToMap_thenGetExpected() {
		val map = cn.tuyucheng.taketoday.kotlin.tomap.KotlinSerializationMapHelper.toMap(SERIALIZABLE_PROJECT)
		val expected = mapOf(
			"name" to "test1",
			"type" to cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION.name,
			"createdDate" to cn.tuyucheng.taketoday.kotlin.tomap.MapHelper.DATE_FORMAT.format(SERIALIZABLE_PROJECT.createdDate),
			"repository" to mapOf(
				"url" to "http://test.baeldung.com/test1"
			),
			"deleted" to false.toString(),
			"owner" to null,
			"description" to "a new project"
		)
		assertEquals(expected, map)
	}

	@Test
	fun whenConvertFromMap_thenGetExpectedObject() {
		val jsonObject = JSON.encodeToJsonElement(SERIALIZABLE_PROJECT).jsonObject
		val newProject = JSON.decodeFromJsonElement<cn.tuyucheng.taketoday.kotlin.tomap.SerializableProject>(jsonObject)
		assertEquals(SERIALIZABLE_PROJECT, newProject)
	}
}
