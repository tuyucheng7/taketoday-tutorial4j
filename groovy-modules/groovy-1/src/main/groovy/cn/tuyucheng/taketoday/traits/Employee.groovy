package cn.tuyucheng.taketoday.traits

class Employee implements UserTrait {

	String name() {
		return 'Bob'
	}

	String lastName() {
		return "Marley"
	}
}