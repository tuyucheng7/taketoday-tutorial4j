---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

Getters和Setters在获取和更新封装类外部变量的值方面发挥着重要作用。setter更新变量的值，而getter读取变量的值。

[在本教程中，我们将讨论不使用getters/setters](https://www.baeldung.com/intro-to-project-lombok)的问题、它们的重要性，以及在用Java实现它们时要避免的常见错误。

## 2.Java中没有getter和setter的生活

考虑一下我们想要根据某些条件更改对象状态的情况。如果没有setter方法，我们如何实现这一点？

-   将变量标记为public、protected或default
-   使用点(.)运算符更改值

让我们看看这样做的后果。

## 3.不使用getter和setter访问变量

首先，为了在没有getter/setter的情况下访问类外部的变量，我们必须将它们标记为public、protected或default。因此，我们正在失去对数据的控制并损害基本的[OOP](https://www.baeldung.com/cs/oop-modeling-real-world)原则——[封装](https://www.baeldung.com/java-oop)。

其次，由于任何人都可以直接从类外部更改非私有字段，因此我们无法实现不变性。

第三，我们无法为变量的变化提供任何条件逻辑。假设我们有一个带有字段retirementAge的Employee类：

```java
public class Employee {
    public String name;
    public int retirementAge;

// Constructor, but no getter/setter
}
```

请注意，这里我们将字段设置为公共字段以允许从类Employee外部进行访问。现在，我们需要更改员工的退休年龄：

```java
public class RetirementAgeModifier {

    private Employee employee = new Employee("John", 58);

    private void modifyRetirementAge(){
        employee.retirementAge=18;
    }
}
```

在这里，Employee类的任何客户都可以轻松地使用retirementAge字段做他们想做的事。无法验证更改。

第四，我们如何实现类外部对字段的只读或只写访问？

有getter和setter来拯救你。

## 4.Java中Getters和Setters的意义

在众多优点中，让我们介绍使用getter和setter的一些最重要的好处：

-   它帮助我们实现封装，用于将结构化数据对象的状态隐藏在类中，防止未经授权的直接访问它们
-   通过将字段声明为私有并仅使用getter来实现不变性
-   getter和setter还允许额外的功能，如验证、错误处理，这些功能将来可以更容易地添加。因此我们可以添加条件逻辑并根据需要提供行为
-   我们可以为字段提供不同的访问级别；例如，get(读访问)可能是公开的，而set(写访问)可能是受保护的
-   控制正确设置属性的值
-   通过getter和setter，我们实现了OOP的另一个关键原则，即抽象，即隐藏实现细节，以便任何人都不能直接在其他类或模块中使用字段

## 5.避免错误

以下是在实现getter和setter时要避免的最常见错误。

### 5.1.将getter和setter与公共变量一起使用

可以使用点(.)运算符在类外部访问公共变量。对公共变量使用getter和setter是没有意义的：

```java
public class Employee {
    public String name;
    public int retirementAge;

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    } 
    // getter/setter for retirementAge
}
```

在这种情况下，任何可以使用getter和setter完成的事情也可以通过简单地公开该字段来完成。

根据经验，我们需要始终根据实现封装的需要使用最受限制的访问修饰符。

### 5.2.直接在Setter方法中分配对象引用

当我们直接在setter方法中分配对象引用时，这两个引用都指向内存中的单个对象。因此，使用任何引用变量所做的更改实际上是在同一个对象上进行的：

```java
public void setEmployee(Employee employee) {
    this.employee = employee;
}
```

[但是，我们可以使用深拷贝](https://www.baeldung.com/java-deep-copy)将所有元素从一个对象复制到另一个对象。因此，该对象的状态独立于现有(传递的)员工对象：

```java
public void setEmployee(Employee employee) {
    this.employee.setName(employee.getName());
    this.employee.setRetirementAge(employee.getRetirementAge());
}
```

### 5.3.直接从Getter方法返回对象引用

同样，如果getter方法直接返回对象的引用，任何人都可以从外部代码使用这个引用来改变对象的状态：

```java
public Employee getEmployee() {
    return this.employee;
}
```

让我们使用此getEmployee()方法并更改退休年龄：

```java
private void modifyAge() {
    Employee employeeTwo = getEmployee();
    employeeTwo.setRetirementAge(65);
}
```

这导致原始对象的不可恢复的丢失。

因此，我们应该返回对象的副本，而不是从getter方法返回引用。一种这样的方式如下：

```java
public Employee getEmployee() {
    return new Employee(this.employee.getName(), this.employee.getRetirementAge());
}
```

但是，我们还应该记住，在getter或setter中创建对象的副本可能并不总是最佳实践。例如，在循环中调用上述getter方法可能会导致昂贵的操作。

另一方面，如果我们希望我们的集合保持不可修改，那么从getter返回集合的副本是有意义的。然后，我们必须确定哪种方法最适合特定情况。

### 5.4.添加不必要的getter和setter

通过拥有getter和setter，我们可以控制成员变量的访问和赋值。但是，在许多地方，事实证明这是不必要的。此外，它使代码冗长：

```java
private String name;

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}
```

简单地为类中的私有字段定义公共的getter和setter相当于在没有getter和setter的情况下将字段公开。因此，深思熟虑地决定是否为所有字段定义访问器方法总是明智的。

## 六，总结

在本教程中，我们讨论了在Java中使用getter和setter的优缺点。我们还讨论了在实现getter和setter时要避免的一些常见错误，以及如何正确使用它们。
