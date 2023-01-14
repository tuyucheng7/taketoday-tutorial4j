package cn.tuyucheng.taketoday.features;

interface HelloWorld {
	default String hello() {
		return "world";
	}
}