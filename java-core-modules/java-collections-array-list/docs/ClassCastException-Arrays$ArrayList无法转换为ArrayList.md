## 1. 概述

ClassCastException是当我们尝试不正确地将类从一种类型转换为另一种类型时在Java中引发的运行时异常。抛出它表示代码已尝试将对象转换为相关类，但它不是该类的实例。

如需更深入地了解Java中的异常，请查看[此处](https://www.baeldung.com/java-exceptions)。

## 2. ClassCastException 详细信息

首先，让我们看一个简单的例子。考虑以下代码片段：

```java
String[] strArray = new String[] { "John", "Snow" };
ArrayList<String> strList = (ArrayList<String>) Arrays.asList(strArray);
System.out.println("String list: " + strList);
```

上面的代码导致ClassCastException ，我们将Arrays.asList(strArray) 的返回值转换为 ArrayList。

原因是虽然静态方法Arrays.asList()返回一个List， 但直到运行时我们才知道究竟返回了什么实现。所以在编译时编译器也不知道并允许强制转换。

当代码运行时，检查实际实现，发现Arrays.asList () 返回一个Arrays$List从而导致ClassCastException。

## 3.分辨率

我们可以简单地将ArrayList声明为List以避免此异常：

```java
List<String> strList = Arrays.asList(strArray);
System.out.println("String list: " + strList);
```

但是，通过将我们的引用声明为 List，我们可以分配任何实现 List接口的类，包括 方法调用返回的Arrays$ArrayList 。

## 4.总结

在本文中，我们了解了ClassCastException到底是什么以及我们必须采取什么措施来解决这个问题。