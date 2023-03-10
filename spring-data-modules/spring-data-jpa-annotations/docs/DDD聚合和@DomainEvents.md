## 1. 概述

在本教程中，我们将解释如何使用@DomainEvents注解和AbstractAggregateRoot类来方便地发布和处理聚合生成的域事件-领域驱动设计中的关键战术设计模式之一。

**聚合接受业务命令，这通常会导致生成与业务域相关的事件-域事件**。

如果你想了解有关DDD和聚合的更多信息，最好从[Eric Evans的原著](https://www.amazon.com/exec/obidos/ASIN/0321125215/domainlanguag-20)开始。Vaughn Vernon还撰写了一系列关于[有效聚合设计](http://dddcommunity.org/wp-content/uploads/files/pdf_articles/Vernon_2011_1.pdf)的文章，绝对值得一读。

手动处理域事件可能很麻烦。值得庆幸的是， **Spring框架允许我们在使用数据Repository处理聚合根时轻松发布和处理域事件**。

## 2. Maven依赖

Spring Data在Ingalls发布系列中引入了@DomainEvents。它适用于任何类型的Repository。

本文提供的代码示例使用Spring Data JPA。**将Spring域事件与我们的项目集成的最简单方法是使用[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.3)**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## 3. 手动发布事件

首先，让我们尝试手动发布域事件。我们将在下一节中解释@DomainEvents的用法。

出于本文的需要，我们将为域事件使用一个空的标记类-DomainEvent。

我们将使用标准的ApplicationEventPublisher接口。

**我们有两个很好的地方可以发布事件：服务层或直接在聚合内部**。

### 3.1 服务层

**我们可以在服务方法中调用Repository save方法后简单地发布事件**。

如果服务方法是事务的一部分，并且我们在使用@TransactionalEventListener标注的监听器中处理事件，那么只有在事务成功提交后才会处理事件。

因此，当事务回滚且聚合未更新时，不存在处理“假”事件的风险：

```java
@Service
public class DomainService {

    // ...
    @Transactional
    public void serviceDomainOperation(long entityId) {
        repository.findById(entityId)
              .ifPresent(entity -> {
                  entity.domainOperation();
                  repository.save(entity);
                  eventPublisher.publishEvent(new DomainEvent());
              });
    }
}
```

下面是一个测试，证明事件确实是由serviceDomainOperation发布的：

```java
@DisplayName("given existing aggregate, when do domain operation on service, then domain event is published")
@Test
void serviceEventsTest() {
	Aggregate existingDomainEntity = new Aggregate(1, eventPublisher);
	repository.save(existingDomainEntity);
    
	// when
	domainService.serviceDomainOperation(existingDomainEntity.getId());
    
	// then
	verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
}
```

### 3.2 聚合

**我们还可以直接从聚合中发布事件**。

通过这种方式，我们可以在类中管理域事件的创建，这感觉更自然：

```java
@Entity
class Aggregate {
    // ...
    void domainOperation() {
        // some business logic
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new DomainEvent());
        }
    }
}
```

不幸的是，由于Spring Data从Repository初始化实体的方式，这可能无法按预期工作。

以下是显示真实行为的相应测试：

```java
@DisplayName("given existing aggregate, when do domain operation directly on aggregate, then domain event is NOT published")
@Test
void aggregateEventsTest() {
	Aggregate existingDomainEntity = new Aggregate(0, eventPublisher);
	repository.save(existingDomainEntity);
    
	// when
	repository.findById(existingDomainEntity.getId())
	    .get()
	    .domainOperation();
    
	// then
	verifyNoInteractions(eventHandler);
}
```

正如我们所看到的，该事件根本没有发布。在聚合内部具有依赖关系可能不是一个好主意。在此示例中，ApplicationEventPublisher未由Spring Data自动初始化。

聚合是通过调用默认构造函数来构造的。为了使它的行为符合我们的预期，我们需要手动重新创建实体(例如使用自定义工厂或切面编程)。

此外，我们应该避免在聚合方法完成后立即发布事件。至少，除非我们100%确定此方法是事务的一部分。否则，我们可能会在更改尚未持久化时发布“虚假”事件。这可能会导致系统不一致。

如果我们想避免这种情况，我们必须记住始终在事务中调用聚合方法。不幸的是，通过这种方式，我们的设计与持久层技术严重耦合。我们需要记住，我们并不总是使用事务系统。

**因此，让我们的聚合简单地管理域事件的集合，并在它即将被持久化时返回它们通常是一个更好的主意**。

在下一节中，我们将解释如何使用@DomainEvents和@AfterDomainEvents注解使域事件发布更易于管理。

## 4. 使用@DomainEvents发布事件

**自Spring Data Ingalls发布以来，我们可以使用@DomainEvents注解来自动发布域事件**。

每当使用正确的Repository保存实体时，Spring Data都会自动调用使用@DomainEvents标注的方法。

然后，使用ApplicationEventPublisher接口发布此方法返回的事件：

```java
@Entity
public class Aggregate2 {

    @Transient
    private final Collection<DomainEvent> domainEvents;
    // ...
    public void domainOperation() {
        // some domain operation
        domainEvents.add(new DomainEvent());
    }

    @DomainEvents
    public Collection<DomainEvent> events() {
        return domainEvents;
    }
}
```

下面是解释此行为的例子：

```java
@DisplayName("given aggregate with @DomainEvents, when do domain operation and save, then an event is published")
@Test
void domainEvents() {
	// given
	Aggregate2 aggregate = new Aggregate2();
    
	// when
	aggregate.domainOperation();
	repository.save(aggregate);
    
	// then
	verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
}
```

发布域事件后，将调用@AfterDomainEventsPublication标注的方法。

此方法的目的通常是清除所有事件的列表，以便将来不会再次发布它们：

```java
@AfterDomainEventPublication
public void clearEvents() {
    domainEvents.clear();
}
```

让我们将这个方法添加到Aggregate2类中，看看它是如何工作的：

```java
@DisplayName("given aggregate with @AfterDomainEventPublication, when do domain operation and save twice, then an event is published only for the first time")
@Test
void afterDomainEvents() {
	// given
	Aggregate2 aggregate = new Aggregate2();
    
	// when
	aggregate.domainOperation();
	repository.save(aggregate);
	repository.save(aggregate);
    
	// then
	verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
}
```

我们清楚地看到该事件仅是第一次发布。**如果我们从clearEvents方法中删除@AfterDomainEventPublication注解，那么将第二次发布相同的事件**。

但是，实际会发生什么取决于实现者。Spring只保证调用此方法-仅此而已。

## 5. 使用AbstractAggregateRoot模板

**借助AbstractAggregateRoot模板类，可以进一步简化域事件的发布**。当我们想要将新的域事件添加到事件集合时，我们所要做的就是调用register方法：

```java
@Entity
public class Aggregate3 extends AbstractAggregateRoot<Aggregate3> {
    // ...
    public void domainOperation() {
        // some domain operation
        registerEvent(new DomainEvent());
    }
}
```

这与上一节中显示的示例相对应。

只是为了确保一切按预期工作-以下是测试：

```java
@DisplayName("given aggregate extending AbstractAggregateRoot, when do domain operation and save twice, then an event is published only for the first time")
@Test
void afterDomainEvents() {
	// given
	Aggregate3 aggregate = new Aggregate3();
    
	// when
	aggregate.domainOperation();
	repository.save(aggregate);
	repository.save(aggregate);
    
	// then
	verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
}

@DisplayName("given aggregate extending AbstractAggregateRoot, when do domain operation and save, then an event is published")
@Test
void domainEvents() {
	// given
	Aggregate3 aggregate = new Aggregate3();
    
	// when
	aggregate.domainOperation();
	repository.save(aggregate);
    
	// then
	verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
}
```

如我们所见，我们可以通过更少的代码并达到完全相同的效果。

## 6. 实现注意事项

虽然一开始使用@DomainEvents功能看起来是个好主意，但我们需要注意一些陷阱。

### 6.1 未发布的事件

使用JPA时，我们不一定要在想要保存更改时调用save方法。

如果我们的代码是事务的一部分(例如使用@Transactional注解)并对现有实体进行更改，那么我们通常只是让事务提交而不显式调用Repository上的save方法。因此，即使我们的聚合产生了新的域事件，它们也永远不会被发布。

**我们还需要记住@DomainEvents功能仅在使用Spring Data Repository时有效**。这可能是一个重要的设计因素。

### 6.2 丢失事件

**如果在事件发布期间发生异常，监听器将永远不会收到通知**。

即使我们能以某种方式保证事件监听器的通知，目前也没有让发布者知道出现问题的背压。如果事件监听器被异常中断，该事件将保持未使用状态并且永远不会再次发布。

这个设计缺陷是Spring开发团队已知的，一位主要开发人员甚至提出了[解决此问题](https://github.com/olivergierke/spring-domain-events)的可能方法。

### 6.3 本地上下文

域事件使用简单的ApplicationEventPublisher接口发布。

**默认情况下，在使用ApplicationEventPublisher时，事件在同一线程中发布和使用**。一切都发生在同一个容器中。

通常，我们希望通过某种消息代理发送事件，以便其他分布式客户端/系统得到通知。在这种情况下，我们需要手动将事件转发到消息代理。

也可以使用[Spring Integration](https://spring.io/projects/spring-integration)或第三方解决方案，例如[Apache Camel](https://camel.apache.org/spring-event.html)。

## 7. 总结

在本文中，我们学习了如何使用@DomainEvents注解管理聚合域事件。

**这种方法可以极大地简化事件基础结构，因此我们可以只关注域逻辑**。我们只需要知道没有灵丹妙药，Spring处理域事件的方式也不例外。