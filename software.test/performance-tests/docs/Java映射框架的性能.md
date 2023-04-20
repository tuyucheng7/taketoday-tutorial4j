## 1. 简介

创建由多个层组成的大型Java应用程序需要使用多种模型，例如持久层模型、域模型或所谓的DTO。为不同的应用层使用多个模型将需要我们提供一种bean之间的映射方式。

手动执行此操作需要创建大量样板代码并消耗大量时间。对我们来说幸运的是，Java有多种对象映射框架。

在本教程中，我们将比较最流行的Java映射框架的性能。

## 2. 映射框架

### 2.1 Dozer

**Dozer是一种映射框架，它使用递归将数据从一个对象复制到另一个对象**。该框架不仅可以在bean之间复制属性，还可以自动在不同类型之间进行转换。

要使用Dozer框架，我们需要将这样的依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>com.github.dozermapper</groupId>
    <artifactId>dozer-core</artifactId>
    <version>6.5.2</version>
</dependency>
```

有关Dozer框架用法的更多信息，请参阅[本文](https://www.baeldung.com/dozer)。

框架的文档可以在[这里](https://github.com/DozerMapper/dozer#what-is-dozer)找到，最新版本可以在[这里](https://central.sonatype.com/artifact/com.github.dozermapper/dozer-core/6.5.2)找到。

### 2.2 Orika

**Orika是一种bean到bean映射框架，它以递归方式将数据从一个对象复制到另一个对象**。

Orika的一般工作原理类似于Dozer。两者之间的主要区别在于**Orika使用字节码生成**。这允许以最小的开销生成更快的映射器。

要使用它，我们需要将这样的依赖添加到我们的项目中：

```xml
<dependency>
    <groupId>ma.glasnost.orika</groupId>
    <artifactId>orika-core</artifactId>
    <version>1.5.4</version>
</dependency>
```

有关Orika用法的更多详细信息，请参阅[本文](https://www.baeldung.com/orika-mapping)。

框架的实际文档可以在[这里](https://orika-mapper.github.io/orika-docs/)找到，最新版本可以在[这里](https://central.sonatype.com/artifact/ma.glasnost.orika/orika-core/1.5.4)找到。

> **警告**：从[Java 16](https://www.baeldung.com/java-16-new-features)开始，默认拒绝[非法反射访问](https://www.baeldung.com/java-illegal-reflective-access)。Orika 1.5.4版本使用了这种反射访问，因此Orika目前无法与Java 16结合使用。据称，这个问题将在未来随着1.6.0版本的发布而得到解决。

### 2.3 MapStruct

**MapStruct是一个代码生成器，可以自动生成bean映射器类**。

MapStruct还具有在不同数据类型之间进行转换的能力。有关如何使用它的更多信息，请参阅[本文](https://www.baeldung.com/mapstruct)。

要将MapStruct添加到我们的项目中，我们需要包含以下依赖项：

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.2.Final</version>
</dependency>
```

