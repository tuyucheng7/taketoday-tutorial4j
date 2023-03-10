## 1. 简介

**乍一看，@NotNull和@Column(nullable = false)注解似乎都有相同的用途，并且可以互换使用**。然而，正如我们很快就会看到的，这并不完全正确。

尽管在JPA实体上使用时，**它们都基本上用于防止在底层数据库中存储空值，但这两种方法之间存在显著差异**。

在本快速教程中，我们将比较@NotNull和@Column(nullable = false)约束。

## 2. 依赖关系

对于所有呈现的示例，我们将使用一个简单的[Spring Boot](https://www.baeldung.com/spring-boot)应用程序。

下面是pom.xml文件的相关部分，其中显示了所需的依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
</dependencies>
```

### 2.1 示例实体

我们还定义一个非常简单的实体，我们将在整个教程中使用它：

```java
@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal price;
}
```

## 3. @NotNull注解

**@NotNull注解在[Bean Validation](https://www.baeldung.com/javax-validation)规范中定义**，这意味着它的使用不仅限于实体。相反，我们也可以在任何其他bean上使用@NotNull注解。

不过，让我们坚持使用我们的用例，并将@NotNull注解添加到Item的prie字段：

```java
@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private BigDecimal price;
}
```

现在，让我们尝试保存一个价格为空的商品：

```java
@SpringBootTest
public class ItemIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void shouldNotAllowToPersistNullItemsPrice() {
        itemRepository.save(new Item());
    }
}
```

让我们看看Hibernate的输出：

```shell
Caused by: javax.validation.ConstraintViolationException: Validation failed for classes 
[cn.tuyucheng.taketoday.h2db.notnull.models.Item] during persist time for groups 
[javax.validation.groups.Default, ]
List of constraint violations:[
	ConstraintViolationImpl{interpolatedMessage='不能为null', propertyPath=price, 
	rootBeanClass=class cn.tuyucheng.taketoday.h2db.notnull.models.Item, 
	messageTemplate='{javax.validation.constraints.NotNull.message}'}
```

正如我们所看到的，**在这种情况下，我们的系统抛出了javax.validation.ConstraintViolationException**。

重要的是要注意Hibernate没有触发SQL插入语句。因此，无效数据不会保存到数据库中。

这是因为在将查询发送到数据库之前，pre-persist实体生命周期事件触发了bean验证。

### 3.1 Schema生成

在上一节中，我们介绍了@NotNull验证的工作原理。

现在让我们看看**如果让Hibernate为我们生成数据库Schema会发生什么**。

因此，我们将在application.properties文件中设置几个属性：

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

如果我们现在启动我们的应用程序，我们将看到DDL语句：

```sql
create table item
(
    id    bigint         not null,
    price decimal(19, 2) not null,
    primary key (id)
)
```

令人惊讶的是，**Hibernate自动将not null约束添加到price列定义中**。

这怎么可能？

事实证明，**Hibernate开箱即用地将应用于实体的bean验证注解转换为DDL模式元数据**。

这非常方便并且很有意义。如果我们将@NotNull应用于实体，我们很可能也希望使相应的数据库列不为空。

然而，如果出于任何原因，我们**想要禁用此Hibernate功能，我们需要做的就是将hibernate.validator.apply_to_ddl属性设置为false**。

为了测试这一点，让我们更新我们的application.properties：

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.validator.apply_to_ddl=false
```

让我们运行应用程序并查看DDL语句：

```sql
create table "item"
(
    "id"    bigint         not null,
    "price" numeric(19, 2) not null,
    primary key ("id")
)
```

正如预期的那样，**这次Hibernate没有将not null约束添加到price列**。

## 4. @Column(nullable = false)注解

**@Column注解被定义为[Java Persistence API](https://jcp.org/en/jsr/detail?id=338)规范的一部分**。

它主要用于DDL模式元数据生成。这意味着**如果我们让Hibernate自动生成数据库模式，它会将not null约束应用于特定的数据库列**。

让我们使用@Column(nullable = false)更新我们的Item实体，看看它是如何工作的：

```java
@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;
}
```

现在，我们可以尝试保存一个空price值：

```java
@SpringBootTest
public class ItemIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void shouldNotAllowToPersistNullItemsPrice() {
        itemRepository.save(new Item());
    }
}
```

这是Hibernate输出的片段：

```shell
Hibernate: 
    
    create table "item" (
       "id" bigint not null,
        "price" numeric(19,2) not null,
        primary key ("id")
    )

(...)

Hibernate: 
    insert 
    into
        "item"
        ("price", "id") 
    values
        (?, ?)
18:28:32.791 [main] WARN  [o.h.e.jdbc.spi.SqlExceptionHelper] >>> SQL Error: 23502, SQLState: 23502 
18:28:32.791 [main] ERROR [o.h.e.jdbc.spi.SqlExceptionHelper] >>> NULL not allowed for column "price"; SQL statement:
insert into "item" ("price", "id") values (?, ?) [23502-214] 
```

首先，我们可以注意到**Hibernate如我们预期的那样生成了带有not null约束的price列**。

此外，它还能够创建SQL插入查询并将其传递。因此，**是底层数据库触发了错误**。

### 4.1 验证

几乎所有来源都强调@Column(nullable = false)仅用于模式DDL生成。

但是，**Hibernate能够针对可能的空值执行实体验证，即使相应的字段仅使用@Column(nullable = false)注解也是如此**。

为了激活这个Hibernate特性，我们需要显式地将hibernate.check_nullability属性设置为true：

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.check_nullability=true
```

现在让我们再次执行我们的测试用例并检查输出：

```shell
org.springframework.dao.DataIntegrityViolationException: 
not-null property references a null or transient value : cn.tuyucheng.taketoday.h2db.notnull.models.Item.price; 
nested exception is org.hibernate.PropertyValueException: 
not-null property references a null or transient value : cn.tuyucheng.taketoday.h2db.notnull.models.Item.price
```

这一次，我们的测试用例抛出了org.hibernate.PropertyValueException。

重要的是要注意，**在这种情况下，Hibernate没有将插入SQL查询发送到数据库**。

## 5. 总结

在本文中，我们描述了@NotNull和@Column(nullable = false)注解的工作原理。

尽管它们都可以防止我们在数据库中存储空值，但它们采用了不同的方法。

**根据经验，我们应该更倾向于@NotNull注解而不是@Column(nullable = false)注解**。这样，我们确保在Hibernate向数据库发送任何插入或更新SQL查询之前进行验证。

此外，**通常最好依赖Bean Validation中定义的标准规则**，而不是让数据库处理验证逻辑。

但是，即使我们让Hibernate生成数据库模式，**它也会将@NotNull注解转换为数据库约束。然后我们必须确保hibernate.validator.apply_to_ddl属性设置为true**。