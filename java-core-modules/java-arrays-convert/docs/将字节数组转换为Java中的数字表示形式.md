## 1. 概述

在本教程中，我们将探讨将字节数组转换为数值(int、long、float、double)的不同方法，反之亦然。

字节是计算机存储和处理信息的基本单位。[Java 语言中定义的基本类型](https://www.baeldung.com/java-primitives)是同时操作多个字节的便捷方式。因此，字节数组和原始类型之间存在着一种内在的转换关系。

由于short和char类型只包含两个字节，因此不需要太多关注。因此，我们将重点关注字节数组与int、long、float和double类型之间的转换。

## 2. 使用移位运算符

将字节数组转换为数值的最直接方法是使用[移位运算符](https://www.baeldung.com/java-bitwise-operators#bitwise-shift-operators)。

### 2.1. 字节数组到int和long

将字节数组转换为int值时，我们使用<<(左移)运算符：

```java
int value = 0;
for (byte b : bytes) {
    value = (value << 8) + (b & 0xFF);
}
```

通常，上述代码片段中字节数组的长度应等于或小于四。那是因为一个int值占用四个字节。否则会导致int范围溢出。

为了验证转换的正确性，让我们定义两个常量：

```java
byte[] INT_BYTE_ARRAY = new byte[] {
    (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE
};
int INT_VALUE = 0xCAFEBABE;
```

如果我们仔细观察这两个常量INT_BYTE_ARRAY和INT_VALUE，我们会发现它们是十六进制数0xCAFEBABE的不同表示。

然后，让我们检查一下这个转换是否正确：

```java
int value = convertByteArrayToIntUsingShiftOperator(INT_BYTE_ARRAY);

assertEquals(INT_VALUE, value);
```

类似地，当将一个字节数组转换为一个长值时，我们可以重用上面的代码片段并进行两处修改：值的类型为长型且字节长度应等于或小于八。

### 2.2. int和long到字节数组

将int值转换为字节数组时，我们可以使用>>(有符号右移)或>>>(无符号右移)运算符：

```java
byte[] bytes = new byte[Integer.BYTES];
int length = bytes.length;
for (int i = 0; i < length; i++) {
    bytes[length - i - 1] = (byte) (value & 0xFF);
    value >>= 8;
}
```

在上面的代码片段中，我们可以将>>运算符替换为>>>运算符。那是因为我们只使用了value参数最初包含的字节。因此，带符号扩展或零扩展的右移不会影响最终结果。

然后，我们可以检查上面转换的正确性：

```java
byte[] bytes = convertIntToByteArrayUsingShiftOperator(INT_VALUE);

assertArrayEquals(INT_BYTE_ARRAY, bytes);
```

将long值转换为字节数组时，我们只需要将Integer.BYTES更改为Long.BYTES并确保值的类型为long。

### 2.3. 字节数组浮点数和双精度数

[将字节](https://www.baeldung.com/java-convert-float-to-byte-array)[数组转换为浮点数](https://www.baeldung.com/java-convert-float-to-byte-array)时，我们使用Float.intBitsToFloat()方法：

```java
// convert bytes to int
int intValue = 0;
for (byte b : bytes) {
    intValue = (intValue << 8) + (b & 0xFF);
}

// convert int to float
float value = Float.intBitsToFloat(intValue);
```

从上面的代码片段中，我们可以了解到字节数组不能直接转换为浮点值。基本上，它需要两个独立的步骤：首先，我们将字节数组转换为int值，然后将相同的位模式解释为float值。

为了验证转换的正确性，让我们定义两个常量：

```java
byte[] FLOAT_BYTE_ARRAY = new byte[] {
    (byte) 0x40, (byte) 0x48, (byte) 0xF5, (byte) 0xC3
};
float FLOAT_VALUE = 3.14F;
```

然后，让我们检查一下这个转换是否正确：

```java
float value = convertByteArrayToFloatUsingShiftOperator(FLOAT_BYTE_ARRAY);

assertEquals(Float.floatToIntBits(FLOAT_VALUE), Float.floatToIntBits(value));
```

同样，我们可以利用中间long值和Double.longBitsToDouble()方法将字节数组转换为双精度值。

### 2.4. float和double到字节数组

将浮点数转换为字节数组时，我们可以利用Float.floatToIntBits()方法：

```java
// convert float to int
int intValue = Float.floatToIntBits(value);

// convert int to bytes
byte[] bytes = new byte[Float.BYTES];
int length = bytes.length;
for (int i = 0; i < length; i++) {
    bytes[length - i - 1] = (byte) (intValue & 0xFF);
    intValue >>= 8;
}
```

然后，让我们检查一下这个转换是否正确：

```java
byte[] bytes = convertFloatToByteArrayUsingShiftOperator(FLOAT_VALUE);

assertArrayEquals(FLOAT_BYTE_ARRAY, bytes);
```

以此类推，我们可以利用Double.doubleToLongBits()方法将双精度值 转换为字节数组。


## 3. 使用字节缓冲区

java.nio.ByteBuffer类提供了一种简洁、统一的方式来在字节数组和数值(int、long、float、double)之间进行转换。

### 3.1. 字节数组到数值

现在，我们使用ByteBuffer类将字节数组转换为int值：

```java
ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
buffer.put(bytes);
buffer.rewind();
int value = buffer.getInt();
```

然后，我们使用ByteBuffer类将int值转换为字节数组：

```java
ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
buffer.putInt(value);
buffer.rewind();
byte[] bytes = buffer.array();
```

我们应该注意到上面的两个代码片段遵循相同的模式：

-   首先，我们使用ByteBuffer.allocate(int)方法获取指定容量的ByteBuffer对象。
-   然后，我们将原始值(字节数组或int值)放入ByteBuffer对象，例如buffer.put(bytes)和buffer.putInt(value)方法。
-   之后，我们将ByteBuffer对象的位置重置为零，以便我们可以从头开始读取。
-   最后，我们使用buffer.getInt()和buffer.array()等方法从ByteBuffer对象中获取目标值。

这种模式非常通用，它支持long、float和double类型的转换。我们唯一需要做的修改是与类型相关的信息。

### 3.2. 使用现有的字节数组

此外，ByteBuffer.wrap(byte[])方法允许我们重用现有字节数组而无需创建新数组：

```java
ByteBuffer.wrap(bytes).getFloat();
```

但是，我们还应该注意，上面的bytes变量的长度等于或大于目标类型(Float.BYTES)的大小。否则，它将抛出BufferUnderflowException。

## 4. 使用大整数

[java.math.BigInteger](https://www.baeldung.com/java-biginteger)类的主要目的是表示大数值，否则这些数值将不适合原始数据类型。尽管我们可以使用它在字节数组和原始值之间进行转换，但使用BigInteger对于这种目的来说有点繁重。

### 4.1. 字节数组到int和long

现在，让我们使用BigInteger类将字节数组转换为int值：

```java
int value = new BigInteger(bytes).intValue();
```

类似地，BigInteger类有一个longValue()方法来将字节数组转换为长值：

```java
long value = new BigInteger(bytes).longValue();
```

此外，BigInteger类还有一个intValueExact()方法和一个longValueExact()方法。应谨慎使用这两个方法：如果BigInteger对象分别超出int或long类型的范围，则这两个方法都将抛出ArithmeticException。

将int或long值转换为字节数组时，我们可以使用相同的代码片段：

```java
byte[] bytes = BigInteger.valueOf(value).toByteArray();
```

但是，BigInteger类的toByteArray()方法返回的是最小字节数，不一定是四个或八个字节。

### 4.2. 字节数组浮点数和双精度数

虽然BigInteger类有一个floatValue()方法，但我们不能像预期的那样使用它来将字节数组转换为浮点值。那么，我们应该怎么做呢？我们可以使用int值作为将字节数组转换为float值的中间步骤：

```java
int intValue = new BigInteger(bytes).intValue();
float value = Float.intBitsToFloat(intValue);
```

同样，我们可以将浮点值转换为字节数组：

```java
int intValue = Float.floatToIntBits(value);
byte[] bytes = BigInteger.valueOf(intValue).toByteArray();
```

同样，通过利用Double.longBitsToDouble()和Double.doubleToLongBits()方法，我们可以使用BigInteger类在字节数组和双精度值之间进行转换。

## 5.使用番石榴

[Guava](https://www.baeldung.com/guava-guide)库为我们提供了方便的方法来进行这种转换。

### 5.1. 字节数组到int和long

在 Guava 中，com.google.common.primitives包中的Ints类包含一个fromByteArray()方法。因此，我们很容易将字节数组转换为int值：

```java
int value = Ints.fromByteArray(bytes);
```

Ints类还有一个toByteArray()方法，可用于将int值转换为字节数组：

```java
byte[] bytes = Ints.toByteArray(value);
```

而且，Longs类在使用上类似于Ints类：

```java
long value = Longs.fromByteArray(bytes);
byte[] bytes = Longs.toByteArray(value);
```

此外，如果我们检查fromByteArray()和toByteArray()方法的源代码，我们可以发现这两种方法都使用移位运算符来完成它们的任务。

### 5.2. 字节数组浮点数和双精度数

在同一个包中还存在Floats和Doubles类。但是，这两个类都不支持fromByteArray()和toByteArray()方法。

但是，我们可以利用Float.intBitsToFloat()、Float.floatToIntBits()、Double.longBitsToDouble()和Double.doubleToLongBits()方法来完成字节数组与浮点数或双精度值之间的转换。为简洁起见，我们省略了此处的代码。

## 6. 使用 Commons Lang

当我们使用[Apache Commons Lang 3](https://www.baeldung.com/java-commons-lang-3)时，进行这些类型的转换有点复杂。这是因为Commons Lang 库默认使用小端字节数组。但是，我们上面提到的字节数组都是大端顺序的。因此，我们需要将大端字节数组转换为小端字节数组，反之亦然。

### 6.1. 字节数组到int和long

org.apache.commons.lang3包中的 Conversion 类提供了byteArrayToInt ()和intToByteArray()方法。

现在，让我们将字节数组转换为int值：

```java
byte[] copyBytes = Arrays.copyOf(bytes, bytes.length);
ArrayUtils.reverse(copyBytes);
int value = Conversion.byteArrayToInt(copyBytes, 0, 0, 0, copyBytes.length);
```

在上面的代码中，我们了原始字节变量。这是因为有时候，我们不想改变原始字节数组的内容。

然后，让我们将一个int值转换为一个字节数组：

```java
byte[] bytes = new byte[Integer.BYTES];
Conversion.intToByteArray(value, 0, bytes, 0, bytes.length);
ArrayUtils.reverse(bytes);
```

Conversion类还定义了byteArrayToLong ()和longToByteArray()方法。并且，我们可以使用这两种方法在字节数组和长值之间进行转换。

### 6.2. 字节数组浮点数和双精度数

但是，Conversion类并没有直接提供相应的方法来转换float或double值。

同样，我们需要一个中间int或long值来在字节数组和float或double值之间进行转换。

## 七、总结

在本文中，我们说明了使用普通Java通过移位运算符、 ByteBuffer和BigInteger将字节数组转换为数值的各种方法。然后，我们看到了使用 Guava 和 Apache Commons Lang 进行的相应转换。