## 1. 概述

Mockito是一个广泛使用的Java Mock框架。有了它，创建mock对象、配置mock行为、捕获方法参数以及验证与mock的交互都很简单。

在本文中，我们重点介绍如何指定mock行为。
我们有两种方法可以做到这一点：分别是when().thenDoSomething()和doSomething().when()语法。

## 2. when()

让我们考虑以下Employee接口：

```java
public interface Employee {
    String greet();

    void work(DayOfWeek day);
}
```

在我们的测试中，我们使用这个接口的mock。假设我们想配置mock的greet()方法以返回字符串“Hello”。使用Mockito的when()方法的例子如下所示：

```java
class WhenVsDoMethodsUnitTest {

    @Mock
    private Employee employee;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenNonVoidMethod_callingWhen_shouldConfigureBehavior() {
        // given
        when(employee.greet()).thenReturn("Hello");

        // when
        String greeting = employee.greet();

        // then
        assertThat(greeting, is("Hello"));
    }
}
```

employee对象是一个mock对象。**当我们调用它的任何方法时，Mockito会注册该调用。
通过调用when()方法，Mockito知道这个调用不是业务逻辑的交互。
这是我们想要为mock对象分配一些行为的声明。之后，使用thenXxx()方法之一，我们指定预期的行为**。

到目前为止，我们的mock配置正常工作。同样，当我们使用Sunday参数调用work()方法时，我们希望将其配置为抛出异常：

```java
class WhenVsDoMethodsUnitTest {

    @Test
    void givenVoidMethod_callingWhen_wontCompile() {
        // given
        when(employee.work(DayOfWeek.SUNDAY)).thenThrow(new IAmOnHolidayException());

        // when
        Executable workCall = () -> employee.work(DayOfWeek.SUNDAY);

        // then
        assertThrows(IAmOnHolidayException.class, workCall);
    }
}
```

**不幸的是，这段代码无法编译，因为在work(employee.work(...))调用中，work()方法的返回类型为void；
因此我们不能将它封装到另一个方法调用中**。这是否意味着我们不能mock void方法？当然可以，接下来让我们看看如何实现这一点。

### 3. doXxx()

让我们看看如何使用doThrow()方法配置异常抛出：

```java
class WhenVsDoMethodsUnitTest {

    @Test
    void givenVoidMethod_callingDoThrow_shouldConfigureBehavior() {
        // given
        doThrow(new IAmOnHolidayException()).when(employee).work(DayOfWeek.SUNDAY);

        // when
        Executable workCall = () -> employee.work(DayOfWeek.SUNDAY);

        // then
        assertThrows(IAmOnHolidayException.class, workCall);
    }
}
```

这种语法与前一种略有不同：我们没有将返回值为void的方法调用包装在另一个方法调用中。因此，这段代码可以编译通过。

首先，**我们在doThrow(...)中声明要抛出的异常类型。
接下来，我们调用了when()方法，并传递了mock对象。之后，我们指定了我们想要配置的mock交互行为**。

请注意，这与我们之前使用的when()方法不同。另外，我们在调用when()之后链接了mock交互。同时，我们用第一种语法在括号内定义了它。

为什么我们需要when().thenXxx()这种语法呢，当它不能完成像配置void调用这样的常见任务时，它与doXxx().when()语法相比具有多个优点。

**首先，对于开发人员来说，编写和阅读诸如“当一些交互时，然后做一些事情”之类的语句比“做一些事，当一些交互时”更合乎逻辑**。

其次，我们可以通过链接将多个行为添加到同一个交互中。
那是因为when()返回OngoingStubbing<T\>类的一个实例，而thenXxx()方法返回相同的类型。

另一方面，doXxx()方法返回一个Stubber实例，而Stubber.when(T mock)返回T，因此我们可以指定我们要配置什么样的方法调用。
但是T是我们应用程序的一部分，例如我们的代码片段中的Employee。而T不会返回Mockito类，因此我们无法通过链接添加多个行为。

## 4. BDDMockito

BDDMockito使用了与我们上面提到的另一种不同语法。这很简单：在我们的mock配置中，我们必须将关键字“when”替换为“given”，将关键字“do”替换为“will”。
除此之外，我们的代码保持不变：

```java
class WhenVsDoMethodsUnitTest {

    @Test
    void givenNonVoidMethod_callingGiven_shouldConfigureBehavior() {
        // given
        given(employee.greet()).willReturn("Hello");

        // when
        String greeting = employee.greet();

        // then
        assertThat(greeting, is("Hello"));
    }

    @Test
    void givenVoidMethod_callingWillThrow_shouldConfigureBehavior() {
        // given
        willThrow(new IAmOnHolidayException()).given(employee).work(DayOfWeek.SUNDAY);

        // when
        Executable workCall = () -> employee.work(DayOfWeek.SUNDAY);

        // then
        assertThrows(IAmOnHolidayException.class, workCall);
    }
}
```

## 5. 总结

我们介绍了使用when().thenXxx()或doXxx().when()方式配置mock对象的优缺点。
此外，我们还了解了这些语法是如何使用的，以及为什么Mockito需要为我们提供这两种语法。