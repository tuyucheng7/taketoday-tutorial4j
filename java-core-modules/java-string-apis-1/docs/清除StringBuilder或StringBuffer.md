## 1. 概述

在本教程中，我们将介绍几种清除[StringBuilder或StringBuffer](https://www.baeldung.com/java-string-builder-string-buffer)的方法，然后对它们进行详细说明。

## 2.清除一个StringBuilder

### 2.1. 使用setLength方法

setLength方法更新StringBuilder的内部长度。在操作StringBuilder时，长度之后的所有条目都会被忽略。因此，用 0 调用它会清除其内容：

```java
@Test
void whenSetLengthToZero_ThenStringBuilderIsCleared() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Hello World");
    int initialCapacity = stringBuilder.capacity();
    stringBuilder.setLength(0);
    assertEquals("", stringBuilder.toString());
    assertEquals(initialCapacity, stringBuilder.capacity();
}
```

请注意，调用setLength方法后， StringBuilder的容量保持不变。

### 2.2. 使用删除方法

delete方法在后台使用[System.arraycopy](https://www.baeldung.com/java-array-copy)。开始索引之前或结束索引之后的所有索引都被到同一个StringBuilder。

因此，如果我们以起始索引为 0 且结束索引等于StringBuilder的长度来调用delete，我们将：

-   0之前的索引：没有。
-   stringBuilder.length()之后的索引：没有。

结果，StringBuilder的所有内容都被删除：

```java
@Test
void whenDeleteAll_ThenStringBuilderIsCleared() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Hello World");
    int initialCapacity = stringBuilder.capacity();
    stringBuilder.delete(0, stringBuilder.length());
    assertEquals("", stringBuilder.toString());
    assertEquals(initialCapacity, stringBuilder.capacity();
}
```

与setLength方法一样，对象容量在删除其内容后保持不变。我们还要强调在此过程中没有涉及新对象的创建。

## 3.清除一个StringBuffer

适用于StringBuilder的所有方法都以与StringBuffer相同的方式工作。此外，所有关于对象容量的评论仍然有效。
让我们展示一个使用setLength方法的例子：

```java
@Test
void whenSetLengthToZero_ThenStringBufferIsCleared() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Hello World");
    int initialCapacity = stringBuffer.capacity();
    stringBuffer.setLength(0);
    assertEquals("", stringBuffer.toString());
    assertEquals(initialCapacity, stringBuffer.capacity();
}
```

也可以使用delete方法：

```java
@Test
void whenDeleteAll_ThenStringBufferIsCleared() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Hello World");
    int initialCapacity = stringBuffer.capacity();
    stringBuffer.delete(0, stringBuffer.length());
    assertEquals("", stringBuffer.toString());
    assertEquals(initialCapacity, stringBuffer.capacity();
}
```

## 4.性能

[让我们用JMH](https://www.baeldung.com/java-microbenchmark-harness)做一个快速的性能比较。让我们比较一下 StringBuilder的三种方法：

```java
@State(Scope.Benchmark)
public static class MyState {
    final String HELLO = "Hello World";
    final StringBuilder sb = new StringBuilder().append(HELLO);
}

@Benchmark
public void evaluateSetLength(Blackhole blackhole, MyState state) {
    state.sb.setLength(0);
    blackhole.consume(state.sb.toString());
}

@Benchmark
public void evaluateDelete(Blackhole blackhole, MyState state) {
    state.sb.delete(0, state.sb.length());
    blackhole.consume(state.sb.toString());
}
```

我们以秒为单位测量了操作次数。该基准测试导致以下结果：

```markdown
Benchmark                  Mode   Cnt         Score          Error  Units
evaluateDelete             thrpt   25  67943684.417 ± 18116791.770  ops/s
evaluateSetLength          thrpt   25  37310891.158 ±   994382.978  ops/s
```

正如我们所看到的，删除似乎是两者中耗时较少的方法，几乎减少了 2 倍。

## 5.总结

在本文中，我们详细介绍了三种清除StringBuilder或StringBuffer的方法。