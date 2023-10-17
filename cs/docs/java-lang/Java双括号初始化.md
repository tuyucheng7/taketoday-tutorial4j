## 1. 概述

在本快速教程中，我们将展示如何使用双大括号**在单个Java表达式中创建和初始化对象**。

我们还将研究为什么这种技术会被视为反模式。

## 2. 标准方法

通常我们初始化并填充一个countries集合如下：

```java
@Test
public void whenInitializeSetWithoutDoubleBraces_containsElements() {
    Set<String> countries = new HashSet<String>();                
    countries.add("India");
    countries.add("USSR");
    countries.add("USA");
 
    assertTrue(countries.contains("India"));
}
```

从上面的例子可以看出，我们正在执行以下操作：

1.  创建HashSet的实例
2.  将国家添加到HashSet
3.  最后，我们断言该国家/地区是否存在于HashSet中

## 3. 使用双括号

但是，我们实际上可以将创建和初始化结合在一条语句中；这是我们使用双括号的地方：

```java
@Test
public void whenInitializeSetWithDoubleBraces_containsElements() {
    Set<String> countries = new HashSet<String>() {
        {
           add("India");
           add("China");
           add("USA");
        }
    };
 
    assertTrue(countries.contains("India"));
}
```

从上面的例子可以看出，我们是：

1.  创建一个扩展HashSet的匿名内部类
2.  提供一个实例初始化块，它调用add方法并将国家名称添加到HashSet中
3.  最后，我们可以断言该国家/地区是否存在于HashSet中

## 4. 使用双括号的优点

使用双括号有一些简单的优点：

-   与创建和初始化的原生方式相比，代码行更少
-   代码更具可读性
-   创建初始化在同一个表达式中完成

## 5. 使用双括号的缺点

使用双括号的缺点是：

-   晦涩难懂的初始化方法
-   每次我们使用它都会创建一个额外的类
-   不支持使用“菱形运算符”-Java 7中引入的一个特性
-   如果我们试图扩展的类被标记为final，则不起作用
-   持有对封闭实例的隐藏引用，这可能会导致内存泄漏

正是由于这些缺点，双括号初始化被认为是一种反模式。

## 6. 备选方案

### 6.1 Stream工厂方法

相反，我们可以充分利用新的Java 8 Stream API来初始化我们的Set：

```java
@Test
public void whenInitializeUnmodifiableSetWithDoubleBrace_containsElements() {
    Set<String> countries = Stream.of("India", "USSR", "USA")
        .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
 
    assertTrue(countries.contains("India"));
}
```

### 6.2 Java 9集合工厂方法

此外，Java 9也带来了一组有用的工厂方法，使以下代码成为可能：

```java
List<String> list = List.of("India", "USSR", "USA");
Set<String> set = Set.of("India", "USSR", "USA");
```

你可以在[本文](https://www.baeldung.com/java-9-collections-factory-methods)中阅读更多相关信息。

## 7. 总结

在这个简短的教程中，我们讨论了双花括号的用法及其优缺点。