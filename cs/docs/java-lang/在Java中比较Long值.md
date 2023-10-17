## 1. 概述

在这个简短的教程中，我们将讨论比较两个Long实例的不同方法。我们强调使用引用比较运算符 ( == ) 时出现的问题。

## 2.使用参考比较的问题

Long是基本类型long的[包装类](https://www.baeldung.com/java-wrapper-classes)。由于它们是对象而不是原始值，我们需要使用.equals()而不是引用比较运算符 (==)来比较Long实例的内容。

在某些情况下，我们可能认为 == 没问题，但看起来是骗人的。考虑一下我们可以使用 == 低数字：

```java
Long l1 = 127L;
Long l2 = 127L;

assertThat(l1 == l2).isTrue();
```

但不是更大的数字。如果值超出 -128 到 127 的范围，我们最终会遇到问题，结果会完全不同且出乎意料：

```java
Long l1 = 128L;
Long l2 = 128L;

assertThat(l1 == l2).isFalse();
```

这是因为Java 为[-128 到 127 之间的](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.7)[Long](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.7)[实例维护了一个常量池](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.7)。

但是，这种优化并没有给我们使用 == 的许可。在一般情况下，具有相同原始值的两个[装箱实例](https://www.baeldung.com/java-primitives-vs-objects)不会产生相同的对象引用。

## 3. 使用.equals()

解决方案之一是使用[.equals()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Long.html#equals(java.lang.Object))。这将评估两个对象的内容(而不是引用)：

```java
Long l1 = 128L;
Long l2 = 128L;

assertThat(l1.equals(l2)).isTrue();
```

## 4.对象.equals()

使用equals()的问题是我们需要小心不要在空引用上调用它。

幸运的是，我们可以使用一个null安全的实用方法—— [Objects.equals()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Objects.html#equals(java.lang.Object,java.lang.Object))。

让我们看看它在实践中是如何工作的：

```java
Long l1 = null;
Long l2 = 128L;

assertThatCode(() -> Objects.equals(l1, l2)).doesNotThrowAnyException();
```

如我们所见，如果我们要比较的任何Long为空，我们都不需要理会。

在底层，Objects.equals()首先使用 == 运算符进行比较，如果失败，则使用标准的equals()。

## 5. 拆箱长值

### 5.1. 使用.longValue()方法

接下来，让我们以安全的方式使用“==”比较运算符。类Number有一个方法[.longValue()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Number.html#longValue())可以解包原始long值：

```java
Long l1 = 128L;
Long l2 = 128L;

assertThat(l1.longValue() == l2.longValue()).isTrue();
```

### 5.2. 转换为原始值

拆箱Long的另一种方法是将对象[转换](https://www.baeldung.com/java-type-casting)为原始类型。因此，我们将提取原始值，然后我们可以继续使用比较运算符：

```java
Long l1 = 128L;
Long l2 = 128L;

assertThat((long) l1 == (long) l2).isTrue();
```

请注意，对于.longValue()方法或使用转换，我们应该检查对象是否为null。如果对象为null ，我们可能会遇到NullPointerException。

## 六，总结

在这个简短的教程中，我们探讨了如何比较Long对象的不同选项。我们在比较对对象或内容的引用时分析了差异。