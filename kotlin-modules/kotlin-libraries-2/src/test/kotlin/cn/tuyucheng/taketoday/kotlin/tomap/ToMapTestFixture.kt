package cn.tuyucheng.taketoday.kotlin.tomap

import kotlinx.serialization.Serializable
import java.util.*

object ToMapTestFixture {
	val PROJECT = cn.tuyucheng.taketoday.kotlin.tomap.Project(
		name = "test1",
		type = cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION,
		createdDate = Date(1000),
		repository = cn.tuyucheng.taketoday.kotlin.tomap.ProjectRepository(url = "http://test.baeldung.com/test1"),
		owner = null
	).apply {
		description = "a new project"
	}

	val SERIALIZABLE_PROJECT = cn.tuyucheng.taketoday.kotlin.tomap.SerializableProject(
		name = "test1",
		type = cn.tuyucheng.taketoday.kotlin.tomap.ProjectType.APPLICATION,
		createdDate = Date(1000),
		repository = cn.tuyucheng.taketoday.kotlin.tomap.SerializableProjectRepository(url = "http://test.baeldung.com/test1"),
		owner = null
	).apply {
		description = "a new project"
	}

}

enum class ProjectType {
	APPLICATION, CONSOLE, WEB
}

data class ProjectRepository(val url: String)

data class Project(
	val name: String,
	val type: cn.tuyucheng.taketoday.kotlin.tomap.ProjectType,
	val createdDate: Date,
	val repository: cn.tuyucheng.taketoday.kotlin.tomap.ProjectRepository,
	val deleted: Boolean = false,
	val owner: String?
) {
	var description: String? = null
}


@Serializable
data class SerializableProjectRepository(val url: String)

@Serializable
data class SerializableProject(
	val name: String,
	val type: cn.tuyucheng.taketoday.kotlin.tomap.ProjectType,
	@Serializable(cn.tuyucheng.taketoday.kotlin.tomap.KotlinSerializationMapHelper.DateSerializer::class) val createdDate: Date,
	val repository: cn.tuyucheng.taketoday.kotlin.tomap.SerializableProjectRepository,
	val deleted: Boolean = false,
	val owner: String?
) {
	var description: String? = null
}