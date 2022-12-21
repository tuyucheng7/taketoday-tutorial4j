## 1. 概述

本文重点介绍[AssertJ](https://joel-costigliola.github.io/assertj/)的Java 8相关特性，是本系列的第三篇文章。

如果你正在寻找有关其主要功能的一般信息，请查看[AssertJ简介]()系列中的第一篇文章，然后查看[AssertJ for Guava]()。

## 2. Maven依赖

自从3.5.1版本起，Java 8的支持包含在主要的AssertJ核心模块中，为了使用该模块，你需要在pom.xml文件中包含以下部分：

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.5.1</version>
    <scope>test</scope>
</dependency>
```

此依赖项仅涵盖基本的Java断言，如果要使用更多高级断言，则需要单独添加其他模块。

## 3. Java 8特性

AssertJ通过为Java 8类型提供特殊的工具方法和新的断言来利用Java 8的特性。

### 3.1 Optional断言

让我们创建一个简单的Optional实例：

```java
Optional<String> givenOptional = Optional.of("something");
```

我们现在可以很容易地检查Optional是否包含某个值以及包含的值是什么：

```java
assertThat(givenOptional)
    .isPresent()
    .hasValue("something");
```

### 3.2 Predicate断言

我们通过检查字符串的长度来创建一个简单的Predicate实例：

```java
Predicate<String> predicate = s -> s.length() > 4;
```

现在你可以轻松地检查哪些字符串满足这个Predicate：

```java
assertThat(predicate)
    .accepts("aaaaa", "bbbbb")
    .rejects("a", "b")
    .acceptsAll(asList("aaaaa", "bbbbb"))
    .rejectsAll(asList("a", "b"));
```

### 3.3 LocalDate断言

首先我们定义两个LocalDate对象：

```java
LocalDate givenLocalDate = LocalDate.of(2016, 7, 8);
LocalDate todayDate = LocalDate.now();
```

然后我们可以检查给定日期是在给定日期之前/之后，或者是今天：

```java
assertThat(givenLocalDate)
    .isBefore(LocalDate.of(2020, 7, 8))
    .isAfterOrEqualTo(LocalDate.of(1989, 7, 8));

assertThat(todayDate)
    .isAfter(LocalDate.of(1989, 7, 8))
    .isToday();
```

### 3.4 LocalDateTime断言

LocalDateTime断言的使用方式与LocalDate的类似，但不共享isToday方法。

首先创建一个LocalDateTime对象：

```java
LocalDateTime givenLocalDate = LocalDateTime.of(2016, 7, 8, 12, 0);
```

现在我们执行以下断言：

```java
assertThat(givenLocalDate)
    .isBefore(LocalDateTime.of(2020, 7, 8, 11, 2));
```

### 3.5 LocalTime断言

LocalTime断言的使用方式与其他java.util.time.*断言类似，但它们有一个专有的方法hasSameHourAs。

首先创建一个LocalTime对象：

```java
LocalTime givenLocalTime = LocalTime.of(12, 15);
```

现在我们可以执行以下断言：

```java
assertThat(givenLocalTime)
    .isAfter(LocalTime.of(1, 0))
    .hasSameHourAs(LocalTime.of(12, 0));
```

### 3.6 flatExtracting工具方法

FlatExtracting是一种特殊的工具方法，它利用Java 8的lambdas从Iterable元素中提取属性。

让我们用LocalDate对象创建一个简单的List：

```java
List<LocalDate> givenList = asList(ofYearDay(2016, 5), ofYearDay(2015, 6));
```

现在我们可以检查这个List是否包含至少一个LocalDate对象，并且年份为2015：

```java
assertThat(givenList)
    .flatExtracting(LocalDate::getYear)
    .contains(2015);
```

flatExtracting方法并不局限于字段提取，我们始终可以为它提供任何函数：

```java
assertThat(givenList)
    .flatExtracting(LocalDate::isLeapYear)
    .contains(true);
```

甚至：

```java
assertThat(givenList)
    .flatExtracting(Object::getClass)
    .contains(LocalDate.class);
```

你还可以一次提取多个属性：

```java
assertThat(givenList)
    .flatExtracting(LocalDate::getYear, LocalDate::getDayOfMonth)
    .contains(2015, 6);
```

### 3.7 satisfies工具方法

satisfies方法允许你快速检查对象是否满足所有提供的断言。

让我们创建一个String实例：

```java
String givenString = "someString";
```

现在我们可以将断言作为lambda体提供：

```java
assertThat(givenString)
    .satisfies(s -> {
        assertThat(s).isNotEmpty();
        assertThat(s).hasSize(10);
    });
```

### 3.8 hasOnlyOneElementSatisfying工具方法

hasOnlyOneElementSatisfying工具方法允许检查Iterable实例是否仅包含一个满足所提供的断言的元素。

让我们创建一个集合：

```java
List<String> givenList = Arrays.asList("");
```

现在你可以进行以下断言：

```java
assertThat(givenList)
    .hasOnlyOneElementSatisfying(s -> assertThat(s).isEmpty());
```

### 3.9 matches工具方法

matches工具方法允许检查给定对象是否与给定Predicate函数匹配。

我们定义一个空字符串：

```java
String emptyString = "";
```

现在我们可以通过提供足够的Predicate lambda函数来检查它的状态：

```java
assertThat(emptyString)
    .matches(String::isEmpty);
```

## 4. 总结

在AssertJ系列的最后一篇文章中，我们探讨了AssertJ Java 8的所有高级特性，至此，该系列结束了。