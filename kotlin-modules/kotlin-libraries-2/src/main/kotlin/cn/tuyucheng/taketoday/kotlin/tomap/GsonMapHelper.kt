@file:Suppress("HasPlatformType")

package cn.tuyucheng.taketoday.kotlin.tomap

import com.google.gson.GsonBuilder

object GsonMapHelper {

	private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

	val GSON_MAPPER =
		GsonBuilder().serializeNulls().setDateFormat(cn.tuyucheng.taketoday.kotlin.tomap.GsonMapHelper.DATE_FORMAT)
			.create()

	val KOTLIN_GSON_MAPPER = GsonBuilder()
		.serializeNulls()
		.setDateFormat(cn.tuyucheng.taketoday.kotlin.tomap.GsonMapHelper.DATE_FORMAT)
		.registerTypeAdapterFactory(cn.tuyucheng.taketoday.kotlin.tomap.KotlinTypeAdapterFactory())
		.create()

}

