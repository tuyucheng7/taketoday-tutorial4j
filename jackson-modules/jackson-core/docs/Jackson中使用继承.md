## 1. 概述

在本文中，我们介绍在Jackson中使用类层次结构。

两个典型的用例是包含子类元数据和忽略从父类继承的属性，我们将描述这两种情况以及需要对子类型进行特殊处理的几种情况。

## 2. 包含子类型信息

在序列化和反序列化数据对象时，有两种添加类型信息的方法，即全局默认类型和每类注解。

### 2.1 全局默认类型

以下三个Java类用于说明类型元数据的全局包含。

Vehicle父类：

```java
public abstract class Vehicle {
    private String make;
    private String model;

    protected Vehicle(String make, String model) {
        this.make = make;
        this.model = model;
    }

    // no-arg constructor, getters and setters
}
```

Car子类：

```java
public class Car extends Vehicle {
    private int seatingCapacity;
    private double topSpeed;

    public Car(String make, String model, int seatingCapacity, double topSpeed) {
        super(make, model);
        this.seatingCapacity = seatingCapacity;
        this.topSpeed = topSpeed;
    }

    // no-arg constructor, getters and setters
}
```

Truck子类：

```java
public class Truck extends Vehicle {
    private double payloadCapacity;

    public Truck(String make, String model, double payloadCapacity) {
        super(make, model);
        this.payloadCapacity = payloadCapacity;
    }

    // no-arg constructor, getters and setters
}
```

通过在ObjectMapper对象上启用类型信息，全局默认类型允许只声明一次类型信息。然后该类型元数据将应用于所有指定的类型。因此，使用这种方法添加类型元数据非常方便，尤其是在涉及大量类时。缺点是它使用完全限定的Java类名称作为类型标识符，因此不适合与非Java系统交互，并且仅适用于几种预定义的类型。

上面显示的Vehicle结构用于填充Fleet类的vehicles实例：

```java
public class Fleet {
    private List<Vehicle> vehicles;
    
    // getters and setters
}
```

要嵌入类型元数据，我们需要在ObjectMapper对象上启用类型化功能，以便稍后用于数据对象的序列化和反序列化：

```java
ObjectMapper.activateDefaultTyping(PolymorphicTypeValidator ptv, ObjectMapper.DefaultTyping applicability, JsonTypeInfo.As includeAs)
```

参数PolymorphicTypeValidator用于根据指定的条件验证要反序列化的实际子类型是否有效。此外，applicability参数确定需要类型信息的类型，includeAs参数是类型元数据包含的机制。此外，还提供了activateDefaultTyping方法的另外两个变体：

-   ObjectMapper.activateDefaultTyping(PolymorphicTypeValidator ptv, ObjectMapper.DefaultTyping applicability)：允许调用者指定validator和applicability，同时使用WRAPPER_ARRAY作为includeAs的默认值。
-   ObjectMapper.activateDefaultTyping(PolymorphicTypeValidator ptv)：允许调用者指定validator，同时使用OBJECT_AND_NON_CONCRETE作为applicability的默认值和WRAPPER_ARRAY作为includeAs的默认值。

让我们看看它是如何工作的。首先，我们需要创建一个验证器validator：

```java
PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
  .allowIfSubType("cn.tuyucheng.taketoday.jackson.inheritance")
  .allowIfSubType("java.util.ArrayList")
  .build();
```

接下来，我们创建一个ObjectMapper对象，并使用上面的验证器激活它的默认类型：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
```

下一步是实例化和填充本小节开头介绍的模型类，稍后将在后续小节中重新使用执行此操作的代码。

```java
Car car = new Car("Mercedes-Benz", "S500", 5, 250.0);
Truck truck = new Truck("Isuzu", "NQR", 7500.0);

List<Vehicle> vehicles = new ArrayList<>();
vehicles.add(car);
vehicles.add(truck);

