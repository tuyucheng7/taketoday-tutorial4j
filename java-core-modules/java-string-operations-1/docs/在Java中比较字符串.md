## 1. 概述

在本文中，我们将讨论在 Java中比较字符串的不同方法。

由于String是Java中使用最多的数据类型之一，这自然是一个非常常用的操作。

## 2.字符串与字符串类的比较

### 2.1. 使用“==”比较运算符

使用“==”运算符比较文本值是Java初学者最常犯的错误之一。这是不正确的，因为“==”只检查两个字符串的引用相等性，这意味着它们是否引用同一个对象。

让我们看一下此行为的示例：

```java
String string1 = "using comparison operator";
String string2 = "using comparison operator";
String string3 = new String("using comparison operator");
 
assertThat(string1 == string2).isTrue();
assertThat(string1 == string3).isFalse();
```

在上面的示例中，第一个断言为真，因为这两个变量指向相同的字符串文字。

另一方面，第二个断言是假的，因为string1是用文字创建的，而string3是使用new运算符创建的——因此它们引用不同的对象。

### 2.2. 使用等号()

String类覆盖了从Object继承的equals() 。此方法逐个字符地比较两个字符串，忽略它们的地址。

如果它们的长度相同且字符顺序相同，则认为它们相等：

```java
String string1 = "using equals method";
String string2 = "using equals method";
        
String string3 = "using EQUALS method";
String string4 = new String("using equals method");

assertThat(string1.equals(string2)).isTrue();
assertThat(string1.equals(string4)).isTrue();

assertThat(string1.equals(null)).isFalse();
assertThat(string1.equals(string3)).isFalse();
```

在此示例中，string1、string2和string4变量相等，因为它们具有相同的大小写和值，而不管它们的地址如何。

对于string3，该方法返回false，因为它区分大小写。

此外，如果两个字符串中的任何一个为null，则该方法返回false。

### 2.3. 使用equalsIgnoreCase()

equalsIgnoreCase ()方法返回一个布尔值。顾名思义，此方法在比较字符串时忽略字符中的大小写：

```java
String string1 = "using equals ignore case";
String string2 = "USING EQUALS IGNORE CASE";

assertThat(string1.equalsIgnoreCase(string2)).isTrue();
```

### 2.4. 使用compareTo()

compareTo()方法返回一个int类型的值，并根据字典或自然顺序按字典顺序逐个字符地比较两个String 。

如果两个字符串相等，则此方法返回 0 ；如果第一个字符串在参数之前，则返回负数；如果第一个字符串在参数字符串之后，则返回大于零的数字。

让我们看一个例子：

```java
String author = "author";
String book = "book";
String duplicateBook = "book";

assertThat(author.compareTo(book))
  .isEqualTo(-1);
assertThat(book.compareTo(author))
  .isEqualTo(1);
assertThat(duplicateBook.compareTo(book))
  .isEqualTo(0);
```

### 2.5. 使用compareToIgnoreCase()

compareToIgnoreCase()类似于前面的方法，除了它忽略大小写：

```java
String author = "Author";
String book = "book";
String duplicateBook = "BOOK";

assertThat(author.compareToIgnoreCase(book))
  .isEqualTo(-1);
assertThat(book.compareToIgnoreCase(author))
  .isEqualTo(1);
assertThat(duplicateBook.compareToIgnoreCase(book))
  .isEqualTo(0);
```

## 3.字符串与对象类比较

Objects是一个实用类，它包含一个静态equals()方法，在这种情况下很有用——比较两个字符串。

如果两个字符串相等，则该方法返回true ，首先使用它们的地址(即“ ==”)比较它们。因此，如果两个参数都为null，则返回true，如果只有一个参数为null，则返回 false。

否则，它会简单地调用传递参数类型类的equals( )方法——在我们的例子中是String类的equals()方法。此方法区分大小写，因为它在内部调用String类的equals()方法。

让我们测试一下：

