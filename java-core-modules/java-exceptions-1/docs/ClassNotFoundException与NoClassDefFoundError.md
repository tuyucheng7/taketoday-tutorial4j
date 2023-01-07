## 1. 概述

当 JVM 在类路径上找不到请求的类时，ClassNotFoundException和NoClassDefFoundError都会发生。尽管它们看起来很熟悉，但两者之间存在一些核心差异。

在本教程中，我们将讨论它们出现的一些原因及其解决方案。

## 2.类未发现异常

ClassNotFoundException是一个已检查的异常，当应用程序试图通过其完全限定名称加载类并且无法在类路径中找到其定义时发生。

这主要发生在尝试使用Class.forName()、ClassLoader.loadClass()或ClassLoader.findSystemClass()加载类时。因此，我们在使用反射时需要格外小心java.lang.ClassNotFoundException 。

例如，让我们尝试加载 JDBC 驱动程序类而不添加必要的依赖项，这将导致我们出现ClassNotFoundException：

```java
@Test(expected = ClassNotFoundException.class)
public void givenNoDrivers_whenLoadDriverClass_thenClassNotFoundException() 
  throws ClassNotFoundException {
      Class.forName("oracle.jdbc.driver.OracleDriver");
}
```

## 3.没有ClassDefFoundError

NoClassDefFoundError是一个致命错误。当 JVM 在尝试执行以下操作时找不到类的定义时会发生：

-   使用new关键字实例化一个类
-   使用方法调用加载类

当编译器可以成功编译类，但Java运行时找不到类文件时，就会发生错误。通常发生在执行静态块或初始化类的静态字段时出现异常，导致类初始化失败。

让我们考虑一个场景，这是重现问题的一种简单方法。ClassWithInitErrors初始化抛出异常。因此，当我们尝试创建 ClassWithInitErrors 的对象时，它会抛出ExceptionInInitializerError。

如果我们尝试再次加载同一个类，我们会得到 NoClassDefFoundError ：

```java
public class ClassWithInitErrors {
    static int data = 1 / 0;
}
public class NoClassDefFoundErrorExample {
    public ClassWithInitErrors getClassWithInitErrors() {
        ClassWithInitErrors test;
        try {
            test = new ClassWithInitErrors();
        } catch (Throwable t) {
            System.out.println(t);
        }
        test = new ClassWithInitErrors();
        return test;
    }
}
```

让我们为这个场景写一个测试用例：

```java
@Test(expected = NoClassDefFoundError.class)
public void givenInitErrorInClass_whenloadClass_thenNoClassDefFoundError() {
 
    NoClassDefFoundErrorExample sample
     = new NoClassDefFoundErrorExample();
    sample.getClassWithInitErrors();
}
```

## 4.分辨率

有时，诊断和修复这两个问题可能非常耗时。这两个问题的主要原因是类文件(在类路径中)在运行时不可用。

让我们看一下在处理其中任何一个时可以考虑的几种方法：

1.  我们需要确保包含该类的类或 jar 在类路径中是否可用。如果没有，我们需要添加它
2.  如果它在应用程序的类路径中可用，那么很可能类路径被覆盖了。要解决这个问题，我们需要找到我们的应用程序使用的确切类路径
3.  此外，如果应用程序使用多个类加载器，则一个类加载器加载的类可能无法被其他类加载器使用。[要很好地排除故障，了解类加载器在Java中的工作方式](https://en.wikipedia.org/wiki/Java_Classloader)至关重要

## 5.总结

虽然这两个异常都与类路径和Java运行时无法在运行时找到类有关，但请务必注意它们的区别。

Java 运行时在尝试仅在运行时加载类时抛出ClassNotFoundException并且名称是在运行时提供的。在 NoClassDefFoundError 的情况下，该类在编译时存在，但Java运行时无法在运行时的Java类路径中找到它。