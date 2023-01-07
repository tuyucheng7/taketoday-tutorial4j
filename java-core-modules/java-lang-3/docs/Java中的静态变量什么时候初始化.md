## 1. 概述

在本教程中，我们将探索静态变量初始化过程。Java 虚拟机 (JVM) 在类加载期间遵循此过程。

## 2.初始化过程

在高层次上，JVM 执行以下步骤：

[![类初始化过程](https://www.baeldung.com/wp-content/uploads/2020/08/class-initialization-process-2.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/class-initialization-process-2.jpg)

首先，类被加载和链接。然后，这个过程的“初始化”阶段处理静态变量初始化。最后，调用与该类关联的主要 方法。

在下一节中，我们将研究类变量的初始化。

## 3.类变量

在Java中，静态变量也称为类变量。也就是说，它们属于一个类而不是一个特定的实例。结果，类初始化将初始化静态变量。

相反，类的实例将初始化实例变量(非静态变量)。一个类的所有实例共享该类的静态变量。

让我们以类StaticVariableDemo 为例：

```java
public class StaticVariableDemo {  
    public static int i;
    public static int j = 20;

    public StaticVariableDemo() {}
}
```

首先，JVM为类StaticVariableDemo创建一个类对象。接下来，静态字段初始值设定项为静态字段分配一个有意义的默认值。在我们上面的示例中，类变量i首先使用int默认值零进行初始化。

文本顺序适用于静态字段。首先，我将初始化，然后j将被初始化。之后，该类及其静态成员将对其他类可见。

## 4.静态块中的变量

再举一个例子：

```java
public class StaticVariableDemo {  
    public static int z;

    static {
        z = 30;
    }
    public StaticVariableDemo() {}
}
```

在这种情况下，变量初始化将按顺序进行。例如，JVM 最初将变量z分配给默认的int值 0。然后，在静态块中，它被更改为 30。

## 5.静态嵌套类中的变量

最后，让我们以外部 StaticVariableDemo类内部的嵌套类为例：

```java
public class StaticVariableDemo {  
    public StaticVariableDemo() {}
    
    static class Nested {
        public static String nestedClassStaticVariable = "test";
    }
}
```

在这种情况下，类StaticVariableDemo加载嵌套类。它将初始化静态变量nestedClassStaticVariable。

## 六，总结

在这篇简短的文章中，我们简要解释了静态变量初始化。有关更多详细信息，请查看[Java 语言规范](https://docs.oracle.com/javase/specs/jls/se14/html/index.html)。