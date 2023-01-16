## 1. 概述

在本教程中，我们将讨论在使用[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)时从数据库中删除实体的两个选项之间的区别。

首先，我们将从CascadeType.REMOVE开始，这是一种在删除父实体时删除一个或多个子实体的方法。然后我们将看一下JPA 2.0 中引入的orphanRemoval属性。这为我们提供了一种从数据库中删除孤立实体的方法。

在整个教程中，我们将使用一个简单的在线商店域来演示我们的示例。

## 2.领域模型

如前所述，本文使用了一个简单的在线商店域。其中OrderRequest 有一个 ShipmentInfo 和一个 LineItem列表。

鉴于此，让我们考虑：

-   对于ShipmentInfo 的删除， 当删除 OrderRequest时，我们将使用CascadeType.REMOVE
-   为了 从 OrderRequest中移除LineItem，我们将使用orphanRemoval

首先，让我们创建一个ShipmentInfo实体： 

```java
@Entity
public class ShipmentInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    // constructors
}
```

接下来，让我们创建一个 LineItem 实体：

```java
@Entity
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    private OrderRequest orderRequest;

    // constructors, equals, hashCode
}
```

最后，让我们通过创建一个OrderRequest实体将它们放在一起：

```java
@Entity
public class OrderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    private ShipmentInfo shipmentInfo;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST, mappedBy = "orderRequest")
    private List<LineItem> lineItems;

    // constructors

    public void removeLineItem(LineItem lineItem) {
        lineItems.remove(lineItem);
    }
}
```

值得强调的是removeLineItem方法，它将LineItem从OrderRequest中分离出来。

## 3. 级联类型.REMOVE

如前所述，使用CascadeType.REMOVE标记引用字段是一种在删除 父实体时删除子实体的方法。

在我们的例子中，一个OrderRequest 有一个 ShipmentInfo，它有一个CascadeType.REMOVE。 

要在删除 OrderRequest时验证 从数据库中删除 ShipmentInfo，让我们创建一个简单的集成测试：

```java
@Test
public void whenOrderRequestIsDeleted_thenDeleteShipmentInfo() {
    createOrderRequestWithShipmentInfo();

    OrderRequest orderRequest = entityManager.find(OrderRequest.class, 1L);

    entityManager.getTransaction().begin();
    entityManager.remove(orderRequest);
    entityManager.getTransaction().commit();

    Assert.assertEquals(0, findAllOrderRequest().size());
    Assert.assertEquals(0, findAllShipmentInfo().size());
}

private void createOrderRequestWithShipmentInfo() {
    ShipmentInfo shipmentInfo = new ShipmentInfo("name");
    OrderRequest orderRequest = new OrderRequest(shipmentInfo);

    entityManager.getTransaction().begin();
    entityManager.persist(orderRequest);
    entityManager.getTransaction().commit();

    Assert.assertEquals(1, findAllOrderRequest().size());
    Assert.assertEquals(1, findAllShipmentInfo().size());
}
```

从断言中，我们可以看到删除OrderRequest导致相关的ShipmentInfo也成功删除。

## 4. 孤儿移除

如前所述，它的用途是 从数据库中删除 孤立的实体。 不再依附于其父实体的实体是孤儿的定义。 

在我们的例子中，OrderRequest有一个LineItem对象的集合，我们在其中使用@OneToMany注解来标识关系。这是我们还将orphanRemoval属性设置为true的地方。要从OrderRequest中分离LineItem，我们可以使用我们之前创建的removeLineItem方法。 

一切就绪后，一旦我们使用removeLineItem方法并保存OrderRequest，就会从数据库中删除孤立的LineItem。 

为了验证从数据库中删除孤立的 LineItem，让我们创建另一个集成测试：

```java
@Test
public void whenLineItemIsRemovedFromOrderRequest_thenDeleteOrphanedLineItem() {
    createOrderRequestWithLineItems();

    OrderRequest orderRequest = entityManager.find(OrderRequest.class, 1L);
    LineItem lineItem = entityManager.find(LineItem.class, 2L);
    orderRequest.removeLineItem(lineItem);

    entityManager.getTransaction().begin();
    entityManager.merge(orderRequest);
    entityManager.getTransaction().commit();

    Assert.assertEquals(1, findAllOrderRequest().size());
    Assert.assertEquals(2, findAllLineItem().size());
}

private void createOrderRequestWithLineItems() {
    List<LineItem> lineItems = new ArrayList<>();
    lineItems.add(new LineItem("line item 1"));
    lineItems.add(new LineItem("line item 2"));
    lineItems.add(new LineItem("line item 3"));

    OrderRequest orderRequest = new OrderRequest(lineItems);

    entityManager.getTransaction().begin();
    entityManager.persist(orderRequest);
    entityManager.getTransaction().commit();

    Assert.assertEquals(1, findAllOrderRequest().size());
    Assert.assertEquals(3, findAllLineItem().size());
}
```

同样，从断言中，它表明我们已经成功地从数据库中删除了孤立的LineItem 。

此外，值得一提的是，removeLineItem 方法修改了LineItem的列表，而不是为其重新分配一个值。执行后者将导致PersistenceException。

为了验证所述行为，让我们创建一个最终的集成测试：

```java
@Test(expected = PersistenceException.class)
public void whenLineItemsIsReassigned_thenThrowAnException() {
    createOrderRequestWithLineItems();

    OrderRequest orderRequest = entityManager.find(OrderRequest.class, 1L);
    orderRequest.setLineItems(new ArrayList<>());

    entityManager.getTransaction().begin();
    entityManager.merge(orderRequest);
    entityManager.getTransaction().commit();
}
```

## 5.总结

在本文中，我们使用一个简单的在线商店域探讨了CascadeType.REMOVE和orphanRemoval之间的区别。此外，为了验证实体是否已从我们的数据库中正确删除，我们创建了几个集成测试。