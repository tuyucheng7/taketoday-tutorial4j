## 1. 概述

在这个简短的教程中，我们将学习如何在Java中快速将字符串转换为枚举。

## 2.设置

我们正在处理核心 Java，因此我们不需要添加任何额外的工件。我们还将使用枚举[指南](https://www.baeldung.com/a-guide-to-java-enums)文章中的PizzaDeliveryStatusEnum 。

## 3. 皈依

枚举类似于标准的Java类，我们可以使用点表示法访问它们的值。因此，要访问PizzaDeliveryStatusEnum的READY值，我们将使用：

```java
PizzaStatusEnum readyStatus = PizzaStatusEnum.READY;
```

这很好，但是如果我们将状态值存储为String并想将其转换为PizzaStatusEnum怎么办？这样做的天真方法是编写一个巨大的switch语句，为枚举的每个可能值返回正确的枚举值。但是编写和维护这样的代码是一场噩梦，我们应该不惜一切代价避免它。

另一方面，枚举类型提供了一个valueOf()方法，该方法将String作为参数，并返回相应的枚举对象：

```java
PizzaStatusEnum readyStatus = PizzaStatusEnum.valueOf("READY");
```

我们可以通过单元测试来检查这种方法是否真的有效：

```java
@Test
public void whenConvertedIntoEnum_thenGetsConvertedCorrectly() {
 
    String pizzaEnumValue = "READY";
    PizzaStatusEnum pizzaStatusEnum
      = PizzaStatusEnum.valueOf(pizzaEnumValue);
    assertTrue(pizzaStatusEnum == PizzaStatusEnum.READY);
}
```

重要的是要记住valueOf()方法对提供给它的参数进行区分大小写的匹配，因此传递一个与任何原始enum值的大小写都不匹配的值将导致IllegalArgumentException：

```java
@Test(expected = IllegalArgumentException.class)
public void whenConvertedIntoEnum_thenThrowsException() {
    
    String pizzaEnumValue = "rEAdY";
    PizzaStatusEnum pizzaStatusEnum
      = PizzaStatusEnum.valueOf(pizzaEnumValue);
}

```

传递不属于原始enum的值的值也会导致IllegalArgumentException：

```java
@Test(expected = IllegalArgumentException.class)
public void whenConvertedIntoEnum_thenThrowsException() {
    String pizzaEnumValue = "invalid";
    PizzaStatusEnum pizzaStatusEnum = PizzaStatusEnum.valueOf(pizzaEnumValue);
}
```

## 4。总结

在这篇简短的文章中，我们说明了如何将String转换为enum。

我们强烈建议使用枚举类型的内置valueOf()方法，而不是自己进行转换。