Fleet serializedFleet = new Fleet();
serializedFleet.setVehicles(vehicles);
```

这些填充的对象将被序列化：

```java
String jsonDataString = mapper.writeValueAsString(serializedFleet);
```

生成的JSON字符串为：

```json
{
    "vehicles": 
    [
        "java.util.ArrayList",
        [
            [
                "cn.tuyucheng.taketoday.jackson.inheritance.Car",
                {
                    "make": "Mercedes-Benz",
                    "model": "S500",
                    "seatingCapacity": 5,
                    "topSpeed": 250.0
                }
            ],

            [
                "cn.tuyucheng.taketoday.jackson.inheritance.Truck",
                {
                    "make": "Isuzu",
                    "model": "NQR",
                    "payloadCapacity": 7500.0
                }
            ]
        ]
    ]
}
```

在反序列化期间，对象从JSON字符串中恢复，并保留类型数据：

```java
Fleet deserializedFleet = mapper.readValue(jsonDataString, Fleet.class);
```

重新创建的对象将是与序列化之前相同的具体子类型：

```java
assertThat(deserializedFleet.getVehicles().get(0), instanceOf(Car.class));
assertThat(deserializedFleet.getVehicles().get(1), instanceOf(Truck.class));
```

### 2.2 每类注解

每类注解是一种包含类型信息的强大方法，对于需要大量定制的复杂用例非常有用；然而，这只能以牺牲复杂性为代价来实现。如果以两种方式配置类型信息，则每类注解会覆盖全局默认类型。

要使用此方法，应该使用@JsonTypeInfo和其他几个相关注解对父类进行标注。本小节使用类似于前面示例中的Vehicle结构的数据模型来演示这些注解，唯一的变化是在Vehicle抽象类上添加了注解，如下图：

```java
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "type"
)
@JsonSubTypes({
    @Type(value = Car.class, name = "car"), 
    @Type(value = Truck.class, name = "truck")
})
public abstract class Vehicle {
    // fields, constructors, getters and setters
}
```

然后序列化上一小节中初始化的Fleet对象serializedFleet：

```java
String jsonDataString = mapper.writeValueAsString(serializedFleet);
```

序列化后的JSON数据为：

```json
{
    "vehicles": 
    [
        {
            "type": "car",
            "make": "Mercedes-Benz",
            "model": "S500",
            "seatingCapacity": 5,
            "topSpeed": 250.0
        },

        {
            "type": "truck",
            "make": "Isuzu",
            "model": "NQR",
            "payloadCapacity": 7500.0
        }
    ]
}
```

该字符串用于重新创建数据对象：

```java
Fleet deserializedFleet = mapper.readValue(jsonDataString, Fleet.class);
```

最后，验证反序列化后的类型匹配：

```java
assertThat(deserializedFleet.getVehicles().get(0), instanceOf(Car.class));
assertThat(deserializedFleet.getVehicles().get(1), instanceOf(Truck.class));
```

## 3. 忽略父类型的属性

有时，在序列化或反序列化期间需要忽略从父类继承的某些属性，这可以通过以下三种方法之一来实现：注解、混合和注解自省。

### 3.1 注解

有两个常用的Jackson注解可以忽略属性，它们是@JsonIgnore和@JsonIgnoreProperties。前者直接应用于类成员变量，告诉Jackson在序列化或反序列化时忽略相应的属性。后者可以用于任何级别，包括类和类成员，以列出应忽略的属性。

@JsonIgnoreProperties比另一个更强大，因为它允许我们忽略从我们无法控制的父类继承的属性，例如外部库中的类。此外，这个注解允许我们一次忽略许多属性，这在某些情况下可以使代码更容易理解。

下面的类结构用于演示这些注解的使用：

```java
public abstract class Vehicle {
    private String make;
    private String model;

    protected Vehicle(String make, String model) {
        this.make = make;
        this.model = model;
    }

    // no-arg constructor, getters and setters
}

@JsonIgnoreProperties({ "model", "seatingCapacity" })
public abstract class Car extends Vehicle {
    private int seatingCapacity;
    
    @JsonIgnore
    private double topSpeed;

    protected Car(String make, String model, int seatingCapacity, double topSpeed) {
        super(make, model);
        this.seatingCapacity = seatingCapacity;
        this.topSpeed = topSpeed;
    }

    // no-arg constructor, getters and setters
}

public class Sedan extends Car {
    public Sedan(String make, String model, int seatingCapacity, double topSpeed) {
        super(make, model, seatingCapacity, topSpeed);
    }

    // no-arg constructor
}

public class Crossover extends Car {
    private double towingCapacity;

