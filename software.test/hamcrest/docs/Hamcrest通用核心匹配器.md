## 1. 概述

在这个快速教程中，我们介绍[Hamcrest](http://hamcrest.org/)框架中的CoreMatchers类，用于编写简单且更具表现力的测试用例，其思想是让断言语句读起来像自然语言。

## 2. Maven

在Mavan中，我们只需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 通用核心匹配器

### 3.1 is(T) and is(Matcher<T\>)

is(T)将一个对象作为参数来检查相等性，而is(Matcher<T\\>)接收另一个匹配器，使相等性语句更具表现力。

**我们可以将它与几乎所有方法一起使用**：

```java
String testString = "hamcrest core";

assertThat(testString, is("hamcrest core"));
assertThat(testString, is(equalTo("hamcrest core")));
```

### 3.2 equalTo(T)

equalTo(T)将一个对象作为参数并检查它与另一个对象的相等性，**这经常与is(Matcher<T\>)一起使用**：

```java
String actualString = "equalTo match";
List<String> actualList = Lists.newArrayList("equalTo", "match");

assertThat(actualString, is(equalTo("equalTo match")));
assertThat(actualList, is(equalTo(Lists.newArrayList("equalTo", "match"))));
```

我们还可以使用equalToObject(Object operand)：它检查相等性，并且不强制两个对象应该具有相同的静态类型：

```java
Object original = 100;
assertThat(original, equalToObject(100));
```

### 3.3 not(T)和not(Matcher<T\>)

not(T)和not(Matcher<T\>)用于检查给定对象的不相等性，第一个以对象作为参数，第二个以另一个匹配器作为参数：

```java
String testString = "troy kingdom";

assertThat(testString, not("german kingdom"));
assertThat(testString, is(not(equalTo("german kingdom"))));
assertThat(testString, is(not(instanceOf(Integer.class))));
```

### 3.4 nullValue()和nullValue(Class<T\>)

nullValue()针对检查对象检查空值，nullValue(Class<T\>)检查给定Class类型对象的可空性：

```java
Integer nullObject = null;

assertThat(nullObject, is(nullValue()));
assertThat(nullObject, is(nullValue(Integer.class)));
```

### 3.5 notNullValue()和notNullValue(Class<T\>)

**这些是常用的is(not(nullValue))的快捷方法**，用检查对象或Class类型的非空相等性：

```java
Integer testNumber = 123;

assertThat(testNumber, is(notNullValue()));
assertThat(testNumber, is(notNullValue(Integer.class)));
```

### 3.6 instanceOf(Class<?>)

如果检查的对象是指定Class类型的实例，则instanceOf(Class<?>)匹配。 

**为了验证，这个方法内部调用了Class类的isIntance(Object)**：

```java
assertThat("instanceOf example", is(instanceOf(String.class)));
```

### 3.7 isA(Class<T\> type)

isA(Class<T\> type)是上述instanceOf(Class<?\>)的快捷方式，**它接收与instanceOf(Class<?\>)完全相同的参数类型**：

```java
assertThat("Drogon is biggest dragon", isA(String.class));
```

### 3.8 sameInstance()

 如果两个引用变量指向堆中的同一个对象，则sameInstance()匹配：

```java
String string1 = "Viseron";
String string2 = string1;

assertThat(string1, is(sameInstance(string2)));
```

### 3.9 any(Class<T\>)

any(Class<T\>)检查类是否与实际对象的类型相同：

```java
assertThat("test string", is(any(String.class)));
assertThat("test string", is(any(Object.class)));
```

### 3.10 allOf(Matcher<? extends T>...)和anyOf(Matcher<? extends T>...)

我们可以使用allOf(Matcher<? extends T>...)来断言实际对象是否与所有指定条件匹配：

```java
String testString = "Achilles is powerful";
assertThat(testString, allOf(startsWith("Achi"), endsWith("ul"), containsString("Achilles")));
```

anyOf(Matcher<? extends T>...)的行为类似于allOf(Matcher<? extends T>...)，但如果检查的对象匹配任何指定条件，则匹配：

```java
String testString = "Hector killed Achilles";
assertThat(testString, anyOf(startsWith("Hec"), containsString("tuyucheng")));
```

### 3.11 hasItem(T)和hasItem(Matcher<? extends T>)

如果检查的Iterable集合与hasItem()或hasItem(Matcher<? extends T>)中的给定对象或匹配器匹配，则这些匹配：

```java
List<String> list = Lists.newArrayList("java", "spring", "tuyucheng");

assertThat(list, hasItem("java"));
assertThat(list, hasItem(isA(String.class)));
```

**类似地，我们也可以使用hasItems(T...)和hasItems(Matcher<? extends T>...)对多个元素进行断言**：

```java
List<String> list = Lists.newArrayList("java", "spring", "tuyucheng");

assertThat(list, hasItems("java", "tuyucheng"));
assertThat(list, hasItems(isA(String.class), endsWith("ing")));
```

### 3.12 both(Matcher<? extends T>)和any(Matcher<? extends T>)

顾名思义，当两个指定的条件都与检查的对象匹配时，both(Matcher<? extends T>)匹配：

```java
String testString = "daenerys targaryen";
assertThat(testString, both(startsWith("daene")).and(containsString("yen")));
```

当任一指定条件与被检查对象匹配时，则any(Matcher<? extends T>)匹配：

```java
String testString = "daenerys targaryen";
assertThat(testString, either(startsWith("tar")).or(containsString("targaryen")));
```

## 4. 字符串比较

我们可以使用containsString(String)或containsStringIgnoringCase(String)来断言实际字符串是否包含特定字符串：

```java
String testString = "Rhaegar Targaryen";
assertThat(testString, containsString("aegar"));
assertThat(testString, containsStringIgnoringCase("AEGAR"));
```

或者使用startsWith(String)和startsWithIgnoringCase(String)断言实际字符串是否以特定字符串开头：

```java
assertThat(testString, startsWith("Rhae"));
assertThat(testString, startsWithIgnoringCase("rhae"));
```

还可以使用endsWith(String)或endsWithIgnoringCase(String)来断言实际字符串是否以特定字符串结尾：

```java
assertThat(testString, endsWith("aryen"));
assertThat(testString, endsWithIgnoringCase("ARYEN"));
```

## 5. 总结

在本文中，我们介绍了Hamcrest库中CoreMatchers类的不同方法。