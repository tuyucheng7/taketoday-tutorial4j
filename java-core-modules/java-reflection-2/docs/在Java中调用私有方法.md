## 1. 概述

虽然在Java中方法是私有的可以防止它们被所属类的外部调用，但出于某些原因我们可能仍然需要调用它们。

为此，我们需要解决Java的访问控制问题。这可能会帮助我们到达库的一个角落，或者允许我们测试一些通常应该保持私有的代码。

在这个简短的教程中，我们将研究如何验证方法的功能而不考虑其可见性。我们将考虑两种不同的方法：[Java反射API](https://www.baeldung.com/java-reflection)和Spring的[ReflectionTestUtils](https://www.baeldung.com/spring-reflection-test-utils)。

## 2. 可见性超出我们的控制

对于我们的示例，让我们使用对long数组进行操作的实用程序类LongArrayUtil。我们的类有两个indexOf方法：

```java
public static int indexOf(long[] array, long target) {
    return indexOf(array, target, 0, array.length);
}

private static int indexOf(long[] array, long target, int start, int end) {
    for (int i = start; i < end; i++) {
        if (array[i] == target) {
            return i;
        }
    }
    return -1;
}
```

假设这些方法的可见性不能改变，但我们想调用私有的indexOf方法。

## 3. Java反射API

### 3.1 通过反射找到方法

虽然编译器阻止我们调用对我们的类不可见的函数，但我们可以通过反射调用函数。首先，我们需要访问描述我们要调用的函数的Method对象：

```java
Method indexOfMethod = LongArrayUtil.class.getDeclaredMethod("indexOf", long[].class, long.class, int.class, int.class);
```

我们必须使用getDeclaredMethod才能访问非私有方法。我们在具有函数的类型上调用它，在本例中为LongArrayUtil，我们传入参数的类型以识别正确的方法。

如果该方法不存在，该函数可能会失败并抛出异常。

### 3.2 允许方法被访问

现在我们需要暂时提升方法的可见性：

```java
indexOfMethod.setAccessible(true);
```

此更改将持续到JVM停止，或将accessible属性设置回false。

### 3.3 使用反射调用方法

最后，我们[在Method对象上调用invoke](https://www.baeldung.com/java-method-reflection)：

```java
int value = (int) indexOfMethod.invoke(LongArrayUtil.class, someLongArray, 2L, 0, someLongArray.length);
```

我们现在已经成功访问了一个私有方法。

invoke的第一个参数是目标对象，其余参数需要与我们的方法签名相匹配。在这种情况下，我们的方法是静态的，目标对象是父类-LongArrayUtil。为了调用实例方法，我们将传递我们正在调用其方法的对象。

我们还应该注意invoke返回Object，它对于void函数是null，并且需要强制转换为正确的类型才能使用它。

## 4. Spring ReflectionTestUtils

访问类的内部是测试中的一个常见问题，Spring的测试库提供了一些快捷方式帮助单元测试到达类。这通常解决特定于单元测试的问题，其中测试需要访问Spring可能在运行时实例化的私有字段。

首先，我们需要在pom.xml中添加[spring-test](https://search.maven.org/search?q=a:spring-test)依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.3.4</version>
    <scope>test</scope>
</dependency>
```

现在我们可以使用ReflectionTestUtils中的invokeMethod函数，它使用与上面相同的算法，并节省我们编写的代码：

```java
int value = ReflectionTestUtils.invokeMethod(LongArrayUtil.class, "indexOf", someLongArray, 1L, 1, someLongArray.length);
```

由于这是一个测试库，我们不希望在测试代码之外使用它。

## 5. 注意事项

使用反射来绕过函数可见性会带来一些风险，甚至可能是不可能的。我们应该考虑：

-   Java安全管理器是否会在我们的运行时允许这样做
-   我们正在调用的函数，在没有编译时检查的情况下，是否会继续存在以供我们将来调用
-   重构我们自己的代码，使事情更加可见和可访问

## 6. 总结

在本文中，我们研究了如何使用Java反射API和使用Spring的ReflectionTestUtils访问私有方法。