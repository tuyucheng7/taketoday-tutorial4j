## 1. 概述

在本教程中，**我们介绍Spock框架中Mock、Stub和Spy之间的区别**，我们会说明框架提供的与基于交互的测试相关的内容。

Spock是一个用于Java和Groovy的测试框架，有助于自动化软件应用程序的手动测试过程，它引入了自己的mocks、stubs和spies，并为通常需要额外库的测试提供了内置功能。

首先，我们会说明何时应该使用Stubs，然后，我们会进行Mock，最后，我们会介绍后面推出的Spy。

## 2. Maven依赖

在开始之前，首先添加我们的Maven依赖项：

```xml
<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.3-groovy-2.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.7</version>
    <scope>test</scope>
</dependency>
```

请注意，我们需要Spock的1.3-RC1-groovy-2.5版本，**Spy将在Spock框架的下一个稳定版本中引入，现在Spy在1.3版本的第一个候选版本中可用**。

## 3. 基于交互的测试

**基于交互的测试是一种帮助我们测试对象行为的技术，特别是它们如何相互交互**。为此，我们可以使用称为mock和stub的虚拟实现。

当然，我们可以很容易地编写自己的mock和stub实现，但当我们的生产代码量增加时，问题就出现了。手动编写和维护此代码变得很困难，这就是为什么我们要使用mock框架的原因，它提供了一种简洁的方式来简要描述预期的交互。**Spock内置了对mock、stub和spy的支持**。

与大多数Java库一样，Spock使用JDK动态代理来mock接口，使用Byte Buddy或cglib代理来mock类，它在运行时创建mock实现。

Java已经有许多用于mock类和接口的不同且成熟的库，尽管这些都可以在Spock中使用，但我们应该使用Spock中的mock、stub和spy仍然有一个主要原因，通过将所有这些引入Spock，我们可以利用Groovy的所有功能来使我们的测试更具可读性、更容易编写，而且绝对更有趣。

## 4. Stubbing方法调用

有时，**在单元测试中，我们需要提供类的虚拟行为**。这可能是外部服务的客户端，也可能是提供对数据库访问的类，这种技术称为stub。

**stub是我们测试代码中现有类依赖项的可控替换**，这对于以特定方式响应的方法调用很有用。当我们使用stub时，我们并不关心一个方法会被调用多少次。相反，我们想表达的只是：“当使用此数据调用时返回此值”。

### 4.1 被测代码

首先我们创建一个名为Item的模型类：

```java
public class Item {
    private final String id;
    private final String name;

    // standard constructor, getters, equals
}
```

我们需要重写equals(Object other)方法来使我们的断言起作用，**当我们使用双等号(==)时，Spock将在断言期间使用equals**：

```groovy
new Item('1', 'name') == new Item('1', 'name')
```

接下来我们创建一个接口ItemProvider：

```java
public interface ItemProvider {
    List<Item> getItems(List<String> itemIds);
}
```

我们还需要一个将被测试的类，因此创建一个ItemService类，并添加一个ItemProvider作为依赖项：

```java
public class ItemService {
    private final ItemProvider itemProvider;

    public ItemService(ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
    }

    List<Item> getAllItemsSortedByName(List<String> itemIds) {
        List<Item> items = itemProvider.getItems(itemIds);
        return items.stream()
            .sorted(Comparator.comparing(Item::getName))
            .collect(Collectors.toList());
    }
}
```

**我们希望我们的代码依赖于抽象，而不是特定的实现**，这就是我们使用接口的原因，它可以有许多不同的实现。例如，我们可以从文件中读取Item，为外部服务创建HTTP客户端，或者从数据库中读取数据。

在这段代码中，**我们需要stub外部依赖项，因为我们只想测试包含在getAllItemsSortedByName方法中的逻辑**。

### 4.2 在被测代码中使用stub对象

我们使用ItemProvider依赖项的Stub在setup()方法中初始化ItemService对象：

```groovy
class ItemServiceUnitTest extends Specification {

	ItemProvider itemProvider
	ItemService itemService

	def setup() {
		itemProvider = Stub(ItemProvider)
		itemService = new ItemService(itemProvider)
	}
}
```

**现在，让itemProvider在每次调用时返回一个带有特定参数的Item集合**：

```groovy
itemProvider.getItems(['offer-id', 'offer-id-2']) >> [new Item('offer-id-2', 'Zname'), new Item('offer-id', 'Aname')]
```

**我们使用“>>”操作数来stub该方法，当使用['offer-id', 'offer-id-2']集合调用时，getItems方法将始终返回包含两个Item的集合**。[]是用于创建集合的Groovy快捷方式。

下面是整个测试方法：

```groovy
def 'should return items sorted by name'() {
	given:
	def ids = ['offer-id', 'offer-id-2']
	itemProvider.getItems(ids) >> [
			new Item('offer-id', 'Zname'),
			new Item('offer-id-2', 'Aname')
	]
    
	when:
	List<Item> items = itemService.getAllItemsSortedByName(ids)
    
	then:
	items.collect { it.name } == ['Aname', 'Zname']
}
```

我们可以使用更多的stub功能，例如：使用参数匹配约束、使用stub中的值序列、在特定条件下定义不同的行为以及链接方法响应。

## 5. Mock类方法

有时，**我们想知道是否使用指定的参数调用了依赖对象的某个方法**，我们希望专注于对象的行为，并通过查看方法调用来探索它们如何交互。mock是对测试类中对象之间强制交互的描述。

### 5.1 交互代码

