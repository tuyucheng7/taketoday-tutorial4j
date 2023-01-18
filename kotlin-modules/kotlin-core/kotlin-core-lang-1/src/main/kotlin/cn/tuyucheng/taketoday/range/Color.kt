package cn.tuyucheng.taketoday.range

enum class Color(val rbg: Int) {
	BLUE(0x0000FF),
	GREEN(0x00FF00),
	RED(0xFF0000),
	MAGENTA(0xFF00FF),
	YELLOW(0xFFFF00);
}

fun main(args: Array<String>) {
	println(Color.values().toList())

	val red = Color.RED
	val yellow = Color.YELLOW
	val range = red..yellow

	println(range.contains(Color.MAGENTA)) // print false
	println(range.contains(Color.BLUE)) // print false
	println(range.contains(Color.GREEN)) // print true
}