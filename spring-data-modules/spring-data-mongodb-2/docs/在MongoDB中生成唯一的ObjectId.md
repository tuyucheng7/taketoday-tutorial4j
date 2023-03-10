## 1. 简介

在本文中，我们将讨论什么是ObjectId、如何生成它以及确保其唯一性的可能方法。

## 2. ObjectId一般信息

让我们首先解释什么是ObjectId。**ObjectId是一个12字节的十六进制值**，是[BSON规范](https://bsonspec.org/)中可能的数据类型之一。BSON是JSON文档的二进制序列化。此外，MongoDB使用ObjectId作为文档中\_id字段的默认标识符。在创建集合时设置的\_id字段上还有一个默认的唯一索引。

这可以防止用户插入两个具有相同\_id的文档。此外，不能从集合中删除\_id索引。但是，可以将具有相同\_id的单个文档插入到两个集合中。

### 2.1 ObjectId结构

ObjectId可以分为三个不同的部分。考虑一个6359388c80616b1fc6d7ec71的ObjectId，第一部分将包含4个字节–6359388c。这4个字节表示自Unix纪元以来的时间(以秒为单位)。第二部分由接下来的5个字节组成，即80616b1fc6。这些字节代表每个进程生成一次的随机值。随机值对于机器和进程是唯一的。最后一部分是3个字节的d7ec71，它表示一个从随机值开始的递增计数器。

另外值得一提的是，上述结构对MongoDB 4.0及以上版本有效。在此之前，ObjectId由四部分构造。前4个字节表示自Unix纪元以来的秒数，接下来的三个字节用于机器标识符。

进程ID的下2个字节和计数器的最后3个字节从随机值开始。

### 2.2 ObjectId唯一性

最重要的是，在MongoDB文档中也提到了，**ObjectId在生成时很可能被认为是唯一的**。也就是说，生成重复的ObjectId的可能性非常小。查看ObjectId的结构，我们可以看到在一秒内生成ObjectId的可能性超过1,8×10^19。

即使所有id都是在同一台机器上、同一进程中、同一秒内生成的，仅计数器本身就有超过1700万种可能性。

## 3. ObjectId的创建

在Java中有多种创建ObjectId的方法。它可以使用非参数或参数化构造函数来完成。

### 3.1 使用非参数化构造函数创建ObjectId

第一个也是最简单的一个是通过带有非参数化构造函数的new关键字：

```java
ObjectId objectId = new ObjectId();
```

第二个是在ObjectId类上简单地调用静态方法get()。不直接调用非参数化构造函数。但是，get()方法的实现包括创建ObjectId，与第一个示例中的相同-通过new关键字：

```java
ObjectId objectId = ObjectId.get();
```

### 3.2 使用参数化构造函数创建ObjectId

其余示例使用参数化构造函数。我们可以通过将Date类作为参数传递或同时传递Date类和int计数器来创建ObjectId。如果我们尝试在两种方法中创建具有相同Date的ObjectId，则new ObjectId(date)与new ObjectId(date, counter)将获得不同的ObjectId。

但是，如果我们在同一秒内通过new ObjectId(date, counter)创建两个ObjectId，我们将得到一个重复的ObjectId，因为它是在同一秒内在同一台机器上使用相同的计数器生成的。让我们看一个例子：

```java
@Test
public void givenSameDateAndCounter_whenComparingObjectIds_thenTheyAreNotEqual() {
    Date date = new Date();
    ObjectId objectIdDate = new ObjectId(date); // 635981f6e40f61599e839ddb
    ObjectId objectIdDateCounter1 = new ObjectId(date, 100); // 635981f6e40f61599e000064
    ObjectId objectIdDateCounter2 = new ObjectId(date, 100); // 635981f6e40f61599e000064

    assertThat(objectIdDate).isNotEqualTo(objectIdDateCounter1);
    assertThat(objectIdDate).isNotEqualTo(objectIdDateCounter2);

    assertThat(objectIdDateCounter1).isEqualTo(objectIdDateCounter2);
}
```

此外，还可以通过直接提供十六进制值作为参数来创建ObjectId：

```java
ObjectId objectIdHex = new ObjectId("635981f6e40f61599e000064");
```

还有更多创建ObjectId的可能性。我们可以传递byte[]或ByteBuffer类。如果我们通过将字节数组传递给构造函数来创建ObjectId，我们应该通过使用相同的字节数组通过ByteBuffer类创建它来获得相同的ObjectId。

让我们看一个例子：

```java
@Test
public void givenSameArrayOfBytes_whenComparingObjectIdsCreatedViaDifferentMethods_thenTheObjectIdsAreEqual(){
    byte[] bytes = "123456789012".getBytes();
    ObjectId objectIdBytes = new ObjectId(bytes);

    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    ObjectId objectIdByteBuffer = new ObjectId(buffer);

    assertThat(objectIdBytes).isEqualTo(objectIdByteBuffer);
}
```

最后一种可能的方法是通过将时间戳和计数器传递给构造函数来创建ObjectId。

## 4. ObjectId的优缺点

与所有事物一样，有利也有弊，值得了解。

### 4.1 ObjectId的好处

由于ObjectId的长度为12字节，因此它小于16字节的UUID。也就是说，如果我们在数据库中有很多文档使用ObjectId而不是UUID，我们可以节省一些空间。与UUID相比，使用ObjectId大约26500次将节省大约1MB。这似乎是最小的数量。

尽管如此，如果数据库足够大并且单个文档也可能多次出现ObjectId，那么**磁盘空间和RAM的增益**可能会很大，因为文档最终会更小。其次，正如我们之前了解到的，时间戳被嵌入到ObjectId中，这在某些情况下可能很有用。

例如，要确定首先创建了哪个ObjectId，假设它们都是自动生成的，而不是像我们之前看到的那样通过将Date类操作到参数化构造函数中来创建的。

### 4.2 ObjectId的缺点

另一方面，有些标识符甚至比12字节的ObjectId还小，这同样可以节省更多的磁盘空间和RAM。此外，由于ObjectId只是一个生成的十六进制值，这意味着**可能存在重复的id**。它非常精确，但仍然是可能的。

## 5. 保证ObjectId的唯一性

如果我们必须确保生成的ObjectId是唯一的，我们可以尝试围绕它进行一些编程，以使其100%确保它不是重复的。

### 5.1 尝试捕获DuplicateKeyException

假设我们插入一个数据库中已包含此_id字段的文档。在这种情况下，**我们可以捕获DuplicateKeyException并重试插入操作，直到成功为止**。此方法仅适用于**创建了唯一索引的字段**。

让我们看一个例子。考虑一个用户类：

```java
public class User {
    public static final String NAME_FIELD = "name";

    private final ObjectId id;
    private final String name;

    // constructor
    // getters
}
```

我们将一个用户插入到数据库中，然后尝试插入另一个具有相同ObjectId的用户。这将导致抛出DuplicateKeyException。我们可以捕获它并重试User的插入操作。但是，这一次，我们将生成另一个ObjectId。出于此测试的目的，我们将使用[嵌入式MongoDB](https://www.baeldung.com/spring-boot-embedded-mongodb)库和[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)。

让我们看一个例子：

```java
@Test
public void givenUserInDatabase_whenInsertingAnotherUserWithTheSameObjectId_DKEThrownAndInsertRetried() {
    // given
    String userName = "Kevin";
    User firstUser = new User(ObjectId.get(), userName);
    User secondUser = new User(ObjectId.get(), userName);

    mongoTemplate.insert(firstUser);

    // when
    try {
        mongoTemplate.insert(firstUser);
    } catch (DuplicateKeyException dke) {
        mongoTemplate.insert(secondUser);
    }

    // then
    Query query = new Query();
    query.addCriteria(Criteria.where(User.NAME_FIELD)
        .is(userName));
    List<User> users = mongoTemplate.find(query, User.class);
    assertThat(users).usingRecursiveComparison()
        .isEqualTo(Lists.newArrayList(firstUser, secondUser));
}
```

### 5.2 查找并插入

另一种可能不推荐的方法是查找具有给定ObjectId的文档以确定它是否存在。如果它不存在，我们可以插入它。否则，抛出错误或生成另一个ObjectId并重试。这种方法也不可靠，因为**MongoDB中没有原子查找和插入选项，这可能会导致不一致**。

**这是自动生成ObjectId并尝试在不确保其唯一性的情况下插入文档的常见方法**。在每次插入时尝试捕获DuplicateKeyException并重试该操作似乎有点矫枉过正。边缘情况的数量非常有限，如果不首先使用Date、计数器或时间戳为ObjectId播种，就很难重现这种情况。

但是，如果出于某种原因，由于这些边缘情况，我们无法承受重复的ObjectId，那么我们会考虑使用上述方法来确保全局唯一性。

## 6. 总结

在本文中，我们了解了ObjectId是什么、它是如何构建的、我们如何生成它以及确保其唯一性的可能方法。最后，信任ObjectId的自动生成似乎是最好的主意。