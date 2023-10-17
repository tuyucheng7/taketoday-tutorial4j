## 1. 概述

在本教程中，我们将了解什么是Java枚举、它们解决的问题以及它们的一些设计模式如何在实践中使用。

**Java 5首先引入了enum关键字**。它表示一种特殊类型的类，它总是扩展java.lang.Enum类。有关使用的官方文档，我们可以转到[文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Enum.html)。

以这种方式定义的常量使代码更具可读性，允许进行编译时检查，预先记录可接受值的列表，并避免由于传入无效值而导致的意外行为。

下面是一个快速简单的枚举示例，它定义了披萨订单的状态；订单状态可以是ORDERED、READY或DELIVERED：

```java
public enum PizzaStatus {
    ORDERED,
    READY,
    DELIVERED;
}
```

此外，枚举带有许多有用的方法，如果我们使用传统的public static final常量，我们将需要编写这些方法。

## 2. 自定义枚举方法

现在我们对什么是枚举以及如何使用它们有了基本的了解，我们将通过在枚举上定义一些额外的API方法来将我们之前的示例提升到一个新的水平：

```java
public class Pizza {
    private PizzaStatus status;
    public enum PizzaStatus {
        ORDERED,
        READY,
        DELIVERED;
    }

    public boolean isDeliverable() {
        if (getStatus() == PizzaStatus.READY) {
            return true;
        }
        return false;
    }

    // Methods that set and get the status variable.
}
```

## 3. 使用“==”运算符比较枚举类型

由于枚举类型确保JVM中只存在常量的一个实例，因此我们可以安全地使用“==”运算符来比较两个变量，就像我们在上面的示例中所做的那样。此外，“==”运算符提供编译时和运行时安全性。

首先，我们将在以下代码片段中查看**运行时安全性**，其中我们将使用“==”运算符来比较状态。任何一个值都可以为null，我们不会得到NullPointerException。相反，如果我们使用equals方法，我们将得到一个NullPointerException：

```java
if(testPz.getStatus().equals(Pizza.PizzaStatus.DELIVERED)); 
if(testPz.getStatus() == Pizza.PizzaStatus.DELIVERED);
```

至于**编译时安全**，让我们看一个例子，我们将通过使用equals方法比较不同类型的枚举来确定它是否相等。这是因为枚举的值和getStatus方法的值恰好是相同的；但是，从逻辑上讲，这种比较应该是错误的。我们通过使用“==”运算符来避免这个问题。

编译器会将比较标记为不兼容错误：

```java
if(testPz.getStatus().equals(TestColor.GREEN));
if(testPz.getStatus() == TestColor.GREEN);
```

## 4. 在Switch语句中使用枚举类型

我们也可以在switch语句中使用枚举类型：

```java
public int getDeliveryTimeInDays() {
    switch (status) {
        case ORDERED: return 5;
        case READY: return 2;
        case DELIVERED: return 0;
    }
    return 0;
}
```

## 5. 枚举中的字段、方法和构造函数

我们可以在枚举类型中定义构造函数、方法和字段，这使它们非常强大。

接下来，让我们通过实现从披萨订单的一个阶段到另一个阶段的转换来扩展上面的示例。我们将看到如何摆脱之前使用的if和switch语句：

```java
public class Pizza {

    private PizzaStatus status;
    public enum PizzaStatus {
        ORDERED (5){
            @Override
            public boolean isOrdered() {
                return true;
            }
        },
        READY (2){
            @Override
            public boolean isReady() {
                return true;
            }
        },
        DELIVERED (0){
            @Override
            public boolean isDelivered() {
                return true;
            }
        };

        private int timeToDelivery;

        public boolean isOrdered() {return false;}

        public boolean isReady() {return false;}

        public boolean isDelivered(){return false;}

        public int getTimeToDelivery() {
            return timeToDelivery;
        }

        PizzaStatus (int timeToDelivery) {
            this.timeToDelivery = timeToDelivery;
        }
    }

    public boolean isDeliverable() {
        return this.status.isReady();
    }

    public void printTimeToDeliver() {
        System.out.println("Time to delivery is " + this.getStatus().getTimeToDelivery());
    }

    // Methods that set and get the status variable.
}
```

下面的测试片段演示了它是如何工作的：

```java
@Test
public void givenPizaOrder_whenReady_thenDeliverable() {
    Pizza testPz = new Pizza();
    testPz.setStatus(Pizza.PizzaStatus.READY);
    assertTrue(testPz.isDeliverable());
}
```

## 6. EnumSet和EnumMap

### 6.1 EnumSet

EnumSet是专门用于Enum类型的Set实现。

与HashSet相比，由于使用了内部位向量表示，它是一组特定枚举常量的非常高效和紧凑的表示。它还为传统的基于int的“位标志”提供了一种类型安全的替代方案，使我们能够编写更易读和可维护的简洁代码。

