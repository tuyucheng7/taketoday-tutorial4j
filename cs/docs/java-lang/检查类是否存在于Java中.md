## 1. 概述

在确定要使用接口的哪个实现时，检查类是否存在可能很有用。在较早的 JDBC 设置期间通常使用此技术。

在本教程中，我们将探讨使用Class.forName()检查Java类路径中是否存在类的细微差别。

## 2. 使用Class.forName()

[我们可以使用Java Reflection](https://www.baeldung.com/java-reflection)检查类是否存在，特别是Class.forName()。文档显示如果找不到该类，将抛出ClassNotFoundException 。

### 2.1. 何时期待ClassNotFoundException

首先，让我们编写一个肯定会抛出[ClassNotFoundException](https://www.baeldung.com/java-classnotfoundexception-and-noclassdeffounderror)的测试，这样我们就可以知道我们的正面测试是安全的：

```java
@Test(expected = ClassNotFoundException.class)
public void givenNonExistingClass_whenUsingForName_thenClassNotFound() throws ClassNotFoundException {
    Class.forName("class.that.does.not.exist");
}
```

因此，我们已经证明不存在的类将抛出ClassNotFoundException。让我们为一个确实存在的类编写一个测试：

```java
@Test
public void givenExistingClass_whenUsingForName_thenNoException() throws ClassNotFoundException {
    Class.forName("java.lang.String");
}
```

这些测试证明运行Class.forName()并且不捕获ClassNotFoundException等同于 classpath 中存在的指定类。[但是，由于副作用](https://medium.com/@bishonbopanna/java-side-effect-methods-good-bad-and-ugly-8ffa697323ec)，这并不是一个完美的解决方案。

### 2.2. 副作用：类初始化

必须指出的是，在不指定类加载器的情况下，Class.forName()必须在请求的类上运行静态初始化程序。这可能会导致意外行为。

为了举例说明这种行为，让我们创建一个在执行其静态初始化程序块时抛出RuntimeException的类，以便我们可以立即知道它何时执行：

```java
public static class InitializingClass {
    static {
        if (true) { //enable throwing of an exception in a static initialization block
            throw new RuntimeException();
        }
    }
}
```

我们可以从forName()文档中看到，如果此方法引发的初始化失败，它会抛出ExceptionInInitializerError 。

让我们编写一个测试，在不指定类加载器的情况下尝试查找我们的InitializingClass时期望出现ExceptionInInitializerError ：

```java
@Test(expected = ExceptionInInitializerError.class)
public void givenInitializingClass_whenUsingForName_thenInitializationError() throws ClassNotFoundException {
    Class.forName("path.to.InitializingClass");
}
```

由于类的静态初始化块的执行是一种不可见的副作用，我们现在可以看到它是如何导致性能问题甚至错误的。让我们看看如何跳过类初始化。

## 3. 告诉Class.forName()跳过初始化

对我们来说幸运的是，有一个forName() 的重载方法，它接受一个类加载器以及是否应该执行类初始化。

根据文档，以下调用是等效的：

```java
Class.forName("Foo")
Class.forName("Foo", true, this.getClass().getClassLoader())
```

通过将true更改为false，我们现在可以编写一个测试来检查我们的InitializingClass是否存在 而不触发其静态初始化块：

```java
@Test
public void givenInitializingClass_whenUsingForNameWithoutInitialization_thenNoException() throws ClassNotFoundException {
    Class.forName("path.to.InitializingClass", false, getClass().getClassLoader());
}
```

## 4.Java9 模块

对于Java9+ 项目，有Class.forName()的第三个重载，它接受一个Module和一个String类名。默认情况下，此重载不运行类初始值设定项。另外，值得注意的是，当请求的类不存在时它返回null而不是抛出ClassNotFoundException。

## 5.总结

在这个简短的教程中，我们揭示了使用Class.forName()时类初始化的副作用，并且发现你可以使用forName()重载来防止这种情况发生。