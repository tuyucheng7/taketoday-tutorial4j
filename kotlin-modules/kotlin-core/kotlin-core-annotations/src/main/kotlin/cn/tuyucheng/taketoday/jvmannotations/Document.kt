package cn.tuyucheng.taketoday.jvmannotations

interface Document {

	@JvmDefault
	fun getTypeDefault() = "document"

	fun getType() = "document"
}