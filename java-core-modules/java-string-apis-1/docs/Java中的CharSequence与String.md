## 1. 概述

简单地说，CharSequence和String是Java中两个不同的基本概念。

在这篇简短的文章中，我们将了解这些类型之间的区别以及何时使用它们。

## 2.字符序列

CharSequence是表示字符序列的接口。此接口不强制执行可变性。因此，可变类和不可变类都实现了这个接口。

当然，接口不能直接实例化；它需要一个实现来实例化一个变量：

```java
CharSequence charSequence = "baeldung";
```

在这里，charSequence是用String 实例化的。实例化其他实现：

```java
CharSequence charSequence = new StringBuffer("baeldung");
CharSequence charSequence = new StringBuilder("baeldung");
```

## 3.字符串

字符串是Java中的字符序列。它是一个不可变类，是Java中最常用的类型之一。此类实现CharSequence、Serializable和Comparable<String>接口。

下面两个实例创建具有相同内容的字符串。但是，它们彼此不相等：

```java
@Test
public void givenUsingString_whenInstantiatingString_thenWrong() {
    CharSequence firstString = "baeldung";
    String secondString = "baeldung";

    assertNotEquals(firstString, secondString);
}
```

## 4. CharSequence与String

让我们比较一下CharSequence和String的区别和共性。它们都位于同一个名为java.lang 的包中。，但前者是一个接口，后者是一个具体的类。此外，String类是不可变的。

在下面的示例中，每个求和操作都会创建另一个实例，增加存储的数据量并返回最近创建的String：

```java
@Test
public void givenString_whenAppended_thenUnmodified() {
    String test = "a";
    int firstAddressOfTest = System.identityHashCode(test);
    test += "b";
    int secondAddressOfTest = System.identityHashCode(test);

    assertNotEquals(firstAddressOfTest, secondAddressOfTest);
}
```

另一方面，StringBuilder更新已经创建的String以保存新值：

```java
@Test
public void givenStringBuilder_whenAppended_thenModified() {
    StringBuilder test = new StringBuilder();
    test.append("a");
    int firstAddressOfTest = System.identityHashCode(test);
    test.append("b");
    int secondAddressOfTest = System.identityHashCode(test);        
    
    assertEquals(firstAddressOfTest, secondAddressOfTest);
}
```

另一个区别是接口不暗示内置比较策略，而String类实现Comparable<String>接口。

要比较两个CharSequence s，我们可以将它们转换为String s，然后再比较它们：

```java
@Test
public void givenIdenticalCharSequences_whenCastToString_thenEqual() {
    CharSequence charSeq1 = "baeldung_1";
    CharSequence charSeq2 = "baeldung_2";
 
    assertTrue(charSeq1.toString().compareTo(charSeq2.toString()) > 0);
}
```

## 5.总结

我们通常在不确定使用什么字符序列的地方使用String 。但是，在某些情况下，StringBuilder和StringBuffer可能更合适。

你可以在 JavaDocs 中找到有关[CharSequence](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/CharSequence.html)和[String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)的更多信息。