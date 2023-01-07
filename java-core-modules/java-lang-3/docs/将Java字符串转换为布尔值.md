## 1. 概述

在本教程中，我们将探讨使用Java的Boolean类 将String转换为boolean的不同方法。

## 2.布尔.parseBoolean()

Boolean.parseBoolean()允许我们传入一个String并接收一个原始的boolean。

首先，让我们编写一个测试，看看parseBoolean()如何转换值为true的字符串：

```java
assertThat(Boolean.parseBoolean("true")).isTrue();
```

当然，测试通过。

事实上，parseBoolean()的语义非常清晰，IntelliJ IDEA 警告我们传递字符串文字“true”是多余的。

换句话说，此方法非常适合将String转换为boolean。

## 3.布尔值.valueOf()

Boolean.valueOf()还允许我们传入一个String，但此方法返回一个Boolean类实例而不是原始boolean。

我们可以看到这个方法也成功地转换了我们的String：

```java
assertThat(Boolean.valueOf("true")).isTrue();
```

这个方法实际上使用parseBoolean()在后台进行它的String转换，并简单地使用结果返回一个静态定义的Boolean实例。

因此，只有在需要返回的Boolean实例时才应使用此方法。如果只需要原始结果，则坚持直接使用parseBoolean()会更高效。

## 4.布尔.getBoolean()

Boolean.getBoolean()是第三种方法，它接受一个String并返回一个boolean。

在不查看此方法的文档或实现的情况下，人们可能会合理地假设此方法也用于将其String参数转换为布尔值：

```java
assertThat(Boolean.getBoolean("true")).isTrue(); // this test fails!
```

该测试失败的原因是字符串参数应该表示布尔系统属性的名称。 

通过定义系统属性：

```java
System.setProperty("CODING_IS_FUN", "true");
assertThat(Boolean.getBoolean("CODING_IS_FUN")).isTrue();
```

最后，测试通过。检查此方法的实现会发现它也使用parseBoolean()方法进行String转换。

请注意，getBoolean() 实际上是parseBoolean(System.getProperty(“true”)) 的快捷方式，这意味着我们不应该被名称误导。

因此，唯一的方法是Boolean.getBoolean(“true”); 如果存在名为“true”的系统属性并且其值解析为true ，则永远会返回true。

## 4。总结

在这个简短的教程中，我们看到了Boolean.parseBoolean()、Boolean.valueOf()和Boolean.getBoolean()之间的主要区别。

虽然parseBoolean()和valueOf()都将String转换为boolean，但重要的是要记住Boolean.getBoolean()不会。