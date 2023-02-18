## 一、概述

十六进制（hex）和RGB（红-绿-蓝）是图形和设计中常见的颜色代码。有时，将十六进制转换为等效的 RGB 可能是必不可少的，因为它广泛用于许多数字应用程序中。

在本教程中，我们将学习如何在 Java 中将十六进制颜色代码转换为等效的 RGB 值。

## 2.十六进制颜色代码和RGB

十六进制颜色代码由六个字符串字符组成。**每个字符代表十六进制表示法中*****0 – 15\*****之间的一个值（ \*0-9\*和\*AF\*）**。

例如，Deep Saffron 的十六进制颜色代码是*FF9933*。

RGB 是红色、绿色和蓝色的组合。***它每个使用 8 位，并且具有介于0\*****和*****255\*****之间的整数值**。

Deep Saffron 的 RGB 值为“ *255, 153, 51* ”。

要将十六进制颜色代码转换为其等效的 RGB，**重要的是要知道每一对十六进制颜色代码表示红色、绿色和蓝色部分之一**。前两个字符代表红色部分，后两个字符代表绿色部分，最后两个字符代表蓝色部分。

此外，十六进制颜色代码以 16 为底，而 RGB 颜色值以 10 为底。必须将十六进制颜色代码转换为十进制才能最终表示 RGB 颜色。

## 3. 十六进制转RGB

首先，让我们将十六进制颜色代码转换为其等效的 RGB：

```java
@Test
void givenHexCode_whenConvertedToRgb_thenCorrectRgbValuesAreReturned() {
    String hexCode = "FF9933";
    int red = 255;
    int green = 153;
    int blue = 51;
    
    int resultRed = Integer.valueOf(hexCode.substring(0, 2), 16);
    int resultGreen = Integer.valueOf(hexCode.substring(2, 4), 16);
    int resultBlue = Integer.valueOf(hexCode.substring(4, 6), 16);
        
    assertEquals(red, resultRed);
    assertEquals(green, resultGreen);
    assertEquals(blue, resultBlue);
}复制
```

*在这个例子中，我们声明了一个String*类型的变量*hexCode* 。该变量包含一个十六进制颜色代码。我们将十六进制代码拆分为红色、绿色和蓝色分量。

此外，我们通过获取十六进制颜色代码的子字符串来提取组件。要将红色、绿色和蓝色的十六进制值转换为其十进制值，我们调用 Integer.valueOf [*()*方法](https://www.baeldung.com/java-integer-parseint-vs-valueof#the-valueof-method)。*Integer.valueOf()* 允许我们解析指定基数中数字的*字符串 表示形式。*示例中指定的基数为 16。

最后，我们确认提取的 RGB 等效值是我们预期的结果。

## 4。结论

在本文中，我们学习了一种将十六进制颜色代码转换为等效 RGB 颜色代码的简单方法。我们使用*Integer.valueOf()*将十六进制值转换为其等效的十进制值。