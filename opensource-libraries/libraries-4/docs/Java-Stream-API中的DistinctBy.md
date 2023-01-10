## 1. 概述

在列表中搜索不同的元素是我们作为程序员通常面临的常见任务之一。从Java8 开始，随着Streams的加入，我们有了一个新的 API 来使用函数式方法处理数据。

在本文中，我们将展示使用列表中对象的特定属性过滤集合的不同替代方法。

## 2.使用流API

Stream API 提供了distinct()方法，该方法基于Object类的equals()方法返回列表的不同元素。

但是，如果我们想按特定属性进行过滤，它就会变得不那么灵活。我们的替代方案之一是编写一个维护状态的过滤器。

### 2.1. 使用有状态过滤器

一种可能的解决方案是实现有状态的谓词：

```java
public static <T> Predicate<T> distinctByKey(
    Function<? super T, ?> keyExtractor) {
  
    Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
}
```

为了测试它，我们将使用以下具有属性age、email和name的Person类：

```java
public class Person { 
    private int age; 
    private String name; 
    private String email; 
    // standard getters and setters 
}
```

要按名称获取新的过滤集合，我们可以使用：

```java
List<Person> personListFiltered = personList.stream() 
  .filter(distinctByKey(p -> p.getName())) 
  .collect(Collectors.toList());
```

## 3. 使用 Eclipse 集合

[Eclipse Collections](https://www.eclipse.org/collections/)是一个库，它提供了用于处理Java 中的流和集合的附加方法。

### 3.1. 使用ListIterate.distinct()

ListIterate.distinct ()方法允许我们使用各种HashingStrategies来过滤Stream 。这些策略可以使用 lambda 表达式或方法引用来定义。

如果我们想按人名过滤：

```java
List<Person> personListFiltered = ListIterate
  .distinct(personList, HashingStrategies.fromFunction(Person::getName));
```

或者，如果我们要使用的属性是原始属性(int、long、double)，我们可以使用这样的专用函数：

```java
List<Person> personListFiltered = ListIterate.distinct(
  personList, HashingStrategies.fromIntFunction(Person::getAge));
```

### 3.2. Maven 依赖

我们需要将以下依赖项添加到我们的pom.xml中，以便在我们的项目中使用 Eclipse Collections：

```xml
<dependency> 
    <groupId>org.eclipse.collections</groupId> 
    <artifactId>eclipse-collections</artifactId> 
    <version>8.2.0</version> 
</dependency>
```

[可以在Maven 中央](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.collections" AND a%3A"eclipse-collections")存储库中找到最新版本的 Eclipse Collections 库。

要了解有关此库的更多信息，我们可以转到[这篇文章](https://www.baeldung.com/eclipse-collections)。

## 4.使用Vavr

这是Java8 的功能库，提供不可变数据和功能控制结构。

### 4.1. 使用List.distinctBy

为了过滤列表，此类提供了自己的 List 类，它具有distinctBy()方法，允许我们按其包含的对象的属性进行过滤：

```java
List<Person> personListFiltered = List.ofAll(personList)
  .distinctBy(Person::getName)
  .toJavaList();
```

### 4.2. Maven 依赖

我们将向我们的pom.xml添加以下依赖项以在我们的项目中使用 Vavr。

```xml
<dependency> 
    <groupId>io.vavr</groupId> 
    <artifactId>vavr</artifactId> 
    <version>0.9.0</version>  
</dependency>
```

[可以在Maven 中央](https://search.maven.org/classic/#search|ga|1|a%3A"vavr")存储库中找到最新版本的 Vavr 库。

要了解有关此库的更多信息，我们可以转到[这篇文章](https://www.baeldung.com/vavr)。

## 5. 使用 StreamEx

该库为Java8 流处理提供了有用的类和方法。

### 5.1. 使用StreamEx.distinct

在提供的类中是StreamEx，它具有独特的方法，我们可以向该方法发送对我们想要区分的属性的引用：

```java
List<Person> personListFiltered = StreamEx.of(personList)
  .distinct(Person::getName)
  .toList();
```

### 5.2. Maven 依赖

我们将向我们的pom.xml添加以下依赖项以在我们的项目中使用 StreamEx。

```xml
<dependency> 
    <groupId>one.util</groupId> 
    <artifactId>streamex</artifactId> 
    <version>0.6.5</version> 
</dependency>
```

[可以在Maven 中央](https://search.maven.org/classic/#search|gav|1|g%3A"one.util" AND a%3A"streamex")存储库中找到最新版本的 StreamEx 库。

## 六. 总结

在本快速教程中，我们探讨了如何基于使用标准Java8 API 的属性和其他库的其他替代方法获取 Stream 的不同元素的示例。