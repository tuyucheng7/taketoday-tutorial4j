## 1. 概述

在某些情况下，如果另一个Optional实例为空，我们可能希望回退到另一个 Optional 实例。

在本教程中，我们将简要介绍如何做到这一点——这比看起来要难。

有关JavaOptional 类的介绍，请查看我们[之前的文章](https://www.baeldung.com/java-optional)。

## 2.Java8

在Java8 中，如果第一个为空，则没有直接的方法来实现返回不同的Optional 。

因此，我们可以实现自己的自定义方法：

```java
public static <T> Optional<T> or(Optional<T> optional, Optional<T> fallback) {
    return optional.isPresent() ? optional : fallback;
}
```

而且，在实践中：

```java
@Test
public void givenOptional_whenValue_thenOptionalGeneralMethod() {
    String name = "Filan Fisteku";
    String missingOptional = "Name not provided";
    Optional<String> optionalString = Optional.ofNullable(name);
    Optional<String> fallbackOptionalString = Optional.ofNullable(missingOptional);
 
    assertEquals(
      optionalString, 
      Optionals.or(optionalString, fallbackOptionalString));
}
    
@Test
public void givenEmptyOptional_whenValue_thenOptionalGeneralMethod() {
    Optional<String> optionalString = Optional.empty();
    Optional<String> fallbackOptionalString = Optional.ofNullable("Name not provided");
 
    assertEquals(
      fallbackOptionalString, 
      Optionals.or(optionalString, fallbackOptionalString));
}
```

### 2.1. 惰性评估

上面的解决方案有一个严重的缺点——我们需要 在使用我们的自定义 or()方法之前评估两个Optional变量。

想象一下，我们有两个返回Optional 的方法，都在后台查询数据库。从性能的角度来看，如果第一个方法已经返回了我们需要的值，那么同时调用这两个方法是不可接受的。

让我们创建一个简单的ItemsProvider类：

```java
public class ItemsProvider {
    public Optional<String> getNail(){
        System.out.println("Returning a nail");
        return Optional.of("nail");
    }

    public Optional<String> getHammer(){
        System.out.println("Returning a hammer");
        return Optional.of("hammer");
    }
}
```

以下是我们如何链接这些方法并利用惰性求值的方法：

```java
@Test
public void givenTwoOptionalMethods_whenFirstNonEmpty_thenSecondNotEvaluated() {
    ItemsProvider itemsProvider = new ItemsProvider();

    Optional<String> item = itemsProvider.getNail()
            .map(Optional::of)
            .orElseGet(itemsProvider::getHammer);

    assertEquals(Optional.of("nail"), item);
}
```

上面的测试用例只打印了“Returning a nail”。这清楚地表明只执行了getNail()方法。

## 3.Java9

Java 9 添加了一个or()方法，如果Optional不存在，我们可以使用它来获取Optional或另一个值。

让我们通过一个简单的例子在实践中看到这一点：

```java
public static Optional<String> getName(Optional<String> name) {
    return name.or(() -> getCustomMessage());
}
```

我们使用了一种辅助方法来帮助我们完成示例：

```java
private static Optional<String> getCustomMessage() {
    return Optional.of("Name not provided");
}
```

我们可以对其进行测试并进一步了解其工作原理。下面的测试用例是Optional有值时的演示：

```java
@Test
public void givenOptional_whenValue_thenOptional() {
    String name = "Filan Fisteku";
    Optional<String> optionalString = Optional.ofNullable(name);
    assertEquals(optionalString, Optionals.getName(optionalString));
}
```

## 4.使用番石榴

另一种方法是使用番石榴Optional类的or()方法。首先，我们需要在我们的项目中添加番石榴(最新版本可以[在这里](https://mvnrepository.com/artifact/com.google.guava/guava/19.0)找到)：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>

```

现在，我们可以继续我们之前的相同示例：

```java
public static com.google.common.base.Optional<String> 
  getOptionalGuavaName(com.google.common.base.Optional<String> name) {
    return name.or(getCustomMessageGuava());
}
private static com.google.common.base.Optional<String> getCustomMessageGuava() {
    return com.google.common.base.Optional.of("Name not provided");
}
```

正如我们所看到的，它与上面显示的非常相似。但是，它在方法的命名上略有不同，与JDK 9 中Optional类的or()方法完全相同。

我们现在可以测试它，类似于上面的例子：

```java
@Test
public void givenGuavaOptional_whenInvoke_thenOptional() {
    String name = "Filan Fisteku";
    Optional<String> stringOptional = Optional.of(name);
 
    assertEquals(name, Optionals.getOptionalGuavaName(stringOptional));
}
@Test
public void givenGuavaOptional_whenNull_thenDefaultText() {
    assertEquals(
      com.google.common.base.Optional.of("Name not provided"), 
      Optionals.getOptionalGuavaName(com.google.common.base.Optional.fromNullable(null)));
}
```

## 5.总结

这是一篇说明如何实现Optional 或 Else Optional功能的快速文章。