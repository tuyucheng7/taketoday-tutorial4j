## 1. 概述

在JUnit 5之前，如果要在框架中引入一个新功能，JUnit团队必须对核心API进行操作。在JUnit 5中，团队决定是时候将核心JUnit API扩展至JUnit本身之外，这是JUnit 5的核心理念，称为“[优先扩展点而不是特性](https://github.com/junit-team/junit5/wiki/Core-Principles)”。

在本文中，我们将重点关注其中一个Extension接口-ParameterResolver，你可以使用它来将参数注入到你的测试方法中。
有几种不同的方法可以让JUnit Platform知道你的Extension(一个称为“注册”的过程)，本文中我们将重点关注声明式注册(即通过源代码注册)。

## 2. ParameterResolver

虽然我们可以使用JUnit 4 API将参数注入到测试方法中，但它相当有限。使用JUnit 5，Jupiter API可以通过实现ParameterResolver进行扩展，以便为你的测试方法提供任何类型的对象。

### 2.1 FooParameterResolver

```java
public class FooParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Foo.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new Foo();
    }
}
```

首先我们需要实现ParameterResolver接口，它有两个方法：

+ supportsParameter()：如果支持该参数的类型(在本例中为Foo)则返回true。
+ resolveParameter()：提供一个正确类型的对象(在本例中是一个new的Foo实例)，然后将其注入到测试方法中。

### 2.2 FooTests

```java
@ExtendWith(FooParameterResolver.class)
class FooTests {

    @Test
    void testIt(Foo fooInstance) {
        assertNotNull(fooInstance);
    }
}
```

为了使用FooParameterResolver扩展，我们需要通过@ExtendWith注解注册它，即告诉JUnit Platform关于它的信息。当JUnit Platform运行你的单元测试时，它会从FooParameterResolver获取一个Foo实例并将其传递给testIt()方法。

Extension有一个影响范围，该范围会激活Extension，具体取决于声明Extension的位置：

+ 方法级别，仅对该方法有效。
+ 类级别，它在整个测试类中都处于激活状态，或者使用@Nested标注的内部测试类。

**注意**：你不应该在两个作用域内为同一参数类型声明ParameterResolver，否则JUnit Platform会不理解这种歧义性。

在本文中，我们将了解如何编写和使用两个Extension来注入Person对象：一个注入有效的数据(称为ValidPersonParameterResolver)，另一个注入无效的数据(InvalidPersonParameterResolver)。我们将使用这些数据对名为PersonValidator的类进行单元测试，该类验证Person对象的状态。

## 3. 编写两个Extension

+ 一个提供有效的Person对象(ValidPersonParameterResolver)
+ 一个提供无效的Person对象(InValidPersonParameterResolver)

### 3.1 ValidPersonParameterResolver

```java
public class ValidPersonParameterResolver implements ParameterResolver {

    public static Person[] VALID_PERSONS = {
            new Person().setId(1L).setLastName("Adams").setFirstName("Jill"),
            new Person().setId(2L).setLastName("Baker").setFirstName("James"),
            new Person().setId(3L).setLastName("Carter").setFirstName("Samanta"),
            new Person().setId(4L).setLastName("Daniels").setFirstName("Joseph"),
            new Person().setId(5L).setLastName("English").setFirstName("Jane"),
            new Person().setId(6L).setLastName("Fontana").setFirstName("Enrique"),
    };
}
```

注意Person类型的VALID_PERSONS数组。这是有效Person对象的数组，每次JUnit Platform调用resolveParameter()方法时，都会从中随机选择一个对象。

在这里使用有效的Person对象主要有两个优点：

1. 单元测试和驱动单元测试的数据之间的关注点分离。
2. 如果其他单元测试需要有效的Person对象来驱动它们，则可以重用。

```java
@Override
public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    // If the Parameter.type == Person.class, then we support it, otherwise, get outta here!
    return parameterContext.getParameter().getType() == Person.class;
}
```

在supportsParameter()方法中，如果参数类型是Person，那么Extension告诉JUnit Platform它支持该参数类型，否则返回false，表示不支持。

这有什么关系？虽然本文中的示例很简单，但在实际应用程序中，单元测试类可能非常庞大和复杂，其中包含许多接收不同类型参数的测试方法。在解析当前影响范围内的参数时，JUnit Platform必须检查所有已注册的ParameterResolver。

```java
@Override
public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
	Object ret = null;
	// Return a random, valid Person object if Person.class is the type of Parameter to be resolved. Otherwise, return null.
	if (parameterContext.getParameter().getType() == Person.class) {
		ret = VALID_PERSONS[new Random().nextInt(VALID_PERSONS.length)];
	}
	return ret;
}
```

从VALID_PERSONS数组返回一个随机Person对象。请注意，只有当supportsParameter()返回true时，JUnit Platform才会调用resolveParameter()。

### 3.2 InvalidPersonParameterResolver

```java
public class InvalidPersonParameterResolver implements ParameterResolver {

    public static Person[] INVALID_PERSONS = {
            new Person().setId(1L).setLastName("Ad_ams").setFirstName("Jill,"),
            new Person().setId(2L).setLastName(",Baker").setFirstName(""),
            new Person().setId(3L).setLastName(null).setFirstName(null),
            new Person().setId(4L).setLastName("Daniel&").setFirstName("{Joseph}"),
            new Person().setId(5L).setLastName("").setFirstName("English, Jane"),
            new Person()/* .setId(6L).setLastName("Fontana").setFirstName("Enrique") */,
    };
}
```

注意Person类型的INVALID_PERSONS数组，就像ValidPersonParameterResolver一样，这个类包含一个无效Person数据的数组，供单元测试使用，以确保在存在无效数据时正确抛出PersonValidator.ValidationExceptions：

```java
public class InvalidPersonParameterResolver implements ParameterResolver {

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Object ret = null;
        if (parameterContext.getParameter().getType() == Person.class)
            ret = INVALID_PERSONS[new Random().nextInt(INVALID_PERSONS.length)];
        return ret;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == Person.class;
    }
}
```

```java
public class PersonValidator {
    private static final String[] ILLEGAL_NAME_CHARACTERS = {",", "_", "{", "}", "!"};

    public static boolean validateFirstName(Person person) throws ValidationException {
        boolean ret = true;
        if (person == null)
            throw new ValidationException("Person is null (not allowed)!");
        if (person.getFirstName() == null)
            throw new ValidationException("Person FirstName is null (not allowed)!");
        if (person.getFirstName().isEmpty())
            throw new ValidationException("Person FirstName is an empty String (not allowed)!");
        if (!isStringValid(person.getFirstName(), ILLEGAL_NAME_CHARACTERS))
            throw new ValidationException("Person FirstName (" + person.getFirstName() + ") may not contain any of the following characters: " + Arrays.toString(ILLEGAL_NAME_CHARACTERS) + "!");
        return ret;
    }

    public static boolean validateLastName(Person person) throws ValidationException {
        boolean ret = true;
        if (person == null)
            throw new ValidationException("Person is null (not allowed)!");
        if (person.getFirstName() == null)
            throw new ValidationException("Person FirstName is null (not allowed)!");
        if (person.getFirstName().isEmpty())
            throw new ValidationException("Person FirstName is an empty String (not allowed)!");
        if (!isStringValid(person.getFirstName(), ILLEGAL_NAME_CHARACTERS))
            throw new ValidationException("Person LastName (" + person.getLastName() + ") may not contain any of the following characters: " + Arrays.toString(ILLEGAL_NAME_CHARACTERS) + "!");
        return ret;
    }

    private static boolean isStringValid(String candidate, String[] illegalCharacters) {
        boolean ret = true;
        for (String illegalChar : illegalCharacters) {
            if (candidate.contains(illegalChar)) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    public static class ValidationException extends Exception {
        @Serial
        private static final long serialVersionUID = -134518049431883102L;

        public ValidationException(String message) {
            super(message);
        }
    }
}
```

## 4. 声明并使用Extension

现在我们为PersonValidator创建一个名为PersonValidatorUnitTest的JUnit测试类。

我们将使用仅在JUnit Jupiter中可用的几个功能：

+ @DisplayName：这是测试报告中显示的名称，并且更具人类可读性。
+ @Nested：创建一个嵌套的测试类，具有自己的测试生命周期，与其外层类分开。
+ @RepeatedTest：可以使用value属性指定测试要重复执行的次数(每个示例中为10)。

通过使用@Nested标注一个类，我们能够在同一个测试类中测试有效和无效的数据，同时让它们完全隔离：

```java
@DisplayName("Testing PersonValidator")
class PersonValidatorUnitTest {

    @Nested
    @DisplayName("When using Valid data")
    @ExtendWith(ValidPersonParameterResolver.class)
    class ValidDataTest {

        @RepeatedTest(value = 10)
        @DisplayName("All first name are valid")
        void validateFirstName(Person person) {
            try {
                assertTrue(PersonValidator.validateFirstName(person));
            } catch (ValidationException e) {
                fail("Exception not expected: " + e.getLocalizedMessage());
            }
        }

        @RepeatedTest(value = 10)
        @DisplayName("All last name are valid")
        void validateLastName(Person person) {
            try {
                assertTrue(PersonValidator.validateLastName(person));
            } catch (ValidationException e) {
                fail("Exception not expected: " + e.getLocalizedMessage());
            }
        }
    }

    @Nested
    @DisplayName("When using Invalid data")
    @ExtendWith(InvalidPersonParameterResolver.class)
    class InvalidDataTest {

        @RepeatedTest(value = 10)
        @DisplayName("All first name are invalid")
        void validateFirstName(Person person) {
            assertThrows(ValidationException.class, () -> PersonValidator.validateFirstName(person));
        }

        @RepeatedTest(value = 10)
        @DisplayName("All last name are invalid")
        void validateLastName(Person person) {
            assertThrows(ValidationException.class, () -> PersonValidator.validateLastName(person));
        }
    }
}
```

通过在一个主测试类中使用@Nested注解标注两个内部测试类，我们可以单独在两个类上分别使用ValidPersonParameterResolver和InvalidPersonParameterResolver Extension，这是在Junit 4中不可能实现的功能。

## 5. 总结

在本文中，我们实现了两个ParameterResolver Extension，分别提供有效和无效的Person对象。然后我们演示了如何在单元测试中使用这两个ParameterResolver实现。