## 1. 概述

在我们编写应用程序时做出的决定中，有很多是关于何时抛出[异常](https://www.baeldung.com/java-exceptions)以及抛出哪种类型的异常。

在本快速教程中，我们将解决当有人将null参数传递给我们的方法之一时抛出哪个异常的问题：IllegalArgumentException或[NullPointerException](https://www.baeldung.com/java-14-nullpointerexception)。

我们将通过检查双方的论点来探讨这个话题。

## 2.非法参数异常

首先，让我们看看抛出IllegalArgumentException的参数。

让我们创建一个简单的方法， 在传递null时抛出IllegalArgumentException：

```java
public void processSomethingNotNull(Object myParameter) {
    if (myParameter == null) {
        throw new IllegalArgumentException("Parameter 'myParameter' cannot be null");
    }
}
```

现在，让我们继续讨论支持IllegalArgumentException的论据。

### 2.1. Javadoc 是这样说的

当我们阅读[IllegalArgumentException](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/IllegalArgumentException.html)[的 Javadoc](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/IllegalArgumentException.html)时，它说它是在将非法或不适当的值传递给方法时使用的。如果我们的方法不需要它，我们可以认为空对象是非法的或不合适的，这将是我们抛出的适当异常。

### 2.2. 它符合开发人员的期望

接下来，让我们考虑一下，当我们在应用程序中看到堆栈跟踪时，作为开发人员，我们是如何思考的。我们收到NullPointerException的一个非常常见的场景是当我们不小心尝试访问空对象时。在这种情况下，我们将尽可能深入堆栈，看看我们引用的是什么null。

当我们得到一个IllegalArgumentException时，我们可能会假设我们将错误的东西传递给了一个方法。在这种情况下，我们将在堆栈中查找我们正在调用的最底部的方法，并从那里开始调试。如果我们考虑这种思维方式，IllegalArgumentException将使我们更接近发生错误的堆栈。

###  2.3. 其他论点

在我们继续讨论NullPointerException的参数之前，让我们先看几个支持IllegalArgumentException的小点。一些开发人员认为只有 JDK 应该抛出NullPointerException。正如我们将在下一节中看到的，Javadoc 不支持这个理论。另一个论点是使用IllegalArgumentException更一致，因为这是我们用于其他非法参数值的方法。

## 3.空指针异常

接下来，让我们考虑NullPointerException的参数。

让我们创建一个抛出NullPointerException的示例：

```java
public void processSomethingElseNotNull(Object myParameter) {
    if (myParameter == null) {
        throw new NullPointerException("Parameter 'myParameter' cannot be null");
    }
}
```

### 3.1. Javadoc 是这样说的

根据[NullPointerException](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/NullPointerException.html)[的 Javadoc](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/NullPointerException.html)，NullPointerException用于尝试在需要对象的地方使用null。如果我们的方法参数不打算为null，那么我们可以合理地将其视为需要的对象并抛出NullPointerException。

### 3.2. 与 JDK APIs 一致

让我们花点时间考虑一下我们在开发过程中调用的许多常见 JDK 方法。如果我们提供null ，他们中的许多人都会抛出NullPointerException。此外，如果我们传入null， Objects.requireNonNull()会抛出 NullPointerException 。根据[Objects](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Objects.html#requireNonNull(T))[文档，](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Objects.html#requireNonNull(T))它主要用于验证参数。

除了抛出NullPointerException的 JDK 方法之外，我们还可以找到从 Collections API 中的方法抛出特定异常类型的其他示例。如果索引超出列表大小，ArrayList.addAll(index, Collection)抛出IndexOutOfBoundsException ，如果集合为null则抛出NullPointerException。这是两个非常具体的异常类型，而不是更通用的IllegalArgumentException。

我们可以将IllegalArgumentException视为适用于我们没有可用的更具体的异常类型的情况。

## 4。总结

正如我们在本教程中看到的，这是一个没有明确答案的问题。这两个例外的文档似乎重叠，因为当单独使用时，它们听起来都很合适。基于开发人员进行调试的方式以及在 JDK 方法本身中看到的模式，双方还有其他令人信服的论据。

无论我们选择哪种异常，我们都应该在整个应用程序中保持一致。此外，我们可以通过向异常构造函数提供有意义的信息来使我们的异常更有用。例如，如果我们在异常消息中提供参数名称，我们的应用程序将更容易调试。