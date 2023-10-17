## 1. 概述

在这个简短的教程中，我们将演示如何使用方法重载来模拟Java中的默认参数。

在这里，我们说模拟是因为与某些其他 OOP 语言(如 C++ 和 Scala)不同，Java 规范不支持为方法参数分配默认值。

## 2.例子

举个例子，让我们泡杯茶吧！首先，我们需要一个Tea POJO：

```java
public class Tea {

    static final int DEFAULT_TEA_POWDER = 1;

    private String name; 
    private int milk;
    private boolean herbs;
    private int sugar;
    private int teaPowder;

    // standard getters 
}

```

在这里， 名称是必填字段，因为我们的茶至少必须有一个名称。

那么，没有茶粉就没有茶。因此，我们假设用户想要在他们的茶中加入标准的 1 汤匙茶粉，如果在调用时没有提供的话。这就是我们的第一个默认参数。

其他可选参数是牛奶(以毫升为单位)、香草(添加或不添加)和糖(以汤匙为单位)。如果没有提供它们的任何值，我们假设用户不需要它们。

让我们看看如何使用方法重载在Java中实现这一点：

```java
public Tea(String name, int milk, boolean herbs, int sugar, int teaPowder) {
    this.name = name;
    this.milk = milk;
    this.herbs = herbs;
    this.sugar = sugar;
    this.teaPowder = teaPowder;
}

public Tea(String name, int milk, boolean herbs, int sugar) {
    this(name, milk, herbs, sugar, DEFAULT_TEA_POWDER);
}

public Tea(String name, int milk, boolean herbs) {
    this(name, milk, herbs, 0);
}

public Tea(String name, int milk) {
    this(name, milk, false);
}

public Tea(String name) {
    this(name, 0);
}
```

很明显，我们在这里使用构造函数链接，这是一种为方法参数提供一些默认值的重载形式。

现在让我们添加一个简单的测试来查看实际效果：

```java
@Test
public void whenTeaWithOnlyName_thenCreateDefaultTea() {
    Tea blackTea = new Tea("Black Tea");

    assertThat(blackTea.getName()).isEqualTo("Black Tea");
    assertThat(blackTea.getMilk()).isEqualTo(0);
    assertThat(blackTea.isHerbs()).isFalse();
    assertThat(blackTea.getSugar()).isEqualTo(0);
    assertThat(blackTea.getTeaPowder()).isEqualTo(Tea.DEFAULT_TEA_POWDER);
}
```

## 3.备选方案

Java中还有其他方法可以实现默认参数模拟。他们之中有一些是：

-   使用[建造者模式](https://www.baeldung.com/java-builder-pattern-freebuilder#Builder java)
-   使用[可选](https://www.baeldung.com/java-optional)
-   允许空值作为方法参数

以下是我们如何在示例中使用允许空参数的第三种方法：

```java
public Tea(String name, Integer milk, Boolean herbs, Integer sugar, Integer teaPowder) {
    this.name = name;
    this.milk = milk == null ? 0 : milk.intValue();
    this.herbs = herbs == null ? false : herbs.booleanValue();
    this.sugar = sugar == null ? 0 : sugar.intValue();
    this.teaPowder = teaPowder == null ? DEFAULT_TEA_POWDER : teaPowder.intValue();
}
```

## 4。总结

在本文中，我们研究了使用方法重载来模拟Java中的默认参数。

虽然还有其他方法可以实现相同的目的，但重载是最干净和简单的。