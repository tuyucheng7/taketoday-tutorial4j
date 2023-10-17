## 1. 概述

在本教程中，**我们将了解Java this关键字**。

在Java中，**this关键字是对正在调用其方法的当前对象的引用**。

让我们探讨如何以及何时可以使用关键字。

## 2. 消除字段阴影的歧义

**该关键字对于消除实例变量与局部参数的歧义很有用**。最常见的原因是当我们有与实例字段同名的构造函数参数时：

```java
public class KeywordTest {

    private String name;
    private int age;

    public KeywordTest(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

正如我们在这里看到的，我们将其与名称和年龄实例字段一起使用-以将它们与参数区分开来。

另一种用法是将其与局部范围内的参数隐藏或阴影一起使用。可以在[变量和方法隐藏](https://www.baeldung.com/java-variable-method-hiding)-文中找到使用示例。

## 3. 引用同一个类的构造函数

**从构造函数中，我们可以使用this()来调用同一类的不同构造函数**。在这里，我们将this()用于构造函数链接以减少代码使用。

最常见的用例是从参数化构造函数调用默认构造函数：

```java
public KeywordTest(String name, int age) {
    this();
    
    // the rest of the code
}
```

或者，我们可以从无参数构造函数调用参数化构造函数并传递一些参数：

```java
public KeywordTest() {
    this("John", 27);
}
```

注意，this()应该是构造函数中的第一条语句，否则会出现编译错误。

## 4. 将其作为参数传递

这里我们有printInstance()方法，其中定义了this关键字参数：

```java
public KeywordTest() {
    printInstance(this);
}

public void printInstance(KeywordTest thisKeyword) {
    System.out.println(thisKeyword);
}
```

在构造函数中，我们调用printInstance()方法。有了这个，我们传递了对当前实例的引用。

## 5. 返回this

**我们还可以使用this关键字从方法中返回当前类实例**。

为了不重复代码，这里有一个完整的实际示例，说明它是如何在[构建器设计模式](https://www.baeldung.com/creational-design-patterns)中实现的。

## 6. 内部类中的this关键字

我们还使用它从内部类中访问外部类实例：

```java
public class KeywordTest {

    private String name;

    class ThisInnerClass {

        boolean isInnerClass = true;

        public ThisInnerClass() {
            KeywordTest thisKeyword = KeywordTest.this;
            String outerString = KeywordTest.this.name;
        }
    }
}
```

在这里，在构造函数内部，我们可以使用KeywordTest.this调用获取对KeywordTest实例的引用。我们可以更深入地访问实例变量，例如KeywordTest.this.name字段。

## 7. 总结

在本文中，我们探讨了Java中的this关键字。