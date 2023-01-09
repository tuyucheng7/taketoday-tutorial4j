## 1. 概述

在本教程中，我们将介绍Mockito API中标准静态方法mock()的各种用途。

下面的MyList类将被用作测试用例中要mock的collaborator(协作者)：

```java
public class MyList extends AbstractList<String> {

    @Override
    public String get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 1;
    }
}
```

## 2. 简单mock

mock()方法最简单的一个重载形式为接收一个要mock的类的Class对象：

```text
public static <T> T mock(Class<T> classToMock)
```

我们可以使用此方法mock一个类并配置方法调用的期望值：

```text
MyList listMock = mock(MyList.class);
when(listMock.add(anyString())).thenReturn(false);
```

然后我们在mock上执行该方法：

```text
boolean added = listMock.add(randomAlphabetic(6));
```

下面的代码确认我们在mock上调用了add()方法，调用返回一个与我们之前设置的期望值相匹配的值：

```text
verify(listMock).add(anyString());
assertThat(added, is(false));
```

## 3. 指定名称的mock

在本节中，我们介绍mock()方法的另一种重载，它提供了一个指定mock名称的参数：

```text
public static <T> T mock(Class<T> classToMock, String name)
```

一般来说，mock的名称与代码的执行无关。但是，在调试时它可能会有所帮助，因为我们可以使用mock的名称来定位验证错误。

可以确保验证失败引发的异常消息包含提供的mock名称。

在下面的代码中，我们将为MyList类创建一个mock并将其命名为myMock：

```text
MyList listMock = mock(MyList.class, "myMock");
```

然后我们配置mock上的方法交互，并调用它：

```text
when(listMock.add(anyString())).thenReturn(false);
listMock.add(randomAlphabetic(6));
```

这里我们故意创建一个失败的验证，该验证应该引发异常，并带有一条包含有关mock信息的消息：

```text
assertThatThrownBy(() -> verify(listMock,times(2)).add(anyString()))
    .isInstanceOf(TooFewActualInvocations.class)
    .hasMessageContaining("myMock.add");
```

以下是抛出的异常的消息:

```text
org.mockito.exceptions.verification.TooFewActualInvocations: 
myMock.add(<any string>);
Wanted 2 times:
-> at java.base/java.util.AbstractList.add(AbstractList.java:111)
But was 1 time:
-> at cn.tuyucheng.taketoday.mockito.MockitoMockUnitTest.whenUsingMockWithName_thenCorrect(MockitoMockUnitTest.java:36)
```

如我们所见，异常消息包含mock的名称，这对于在验证不成功的情况下定位错误很有用。

## 4. 使用Answer mock

在这里，我们演示另一个mock()重载的使用，其中我们将为mock在创建时对交互的Answer配置策略。
Mockito文档中此mock()方法的定义如下所示:

```text
public static <T> T mock(Class<T> classToMock, Answer defaultAnswer)
```

首先我们定义一个Answer接口的实现：

```java
private static class CustomAnswer implements Answer<Boolean> {

    @Override
    public Boolean answer(InvocationOnMock invocation) {
        return false;
    }
}
```

我们将使用上面的CustomAnswer类来创建mock:

```text
MyList listMock = mock(MyList.class, new CustomAnswer());
```

如果我们不对方法调用设置期望值，则由CustomAnswer类配置的默认值将发挥作用。为了证明这一点，我们跳过期望值的设置，直接执行方法：

```text
boolean added = listMock.add(randomAlphabetic(6));
```

以下验证和断言确认带有Answer参数的mock()方法按预期工作：

```text
verify(listMock).add(anyString());
assertThat(added, is(false));
```

## 5. 使用MockSettings进行mock

最后一种mock()方法是带有MockSettings类型参数的重载。我们使用这个重载方法来提供一个非标准的mock。

MockSettings接口的方法支持多种自定义设置，例如使用invocationListeners()在当前mock上注册方法调用的监听器，
使用serializable()配置序列化，使用spiedInstance()指定要spy的实例，
使用useConstructor将Mockito配置为在实例化mock时尝试使用构造函数，等等。

为了方便起见，我们重用上一节介绍的CustomAnswer类来创建定义默认Answer的MockSettings实现。

MockSettings对象由工厂方法实例化：

```text
MockSettings customSettings = withSettings().defaultAnswer(new CustomAnswer());
```

我们将在创建新mock时使用该MockSettings对象：

```text
MyList listMock = mock(MyList.class, customSettings);
```

与上一节类似，我们调用MyList实例的add方法，并验证带有MockSettings参数的mock方法是否按预期工作：

```text
boolean added = listMock.add(randomAlphabetic(6));
verify(listMock).add(anyString());
assertThat(added, is(false));
```

## 6. 总结

在本文中，我们详细介绍了Mockito中mock()方法各种重载的使用。