## 1. 概述

在本文中，我们将了解Java中的菱形运算符以及[泛型](https://www.baeldung.com/java-generics)和集合API如何影响它的演变。

## 2. 原始类型

在Java 1.5之前，Collections API仅支持原始类型-在构造集合时无法对类型参数进行参数化：

```java
List cars = new ArrayList();
cars.add(new Object());
cars.add("car");
cars.add(new Integer(1));
```

这允许添加任何类型并在运行时导致潜在的转换异常。

## 3. 泛型

在 Java 1.5中，引入了泛型-它允许我们在声明和构造对象时参数化类的类型参数，包括Collections API中的类型参数：

```java
List<String> cars = new ArrayList<String>();
```

此时，我们必须在构造函数中指定参数化类型，这可能有些不可读：

```java
Map<String, List<Map<String, Map<String, Integer>>>> cars = new HashMap<String, List<Map<String, Map<String, Integer>>>>();
```

之所以采用这种方式，是因为为了向后兼容，原始类型仍然存在，所以编译器需要区分这些原始类型和泛型：

```java
List<String> generics = new ArrayList<String>();
List<String> raws = new ArrayList();
```

即使编译器仍然允许我们在构造函数中使用原始类型，它也会提示我们警告信息：

```shell
ArrayList is a raw type. References to generic type ArrayList<E> should be parameterized
```

## 4. 钻石运算符

菱形运算符在Java 1.7中引入-添加了类型推断并减少了赋值中的冗长，当使用泛型时：

```java
List<String> cars = new ArrayList<>();
```

Java 1.7编译器的类型推断特性决定了与调用相匹配的最合适的构造函数声明。

考虑以下用于车辆和引擎的接口和类层次结构：

```java
public interface Engine { }
public class Diesel implements Engine { }
public interface Vehicle<T extends Engine> { }
public class Car<T extends Engine> implements Vehicle<T> { }
```

让我们使用菱形运算符创建一个Car的新实例：

```java
Car<Diesel> myCar = new Car<>();
```

在内部，编译器知道Diesel实现了Engine接口，然后能够通过推断类型来确定合适的构造函数。

## 5. 总结

简而言之，菱形运算符为编译器添加了类型推断功能，并减少了泛型引入的赋值中的冗长。