## 1. 概述

在本教程中，我们将简要介绍在Java中将int转换为[枚举值的不同方法。](https://www.baeldung.com/a-guide-to-java-enums)虽然没有直接的铸造方法，但有几种方法可以近似。

## 2.使用枚举#值

首先，让我们看看如何使用[Enum的值方法](https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html)来解决这个问题。

让我们从创建一个枚举PizzaStatus开始，它定义比萨饼订单的状态：

```java
public enum PizzaStatus {
    ORDERED(5),
    READY(2),
    DELIVERED(0);

    private int timeToDelivery;

    PizzaStatus (int timeToDelivery) {
        this.timeToDelivery = timeToDelivery;
    }

    // Method that gets the timeToDelivery variable.
}
```

我们将每个常量枚举值与timeToDelivery字段相关联。在定义常量枚举时，我们将timeToDelivery字段传递给构造函数。

[静态](https://www.baeldung.com/java-static)值方法返回一个数组，其中包含按声明顺序排列的枚举的所有值。因此，我们可以使用timeToDelivery整数值来获取对应的枚举值：

```java
int timeToDeliveryForOrderedPizzaStatus = 5;

PizzaStatus pizzaOrderedStatus = null;

for (PizzaStatus pizzaStatus : PizzaStatus.values()) {
    if (pizzaStatus.getTimeToDelivery() == timeToDeliveryForOrderedPizzaStatus) {
        pizzaOrderedStatus = pizzaStatus;
    }
}

assertThat(pizzaOrderedStatus).isEqualTo(PizzaStatus.ORDERED);
```

在这里，我们使用PizzaStatus.values()返回的数组 来查找基于timeToDelivery属性的匹配值。

然而，这种方法非常冗长。此外，它的效率也很低，因为每次我们想要获取相应的PizzaStatus时，我们都需要遍历PizzaStatus.values()。

### 2.1. 使用Java8流

让我们看看如何使用Java8 方法找到匹配的PizzaStatus ：

```java
int timeToDeliveryForOrderedPizzaStatus = 5;

Optional<PizzaStatus> pizzaStatus = Arrays.stream(PizzaStatus.values())
  .filter(p -> p.getTimeToDelivery() == timeToDeliveryForOrderedPizzaStatus)
  .findFirst();

assertThat(pizzaStatus).hasValue(PizzaStatus.ORDERED);
```

这段代码看起来比使用for循环的代码更简洁。然而，每次我们需要获得匹配的枚举时，我们仍然会迭代PizzaStatus.values() 。

另外请注意，在这种方法中，我们直接获取Optional<PizzaStatus>而不是PizzaStatus实例。

## 3.使用地图

接下来，让我们使用Java的Map数据结构以及values方法来获取与传递整数值的时间对应的枚举值。

在这种方法中，values方法在初始化 map 时只被调用一次。此外，由于我们使用的是地图，因此每次需要获取与交付时间相对应的枚举值时，我们都不需要遍历这些值。

我们在内部使用静态映射timeToDeliveryToEnumValuesMapping，它处理时间的映射以传递到其相应的枚举值。

此外，枚举类的值方法提供了所有枚举值。在静态块中，我们遍历枚举值数组，并将它们与相应的时间一起添加到映射中，以传递整数值作为键：

```java
private static Map<Integer, PizzaStatus> timeToDeliveryToEnumValuesMapping = new HashMap<>();

static {
    for (PizzaStatus pizzaStatus : PizzaStatus.values()) {
        timeToDeliveryToEnumValuesMapping.put(
          pizzaStatus.getTimeToDelivery(),
          pizzaStatus
        );
    }
}
```

最后，我们创建一个将timeToDelivery整数作为参数的静态方法。此方法使用静态映射timeToDeliveryToEnumValuesMapping返回相应的枚举值：

```java
public static PizzaStatus castIntToEnum(int timeToDelivery) {
    return timeToDeliveryToEnumValuesMapping.get(timeToDelivery);
}
```

通过使用静态映射和静态方法，我们获取与传递整数值的时间对应的枚举值。

## 4。总结

总之，我们研究了几种变通方法来获取与整数值相对应的枚举值。