## 1. 概述

在本教程中，我们将回顾 Hibernate Validator 约束，这些约束内置于 Hibernate Validator 中，但在 Bean Validation 规范之外。

有关 Bean Validation 的概述，请参阅我们关于 [Java Bean Validation Basics 的](https://www.baeldung.com/javax-validation)文章。

## 2. Hibernate 验证器设置

至少，我们应该将[Hibernate Validator](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-validator")添加到我们的依赖项中：

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.16.Final</version>
</dependency>
```

请注意，Hibernate Validator 不依赖于[Hibernate，即我们在许多其他文章中介绍过的 ORM。](https://www.baeldung.com/tag/hibernate/)

此外，我们将介绍的一些注解仅在我们的项目使用某些库时才适用。因此，对于其中的每一个，我们将指出必要的依赖关系。

## 3. 验证与货币相关的价值

### 3.1. 验证信用卡号

有效的信用卡号必须满足我们使用[Luhn 算法](https://en.wikipedia.org/wiki/Luhn_algorithm)计算的校验和。当字符串满足校验和时， @CreditCardNumber约束成功。

@CreditCardNumber不对输入字符串执行任何其他检查。特别是，它不检查输入的长度。因此，它只能检测由于小错字而无效的数字。

请注意，默认情况下，如果字符串包含非数字字符，约束将失败，但我们可以告诉它忽略它们：

```java
@CreditCardNumber(ignoreNonDigitCharacters = true)
private String lenientCreditCardNumber;
```

然后，我们可以包含空格或破折号等字符：

```java
validations.setLenientCreditCardNumber("7992-7398-713");
constraintViolations = validator.validateProperty(validations, "lenientCreditCardNumber");
assertTrue(constraintViolations.isEmpty());
```

### 3.2. 验证货币价值

@Currency验证器检查给定的货币金额是否为指定的货币：

```java
@Currency("EUR")
private MonetaryAmount balance;
```

MonetaryAmount类是JavaMoney 的一部分。因此，@Currency仅[在JavaMoney 实现可用时](https://www.baeldung.com/java-money-and-currency)适用。

正确设置JavaMoney 后，我们可以检查约束：

```java
bean.setBalance(Money.of(new BigDecimal(100.0), Monetary.getCurrency("EUR")));
constraintViolations = validator.validateProperty(bean, "balance");
assertEquals(0, constraintViolations.size());
```

## 4. 验证范围

### 4.1. 数字和货币范围

bean 验证规范定义了几个我们可以在数字字段上强制执行的约束。除此之外，Hibernate Validator 还提供了一个方便的注解， @Range ，它充当@Min 和@Max 的组合，匹配包含范围的范围： 

```java
@Range(min = 0, max = 100)
private BigDecimal percent;
```

与@Min和@Max一样，@Range适用于原始数字类型及其包装器的字段；BigInteger和BigDecimal， 上面的字符串表示，最后是MonetaryValue字段。

### 4.2. 持续时间

除了表示时间点的值的标准 JSR 380 注解之外，Hibernate Validator 还包括Duration的约束。确保首先检查Java Time[的 Period和Duration类](https://www.baeldung.com/java-8-date-time-intro#period) 。

因此，我们可以对属性强制执行最小和最大持续时间：

```java
@DurationMin(days = 1, hours = 2)
@DurationMax(days = 2, hours = 1)
private Duration duration;
```

即使我们没有在这里显示它们，注解也包含从纳秒到天的所有时间单位的参数。

请注意，默认情况下，包含最小值和最大值。也就是说，与最小值或最大值完全相同的值将通过验证。

相反，如果我们希望边界值无效，我们将inclusive属性定义为 false：

```java
@DurationMax(minutes = 30, inclusive = false)
```

## 5.验证字符串

### 5.1. 字符串长度

我们可以使用两个略有不同的约束来强制字符串具有一定的长度。

通常，我们要确保一个字符串的字符长度——我们用长度方法测量的长度——在最小值和最大值之间。在这种情况下，我们在 String 属性或字段上使用@Length ：

```java
@Length(min = 1, max = 3)
private String someString;
```

但是，由于 Unicode 的复杂性，有时字符长度和代码点长度不同。当我们想检查后者时，我们使用@CodePointLength：

```java
@CodePointLength(min = 1, max = 3)
private String someString;
```

例如，字符串“aauD835uDD0A”有 4 个字符长，但它只包含 3 个代码点，因此它会通过第一个约束并通过第二个约束。

此外，对于这两个注解，我们可以省略最小值或最大值。

### 5.2. 检查数字串

我们已经了解了如何检查字符串是否是有效的信用卡号。但是，Hibernate Validator 包括其他几个对数字字符串的约束。

我们正在审查的第一个是@LuhnCheck。这是@CreditCardNumber的通用版本，因为它执行相同的检查，但允许附加参数：

```java
@LuhnCheck(startIndex = 0, endIndex = Integer.MAX_VALUE, checkDigitIndex = -1)
private String someString;
```

在这里，我们已经显示了参数的默认值，所以上面相当于一个简单的@LuhnCheck注解。

但是，正如我们所见，我们可以对子字符串(startIndex和endIndex)执行检查并告诉约束哪个数字是校验和数字，-1 表示检查的子字符串中的最后一个。

其他有趣的约束包括[模 10 校验](https://www.activebarcode.com/codes/checkdigit/modulo10.html)( @Mod10Check ) 和[模 11 校验](https://www.activebarcode.com/codes/checkdigit/modulo11.html)( @Mod11Check )，它们通常用于条形码和其他代码，例如 ISBN。

然而，对于那些特定的情况，Hibernate Validator 恰好提供了一个约束来验证 ISBN 代码，@ISBN，以及一个用于[EAN 条形码的](https://en.wikipedia.org/wiki/International_Article_Number)@EAN约束。

### 5.3. URL 和 HTML 验证

@Url约束验证字符串是否是 URL 的有效表示。此外，我们可以检查 URL 的特定部分是否具有特定值：

```java
@URL(protocol = "https")
private String url;
```

因此，我们可以检查协议、主机和端口。如果这还不够，我们可以使用regexp属性将 URL 与正则表达式进行匹配。

我们还可以验证属性是否包含“安全”HTML 代码(例如，没有脚本标签)：

```java
@SafeHtml
private String html;
```

@SafeHtml使用[JSoup 库](https://www.baeldung.com/java-with-jsoup)，它必须包含在我们的依赖项中。

我们可以使用内置标签白名单(注解的白名单属性)并包括额外的标签和属性( additionalTags和additionalTagsWithAttributes参数)来根据我们的需要定制 HTML 清理。

## 6. 其他限制

让我们简单地提一下，Hibernate Validator 包括一些国家和地区特定的约束，特别是一些巴西和波兰的身份证号码、纳税人代码等。请参阅[文档的相关部分](http://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#_country_specific_constraints)以获取完整列表。

此外，我们可以使用@UniqueElements 检查集合是否包含重复项。

最后，对于现有注解未涵盖的复杂情况，我们可以调用用 JSR-223 兼容脚本引擎编写的脚本。当然，[我们在关于 Nashorn 的文章](https://www.baeldung.com/java-nashorn)中提到了 JSR-223，Nashorn是现代 JVM 中包含的 JavaScript 实现。

在这种情况下，注解在类级别，脚本在整个实例上调用，作为变量_this 传递：

```java
@ScriptAssert(lang = "nashorn", script = "_this.valid")
public class AdditionalValidations {
    private boolean valid = true;
    // standard getters and setters
}
```

然后，我们可以检查整个实例的约束：

```java
bean.setValid(false);
constraintViolations = validator.validate(bean);
assertEquals(1, constraintViolations.size());
```

## 七. 总结

在本文中，我们列出了 Hibernate Validator 中的约束，这些约束超出了 Bean Validation 规范中定义的最小集。