## 1. 简介

在本教程中，我们将学习如何使用[Spring Data](https://www.baeldung.com/spring-data-mongodb-tutorial)在MongoDB中定义唯一字段。唯一字段是数据库设计的重要组成部分，它们同时保证了一致性和性能，防止出现不应该出现的重复值。

## 2. 配置

与关系型数据库不同，MongoDB不提供创建约束的选项。**因此，我们唯一的选择是创建唯一[索引](https://www.baeldung.com/spring-data-mongodb-index-annotations-converter)**。但是，默认情况下，Spring Data中的自动索引创建是关闭的。首先，让我们继续在我们的application.properties中开启它：

```properties
spring.data.mongodb.auto-index-creation=true
```

使用该配置，如果索引尚不存在，则将在引导时创建索引。**但是，我们必须记住，我们不能在已经有重复值之后创建唯一索引**。这将导致在我们的应用程序启动期间抛出异常。

## 3. @Indexed注解

@Indexed注解允许我们将字段标记为具有索引。而且由于我们配置了自动索引创建，因此我们不必自己创建它们。**默认情况下，索引不是唯一的**；因此，我们必须通过unique属性将其开启。让我们通过创建第一个示例来查看它的实际效果：

```java
@Document
public class Company {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    // getters and setters
}
```

请注意，我们仍然可以使用@Id注解，它完全独立于我们的索引。这就是我们拥有一个具有唯一字段的文档所需要的一切。因此，如果我们插入多个具有相同email的文档，则会导致DuplicateKeyException：

```java
@Test
void givenUniqueIndex_whenInsertingDupe_thenExceptionIsThrown() {
    Company a = new Company();
    a.setName("Name");
    a.setEmail("a@mail.com");
    
    companyRepo.insert(a);
    
    Company b = new Company();
    b.setName("Other");
    b.setEmail("a@mail.com");
    
    assertThrows(DuplicateKeyException.class, () -> companyRepo.insert(b));
}
```

当我们想要强制唯一性但仍然有一个自动生成的唯一ID字段时，这种方法很有用。

### 3.1 标注多个字段

我们还可以将注解添加到多个字段。让我们继续创建第二个示例：

```java
@Document
public class Asset {
    @Indexed(unique = true)
    private String name;

    @Indexed(unique = true)
    private Integer number;
}
```

请注意，我们没有在任何字段上明确设置@Id，MongoDB仍然会自动为我们设置一个“_id”字段，但我们的应用程序无法访问它。**但是，我们不能将@Id与标记为唯一的@Indexed注解放在同一字段上**。当应用程序尝试创建索引时，它会抛出异常。

此外，现在我们有两个特殊的字段。**请注意，这并不意味着它是一个复合索引**。因此，对任何字段多次插入相同的值都将导致重复键。让我们测试一下：

```java
@Test
void givenMultipleIndexes_whenAnyFieldDupe_thenExceptionIsThrown() {
    Asset a = new Asset();
    a.setName("Name");
    a.setNumber(1);
    
    assetRepo.insert(a);
    
    Asset b = new Asset();
    b.setName("Name");
    b.setNumber(2);
    assertThrows(DuplicateKeyException.class, () -> assetRepo.insert(b));
    
    Asset c = new Asset();
    c.setName("Other");
    c.setNumber(1);
    assertThrows(DuplicateKeyException.class, () -> assetRepo.insert(c));
}
```

如果我们只想让组合值形成一个唯一的索引，我们必须创建一个复合索引。

### 3.2 使用自定义类型作为索引

**同样，我们可以对自定义类型的字段进行标注，这样就达到了复合索引的效果**。让我们从一个SaleId类开始来表示我们的复合索引：

```java
public class SaleId {
    private Long item;
    private String date;

    // getters and setters
}
```

现在让我们创建我们的Sale类来使用它：

```java
@Document
public class Sale {
    @Indexed(unique = true)
    private SaleId saleId;

    private Double value;

    // getters and setters
}
```

现在，每次我们尝试添加具有相同SaleId的新Sale时，我们都会得到DuplicateKeyException。让我们测试一下：

```java
@Test
void givenCustomTypeIndex_whenInsertingDupe_thenExceptionIsThrown() {
    SaleId id = new SaleId();
    id.setDate("2022-06-15");
    id.setItem(1L);
    
    Sale a = new Sale(id);
    a.setValue(53.94);
    
    saleRepo.insert(a);
    
    Sale b = new Sale(id);
    b.setValue(100.00);
    assertThrows(DuplicateKeyException.class, () -> saleRepo.insert(b));
}
```

这种方法的优点是使索引定义保持独立。**这使我们能够在SaleId中包含或删除新字段，而无需重新创建或更新我们的索引**。它也非常类似于[复合键](https://www.baeldung.com/spring-data-mongodb-composite-key)。但是，索引与主键不同，因为它们可以有一个null值。

## 4. @CompoundIndex注解

要拥有一个由多个字段组成且不使用自定义类的唯一索引，我们必须创建一个复合索引。**为此，我们直接在类中使用@CompoundIndex注解**。这个注解包含一个def属性，我们将使用该属性来包含我们需要的字段。让我们创建我们的Customer类，为storeId和number字段定义一个唯一索引：

```java
@Document
@CompoundIndex(def = "{'storeId': 1, 'number': 1}", unique = true)
public class Customer {
    @Id
    private String id;

    private Long storeId;
    private Long number;
    private String name;

    // getters and setters
}
```

这与在多个字段上使用@Indexed不同，仅当我们尝试插入具有相同storeId和number值的Customer时，此方法才会导致DuplicateKeyException：

```java
@Test
void givenCompoundIndex_whenDupeInsert_thenExceptionIsThrown() {
    Customer customerA = new Customer("Name A");
    customerA.setNumber(1L);
    customerA.setStoreId(2L);
    
    Customer customerB = new Customer("Name B");
    customerB.setNumber(1L);
    customerB.setStoreId(2L);
    
    customerRepo.insert(customerA);
    
    assertThrows(DuplicateKeyException.class, () -> customerRepo.insert(customerB));
}
```

使用这种方法，我们的优势在于不必为我们的索引创建另一个类。**此外，还可以将@Id注解添加到复合索引定义中的字段**。但是，与@Indexed不同，它不会导致异常。

## 5. 总结

在本文中，我们学习了如何为文档定义唯一字段。**因此，我们了解到我们唯一的选择是使用唯一索引**。此外，使用Spring Data，我们可以轻松地配置我们的应用程序以自动创建我们的索引。而且，我们看到了使用@Indexed和@CompoundIndex注解的多种方法。