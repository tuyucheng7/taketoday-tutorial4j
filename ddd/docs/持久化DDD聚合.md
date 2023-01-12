## 1. 概述

在本教程中，我们将探讨使用不同技术持久化[DDD 聚合的可能性。](https://martinfowler.com/bliki/DDD_Aggregate.html)

## 2. 聚合介绍

聚合是一组始终需要保持一致的业务对象。因此，我们在事务中将聚合作为一个整体进行保存和更新。

聚合是 DDD 中一个重要的战术模式，它有助于维护我们业务对象的一致性。但是，聚合的概念在 DDD 上下文之外也很有用。

在许多业务案例中，这种模式可以派上用场。根据经验，当多个对象作为同一事务的一部分发生更改时，我们应该考虑使用聚合。

让我们看一下在为订单购买建模时如何应用它。

### 2.1. 采购订单示例

因此，假设我们要为采购订单建模：

```java
class Order {
    private Collection<OrderLine> orderLines;
    private Money totalCost;
    // ...
}
class OrderLine {
    private Product product;
    private int quantity;
    // ...
}
class Product {
    private Money price;
    // ...
}
```

这些类形成一个简单的集合。订单的 orderLines和 totalCost字段必须始终保持一致，即 totalCost的值应始终等于所有orderLines的总和。

现在，我们都可能想将所有这些变成成熟的JavaBean。 但是，请注意，在 Order中引入简单的 getter 和 setter很容易破坏我们模型的封装并违反业务约束。

让我们看看会出什么问题。

### 2.2. 朴素的聚合设计

让我们想象一下，如果我们决定天真地向Order类的所有属性(包括 setOrderTotal )添加 getter 和 setter 会发生什么。

没有什么可以阻止我们执行以下代码：

```java
Order order = new Order();
order.setOrderLines(Arrays.asList(orderLine0, orderLine1));
order.setTotalCost(Money.zero(CurrencyUnit.USD)); // this doesn't look good...
```

在此代码中，我们手动将totalCost属性设置为零，这违反了重要的业务规则。当然，总成本不应该是零美元！

我们需要一种方法来保护我们的业务规则。让我们看看聚合根如何提供帮助。

### 2.3. 聚合根

聚合根是一个类，它作为我们聚合的入口点。所有的业务操作都应该通过根。这样，聚合根可以负责将聚合保持在一致状态。

根负责处理我们所有的业务不变量。

在我们的示例中， Order类是聚合根的正确候选者。我们只需要进行一些修改以确保聚合始终一致：

```java
class Order {
    private final List<OrderLine> orderLines;
    private Money totalCost;

    Order(List<OrderLine> orderLines) {
        checkNotNull(orderLines);
        if (orderLines.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one order line item");
        }
        this.orderLines = new ArrayList<>(orderLines);
        totalCost = calculateTotalCost();
    }

    void addLineItem(OrderLine orderLine) {
        checkNotNull(orderLine);
        orderLines.add(orderLine);
        totalCost = totalCost.plus(orderLine.cost());
    }

    void removeLineItem(int line) {
        OrderLine removedLine = orderLines.remove(line);
        totalCost = totalCost.minus(removedLine.cost());
    }

    Money totalCost() {
        return totalCost;
    }

    // ...
}
```

现在使用聚合根可以让我们更轻松地将Product和OrderLine变成不可变对象，其中所有属性都是最终的。

正如我们所见，这是一个非常简单的聚合。

而且，我们可以在不使用字段的情况下每次都简单地计算总成本。

但是，现在我们只是在谈论聚合持久性，而不是聚合设计。请继续关注，因为这个特定的域很快就会派上用场。

这与持久性技术的关系如何？让我们来看看。最终，这将帮助我们为下一个项目选择正确的持久化工具。

## 3. JPA 和 Hibernate

在本节中，让我们尝试使用 JPA 和 Hibernate 来持久化我们的 Order聚合。我们将使用Spring Boot和[JPA](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-data-jpa) starter：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

对于我们大多数人来说，这似乎是最自然的选择。毕竟，我们已经在关系系统上工作了多年，而且我们都知道流行的 ORM 框架。

使用 ORM 框架时最大的问题可能是模型设计的简化。它有时也称为[对象关系阻抗失配](https://en.wikipedia.org/wiki/Object-relational_impedance_mismatch)。让我们考虑一下如果我们想要保留我们的 Order聚合会发生什么：

```java
@DisplayName("given order with two line items, when persist, then order is saved")
@Test
public void test() throws Exception {
    // given
    JpaOrder order = prepareTestOrderWithTwoLineItems();

    // when
    JpaOrder savedOrder = repository.save(order);

    // then
    JpaOrder foundOrder = repository.findById(savedOrder.getId())
      .get();
    assertThat(foundOrder.getOrderLines()).hasSize(2);
}
```

此时，这个测试会抛出一个异常： java.lang.IllegalArgumentException: Unknown entity: com.baeldung.ddd.order.Order。显然，我们遗漏了一些 JPA 要求：

1.  添加映射注解
2.  OrderLine和Product类必须是实体或@Embeddable类，而不是简单的值对象
3.  为每个实体或@Embeddable类添加一个空构造函数
4.  用简单类型替换Money属性

嗯，我们需要修改Order聚合的设计才能使用 JPA。虽然添加注解不是什么大问题，但其他要求可能会带来很多问题。

### 3.1. 对值对象的更改

尝试将聚合放入 JPA 的第一个问题是我们需要打破我们值对象的设计：它们的属性不再是最终的，我们需要打破封装。

我们需要向OrderLine和Product 添加人工 ID，即使这些类从未设计为具有标识符。我们希望它们是简单的值对象。

可以改用@Embedded和@ElementCollection注解，但这种方法在使用复杂对象图时会使事情复杂化很多(例如@Embeddable对象具有另一个@Embedded属性等)。

使用@Embedded注解只是将平面属性添加到父表。除此之外，基本属性(例如String类型)仍然需要setter方法，这违反了期望值对象设计。

空构造函数要求强制值对象属性不再是最终的，打破了我们原始设计的一个重要方面。说实话，Hibernate 可以使用私有的无参构造函数，这稍微缓解了这个问题，但还远非完美。

即使在使用私有默认构造函数时，我们也不能将我们的属性标记为 final，或者我们需要在默认构造函数中使用默认值(通常为 null)初始化它们。

但是，如果我们想要完全符合 JPA，我们必须至少为默认构造函数使用受保护的可见性，这意味着同一包中的其他类可以创建值对象而无需指定其属性的值。

### 3.2. 复杂类型

遗憾的是，我们不能期望 JPA 自动将第三方复杂类型映射到表中。看看我们在上一节中必须介绍多少更改！

例如，在使用我们的 订单聚合时，我们会遇到持久化 Joda Money字段的困难。

在这种情况下，我们最终可能会编写JPA 2.1 中可用的自定义类型@Converter 。不过，这可能需要一些额外的工作。

或者，我们也可以将Money属性拆分为两个基本属性。例如货币单位的String和实际值的BigDecimal。

虽然我们可以隐藏实现细节并仍然通过公共方法 API 使用Money类，但实践表明大多数开发人员无法证明额外的工作是合理的，而只会退化模型以符合 JPA 规范。

### 3.3. 总结

虽然 JPA 是世界上采用最广泛的规范之一，但它可能不是持久保存我们的 订单聚合的最佳选择。

如果我们希望我们的模型反映真实的业务规则，我们应该将其设计为不是底层表的简单 1:1 表示。

基本上，我们在这里有三个选择：

1.  创建一组简单的数据类并使用它们来保存和重新创建丰富的业务模型。不幸的是，这可能需要很多额外的工作。
2.  接受 JPA 的限制并选择正确的折衷方案。
3.  考虑另一种技术。

第一个选项具有最大的潜力。实际上，大多数项目都是使用第二种方案开发的。

现在，让我们考虑另一种持久化聚合的技术。

## 4.文档存储

文档存储是另一种存储数据的方式。我们不使用关系和表，而是保存整个对象。这使得文档存储成为持久聚合的潜在完美候选者。

出于本教程的需要，我们将重点关注类 JSON 文档。

让我们仔细看看我们的订单持久化问题在像 MongoDB 这样的文档存储中的表现。

### 4.1. 使用 MongoDB 持久化聚合

现在，有相当多的数据库可以存储 JSON 数据，其中一种流行的是 MongoDB。MongoDB 实际上以二进制形式存储 BSON 或 JSON。

感谢 MongoDB，我们可以按原样存储Order示例聚合。

在我们继续之前，让我们添加Spring Boot[MongoDB](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-data-mongodb) starter：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

现在我们可以运行与 JPA 示例中类似的测试用例，但这次使用 MongoDB：

```java
@DisplayName("given order with two line items, when persist using mongo repository, then order is saved")
@Test
void test() throws Exception {
    // given
    Order order = prepareTestOrderWithTwoLineItems();

    // when
    repo.save(order);

    // then
    List<Order> foundOrders = repo.findAll();
    assertThat(foundOrders).hasSize(1);
    List<OrderLine> foundOrderLines = foundOrders.iterator()
      .next()
      .getOrderLines();
    assertThat(foundOrderLines).hasSize(2);
    assertThat(foundOrderLines).containsOnlyElementsOf(order.getOrderLines());
}
```

重要的是——我们根本没有改变原来的Order聚合类； 无需为Money类创建默认构造函数、setter 或自定义转换器。

这是我们的 订单聚合出现在商店中的内容：

```javascript
{
  "_id": ObjectId("5bd8535c81c04529f54acd14"),
  "orderLines": [
    {
      "product": {
        "price": {
          "money": {
            "currency": {
              "code": "USD",
              "numericCode": 840,
              "decimalPlaces": 2
            },
            "amount": "10.00"
          }
        }
      },
      "quantity": 2
    },
    {
      "product": {
        "price": {
          "money": {
            "currency": {
              "code": "USD",
              "numericCode": 840,
              "decimalPlaces": 2
            },
            "amount": "5.00"
          }
        }
      },
      "quantity": 10
    }
  ],
  "totalCost": {
    "money": {
      "currency": {
        "code": "USD",
        "numericCode": 840,
        "decimalPlaces": 2
      },
      "amount": "70.00"
    }
  },
  "_class": "com.baeldung.ddd.order.mongo.Order"
}
```

这个简单的 BSON 文档包含了整个Order集合，非常符合我们最初的想法，即所有这些应该共同一致。

请注意，BSON 文档中的复杂对象被简单地序列化为一组常规 JSON 属性。由于这一点，即使是第三方类(如Joda Money)也可以轻松序列化，而无需简化模型。

### 4.2. 总结

使用 MongoDB 持久化聚合比使用 JPA 更简单。

这绝对不意味着 MongoDB 优于传统数据库。在很多合理的情况下，我们甚至不应该尝试将我们的类建模为聚合，而是使用 SQL 数据库。

尽管如此，当我们根据复杂的要求确定了一组应该始终保持一致的对象时，使用文档存储可能是一个非常有吸引力的选择。

## 5.总结

在 DDD 中，聚合通常包含系统中最复杂的对象。使用它们需要一种与大多数 CRUD 应用程序截然不同的方法。

使用流行的 ORM 解决方案可能会导致域模型过于简单或过度暴露，这通常无法表达或执行复杂的业务规则。

文档存储可以在不牺牲模型复杂性的情况下更容易地持久化聚合。