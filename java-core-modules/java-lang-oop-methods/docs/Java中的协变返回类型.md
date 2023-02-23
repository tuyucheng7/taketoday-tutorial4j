## 1. 概述

在本教程中，我们将仔细研究Java中的协变返回类型。在从返回类型的角度检查协变之前，让我们看看这意味着什么。

## 2. 协变

**协变可以被视为当仅定义超类型时如何接受子类型的契约**。

让我们考虑几个协变的基本示例：

```java
List<? extends Number> integerList = new ArrayList<Integer>();
List<? extends Number> doubleList = new ArrayList<Double>();
```

**所以协变意味着，我们可以访问通过它们的超类型定义的特定元素**。但是，**我们不允许将元素放入协变系统中**，因为编译器无法确定泛型结构的实际类型。

## 3. 协变返回类型

**协变返回类型是-当我们覆盖一个方法时允许返回类型是被覆盖方法类型的子类型**。

为了将其付诸实践，让我们使用一个带有produce()方法的简单Producer类。默认情况下，它返回一个String作为Object来为子类提供灵活性：

```java
public class Producer {
    public Object produce(String input) {
        Object result = input.toLowerCase();
        return result;
    }
}
```

由于将Object作为返回类型，我们可以在子类中有一个更具体的返回类型。这将是协变返回类型，并将从字符序列中生成数字：

```java
public class IntegerProducer extends Producer {
    @Override
    public Integer produce(String input) {
        return Integer.parseInt(input);
    }
}
```

## 4. 结构的使用

协变返回类型背后的主要思想是支持[里氏替换](https://www.baeldung.com/solid-principles#l)。

例如，让我们考虑以下生产者场景：

```java
@Test
public void whenInputIsArbitrary_thenProducerProducesString() {
    String arbitraryInput = "just a random text";
    Producer producer = new Producer();

    Object objectOutput = producer.produce(arbitraryInput);

    assertEquals(arbitraryInput, objectOutput);
    assertEquals(String.class, objectOutput.getClass());
}
```

改成IntegerProducer后，实际产生结果的业务逻辑可以保持不变：

```java
@Test
public void whenInputIsSupported_thenProducerCreatesInteger() {
    String integerAsString = "42";
    Producer producer = new IntegerProducer();

    Object result = producer.produce(integerAsString);

    assertEquals(Integer.class, result.getClass());
    assertEquals(Integer.parseInt(integerAsString), result);
}
```

但是，我们仍然通过对象引用结果。每当我们开始使用对IntegerProducer的显式引用时，我们都可以在不向下转换的情况下将结果作为Integer检索：

```java
@Test
public void whenInputIsSupported_thenIntegerProducerCreatesIntegerWithoutCasting() {
    String integerAsString = "42";
    IntegerProducer producer = new IntegerProducer();

    Integer result = producer.produce(integerAsString);

    assertEquals(Integer.parseInt(integerAsString), result);
}
```

**一个众所周知的场景是Object#clone方法，该方法默认返回一个Object。每当我们覆盖clone()方法时，协变返回类型的功能允许我们拥有比Object本身更具体的返回对象**。

## 5. 总结

在本文中，我们了解了协变和协变返回类型是什么以及它们在Java中的行为方式。