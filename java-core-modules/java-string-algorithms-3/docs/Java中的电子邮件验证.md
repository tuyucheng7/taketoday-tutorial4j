## 1. 概述

在本教程中，我们将学习如何使用[正则表达式](https://www.baeldung.com/regular-expressions-java)在Java中验证电子邮件地址。

## 2.Java中的电子邮件验证

几乎每个已进行用户注册的应用程序都需要电子邮件验证。

电子邮件地址分为三个主要部分：本地部分、@符号和域。例如，如果“ username@domain.com ”是一封电子邮件，那么：

-   本地部分 = 用户名
-   @ = @
-   域 = domain.com

通过字符串操作技术验证电子邮件地址可能需要付出很多努力，因为我们通常需要计算和检查所有字符类型和长度。但在Java中，通过使用正则表达式，它会容易得多。

众所周知，正则表达式是用于匹配模式的字符序列。在以下部分中，我们将了解如何使用几种不同的正则表达式方法来执行电子邮件验证。

## 3. 简单的正则表达式验证

验证电子邮件地址的最简单的正则表达式是^(.+)@(S+) $。

它只检查电子邮件地址中是否存在@符号。如果存在，则验证结果返回true，否则结果为false。但是，此正则表达式不检查电子邮件的本地部分和域。

例如，根据这个正则表达式，username @domain.com 将通过验证，但username#domain.com将无法通过验证。

让我们定义一个简单的辅助方法来匹配正则表达式模式：

```java
public static boolean patternMatches(String emailAddress, String regexPattern) {
    return Pattern.compile(regexPattern)
      .matcher(emailAddress)
      .matches();
}
```

我们还将编写代码以使用此正则表达式验证电子邮件地址：

```java
@Test
public void testUsingSimpleRegex() {
    emailAddress = "username@domain.com";
    regexPattern = "^(.+)@(S+)$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

电子邮件地址中缺少@符号也会导致验证失败。

## 4. 严格的正则表达式验证

现在让我们编写一个更严格的正则表达式来检查本地部分以及电子邮件的域部分：

^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)@[^-][A-Za- z0-9-]+(.[A-Za-z0-9-]+)(.[A-Za-z]{2,})$

使用此正则表达式在电子邮件地址的本地部分施加以下限制：

-   它允许从 0 到 9 的数值。
-   从 a 到 z 的大写和小写字母都是允许的。
-   允许使用下划线“_”、连字符“-”和点“.”
-   本地部分的开头和结尾不允许使用点。
-   不允许有连续的点。
-   对于本地部分，最多允许 64 个字符。

此正则表达式中域部分的限制包括：

-   它允许从 0 到 9 的数值。
-   我们允许从 a 到 z 的大写和小写字母。
-   连字符“-”和点“.” 不允许出现在域部分的开头和结尾。
-   没有连续的点。

我们还将编写代码来测试这个正则表达式：

```java
@Test
public void testUsingStrictRegex() {
    emailAddress = "username@domain.com";
    regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)@" 
        + "[^-][A-Za-z0-9-]+(.[A-Za-z0-9-]+)(.[A-Za-z]{2,})$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

因此，通过此电子邮件验证技术有效的一些电子邮件地址是：

-   username@domain.com
-   user.name@domain.com
-   user-name@domain.com
-   username@domain.co.in
-   user_name@domain.com

以下是通过此电子邮件验证将无效的一些电子邮件地址的候选名单：

-   用户名.@domain.com
-   .user.name@domain.com
-   user-name@domain.com.
-   用户名@.com

## 5. 验证非拉丁或 Unicode 字符电子邮件的正则表达式

我们刚刚在上一节中看到的正则表达式适用于用英语编写的电子邮件地址，但不适用于非拉丁电子邮件地址。

所以我们将编写一个正则表达式，我们也可以使用它来验证 unicode 字符：

^(?=.{1,64}@)[p{L}0-9_-]+(.[p{L}0-9_-]+)@[^-][ p{L}0-9-]+(.[p{L}0-9-]+)(.[p{L}]{2,})$

我们可以使用此正则表达式来验证 Unicode 或非拉丁电子邮件地址以支持所有语言。

正如我们所见，这个正则表达式类似于我们在上一节中构建的严格正则表达式，只是我们将“ A-Za-Z ”部分更改为“ p{L}”。这是为了启用对 Unicode 字符的支持。

让我们通过编写测试来检查这个正则表达式：

```java
@Test
public void testUsingUnicodeRegex() {
    emailAddress = "用户名@领域.电脑";
    regexPattern = "^(?=.{1,64}@)[p{L}0-9_-]+(.[p{L}0-9_-]+)@" 
        + "[^-][p{L}0-9-]+(.[p{L}0-9-]+)(.[p{L}]{2,})$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

这个正则表达式不仅提供了一种更严格的验证电子邮件地址的方法，而且还支持非拉丁字符。

## 6. RFC 5322 用于电子邮件验证的正则表达式

我们可以使用 RFC 标准提供的正则表达式来验证电子邮件地址，而不是编写自定义正则表达式。

[RFC 5322](https://www.rfc-editor.org/info/rfc5322)是[RFC 822](https://www.rfc-editor.org/info/rfc822)的更新版本，提供了用于电子邮件验证的正则表达式。

让我们来看看：

^[a-zA-Z0-9_!#$%&'+/=?`{|}~^.-] +@ [a-zA-Z0-9.-]+$

正如我们所见，这是一个非常简单的正则表达式，允许电子邮件中的所有字符。

