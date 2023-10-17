## 一、简介

Java 中的类命名遵循称为 Upper Camel Case syntax 的国际惯例，就像主要的编程语言一样。然而，当涉及到处理具有相同名称的类时，就存在一个挑战。

**自 1998 年 JDK 早期发布以来，一直存在关于如何解决这种异常情况的争论。**这里是[JDK-4194542](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4194542)，这是关于该主题的第一个公开错误，从那时起，JDK 开发团队的建议就是使用完全限定的类名。尽管如此，JDK 短期内没有任何允许这种用法的功能的计划。

最近，在 2019 年 8 月，Java 开发者社区就如何解决这种情况提出了一项新提案( [JEP )，并获得了全球 Java 开发者的更多支持。](https://gist.github.com/cardil/b29a81efd64a09585076fe00e3d34de7)

**在本教程中，我们将讨论处理同名类的策略和建议。**

## 2.定义类

首先，让我们在自定义包com.baeldung.date 中创建一个名为Date的类。

```java
package com.baeldung.date;

public class Date {

    private long currentTimeMillis;

    public Date() {
        this(System.currentTimeMillis());
    }

    public Date(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public long getTime() {
        return currentTimeMillis;
    }
}
```

## 3.完全合格的类名

**当这种类型的使用被隔离并且不经常重复时，我们将使用这种方法来避免冲突。**然而，使用完全限定名称通常被认为是一种糟糕的风格。

**让我们看看如何使用它，特别是如果包名很短且具有描述性可以使代码更具表现力，从而减少混淆并提高可读性。**

另一方面，当使用的对象是太大的类或方法时，它有助于调试：

```java
public class DateUnitTest {

    @Test
    public void whenUsingFullyQualifiedClassNames() {

        java.util.Date javaDate = new java.util.Date();
        date.cn.tuyucheng.taketoday.Date baeldungDate = new date.cn.tuyucheng.taketoday.Date(javaDate.getTime());

        Assert.assertEquals(javaDate.getTime(), baeldungDate.getTime());
    }
}
```

## 4.导入最常用的

**我们导入我们最常使用的一个，并利用具有完整类路径的最少使用的一个，因为这是 Java 开发人员的常用技术和最佳实践：**

```java
import java.util.Date;

public class DateUnitTest {

    @Test
    public void whenImportTheMostUsedOne() {

        Date javaDate = new Date();
        date.cn.tuyucheng.taketoday.Date baeldungDate = new date.cn.tuyucheng.taketoday.Date(javaDate.getTime());

        Assert.assertEquals(javaDate.getTime(), baeldungDate.getTime());
    }
}
```

## 5.总结

在本文中，我们说明了根据特定情况使用具有相同名称的类的两种可能方法，并观察了它们之间的主要区别。