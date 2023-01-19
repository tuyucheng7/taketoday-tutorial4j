package cn.tuyucheng.taketoday.metaprogramming.extension

import cn.tuyucheng.taketoday.metaprogramming.Employee

class StaticEmployeeExtension {

    static Employee getDefaultObj(Employee self) {
        return new Employee(firstName: "firstName", lastName: "lastName", age: 20)
    }
}