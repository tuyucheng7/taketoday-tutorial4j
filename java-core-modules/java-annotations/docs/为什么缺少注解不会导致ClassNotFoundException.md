## 1. 概述

在本教程中，我们将熟悉Java编程语言中一个看似奇怪的特性：缺少注解不会在运行时导致任何异常。

然后，我们将更深入地挖掘，看看是什么原因和规则支配着这种行为，以及这些规则的例外情况是什么。

## 2. 快速复习

让我们从一个熟悉的Java示例开始。有类A，然后有类B，这取决于A：

```java
public class A {
}

public class B {
    public static void main(String[] args) {
        System.out.println(new A());
    }
}
```

现在，如果我们编译这些类并运行编译后的B，它会在控制台上为我们打印一条消息：

```bash
>> javac A.java
>> javac B.java
>> java B
A@d716361
```

但是，如果我们删除已编译的A.class文件并重新运行类B，我们将看到由[ClassNotFoundException](https://www.baeldung.com/java-classnotfoundexception-and-noclassdeffounderror)引起的NoClassDefFoundError：

```bash
>> rm A.class
>> java B
Exception in thread "main" java.lang.NoClassDefFoundError: A
        at B.main(B.java:3)
Caused by: java.lang.ClassNotFoundException: A
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:606)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:168)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
        ... 1 more
```

发生这种情况是因为类加载器在运行时找不到类文件，即使它在编译期间就存在。这是许多Java开发人员期望的正常行为。

## 3. 缺少注解

现在，让我们看看在相同情况下注解会发生什么。为此，我们将A类更改为注解：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface A {
}
```

如上所示，Java会在运行时保留注解信息。之后，是时候用A注解类B了：

```java
@A
public class B {
    public static void main(String[] args) {
        System.out.println("It worked!");
    }
}
```

接下来，让我们编译并运行这些类：

```bash
>> javac A.java
>> javac B.java
>> java B
It worked!
```

所以，我们看到B成功地在控制台上打印了它的消息，这是有道理的，因为一切都被编译并连接在一起，非常好。

现在，让我们删除A的类文件：

```bash
>> rm A.class
>> java B
It worked!
```

如上所示，即使注解类文件丢失，注解类运行也没有任何异常。

### 3.1 使用类标记的注解

为了让它更有趣，让我们介绍另一个具有Class<?>属性的注解：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface C {
    Class<?> value();
}
```

如上所示，此注解有一个名为value的属性，返回类型为Class<?>。作为该属性的参数，让我们添加另一个名为 D的空类：

```java
public class D {
}
```

现在，我们将使用这个新注解来注解B类：

```java
@A
@C(D.class)
public class B {
    public static void main(String[] args) {
        System.out.println("It worked!");
    }
}
```

当所有类文件都存在时，一切都应该可以正常工作。但是，如果我们只删除D类文件，其他的都不去碰，会发生什么情况呢？让我们找出来：

```bash
>> rm D.class
>> java B
It worked!
```

如上所示，尽管在运行时没有D，但一切仍然有效！因此，除了注解之外，属性中引用的类标记也不需要在运行时出现。

### 3.2 Java语言规范

所以，我们看到一些带有运行时保留的注解在运行时丢失了，但是被注解的类运行得很好。听起来可能出乎意料，但根据[Java语言规范9.6.4.2](https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.6.4.2)，这种行为实际上完全没问题：

>   注解可能只存在于源代码中，也可能以类或接口的二进制形式存在。以二进制形式存在的注解在运行时可能会或可能不会通过JavaSE平台的反射库提供。

此外，[JLS 13.5.7](https://docs.oracle.com/javase/specs/jls/se16/html/jls-13.html#jls-13.5.7)条目还指出：

>   添加或删除注解对Java编程语言中程序的二进制表示的正确链接没有影响。

最重要的是，运行时不会因缺少注解而抛出异常，因为JLS允许这样做。

### 3.3 访问缺失的注解

让我们以反射方式检索A信息的方式更改B类：

```java
@A
public class B {
    public static void main(String[] args) {
        System.out.println(A.class.getSimpleName());
    }
}
```

如果我们编译并运行它们，一切都会好起来的：

```bash
>> javac A.java
>> javac B.java
>> java B
A
```

现在，如果我们删除A类文件并运行B ，我们将看到由ClassNotFoundException引起的相同的NoClassDefFoundError：

```java
Exception in thread "main" java.lang.NoClassDefFoundError: A
        at B.main(B.java:5)
Caused by: java.lang.ClassNotFoundException: A
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:606)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:168)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
        ... 1 more
```

根据JLS，注解不必在运行时可用。然而，当一些其他代码读取该注解并对其执行某些操作时(就像我们所做的那样)，该注解必须在运行时存在。否则，我们会看到 ClassNotFoundException。

## 4. 总结

在本文中，我们看到了一些注解如何在运行时不存在，即使它们是类的二进制表示的一部分。