## 1. 概述

长期以来，在mock框架的帮助下进行单元测试被认为是一种良好的实践，尤其是[Mockito](http://mockito.org/)框架近年来主导了这个市场。

为了促进体面的代码设计并使公共API变得简单，开发人员可能故意省略了一些所需的功能。然而，在某些情况下，这些缺点迫使测试人员编写繁琐的代码只是为了使mock的创建可行。

**这就是[PowerMock](https://github.com/jayway/powermock)框架发挥作用的地方**。

[PowerMockito](https://github.com/jayway/powermock/wiki/MockitoUsage)是PowerMock的扩展API，用于支持Mockito。它提供了以简单的方式使用Java反射API的功能，以克服Mockito的问题，例如缺乏mock final、static或private方法的能力。

本教程将介绍PowerMockito API，并了解如何在测试中应用它。

## 2. 为PowerMockito进行测试做准备

为Mockito集成PowerMock支持的第一步是在Maven POM文件中包含以下两个依赖项：

```xml
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-module-junit4</artifactId>
    <version>2.21.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-api-mockito</artifactId>
    <version>2.0.7</version>
    <scope>test</scope>
</dependency>
```

接下来，我们需要通过应用以下两个注解来准备使用PowerMockito的测试用例：

```java
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "cn.tuyucheng.taketoday.powermockito.introduction.*")
```

@PrepareForTest注解中的fullyQualifiedNames属性表示我们要mock的类的完全限定名数组。在这种情况下，我们使用带有通配符的包名称来告诉PowerMockito准备cn.tuyucheng.taketoday.powermockito.introduction包中的所有类以进行mock。

## 3. Mock构造函数和final方法

在本节中，我们将演示在使用new运算符实例化类时获取mock实例而不是真实实例的方法，然后使用该对象mock final方法。

下面是我们要用于mock的对象，其构造函数和final方法将被mock：

```java
public class CollaboratorWithFinalMethods {

    final String helloMethod() {
        return "Hello World!";
    }
}
```

首先，我们使用PowerMockito API创建一个mock对象：

```java
CollaboratorWithFinalMethods mock = mock(CollaboratorWithFinalMethods.class);
```

接下来，我们设置一个期望，即每当调用该类的无参构造函数时，都应该返回一个mock实例而不是一个真实的实例：

```java
whenNew(CollaboratorWithFinalMethods.class).withNoArguments().thenReturn(mock);
```

通过上面的构造mock，此时当我们new出一个CollaboratorWithFinalMethods的实例时，我们可以验证PowerMock的行为：

```java
CollaboratorWithFinalMethods collaborator = new CollaboratorWithFinalMethods();
verifyNew(CollaboratorWithFinalMethods.class).withNoArguments();
```

在下一步中，配置final方法helloMethod返回一个期望值：

```java
when(collaborator.helloMethod()).thenReturn("Hello Tuyucheng!");
```

然后执行此方法：

```java
String welcome = collaborator.helloMethod();
```

以下断言确认已在mock对象上调用了helloMethod方法并返回mock期望设置的值：

```java
verify(collaborator).helloMethod();
assertEquals("Hello Tuyucheng!", welcome);
```

如果我们想mock一个特定的final方法，而不是对象中的所有final方法，Mockito.spy(T object)方法可能会派上用场。我们在第5节中对此进行了说明。

## 4. Mock静态方法

假设我们有一个CollaboratorWithStaticMethods类，其中包含一些静态方法：

```java
class CollaboratorWithStaticMethods {

    static String firstMethod(String name) {
        return "Hello " + name + " !";
    }

    static String secondMethod() {
        return "Hello no one!";
    }

    static String thirdMethod() {
        return "Hello no one again!";
    }
}
```

为了mock这些静态方法，我们需要使用PowerMockito.mockStatic API注册封闭类：

```java
mockStatic(CollaboratorWithStaticMethods.class);
```

或者，我们可以使用Mockito.spy(Class<T\> class)方法来mock特定的方法，如下一节所示。

接下来，可以设置期望值来定义方法在调用时应返回的值：

```java
when(CollaboratorWithStaticMethods.firstMethod(Mockito.anyString())).thenReturn("Hello Tuyucheng!");
when(CollaboratorWithStaticMethods.secondMethod()).thenReturn("Nothing special");
```

或者可以设置在调用thirdMethod方法时抛出异常：

```java
doThrow(new RuntimeException()).when(CollaboratorWithStaticMethods.class);
CollaboratorWithStaticMethods.thirdMethod();
```

现在我们运行firstMethod方法：

```java
String firstWelcome = CollaboratorWithStaticMethods.firstMethod("Whoever");
String secondWelcome = CollaboratorWithStaticMethods.firstMethod("Whatever");
```

上面的调用被委托给mock的方法，而不是调用真实类的成员。

这些断言证明mock已经生效：

```java
assertEquals("Hello Tuyucheng!", firstWelcome);
assertEquals("Hello Tuyucheng!", secondWelcome);
```

我们还能够验证mock方法的行为，包括方法被调用的次数。

在这种情况下，firstMethod被调用了两次，而secondMethod从未被调用过：

```java
verifyStatic(CollaboratorWithStaticMethods.class, times(2));
CollaboratorWithStaticMethods.firstMethod(Mockito.anyString());

verifyStatic(CollaboratorWithStaticMethods.class, Mockito.never());
CollaboratorWithStaticMethods.secondMethod();
```

> **注意**：必须在任何静态方法验证之前调用verifyStatic方法，以便PowerMockito知道需要验证连续的方法调用。

最后，静态thirdMethod方法应该抛出一个RuntimeException，就像之前在mock上声明的那样。

它由@Test注解的expected属性验证：

```java
@Test(expected = RuntimeException.class)
public void givenStaticMethods_whenUsingPowerMockito_thenCorrect() {
    // other methods   
       
    CollaboratorWithStaticMethods.thirdMethod();
}
```

## 5. 部分Mock

除了mock整个类的行为，PowerMockito API还允许使用spy方法mock其中的一部分。

下面的这个类将用来演示PowerMock对部分mock的支持：

```java
class CollaboratorForPartialMocking {

    static String staticMethod() {
        return "Hello Tuyucheng!";
    }

    final String finalMethod() {
        return "Hello Tuyucheng!";
    }

    private String privateMethod() {
        return "Hello Tuyucheng!";
    }

    String privateMethodCaller() {
        return privateMethod() + " Welcome to the Java world.";
    }
}
```

让我们从mock一个静态方法开始，它在上面的类定义中被命名为staticMethod。

首先，我们使用PowerMockito API部分mock CollaboratorForPartialMocking类，并为其静态方法设置期望：

```java
String returnValue;

spy(CollaboratorForPartialMocking.class);
when(CollaboratorForPartialMocking.staticMethod()).thenReturn("I am a static mock method.");
```

然后执行静态方法：

```java
returnValue = CollaboratorForPartialMocking.staticMethod();
```

验证mock的行为：

```java
verifyStatic();
CollaboratorForPartialMocking.staticMethod();
```

以下断言通过将返回值与期望值进行比较来确认mock方法实际上已被调用：

```java
assertEquals("I am a static mock method.", returnValue);
```

然后我们mock对final和private方法的调用。为了说明这些方法的部分mock，我们需要实例化该类并告诉PowerMockito API来spy它：

```java
CollaboratorForPartialMocking collaborator = new CollaboratorForPartialMocking();
CollaboratorForPartialMocking mock = spy(collaborator);
```

上面创建的对象用于演示final方法和private方法的mock。

我们现在将通过设置期望并调用该方法来处理final方法：

```java
when(mock.finalMethod()).thenReturn("I am a final mock method.");
returnValue = mock.finalMethod();
```

以下语句证明了部分mock该方法的行为：

```java
Mockito.verify(mock).finalMethod();
```

测试验证调用finalMethod方法将返回与预期匹配的值：

```java
assertEquals("I am a final mock method.", returnValue);
```

类似的过程适用于private方法，主要的区别在于我们不能直接从测试用例中调用私有方法。

基本上，私有方法由同一类中的其他方法调用。在CollaboratorForPartialMocking类中，privateMethod方法由privateMethodCaller方法调用，我们将使用后者作为委托。

让我们从设置期望和调用方法开始：

```java
when(mock, "privateMethod").thenReturn("I am a private mock method.");
returnValue = mock.privateMethodCaller();
```

确认对私有方法的mock：

```java
verifyPrivate(mock).invoke("privateMethod");
```

以下断言确保调用私有方法的返回值与预期相同：

```java
assertEquals("I am a private mock method. Welcome to the Java world.", returnValue);
```

## 6. 总结

本文介绍了PowerMockito API，演示了它在解决开发人员在使用Mockito框架时遇到的一些问题的用途。