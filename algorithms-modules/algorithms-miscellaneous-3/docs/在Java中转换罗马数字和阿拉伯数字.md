## 1. 简介

古罗马人开发了自己的数字系统，称为罗马数字。系统使用具有不同值的字母来表示数字。罗马数字今天仍在一些较小的应用程序中使用。

在本教程中，我们将实现简单的转换器，将数字从一个系统转换为另一个系统。

## 2. 罗马数字

在罗马系统中，我们有7 个符号代表数字：

-   我代表1
-   V代表5
-   X代表10
-   L代表50
-   C代表100
-   D代表500
-   M代表1000

最初，人们习惯用 IIII 表示 4 或用 XXXX 表示 40。这可能读起来很不舒服。也很容易将相邻的四个符号误认为是三个符号。

罗马数字使用减法符号来避免此类错误。与其说四乘以一 (IIII)，不如说它 比 五少一(IV)。

从我们的角度来看它有多重要？这很重要，因为我们可能需要检查下一个符号以确定是否应该添加或减去数字，而不是简单地逐个符号地添加数字。

## 3.型号

让我们定义一个枚举来表示罗马数字：

```java
enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10), 
    XL(40), L(50), XC(90), C(100), 
    CD(400), D(500), CM(900), M(1000);

    private int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
          .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
          .collect(Collectors.toList());
    }
}
```

请注意，我们已经定义了额外的符号来帮助减法表示法。我们还定义了一个名为 getReverseSortedValues()的附加方法。

此方法将允许我们以降序值顺序显式检索定义的罗马数字。

## 4.罗马到阿拉伯语

罗马数字只能表示 1 到 4000 之间的整数。我们可以使用以下算法将罗马数字转换为阿拉伯数字(从M到I以相反的顺序遍历符号)：

```plaintext
LET numeral be the input String representing an Roman Numeral
LET symbol be initialy set to RomanNumeral.values()[0]
WHILE numeral.length > 0:
    IF numeral starts with symbol's name:
        add symbol's value to the result
        remove the symbol's name from the numeral's beginning
    ELSE:
        set symbol to the next symbol
```

### 4.1. 执行

接下来，我们可以用Java实现算法：

```java
public static int romanToArabic(String input) {
    String romanNumeral = input.toUpperCase();
    int result = 0;
        
    List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

    int i = 0;

    while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
        RomanNumeral symbol = romanNumerals.get(i);
        if (romanNumeral.startsWith(symbol.name())) {
            result += symbol.getValue();
            romanNumeral = romanNumeral.substring(symbol.name().length());
        } else {
            i++;
        }
    }

    if (romanNumeral.length() > 0) {
        throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
    }

    return result;
}
```

### 4.2. 测试

最后，我们可以测试实现：

```java
@Test
public void given2018Roman_WhenConvertingToArabic_ThenReturn2018() {
    String roman2018 = "MMXVIII";

    int result = RomanArabicConverter.romanToArabic(roman2018);

    assertThat(result).isEqualTo(2018);
}
```

## 5. 阿拉伯语到罗马语

我们可以使用以下算法将阿拉伯数字转换为罗马数字(从M到I以相反的顺序遍历符号)：

```plaintext
LET number be an integer between 1 and 4000
LET symbol be RomanNumeral.values()[0]
LET result be an empty String
WHILE number > 0:
    IF symbol's value <= number:
        append the result with the symbol's name
        subtract symbol's value from number
    ELSE:
        pick the next symbol
```

### 5.1. 执行

接下来，我们现在可以实现算法：

```java
public static String arabicToRoman(int number) {
    if ((number <= 0) || (number > 4000)) {
        throw new IllegalArgumentException(number + " is not in range (0,4000]");
    }

    List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

    int i = 0;
    StringBuilder sb = new StringBuilder();

    while ((number > 0) && (i < romanNumerals.size())) {
        RomanNumeral currentSymbol = romanNumerals.get(i);
        if (currentSymbol.getValue() <= number) {
            sb.append(currentSymbol.name());
            number -= currentSymbol.getValue();
        } else {
            i++;
        }
    }

    return sb.toString();
}
```

### 5.2. 测试

最后，我们可以测试实现：

```java
@Test
public void given1999Arabic_WhenConvertingToRoman_ThenReturnMCMXCIX() {
    int arabic1999 = 1999;

    String result = RomanArabicConverter.arabicToRoman(arabic1999);

    assertThat(result).isEqualTo("MCMXCIX");
}
```

## 六. 总结

在这篇快速文章中，我们展示了如何在罗马数字和阿拉伯数字之间进行转换。

我们使用了一个 枚举来表示一组罗马数字，并且我们创建了一个实用程序类来执行转换。