## 1. 概述

Varargs是在Java 5中引入的，它为支持一种类型的任意数量参数的方法提供了简写形式。

在本文中，我们将了解如何使用这个核心Java特性。

## 2.在可变参数之前

在Java5 之前，每当我们想要传递任意数量的参数时，我们都必须将所有参数传递到一个数组中或实现 N 个方法(每个附加参数一个)：

```java
public String format() { ... }

public String format(String value) { ... }

public String format(String val1, String val2) { ... }
```

## 3.可变参数的使用

Varargs通过引入可以自动处理任意数量参数的新语法来帮助我们避免编写样板代码——在引擎盖下使用数组。

我们可以使用标准类型声明，后跟省略号来定义它们：

```java
public String formatWithVarArgs(String... values) {
    // ...
}
```

现在，我们可以使用任意数量的参数调用我们的方法，例如：

```java
formatWithVarArgs();

formatWithVarArgs("a", "b", "c", "d");
```

如前所述，可变参数是数组，因此我们需要像使用普通数组一样使用它们。

## 4.规则

Varargs使用起来很简单。但是我们必须牢记一些规则：

-   每个方法只能有一个可变参数
-   varargs参数必须是最后一个参数

## 5.堆污染

使用可变参数会导致所谓的[堆污染](https://en.wikipedia.org/wiki/Heap_pollution)。 为了更好地理解堆污染，请考虑以下可变参数方法：

```java
static String firstOfFirst(List<String>... strings) {
    List<Integer> ints = Collections.singletonList(42);
    Object[] objects = strings;
    objects[0] = ints; // Heap pollution

    return strings[0].get(0); // ClassCastException
}
```

如果我们在测试中调用这个奇怪的方法：

```java
String one = firstOfFirst(Arrays.asList("one", "two"), Collections.emptyList());

assertEquals("one", one);
```

即使我们在这里甚至没有使用任何显式类型转换，我们也会得到一个 ClassCastException ：

```bash
java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String
```

### 5.1. 安全使用

每次我们使用varargs时，Java 编译器都会创建一个数组来保存给定的参数。在这种情况下，编译器创建一个包含泛型类型组件的数组来保存参数。

当我们将可变参数与泛型一起使用时，由于存在致命运行时异常的潜在风险，Java 编译器会警告我们可能存在不安全的可变参数用法：

```plaintext
warning: [varargs] Possible heap pollution from parameterized vararg type T
```

当且仅当：

-   我们不在隐式创建的数组中存储任何内容。在此示例中，我们确实 在该数组中存储了一个List<Integer>
-   我们不会让对生成的数组的引用从方法中逃脱(稍后会详细介绍)

如果我们确定方法本身确实安全地使用可变参数，我们可以使用[@SafeVarargs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/SafeVarargs.html)来抑制警告。

简而言之，如果我们使用可变参数将可变数量的参数从调用者传输到方法，那么可变参数的使用是安全的，仅此而已！

### 5.2. 转义Varargs参考

让我们考虑另一种不安全的varargs用法：

```java
static <T> T[] toArray(T... arguments) {
    return arguments;
}
```

乍一看， toArray 方法似乎 完全无害。但是，因为它让可变参数数组转义给调用者，所以它违反了安全可变参数的第二条规则。

要了解此方法的危险性，让我们在另一种方法中使用它：

```java
static <T> T[] returnAsIs(T a, T b) {
    return toArray(a, b);
}
```

那么如果我们调用这个方法：

```java
String[] args = returnAsIs("One", "Two");
```

我们将再次获得 ClassCastException。 下面是我们调用 returnAsIs 方法时发生的情况：

-   要将 a 和 b 传递给 toArray 方法，Java 需要创建一个数组
-   由于Object[] 可以容纳任何类型的项目，编译器创建一个
-   toArray 方法将给定的 Object[] 返回 给 调用 者
-   由于调用站点需要一个 String[]， 编译器会尝试将 Object[] 转换为预期的 String[]，因此会抛出 ClassCastException

有关堆污染的更详细讨论，强烈建议阅读[Joshua Bloch 的 EffectiveJava的第](https://learning.oreilly.com/library/view/effective-java-3rd/9780134686097/)32 条。

## 六，总结

Varargs可以让Java中的许多样板文件消失。

而且，由于它们与数组的隐式自动装箱，它们在面向未来的代码中发挥了作用。