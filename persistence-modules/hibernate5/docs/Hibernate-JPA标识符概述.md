## 1. 概述

Hibernate 中的标识符表示实体的主键。这意味着这些值是唯一的，因此它们可以识别特定实体，它们不为空并且不会被修改。

Hibernate 提供了几种不同的方式来定义标识符。在本文中，我们将回顾使用该库映射实体 ID 的每种方法。

## 2. 简单标识符

定义标识符最直接的方法是使用@Id注解。

使用@Id将简单 ID 映射到以下类型之一的单个属性：Java 原始类型和原始包装类型、String、Date、BigDecimal和BigInteger。

让我们看一个使用long类型的主键定义实体的快速示例：

```java
@Entity
public class Student {

    @Id
    private long studentId;
    
    // standard constructor, getters, setters
}
```

## 3.生成的标识符

如果我们想自动生成主键值，可以添加@GeneratedValue注解。

这可以使用四种生成类型：AUTO、IDENTITY、SEQUENCE 和 TABLE。

如果我们没有明确指定一个值，生成类型默认为 AUTO。

### 3.1. 自动生成

如果我们使用默认生成类型，持久性提供程序将根据主键属性的类型确定值。此类型可以是数字或UUID。

对于数值，生成基于序列或表生成器，而UUID值将使用UUIDGenerator。

让我们首先使用自动生成策略映射实体主键：

```java
@Entity
public class Student {

    @Id
    @GeneratedValue
    private long studentId;

    // ...
}
```

在这种情况下，主键值在数据库级别将是唯一的。

现在我们来看看Hibernate 5 中引入的UUIDGenerator 。

为了使用这个特性，我们只需要用@GeneratedValue注解声明一个UUID类型的id ：

```java
@Entity
public class Course {

    @Id
    @GeneratedValue
    private UUID courseId;

    // ...
}
```

Hibernate 将生成一个形式为“8dd5f315-9788-4d00-87bb-10eed9eff566”的 id。

### 3.2. 身份生成

这种类型的生成依赖于IdentityGenerator，它需要由数据库中的标识列生成的值。这意味着它们是自动递增的。

要使用这个生成类型，我们只需要设置策略参数：

```java
@Entity
public class Student {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long studentId;

    // ...
}
```

需要注意的一件事是 IDENTITY 生成禁用批量更新。

### 3.3. 序列生成

为了使用基于序列的 id，Hibernate 提供了SequenceStyleGenerator类。

如果我们的数据库支持的话，这个生成器会使用序列。如果不支持，它会切换到表生成。

为了自定义序列名称，我们可以使用 带有SequenceStyleGenerator 策略的@GenericGenerator注解：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
      name = "sequence-generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "user_sequence"),
        @Parameter(name = "initial_value", value = "4"),
        @Parameter(name = "increment_size", value = "1")
        }
    )
    private long userId;
    
    // ...
}
```

在这个例子中，我们还为序列设置了一个初始值，这意味着主键生成将从 4 开始。

SEQUENCE是 Hibernate 文档推荐的生成类型。

每个序列生成的值都是唯一的。如果我们不指定序列名称，Hibernate 将对不同的类型重用相同的hibernate_sequence。

### 3.4. 表格生成

TableGenerator使用一个底层数据库表来保存标识符生成值的段。

让我们使用@TableGenerator注解自定义表名：

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, 
      generator = "table-generator")
    @TableGenerator(name = "table-generator", 
      table = "dep_ids", 
      pkColumnName = "seq_id", 
      valueColumnName = "seq_value")
    private long depId;

    // ...
}
```

在这个例子中，我们可以看到我们还可以自定义其他属性，例如pkColumnName和valueColumnName。

但是，这种方法的缺点是它不能很好地扩展并且会对性能产生负面影响。

综上所述，这四种生成类型将导致生成相似的值但使用不同的数据库机制。

### 3.5. 自定义生成器

假设我们不想使用任何现成的策略。为此，我们可以通过实现IdentifierGenerator接口来定义我们的自定义生成器。

我们将创建一个生成器来构建包含字符串前缀和数字的标识符：

```java
public class MyGenerator 
  implements IdentifierGenerator, Configurable {

    private String prefix;

    @Override
    public Serializable generate(
      SharedSessionContractImplementor session, Object obj) 
      throws HibernateException {

        String query = String.format("select %s from %s", 
            session.getEntityPersister(obj.getClass().getName(), obj)
              .getIdentifierPropertyName(),
            obj.getClass().getSimpleName());

        Stream ids = session.createQuery(query).stream();

        Long max = ids.map(o -> o.replace(prefix + "-", ""))
          .mapToLong(Long::parseLong)
          .max()
          .orElse(0L);

        return prefix + "-" + (max + 1);
    }

    @Override
    public void configure(Type type, Properties properties, 
      ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
    }
}
```

在此示例中，我们覆盖了IdentifierGenerator接口中的generate()方法。

