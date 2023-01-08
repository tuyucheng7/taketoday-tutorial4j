## 1. 概述

在这篇简短的文章中，我们将在十六进制和[ASCII](https://www.baeldung.com/cs/ascii-code)格式之间进行一些简单的转换。

在典型的用例中，十六进制格式可用于以紧凑的形式写下非常大的整数值。例如，AD45 比其十进制等效值 44357 短，并且随着值的增加，长度差异变得更加明显。

## 2. ASCII转十六进制

现在，让我们看看将 ASCII 值转换为十六进制值的选项：

1.  将字符串转换为字符数组
2.  将每个字符转换为一个整数
3.  使用Integer.toHexString()将其转换为十六进制

这是我们如何实现上述步骤的简单示例：

```java
private static String asciiToHex(String asciiStr) {
    char[] chars = asciiStr.toCharArray();
    StringBuilder hex = new StringBuilder();
    for (char ch : chars) {
        hex.append(Integer.toHexString((int) ch));
    }

    return hex.toString();
}
```

## 3.十六进制转ASCII格式

同样，让我们分三步将 Hex 格式转换为 ASCII 格式：

1.  将十六进制值剪切为 2 个字符组
2.  使用Integer.parseInt(hex, 16)将其转换为 16 进制整数并转换为char
3.  在StringBuilder中追加所有字符

让我们看一个示例，我们如何实现上述步骤：

```java
private static String hexToAscii(String hexStr) {
    StringBuilder output = new StringBuilder("");
    
    for (int i = 0; i < hexStr.length(); i += 2) {
        String str = hexStr.substring(i, i + 2);
        output.append((char) Integer.parseInt(str, 16));
    }
    
    return output.toString();
}
```

## 4.测试

最后，使用这些方法，让我们做一个快速测试：

```java
@Test
public static void whenHexToAscii() {
    String asciiString = "www.baeldung.com";
    String hexEquivalent = 
      "7777772e6261656c64756e672e636f6d";

    assertEquals(asciiString, hexToAscii(hexEquivalent));
}

@Test
public static void whenAsciiToHex() {
    String asciiString = "www.baeldung.com";
    String hexEquivalent = 
      "7777772e6261656c64756e672e636f6d";

    assertEquals(hexEquivalent, asciiToHex(asciiString));
}
```

## 5.总结

最后，我们研究了使用Java在 ASCII 和 Hex 之间进行转换的最简单方法。