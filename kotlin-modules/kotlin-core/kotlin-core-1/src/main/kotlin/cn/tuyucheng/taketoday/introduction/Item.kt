package cn.tuyucheng.taketoday.introduction

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport

open class Item(val id: String, val name: String = "unknown_name") {
	open fun getIdOfItem(): String {
		return id
	}
}

class ItemWithCategory(id: String, name: String, val categoryId: String) : Item(id, name) {
	override fun getIdOfItem(): String {
		return id + name
	}
}