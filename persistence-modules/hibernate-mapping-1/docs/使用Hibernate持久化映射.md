## 1. 简介

在 Hibernate 中，我们可以通过将其中一个字段设为List来表示Javabean 中[的一对多关系](https://www.baeldung.com/hibernate-one-to-many)。

在本快速教程中，我们将探索使用Map执行此操作的各种方法。

## 2. Map不同于 List _

使用Map表示一对多关系与List不同，因为我们有一个键。

这个键将我们的实体关系变成了一个三元关联，其中每个键都指向一个简单的值或一个可嵌入的对象或一个实体。因此，要使用Map，我们总是需要一个连接表来存储引用父实体的外键——键和值。

但是这个连接表与其他连接表有点不同，因为主键不一定是父表和目标表的外键。相反，我们将主键作为父项的外键和作为Map 键的列的组合。

Map中的键值对可能有两种类型：[Value Type和Entity Type](https://javabydeveloper.com/hibernate-entity-types-vs-value-types/)。在接下来的部分中，我们将研究在 Hibernate 中表示这些关联的方法。

## 3.使用@MapKeyColumn

假设我们有一个 订单实体，我们想要跟踪订单中所有商品的名称和价格。因此， 我们想向 Order 引入一个 Map < String, Double>，它将把商品的名称映射到它的价格：

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ElementCollection
    @CollectionTable(name = "order_item_mapping", 
      joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "item_name")
    @Column(name = "price")
    private Map<String, Double> itemPriceMap;

    // standard getters and setters
}
```

我们需要向 Hibernate 指示从何处获取键和值。对于键，我们使用了 @MapKey Column，表示Map的键是我们连接表order_item_mapping的item_name列。同样， @Column 指定 Map 的 值对应于连接表的 价格 列。

另外，itemPriceMap 对象是一个值类型的映射，因此我们必须使用 @ElementCollection 注解。

除了基本的值类型对象，[@ Embeddable](https://docs.jboss.org/hibernate/jpa/2.1/api/javax/persistence/Embeddable.html)对象也可以以类似的方式用作Map的值。

## 4.使用@MapKey

众所周知，需求会随着时间而变化——所以，现在，假设我们需要存储Item 的更多属性以及 itemName 和 itemPrice：

```java
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String itemName;

    @Column(name = "price")
    private double itemPrice;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date createdOn;
   
    // standard getters and setters
}
```

因此，让我们将Order 实体类中的 Map<String, Double>更改为Map<String, Item> ：

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "order_item_mapping", 
      joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")})
    @MapKey(name = "itemName")
    private Map<String, Item> itemMap;

}
```

请注意，这一次，我们将使用@MapKey注解，以便 Hibernate 将使用Item# itemName作为映射键列，而不是在连接表中引入额外的列。因此，在这种情况下，连接表 order_item_mapping 没有键列— 相反，它指的是I tem的名称。

这与@MapKeyColumn 形成对比。 当我们使用 @MapKeyColumn 时， 映射键驻留在连接表中。这就是为什么我们不能同时使用这两个注解来定义我们的实体映射的原因。

此外， itemMap 是一个实体类型映射，因此我们必须使用 [@OneToMany](https://www.baeldung.com/hibernate-one-to-many)或[@ManyToMany](https://www.baeldung.com/hibernate-many-to-many)来注解关系。

## 5. 使用@MapKeyEnumerated和@MapKeyTemporal

每当我们将枚举指定为Map键时，我们都会使用@MapKeyEnumerated。同样，对于时间值，使用@MapKeyTemporal。[该行为分别与标准的@Enumerated](https://docs.jboss.org/hibernate/jpa/2.1/api/javax/persistence/Enumerated.html)和[@Temporal](https://docs.jboss.org/hibernate/jpa/2.1/api/javax/persistence/Temporal.html)注解非常相似。

默认情况下，这些类似于 @MapKeyColumn，因为将在连接表中创建一个键列。如果我们想重用已存储在持久化实体中的值，我们应该另外用@MapKey标记该字段。

## 6.使用@MapKeyJoinColumn

接下来，假设我们还需要跟踪每件商品的卖家。我们可以这样做的一种方法是添加一个Seller实体并将其绑定到我们的Item实体：

```java
@Entity
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String sellerName;
   
    // standard getters and setters

}
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String itemName;

    @Column(name = "price")
    private double itemPrice;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date createdOn;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private Seller seller;
 
    // standard getters and setters
}
```

在这种情况下，假设我们的用例是 按 卖家对所有Order的Item进行分组。 因此，让我们将Map<String, Item>更改为 Map<Seller, Item>：

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "order_item_mapping", 
      joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "seller_id")
    private Map<Seller, Item> sellerItemMap;

    // standard getters and setters

}
```

我们需要添加@MapKeyJoinColumn来实现这一点，因为该注解允许 Hibernate 将seller_id 列(映射键) 与 item_id 列一起保留在连接表order_item_mapping 中。那么，在从数据库中读取数据的时候，我们可以很方便的进行 GROUP BY 操作。

## 七. 总结

在本文中，我们了解了 根据所需映射在 Hibernate 中持久化Map的几种方法。