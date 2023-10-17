## 1. 概述

在本教程中，我们将了解如何使用按位运算符实现低级位掩码。我们将看到如何将单个int变量视为单独数据的容器，类似于[BitSet](https://www.baeldung.com/java-bitset)。

## 2. 不要隐藏

位掩码允许我们在一个数值变量中存储多个值。我们没有将这个变量视为一个整数，而是将它的每一位都视为一个单独的值。

因为一个位可以等于0或1，所以我们也可以将其视为false或true。我们还可以对一组位进行切片，并将它们视为一个较小的数字变量，甚至是一个String。

### 2.1 例子

假设我们有最小的内存占用并且需要将有关用户帐户的所有信息存储在一个int变量中。前八位(从32个可用位开始)将存储布尔信息，例如“帐户是否有效？”或者“账户溢价是多少？”

至于剩下的24位，我们将把它们转换成三个字符，作为用户的标识符。

### 2.2 编码

我们的用户将有一个标识符“AAA”，他将有一个活跃的高级帐户(存储在前两位)。在二进制表示中，它看起来像：

```java
String stringRepresentation = "01000001010000010100000100000011";
```

这可以很容易地使用内置的Integer#parseUnsignedInt方法编码成一个int变量：

```java
int intRepresentation = Integer.parseUnsignedInt(stringRepresentation, 2);
assertEquals(intRepresentation, 1094795523);
```

### 2.3 解码

这个过程也可以使用Integer#toBinaryString方法逆转：

```java
String binaryString = Integer.toBinaryString(intRepresentation);
String stringRepresentation = padWithZeros(binaryString);
assertEquals(stringRepresentation, "01000001010000010100000100000011");
```

## 3. 提取一位

### 3.1 第一位

如果我们想检查帐户变量的第一位，我们只需要按位“and”运算符和数字“one”作为位掩码。因为二进制形式的数字“one”只有第一位设置为1，其余为零，它将擦除我们变量中的所有位，只保留第一个完整的位：

```java
10000010100000101000001000000011
00000000000000000000000000000001
-------------------------------- &
00000000000000000000000000000001
```

然后我们需要检查产生的值是否不为零：

```java
intRepresentation & 1 != 0
```

### 3.2 任意位置位

如果我们想检查其他位，我们需要创建一个适当的掩码，需要将给定位置的位设置为1，其余设置为零。最简单的方法是移动我们已有的掩码：

```java
1 << (position - 1)
```

上面将位置变量设置为3的代码行会将我们的掩码从：

`00000000000000000000000000000001`
至：

```java
00000000000000000000000000000100
```

所以现在，按位方程将如下所示：

```java
10000010100000101000001000000011
00000000000000000000000000000100
-------------------------------- &
00000000000000000000000000000000
```

将所有这些放在一起，我们可以编写一个在给定位置提取单个位的方法：

```java
private boolean extractValueAtPosition(int intRepresentation, int position) {
    return ((intRepresentation) & (1 << (position - 1))) != 0;
}
```

为了同样的效果，我们也可以在相反的方向移动intRepresentation变量而不是改变掩码。

## 4. 提取多个位

我们可以使用类似的方法从整数中提取多个位。让我们提取用户帐户变量的最后三个字节并将它们转换为字符串。首先，我们需要通过将变量右移来去掉前八位：

```java
int lastThreeBites = intRepresentation >> 8;
String stringRepresentation = getStringRepresentation(lastThreeBites);
assertEquals(stringRepresentation, "00000000010000010100000101000001");
```

我们仍然有32位，因为int总是有32位。然而，现在我们只关心前24位，其余的都是零，很容易忽略。我们创建的int变量可以很容易地用作整数ID，但是因为我们想要一个字符串ID，所以我们还需要执行一个步骤。

我们将二进制文件的字符串表示形式分成八个字符一组，将它们解析为char变量，然后将它们连接成一个最终的String。

为方便起见，我们还将忽略空字节：

```java
Arrays.stream(stringRepresentation.split("(?<=G.{8})"))
  .filter(eightBits -> !eightBits.equals("00000000"))
  .map(eightBits -> (char)Integer.parseInt(eightBits, 2))
  .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
  .toString();
```

## 5. 应用位掩码

除了提取和检查单个位的值，我们还可以创建一个掩码来同时检查其中的许多值。我们想检查我们的用户是否有一个活跃的高级帐户，所以他的变量的前两位都设置为1。

我们可以使用以前的方法分别检查它们，但创建一个同时选择它们的掩码会更快：

```java
int user = Integer.parseUnsignedInt("00000000010000010100000101000001", 2);
int mask = Integer.parseUnsignedInt("00000000000000000000000000000011", 2);
int masked = user & mask;
```

因为我们的用户有一个活跃账户，但不是高级账户，掩码值只会将第一位设置为1：

```java
assertEquals(getStringRepresentation(masked), "00000000000000000000000000000001");
```

现在，我们可以轻松且廉价地断言用户是否满足我们的条件：

```java
assertFalse((user & mask) == mask);
```

## 6. 总结

在本教程中，我们学习了如何使用位运算符创建位掩码并应用它们从整数中提取二进制信息。