举个简单的例子，我们将在数据库中保存Item，保存成功后，我们想在消息服务上发布一个关于我们系统中新Item的事件。

常用的消息服务是RabbitMQ或Kafka，所以一般来说，我们只描述我们的合约：

```java
public interface EventPublisher {
    void publish(String addedOfferId);
}
```

我们的测试方法会将非空Item保存在数据库中，然后发布事件。在我们的示例中，将Item保不保存在数据库中是无关紧要的，重点是发布事件。因此为了简单起见，跳过实际的数据库操作：

```java
void saveItems(List<String> itemIds) {
    List<String> notEmptyOfferIds = itemIds.stream()
        .filter(itemId -> !itemId.isEmpty())
        .collect(Collectors.toList());
        
    // save in database

    notEmptyOfferIds.forEach(eventPublisher::publish);
}
```

### 5.2 验证与mock对象的交互

现在，让我们测试代码中的交互。首先，**我们需要在setup()方法中mock EventPublisher**，所以我们创建一个新的实例字段并使用Mock(Class)函数mock它：

```groovy
class ItemServiceUnitTest extends Specification {

	ItemProvider itemProvider
	ItemService itemService
	EventPublisher eventPublisher

	def setup() {
		itemProvider = Stub(ItemProvider)
		eventPublisher = Mock(EventPublisher)
		itemService = new ItemService(itemProvider, eventPublisher)
	}
}
```

对于测试方法，我们将传递3个字符串："、'a'、'b'，我们希望我们的eventPublisher将发布2个带有'a'和'b'字符串的事件：

```groovy
def 'should publish events about new non-empty saved offers'() {
    given:
    def offerIds = ['', 'a', 'b']

    when:
    itemService.saveItems(offerIds)

    then:
    1  eventPublisher.publish('a')
    1  eventPublisher.publish('b')
}
```

让我们仔细看看在最后then部分中的断言：

```groovy
1  eventPublisher.publish('a')
```

我们期望itemService会调用一次eventPublisher.publish(String)，参数为'a'。

在stub中，我们讨论了参数约束。相同的规则也适用于mock，**我们可以验证是否使用任何非null和非空参数调用了eventPublisher.publish(String)两次**：

```groovy
2  eventPublisher.publish({ it != null && !it.isEmpty() })
```

### 5.3 结合mock和stub

**在Spock中，Mock的行为可能与Stub相同**。所以我们可以对mock对象说，对于给定的方法调用，它应该返回给定的数据。

让我们用Mock(Class)覆盖ItemProvider并创建一个新的ItemService：

```groovy
given:
itemProvider = Mock(ItemProvider)
itemProvider.getItems(['item-id']) >> [new Item('item-id', 'name')]
itemService = new ItemService(itemProvider, eventPublisher)

when:
def items = itemService.getAllItemsSortedByName(['item-id'])

then:
items == [new Item('item-id', 'name')]
```

我们可以从给定的部分重写stub：

```groovy
1  itemProvider.getItems(['item-id']) >> [new Item('item-id', 'name')]
```

所以一般来说，这一行说：**itemProvider.getItems将使用['item-'id']参数调用一次并返回给定的数组**。

我们已经知道mock的行为与stub相同。所有关于参数约束、返回多个值和副作用的规则也适用于Mock。

## 6. Spock中的Spy类

**Spy提供了包装现有对象的能力**，这意味着我们可以监听调用者和真实对象之间的对话，但保留原始对象行为。基本上，**Spy将方法调用委托给原始对象**。

与Mock和Stub不同，我们不能在接口上创建Spy。它包装了一个实际的对象，因此另外，我们需要为构造函数传递参数。否则，将调用该类型的默认构造函数。

### 6.1 被测代码

让我们为EventPublisher创建一个简单的实现LoggingEventPublisher，它将在控制台中打印每个添加Item的id，下面是接口方法的实现：

```java
@Override
public void publish(String addedOfferId) {
    System.out.println("I've published: " + addedOfferId);
}
```

### 6.2 使用Spy进行测试

**我们使用Spy(Class)方法创建类似于mock和stub的Spy(间谍)**，LoggingEventPublisher没有任何其他类依赖项，因此我们不必传递构造函数参数：

```groovy
eventPublisher = Spy(LoggingEventPublisher)
```

现在，让我们测试一下我们的Spy，我们需要一个带有Spy对象的ItemService新实例：

```groovy
given:
eventPublisher = Spy(LoggingEventPublisher)
itemService = new ItemService(itemProvider, eventPublisher)

when:
itemService.saveItems(['item-id'])

then:
1  eventPublisher.publish('item-id')
```

我们验证了eventPublisher.publish方法只被调用了一次。**此外，方法调用被传递给了真实对象，所以我们将在控制台中看到println的输出**：

```shell
I've published: item-id
```

请注意，当我们在Spy的方法上使用stub时，它不会调用真实对象方法。**一般来说，我们应该避免使用Spy**，如果我们到了必须使用Spy的地步，那也可能意味着我们应该根据规范重新安排代码。

## 7. 好的单元测试

让我们快速总结一下使用mock对象如何改进我们的测试：

-   我们创建确定性测试套件
-   我们不会有任何副作用
-   我们的单元测试会非常快
-   我们可以专注于单个Java类中包含的逻辑
-   我们的测试独立于环境

## 8. 总结

在本文中，我们详细描述了Groovy中的Spy、Mock和Stub，这些技术的使用可以使我们的测试更快、更可靠、更容易阅读。