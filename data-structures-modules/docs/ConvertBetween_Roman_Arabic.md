## 1. 概述

古罗马人发明了他们自己的数字系统，称为罗马数字。使用具有不同值的字母来表示数字。罗马数字今天仍在一些小应用中使用。

在本文中，我们将实现简单的转换器，将数字从一个系统转换到另一个系统。

## 2. 罗马数字

在罗马数字体系中，我们有7个代表数字的符号：

+ I代表1
+ V代表5
+ X代表10
+ L代表50
+ C代表100
+ D代表500
+ M代表1000

最初，人们习惯用IIII表示4或用XXXX表示40。这读起来会很不舒服。很容易将四个相邻的符号误认为是三个符号。

罗马数字使用减法符号来避免这种错误。与其说是四乘一(IIII)，不如说是五减一(IV)。

这一点很重要，因为我们可能需要检查下一个符号，以确定数字是应该加还是减，而不是简单地逐符号添加数字。

## 3. 模型

让我们定义一个枚举来表示罗马数字：

```java
enum RomanNumeral {
  I(1), IV(4), V(5), IX(9), X(10),
  XL(40), L(50), XC(90), C(100),
  CD(400), D(500), CM(900), M(1000);

  private final int value;

  RomanNumeral(int value) {
    this.value = value;
  }

  public static List<RomanNumeral> getReverseSortedValues() {
    return Arrays.stream(values())
        .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
        .collect(Collectors.toList());
  }

  public int getValue() {
    return value;
  }
}
```

请注意，我们已经定义了额外的符号来帮助使用减法符号。我们还定义了一个名为getReverseSortedValues()的附加方法。

这个方法将允许我们显式地按降序检索定义的罗马数字。

## 4. 罗马数字转换到阿拉伯数字

罗马数字只能表示1~4000之间的整数。我们可以使用以下算法将罗马数字转换为阿拉伯数字(从M到I以相反的顺序遍历符号)：

```
LET numeral be the input String representing an Roman Numeral
LET symbol be initialy set to RomanNumeral.values()[0]
WHILE numeral.length > 0:
    IF numeral starts with symbol's name:
        add symbol's value to the result
        remove the symbol's name from the numeral's beginning
    ELSE:
        set symbol to the next symbol
```

### 4.1 实现

接下来，我们可以用Java实现该算法：

```
public static int romanToArabic(String input) {
  String romanNumeral = input.toUpperCase();
  int result = 0;
  List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();
  int i = 0;
  while (romanNumeral.length() > 0 && i < romanNumerals.size()) {
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

### 4.2 测试

最后，我们可以测试该算法实现：

```
@Test
void given2022Roman_WhenConvertingToArabic_ThenReturn2018() {
  String roman2022 = "MMXXII";
  int result = RomanArabicConverter.romanToArabic(roman2022);
  assertThat(result).isEqualTo(2022);
}
```

## 5. 阿拉伯数字转换到罗马数字

我们可以使用以下算法将阿拉伯数字转换为罗马数字(从M到I以相反的顺序遍历符号)：

```
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

### 5.1 接下来，我们可以实现该算法：

```
public static String arabicToRoman(int number) {
  if (number <= 0 || number > 4000) {
    throw new IllegalArgumentException(number + " is not in range (0,4000]");
  }
  List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();
  int i = 0;
  StringBuilder sb = new StringBuilder();
  while (number > 0 && i < romanNumerals.size()) {
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

### 5.2 测试

最后，我们可以测试该算法实现：

```
@Test
void given2023Arabic_WhenConvertingToRoman_ThenReturnMMXXIII() {
  int arabic2023 = 2023;
  String result = RomanArabicConverter.arabicToRoman(arabic2023);
  assertThat(result).isEqualTo("MMXXIII");
}
```