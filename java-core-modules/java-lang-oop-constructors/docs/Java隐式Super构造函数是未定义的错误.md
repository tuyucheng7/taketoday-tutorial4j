## 1. 概述

在本教程中，我们将仔细研究Java的“**implicit super constructor is undefined**”错误。首先，我们将创建一个示例来说明如何生成它。接下来，我们将解释异常的主要原因，稍后我们将看到如何解决它。

## 2. 示例

现在，让我们看一个生成编译错误““Implicit super constructor X() is undefined. Must explicitly invoke another constructor”的示例。

在这里，X表示由看到此错误的任何子类扩展的父类。

首先，让我们创建一个父类Person：

```java
public class Person {

    String name;
    Integer age;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    // setters and getters
}
```

现在，让我们创建一个子类Employee，其父类是Person：

```java
public class Employee extends Person {

    Double salary;

    public Employee(String name, Integer age, Double salary) {
        this.salary = salary;
    }

    // setters and getters
}
```

现在，在我们的IDE中，我们将看到错误：

```bash
Implicit super constructor Person() is undefined. Must explicitly invoke another constructor
```

**在某些情况下，如果子类没有构造函数，我们可能会遇到类似的错误**。

例如，让我们考虑没有构造函数的Employee：

```java
public class Employee extends Person {

    Double salary;

    // setters and getters
}
```

我们将在我们的IDE中看到错误：

```bash
Implicit super constructor Person() is undefined for default constructor. Must define an explicit constructor
```

## 3. 原因

在Java继承中，[构造函数链](https://www.baeldung.com/java-chain-constructors)是指使用super方法调用一系列构造函数，以将构造函数与父类链接在一起。**子类构造函数必须显式或隐式调用超类构造函数。无论哪种方式，都必须定义父类构造函数**。

没有父类的类将Object类作为其父类。Java中的Object类有一个没有参数的构造函数。

当一个类没有构造函数时，**编译器会添加一个不带参数的默认构造函数，并且在第一条语句中，编译器会插入对super的调用**-调用Object类的构造函数。

假设我们的Person类不包含任何构造函数并且没有父类。一旦我们编译，我们可以看到编译器已经添加了默认构造函数：

```java
public Person() {
    super();
}
```

相反，**如果Person类中已经有一个构造函数，则编译器不会添加这个默认的无参数构造函数**。

现在，如果我们创建扩展Person的子类Employee，我们会在Employee类中得到一个错误：

```bash
Implicit super constructor Person() is undefined for default constructor. Must define an explicit constructor
```

由于编译器会插入对Employee构造函数的super调用，因此它不会在父类Person中找到无参数的构造函数。

## 4. 解决方案

要解决此错误，我们需要向编译器显式提供信息。

我们需要做的第一件事是从Employee构造函数中显式调用父类构造函数：

```java
public Employee(String name, Integer age, Double salary) {
    super(name, age);
    this.salary = salary;
}
```

现在，假设我们需要创建一个只有salary字段的Employee对象。让我们编写构造函数：

```java
public Employee(Double salary) {
    super();
    this.salary = salary;
}
```

**尽管向Employee构造函数添加了super调用，但我们仍然收到错误，因为Person类仍然缺少匹配的构造函数**。我们可以通过在Person类中显式创建一个无参数的构造函数来解决这个问题：

```java
public Person(String name, Integer age) {
    this.name = name;
    this.age = age;
}

public Person() {
}
```

最后，由于这些更改，我们不会收到编译错误。

## 5. 总结

我们已经解释了Java的“implicit super constructor is undefined”错误。然后，我们讨论了如何产生错误和异常的原因。最后，我们讨论了解决错误的解决方案。