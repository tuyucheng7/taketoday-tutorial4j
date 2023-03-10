## 1. 简介

在本教程中，我们将在[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)应用程序中创建组合键。我们将了解不同的策略以及如何配置它们。

## 2. 什么是复合键以及何时使用它

复合键是文档中唯一标识文档的属性组合。**使用复合主键并不比使用单个自动生成的属性更好或更差**。我们甚至可以将这些方法与唯一索引结合起来。

通常，没有单个属性能够唯一标识文档。在这些情况下，我们可以将其保留空，MongoDB将为其“_id”属性[生成一个唯一值](https://www.baeldung.com/spring-boot-mongodb-auto-generated-field)。或者，我们可以选择多个属性，这些属性组合在一起时可以达到该目的。**在这种情况下，我们必须为我们的ID属性创建一个自定义类来保存所有这些属性**。让我们看看这是如何工作的。

## 3. 使用@Id注解创建复合键

@Id注解可用于标注自定义类型的属性，从而完全控制其生成。**我们的ID类的唯一要求是我们[重写equals()和hashCode()](https://www.baeldung.com/java-equals-hashcode-contracts并有一个默认的[无参数构造函数](https://www.baeldung.com/java-constructors#noargs)**。

在我们的第一个示例中，我们为活动门票创建一个文档。它的ID将是venue和date属性的组合。让我们从我们的ID类开始：

```java
public class TicketId {
    private String venue;
    private String date;

    // getters and setters

    // override hashCode() and equals()
}
```

由于无参构造函数是隐式的，我们不需要其他构造函数，因此我们不需要显式编写它。此外，我们将使用字符串日期来简化示例。接下来让创建Ticket类，并使用@Id标注我们的TicketId属性：

```java
@Document
public class Ticket {
    @Id
    private TicketId id;

    private String event;

    // getters and setters
}
```

**对于我们的MongoRepository，我们可以将TicketId指定为ID类型，这就是所需的所有设置**：

```java
public interface TicketRepository extends MongoRepository<Ticket, TicketId> {
}
```

### 3.1 测试我们的模型

**因此，尝试两次插入具有相同ID的Ticket将抛出DuplicateKeyException**。我们可以通过测试来验证这一点：

```java
@Test
void givenCompositeId_whenDupeInsert_thenExceptionIsThrown() {
    TicketId ticketId = new TicketId();
    ticketId.setDate("2020-01-01");
    ticketId.setVenue("V");
    
    Ticket ticket = new Ticket(ticketId, "Event C");
    service.insert(ticket);
    
    assertThrows(DuplicateKeyException.class, () -> service.insert(ticket));
}
```

这确保了我们的复合键正常工作。

### 3.2 按ID查找

由于我们将TicketId定义为Repository中的ID类，因此我们仍然可以使用默认的findById()方法。让我们编写一个测试来查看它的实际效果：

```java
@Test
void givenCompositeId_whenSearchingByIdObject_thenFound() {
    TicketId ticketId = new TicketId();
    ticketId.setDate("2020-01-01");
    ticketId.setVenue("Venue B");
    
    service.insert(new Ticket(ticketId, "Event B"));
    
    Optional<Ticket> optionalTicket = service.find(ticketId);
    
    assertThat(optionalTicket.isPresent());
    Ticket savedTicket = optionalTicket.get();
    
    assertEquals(savedTicket.getId(), ticketId);
}
```

当我们想要绝对控制我们的ID属性时，我们应该使用这种方法。**同样，这将确保我们的ID对象中的属性不能被修改**。一个缺点是我们丢失了MongoDB生成的ID，该ID的可读性较差。但是更容易在链接中使用。

## 4. 警告

当使用嵌套对象作为ID时，属性的顺序很重要。这在使用我们的Repository时通常不是问题，因为Java对象总是以相同的顺序构造。**但是，如果我们更改TicketId类中字段的顺序，我们可以插入另一个具有相同值的文档**。例如，这些对象被认为是不同的：

```json
{
    "id": {
        "venue": "Venue A",
        "date": "2023-05-27"
    },
    "event": "Event 1"
}
```

之后，如果我们更改TicketId中的字段顺序，我们将能够插入相同的值。**不会引发任何异常**：

```json
{
    "id": {
        "date": "2023-05-27",
        "venue": "Venue A"
    },
    "event": "Event 1"
}
```

如果我们在Ticket类的属性上使用唯一索引而不是ID类，则不会发生这种情况。换句话说，它只发生在嵌套对象中。

## 5. 总结

在本文中，我们了解了为MongoDB文档创建复合键的优缺点。以及通过简单用例实现它们所需的配置。同时，我们还了解了一个需要注意的重要警告。