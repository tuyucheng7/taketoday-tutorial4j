## 1. 概述

一些流行的Mock库(例如[Mockito和Easymock](https://www.baeldung.com/mockito-vs-easymock-vs-jmockit))通过利用Java基于继承的类模型来生成mock。**EasyMock在运行时实现一个接口，而Mockito从目标类继承来创建一个mock存根**。

这两种方法都不适用于静态方法，因为静态方法与类相关联并且不能被重写。但是，**JMockit确实提供了mock静态方法的功能**。

在本教程中，我们将探讨其中的一些功能。

有关JMockit的介绍，请参阅我们[之前的文章](https://www.baeldung.com/jmockit-101)。

## 2. Maven依赖

让我们从Maven依赖项开始：

```xml
<dependency>
    <groupId>org.jmockit</groupId>
    <artifactId>jmockit</artifactId>
    <version>1.41</version>
    <scope>test</scope>
</dependency>
```

你可以在[Maven Central](https://central.sonatype.com/artifact/org.jmockit/jmockit/1.49)上找到这些库的最新版本。

## 3. 从非静态方法调用的静态方法 

首先，让我们考虑一种情况，当我们有一个类**具有内部依赖于静态方法的非静态方法**时：

```java
public class AppManager {

    public boolean managerResponse(String question) {
        return AppManager.isResponsePositive(question);
    }

    public static boolean isResponsePositive(String value) {
        if (value == null)
            return false;
        int orgLength = value.length();
        int randomNumber = randomNumber();
        return orgLength == randomNumber;
    }

    private static int randomNumber() {
        return new Random().nextInt(7);
    }

    private static Integer stringToInteger(String num) {
        return Integer.parseInt(num);
    }
}
```

现在，我们要测试方法managerResponse()。由于它的返回值取决于另一个方法，我们需要mock isResponsePositive()方法。

我们可以使用JMockit的匿名类mockit.MockUp.MockUp<T\>(其中T是类名)和@Mock注解来mock这个静态方法：

```java
@Test
void givenAppManager_whenStaticMethodCalled_thenValidateExpectedResponse() {
	new MockUp<AppManager>() {
		@Mock
		public boolean isResponsePositive(String value) {
			return false;
		}
	};
    
	assertFalse(appManager.managerResponse("Why are you coming late?"));
}
```

在这里，我们使用我们希望用于测试的返回值来mock isResponsePositive()，因此，使用JUnit 5中可用的[Assertions](https://www.baeldung.com/junit-5-preview#new)工具方法验证预期结果。 

## 4. 测试私有静态方法

在少数情况下，其他方法可能会使用到类的私有静态方法：

```java
private static Integer stringToInteger(String num) {
    return Integer.parseInt(num);
}
```

为了测试这种方法，**我们需要mock私有静态方法**。我们可以使用JMockit提供的Deencapsulation.invoke()工具方法：

```java
@Test
void givenAppManager_whenPrivateStaticMethod_thenValidateExpectedResponse() {
    int response = Deencapsulation.invoke(AppManager.class, "stringToInteger", "110");
    assertEquals(110, response);
}
```

顾名思义，它的目的是解封装对象的状态。通过这种方式，JMockit简化了无法以其他方式测试的测试方法。

## 5. 总结

在本文中，我们了解了如何使用JMockit mock静态方法。要更深入地了解JMockit的一些高级功能，请查看我们的JMockit高级用法[文章](https://www.baeldung.com/jmockit-advanced-usage)。