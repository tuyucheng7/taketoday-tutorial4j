## 1. 概述

在本快速教程中，我们将介绍在Java中将字符数组转换为字符串的各种方法。

## 2. 字符串构造器

String类有一个构造函数，它接受一个char数组作为参数：

```java
@Test 
public void whenStringConstructor_thenOK() {
    final char[] charArray = { 'b', 'a', 'e', 'l', 'd', 'u', 'n', 'g' };
    String string = new String(charArray);
    assertThat(string, is("baeldung"));
}
```

这是将char数组转换为String的最简单方法之一。它在内部调用String#valueOf 来创建一个String对象。

## 3.字符串.valueOf()

说到valueOf()，我们甚至可以直接使用它：

```java
@Test
public void whenStringValueOf_thenOK() {
    final char[] charArray = { 'b', 'a', 'e', 'l', 'd', 'u', 'n', 'g' };
    String string = String.valueOf(charArray);
    assertThat(string, is("baeldung"));
}
```

String#copyValueOf 是另一种在语义上等同于valueOf()方法的方法，但仅在最初的几个Java版本中才有意义。截至目前，copyValueOf ()方法是多余的，我们不建议使用它。

## 4. StringBuilder的toString()

如果我们想从一个char数组组成一个String怎么办？

然后，我们可以先实例化一个StringBuilder实例，并使用它的append(char[])方法将所有内容追加在一起。

稍后，我们将使用 toString()方法来获取其String表示形式：

```java
@Test
public void whenStringBuilder_thenOK() {
    final char[][] arrayOfCharArray = { { 'b', 'a' }, { 'e', 'l', 'd', 'u' }, { 'n', 'g' } };    
    StringBuilder sb = new StringBuilder();
    for (char[] subArray : arrayOfCharArray) {
        sb.append(subArray);
    }
    assertThat(sb.toString(), is("baeldung"));
}
```

我们可以通过实例化我们需要的确切长度的StringBuilder来进一步优化上面的代码。

## 5.Java8 流

使用Arrays.stream(T[] object)方法，我们可以在T类型的数组上打开一个流。

考虑到我们有一个Character数组，我们可以使用Collectors.joining()操作来形成一个String实例：

```java
@Test
public void whenStreamCollectors_thenOK() {
    final Character[] charArray = { 'b', 'a', 'e', 'l', 'd', 'u', 'n', 'g' };
    Stream<Character> charStream = Arrays.stream(charArray);
    String string = charStream.map(String::valueOf).collect(Collectors.joining());
    assertThat(string, is("baeldung"));
}
```

这种方法的警告是我们在每个Character元素上调用valueOf()，因此它会非常慢。

## 6. Guava 通用基础连接器

假设我们需要创建的字符串是分隔字符串。Guava 给了我们一个方便的方法：

```java
@Test
public void whenGuavaCommonBaseJoiners_thenOK() {
    final Character[] charArray = { 'b', 'a', 'e', 'l', 'd', 'u', 'n', 'g' };
    String string = Joiner.on("|").join(charArray);
    assertThat(string, is("b|a|e|l|d|u|n|g"));
}
```

再次注意，join ()方法将只接受字符数组，而不是原始字符数组。

## 七、总结

在本教程中，我们探索了将给定字符数组转换为Java 中的字符串表示形式的方法。