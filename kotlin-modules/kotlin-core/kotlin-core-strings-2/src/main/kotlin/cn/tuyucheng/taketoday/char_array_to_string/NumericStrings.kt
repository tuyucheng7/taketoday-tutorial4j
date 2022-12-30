package cn.tuyucheng.taketoday.char_array_to_string

object NumericStrings {
	fun stringConstructor() {
		val charArray = charArrayOf('t', 'u', 'y', 'u')
		val convertedString = String(charArray)
	}

	fun joinToString() {
		val charArray: Array<Char> = arrayOf('t', 'u', 'y', 'u')
		val convertedString = charArray.joinToString()
	}

	fun stringBuilder() {
		val charArray = charArrayOf('t', 'u', 'y', 'u')
		val convertedString = StringBuilder().append(charArray).toString()
	}
}