但是，它不允许使用竖线字符 (|) 和单引号 (')，因为它们在从客户端站点传递到服务器时存在潜在的[SQL 注入风险。](https://www.baeldung.com/sql-injection)

让我们编写代码以使用此正则表达式验证电子邮件：

```java
@Test
public void testUsingRFC5322Regex() {
    emailAddress = "username@domain.com";
    regexPattern = "^[a-zA-Z0-9_!#$%&'+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

## 7. 正则表达式检查顶级域中的字符

我们编写了正则表达式来验证电子邮件地址的本地和域部分。现在我们还将编写一个正则表达式来检查电子邮件的顶级域。

以下正则表达式验证电子邮件地址的顶级域部分：

^[w!#$%&'+/=?`{|}~^-]+(?:.[w!#$%&'+/=?`{|} ~^-]+)@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,6}$

这个正则表达式主要检查电子邮件地址是否只有一个点，以及顶级域中是否至少有两个字符，最多六个字符。

我们还将编写一些代码来使用此正则表达式验证电子邮件地址：

```java
@Test
public void testTopLevelDomain() {
    emailAddress = "username@domain.com";
    regexPattern = "^[w!#$%&'+/=?`{|}~^-]+(?:.[w!#$%&'+/=?`{|}~^-]+)" 
        + "@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,6}$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

## 8 . 用于限制连续点、尾随点和前导点的正则表达式

现在让我们编写一个正则表达式来限制电子邮件地址中点的使用：

^[a-zA-Z0-9_!#$%&'+/=?`{|}~^-]+(?:.[a-zA-Z0-9_!#$%&' +/=?`{|}~^-]+)@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)$

上面的正则表达式用于限制连续的、前导的和尾随的点。因此，一封电子邮件可以包含多个点，但在本地和域部分不连续。

让我们看一下代码：

```java
@Test
public void testRestrictDots() {
    emailAddress = "username@domain.com";
    regexPattern = "^[a-zA-Z0-9_!#$%&'+/=?`{|}~^-]+(?:.[a-zA-Z0-9_!#$%&'+/=?`{|}~^-]+)@" 
        + "[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

## 9. OWASP 验证正则表达式

此正则表达式由[OWASP 验证正则表达式存储库](https://owasp.org/www-community/OWASP_Validation_Regex_Repository)提供，用于检查电子邮件验证：

^[a-zA-Z0-9_+&-] + (?:.[a-zA-Z0-9_+&-] + )@(?:[a-zA-Z0-9- ]+.) + [a-zA-Z]{2, 7}

此正则表达式还支持标准电子邮件结构中的大多数验证。

让我们使用以下代码验证电子邮件地址：

```java
@Test
public void testOwaspValidation() {
    emailAddress = "username@domain.com";
    regexPattern = "^[a-zA-Z0-9_+&-]+(?:.[a-zA-Z0-9_+&-]+)@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,7}$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

## 10. Gmail 电子邮件特例

有一种特殊情况仅适用于 Gmail 域：在电子邮件的本地部分使用字符 + 字符的权限。对于Gmail域，username+something@gmail.com和username@gmail.com这两个邮箱地址是一样的。

此外，username @gmail.com类似于user+name@gmail.com。

我们必须实现一个稍微不同的正则表达式，该正则表达式也将通过这种特殊情况的电子邮件验证：

^(?=.{1,64}@)[A-Za-z0-9_-+]+(.[A-Za-z0-9_-+]+)@[^-][A- Za-z0-9-+]+(.[A-Za-z0-9-+]+)(.[A-Za-z]{2,})$

让我们写一个例子来测试这个用例：

```java
@Test
public void testGmailSpecialCase() {
    emailAddress = "username+something@domain.com";
    regexPattern = "^(?=.{1,64}@)[A-Za-z0-9+_-]+(.[A-Za-z0-9+_-]+)@" 
        + "[^-][A-Za-z0-9+-]+(.[A-Za-z0-9+-]+)(.[A-Za-z]{2,})$";
    assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
}
```

## 11. 电子邮件的 Apache Commons 验证器

[Apache Commons Validator](https://commons.apache.org/proper/commons-validator/)是一个包含标准验证规则的验证包。所以通过导入这个包，我们可以应用电子邮件验证。

我们可以使用EmailValidator类来验证电子邮件，它使用 RFC 822 标准。此验证器包含自定义代码和正则表达式的组合以验证电子邮件。它不仅支持特殊字符，还支持我们讨论过的 Unicode 字符。

让我们在我们的项目中添加[commons-validator](https://search.maven.org/artifact/commons-validator/commons-validator/1.7/jar)依赖：

```xml
<dependency>
    <groupId>commons-validator</groupId>
    <artifactId>commons-validator</artifactId>
    <version>${validator.version}</version>
</dependency>
```

现在我们可以使用以下代码验证电子邮件地址：

```java
@Test
public void testUsingEmailValidator() {
    emailAddress = "username@domain.com";
    assertTrue(EmailValidator.getInstance()
      .isValid(emailAddress));
}
```

## 12. 我应该使用哪个正则表达式？

在本文中，我们研究了使用正则表达式进行电子邮件地址验证的各种解决方案。显然，确定我们应该使用哪种解决方案取决于我们希望验证的严格程度以及我们的具体要求。

例如，如果我们只需要一个简单的正则表达式来检查电子邮件中是否存在@符号，我们可以使用第 3 节中的简单正则表达式。然而，为了更详细的验证，我们可以选择第 6 节中基于 RFC5322 标准的更严格的正则表达式解决方案。

最后，如果我们要处理电子邮件中的 Unicode 字符，我们可以使用第 5 节中提供的正则表达式解决方案。

## 13.总结

在本文中，我们学习了使用正则表达式在Java中验证电子邮件地址的各种方法。