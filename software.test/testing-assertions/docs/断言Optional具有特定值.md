## 1. 概述

当我们测试返回Optional对象的方法时，我们可能需要编写断言来检查Optional是否有值或检查其中包含的值。

在这个教程中，我们介绍如何使用JUnit和AssertJ中的方法编写这些断言。

## 2. 测试一个Optional是否为空

如果我们只需要找出Optional是否有值，我们可以使用isPresent或isEmpty断言。

### 2.1 测试Optional有一个值

如果Optional有值，我们可以在Optional.isPresent上断言：

```java
assertTrue(optional.isPresent());
```

不过，AssertJ库提供了一种更流畅的表达方式：

```java
assertThat(optional).isNotEmpty();
```

### 2.2 测试Optional为空

当使用JUnit时，我们可以反转逻辑：

```java
assertFalse(optional.isPresent());
```

此外，在Java 11之后，我们可以使用Optional.isEmpty：

```java
assertTrue(optional.isEmpty());
```

但是，AssertJ也为我们提供了一个简洁的替代方案：

```java
assertThat(optional).isEmpty();
```

## 3. 测试一个Optional的值

通常我们想要测试Optional中的值，而不仅仅是存在或不存在。

### 3.1 使用JUnit Assertions

我们可以使用Optional.get来获取值，然后编写一个断言：

```java
Optional<String> optional = Optional.of("SOMEVALUE");
assertEquals("SOMEVALUE", optional.get());
```

但是，使用get可能会导致异常，这使得更难理解测试失败；因此，我们可能更倾向于先断言该值是否存在：

```java
assertTrue(optional.isPresent());
assertEquals("SOMEVALUE", optional.get());
```

不过，**Optional支持equals方法**，因此我们可以使用具有正确值的Optional作为一般相等断言的一部分：

```java
Optional<String> expected = Optional.of("SOMEVALUE");
Optional<String> actual = Optional.of("SOMEVALUE");
assertEquals(expected, actual);
```

### 3.2 使用AssertJ

使用AssertJ，我们可以使用hasValue流式断言：

```java
assertThat(Optional.of("SOMEVALUE")).hasValue("SOMEVALUE");
```

## 4. 总结

在本文中，我们介绍了几种测试Optional的方法。

我们演示了内置的JUnit断言如何与isPresent和get一起使用，并介绍了Optional.equals如何为我们提供了一种比较断言中的Optional对象的方法。

最后，我们介绍了AssertJ断言，它为我们提供了流式的API来检查我们的Optional值。