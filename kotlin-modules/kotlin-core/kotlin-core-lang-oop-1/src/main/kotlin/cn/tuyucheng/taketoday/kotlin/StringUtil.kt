@file:JvmName("Strings")

package cn.tuyucheng.taketoday.kotlin

fun String.escapeForXml(): String {
	return this
		.replace("&", "&amp;")
		.replace("<", "&lt;")
		.replace(">", "&gt;")
}