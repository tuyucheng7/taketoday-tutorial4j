## 1. 概述

在本快速教程中，我们将研究在忽略大小写的情况下确定两个字符串值是否相同。

## 2. 使用equalsIgnoreCase()

equalsIgnoreCase() 接受另一个字符串并返回一个布尔值：

```java
String lower = "equals ignore case";
String UPPER = "EQUALS IGNORE CASE";

assertThat(lower.equalsIgnoreCase(UPPER)).isTrue();

```

## 3. 使用 Apache Commons Lang

[Apache Commons Lang](https://www.baeldung.com/string-processing-commons-lang)库包含一个名为[StringUtils](https://www.baeldung.com/string-processing-commons-lang)的类，它提供与上述方法类似的方法，但它具有处理空值的额外好处：

```java
String lower = "equals ignore case"; 
String UPPER = "EQUALS IGNORE CASE"; 

assertThat(StringUtils.equalsIgnoreCase(lower, UPPER)).isTrue();
assertThat(StringUtils.equalsIgnoreCase(lower, null)).isFalse();
```

## 4。总结

在本文中，我们快速浏览了如何在忽略大小写时确定两个字符串值是否相同。现在，当我们国际化时，事情变得有点棘手，因为区分大小写是特定于一种语言的——请继续关注更多信息。