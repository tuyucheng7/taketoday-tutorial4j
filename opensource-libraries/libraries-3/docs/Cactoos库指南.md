## 1. 简介

[Cactoos](https://github.com/yegor256/cactoos) 是一个面向对象的Java基本类型库。

在本教程中，我们将了解一些作为该库的一部分可用的类。

## 2.仙人掌

Cactoos 库的库非常丰富，从字符串操作到数据结构。该库提供的原始类型及其对应的方法与其他库(如[Guava](https://www.baeldung.com/category/guava/)和[Apache Commons](https://www.baeldung.com/java-commons-lang-3) )提供的类似，但更侧重于面向对象的[设计原则](https://www.baeldung.com/solid-principles)。

### 2.1. 与 Apache Commons 的比较

Cactoos 库配备了提供与作为 Apache Commons 库一部分的静态方法相同功能的类。

让我们看一下StringUtils包中的一些静态方法及其在 Cactoos 中的等效类：

| StringUtils 的静态方法 | 等效的 Cactoos 类 |
| :------------------------- | :-------------------- |
| 是空白()                 | 空白                  |
| 小写()                     | 降低                  |
| 大写()                     | 上                    |
| 旋转()                   | 旋转的                |
| 交换大小写()               | 调换大小写            |
| 剥离开始()                 | 修剪左                |
| 剥离结束()                 | 右修剪                |

有关这方面的更多信息，请参见[官方文档](https://github.com/yegor256/cactoos#our-objects-vs-their-static-methods)。我们将在后续部分中查看其中一些的实现。

## 3. Maven 依赖

让我们从添加所需的 Maven 依赖项开始。这个库的最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A "org.cactoos" AND a%3A "cactoos")上找到：

```xml
<dependency>
    <groupId>org.cactoos</groupId>
    <artifactId>cactoos</artifactId>
    <version>0.43</version>
</dependency>
```

## 4. 字符串

Cactoos 有很多类来操作String对象。

### 4.1. 字符串对象创建

让我们看看如何使用TextOf 类创建String对象：

```java
String testString = new TextOf("Test String").asString();
```

### 4.2. 格式化字符串

如果需要创建格式化的字符串，我们可以使用FormattedText类：

```java
String formattedString = new FormattedText("Hello %s", stringToFormat).asString();
```

让我们验证此方法实际上返回格式化的String：

```java
StringMethods obj = new StringMethods();

String formattedString = obj.createdFormattedString("John");
assertEquals("Hello John", formattedString);
```

### 4.3. 小写/大写字符串

Lowered类使用其TextOf对象将String转换为小写：

```java
String lowerCaseString = new Lowered(new TextOf(testString)).asString();
```

同样，可以使用Upper类将给定的String转换为大写：

```java
String upperCaseString = new Upper(new TextOf(testString)).asString();
```

让我们使用测试字符串验证这些方法的输出：

```java
StringMethods obj = new StringMethods();

String lowerCaseString = obj.toLowerCase("TeSt StrIng");
String upperCaseString = obj.toUpperCase("TeSt StrIng"); 

assertEquals("test string", lowerCaseString);
assertEquals("TEST STRING", upperCaseString);
```

### 4.4. 检查空字符串

如前所述，Cactoos 库提供了一个IsBlank类来检查null或空String：

```java
new IsBlank(new TextOf(testString)) != null;
```

## 5.收藏品

该库还提供了几个用于处理Collections的类。让我们来看看其中的一些。

### 5.1. 迭代集合

我们可以使用实用程序类And迭代字符串列表：

```java
new And((String input) -> LOGGER.info(new FormattedText("%sn", input).asString()), strings).value();
```

上面的方法是一种迭代Strings列表的功能方法，它将输出写入记录器。

### 5.2. 过滤集合

Filtered类可用于根据特定条件过滤集合：

```java
Collection<String> filteredStrings 
  = new ListOf<>(new Filtered<>(string -> string.length() == 5, new IterableOf<>(strings)));
```

让我们通过传入几个参数来测试此方法，其中只有 3 个参数满足条件：

```java
CollectionUtils obj = new CollectionUtils(); 

List<String> strings = new ArrayList<String>() {
    add("Hello"); 
    add("John");
    add("Smith"); 
    add("Eric"); 
    add("Dizzy"); 
};

int size = obj.getFilteredList(strings).size(); 

assertEquals(3, size);
```

这个库提供的一些其他的Collections类可以在[官方文档](https://github.com/yegor256/cactoos#iterablescollectionslistssets)中找到。

## 六. 总结

在本教程中，我们了解了 Cactoos 库及其为字符串和数据结构操作提供的一些类。

除了这些之外，该库还提供了其他用于[IO 操作](https://github.com/yegor256/cactoos#inputoutput)的实用程序类以及[Date 和 Time](https://github.com/yegor256/cactoos#dates-and-times)。