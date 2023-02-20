package cn.tuyucheng.taketoday.traits

class Employee implements UserTrait {

    @Override
    String name() {
        return 'Bob'
    }

    @Override
    String lastName() {
        return "Marley"
    }
}