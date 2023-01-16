## 1. 简介

这个快速的 Hibernate 教程将带我们通过一个 使用 JPA 注解的一对多映射示例，它是 XML 的替代方法。

我们还将了解什么是双向关系，它们如何造成不一致，以及所有权的概念如何提供帮助。

## 延伸阅读：

## [带休眠功能的 Spring Boot](https://www.baeldung.com/spring-boot-hibernate)

集成Spring Boot和 Hibernate/JPA 的快速、实用的介绍。

[阅读更多](https://www.baeldung.com/spring-boot-hibernate)→

## [Hibernate/JPA 标识符概述](https://www.baeldung.com/hibernate-identifiers)

了解如何使用 Hibernate 映射实体标识符。

[阅读更多](https://www.baeldung.com/hibernate-identifiers)→

## 2.说明

简单的说，一对多映射就是一个表中的一行映射到另一个表中的多行。

让我们看下面的实体关系图来了解一对多关联：

[![C-1](https://www.baeldung.com/wp-content/uploads/2017/02/C-1.png)](https://www.baeldung.com/wp-content/uploads/2017/02/C-1.png)

对于这个例子，我们将实现一个购物车系统，其中每个购物车都有一个表，每个项目都有另一个表。一个购物车可以有很多物品，所以这里我们有一个一对多的映射。

这在数据库级别的工作方式是我们将cart_id作为cart表中的主键，并将cart_id 作为items中的外键 。

我们在代码中的做法是使用@OneToMany。

让我们 以反映数据库中关系的方式将Cart类映射到Item对象的集合 ：

```java
public class Cart {

    //...     
 
    @OneToMany(mappedBy="cart")
    private Set<Item> items;
	
    //...
}
```

 我们还可以使用@ManyToOne在每个 Item中添加对Cart 的引用，使其成为 [双向](https://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch07.html#collections-bidirectional)关系。双向意味着我们可以 从购物车访问商品，也可以 从商品访问购物车。

mappedBy属性是我们用来告诉 Hibernate 我们使用哪个变量来表示子类中的父类的。

为了开发实现一对多关联的示例 Hibernate 应用程序，使用了以下技术和库：

-   JDK 1.8 或更高版本
-   休眠5
-   Maven 3 或更高版本
-   H2数据库

## 3.设置

### 3.1. 数据库设置

我们将使用 Hibernate 从域模型管理我们的模式。换句话说，我们不需要提供 SQL 语句来创建实体之间的各种表和关系。那么让我们继续创建 Hibernate 示例项目。

### 3.2. Maven 依赖项

让我们首先将 Hibernate 和 H2 驱动程序依赖项添加到我们的pom.xml文件中。Hibernate 依赖项使用 JBoss 日志记录，它会自动添加为传递依赖项：

-   休眠版本5.6.7.Final
-   H2 驱动程序版本2.1.212

请访问 Maven 中央存储库以获取最新版本的[Hibernate](https://search.maven.org/classic/#search|ga|1|a%3A"hibernate-core")和[H2](https://search.maven.org/classic/#search|ga|1|g%3A"com.h2database")依赖项。

### 3.3. Hibernate会话工厂

接下来，让我们为我们的数据库交互创建 Hibernate SessionFactory ：

```java
public static SessionFactory getSessionFactory() {

    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
      .applySettings(dbSettings())
      .build();

    Metadata metadata = new MetadataSources(serviceRegistry)
      .addAnnotatedClass(Cart.class)
      // other domain classes
      .buildMetadata();

    return metadata.buildSessionFactory();
}

private static Map<String, String> dbSettings() {
    // return Hibernate settings
}
```

## 4.模型

与映射相关的配置将使用模型类中的 JPA 注解完成：

```java
@Entity
@Table(name="CART")
public class Cart {

    //...

    @OneToMany(mappedBy="cart")
    private Set<Item> items;
	
    // getters and setters
}
```

请注意，@OneToMany注解用于定义Item 类中的属性，该属性将用于映射mappedBy变量。这就是为什么我们在Item 类中有一个名为“ cart ”的属性：

```java
@Entity
@Table(name="ITEMS")
public class Item {
    
    //...
    @ManyToOne
    @JoinColumn(name="cart_id", nullable=false)
    private Cart cart;

    public Item() {}
    
    // getters and setters
}

```

还需要注意的是，@ManyToOne注解与Cart类变量相关联。@JoinColumn注解引用映射的列。

## 5. 行动中

在测试程序中，我们正在创建一个带有main ()方法的类，用于获取Hibernate Session，并将模型对象保存到数据库中，实现一对多关联：

```java
sessionFactory = HibernateAnnotationUtil.getSessionFactory();
session = sessionFactory.getCurrentSession();
System.out.println("Session created");
	    
tx = session.beginTransaction();

session.save(cart);
session.save(item1);
session.save(item2);
	    
tx.commit();
System.out.println("Cart ID=" + cart.getId());
System.out.println("item1 ID=" + item1.getId()
  + ", Foreign Key Cart ID=" + item.getCart().getId());
System.out.println("item2 ID=" + item2.getId()
+ ", Foreign Key Cart ID=" + item.getCart().getId());
```

这是我们测试程序的输出：

```bash
Session created
Hibernate: insert into CART values ()
Hibernate: insert into ITEMS (cart_id)
  values (?)
Hibernate: insert into ITEMS (cart_id)
  values (?)
Cart ID=7
item1 ID=11, Foreign Key Cart ID=7
item2 ID=12, Foreign Key Cart ID=7
Closing SessionFactory
```

## 6. @ManyToOne注解

正如我们在第 2 节中看到的，我们可以使用@ManyToOne注解指定多对一关系。多对一映射意味着该实体的许多实例映射到另一个实体的一个实例——一个购物车中的许多项目。

@ManyToOne注解也让我们可以创建双向关系。我们将在接下来的几小节中详细介绍这一点。

### 6.1. 不一致和所有权

现在，如果Cart引用Item，但 Item 没有反过来引用Cart，我们的关系将是单向的。这些对象也将具有自然的一致性。

但在我们的例子中，这种关系是双向的，这会带来不一致的可能性。

让我们想象这样一种情况，开发人员想要将 item1添加到 cart1 实例并将 item2 添加到 cart2 实例，但是犯了一个错误，导致cart2和item2之间的引用变得不一致：

```java
Cart cart1 = new Cart();
Cart cart2 = new Cart();

Item item1 = new Item(cart1);
Item item2 = new Item(cart2); 
Set<Item> itemsSet = new HashSet<Item>();
itemsSet.add(item1);
itemsSet.add(item2); 
cart1.setItems(itemsSet); // wrong!
```

如上所示，item2引用了cart2，而cart2 没有引用item2，这很糟糕。

Hibernate应该如何将 item2保存到数据库中呢？item2外键会引用cart1还是cart2？

我们使用关系的拥有方的想法来解决这种歧义；属于拥有方的引用优先并保存到数据库中。

### 6.2. 作为拥有方的项目

[正如第 2.9 节下的JPA 规范](https://download.oracle.com/otndocs/jcp/persistence-2.0-fr-eval-oth-JSpec/)中所述，将多对一端标记 为拥有端是一种很好的做法。

换句话说，I tem 是拥有方，而Cart是相反方，这正是我们之前所做的。

那么我们是如何做到这一点的呢？

通过在Cart类中包含mappedBy属性，我们将其标记为反面。

同时，我们也对Item进行注解。带有@ManyToOne的购物车字段，使 Item 成为拥有方。

回到我们的“不一致”示例，现在 Hibernate 知道item2的引用更重要，并将保存 item2对数据库的引用。

让我们检查一下结果：

```java
item1 ID=1, Foreign Key Cart ID=1
item2 ID=2, Foreign Key Cart ID=2
```

尽管cart在我们的代码片段中引用了item2， 但 item2对cart2的引用保存在数据库中。

### 6.3. 购物车作为拥有方

也可以将 一对多的一侧标记为拥有侧，将 多对一的一侧标记为反向。

虽然这不是推荐的做法，但让我们继续尝试一下。

下面的代码片段显示了一对多方作为拥有方的实现 ：

```java
public class ItemOIO {
    
    //  ...
    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private CartOIO cart;
    //..
}

public class CartOIO {
    
    //..  
    @OneToMany
    @JoinColumn(name = "cart_id") // we need to duplicate the physical information
    private Set<ItemOIO> items;
    //..
}

```

请注意我们如何删除mappedBy元素并将多对一 @JoinColumn设置为可插入和可更新的false。

如果我们运行相同的代码，结果将相反：

```plaintext
item1 ID=1, Foreign Key Cart ID=1
item2 ID=2, Foreign Key Cart ID=1
```

如上所示，现在item2属于 购物车。

## 七. 总结

我们已经看到使用 JPA 注解实现与 Hibernate ORM 和 H2 数据库的一对多关系是多么容易。

此外，我们了解了双向关系以及如何实施拥有方的概念。