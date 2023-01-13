package cn.tuyucheng.taketoday.receiver

fun main() {
	"Tuyucheng".applyThenReturn { n -> println(n.toUpperCase()) }
	"Tuyucheng".apply { println(toUpperCase()) }
}

fun <T> T.applyThenReturn(f: (T) -> Unit): T {
	f(this)
	return this
}

fun <T> T.apply(f: T.() -> Unit): T {
	f() // or this.f()
	return this
}