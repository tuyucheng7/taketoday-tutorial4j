## 1. 概述

[JavaFaker](https://github.com/DiUS/java-faker)是一个库，可用于生成从地址到流行文化参考的各种真实数据。

在本教程中，我们将研究如何使用JavaFaker的类来生成假数据。我们将首先介绍Faker类和FakeValueService，然后再介绍区域设置以使数据更具体到单个位置。

最后，我们将讨论数据的独特性。为了测试JavaFaker的类，我们将使用正则表达式，你可以在[此处](https://www.baeldung.com/regular-expressions-java)阅读有关它们的更多信息。

## 2. 依赖

下面是我们开始使用JavaFaker所需的单个[依赖项](https://central.sonatype.com/artifact/com.github.javafaker/javafaker/1.0.2)。

首先，基于Maven项目所需的依赖项：

```xml
<dependency>
    <groupId>com.github.javafaker</groupId>
    <artifactId>javafaker</artifactId>
    <version>0.15</version>
</dependency>
```

对于Gradle用户，你可以将以下内容添加到build.gradle文件中：

```groovy
compile group: 'com.github.javafaker', name: 'javafaker', version: '0.15'
```

## 3. FakeValueService

FakeValueService类提供了[生成随机序列](https://dius.github.io/java-faker/apidocs/index.html)以及解析与[locale](https://www.baeldung.com/java-faker#locales)关联的.yml文件的方法。 

在本节中，我们将介绍FakerValueService必须提供的一些有用方法。

### 3.1 Letterify、Numerify和Bothify

三个有用的方法是Letterify、Numberify和Bothify。Letterify有助于生成**随机的字母字符序列**。

接下来，Numerify简单地生成数字序列。

最后，Bothify是两者的结合，可以**创建随机的字母数字序列**-对于模拟ID字符串之类的东西很有用。

FakeValueService需要一个有效的Locale设置，以及一个RandomService：

```java
@Test
public void whenBothifyCalled_checkPatternMatches() throws Exception {
    FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());

    String email = fakeValuesService.bothify("????##@gmail.com");
    Matcher emailMatcher = Pattern.compile("\\w{4}\\d{2}@gmail.com").matcher(email);
 
    assertTrue(emailMatcher.find());
}
```

在这个单元测试中，我们创建了一个语言环境为en-GB的新FakeValueService，并**使用bothify方法生成一个唯一的假Gmail地址**。

它的工作原理**是用随机字母替换“？”，用随机数替换“#”**。然后我们可以通过简单的Matcher检查来检查输出是否正确。

### 3.2 正则表达式

同样，**regexify会根据选择的正则表达式模式生成一个随机序列**。

在此代码段中，我们将使用FakeValueService根据指定的正则表达式创建一个随机序列：

```java
@Test
public void givenValidService_whenRegexifyCalled_checkPattern() throws Exception {
    FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());

    String alphaNumericString = fakeValuesService.regexify("[a-z1-9]{10}");
    Matcher alphaNumericMatcher = Pattern.compile("[a-z1-9]{10}").matcher(alphaNumericString);
 
    assertTrue(alphaNumericMatcher.find());
}
```

我们的代码**创建了一个长度为10的小写字母数字字符串**。我们的模式根据正则表达式检查生成的字符串。

## 4. JavaFaker的Faker类

Faker类**允许我们使用JavaFaker的假数据类**。

在本节中，我们将了解如何实例化一个Faker对象并使用它来调用一些假数据：

```java
Faker faker = new Faker();

String streetName = faker.address().streetName();
String number = faker.address().buildingNumber();
String city = faker.address().city();
String country = faker.address().country();

System.out.println(String.format("%s\n%s\n%s\n%s",
    number,
    streetName,
    city,
    country));
```

上面，**我们使用Faker Address对象来生成一个随机地址**。

当我们运行这段代码时，我们将得到一个输出示例：

```shell
3188
Dayna Mountains
New Granvilleborough
Tonga
```

我们可以看**到数据没有单一的地理位置**，因为我们没有指定区域设置。为了改变这一点，我们将在下一节中学习使数据与我们的位置更相关。

我们还可以以类似的方式使用这个faker对象来创建与更多对象相关的数据，例如：

-   Business
-   Beer
-   Food
-   PhoneNumber

你可以在[此处](https://github.com/DiUS/java-faker)找到完整列表。

## 5. 介绍语言环境

在这里，我们将介绍如何**使用语言环境使生成的数据更具体到单个位置**。我们将介绍一个具有US语言环境和UK语言环境的Faker：

```java
@Test
public void givenJavaFakersWithDifferentLocals_thenHeckZipCodesMatchRegex() {
    Faker ukFaker = new Faker(new Locale("en-GB"));
    Faker usFaker = new Faker(new Locale("en-US"));

    System.out.println(String.format("American zipcode: %s", usFaker.address().zipCode()));
    System.out.println(String.format("British postcode: %s", ukFaker.address().zipCode()));

    Pattern ukPattern = Pattern.compile(
        "([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|"
        + "(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y]" 
        + "[0-9]?[A-Za-z]))))\\s?[0-9][A-Za-z]{2})");
    Matcher ukMatcher = ukPattern.matcher(ukFaker.address().zipCode());

    assertTrue(ukMatcher.find());

    Matcher usMatcher = Pattern.compile("^\\d{5}(?:[-\\s]\\d{4})?$")
        .matcher(usFaker.address().zipCode());

    assertTrue(usMatcher.find());
}
```

在上面，我们看到两个具有区域设置的Faker匹配他们的国家邮政编码的正则表达式。

**如果传递给Faker的语言环境不存在，Faker会抛出LocaleDoesNotExistException**。

我们将使用以下单元测试对其进行测试：

```java
@Test(expected = LocaleDoesNotExistException.class)
public void givenWrongLocale_whenFakerInitialised_testExceptionThrown() {
    Faker wrongLocaleFaker = new Faker(new Locale("en-seaWorld"));
}
```

## 6. 独特性

**虽然JavaFaker看似随机生成数据，但无法保证其唯一性**。

**JavaFaker支持以RandomService的形式为其伪随机数生成器(PRNG)设定种子**，以提供重复方法调用的确定性输出。

简单地说，伪随机性是一个看似随机但并非随机的过程。

我们可以通过创建两个具有相同种子的Faker来了解其工作原理：

```java
@Test
public void givenJavaFakersWithSameSeed_whenNameCalled_CheckSameName() {
    Faker faker1 = new Faker(new Random(24));
    Faker faker2 = new Faker(new Random(24));

    assertEquals(faker1.name().firstName(), faker2.name().firstName());
}
```

上面的代码从两个不同的faker返回相同的名称。

## 7. 总结

在本教程中，我们探索了**JavaFaker库来生成看起来真实的假数据**。我们还介绍了两个有用的类Faker类和FakeValueService类。

我们探索了如何使用语言环境来生成特定于位置的数据。

最后，我们讨论了生成的数据如何看起来只是随机的，并且无法保证数据的唯一性。