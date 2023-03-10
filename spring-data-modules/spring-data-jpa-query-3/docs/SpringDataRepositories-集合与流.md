## 1. 概述

[Spring Data Repository](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)提供了灵活的方式来查询Collection或Stream中的大块数据。在本教程中，我们将学习如何查询List和Stream中的数据以及何时使用它们。

## 2. List与Stream

众所周知，社交媒体网站拥有数百万用户的详细信息。让我们定义一种情况，即需要找到所有年龄大于20岁的用户。在本节中，我们将学习使用返回List和Stream的查询来解决这个问题。我们还将了解这两种查询的工作方式。

由于我们将使用一些代码示例，因此运行它们有一些先决条件。我们使用了H2数据库。User是我们的实体，具有firstName、lastName和age作为其属性。我们在测试类的setup方法中保存了一些用户。

我们使用[Java Faker](https://www.baeldung.com/java-faker)库为该实体生成随机数据。

### 2.1 List

Java中的[List](https://www.baeldung.com/java-arraylist)是一个有多种实现的接口(如[ArrayList](https://www.baeldung.com/java-arraylist-linkedlist)、[LinkedList](https://www.baeldung.com/java-arraylist-linkedlist)等)，并存储数据的集合。

在下面的示例中，我们将编写一个Spring Data JPA测试来加载列表中的所有用户，并断言结果中的所有用户都超过20岁。

Spring Data提供了多种创建查询的方法。这里我们将使用查询方法来形成我们的查询。

我们将使用此查询方法将用户加载到List中：

```java
List<User> findByAgeGreaterThan20();
```

现在，让我们编写一个测试用例，看看它是如何工作的：

```java
@Test
public void whenAgeIs20_thenItShouldReturnAllUsersWhoseAgeIsGreaterThan20InAList() {
    List<User> users = userRepository.findByAgeGreaterThan(20);
    assertThat(users).isNotEmpty();
    assertThat(users.stream().map(User::getAge).allMatch(age -> age > 20)).isTrue();
}
```

上面的测试用例查询用户并断言他们都大于20。在这里，**客户端一次获取所有用户，并且在为此查询获取用户后将关闭底层数据库资源**，除非我们将它们保持打开状态。

### 2.2 Stream

[Stream](https://www.baeldung.com/java-8-streams-introduction)是数据流经的管道。它支持的一些中间操作在数据流动时对数据执行操作。

尽管在List中查询是获取集合的常用方法，但将其用作数据库结果时有一些注意事项，我们将在下一节中讨论。现在，让我们了解如何在Stream中查询数据。

这次我们将使用此查询方法在Stream中加载用户：

```java
Stream<User> findByAgeGreaterThan20();
```

现在，让我们编写一个测试用例：

```java
@Test
public void whenAgeIs20_thenItShouldReturnAllUsersWhoseAgeIsGreaterThan20InAStream() {
    Stream<User> users = userRepository.findAllByAgeGreaterThan(20);
    assertThat(users).isNotNull();
    assertThat(users.map(User::getAge).allMatch(age -> age > 20)).isTrue();
}
```

我们可以清楚地看到，通过在Stream中获取结果，我们可以直接对其进行操作。**一旦第一个用户到达，客户端就可以对其进行操作，并且底层数据库资源在处理流中的所有用户时保持打开状态**。

为确保EntityManager在处理完Stream中的所有结果之前不会关闭，**必须使用@Transactional标注查询Stream数据**。**将Stream查询包装在[try-with-resources](https://www.baeldung.com/java-try-with-resources)中也是一个好习惯**。

现在我们知道如何使用它们中的每一个，在下一节中我们将探讨何时最好使用它们。

## 3. 何时使用

在适当的上下文中使用Stream和List非常重要，因为在它们不是最佳选择的情况下使用它们可能会导致性能不佳或意外行为等问题。评估备选方案并选择最适合该问题的备选方案总是好的。

**List非常适合一次性需要所有记录的小型结果集**，而**Stream更适合可以逐个处理的大型结果集**，以及客户端需要Stream而不是Collection的情况。

在Stream中查询数据时，如果两者都可以产生相同的结果，我们应该首选数据库查询而不是中间Stream方法。

## 4. 总结

在本文中，我们学习了如何在使用Spring Data Repository时使用List和Stream。

我们还了解到，当客户端一次需要所有结果时使用List，而在Stream的情况下，客户端可以在获得第一个结果后立即开始工作。我们还讨论了对底层数据库资源的影响以及何时最好使用它们。