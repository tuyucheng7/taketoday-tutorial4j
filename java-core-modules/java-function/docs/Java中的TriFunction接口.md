## 1. 概述

在本文中，我们将定义一个TriFunction [FunctionalInterface](https://www.baeldung.com/java-8-functional-interfaces)，它表示接受三个参数并计算结果的函数。[稍后，我们还将看到一个使用Vavr](https://www.baeldung.com/vavr)库的内置Function3 的示例。

## 2. 创建我们自己的TriFunction接口

从版本 8 开始，Java 定义了[BiFunction](https://www.baeldung.com/java-bifunction-interface) FunctionalInterface。它表示接受两个参数并计算其结果的函数。为了允许函数组合，它还提供了一个andThen()方法，该方法将另一个Function应用于BiFunction的结果。

同样，我们将定义我们的TriFunction接口并为其提供andThen()方法：

```java
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);

    default <K> TriFunction<T, U, V, K> andThen(Function<? super R, ? extends K> after) {
        Objects.requireNonNull(after);
        return (T t, U u, V v) -> after.apply(apply(t, u, v));
    }
}
```

让我们看看如何使用这个接口。我们将定义一个接受三个Integers 的函数，将两个第一个操作数相乘，然后添加最后一个操作数：

```java
static TriFunction<Integer, Integer, Integer, Integer> multiplyThenAdd = (x, y, z) -> x  y + z;
```

请注意，只有当两个第一个操作数的乘积低于[Integer maximum value](https://www.baeldung.com/cs/max-int-java-c-python)时，此方法的结果才是准确的。

例如，我们可以使用andThen()方法来定义一个TriFunction：

-   首先，将multiplyThenAdd()应用于参数
-   然后，应用一个函数，该函数计算整数的欧几里德除法除以10 到上一步的结果

```java
static TriFunction<Integer, Integer, Integer, Integer> multiplyThenAddThenDivideByTen = multiplyThenAdd.andThen(x -> x / 10);
```

我们现在可以编写一些快速测试来检查我们的TriFunction是否按预期运行：

```java
@Test
void whenMultiplyThenAdd_ThenReturnsCorrectResult() {
    assertEquals(25, multiplyThenAdd.apply(2, 10, 5));
}

@Test
void whenMultiplyThenAddThenDivideByTen_ThenReturnsCorrectResult() {
    assertEquals(2, multiplyThenAddThenDivideByTen.apply(2, 10, 5));
}
```

最后一点，TriFunction的操作数可以有多种类型。例如，我们可以定义一个TriFunction，它根据布尔条件将Integer转换为String或返回另一个给定的String：

```java
static TriFunction<Integer, String, Boolean, String> convertIntegerOrReturnStringDependingOnCondition = (myInt, myStr, myBool) -> {
    if (Boolean.TRUE.equals(myBool)) {
        return myInt != null ? myInt.toString() : "";
    } else {
        return myStr;
    }
};
```

## 3.使用Vavr的功能3

Vavr 库已经定义了一个具有我们想要的行为的Function3接口。首先，让我们将 Vavr[依赖项](https://search.maven.org/search?q=io.vavr)添加到我们的项目中：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.10.4</version>
</dependency>
```

我们现在可以用它重新定义multiplyThenAdd()和multiplyThenAddThenDivideByTen()方法：

```java
static Function3<Integer, Integer, Integer, Integer> multiplyThenAdd = (x, y, z) -> x  y + z;

static Function3<Integer, Integer, Integer, Integer> multiplyThenAddThenDivideByTen = multiplyThenAdd.andThen(x -> x / 10);
```

如果我们需要定义最多有 8 个参数的函数，使用 Vavr 是一个不错的选择。Function4 , Function5, ... Function8确实已经在库中定义了。

## 4。总结

在本教程中，我们为接受 3 个参数的函数实现了自己的FunctionalInterface 。我们还强调了 Vavr 库包含此类函数的实现。