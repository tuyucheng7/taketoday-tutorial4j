## 1. 概述

从Java 9开始，可以将[私有方法](https://openjdk.java.net/jeps/213)添加到Java的接口中。在这个简短的教程中，我们介绍如何在接口中定义私有方法，以及它们的好处。

## 2. 在接口中定义私有方法

私有方法可以是静态的或非静态的，这意味着在接口中，我们能够创建私有方法来封装来自默认和静态公共方法签名的代码。

首先，让我们看看如何从默认接口方法中使用私有方法：

```java
public interface Foo {

    default void bar() {
        System.out.print("Hello");
        baz();
    }

    private void baz() {
        System.out.println(" world!");
    }
}
```

bar()能够通过从其默认方法调用私有方法baz()来使用它。

接下来，我们在Foo接口中添加一个静态定义的私有方法：

```java
public interface Foo {

    static void buzz() {
        System.out.print("Hello");
        staticBaz();
    }

    private static void staticBaz() {
        System.out.println(" static world!");
    }
}
```

在接口内，其他静态定义的方法可以使用这些私有静态方法。

最后，让我们从一个具体的类中调用定义的默认和静态方法：

```java
public class CustomFoo implements Foo {

    public static void main(String... args) {
        Foo customFoo = new CustomFoo();
        customFoo.bar();
        Foo.buzz();
    }
}
```

对bar()方法的调用输出“Hello world！”，对buzz()方法的调用输出“Hello static world！”。

## 3. 接口中私有方法的好处

在上一节中提到，接口能够使用私有方法来隐藏实现接口的类的实现细节。因此，在接口中包含这些私有方法的主要好处之一是封装。

另一个好处是(与一般的私有方法一样)为具有类似功能的方法的接口添加了更少的重复和更多可重用的代码。

## 4. 总结

在本教程中，我们介绍了如何在接口中定义私有方法，以及如何在静态和非静态上下文中使用它们。