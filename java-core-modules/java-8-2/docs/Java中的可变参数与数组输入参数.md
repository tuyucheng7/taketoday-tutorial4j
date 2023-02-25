## 一、概述

在本教程中，我们将探讨Java 中*method(String…args)*和*method(String[] args)*之间的区别。在此过程中，我们将检查如何将[数组](https://www.baeldung.com/java-arrays-guide)或[可变长度参数列表](https://www.baeldung.com/java-varargs)传递给方法。

## 2. 将数组传递给方法

在本节中，我们将展示如何将*String*类型的数组声明为方法的参数，以及如何在方法调用期间将相同类型的数组作为参数传递。

Java 是一种静态类型的编程语言，这意味着变量类型在编译时是已知的。程序员必须声明变量类型，原始类型或引用类型。当定义一个带有数组参数的方法时，**我们期望**在方法调用期间声明我们希望作为参数传递的数组类型。

让我们看看在方法头中定义*String*数组参数的语法：

```java
void capitalizeNames(String[] args)复制
```

让我们分解上面方法头中声明的参数：

-   *String[]* – 类型名称
-   *args——*参数名称

```java
void capitalizeNames(String[] args) {
    for(int i = 0; i < args.length; i++){
       args[i] = args[i].toUpperCase();
    }
}复制
```

从上面可以看出，*capitalizeNames()*方法有一个*String*数组参数*args。*方法头指定该方法在调用时仅接收一个*java.lang.String[]*类型的数组引用。

本质上，当我们在方法头中遇到*(String[] args)*时，我们应该理解该方法在调用时采用单个*String*类型的数组作为参数。

让我们看一个例子：

```java
@Test
void whenCheckingArgumentClassName_thenNameShouldBeStringArray() {
    String[] names = {"john", "ade", "kofi", "imo"};
    assertNotNull(names);
    assertEquals("java.lang.String[]", names.getClass().getTypeName());
    capitalizeNames(names);
}复制
```

*当我们检查capitalizeNames()*方法参数的类名*names*时，我们得到*java.lang.String[]*，它与方法定义中的参数匹配。如果**我们尝试将不同的类型作为参数传递给该方法，则会出现编译错误**：

```java
@Test
void whenCheckingArgumentClassName_thenNameShouldBeStringArray() {
    ...
    int[] evenNumbers = {2, 4, 6, 8};
    capitalizeNames(evenNumbers);
}复制
```

上面的代码片段将在我们的控制台上输出编译器错误消息：

```markdown
incompatible types: int[] cannot be converted to java.lang.String[]复制
```

## 3.可变长度参数列表

可变长度参数列表，在 Java 中也称为*可变参数*，允许我们在方法调用期间传递任意数量的相同类型的参数。

方法中可变长度参数列表的语法如下所示：

```java
String[] firstLetterOfWords(String... args)复制
```

让我们分解上面方法头中声明的参数：

-   *String ...* – 带省略号的类型名称
-   *args——*参数名称

```java
String[] firstLetterOfWords(String... args) {
    String[] firstLetter = new String[args.length];
    for(int i = 0; i < args.length; i++){
        firstLetter[i] = String.valueOf(args[i].charAt(0));
    }
    return firstLetter;
}复制
```

我们在方法签名中声明参数类型后跟省略号 (...) 和参数名称。

使用可变长度参数列表，**我们可以将任意数量的相同类型的参数添加到一个方法中，因为 Java 将给定的参数作为数组中的元素来处理**。添加*可变参数*作为方法参数的一部分时，请确保类型、省略号和参数名称位于最后。

例如，这是不正确的：

```java
static String[] someMethod(String... args, int number)复制
```

我们可以通过交换参数的顺序轻松解决这个问题，将 *可变* 参数放在最后：

```java
static String[] someMethod(int number, String... args)复制
```

让我们测试一下我们上面写的*firstLetterOfWords*方法：

```java
@Test
void whenCheckingReturnedObjectClass_thenClassShouldBeStringArray() {
    assertEquals(String[].class, firstLetterOfWords("football", "basketball", "volleyball").getClass());
    assertEquals(3, firstLetterOfWords("football", "basketball", "volleyball").length);
}复制
```

我们知道 *firstLetterOfWords()方法采用**String*类型的可变长度参数列表，因为省略号，我们将其作为参数传递。测试表明，当我们访问其*getClass()*属性时，该方法返回一个数组。当我们访问数组的 length 属性时，我们也会得到 3，这与传递给它的参数数量相匹配。

## 4. *(String[] args)*与*(String ... args)*

*String[] args*表示一个*String*类型的数组作为Java中的方法参数。**它经常作为 Java 类中 main 方法的数组参数出现**。main 方法中的*String[]* *args*参数从命令行参数形成一个*String数组。****使用(String[] args)\*****调用方法时，必须传入一个\*String\***数组作为参数。

在定义一个方法时，**我们只能有一个变长参数列表。***Varargs*不仅限于*java.lang.String*类型。我们可以有其他类型，如*(int...args)*、*(double...args)*等。在幕后，Java 获取调用带有*可变参数的*方法时传递的所有参数，并从中生成一个数组。*但是，我们可以在没有参数的情况下调用带有可变参数*参数的方法，在这种情况下它将被视为空数组。

**请记住，将\*args\*作为变量名只是一种约定**——可以使用任何其他合适的名称。

## 5.结论

*在本教程中，我们检查了method(String[] args)*和*method(String... args)*之间的区别。*第一个是带有String*数组参数的方法，而后者是一个带有可变长度参数列表 ( *varargs* ) 的方法。

*可变参数*始终作为方法参数列表中的最后一个参数放置，因此一个方法只能声明一个*可变参数*参数。