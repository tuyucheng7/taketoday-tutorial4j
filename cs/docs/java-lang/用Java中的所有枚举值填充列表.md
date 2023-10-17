## 1. 概述

Java在1.5版本中引入了[枚举](https://www.baeldung.com/a-guide-to-java-enums)。将常量定义为枚举使代码更具可读性。此外，它允许编译时检查。

在本快速教程中，让我们探讨如何获取包含枚举类型的所有实例的列表。

## 2. 问题简介

像往常一样，我们将通过一个例子来理解这个问题。

首先，让我们创建一个枚举类型MagicNumber：

```java
enum MagicNumber {
    ONE, TWO, THREE, FOUR, FIVE
}
```

然后，我们的目标是获得一个包含MagicNumber枚举的所有实例的列表：

```java
List<MagicNumber> EXPECTED_LIST = Arrays.asList(ONE, TWO, THREE, FOUR, FIVE);
```

在这里，我们使用Arrays.asList()方法[从数组初始化列表](https://www.baeldung.com/java-init-list-one-line#create-from-an-array)。

稍后，我们将探索几种不同的方法来获得预期的结果。最后，为简单起见，我们将使用单元测试断言来验证每个方法是否给出了预期的结果。

那么接下来，让我们看看他们的实际效果。

## 3. 使用EnumType.values()方法

当我们准备EXPECTED_LIST时，我们从一个数组中初始化它。因此，如果我们可以从数组中的枚举中获取所有实例，我们就可以构建列表并解决问题。

每个枚举类型都提供标准的values()方法来返回数组中的所有实例。那么接下来，让我们从MagicNumber.values()建立一个列表：

```java
List<MagicNumber> result = Arrays.asList(MagicNumber.values());
assertEquals(EXPECTED_LIST, result);
```

如果我们运行测试，它就会通过。所以，我们得到了预期的列表。

## 4. 使用EnumType.class.getEnumConstants()方法

我们已经看到使用枚举类型的values()来获取数组中的所有枚举实例。这是一种标准且直接的方法。但是，我们需要确切地知道枚举类型的名称并将其硬编码在代码中，例如MagicNumber.values()。换句话说，通过这种方式，我们无法构建适用于所有枚举类型的实用方法。

从Java 1.5开始，Class对象提供了getEnumConstants()方法来从枚举Class对象中获取所有枚举实例。因此，我们可以让getEnumConstants()提供枚举实例：

```java
List<MagicNumber> result = Arrays.asList(MagicNumber.class.getEnumConstants());
assertEquals(EXPECTED_LIST, result);
```

如上面的测试所示，我们使用MagicNumber.class.getEnumConstants()来提供枚举实例数组。此外，很容易构建适用于所有枚举类型的实用方法：

```java
static <T> List<T> enumValuesInList(Class<T> enumCls) {
    T[] arr = enumCls.getEnumConstants();
    return arr == null ? Collections.emptyList() : Arrays.asList(arr);
}
```

值得一提的是，如果Class对象不是枚举类型，则getEnumConstants()方法返回null。如我们所见，在这种情况下我们返回一个空列表。

接下来，让我们创建一个测试来验证enumValuesInList()：

```java
List<MagicNumber> result1 = enumValuesInList(MagicNumber.class);
assertEquals(EXPECTED_LIST, result1);
                                                                
List<Integer> result2 = enumValuesInList(Integer.class);
assertTrue(result2.isEmpty());
```

如果我们试一试，测试就会通过。如我们所见，如果类对象不在枚举类型中，我们就有一个空列表。

## 5. 使用EnumSet.allOf()方法

从1.5版开始，Java引入了一个特定的Set来处理枚举类：[EnumSet](https://www.baeldung.com/java-enumset)。此外，EnumSet具有allOf()方法来加载给定枚举类型的所有实例。

因此，我们可以使用ArrayList()构造函数和填充的EnumSet来构造一个List对象。那么接下来，我们通过一个测试来看看它是如何工作的：

```java
List<MagicNumber> result = new ArrayList<>(EnumSet.allOf(MagicNumber.class));
assertEquals(EXPECTED_LIST, result);
```

值得一提的是，调用allOf()方法以自然顺序存储enum的实例。

## 6. 总结

在本文中，我们学习了三种获取包含枚举所有实例的List对象的方法。