```java
String string1 = "using objects equals";
String string2 = "using objects equals";
String string3 = new String("using objects equals");

assertThat(Objects.equals(string1, string2)).isTrue();
assertThat(Objects.equals(string1, string3)).isTrue();

assertThat(Objects.equals(null, null)).isTrue();
assertThat(Objects.equals(null, string1)).isFalse();
```

## 4.与Apache Commons 的字符串比较

Apache Commons 库包含一个名为StringUtils的实用程序类，用于与字符串相关的操作；这也有一些非常有益的字符串比较方法。

### 4.1. 使用equals()和equalsIgnoreCase()

StringUtils类的equals()方法是String类方法equals()的增强版，同样处理空值：

```java
assertThat(StringUtils.equals(null, null))
  .isTrue();
assertThat(StringUtils.equals(null, "equals method"))
  .isFalse();
assertThat(StringUtils.equals("equals method", "equals method"))
  .isTrue();
assertThat(StringUtils.equals("equals method", "EQUALS METHOD"))
  .isFalse();
```

StringUtils的equalsIgnoreCase()方法返回一个布尔值。这与equals() 的工作方式类似，只是它忽略了字符串中字符的大小写：

```java
assertThat(StringUtils.equalsIgnoreCase("equals method", "equals method"))
  .isTrue();
assertThat(StringUtils.equalsIgnoreCase("equals method", "EQUALS METHOD"))
  .isTrue();
```

### 4.2. 使用equalsAny()和equalsAnyIgnoreCase()

equalsAny ()方法的第一个参数是一个字符串，第二个是一个多参数类型的CharSequence。如果任何其他给定字符串与第一个字符串区分大小写匹配，则该方法返回true 。

否则，返回 false：

```java
assertThat(StringUtils.equalsAny(null, null, null))
  .isTrue();
assertThat(StringUtils.equalsAny("equals any", "equals any", "any"))
  .isTrue();
assertThat(StringUtils.equalsAny("equals any", null, "equals any"))
  .isTrue();
assertThat(StringUtils.equalsAny(null, "equals", "any"))
  .isFalse();
assertThat(StringUtils.equalsAny("equals any", "EQUALS ANY", "ANY"))
  .isFalse();
```

equalsAnyIgnoreCase()方法的工作方式与 equalsAny() 方法类似，但也会忽略大小写：

```java
assertThat(StringUtils.equalsAnyIgnoreCase("ignore case", "IGNORE CASE", "any")).isTrue();
```

### 4.3. 使用compare()和compareIgnoreCase()

StringUtils类中的compare()方法是String类的compareTo()方法的null 安全版本，它通过考虑小于非 null值的 null 值来处理null值。两个空值被认为是相等的。

此外，此方法可用于对包含空条目的字符串列表进行排序：

```java
assertThat(StringUtils.compare(null, null))
  .isEqualTo(0);
assertThat(StringUtils.compare(null, "abc"))
  .isEqualTo(-1);
assertThat(StringUtils.compare("abc", "bbc"))
  .isEqualTo(-1);
assertThat(StringUtils.compare("bbc", "abc"))
  .isEqualTo(1);
```

compareIgnoreCase ()方法的行为类似，除了它忽略大小写：

```java
assertThat(StringUtils.compareIgnoreCase("Abc", "bbc"))
  .isEqualTo(-1);
assertThat(StringUtils.compareIgnoreCase("bbc", "ABC"))
  .isEqualTo(1);
assertThat(StringUtils.compareIgnoreCase("abc", "ABC"))
  .isEqualTo(0);
```

这两种方法也可以与nullIsLess选项一起使用。这是第三个布尔参数，它决定空值是否应该被认为更少。

如果nullIsLess 为真，则空值低于另一个字符串；如果 nullIsLess为假，则空值高于另一个字符串。

让我们试试看：

```java
assertThat(StringUtils.compare(null, "abc", true))
  .isEqualTo(-1);
assertThat(StringUtils.compare(null, "abc", false))
  .isEqualTo(1);
```

具有第三个布尔参数的compareIgnoreCase()方法的工作方式类似，只是忽略大小写。

## 5.总结

在本快速教程中，我们讨论了比较字符串的不同方法。