首先，我们要从prefix-XX形式的现有主键中找到最大的数字。然后我们将找到的最大数加 1 并附加前缀属性以获得新生成的 id 值。

我们的类还实现了Configurable接口，以便我们可以在configure()方法中设置前缀属性值。

接下来，让我们将这个自定义生成器添加到一个实体中。

为此，我们可以使用带有策略参数的@GenericGenerator注解，该策略参数包含生成器类的完整类名：

```java
@Entity
public class Product {

    @Id
    @GeneratedValue(generator = "prod-generator")
    @GenericGenerator(name = "prod-generator", 
      parameters = @Parameter(name = "prefix", value = "prod"), 
      strategy = "com.baeldung.hibernate.pojo.generator.MyGenerator")
    private String prodId;

    // ...
}
```

另外，请注意我们已将前缀参数设置为“prod”。

让我们看一个快速的 JUnit 测试，以便更清楚地了解生成的 id 值：

```java
@Test
public void whenSaveCustomGeneratedId_thenOk() {
    Product product = new Product();
    session.save(product);
    Product product2 = new Product();
    session.save(product2);

    assertThat(product2.getProdId()).isEqualTo("prod-2");
}
```

这里使用“prod”前缀生成的第一个值是“prod-1”，然后是“prod-2”。

## 4.复合标识符

除了到目前为止我们看到的简单标识符，Hibernate 还允许我们定义复合标识符。

复合 id 由具有一个或多个持久属性的主键类表示。

主键类必须满足几个条件：

-   它应该使用@EmbeddedId或@IdClass注解来定义。
-   它应该是公共的、可序列化的并且有一个公共的无参数构造函数。
-   最后，它应该实现equals()和hashCode()方法。

类的属性可以是基本的、复合的或多对一的，同时避免集合和一对一的属性。

### 4.1. @EmbeddedId

现在让我们看看如何使用@EmbeddedId定义一个 id 。

首先，我们需要一个用@Embeddable注解的主键类：

```java
@Embeddable
public class OrderEntryPK implements Serializable {

    private long orderId;
    private long productId;

    // standard constructor, getters, setters
    // equals() and hashCode() 
}
```

接下来，我们可以使用 @EmbeddedId 将 OrderEntryPK 类型的 id添加到实体：

```java
@Entity
public class OrderEntry {

    @EmbeddedId
    private OrderEntryPK entryId;

    // ...
}
```

让我们看看如何使用这种类型的复合 id 来设置实体的主键：

```java
@Test
public void whenSaveCompositeIdEntity_thenOk() {
    OrderEntryPK entryPK = new OrderEntryPK();
    entryPK.setOrderId(1L);
    entryPK.setProductId(30L);
        
    OrderEntry entry = new OrderEntry();
    entry.setEntryId(entryPK);
    session.save(entry);

    assertThat(entry.getEntryId().getOrderId()).isEqualTo(1L);
}
```

这里的OrderEntry对象有一个OrderEntryPK主 ID，它由两个属性组成：orderId和productId。

### 4.2. @IdClass

@IdClass注解类似于@EmbeddedId 。与@IdClass的不同之处在于属性是在主实体类中定义的，每个属性都使用@Id 。主键类看起来和以前一样。

让我们用@IdClass重写OrderEntry示例：

```java
@Entity
@IdClass(OrderEntryPK.class)
public class OrderEntry {
    @Id
    private long orderId;
    @Id
    private long productId;
    
    // ...
}
```

然后我们可以直接在OrderEntry对象上设置 id 值：

```java
@Test
public void whenSaveIdClassEntity_thenOk() {        
    OrderEntry entry = new OrderEntry();
    entry.setOrderId(1L);
    entry.setProductId(30L);
    session.save(entry);

    assertThat(entry.getOrderId()).isEqualTo(1L);
}
```

请注意，对于这两种类型的复合 id，主键类还可以包含@ManyToOne属性。

Hibernate 还允许定义由@ManyToOne关联和@Id注解组合而成的主键。在这种情况下，实体类也应该满足主键类的条件。

但是，这种方法的缺点是实体对象和标识符之间没有分离。

## 5.派生标识符

派生标识符是使用@MapsId注解从实体的关联中获取的。

首先，让我们创建一个UserProfile实体，它从与User实体的一对一关联中派生其 id ：

```java
@Entity
public class UserProfile {

    @Id
    private long profileId;
    
    @OneToOne
    @MapsId
    private User user;

    // ...
}
```

接下来，让我们验证UserProfile实例与其关联的User实例具有相同的 id ：

```java
@Test
public void whenSaveDerivedIdEntity_thenOk() {        
    User user = new User();
    session.save(user);
       
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    session.save(profile);

    assertThat(profile.getProfileId()).isEqualTo(user.getUserId());
}
```

## 六. 总结

在本文中，我们了解了在 Hibernate 中定义标识符的多种方式。