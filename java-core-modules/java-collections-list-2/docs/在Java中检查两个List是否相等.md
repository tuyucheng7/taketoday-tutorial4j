## 1. 概述

在这篇简短的文章中，我们将重点关注测试两个List实例是否以完全相同的顺序包含相同元素的常见问题。

[列表](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html) 是一种有序的数据结构，因此元素的顺序在设计上很重要。

[看一下List#equals](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html#equals(java.lang.Object))Java文档的摘录：

>   …如果两个列表以相同的顺序包含相同的元素，则它们被定义为相等。

此定义可确保 equals 方法在 List 接口的不同实现中正常工作。

我们可以在编写断言时使用这些知识。

在以下代码片段中，我们将使用以下列表作为示例输入：

```java
List<String> list1 = Arrays.asList("1", "2", "3", "4");
List<String> list2 = Arrays.asList("1", "2", "3", "4");
List<String> list3 = Arrays.asList("1", "2", "4", "3");
```

## 2.J单元

在纯 JUnit 测试中，以下断言为真：

```java
@Test
public void whenTestingForEquality_ShouldBeEqual() throws Exception {
    Assert.assertEquals(list1, list2);
    Assert.assertNotSame(list1, list2);
    Assert.assertNotEquals(list1, list3);
}
```

## 3. 测试NG

当使用 TestNG 的断言时，它们看起来与 JUnit 的断言非常相似，但重要的是要注意[Assert](https://static.javadoc.io/org.testng/testng/6.9.5/org/testng/Assert.html)类来自不同的包：

```java
@Test
public void whenTestingForEquality_ShouldBeEqual() throws Exception {
    Assert.assertEquals(list1, list2);
    Assert.assertNotSame(list1, list2);
    Assert.assertNotEquals(list1, list3);
}
```

## 4.断言

如果你喜欢使用[AssertJ](https://joel-costigliola.github.io/assertj/)，它的断言将如下所示：

```java
@Test
public void whenTestingForEquality_ShouldBeEqual() throws Exception {
    assertThat(list1)
      .isEqualTo(list2)
      .isNotEqualTo(list3);

    assertThat(list1.equals(list2)).isTrue();
    assertThat(list1.equals(list3)).isFalse();
}
```

## 5.总结

在本文中，我们探索了如何测试两个List实例是否以相同的顺序包含相同的元素。这个问题最重要的部分是正确理解List数据结构是如何设计的。