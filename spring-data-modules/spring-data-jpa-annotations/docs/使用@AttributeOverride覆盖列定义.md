## 1. 概述

在本教程中，我们演示如何使用[@AttributeOverride](https://docs.oracle.com/javaee/6/api/javax/persistence/AttributeOverride.html)更改列映射、介绍在使用@MappedSuperclass或@Embeddable实体时如何使用它。

## 2. @AttributeOverride的属性

该注解包含两个必需属性：

-   name：包含实体的字段名称
-   column：覆盖原始对象中定义的列定义

## 3. 与@MappedSuperclass一起使用

首先我们定义一个Vehicle类：

```java
@MappedSuperclass
public class Vehicle {
    @Id
    @GeneratedValue
    private Integer id;
    private String identifier;
    private Integer numberOfWheels;
    
    // standard getters and setters
}
```

[@MappedSuperclass](https://docs.oracle.com/javaee/5/api/javax/persistence/MappedSuperclass.html)注解表示它是其他实体的基类。

接下来定义Car类，它扩展了Vehicle。**该类演示如何扩展实体并将汽车的信息存储在单个表中。请注意，@AttributeOverride注解在类上指定**：

```java
@Entity
@AttributeOverride(name = "identifier", column = @Column(name = "VIN"))
public class Car extends Vehicle {
    private String model;
    private String name;

    // standard getters and setters
}
```

因此，我们有一个包含所有Car详细信息和Vehicle详细信息的表。问题是对于Car，我们希望在VIN列中存储identifier，我们通过@AttributeOverride实现了这一点。**该注解用于定义字段identifier存储在VIN列中**。 

## 4. 与@Embeddable一起使用

现在让我们使用两个可嵌入类向我们的车辆添加更多细节，首先定义基本的地址信息：

```java
@Embeddable
public class Address {
    private String name;
    private String city;

    // standard getters and setters
}
```

并创建一个包含汽车制造商信息的类：

```java
@Embeddable
public class Brand {
    private String name;
    private LocalDate foundationDate;
    @Embedded
    private Address address;

    // standard getters and setters
}
```

Brand类包含一个带有地址详细信息的嵌入类，**我们使用它来演示如何将@AttributeOverride与多级嵌入一起使用**。

下面在我们的Car实体中添加Brand字段 ：

```java
@Entity
@AttributeOverride(name = "identifier", column = @Column(name = "VIN"))
public class Car extends Vehicle {
    // existing fields

    @Embedded
    @AttributeOverrides({
			@AttributeOverride(name = "name", column = @Column(name = "BRAND_NAME", length = 5)), 
			@AttributeOverride(name = "address.name", column = @Column(name = "ADDRESS_NAME"))
    })
    private Brand brand;

    // standard getters and setters
}
```

首先，**[@AttributeOverrides注解](https://docs.oracle.com/javaee/5/api/javax/persistence/AttributeOverrides.html)允许我们修改多个属性**。我们覆盖了Brand类中的name列定义，因为Car类中存在名称相同的列。因此，Brand的name属性存储在BRAND_NAME列中。

此外，**我们还定义了BRAND_NAME列长度**。请注意，**column属性会覆盖被覆盖类中的所有值**，要保留原始值，必须在column属性中设置所有值。

除此之外，Address类中的name列被映射到ADDRESS_NAME。**为了覆盖多个嵌入级别的映射，我们使用点“.”来指定覆盖字段的路径**。

## 5. 嵌入集合

下面来看看如何将该注解与集合一起使用，我们添加一个车主的详细信息类：

```java
@Embeddable
public class Owner {
    private String name;
    private String surname;

    // standard getters and setters
}
```

我们希望将车主与地址信息关联在一起，所以我们添加一个所有者及其地址的Map字段：

```java
@Entity
@AttributeOverride(name = "identifier", column = @Column(name = "VIN"))
public class Car extends Vehicle {
    // existing fields

    @ElementCollection
    @AttributeOverrides({
			@AttributeOverride(name = "key.name", column = @Column(name = "OWNER_NAME")), 
			@AttributeOverride(name = "key.surname", column = @Column(name = "OWNER_SURNAME")), 
			@AttributeOverride(name = "value.name", column = @Column(name = "ADDRESS_NAME")),
    })
    Map<Owner, Address> owners;

    // standard getters and setters
}
```

**多亏了注解的存在，我们可以重用Address类**。在上面的@AttributeOverride注解中，**name属性的key前缀表示覆盖Owner类中的字段；此外，name属性的value前缀指向Address类中的字段**。对于List集合，不需要这些添加前缀。

## 6. 总结

这篇关于@AttibuteOverride注解的短文到此结束，我们了解了如何在@MappedSuperclass或@Embeddable实体时使用此注解；然后我们演示了将它与集合一起使用。