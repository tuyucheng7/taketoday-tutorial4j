## 1. 概述

[Hamcrest](http://hamcrest.org/)是Java生态系统中用于单元测试的知名框架。它捆绑在JUnit中，简单地说，它使用现有的谓词(称为匹配器类)来进行断言。

在本教程中，我们将**探索Hamcrest API**并学习如何利用它为我们的软件编写更简洁、更直观的单元测试。

## 2. Hamcrest设置

我们可以通过将以下依赖项添加到我们的pom.xml文件中来将**Hamcrest**与Maven一起使用：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>1.3</version>
</dependency>
```

这个库的最新版本总是可以在[这里](https://central.sonatype.com/artifact/org.hamcrest/hamcrest-all/1.3)找到。

## 3. 示例测试

Hamcrest通常与JUnit和其他测试框架一起使用以进行断言。具体来说，我们不会使用JUnit的众多断言方法，而是仅使用API的单个assertThat语句和适当的匹配器。

让我们看一个示例，该示例测试两个String的相等性，而不考虑大小写。这应该让我们清楚地了解Hamcrest如何适应测试方法：

```java
public class StringMatcherTest {

    @Test
    public void given2Strings_whenEqual_thenCorrect() {
        String a = "foo";
        String b = "FOO";
        assertThat(a, equalToIgnoringCase(b));
    }
}
```

在以下部分中，我们将介绍Hamcrest提供的其他几个常见匹配器。

## 4. 对象匹配器

Hamcrest提供了对任意Java对象进行断言的匹配器。

断言Object的toString方法返回指定的String：

```java
@Test
public void givenBean_whenToStringReturnsRequiredString_thenCorrect(){
    Person person=new Person("Barrack", "Washington");
    String str=person.toString();
    assertThat(person,hasToString(str));
}
```

我们还可以检查一个类是另一个类的子类：

```java
@Test
public void given2Classes_whenOneInheritsFromOther_thenCorrect(){
    assertThat(Cat.class,typeCompatibleWith(Animal.class));
}
```

## 5. Bean匹配器

我们可以使用Hamcrest的Bean匹配器来检查Java bean的属性。

假设以下Person bean：

```java
public class Person {
    String name;
    String address;

    public Person(String personName, String personAddress) {
        name = personName;
        address = personAddress;
    }
}
```

我们可以检查bean是否具有属性name：

```java
@Test
public void givenBean_whenHasValue_thenCorrect() {
    Person person = new Person("Tuyucheng", 25);
    assertThat(person, hasProperty("name"));
}
```

我们还可以检查Person是否有address属性，初始值为New York：

```java
@Test
public void givenBean_whenHasCorrectValue_thenCorrect() {
    Person person = new Person("Tuyucheng", "New York");
    assertThat(person, hasProperty("address", equalTo("New York")));
}
```

我们还可以检查两个Person对象是否使用相同的值构造：

```java
@Test
public void given2Beans_whenHavingSameValues_thenCorrect() {
    Person person1 = new Person("Tuyucheng", "New York");
    Person person2 = new Person("Tuyucheng", "New York");
    assertThat(person1, samePropertyValuesAs(person2));
}
```

## 6. 集合匹配器

Hamcrest提供了用于检查Collection的匹配器。

简单检查以找出Collection是否为空：

```java
@Test
public void givenCollection_whenEmpty_thenCorrect() {
    List<String> emptyList = new ArrayList<>();
    assertThat(emptyList, empty());
}
```

检查集合的大小：

```java
@Test
public void givenAList_whenChecksSize_thenCorrect() {
    List<String> hamcrestMatchers = Arrays.asList("collections", "beans", "text", "number");
    assertThat(hamcrestMatchers, hasSize(4));
}
```

我们还可以使用它来断言数组具有所需的大小：

```java
@Test
public void givenArray_whenChecksSize_thenCorrect() {
    String[] hamcrestMatchers = { "collections", "beans", "text", "number" };
    assertThat(hamcrestMatchers, arrayWithSize(4));
}
```

检查集合是否包含给定成员，无论顺序如何：

```java
@Test
public void givenAListAndValues_whenChecksListForGivenValues_thenCorrect() {
    List<String> hamcrestMatchers = Arrays.asList("collections", "beans", "text", "number");
    assertThat(hamcrestMatchers,
    containsInAnyOrder("beans", "text", "collections", "number"));
}
```

进一步断言Collection成员按给定顺序排列：

```java
@Test
public void givenAListAndValues_whenChecksListForGivenValuesWithOrder_thenCorrect() {
    List<String> hamcrestMatchers = Arrays.asList("collections", "beans", "text", "number");
    assertThat(hamcrestMatchers,
    contains("collections", "beans", "text", "number"));
}
```

检查数组是否具有单个给定元素：

```java
@Test
public void givenArrayAndValue_whenValueFoundInArray_thenCorrect() {
    String[] hamcrestMatchers = { "collections", "beans", "text", "number" };
    assertThat(hamcrestMatchers, hasItemInArray("text"));
}
```

我们还可以为相同的测试使用替代匹配器：

```java
@Test
public void givenValueAndArray_whenValueIsOneOfArrayElements_thenCorrect() {
    String[] hamcrestMatchers = { "collections", "beans", "text", "number" };
    assertThat("text", isOneOf(hamcrestMatchers));
}
```

或者我们仍然可以像这样使用不同的匹配器做同样的事情：

```java
@Test
public void givenValueAndArray_whenValueFoundInArray_thenCorrect() {
    String[] array = new String[] { "collections", "beans", "text", "number" };
    assertThat("beans", isIn(array));
}
```

我们还可以检查数组是否包含给定的元素，无论顺序如何：

```java
@Test
public void givenArrayAndValues_whenValuesFoundInArray_thenCorrect() {
    String[] hamcrestMatchers = { "collections", "beans", "text", "number" };
    assertThat(hamcrestMatchers, arrayContainingInAnyOrder("beans", "collections", "number","text"));
}
```

检查数组是否包含给定元素，并且遵循给定顺序：

```java
@Test
public void givenArrayAndValues_whenValuesFoundInArrayInOrder_thenCorrect() {
    String[] hamcrestMatchers = { "collections", "beans", "text", "number" };
    assertThat(hamcrestMatchers,
    arrayContaining("collections", "beans", "text", "number"));
}
```

当我们的Collection是Map时，我们可以在这些各自的函数中使用以下匹配器：

检查它是否包含给定的键：

```java
@Test
public void givenMapAndKey_whenKeyFoundInMap_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("blogname", "tuyucheng");
    assertThat(map, hasKey("blogname"));
}
```

和给定的值：

```java
@Test
public void givenMapAndValue_whenValueFoundInMap_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("blogname", "tuyucheng");
    assertThat(map, hasValue("tuyucheng"));
}
```

给定的Entry(键，值)：

```java
@Test
public void givenMapAndEntry_whenEntryFoundInMap_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("blogname", "tuyucheng");
    assertThat(map, hasEntry("blogname", "tuyucheng"));
}
```

## 7. 数字匹配器

数字匹配器用于对Number类的变量执行断言。

检查大于条件：

```java
@Test
public void givenAnInteger_whenGreaterThan0_thenCorrect() {
    assertThat(1, greaterThan(0));
}
```

检查大于或等于条件：

```java
@Test
public void givenAnInteger_whenGreaterThanOrEqTo5_thenCorrect() {
    assertThat(5, greaterThanOrEqualTo(5));
}
```

检查小于条件：

```java
@Test
public void givenAnInteger_whenLessThan0_thenCorrect() {
    assertThat(-1, lessThan(0));
}
```

检查小于或等于条件：

```java
@Test
public void givenAnInteger_whenLessThanOrEqTo5_thenCorrect() {
    assertThat(-1, lessThanOrEqualTo(5));
}
```

检查closeTo条件：

```java
@Test
public void givenADouble_whenCloseTo_thenCorrect() {
    assertThat(1.2, closeTo(1, 0.5));
}
```

让我们密切关注最后一个匹配器closeTo。第一个参数(操作数)是与目标进行比较的参数，第二个参数是与操作数的允许偏差。这意味着如果目标是操作数+偏差或操作数-偏差，那么测试将通过。

## 8. 文本匹配器

使用Hamcrest的文本匹配器，String上的断言变得更加容易、更整洁、更直观。我们将在本节中介绍它们。

检查字符串是否为empty：

```java
@Test
public void givenString_whenEmpty_thenCorrect() {
    String str = "";
    assertThat(str, isEmptyString());
}
```

检查String是否为empty或null：

```java
@Test
public void givenString_whenEmptyOrNull_thenCorrect() {
    String str = null;
    assertThat(str, isEmptyOrNullString());
}
```

在忽略空格的情况下检查两个String的相等性：

```java
@Test
public void given2Strings_whenEqualRegardlessWhiteSpace_thenCorrect() {
    String str1 = "text";
    String str2 = " text ";
    assertThat(str1, equalToIgnoringWhiteSpace(str2));
}
```

我们还可以按给定顺序检查给定字符串中是否存在一个或多个子字符串：

```java
@Test
public void givenString_whenContainsGivenSubstring_thenCorrect() {
    String str = "calligraphy";
    assertThat(str, stringContainsInOrder(Arrays.asList("call", "graph")));
}
```

最后，我们可以检查两个String是否相等，而不考虑大小写：

```java
@Test
public void given2Strings_whenEqual_thenCorrect() {
    String a = "foo";
    String b = "FOO";
    assertThat(a, equalToIgnoringCase(b));
}
```

## 9. 核心API

Hamcrest核心API将由第三方框架提供商使用。但是，它为我们提供了一些很棒的结构来使我们的单元测试更具可读性，并且还提供了一些可以轻松使用的核心匹配器。

匹配器上is构造的可读性：

```java
@Test
public void given2Strings_whenIsEqualRegardlessWhiteSpace_thenCorrect() {
    String str1 = "text";
    String str2 = " text ";
    assertThat(str1, is(equalToIgnoringWhiteSpace(str2)));
}
```

对简单的数据类型使用is构造：

```java
@Test
public void given2Strings_whenIsEqual_thenCorrect() {
    String str1 = "text";
    String str2 = "text";
    assertThat(str1, is(str2));
}
```

在匹配器上使用not构造进行否定：

```java
@Test
public void given2Strings_whenIsNotEqualRegardlessWhiteSpace_thenCorrect() {
    String str1 = "text";
    String str2 = " texts ";
    assertThat(str1, not(equalToIgnoringWhiteSpace(str2)));
}
```

简单数据类型上的not构造：

```java
@Test
public void given2Strings_whenNotEqual_thenCorrect() {
    String str1 = "text";
    String str2 = "texts";
    assertThat(str1, not(str2));
}
```

检查字符串是否包含给定的子字符串：

```java
@Test
public void givenAStrings_whenContainsAnotherGivenString_thenCorrect() {
    String str1 = "calligraphy";
    String str2 = "call";
    assertThat(str1, containsString(str2));
}
```

检查字符串是否以给定的子字符串开头：

```java
@Test
public void givenAString_whenStartsWithAnotherGivenString_thenCorrect() {
    String str1 = "calligraphy";
    String str2 = "call";
    assertThat(str1, startsWith(str2));
}
```

检查字符串是否以给定的子字符串结尾：

```java
@Test
public void givenAString_whenEndsWithAnotherGivenString_thenCorrect() {
    String str1 = "calligraphy";
    String str2 = "phy";
    assertThat(str1, endsWith(str2));
}
```

检查两个Object是否属于同一个实例：

```java
@Test
public void given2Objects_whenSameInstance_thenCorrect() {
    Cat cat=new Cat();
    assertThat(cat, sameInstance(cat));
}
```

检查Object是否是给定类的实例：

```java
@Test
public void givenAnObject_whenInstanceOfGivenClass_thenCorrect() {
    Cat cat=new Cat();
    assertThat(cat, instanceOf(Cat.class));
}
```

检查集合的所有成员是否满足条件：

```java
@Test
public void givenList_whenEachElementGreaterThan0_thenCorrect() {
    List<Integer> list = Arrays.asList(1, 2, 3);
    int baseCase = 0;
    assertThat(list, everyItem(greaterThan(baseCase)));
}
```

检查String是否不为null：

```java
@Test
public void givenString_whenNotNull_thenCorrect() {
    String str = "notnull";
    assertThat(str, notNullValue());
}
```

将条件链接在一起，当目标满足任何条件时测试通过，类似于逻辑或：

```java
@Test
public void givenString_whenMeetsAnyOfGivenConditions_thenCorrect() {
    String str = "calligraphy";
    String start = "call";
    String end = "foo";
    assertThat(str, anyOf(startsWith(start), containsString(end)));
}
```

将条件链在一起，仅当目标满足所有条件时测试才通过，类似于逻辑与：

```java
@Test
public void givenString_whenMeetsAllOfGivenConditions_thenCorrect() {
    String str = "calligraphy";
    String start = "call";
    String end = "phy";
    assertThat(str, allOf(startsWith(start), endsWith(end)));
}
```

## 10. 自定义匹配器

我们可以通过扩展TypeSafeMatcher来定义我们自己的匹配器。在本节中，我们将创建一个自定义匹配器，该匹配器仅在目标为正整数时才允许测试通过。

```java
public class IsPositiveInteger extends TypeSafeMatcher<Integer> {

    public void describeTo(Description description) {
        description.appendText("a positive integer");
    }

    @Factory
    public static Matcher<Integer> isAPositiveInteger() {
        return new IsPositiveInteger();
    }

    @Override
    protected boolean matchesSafely(Integer integer) {
        return integer > 0;
    }
}
```

我们只需要实现matchSafely方法来检查目标确实是一个正整数，以及describeTo方法，它会在测试不通过时生成失败消息。

这是一个使用我们新的自定义匹配器的测试：

```java
@Test
public void givenInteger_whenAPositiveValue_thenCorrect() {
    int num = 1;
    assertThat(num, isAPositiveInteger());
}
```

这是我们收到的失败消息，因为我们传入了一个非正整数：

```shell
java.lang.AssertionError: Expected: a positive integer but: was <-1>
```

## 11. 总结

在本教程中，我们探索了Hamcrest API并学习了如何使用它编写更好、更易于维护的单元测试。