## 1. 概述

在本教程中，我们将展示如何使用JavaStreamTokenizer类将字符流解析为标记。

## 2.StreamTokenizer _

StreamTokenizer类逐字符读取流。它们中的每一个都可以具有零个或多个以下属性：空格、字母、数字、字符串引号或注解字符。

现在，我们需要了解默认配置。我们有以下类型的字符：

-   单词字符：范围如 'a' 到 'z' 和 'A' 到 'Z
-   数字字符：0,1,…,9
-   空白字符：从 0 到 32 的 ASCII 值
-   注解字符：/
-   字符串引号字符：' 和 “

请注意，行尾被视为空格，而不是单独的标记，默认情况下不识别 C/C++ 风格的注解。

这个类拥有一组重要的字段：

-   TT_EOF – 指示流结束的常量
-   TT_EOL——表示行尾的常量
-   TT_NUMBER – 表示数字标记的常量
-   TT_WORD – 表示单词标记的常量

## 3.默认配置

在这里，我们将创建一个示例以了解StreamTokenizer机制。我们将从创建此类的实例开始，然后调用nextToken()方法，直到它返回TT_EOF值：

```java
private static final int QUOTE_CHARACTER = ''';
private static final int DOUBLE_QUOTE_CHARACTER = '"';

public static List<Object> streamTokenizerWithDefaultConfiguration(Reader reader) throws IOException {
    StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
    List<Object> tokens = new ArrayList<Object>();

    int currentToken = streamTokenizer.nextToken();
    while (currentToken != StreamTokenizer.TT_EOF) {

        if (streamTokenizer.ttype == StreamTokenizer.TT_NUMBER) {
            tokens.add(streamTokenizer.nval);
        } else if (streamTokenizer.ttype == StreamTokenizer.TT_WORD
            || streamTokenizer.ttype == QUOTE_CHARACTER
            || streamTokenizer.ttype == DOUBLE_QUOTE_CHARACTER) {
            tokens.add(streamTokenizer.sval);
        } else {
            tokens.add((char) currentToken);
        }

        currentToken = streamTokenizer.nextToken();
    }

    return tokens;
}
```

测试文件仅包含：

```xml
3 quick brown foxes jump over the "lazy" dog!
#test1
//test2
```

现在，如果我们打印出数组的内容，我们会看到：

```plaintext
Number: 3.0
Word: quick
Word: brown
Word: foxes
Word: jump
Word: over
Word: the
Word: lazy
Word: dog
Ordinary char: !
Ordinary char: #
Word: test1
```

为了更好地理解示例，我们需要解释StreamTokenizer.ttype、StreamTokenizer.nval和StreamTokenizer.sval字段。

ttype字段包含刚刚读取的令牌的类型。它可以是TT_EOF、TT_EOL、TT_NUMBER、TT_WORD。但是，对于带引号的字符串标记，其值是引号字符的 ASCII 值。此外，如果令牌是普通字符，如“！” , 没有属性，则ttype将填充该字符的 ASCII 值。

接下来，我们使用sval字段来获取令牌，前提是它是TT_WORD，即单词令牌。但是，如果我们正在处理一个带引号的字符串标记——比如“惰性”——那么这个字段包含字符串的主体。

最后，我们使用nval字段获取令牌，仅当它是数字令牌时，使用TT_NUMBER。

## 4.自定义配置

在这里，我们将更改默认配置并创建另一个示例。

首先，我们将使用wordChars(int low, int hi)方法设置一些额外的单词字符。然后，我们将注解字符 ('/') 设为普通注解字符，并将'#'提升为新的注解字符。

最后，我们将在eolIsSignificant(boolean flag)方法的帮助下将行尾视为标记字符。

我们只需要在streamTokenizer对象上调用这些方法：

```java
public static List<Object> streamTokenizerWithCustomConfiguration(Reader reader) throws IOException {
    StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
    List<Object> tokens = new ArrayList<Object>();

    streamTokenizer.wordChars('!', '-');
    streamTokenizer.ordinaryChar('/');
    streamTokenizer.commentChar('#');
    streamTokenizer.eolIsSignificant(true);

    // same as before

    return tokens;
}
```

在这里我们有一个新的输出：

```plaintext
// same output as earlier
Word: "lazy"
Word: dog!
Ordinary char: 

Ordinary char: 

Ordinary char: /
Ordinary char: /
Word: test2
```

请注意，双引号成为标记的一部分，换行符不再是空白字符，而是一个普通字符，因此是一个单字符标记。

此外，'#' 字符后面的字符现在被跳过，'/' 是一个普通字符。

我们还可以使用quoteChar(int ch)方法更改引号字符，甚至可以通过调用whitespaceChars(int low, int hi)方法更改空白字符。因此，可以进一步定制以不同的组合调用StreamTokenizer的方法。

## 5.总结

在本教程中，我们了解了如何使用StreamTokenizer类将字符流解析为标记。我们已经了解了默认机制并使用默认配置创建了一个示例。

最后，我们更改了默认参数，并且注意到StreamTokenizer类非常灵活。