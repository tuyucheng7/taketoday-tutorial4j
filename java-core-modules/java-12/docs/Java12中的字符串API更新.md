## 1. 简介

Java 12向[String](https://www.baeldung.com/java-string)类添加了几个有用的API，在本教程中，我们将通过示例来探索这些新的API。

## 2. indent()

indent()方法根据传递给它的参数调整字符串每一行的缩进。

当在字符串上调用indent()时，会执行以下操作：

1.  该字符串在概念上使用lines()分成几行，[lines()是Java 11中引入的String API](https://www.baeldung.com/java-11-string-api)。

2.  然后根据传递给它的int参数n调整每一行，然后以换行符“\n”作为后缀。

    +   如果n > 0，则在每行的开头插入n个空格。

    +   如果n < 0，则从每行的开头最多删除n个空格字符，如果给定行不包含足够的空格，则删除所有前导空格字符。

    +   如果n == 0，则该行保持不变，但是，行终止符仍然是标准化的。

3.  然后拼接并返回生成的行。

例如：

```java
@Test
public void whenPositiveArgument_thenReturnIndentedString() {
    String multilineStr = "This is\na multiline\nstring.";
    String outputStr = "   This is\n   a multiline\n   string.\n";

    String postIndent = multilineStr.indent(3);

    assertThat(postIndent, equalTo(outputStr));
}
```

我们还可以传递一个负整数来减少字符串的缩进，例如：

```java
@Test
public void whenNegativeArgument_thenReturnReducedIndentedString() {
    String multilineStr = "   This is\n   a multiline\n   string.";
    String outputStr = " This is\n a multiline\n string.\n";

    String postIndent = multilineStr.indent(-2);

    assertThat(postIndent, equalTo(outputStr));
}
```

## 3. transform()

我们可以使用transform()方法将函数应用于此字符串，该函数应该期望单个String参数并产生一个结果：

```java
@Test
void whenTransformUsingLambda_thenReturnTransformedString() {
	String result = "hello".transform(input -> input + " world!");
    
	assertThat(result, equalTo("hello world!"));
}
```

输出不必是字符串。例如：

```java
@Test
void whenTransformUsingParseInt_thenReturnInt() {
	int result = "42".transform(Integer::parseInt);
    
	assertThat(result, equalTo(42));
}
```

## 4. 总结

在本文中，我们探讨了Java 12中的新添加的String API。