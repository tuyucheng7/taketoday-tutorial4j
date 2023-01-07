## 1. 概述

在本快速教程中，我们将了解导致Java抛出 [ExceptionInInitializerError](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ExceptionInInitializerError.html) 异常实例的原因。

我们将从一些理论开始。然后我们将在实践中看到这个异常的几个例子。

## 2.ExceptionInInitializerError _

ExceptionInInitializerError 表示[静态初始化程序中发生](https://www.baeldung.com/java-static)了 意外异常。 基本上，当我们看到这个异常时，我们应该知道Java未能评估静态初始化块或实例化静态变量。

事实上，每次在静态初始化程序中发生任何异常时，Java 都会自动将该异常包装在ExceptionInInitializerError 类的实例中。这样，它还维护对实际异常的引用作为根本原因。

现在我们知道了这个例外背后的基本原理，让我们在实践中看看它。

## 3.静态初始化块

要有一个失败的静态块初始化器，我们要有意地将一个整数除以零：

```java
public class StaticBlock {

    private static int state;

    static {
        state = 42 / 0;
    }
}
```

现在，如果我们用类似的东西触发类初始化：

```java
new StaticBlock();
```

然后，我们会看到以下异常：

```plaintext
java.lang.ExceptionInInitializerError
    at com.baeldung...(ExceptionInInitializerErrorUnitTest.java:18)
Caused by: java.lang.ArithmeticException: / by zero
    at com.baeldung.StaticBlock.<clinit>(ExceptionInInitializerErrorUnitTest.java:35)
    ... 23 more
```

如前所述，Java 抛出 ExceptionInInitializerError 异常的同时维护对根本原因的引用：

```java
assertThatThrownBy(StaticBlock::new)
  .isInstanceOf(ExceptionInInitializerError.class)
  .hasCauseInstanceOf(ArithmeticException.class);
```

另外值得一提的是，<clinit> 方法是JVM中的[类初始化方法。](https://www.baeldung.com/jvm-init-clinit-methods#clinit)

## 4.静态变量初始化

如果Java未能初始化静态变量，也会发生同样的事情：

```java
public class StaticVar {

    private static int state = initializeState();

    private static int initializeState() {
        throw new RuntimeException();
    }
}
```

同样，如果我们触发类初始化过程：

```java
new StaticVar();
```

然后出现同样的异常：

```plaintext
java.lang.ExceptionInInitializerError
    at com.baeldung...(ExceptionInInitializerErrorUnitTest.java:11)
Caused by: java.lang.RuntimeException
    at com.baeldung.StaticVar.initializeState(ExceptionInInitializerErrorUnitTest.java:26)
    at com.baeldung.StaticVar.<clinit>(ExceptionInInitializerErrorUnitTest.java:23)
    ... 23 more
```

与静态初始化块类似，也保留了异常的根本原因：

```java
assertThatThrownBy(StaticVar::new)
  .isInstanceOf(ExceptionInInitializerError.class)
  .hasCauseInstanceOf(RuntimeException.class);
```

## 5.检查异常

作为[Java 语言规范 (JLS-11.2.3)](https://docs.oracle.com/javase/specs/jls/se14/html/jls-11.html#jls-11.2.3)的一部分，我们不能在静态初始化程序块或静态变量初始化程序中抛出[检查异常。](https://www.baeldung.com/java-exceptions#1checked-exceptions)例如，如果我们尝试这样做：

```java
public class NoChecked {
    static {
        throw new Exception();
    }
}
```

编译器将因以下编译错误而失败：

```plaintext
java: initializer must be able to complete normally
```

作为约定，当我们的静态初始化逻辑抛出检查异常时，我们应该将可能的检查异常包装在 ExceptionInInitializerError 实例中：

```java
public class CheckedConvention {

    private static Constructor<?> constructor;

    static {
        try {
            constructor = CheckedConvention.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
```

如上所示， getDeclaredConstructor() 方法抛出一个检查异常。因此，我们捕获了已检查的异常并按照惯例将其包装起来。

因为我们已经明确地返回了一个ExceptionInInitializerError异常的实例，所以Java不会将这个异常包装在另一个 ExceptionInInitializerError 实例中。

但是，如果我们抛出任何其他未经检查的异常，Java 将抛出另一个ExceptionInInitializerError：

```java
static {
    try {
        constructor = CheckedConvention.class.getConstructor();
    } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
    }
}
```

在这里，我们将已检查的异常包装在未检查的异常中。因为这个未经检查的异常不是ExceptionInInitializerError 的实例，Java将再次包装它，导致这个意外的堆栈跟踪：

```plaintext
java.lang.ExceptionInInitializerError
	at com.baeldung.exceptionininitializererror...
Caused by: java.lang.RuntimeException: java.lang.NoSuchMethodException: ...
Caused by: java.lang.NoSuchMethodException: com.baeldung.CheckedConvention.<init>()
	at java.base/java.lang.Class.getConstructor0(Class.java:3427)
	at java.base/java.lang.Class.getConstructor(Class.java:2165)
```

如上所示，如果我们遵循约定，那么堆栈跟踪将比这更清晰。

### 5.1. OpenJDK

最近，这个约定甚至用在 OpenJDK 源代码本身中。例如，下面是 [AtomicReference](https://github.com/openjdk/jdk/blob/b87302ca99ff30a03e311ab1c0f524684ed37596/src/java.base/share/classes/java/util/concurrent/atomic/AtomicReference.java#L51) 如何使用这种方法：

```java
public class AtomicReference<V> implements java.io.Serializable {
    private static final VarHandle VALUE;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            VALUE = l.findVarHandle(AtomicReference.class, "value", Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private volatile V value;

   // omitted
}
```

## 六，总结

在本教程中，我们了解了导致Java抛出 ExceptionInInitializerError 异常实例的原因。