    public Crossover(String make, String model, int seatingCapacity, 
                     double topSpeed, double towingCapacity) {
        super(make, model, seatingCapacity, topSpeed);
        this.towingCapacity = towingCapacity;
    }

    // no-arg constructor, getters and setters
}
```

可以看到，@JsonIgnore告诉Jackson忽略Car.topSpeed属性，而@JsonIgnoreProperties注解忽略Vehicle.model和Car.seatingCapacity属性。

两个注解的行为都通过以下测试进行验证。首先，我们需要实例化ObjectMapper和模型类，然后使用该ObjectMapper实例来序列化模型对象：

```java
ObjectMapper mapper = new ObjectMapper();

Sedan sedan = new Sedan("Mercedes-Benz", "S500", 5, 250.0);
Crossover crossover = new Crossover("BMW", "X6", 5, 250.0, 6000.0);

List<Vehicle> vehicles = new ArrayList<>();
vehicles.add(sedan);
vehicles.add(crossover);

String jsonDataString = mapper.writeValueAsString(vehicles);
```

jsonDataString包含以下JSON数组：

```json
[
    {
        "make": "Mercedes-Benz"
    },
    {
        "make": "BMW",
        "towingCapacity": 6000.0
    }
]
```

最后，我们验证生成的JSON字符串中是否存在相关的属性：

```java
assertThat(jsonDataString, containsString("make"));
assertThat(jsonDataString, not(containsString("model")));
assertThat(jsonDataString, not(containsString("seatingCapacity")));
assertThat(jsonDataString, not(containsString("topSpeed")));
assertThat(jsonDataString, containsString("towingCapacity"));
```

### 3.2 混合

Mix-ins允许我们应用行为(例如在序列化和反序列化时忽略属性)，而无需直接将注解应用到类上。这在我们无法直接修改第三方类的代码时特别有用。

本小节重用上一节中介绍的父子类模型，只是去掉了Car类上的@JsonIgnore和@JsonIgnoreProperties注解：

```java
public abstract class Car extends Vehicle {
    private int seatingCapacity;
    private double topSpeed;
        
    // fields, constructors, getters and setters
}
```

为了演示混合的操作，我们忽略Vehicle.make和Car.topSpeed属性，然后通过测试确保一切按预期工作。

第一步是声明一个混合类型：

```java
private abstract class CarMixIn {
    @JsonIgnore
    public String make;
    @JsonIgnore
    public String topSpeed;
}
```

接下来，混合类型通过ObjectMapper绑定到一个数据类：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.addMixIn(Car.class, CarMixIn.class);
```

之后，我们实例化数据对象并将它们序列化为字符串：

```java
Sedan sedan = new Sedan("Mercedes-Benz", "S500", 5, 250.0);
Crossover crossover = new Crossover("BMW", "X6", 5, 250.0, 6000.0);

List<Vehicle> vehicles = new ArrayList<>();
vehicles.add(sedan);
vehicles.add(crossover);

String jsonDataString = mapper.writeValueAsString(vehicles);
```

jsonDataString现在包含以下JSON：

```json
[
    {
        "model": "S500",
        "seatingCapacity": 5
    },
    {
        "model": "X6",
        "seatingCapacity": 5,
        "towingCapacity": 6000.0
    }
]
```

最后，我们验证结果的正确性：

```java
assertThat(jsonDataString, not(containsString("make")));
assertThat(jsonDataString, containsString("model"));
assertThat(jsonDataString, containsString("seatingCapacity"));
assertThat(jsonDataString, not(containsString("topSpeed")));
assertThat(jsonDataString, containsString("towingCapacity"));
```

### 3.3 注解自省

注解自省是忽略父类属性的最强大的方法，因为它允许使用AnnotationIntrospector.hasIgnoreMarker API进行详细定制。

本小节使用与前一节相同的类层次结构。在这个用例中，我们希望要求Jackson忽略Vehicle.model、Crossover.towingCapacity和Car类中声明的所有属性。首先我们定义一个扩展JacksonAnnotationIntrospector的子类：

```java
class IgnoranceIntrospector extends JacksonAnnotationIntrospector {
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return m.getDeclaringClass() == Vehicle.class && m.getName() == "model" 
            || m.getDeclaringClass() == Car.class 
            || m.getName() == "towingCapacity" 
            || super.hasIgnoreMarker(m);
    }
}
```

