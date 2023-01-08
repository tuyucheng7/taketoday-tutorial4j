## 1. 概述

在添加信息性注解@FunctionalInterface之前，Java已经有了[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)。FilenameFilter就是这样一个接口。

我们将简要了解它的用法，并了解它在当今Java世界中的位置。

## 2.文件名过滤

因为这是一个函数式接口——我们必须有一个抽象方法，并且 FilenameFilter遵循这个定义：

```java
boolean accept(File dir, String name);
```

## 3.用法

我们几乎只使用FilenameFilter来列出目录中满足指定过滤器的所有文件。

java.io.File中重载的list(..)和listFiles(..)方法采用FilenameFilter的实例并返回满足过滤器的所有文件的数组。

下面的测试用例过滤一个目录下的所有json文件：

```java
@Test
public void whenFilteringFilesEndingWithJson_thenEqualExpectedFiles() {
    FilenameFilter filter = (dir, name) -> name.endsWith(".json");

    String[] expectedFiles = { "people.json", "students.json" };
    File directory = new File(getClass().getClassLoader()
      .getResource("testFolder")
      .getFile());
    String[] actualFiles = directory.list(filter);

    Assert.assertArrayEquals(expectedFiles, actualFiles);
}
```

### 3.1. FileFilter作为BiPredicate

Oracle 在Java8 中添加了 40 多个函数式接口，与遗留接口不同，它们是通用的。这意味着我们可以将它们用于任何引用类型。

[BiPredicate](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/BiPredicate.html)就是这样一个接口。它的单一抽象方法有这样的定义：

```java
boolean test(T t, U u);
```

这意味着FilenameFilter只是BiPredicate的一个特例，其中T是File而U是String。

## 4。总结

即使我们现在拥有通用的Predicate和BiPredicate函数接口，我们仍会继续看到FilenameFilter的出现，因为它已在现有的Java库中使用。

此外，它很好地服务于它的单一用途，因此没有理由在适用时不使用它。