EnumSet是一个抽象类，它有两个实现，RegularEnumSet和JumboEnumSet，其中一个是根据实例化时枚举中常量的数量来选择的。

因此，在大多数情况下(如子集、添加、删除和批量操作，如containsAll和removeAll)，每当我们想要使用枚举常量集合时，最好使用此集合，如果我们使用Enum.values()只想遍历所有可能的常量。

在下面的代码片段中，我们可以看到如何使用EnumSet创建常量子集：

```java
public class Pizza {

    private static EnumSet<PizzaStatus> undeliveredPizzaStatuses = EnumSet.of(PizzaStatus.ORDERED, PizzaStatus.READY);

    private PizzaStatus status;

    public enum PizzaStatus {
        // ...
    }

    public boolean isDeliverable() {
        return this.status.isReady();
    }

    public void printTimeToDeliver() {
        System.out.println("Time to delivery is " + this.getStatus().getTimeToDelivery() + " days");
    }

    public static List<Pizza> getAllUndeliveredPizzas(List<Pizza> input) {
        return input.stream().filter(
                    (s) -> undeliveredPizzaStatuses.contains(s.getStatus()))
              .collect(Collectors.toList());
    }

    public void deliver() {
        if (isDeliverable()) {
            PizzaDeliverySystemConfiguration.getInstance().getDeliveryStrategy()
                  .deliver(this);
            this.setStatus(PizzaStatus.DELIVERED);
        }
    }

    // Methods that set and get the status variable.
}
```

执行以下测试展示了Set接口的EnumSet实现的强大功能：

```java
@Test
public void givenPizaOrders_whenRetrievingUnDeliveredPzs_thenCorrectlyRetrieved() {
    List<Pizza> pzList = new ArrayList<>();
    Pizza pz1 = new Pizza();
    pz1.setStatus(Pizza.PizzaStatus.DELIVERED);

    Pizza pz2 = new Pizza();
    pz2.setStatus(Pizza.PizzaStatus.ORDERED);

    Pizza pz3 = new Pizza();
    pz3.setStatus(Pizza.PizzaStatus.ORDERED);

    Pizza pz4 = new Pizza();
    pz4.setStatus(Pizza.PizzaStatus.READY);

    pzList.add(pz1);
    pzList.add(pz2);
    pzList.add(pz3);
    pzList.add(pz4);

    List<Pizza> undeliveredPzs = Pizza.getAllUndeliveredPizzas(pzList); 
    assertTrue(undeliveredPzs.size() == 3); 
}
```

### 6.2 EnumMap

EnumMap是一种专门的Map实现，旨在与枚举常量一起用作键。与其对应的HashMap 相比，它是一种高效且紧凑的实现，在内部表示为数组：

```java
EnumMap<Pizza.PizzaStatus, Pizza> map;

```

让我们看一个如何在实践中使用它的例子：

```java
public static EnumMap<PizzaStatus, List<Pizza>> groupPizzaByStatus(List<Pizza> pizzaList) {
    EnumMap<PizzaStatus, List<Pizza>> pzByStatus = new EnumMap<PizzaStatus, List<Pizza>>(PizzaStatus.class);
    
    for (Pizza pz : pizzaList) {
        PizzaStatus status = pz.getStatus();
        if (pzByStatus.containsKey(status)) {
            pzByStatus.get(status).add(pz);
        } else {
            List<Pizza> newPzList = new ArrayList<Pizza>();
            newPzList.add(pz);
            pzByStatus.put(status, newPzList);
        }
    }
    return pzByStatus;
}
```

执行以下测试演示了Map接口的EnumMap实现的强大功能：

```java
@Test
public void givenPizaOrders_whenGroupByStatusCalled_thenCorrectlyGrouped() {
    List<Pizza> pzList = new ArrayList<>();
    Pizza pz1 = new Pizza();
    pz1.setStatus(Pizza.PizzaStatus.DELIVERED);

    Pizza pz2 = new Pizza();
    pz2.setStatus(Pizza.PizzaStatus.ORDERED);

    Pizza pz3 = new Pizza();
    pz3.setStatus(Pizza.PizzaStatus.ORDERED);

    Pizza pz4 = new Pizza();
    pz4.setStatus(Pizza.PizzaStatus.READY);

    pzList.add(pz1);
    pzList.add(pz2);
    pzList.add(pz3);
    pzList.add(pz4);

    EnumMap<Pizza.PizzaStatus,List<Pizza>> map = Pizza.groupPizzaByStatus(pzList);
    assertTrue(map.get(Pizza.PizzaStatus.DELIVERED).size() == 1);
    assertTrue(map.get(Pizza.PizzaStatus.ORDERED).size() == 2);
    assertTrue(map.get(Pizza.PizzaStatus.READY).size() == 1);
}
```

