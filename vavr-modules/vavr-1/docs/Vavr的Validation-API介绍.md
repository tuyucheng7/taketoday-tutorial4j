## 1. 概述

验证是Java应用程序中经常发生的任务，因此在验证库的开发上投入了大量精力。

[Vavr](http://www.vavr.io/)(以前称为Javaslang)提供了一个成熟的[验证API](http://www.vavr.io/vavr-docs/#_validation)。它允许我们通过使用对象函数式编程风格以直接的方式验证数据。如果你想了解这个库开箱即用的功能，请随时查看[这篇文章](https://www.baeldung.com/vavr)。

在本教程中，我们深入了解库的验证API并学习如何使用其最相关的方法。

## 2. Validation接口

Vavr的验证接口基于称为[applicative functor](https://en.wikipedia.org/wiki/Applicative_functor)的函数式编程概念。它在累积结果的同时执行一系列函数，即使这些函数中的一些或全部在执行链中失败。

该库的应用函子建立在其验证接口的实现者之上。此接口提供用于累积验证错误和验证数据的方法，因此允许将它们作为批处理。

## 3. 验证用户输入

使用验证API可以顺利验证用户输入(例如，从Web层收集的数据)，因为它归结为创建一个自定义验证类来验证数据，同时累积结果错误(如果有)。

让我们验证通过登录表单提交的用户名和电子邮件。首先，我们需要将[Vavr](https://search.maven.org/classic/#search|ga|1|vavr)的Maven工件包含到pom.xml文件中：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.0</version>
</dependency>
```

接下来，让我们创建一个对用户对象建模的域类：

```java
public class User {
    private String name;
    private String email;
    
    // standard constructors, setters and getters, toString
}
```

最后，让我们定义我们的自定义验证器：

```java
public class UserValidator {
	private static final String NAME_PATTERN = ...
	private static final String NAME_ERROR = ...
	private static final String EMAIL_PATTERN = ...
	private static final String EMAIL_ERROR = ...

	public Validation<Seq<String>, User> validateUser(
		String name, String email) {
		return Validation
			.combine(
				validateField(name, NAME_PATTERN, NAME_ERROR),
				validateField(email, EMAIL_PATTERN, EMAIL_ERROR))
			.ap(User::new);
	}

	private Validation<String, String> validateField
		(String field, String pattern, String error) {

		return CharSeq.of(field)
			.replaceAll(pattern, "")
			.transform(seq -> seq.isEmpty()
				? Validation.valid(field)
				: Validation.invalid(error));
	}
}
```

UserValidator类使用validateField()方法分别验证提供的名称和电子邮件。在这种情况下，此方法执行典型的基于正则表达式的模式匹配。

此示例的本质是使用valid()、invalid()和combine()方法。

## 4. valid()、invalid()和combine()方法

如果提供的姓名和电子邮件与给定的正则表达式匹配，则validateField()方法调用valid()。此方法返回Validation.Valid的一个实例。相反，如果值无效，对应部分invalid()方法返回Validation.Invalid的实例。

这种基于根据验证结果创建不同的Validation实例的简单机制应该至少让我们对如何处理结果有一个基本的了解(更多内容在第5节)。

验证过程中最相关的方面是combine()方法。在内部，此方法使用Validation.Builder类，它允许组合最多8个不同的Validation实例，这些实例可以用不同的方法计算：

```java
static <E, T1, T2> Builder<E, T1, T2> combine(Validation<E, T1> validation1, Validation<E, T2> validation2) {
    Objects.requireNonNull(validation1, "validation1 is null");
    Objects.requireNonNull(validation2, "validation2 is null");
    return new Builder<>(validation1, validation2);
}
```

最简单的Validation.Builder类采用两个验证实例：

```java
final class Builder<E, T1, T2> {

	private Validation<E, T1> v1;
	private Validation<E, T2> v2;

	// standard constructors

	public <R> Validation<Seq<E>, R> ap(Function2<T1, T2, R> f) {
		return v2.ap(v1.ap(Validation.valid(f.curried())));
	}

	public <T3> Builder3<E, T1, T2, T3> combine(
		Validation<E, T3> v3) {
		return new Builder3<>(v1, v2, v3);
	}
}
```

Validation.Builder与ap(Function)方法一起返回一个包含验证结果的结果。如果所有结果都有效，则ap(Function)方法将结果映射到单个值。该值通过使用其签名中指定的函数存储在Valid实例中。

在我们的示例中，如果提供的名称和电子邮件有效，则会创建一个新的用户对象。当然，可以对有效结果做一些完全不同的事情，即将其存储到数据库中，通过电子邮件发送等等。

## 5. 处理验证结果

实现处理验证结果的不同机制非常容易。但是我们首先如何验证数据呢？为此，我们使用UserValidator类：

```java
UserValidator userValidator = new UserValidator(); 
Validation<Seq<String>, User> validation = userValidator
  	.validateUser("John", "john@domain.com");
```

一旦获得了Validation的实例，我们就可以通过多种方式利用验证API和处理结果的灵活性。

让我们详细说明最常见的方法。

### 5.1 Valid和Invalid实例

这种方法是迄今为止最简单的方法。它包括使用Valid和Invalid实例检查验证结果：

```java
@Test
public void givenInvalidUserParams_whenValidated_thenInvalidInstance() {
    assertThat(
      	userValidator.validateUser(" ", "no-email"), 
      	instanceOf(Invalid.class));
}
	
@Test
public void givenValidUserParams_whenValidated_thenValidInstance() {
	assertThat(
      	userValidator.validateUser("John", "john@domain.com"), 
      	instanceOf(Valid.class));
}
```

与其使用Valid和Invalid实例检查结果的有效性，不如更进一步，使用isValid()和isInvalid()方法。

### 5.2 isValid()和isInvalid() API

使用串联isValid()/isInvalid()类似于以前的方法，不同之处在于这些方法返回true或false，具体取决于验证结果：

```java
@Test
public void givenInvalidUserParams_whenValidated_thenIsInvalidIsTrue() {
    assertTrue(userValidator
      	.validateUser("John", "no-email")
      	.isInvalid());
}

@Test
public void givenValidUserParams_whenValidated_thenIsValidMethodIsTrue() {
    assertTrue(userValidator
      	.validateUser("John", "john@domain.com")
      	.isValid());
}
```

Invalid实例包含所有验证错误，可以使用getError()方法获取它们：

```java
@Test
public void givenInValidUserParams_withGetErrorMethod_thenGetErrorMessages() {
    assertEquals(
      	"Name contains invalid characters, Email must be a well-formed email address", 
      	userValidator.validateUser("John", "no-email")
        	.getError()
        	.intersperse(", ")
        	.fold("", String::concat));
 }
```

相反，如果结果有效，则可以使用get()方法获取User实例：

```java
@Test
public void givenValidUserParams_withGetMethod_thenGetUserInstance() {
    assertThat(userValidator.validateUser("John", "john@domain.com")
      	.get(), instanceOf(User.class));
 }
```

这种方法按预期工作，但代码看起来仍然非常冗长。我们可以使用toEither()方法进一步压缩它。

### 5.3 toEither() API

toEither()方法构造Either接口的Left和Right实例。这个补充接口有几个方便的方法，可用于缩短验证结果的处理。

如果结果有效，则将结果存储在Right实例中。在我们的示例中，这相当于一个有效的用户对象。相反，如果结果无效，错误将存储在Left实例中：

```java
@Test
public void givenValidUserParams_withtoEitherMethod_thenRightInstance() {
    assertThat(userValidator.validateUser("John", "john@domain.com")
      	.toEither(), instanceOf(Right.class));
}
```

代码现在看起来更加简洁和流线型。但我们还没有完成。Validation接口提供了fold()方法，该方法应用一个自定义函数，该函数适用于有效结果，另一个适用于无效结果。

### 5.4 fold() API

让我们看看如何使用fold()方法来处理验证结果：

```java
@Test
public void givenValidUserParams_withFoldMethod_thenEqualstoParamsLength() {
    assertEquals(2, (int) userValidator.validateUser(" ", " ")
      	.fold(Seq::length, User::hashCode));
}
```

fold()的使用将验证结果的处理减少到一行。

值得强调的是，作为参数传递给方法的函数的返回类型必须相同。此外，函数必须由验证类中定义的类型参数支持，即Seq<String\>和User。

## 6. 总结

在本文中，我们深入探讨了Vavr的Validation API，并学习了如何使用它的一些最相关的方法。有关完整列表，请查看[官方文档API](https://www.javadoc.io/doc/io.vavr/vavr/0.9.0)。

Vavr的验证控件提供了一个非常有吸引力的替代方案，可以替代更传统的[Java Beans验证](http://beanvalidation.org/)实现，例如[Hibernate Validator](http://hibernate.org/validator/)。