## 1. 概述

在本教程中，我们将了解是什么使对象不可变，如何在Java中实现不可变性，以及这样做的好处。

## 2. 什么是不可变对象？

**不可变对象是在完全创建后内部状态保持不变的对象**。

这意味着不可变对象的公共API向我们保证，它在整个生命周期内都将以相同的方式运行。

如果我们看一下String类，我们可以看到，即使它的API似乎通过其replace方法为我们提供了可变行为，但原始String并没有改变：

```java
String name = "tuyucheng";
String newName = name.replace("cheng", "-----");

assertEquals("tuyucheng", name);
assertEquals("tuyu-----", newName);
```

API为我们提供了只读方法，它永远不应该包含改变对象内部状态的方法。

## 3. Java中的final关键字

在尝试在Java中实现不变性之前，我们应该先谈谈final关键字。

在Java中，**变量默认是可变的，这意味着我们可以改变它们持有的值**。

通过在声明变量时使用final关键字，Java编译器不会让我们更改该变量的值。相反，它会报告编译时错误：

```java
final String name = "tuyucheng";
name = "tuyu...";
```

请注意，final仅禁止我们更改变量持有的引用，它不会保护我们通过使用其公共API更改它所引用的对象的内部状态：

```java
final List<String> strings = new ArrayList<>();
assertEquals(0, strings.size());
strings.add("tuyucheng");
assertEquals(0, strings.size());
```

第二个assertEquals将失败，因为将元素添加到列表会改变其大小，因此，它不是不可变对象。

## 4. Java中的不变性

现在我们知道如何避免更改变量的内容，我们可以使用它来构建不可变对象的API。

构建不可变对象的API需要我们保证无论我们如何使用它的API，它的内部状态都不会改变。

朝正确方向迈出的一步是在声明其属性时使用final：

```java
class Money {
    private final double amount;
    private final Currency currency;

    // ...
}
```

请注意，Java向我们保证amount的值不会改变，所有原始类型变量都是如此。

但是，在我们的示例中，我们只能保证currency不会发生变化，因此**我们必须依赖Currency API来保护自己免受变化**。

大多数时候，我们需要对象的属性来保存自定义值，而初始化不可变对象内部状态的地方是它的构造函数：

```java
class Money {
    // ...
    public Money(double amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }
}
```

正如我们之前所说，为了满足不可变API的要求，我们的Money类只有只读方法。

使用反射API，我们可以打破不可变性并[改变不可变对象](https://stackoverflow.com/questions/20945049/is-a-java-string-really-immutable)。但是，反射违反了不可变对象的公共API，通常我们应该避免这样做。

## 5. 好处

由于不可变对象的内部状态在时间上保持不变，**我们可以在多个线程之间安全地共享它**。

我们也可以自由使用它，引用它的对象都不会注意到任何差异，我们可以说**不可变对象是无副作用的**。

## 6. 总结

不可变对象不会及时改变它们的内部状态，它们是线程安全的并且没有副作用。由于这些属性，不可变对象在处理多线程环境时也特别有用。