## 7. 使用枚举实现设计模式

### 7.1 单例模式

通常，使用单例模式实现一个类是非常重要的。枚举提供了一种实现单例的快速简便的方法。

此外，由于枚举类在底层实现了Serializable接口，因此JVM保证该类是单例。这与传统实现不同，在传统实现中，我们必须确保在反序列化期间不创建新实例。

在下面的代码片段中，我们看到了如何实现单例模式：

```java
public enum PizzaDeliverySystemConfiguration {
    INSTANCE;

    PizzaDeliverySystemConfiguration() {
        // Initialization configuration which involves
        // overriding defaults like delivery strategy
    }

    private PizzaDeliveryStrategy deliveryStrategy = PizzaDeliveryStrategy.NORMAL;

    public static PizzaDeliverySystemConfiguration getInstance() {
        return INSTANCE;
    }

    public PizzaDeliveryStrategy getDeliveryStrategy() {
        return deliveryStrategy;
    }
}
```

### 7.2 策略模式

通常，策略模式是通过具有由不同类实现的接口来编写的。

添加新策略意味着添加新的实现类。使用枚举，我们可以更轻松地实现这一目标，添加新的实现意味着只需定义另一个具有某种实现的实例。

下面的代码片段展示了如何实现策略模式：

```java
public enum PizzaDeliveryStrategy {
    EXPRESS {
        @Override
        public void deliver(Pizza pz) {
            System.out.println("Pizza will be delivered in express mode");
        }
    },
    NORMAL {
        @Override
        public void deliver(Pizza pz) {
            System.out.println("Pizza will be delivered in normal mode");
        }
    };

    public abstract void deliver(Pizza pz);
}
```

然后我们将以下方法添加到Pizza类：

```java
public void deliver() {
    if (isDeliverable()) {
        PizzaDeliverySystemConfiguration.getInstance().getDeliveryStrategy()
          .deliver(this);
        this.setStatus(PizzaStatus.DELIVERED);
    }
}
@Test
public void givenPizaOrder_whenDelivered_thenPizzaGetsDeliveredAndStatusChanges() {
    Pizza pz = new Pizza();
    pz.setStatus(Pizza.PizzaStatus.READY);
    pz.deliver();
    assertTrue(pz.getStatus() == Pizza.PizzaStatus.DELIVERED);
}
```

## 8. Java 8和枚举

我们可以在Java 8中重写Pizza类，看看getAllUndeliveredPizzas()和groupPizzaByStatus()方法如何通过使用lambda和Stream API变得如此简洁：

```java
public static List<Pizza> getAllUndeliveredPizzas(List<Pizza> input) {
    return input.stream().filter(
        (s) -> !deliveredPizzaStatuses.contains(s.getStatus()))
            .collect(Collectors.toList());
}


public static EnumMap<PizzaStatus, List<Pizza>> groupPizzaByStatus(List<Pizza> pzList) {
    EnumMap<PizzaStatus, List<Pizza>> map = pzList.stream().collect(
        Collectors.groupingBy(Pizza::getStatus,
        () -> new EnumMap<>(PizzaStatus.class), Collectors.toList()));
    return map;
}
```

## 9. 枚举的JSON表示

使用Jackson库，可以使用JSON表示枚举类型，就好像它们是POJO一样。在下面的代码片段中，我们将看到如何使用Jackson注解：

```java
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PizzaStatus {
    ORDERED (5){
        @Override
        public boolean isOrdered() {
            return true;
        }
    },
    READY (2){
        @Override
        public boolean isReady() {
            return true;
        }
    },
    DELIVERED (0){
        @Override
        public boolean isDelivered() {
            return true;
        }
    };

    private int timeToDelivery;

    public boolean isOrdered() {return false;}

    public boolean isReady() {return false;}

    public boolean isDelivered(){return false;}

    @JsonProperty("timeToDelivery")
    public int getTimeToDelivery() {
        return timeToDelivery;
    }

    private PizzaStatus (int timeToDelivery) {
        this.timeToDelivery = timeToDelivery;
    }
}
```

我们可以按如下方式使用Pizza和PizzaStatus：

```java
Pizza pz = new Pizza();
pz.setStatus(Pizza.PizzaStatus.READY);
System.out.println(Pizza.getJsonString(pz));
```

这将生成Pizza状态的以下JSON表示形式：

```json
{
    "status": {
        "timeToDelivery": 2,
        "ready": true,
        "ordered": false,
        "delivered": false
    },
    "deliverable": true
}
```

有关枚举类型的JSON序列化/反序列化(包括自定义)的更多信息，我们可以参考[Jackson–将枚举序列化为JSON对象](https://www.baeldung.com/jackson-serialize-enums)。

## 10. 总结

在本文中，我们探讨了Java枚举，从语言基础知识到更高级、更有趣的实际用例。