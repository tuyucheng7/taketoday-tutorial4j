## **一、概述**

在本快速教程中，我们将讨论 Java 编译器错误*“应为类、接口或枚举”。*这个错误主要是刚接触 java 世界的开发人员面临的。

让我们看一下此错误的几个示例并讨论如何修复它们。

## **2.错位的大括号**

*“预期的类、接口或枚举”*错误的根本原因 通常是大括号 *“}”*放错了位置。这可以是课后额外的花括号。也可能是不小心写在类外的方法。

让我们看一个例子：

```java
public class MyClass {
    public static void main(String args[]) {
      System.out.println("Baeldung");
    }
}
}复制
/MyClass.java:6: error: class, interface, or enum expected
}
^
1 error复制
```

在上面的代码示例中，最后一行有一个额外的*“}”*大括号导致编译错误。如果我们删除它，那么代码将编译。

我们来看另一个出现这个错误的场景：

```java
public class MyClass {
    public static void main(String args[]) {
        //Implementation
    }
}
public static void printHello() {
    System.out.println("Hello");
}复制
/MyClass.java:6: error: class, interface, or enum expected
public static void printHello()
^
/MyClass.java:8: error: class, interface, or enum expected
}
^
2 errors复制
```

在上面的示例中，我们会得到错误，因为方法*printHello()* 在类*MyClass*之外。我们可以通过将右花括号*“}”*移动  到文件末尾来解决这个问题。换句话说，将*printHello()方法移到**MyClass*中 。

## **3.结论**

在这个简短的教程中，我们讨论了“预期的类、接口或枚举”Java 编译器错误，并演示了两个可能的根本原因。