## 一、概述

在本教程中，我们将了解 Java 中记录类和最终类之间的区别。

## 2. 什么是记录？

**[记录](https://www.baeldung.com/java-record-keyword)类是充当不可变数据透明载体的特殊类**。它们是不可变的类（所有字段都是*final*）并且是隐式的*final*类，这意味着它们不能被扩展。

在编写记录类时，我们需要牢记一些限制：

-   我们不能将*extends*子句添加到声明中，因为每个记录类都隐式扩展了抽象类[Record](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Record.html)，并且 Java 不允许多重继承
-   我们不能在记录类中声明实例变量或实例初始化器
-   我们不能在记录类中声明[*本地方法*](https://www.baeldung.com/java-native)

记录类声明包括名称、类型参数（如果记录是通用的）、包含记录组件的标头和主体：

```java
public record Citizen (String name, String address) {}复制
```

然后，Java 编译器会自动生成*private*、*final*字段、getter、公共构造函数以及*equals*、*hashCode*和*toString*方法。

此外，我们可以将实例方法以及静态字段和方法添加到记录体中：

```java
public record USCitizen(String firstName, String lastName, String address) {
    static int countryCode;

    // static initializer
    static {
        countryCode = 1;
    }

    public static int getCountryCode() {
        return countryCode;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}复制
```

上面的记录包含一个静态字段、一个静态方法和一个实例方法。

## 3. 什么是期末班？

**[期末](https://www.baeldung.com/java-final)班不能延期**。*它们使用final*关键字声明：

```java
final class Rectangle {
    private double length;
    private double width;

    // Rest of the body
}复制
```

在这里，我们不能创建另一个扩展*Rectangle*类的类，因为它是最终类。

## 4.有什么区别？

记录本身就是最终课程。**但是，记录类与常规最终类相比有更多限制**。另一方面，记录在某些情况下使用起来更方便，因为它们可以在一行中声明，编译器会自动生成其他所有内容。如果我们需要一个不可变且不会从其他类继承的简单数据载体，我们可以使用记录。

## 5.总结

在这篇简短的文章中，我们了解了 Java 中记录类和最终类之间的区别。