自省将忽略任何与方法中定义的条件集匹配的属性(也就是说，它将把它们视为通过其他方法之一标记为已忽略)。

下一步是使用ObjectMapper对象注册IgnoranceIntrospector类的实例：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.setAnnotationIntrospector(new IgnoranceIntrospector());
```

现在我们以与第3.2节相同的方式创建和序列化数据对象。新生成的字符串的内容是：

```json
[
    {
        "make": "Mercedes-Benz"
    },
    {
        "make": "BMW"
    }
]
```

最后，我们验证自省器是否按预期工作：

```java
assertThat(jsonDataString, containsString("make"));
assertThat(jsonDataString, not(containsString("model")));
assertThat(jsonDataString, not(containsString("seatingCapacity")));
assertThat(jsonDataString, not(containsString("topSpeed")));
assertThat(jsonDataString, not(containsString("towingCapacity")));
```

## 4. 子类型处理场景

### 4.1 子类型之间的转换

Jackson允许将对象转换为原始类型以外的类型。事实上，这种转换可能发生在任何兼容的类型之间，但在同一接口或类的两个子类型之间使用时最有帮助。

为了演示将一种类型转换为另一种类型，我们将重用第2节中的Vehicle类层次结构，并在Car和Truck类中的属性上添加@JsonIgnore注解以避免不兼容。

```java
public class Car extends Vehicle {
    @JsonIgnore
    private int seatingCapacity;

    @JsonIgnore
    private double topSpeed;

    // constructors, getters and setters
}

public class Truck extends Vehicle {
    @JsonIgnore
    private double payloadCapacity;

    // constructors, getters and setters
}
```

以下代码将验证转换是否成功以及新对象是否保留旧对象的数据值：

```java
ObjectMapper mapper = new ObjectMapper();

Car car = new Car("Mercedes-Benz", "S500", 5, 250.0);
Truck truck = mapper.convertValue(car, Truck.class);

assertEquals("Mercedes-Benz", truck.getMake());
assertEquals("S500", truck.getModel());
```

### 4.2 没有无参构造函数的反序列化

默认情况下，Jackson使用无参构造函数重新创建模型对象。这在某些情况下很不方便，例如当一个类具有非默认构造函数并且用户必须编写无参数的构造函数来满足Jackson的要求时。在类层次结构中，如果必须将无参构造函数添加到类以及继承链中的所有父类中，这就更麻烦了。在这些情况下，创建者方法的作用就体现出来了。

本节使用类似于第2节中的对象结构，但对构造函数进行了一些更改。具体来说，所有无参数构造函数都被删除，具体子类型的构造函数使用@JsonCreator和@JsonProperty进行标注以使其成为创建者方法。

```java
public class Car extends Vehicle {

    @JsonCreator
    public Car(@JsonProperty("make") String make, 
               @JsonProperty("model") String model, 
               @JsonProperty("seating") int seatingCapacity, 
               @JsonProperty("topSpeed") double topSpeed) {
        super(make, model);
        this.seatingCapacity = seatingCapacity;
        this.topSpeed = topSpeed;
    }

    // fields, getters and setters
}

public class Truck extends Vehicle {

    @JsonCreator
    public Truck(@JsonProperty("make") String make, 
                 @JsonProperty("model") String model, 
                 @JsonProperty("payload") double payloadCapacity) {
        super(make, model);
        this.payloadCapacity = payloadCapacity;
    }

    // fields, getters and setters
}
```

以下测试验证Jackson是否可以处理缺少无参构造函数的对象：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.enableDefaultTyping();
        
Car car = new Car("Mercedes-Benz", "S500", 5, 250.0);
Truck truck = new Truck("Isuzu", "NQR", 7500.0);

List<Vehicle> vehicles = new ArrayList<>();
vehicles.add(car);
vehicles.add(truck);

Fleet serializedFleet = new Fleet();
serializedFleet.setVehicles(vehicles);

String jsonDataString = mapper.writeValueAsString(serializedFleet);
mapper.readValue(jsonDataString, Fleet.class);
```

## 5. 总结

本教程涵盖了几个有用的用例来演示Jackson对类型继承的支持，重点是多态性和对父类型属性的忽略。
