## 1. 简介

在这个简短的教程中，我们将了解可以在@ org.hibernate.annotations.Fetch注解中使用的不同FetchMode值。

## 2.设置示例

例如，我们将使用以下仅具有两个属性的Customer实体——一个 id 和一组订单：

```java
@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "customer")
    @Fetch(value = FetchMode.SELECT)
    private Set<Order> orders = new HashSet<>();

    // getters and setters
}
```

此外，我们将创建一个包含 ID、名称和对Customer的引用的Order实体。

```java
@Entity
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // getters and setters
}
```

在接下来的每一节中，我们将从数据库中获取客户并获取其所有订单：

```java
Customer customer = customerRepository.findById(id).get();
Set<Order> orders = customer.getOrders();
```

## 3. FetchMode.SELECT

在我们的Customer实体上，我们用@Fetch注解注解了orders属性：

```java
@OneToMany
@Fetch(FetchMode.SELECT)
private Set<Orders> orders;
```

我们使用 @Fetch来描述当我们查找客户时 Hibernate 应该如何检索属性 。

使用 SELECT表示应该延迟加载该属性。

这意味着对于第一行：

```java
Customer customer = customerRepository.findById(id).get();
```

我们不会看到与订单表的连接：

```plaintext
Hibernate: 
    select ...from customer
    where customer0_.id=?

```

下一行：

```java
Set<Order> orders = customer.getOrders();
```

我们将看到相关订单的后续查询：

```plaintext
Hibernate: 
    select ...from order
    where order0_.customer_id=?

```

Hibernate FetchMode.SELECT为每个需要加载的订单生成一个单独的查询。

在我们的示例中，这提供了一个查询来加载 Customers 和五个额外的查询来加载订单集合。

这被称为n + 1 选择问题。执行一个查询将触发n 个额外的查询。

### 3.1. @批量大小

FetchMode.SELECT有一个可选的配置注解，使用@BatchSize注解：

```java
@OneToMany
@Fetch(FetchMode.SELECT)
@BatchSize(size=10)
private Set<Orders> orders;
```

Hibernate将尝试按大小参数定义的批次加载订单集合。

在我们的示例中，我们只有五个订单，因此一个查询就足够了。

我们仍将使用相同的查询：

```plaintext
Hibernate:
    select ...from order
    where order0_.customer_id=?
```

但它只会运行一次。现在我们只有两个查询：一个加载客户，一个加载订单集合。

## 4.获取模式.JOIN

FetchMode.SELECT延迟加载关系，而FetchMode.JOIN急切地加载它们，比如通过连接：

```java
@OneToMany
@Fetch(FetchMode.JOIN)
private Set<Orders> orders;
```

这导致对 Customer及其Order的查询只有一个：

```plaintext
Hibernate: 
    select ...
    from
        customer customer0_ 
    left outer join
        order order1 
            on customer.id=order.customer_id 
    where
        customer.id=?
```

## 5.获取模式.SUBSELECT

因为orders属性是一个集合，我们也可以使用 FetchMode.SUBSELECT：

```java
@OneToMany
@Fetch(FetchMode.SUBSELECT)
private Set<Orders> orders;
```

我们只能对集合使用SUBSELECT 。

通过此设置，我们返回到对客户的一个查询：

```plaintext
Hibernate: 
    select ...
    from customer customer0_

```

一次查询Order，这次使用子选择：

```plaintext
Hibernate: 
    select ...
    from
        order order0_ 
    where
        order0_.customer_id in (
            select
                customer0_.id 
            from
                customer customer0_
        )
```

## 6. FetchMode与FetchType

通常，FetchMode定义了Hibernate将如何获取数据(通过选择、连接或子选择)。另一方面， FetchType定义了 Hibernate 是急切还是延迟加载数据。

这两者之间的确切规则如下：

-   如果代码没有设置FetchMode，默认的是JOIN并且FetchType按照定义工作
-   设置FetchMode.SELECT或FetchMode.SUBSELECT后，FetchType也按定义工作
-   设置了FetchMode.JOIN后，FetchType将被忽略并且查询总是急切的

有关详细信息，请参阅[Hibernate 中的预热/延迟加载](https://www.baeldung.com/hibernate-lazy-eager-loading)。

## 七. 总结

在本教程中，我们了解了FetchMode的不同值以及它们与FetchType 的关系。