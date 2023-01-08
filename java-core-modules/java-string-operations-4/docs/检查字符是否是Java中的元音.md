## 1. 概述

在处理 String 中的字符时，我们可能希望根据它们是否属于特定组对它们进行分类。例如，英文字母表中的字符要么是元音字母，要么是辅音字母。

在本教程中，我们将了解几种检查字符是否为元音的方法。我们可以轻松地将这些方法扩展到其他字符组。

## 2. 使用indexOf方法检查元音

因为我们知道所有的元音，我们可以将它们以大写和小写形式添加到 String中：

```java
String VOWELS = "aeiouAEIOU";
```

我们可以使用[String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#indexOf(int))[类中的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#indexOf(int))[indexOf方法](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#indexOf(int)) 来查看字符是否存在：

```java
boolean isInVowelsString(char c) {
    return VOWELS.indexOf(c) != -1;
}
```

如果该字符存在，则索引不会是-1。如果是 -1，则该字符不在元音集中。让我们测试一下：

```java
assertThat(isInVowelsString('e')).isTrue();
assertThat(isInVowelsString('z')).isFalse();
```

在这里，我们 在Java中使用char 。如果我们的角色是单个字符 String对象，我们可以使用不同的实现：

```java
boolean isInVowelsString(String c) {
    return VOWELS.contains(c);
}
```

它将通过相同的测试：

```java
assertThat(isInVowelsString("e")).isTrue();
assertThat(isInVowelsString("z")).isFalse();
```

正如我们所见，此方法的实现开销很小。但是，我们必须遍历元音字符串中的 10 个可能的元音以确定组中是否有某物。

## 3. 使用switch检查元音

相反，我们可以使用switch语句，其中每个元音都是一个单独的case：

```java
boolean isVowelBySwitch(char c) {
    switch (c) {
        case 'a':            
        case 'e':           
        case 'i':           
        case 'o':            
        case 'u':            
        case 'A':
        case 'E':            
        case 'I':           
        case 'O':            
        case 'U':
            return true;
        default:
            return false;
    }
}
```

我们也可以测试一下：

```java
assertThat(isVowelBySwitch('e')).isTrue();
assertThat(isVowelBySwitch('z')).isFalse();
```

由于Java在 switch 语句中支持String，因此我们也可以使用单字符字符串来实现它。

## 4. 使用正则表达式检查元音

虽然我们可以实现自己的字符串匹配算法，但 Java[正则表达式](https://www.baeldung.com/tag/regex/)引擎允许我们强大地匹配字符串。

让我们构建一个正则表达式来识别元音：

```java
Pattern VOWELS_PATTERN = Pattern.compile("[aeiou]", Pattern.CASE_INSENSITIVE);
```

[ ]用于表示字符类。我们只将元音以小写形式放入此类，因为我们可以以不区分大小写的方式匹配它们。

让我们为具有单个字符的String对象实现我们的匹配算法 ：

```java
boolean isVowelByRegex(String c) {
    return VOWELS_PATTERN.matcher(c).matches();
}
```

让我们测试一下：

```java
assertThat(isVowelByRegex("e")).isTrue();
assertThat(isVowelByRegex("E")).isTrue();
```

正如我们所见，正则表达式不区分大小写。

我们应该注意，这要求输入是一个 字符串，而不是一个字符。尽管我们可以借助 Character类的toString方法将字符转换为String：

```java
assertThat(isVowelByRegex(Character.toString('e'))).isTrue();
```

使用正则表达式可以直接处理此问题的一般情况。我们可以使用字符类指定任何字符分组，包括字符范围。

## 5. 我们应该使用哪种解决方案？

基于String的解决方案可能是最容易理解的并且性能非常好，因为它只需要为它分类的每个字符检查最多 10 个选项。

但是，我们通常希望 switch语句比 String查找执行得更快。

正则表达式解决方案应该执行得很好，因为在Pattern的编译方法 期间优化了正则表达式。然而，正则表达式的实现可能更复杂，对于像检测元音这样简单的事情来说可能不值得如此复杂。同样，如果我们使用 char值，那么正则表达式需要一些其他方法不需要的转换。

但是，使用正则表达式可以让我们实现复杂的表达式来对字符进行分类。

## 六，总结

在本文中，我们看到了几种识别字符是否为元音的不同方法。我们看到了如何使用包含所有元音的字符串以及如何实现switch语句。

最后，我们看到了如何使用正则表达式来解决这种情况和更一般的情况。