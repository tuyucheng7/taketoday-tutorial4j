## 一、概述

在本快速教程中，我们将说明如何检查String是否至少包含以下各项之一：大写字母、小写字母、数字或 Java 中的特殊字符。

## 2.使用正则表达式

执行检查的方法之一是使用正则表达式。要熟悉正则表达式，请查看[这篇文章](https://www.baeldung.com/regular-expressions-java)。

首先，让我们为每个所需的字符组定义正则表达式。由于正则表达式是固定的，因此无需在每次运行时对它们求值，因此我们将在与它们进行比较之前先编译它们：

```java
private static final Pattern[] inputRegexes = new Pattern[4];

static {
    inputRegexes[0] = Pattern.compile(".[A-Z].");
    inputRegexes[1] = Pattern.compile(".[a-z].");
    inputRegexes[2] = Pattern.compile(".d.");
    inputRegexes[3] = Pattern.compile(".[`~!@#$%^&()-_=+|[{]};:'",<.>/?].");
}
```

此外，我们应该创建一个简单的方法，我们将使用它来测试我们的String是否 符合条件：

```java
private static boolean isMatchingRegex(String input) {
    boolean inputMatches = true;
    for (Pattern inputRegex : inputRegexes) {
        if (!inputRegex.matcher(input).matches()) {
            inputMatches = false;
        }
    }
    return inputMatches;
}
```

### 2.1. 单个正则表达式

前面的示例非常可读，并且允许我们在必要时轻松地仅使用一些模式。但是，在我们只关心满足所有条件的情况下，使用单个正则表达式会更有效。

这样我们就不需要静态块来初始化和编译我们所有的多个表达式。此外，无需遍历所有这些并找出哪些匹配，哪些不匹配。

我们需要做的就是声明我们的正则表达式：

```java
String regex = "^(?=.?p{Lu})(?=.?p{Ll})(?=.?d)" +
    "(?=.?[`~!@#$%^&()-_=+|[{]};:'",<.>/?]).$";
```

然后编译并比较它：

```java
@Test
public void givenSingleRegex_whenMatchingCorrectString_thenMatches() {
    String validInput = "Ab3;";
    assertTrue(Pattern.compile(regex).matcher(validInput).matches());
}
```

关于我们的正则表达式，我们应该指出一些事情。

首先，我们对每组字符都使用了正向先行 ( ?=X ) 。这意味着我们希望在字符串的开头(标有^ )之后找到X以便匹配，但我们不想去X的结尾，而是希望留在行的开头.

另一件需要注意的事情是，这次我们没有使用[AZ]或[az]作为字母组，而是 使用p{Lu}和p{Ll}。这些将匹配来自任何语言的任何类型的字母(在我们的例子中，分别是大写和小写)，而不仅仅是英语。

## 3. 使用核心Java

如果我们不想使用正则表达式，现在让我们看看如何执行相同的检查。我们将利用Character 和String类及其方法来检查我们的String中是否存在所有必需的字符：

```java
private static boolean checkString(String input) {
    String specialChars = "~`!@#$%^&()-_=+|[{]};:'",<.>/?";
    char currentCharacter;
    boolean numberPresent = false;
    boolean upperCasePresent = false;
    boolean lowerCasePresent = false;
    boolean specialCharacterPresent = false;

    for (int i = 0; i < input.length(); i++) {
        currentCharacter = input.charAt(i);
        if (Character.isDigit(currentCharacter)) {
            numberPresent = true;
        } else if (Character.isUpperCase(currentCharacter)) {
            upperCasePresent = true;
        } else if (Character.isLowerCase(currentCharacter)) {
            lowerCasePresent = true;
        } else if (specialChars.contains(String.valueOf(currentCharacter))) {
            specialCharacterPresent = true;
        }
    }

    return
      numberPresent && upperCasePresent && lowerCasePresent && specialCharacterPresent;
}
```

在这里我们应该注意一些事情。基本思想是我们遍历String并检查其字符是否属于所需类型。通过使用 Character类，我们可以轻松地检查某个字符是数字、大写还是小写字符。

不幸的是，没有类似的方法可以告诉我们是否正在处理其中一个特殊字符。所以，这意味着我们需要采取另一种方法。

我们创建了一个包含我们需要的所有特殊字符的字符串，然后检查它是否包含我们的特定字符。

## 4。总结

在这篇快速文章中，我们展示了如何检查String是否 包含必需的字符。在第一个场景中，我们使用了正则表达式，而在第二个场景中，我们利用了核心 Java 类。