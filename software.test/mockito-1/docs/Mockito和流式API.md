## 1. 概述

Fluent API是一种基于方法链的软件工程设计技术，用于构建简洁、易读的接口。

它们通常用于建造者、工厂和其他创建型的设计模式。
近年来，随着Java的发展，它们变得越来越流行，并且可以在很多地方看到它们的出现，例如Java Stream API和Mockito测试框架。

**然而，mock Fluent API可能会很痛苦，因为我们经常需要建立mock对象的复杂层次结构**。

在本教程中，我们将看看如何使用Mockito的强大功能来避免这种情况。

## 2. 一个简单的Fluent API

**在本文中，我们将使用建造者设计模式演示一个用于构建Pizza对象的简单fluent API**:

```java
public class Pizza {
    private final String name;
    private final PizzaSize size;
    private final List<String> toppings;
    private final boolean stuffedCrust;
    private final boolean collect;
    private final Integer discount;

    private Pizza(PizzaBuilder builder) {
        this.name = builder.name;
        this.size = builder.size;
        this.toppings = builder.toppings;
        this.stuffedCrust = builder.stuffedCrust;
        this.collect = builder.collect;
        this.discount = builder.discount;
    }

    // getters and setters ...

    public enum PizzaSize {
        LARGE, MEDIUM, SMALL
    }

    public static class PizzaBuilder {
        private String name;
        private PizzaSize size;
        private List<String> toppings;
        private boolean stuffedCrust;
        private boolean collect;
        private Integer discount = null;
        // build methods ...
    }
}
```

正如我们所见，我们创建了一个易于理解的API，它读起来像DSL，并允许我们创建具有各种特征的Pizza对象。

现在我们将定义一个使用构建器的简单Service类。这将是我们稍后要测试的类：

```java
public class PizzaService {
    private Pizza.PizzaBuilder builder;

    public PizzaService(Pizza.PizzaBuilder builder) {
        this.builder = builder;
    }

    public Pizza orderHouseSpecial() {
        return builder.name("Special")
                .size(PizzaSize.LARGE)
                .withExtraTopping("Mushrooms")
                .withStuffedCrust(true)
                .withExtraTopping("Chilli")
                .willCollect(true)
                .applyDiscount(20)
                .build();
    }
}
```

我们的Service非常简单，包含一个名为orderHouseSpecial()的方法。顾名思义，我们可以使用这个方法来构建具有一些预定义属性的特殊Pizza。

## 3. 传统mock

以传统方式使用mock进行stubbing需要创建八个PizzaBuilder mock对象。
我们需要一个由name方法返回的PizzaBuilder的mock，然后是一个由size方法返回的PizzaBuilder的mock，等等。
我们将继续这种方式，直到我们满足流式API链中的所有方法调用。

现在让我们看看如何编写一个单元测试来使用传统的Mockito mock来测试我们的Service方法：

```java

@ExtendWith(MockitoExtension.class)
class PizzaServiceUnitTest {
    @Mock
    private Pizza expectedPizza;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Pizza.PizzaBuilder anotherBuilder;

    @Captor
    private ArgumentCaptor<String> stringCaptor;
    @Captor
    private ArgumentCaptor<Pizza.PizzaSize> sizeCaptor;

    @Test
    void givenTraditionalMocking_whenServiceInvoked_thenPizzaIsBuilt() {
        Pizza.PizzaBuilder nameBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
        Pizza.PizzaBuilder sizeBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
        Pizza.PizzaBuilder firstToppingBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
        Pizza.PizzaBuilder secondToppingBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
        Pizza.PizzaBuilder stuffedBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
        Pizza.PizzaBuilder willCollectBuilder = Mockito.mock(Pizza.PizzaBuilder.class);
        Pizza.PizzaBuilder discountBuilder = Mockito.mock(Pizza.PizzaBuilder.class);

        Pizza.PizzaBuilder builder = Mockito.mock(Pizza.PizzaBuilder.class);
        when(builder.name(anyString())).thenReturn(nameBuilder);
        when(nameBuilder.size(any(Pizza.PizzaSize.class))).thenReturn(sizeBuilder);
        when(sizeBuilder.withExtraTopping(anyString())).thenReturn(firstToppingBuilder);
        when(firstToppingBuilder.withStuffedCrust(anyBoolean())).thenReturn(stuffedBuilder);
        when(stuffedBuilder.withExtraTopping(anyString())).thenReturn(secondToppingBuilder);
        when(secondToppingBuilder.willCollect(anyBoolean())).thenReturn(willCollectBuilder);
        when(willCollectBuilder.applyDiscount(anyInt())).thenReturn(discountBuilder);
        when(discountBuilder.build()).thenReturn(expectedPizza);

        PizzaService service = new PizzaService(builder);
        assertEquals(expectedPizza, service.orderHouseSpecial(), "Expected Pizza");

        verify(builder).name(stringCaptor.capture());
        assertEquals("Pizza name: ", "Special", stringCaptor.getValue());

        verify(nameBuilder).size(sizeCaptor.capture());
        assertEquals(Pizza.PizzaSize.LARGE, sizeCaptor.getValue(), "Pizza size: ");
    }
}
```

在这个例子中，我们需要模拟我们提供给PizzaService的PizzaBuilder。
正如我们所看到的，这不是一个简单的任务，因为我们需要返回一个mock，它将为我们流式API中的每个调用返回一个mock。

这导致了复杂的mock对象层次结构，难以理解并且难以维护。

## 4. Deep Stubbing

**值得庆幸的是，Mockito提供了一个非常简洁的功能，称为deep stubbing，它允许我们在创建mock时指定Answer**。

要创建一个deep stubbing，我们只需在创建mock时添加Mockito.RETURNS_DEEP_STUBS常量作为附加参数：

```java
class PizzaServiceUnitTest {

    @Test
    void givenDeepStubs_whenServiceInvoked_thenPizzaIsBuilt() {
        Mockito.when(anotherBuilder.name(anyString())
                        .size(any(Pizza.PizzaSize.class))
                        .withExtraTopping(anyString())
                        .withStuffedCrust(anyBoolean())
                        .withExtraTopping(anyString())
                        .willCollect(anyBoolean())
                        .applyDiscount(anyInt())
                        .build())
                .thenReturn(expectedPizza);

        PizzaService service = new PizzaService(anotherBuilder);
        assertEquals(expectedPizza, service.orderHouseSpecial(), "Expected Pizza");
    }
}
```

通过使用Mockito.RETURNS_DEEP_STUBS参数，我们告诉Mockito进行一种deep mock，这使得可以一次性mock完整方法链的结果。

这种更优雅的解决方案比我们在上一节中看到的测试更容易理解。从本质上讲，我们避免了创建复杂的mock对象层次结构的需要。

我们也可以直接通过@Mock注解使用这种Answer mode：

```text
@Mock(answer = Answers.RETURNS_DEEP_STUBS)
private PizzaBuilder anotherBuilder;
```

**需要注意的一点是，验证仅适用于链中的最后一个mock**。

## 5. 总结

在这个教程中，我们看到了如何使用Mockito来mock一个简单的流式API。首先，我们给出了一种传统的mock方法，演示了它的缺点。

然后我们给出一个使用Mockito中deep stubbing的示例，它允许以更优雅的方式mock我们的流式API。