框架的文档可以在[这里](http://mapstruct.org/)找到，最新版本可以在[这里](https://central.sonatype.com/artifact/org.mapstruct/mapstruct/1.5.4.Final)找到。

### 2.4 ModelMapper

**ModelMapper是一个旨在通过确定对象如何根据约定相互映射来简化对象映射的框架。它提供类型安全和重构安全的API**。

有关该框架的更多信息可以在[文档](http://modelmapper.org/)中找到。

要在我们的项目中包含ModelMapper，我们需要添加以下依赖项：

```xml
<dependency>
  <groupId>org.modelmapper</groupId>
  <artifactId>modelmapper</artifactId>
  <version>3.1.0</version>
</dependency>
```

最新版本的框架可以在[这里](https://central.sonatype.com/artifact/org.modelmapper/modelmapper/3.1.1)找到。

### 2.5 JMappers

JMapper是一个映射框架，旨在提供Java Beans之间易于使用、高性能的映射。

**该框架旨在使用注解和关系映射来应用DRY原则**。

该框架允许不同的配置方式：基于注解、基于XML或基于API。

有关该框架的更多信息可以在其[文档](https://github.com/jmapper-framework/jmapper-core/wiki)中找到。

要在我们的项目中包含JMapper，我们需要添加它的依赖项：

```xml
<dependency>
    <groupId>com.googlecode.jmapper-framework</groupId>
    <artifactId>jmapper-core</artifactId>
    <version>1.6.1.CR2</version>
</dependency>
```

最新版本的框架可以在[这里](https://central.sonatype.com/artifact/com.googlecode.jmapper-framework/jmapper-core/1.6.1.CR2)找到。

## 3. 测试模型

为了能够正确测试映射，我们需要有源模型和目标模型。我们创建了两个测试模型。

第一个只是一个带有一个String字段的简单POJO，这使我们能够在更简单的情况下比较框架，并检查如果我们使用更复杂的beans是否有任何变化。

简单的源模型如下所示：

```java
public class SourceCode {
    String code;
    // getter and setter
}
```

它的目标模型非常相似：

```java
public class DestinationCode {
    String code;
    // getter and setter
}
```

源bean的真实示例如下所示：

```java
public class SourceOrder {
    private String orderFinishDate;
    private PaymentType paymentType;
    private Discount discount;
    private DeliveryData deliveryData;
    private User orderingUser;
    private List<Product> orderedProducts;
    private Shop offeringShop;
    private int orderId;
    private OrderStatus status;
    private LocalDate orderDate;
    // standard getters and setters
}
```

目标类如下所示：

```java
public class Order {
    private User orderingUser;
    private List<Product> orderedProducts;
    private OrderStatus orderStatus;
    private LocalDate orderDate;
    private LocalDate orderFinishDate;
    private PaymentType paymentType;
    private Discount discount;
    private int shopId;
    private DeliveryData deliveryData;
    private Shop offeringShop;
    // standard getters and setters
}
```

整个模型结构可以在[这里](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/software.test/performance-tests/src/main/java/cn/tuyucheng/taketoday/performancetests/model/source)找到。

## 4. 转换器

为了简化测试设置的设计，我们创建了Converter接口：

```java
public interface Converter {
    Order convert(SourceOrder sourceOrder);

    DestinationCode convert(SourceCode sourceCode);
}
```

我们所有的自定义映射器都将实现这个接口。

### 4.1 OrikaConverter

Orika允许完整的API实现，这大大简化了映射器的创建：

```java
public class OrikaConverter implements Converter{
    private MapperFacade mapperFacade;

    public OrikaConverter() {
        MapperFactory mapperFactory = new DefaultMapperFactory
                .Builder().build();

        mapperFactory.classMap(Order.class, SourceOrder.class)
                .field("orderStatus", "status").byDefault().register();
        mapperFacade = mapperFactory.getMapperFacade();
    }

    @Override
    public Order convert(SourceOrder sourceOrder) {
        return mapperFacade.map(sourceOrder, Order.class);
    }

    @Override
    public DestinationCode convert(SourceCode sourceCode) {
        return mapperFacade.map(sourceCode, DestinationCode.class);
    }
}
```

### 4.2 DozerConverter

Dozer需要XML映射文件，包含以下部分：

```xml
<mappings xmlns="http://dozermapper.github.io/schema/bean-mapping"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://dozermapper.github.io/schema/bean-mapping 
          https://dozermapper.github.io/schema/bean-mapping.xsd">

    <mapping>
        <class-a>cn.tuyucheng.taketoday.performancetests.model.source.SourceOrder</class-a>
        <class-b>cn.tuyucheng.taketoday.performancetests.model.destination.Order</class-b>
        <field>
            <a>status</a>
            <b>orderStatus</b>
        </field>
    </mapping>
    <mapping>
        <class-a>cn.tuyucheng.taketoday.performancetests.model.source.SourceCode</class-a>
        <class-b>cn.tuyucheng.taketoday.performancetests.model.destination.DestinationCode</class-b>
    </mapping>
</mappings>
```

定义XML映射后，我们可以从代码中使用它：

```java
public class DozerConverter implements Converter {
    private final Mapper mapper;

    public DozerConverter() {
        this.mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("dozer-mapping.xml")
                .build();
    }

    @Override
    public Order convert(SourceOrder sourceOrder) {
        return mapper.map(sourceOrder,Order.class);
    }

    @Override
    public DestinationCode convert(SourceCode sourceCode) {
        return mapper.map(sourceCode, DestinationCode.class);
    }
}
```

### 4.3 MapStructConverter

MapStruct定义非常简单，因为它完全基于代码生成：

```java
@Mapper
public interface MapStructConverter extends Converter {
    MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

    @Mapping(source = "status", target = "orderStatus")
    @Override
    Order convert(SourceOrder sourceOrder);

    @Override
    DestinationCode convert(SourceCode sourceCode);
}
```

### 4.4 JMapperConverter

JMapperConverter需要做更多的工作。实现接口后：

```java
public class JMapperConverter implements Converter {
    JMapper realLifeMapper;
    JMapper simpleMapper;

    public JMapperConverter() {
        JMapperAPI api = new JMapperAPI()
                .add(JMapperAPI.mappedClass(Order.class));
        realLifeMapper = new JMapper(Order.class, SourceOrder.class, api);
        JMapperAPI simpleApi = new JMapperAPI()
                .add(JMapperAPI.mappedClass(DestinationCode.class));
        simpleMapper = new JMapper(
                DestinationCode.class, SourceCode.class, simpleApi);
    }

    @Override
    public Order convert(SourceOrder sourceOrder) {
        return (Order) realLifeMapper.getDestination(sourceOrder);
    }

    @Override
    public DestinationCode convert(SourceCode sourceCode) {
        return (DestinationCode) simpleMapper.getDestination(sourceCode);
    }
}
```

我们还需要为目标类的每个字段添加@JMap注解。此外，JMapper不能自行在枚举类型之间进行转换，它需要我们创建自定义映射函数：

```java
@JMapConversion(from = "paymentType", to = "paymentType")
public PaymentType conversion(cn.tuyucheng.taketoday.performancetests.model.source.PaymentType type) {
    PaymentType paymentType = null;
    switch(type) {
        case CARD:
            paymentType = PaymentType.CARD;
            break;

        case CASH:
            paymentType = PaymentType.CASH;
            break;

        case TRANSFER:
            paymentType = PaymentType.TRANSFER;
            break;
    }
    return paymentType;
}
```

### 4.5 ModelMapperConverter

ModelMapperConverter要求我们只提供我们想要映射的类：

```java
public class ModelMapperConverter implements Converter {
    private ModelMapper modelMapper;

    public ModelMapperConverter() {
        modelMapper = new ModelMapper();
    }

    @Override
    public Order convert(SourceOrder sourceOrder) {
        return modelMapper.map(sourceOrder, Order.class);
    }

    @Override
    public DestinationCode convert(SourceCode sourceCode) {
        return modelMapper.map(sourceCode, DestinationCode.class);
    }
}
```

## 5. 简单模型测试

对于性能测试，我们可以使用Java Microbenchmark Harness，有关如何使用它的更多信息可以在[本文](https://www.baeldung.com/java-microbenchmark-harness)中找到。

我们为每个转换器创建了一个单独的基准测试，并将BenchmarkMode指定为Mode.All。

### 5.1 平均时间

JMH返回以下平均运行时间结果(越小越好)：

| 框架名称      | 平均运行时间(每次操作以毫秒为单位) |
|-----------|--------------------|
| MapStruct | 10<sup>-5</sup>    |
| JMappers  | 10<sup>-5</sup>               |
| Orika        | 0.001              |
| ModelMapper     | 0.002              |
| Dozer       | 0.004              |

这个基准清楚地表明MapStruct和JMapper都有最好的平均工作时间。

### 5.2 吞吐量

在这种模式下，基准测试返回每秒的操作数。我们收到了以下结果(越多越好)：

| 框架名称      |吞吐量(每毫秒的操作数)|
|-----------|------------------------|
| MapStruct |58101                    |
| JMapper   |53667                    |
| Orika     |1195                     |
| ModelMapper         |379                      |
| Dozer       |230                      |

在吞吐量模式下，MapStruct是测试框架中最快的，JMapper紧随其后。

### 5.3 单发时间

此模式允许测量单次操作从开始到结束的时间。基准测试给出了以下结果(越少越好)：

| 框架名称      |单发时间(每次操作以毫秒为单位)|
|-----------|------------------------------------|
| JMappers  |0.016                                |
| MapStruct |1.904                                |
| Dozer     |3.864                                |
| Orika     |6.593                                |
| ModelMapper     |8.788                                |

在这里，我们看到JMapper返回的结果比MapStruct更好。

### 5.4 采样时间

此模式允许对每个操作的时间进行采样。三个不同百分位数的结果如下所示：

|             | 采样时间(每次操作的毫秒数)  |        |       |
|-------------|-----------------|------|-----|
| **框架名称**    | **p0.90**       |**p0.999**|**p1.0**  |
| JMapper     | 10<sup>-4</sup> |0.001  |1.526|
| MapStruct   | 10<sup>-4</sup>            |10<sup>-4</sup>   |1.948|
| Orika       | 0.001           |0.018  |2.327|
| ModelMapper | 0.002           |0.044  |3.604|
| Dozer       | 0.003           |0.088  |5.382|

所有基准测试都表明MapStruct和JMapper都是不错的选择，具体取决于场景。

## 6. 真实模型测试

对于性能测试，我们可以使用Java Microbenchmark Harness，有关如何使用它的更多信息可以在[本文](https://www.baeldung.com/java-microbenchmark-harness)中找到。

我们为每个转换器创建了一个单独的基准，并将BenchmarkMode指定为Mode.All。

### 6.1 平均时间

JMH返回以下平均运行时间结果(越少越好)：

| 框架名称      | 平均运行时间(每次操作以毫秒为单位) |
|-----------|--------------------|
| MapStruct | 10<sup>-4</sup>    |
| JMappers  | 10<sup>-4</sup>               |
| Orika     | 0.007              |
| ModelMapper     | 0.137              |
| Dozer     | 0.145              |

### 6.2 吞吐量

在这种模式下，基准测试返回每秒的操作数。对于每个映射器，我们收到以下结果(越多越好)：

| 框架名称        |吞吐量(每毫秒的操作数)|
|-------------|------------------------|
| JMappers    |3205                     |
| MapStruct   |3467                     |
| Orika       |121                      |
| ModelMapper |7                        |
| Dozer       |6.342                    |

### 6.3 单发时间

此模式允许测量单次操作从开始到结束的时间。基准测试给出了以下结果(越少越好)：

| 框架名称        |单次发射时间(每次操作以毫秒为单位)|
|-------------|------------------------------------|
| JMappers    |0.722                                |
| MapStruct   |2.111                                |
| Dozer       |16.311                               |
| ModelMapper |22.342                               |
| Orika       |32.473                               |

### 6.4 采样时间

此模式允许对每个操作的时间进行采样。抽样结果分为百分位数，我们将展示三个不同百分位数p0.90、p0.999和p1.00的结果：

|             | 采样时间(每次操作的毫秒数)  |        |      |
|-------------|-----------------|------|----|
| **框架名称**    | p0.90****       |**p0.999**|**p1.0**|
| JMappers    | 10<sup>-3</sup> |0.006  |3    |
| MapStruct   | 10<sup>-3</sup>           |0.006  |8    |
| Orika       | 0.007           |0.143  |14   |
| ModelMapper | 0.138           |0.991  |15   |
| Dozer       | 0.131           |0.954  |7    |

虽然简单示例和真实示例的确切结果明显不同，但它们或多或少确实遵循相同的趋势。在这两个示例中，我们都看到了JMapper和MapStruct之间的激烈竞争。

### 6.5 结论

基于我们在本节中执行的真实模型测试，我们可以看到最好的性能显然属于JMapper，尽管MapStruct紧随其后。在相同的测试中，我们看到Dozer一直位于结果表的底部，除了单发时间。

## 7. 总结

在本文中，我们对5个流行的Java bean映射框架进行了性能测试：ModelMapper、MapStruct、Orika、Dozer和JMapper。