## 1. 概述

在本快速教程中，我们将讨论针对[单例](https://www.baeldung.com/java-singleton)设计模式编程与在Java中使用静态类之间的一些显著差异。我们将回顾这两种编码方法，并在编程的不同方面对它们进行比较。

到本文结束时，我们将能够在两个选项之间进行选择时做出正确的决定。

## 2. 基础知识

让我们归零。单例是一种[设计模式](https://www.baeldung.com/design-patterns-series)，可确保在应用程序的生命周期内使用类的单个实例。
它还提供了对该实例的全局访问点。

[static](https://www.baeldung.com/java-static)(保留关键字)是一个修饰符，它使实例变量成为类变量。因此，这些变量与类(与任何对象)相关联。当与方法一起使用时，它使它们可以通过类名访问。最后，我们还可以创建静态[嵌套内部类](https://www.baeldung.com/java-nested-classes)。

**在这种情况下，静态类包含静态方法和静态变量**。

## 3. 单例与静态实用类

现在，让我们深入了解一下这两个概念之间的一些显著差异。我们从一些面向对象的概念开始我们的探索。

### 3.1 运行时多态性

Java中的静态方法在编译时解析，不能在运行时覆盖。因此，静态类不能真正受益于运行时多态性：

```java
public class SuperUtility {

    public static String echoIt(String data) {
        return "SUPER";
    }
}

public class SubUtility extends SuperUtility {

    public static String echoIt(String data) {
        return data;
    }
}

@Test
public void whenStaticUtilClassInheritance_thenOverridingFails() {
    SuperUtility superUtility = new SubUtility();
    Assert.assertNotEquals("ECHO", superUtility.echoIt("ECHO"));
    Assert.assertEquals("SUPER", superUtility.echoIt("ECHO"));
}
```

相比之下，**单例可以像任何其他类一样通过从基类派生来利用运行时多态性**：

```java
public class MyLock {

    protected String takeLock(int locks) {
        return "Taken Specific Lock";
    }
}

public class SingletonLock extends MyLock {

    // private constructor and getInstance method 

    @Override
    public String takeLock(int locks) {
        return "Taken Singleton Lock";
    }
}

@Test
public void whenSingletonDerivesBaseClass_thenRuntimePolymorphism() {
    MyLock myLock = new MyLock();
    Assert.assertEquals("Taken Specific Lock", myLock.takeLock(10));
    myLock = SingletonLock.getInstance();
    Assert.assertEquals("Taken Singleton Lock", myLock.takeLock(10));
}
```

此外，**单例还可以实现接口**，使它们比静态类更具优势：

```java
public class FileSystemSingleton implements SingletonInterface {

    // private constructor and getInstance method

    @Override
    public String describeMe() {
        return "File System Responsibilities";
    }
}

public class CachingSingleton implements SingletonInterface {

    // private constructor and getInstance method

    @Override
    public String describeMe() {
        return "Caching Responsibilities";
    }
}

@Test
public void whenSingletonImplementsInterface_thenRuntimePolymorphism() {
    SingletonInterface singleton = FileSystemSingleton.getInstance();
    Assert.assertEquals("File System Responsibilities", singleton.describeMe());
    singleton = CachingSingleton.getInstance();
    Assert.assertEquals("Caching Responsibilities", singleton.describeMe());
}
```

实现接口的[单例作用域Spring Beans](https://www.baeldung.com/spring-bean-scopes)是这种范例的完美示例。

### 3.2 方法参数

由于它本质上是一个对象，**我们可以轻松地将单例作为参数传递给其他方法**：

```java
@Test
public void whenSingleton_thenPassAsArguments() {
    SingletonInterface singleton = FileSystemSingleton.getInstance();
    Assert.assertEquals("Taken Singleton Lock", singleton.passOnLocks(SingletonLock.getInstance()));
}
```

然而，创建一个静态实用程序类对象并在方法中传递它是毫无价值的，也是一个坏主意。

### 3.3 对象状态、序列化和可克隆性

单例可以有实例变量，就像任何其他对象一样，它可以维护这些变量的状态：

```java
@Test
public void whenSingleton_thenAllowState() {
    SingletonInterface singleton = FileSystemSingleton.getInstance();
    IntStream.range(0, 5)
        .forEach(i -> singleton.increment());
    Assert.assertEquals(5, ((FileSystemSingleton) singleton).getFilesWritten());
}
```

此外，**可以[序列化](https://www.baeldung.com/java-serialization)单例以保留其状态或通过介质(例如网络)传输**：

```java
new ObjectOutputStream(baos).writeObject(singleton);
SerializableSingleton singletonNew = (SerializableSingleton) new ObjectInputStream
    (new ByteArrayInputStream(baos.toByteArray())).readObject();
```

最后，实例的存在也设置了使用Object的克隆方法[克隆](https://www.baeldung.com/java-deep-copy)它的可能性：

```java
@Test
public void whenSingleton_thenAllowCloneable() {
    Assert.assertEquals(2, ((SerializableCloneableSingleton) singleton.cloneObject()).getState());
}
```

**相反，静态类只有类变量和静态方法，因此它们没有对象特定的状态。由于静态成员属于类，因此我们无法序列化它们。此外，由于缺少要克隆的对象，克隆对于静态类毫无意义**。 

### 3.4 加载机制和内存分配

与类的任何其他实例一样，单例存在于堆中。其优势在于，只要应用程序需要，就可以延迟加载巨大的单例对象。

另一方面，静态类在编译时包含静态方法和静态绑定变量，并在堆栈上分配。

因此，静态类在JVM中[加载](https://www.baeldung.com/java-classloaders)类的时候总是被急切加载的。

### 3.5 效率和性能

如前所述，静态类不需要对象初始化。这消除了创建对象所需的时间开销。

此外，通过在编译时进行静态绑定，它们比单例更有效并且往往更快。

我们必须仅出于设计原因选择单例，而不是将其作为效率或性能增益的单实例解决方案。

### 3.6 其他细微差别

针对单例而不是静态类进行编程也可以减少所需的重构量。

毫无疑问，单例是类的对象。因此，我们可以很容易地从它转移到一个类的多实例世界。

由于静态方法是在没有对象但使用类名的情况下调用的，因此迁移到多实例环境可能是一个相对较大的重构。

其次，在静态方法中，由于逻辑耦合到类定义而不是对象，因此来自被单元测试的对象的静态方法调用变得更难被mock或什至被dummy或stub实现覆盖。

## 4. 做出正确的选择

如果我们：

-   需要为应用程序提供完整的面向对象解决方案
-   在所有给定时间只需要一个类的实例并保持一种状态
-   想要一个类的延迟加载解决方案，以便仅在需要时加载它

在以下情况下使用静态类：

-   只需要存储许多只对输入参数进行操作并且不修改任何内部状态的静态实用方法
-   不需要运行时多态性或面向对象的解决方案

## 5. 总结

在本文中，我们回顾了静态类和Java中的单例模式之间的一些本质区别。我们还推断了在开发软件时何时使用这两种方法中的任何一种。