## 1. 概述

将String转换为int或Integer是Java中非常常见的操作。在本文中，我们将展示处理此问题的多种方法。

有几种简单的方法可以处理这种基本转换。

## 2. Integer.parseInt()

主要解决方案之一是使用Integer的专用静态方法：parseInt()，它返回一个原始int值：

```java
@Test
public void givenString_whenParsingInt_shouldConvertToInt() {
    String givenString = "42";

    int result = Integer.parseInt(givenString);

    assertThat(result).isEqualTo(42);
}
```

默认情况下， parseInt() 方法假定给定的 String 是一个以 10 为底的整数。此外，此方法接受另一个参数来更改此默认 基数。 例如，我们可以 按如下方式解析二进制String ：

```java
@Test
public void givenBinaryString_whenParsingInt_shouldConvertToInt() {
    String givenString = "101010";

    int result = Integer.parseInt(givenString, 2);

    assertThat(result).isEqualTo(42);
}
```

当然，也可以将此方法用于任何其他基数，例如 16(十六进制)或 8(八进制)。

## 3.整数.valueOf()

另一种选择是使用静态Integer.valueOf()方法，它返回一个Integer实例：

```java
@Test
public void givenString_whenCallingIntegerValueOf_shouldConvertToInt() {
    String givenString = "42";

    Integer result = Integer.valueOf(givenString);

    assertThat(result).isEqualTo(new Integer(42));
}
```

同样， valueOf() 方法也接受自定义 基数 作为第二个参数：

```java
@Test
public void givenBinaryString_whenCallingIntegerValueOf_shouldConvertToInt() {
    String givenString = "101010";

    Integer result = Integer.valueOf(givenString, 2);

    assertThat(result).isEqualTo(new Integer(42));
}
```

### 3.1. 整数缓存

乍一看， valueOf() 和 parseInt()方法似乎完全相同。在大多数情况下，这是正确的——甚至valueOf() 方法在内部委托给 parseInt 方法。

但是，这两种方法之间存在一个细微差别：valueOf()方法在 内部使用整数缓存。此缓存将为 -128 和 127 之间的数字返回相同的 Integer实例：

```java
@Test
public void givenString_whenCallingValueOf_shouldCacheSomeValues() {
    for (int i = -128; i <= 127; i++) {
        String value = i + "";
        Integer first = Integer.valueOf(value);
        Integer second = Integer.valueOf(value);

        assertThat(first).isSameAs(second);
    }
}
```

因此，强烈建议使用 valueOf() 而不是 parseInt() 来提取盒装整数，因为它可能会为我们的应用程序带来更好的整体占用空间。

## 4. Integer的构造函数

你还可以使用Integer的构造函数：

```java
@Test
public void givenString_whenCallingIntegerConstructor_shouldConvertToInt() {
    String givenString = "42";

    Integer result = new Integer(givenString);

    assertThat(result).isEqualTo(new Integer(42));
}
```

从Java9 开始，此构造函数已被[弃用](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#(java.lang.String))，取而代之的是其他静态工厂方法，例如 valueOf() 或 parseInt()。即使在此弃用之前，使用此构造函数也很少是合适的。我们应该使用parseInt() 将字符串转换为int 原语或使用valueOf() 将其转换为Integer 对象。

## 5.整数解码()

此外，Integer.decode()的工作方式与 Integer.valueOf() 类似，但也可以接受不同的[数字表示形式](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#decode(java.lang.String))：

```java
@Test
public void givenString_whenCallingIntegerDecode_shouldConvertToInt() {
    String givenString = "42";

    int result = Integer.decode(givenString);

    assertThat(result).isEqualTo(42);
}
```

## 6.数字格式异常

当遇到意外的String值时，上述所有方法都会抛出NumberFormatException 。在这里你可以看到这种情况的示例：

```java
@Test(expected = NumberFormatException.class)
public void givenInvalidInput_whenParsingInt_shouldThrow() {
    String givenString = "nan";
    Integer.parseInt(givenString);
}
```

## 7.番石榴

当然，我们不需要拘泥于核心Java本身。这就是使用 Guava 的Ints.tryParse()可以实现同样的事情的方式，如果它无法解析输入，它会返回一个空值：

```java
@Test
public void givenString_whenTryParse_shouldConvertToInt() {
    String givenString = "42";

    Integer result = Ints.tryParse(givenString);

    assertThat(result).isEqualTo(42);
}
```

此外， tryParse()方法还接受 类似于 parseInt() 和 valueOf()的第二个基数 参数。

## 八、总结

在本文中，我们探讨了将String实例转换为int或Integer实例的多种方法。