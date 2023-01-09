## 1. 概述

通常，Mockito为我们的mock对象提供的默认设置应该足够我们使用。

但是，**在某些情况下，我们可能需要在创建mock时提供额外的mock设置**。这在调试、处理遗留代码或涉及某些边缘情况时可能很有用。

在之前的教程中，我们学习了如何使用lenient mocks。在这个教程中，我们将学习如何使用MockSettings接口提供的一些其他有用的功能。

## 2. MockSettings

简单地说，MockSettings接口提供了一个Fluent API，允许我们在创建mock时轻松添加和组合其他mock设置。

当我们创建一个mock对象时，所有的mock都带有一组默认设置。让我们看一个简单的mock示例：

```text
List mockedList = mock(List.class);
```

在背后，Mockito mock方法委托给另一个重载方法，并为我们的mock提供了一组默认设置：

```text
public static <T> T mock(Class<T> classToMock) {
    return mock(classToMock, withSettings());
}
```

让我们看看Mockito的默认设置：

```text
public static MockSettings withSettings() {
    return new MockSettingsImpl().defaultAnswer(RETURNS_DEFAULTS);
}
```

正如我们所见，mock对象的标准设置非常简单。我们为mock交互配置默认Answer。通常，使用RETURNS_DEFAULTS将返回一些空值。

**重要的一点是，如果需要，我们可以为我们的mock对象提供我们自己的一组自定义设置**。

## 3. 提供不同的默认Answer

现在我们对mock设置有了基本了解，让我们看看如何更改mock对象的默认返回值。

假设我们有一个非常简单的mock构建：

```text
PizzaService service = mock(PizzaService.class);
Pizza pizza = service.orderHouseSpecial();
PizzaSize size = pizza.getSize();
```

当我们按预期运行此代码时，我们将得到NullPointerException，因为我们没有对orderHouseSpecial方法进行stubbing，因此它会返回null。

这是可以的，但有时在处理遗留代码时，我们可能需要处理复杂的mock对象层次结构，并且定位这些类型的异常发生的确切位置可能很耗时。

为了帮助我们解决这个问题，**我们可以在mock创建期间通过mock设置提供不同的默认Answer**：

```text
PizzaService pizzaService = mock(PizzaService.class, withSettings().defaultAnswer(RETURNS_SMART_NULLS));
```

通过使用RETURNS_SMART_NULLS作为我们的默认Answer，Mockito为我们提供了一个更有意义的错误消息，它可以准确地显示错误stubbing发生的位置：

```text
org.mockito.exceptions.verification.SmartNullPointerException: 
You have a NullPointerException here:
-> at cn.tuyucheng.taketoday.mockito.mocksettings.MockSettingsUnitTest.whenServiceMockedWithSmartNulls_thenExceptionHasExtraInfo(MockSettingsUnitTest.java:45)
because this method call was *not* stubbed correctly:
-> at cn.tuyucheng.taketoday.mockito.mocksettings.MockSettingsUnitTest.whenServiceMockedWithSmartNulls_thenExceptionHasExtraInfo(MockSettingsUnitTest.java:44)
pizzaService.orderHouseSpecial();
```

这可以在调试测试代码时为我们节省一些时间。Answers枚举还提供了一些其他的预配置mock Answer：

+ RETURNS_DEEP_STUBS - **返回deep stubs的Answer，这在使用Fluent API时非常有用**。
+ RETURNS_MOCKS - 使用这个Answer将返回普通值，例如空集合或空字符串，然后，它将尝试返回mock。
+ CALLS_REAL_METHODS - 顾名思义，当我们使用这个实现时，unstubbed方法将委托给真正的实现。

## 4. 命名mock和详细日志记录

我们可以使用MockSettings的name方法为我们的mock起一个名字。这对于调试特别有用，因为我们提供的名称用于所有验证错误：

```text
PizzaService service = mock(PizzaService.class, withSettings().name("pizzaServiceMock").verboseLogging());
```

在本例中，我们使用verboseLogging()方法将命名功能与详细日志记录相结合。

**使用此方法可以实时记录到此mock上的方法调用的标准输出流**。同样，它可以在测试调试期间使用，以发现与mock的错误交互。

当我们运行测试时，我们会在控制台上看到一些输出：

```text
pizzaServiceMock.orderHouseSpecial();
   invoked: -> at cn.tuyucheng.taketoday.mockito.mocksettings.MockSettingsUnitTest.whenServiceMockedWithNameAndVerboseLogging_thenLogsMethodInvocations(MockSettingsUnitTest.java:37)
   has returned: "Mock for Pizza, hashCode: 1262237002" (cn.tuyucheng.taketoday.mockito.fluentapi.Pizza$MockitoMock$185391651)
```

有趣的是，如果我们使用@Mock注解，我们的mock会自动将字段名作为mock名称。

## 5. mock额外接口

有时，我们可能想要指定我们的mock应该实现的额外接口。同样，这在处理我们无法重构的遗留代码时可能很有用。

假设我们有一个特殊的接口：

```java
public interface SpecialInterface {

}
```

以及使用此接口的类：

```java
public class SimpleService {

    public SimpleService(SpecialInterface special) {
        Runnable runnable = (Runnable) special;
        runnable.run();
    }
}
```

当然，这不是完整的代码，但如果我们被迫为此编写单元测试，我们很可能会遇到问题：

```text
SpecialInterface specialMock = mock(SpecialInterface.class);
SimpleService service = new SimpleService(specialMock);
```

当我们运行这段代码时，我们会得到一个ClassCastException。
**为了解决这个问题，我们可以使用extraInterfaces方法创建具有多种类型的mock**：

```text
SpecialInterface specialMock = mock(SpecialInterface.class, withSettings().extraInterfaces(Runnable.class));
```

现在，我们的mock创建代码不会失败，但我们应该强调的是，强制转换为未声明的类型并不建议。

## 6. 提供构造函数参数

在最后一个例子中，我们介绍如何使用MockSettings调用具有参数值的真实构造函数：

```java

@ExtendWith(MockitoExtension.class)
class MockSettingsUnitTest {

    @Test
    void whenMockSetupWithConstructor_thenConstructorIsInvoked() {
        AbstractCoffee coffeeSpy = mock(AbstractCoffee.class, withSettings().useConstructor("espresso").defaultAnswer(CALLS_REAL_METHODS));

        assertEquals("espresso", coffeeSpy.getName(), "Coffee name: ");
    }
}

public abstract class AbstractCoffee {

    protected String name;

    protected AbstractCoffee(String name) {
        this.name = name;
    }

    protected String getName() {
        return name;
    }
}
```

这一次，Mockito在创建AbstractCoffee mock实例时尝试使用带有String值的构造函数。我们还将默认Answer配置为委托给实际实现。

**如果我们在构造函数中有一些逻辑要测试或触发，以使类处于某种特定状态，那么这可能会很有用**。
在spying抽象类时它也很有用。

## 7. 总结

在本教程中，我们了解了如何使用其他mock设置创建mock。

然而，我们强调，虽然这有时很有用并且可能是不可避免的，但在大多数情况下，我们应该使用简单的mock编写简单的测试。