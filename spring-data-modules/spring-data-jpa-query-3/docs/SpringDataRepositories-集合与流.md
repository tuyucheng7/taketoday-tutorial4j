## 一、概述

[Spring Data Repositories提供了灵活的方式来查询](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)*Collection* 或*Stream*中的大块数据。*在本教程中，我们将学习如何查询List*和*Stream*中的数据以及何时使用它们。

## 2.*列表*与*流*

众所周知，社交媒体网站拥有数百万用户的详细信息。让我们定义一种情况，需要找到所有年龄大于 20 岁的用户。在本节中，我们将学习使用返回 List 和*Stream*的*查询*来解决这个问题。我们还将了解这两种查询的工作方式。

由于我们将使用一些代码示例，因此运行它们有一些先决条件。我们使用了 H2 数据库。*User*是我们的*实体*，具有*firstName*、*lastName*和*age*作为其属性。我们在测试类的设置方法中保留了一些用户。

我们使用[Java Faker](https://www.baeldung.com/java-faker)库为该实体生成随机数据。

### 2.1. *列表*

[*Java 中的List*](https://www.baeldung.com/java-arraylist)是一个有多种实现的接口，如[*ArrayList*](https://www.baeldung.com/java-arraylist-linkedlist)、[ *LinkedList*](https://www.baeldung.com/java-arraylist-linkedlist)等，存储数据的集合。

在下面的示例中，我们将编写一个 Spring Data JPA 测试来加载列表中的所有用户，*并* 断言结果中的所有用户都超过 20 岁。

Spring Data 提供了多种创建查询的方法。这里我们将使用查询方法来形成我们的查询。

我们将使用此查询方法将用户加载到*List*中：

```java
List<User> findByAgeGreaterThan20();复制
```

现在，让我们编写一个测试用例，看看它是如何工作的：

```java
@Test
public void whenAgeIs20_thenItShouldReturnAllUsersWhoseAgeIsGreaterThan20InAList() {
  List<User> users = userRepository.findByAgeGreaterThan(20);
  assertThat(users).isNotEmpty();
  assertThat(users.stream().map(User::getAge).allMatch(age -> age > 20)).isTrue();
}复制
```

上面的测试用例查询用户并断言他们都大于 2o。在这里，客户端一次获取所有用户，并且在为此查询获取用户后将**关闭底层数据库资源，除非我们将它们保持打开状态。**

### 2.2. *溪流*

Stream是数据流经的管道[*。*](https://www.baeldung.com/java-8-streams-introduction)它支持的一些中间方法在数据流动时对数据执行操作。

尽管在*List*中查询是获取集合的常用方法，但将其用作数据库结果时有一些注意事项，我们将在下一节中讨论。现在，让我们了解如何在*Stream*中查询数据。

这次我们将使用此查询方法在*Stream*中加载用户：

```java
Stream<User> findByAgeGreaterThan20();
复制
```

现在，让我们编写一个测试用例：

```java
@Test
public void whenAgeIs20_thenItShouldReturnAllUsersWhoseAgeIsGreaterThan20InAStream() {
  Stream<User> users = userRepository.findAllByAgeGreaterThan(20);
  assertThat(users).isNotNull();
  assertThat(users.map(User::getAge).allMatch(age -> age > 20)).isTrue();
}复制
```

我们可以清楚地看到，通过在*Stream*中获取结果，我们可以直接对其进行操作。一旦第一个用户到达，**客户端就可以对其进行操作，并且底层数据库资源在处理流中的所有用户时保持打开状态**。

为确保*EntityManager*在处理完*Stream*中的所有结果之前不会关闭**，必须使用\*@Transactional\*注释查询\*Stream\***数据。**将*****Stream\*****查询包装在*****[try-with-resources](https://www.baeldung.com/java-try-with-resources)\***中也是一个好习惯***。\***

现在我们知道如何使用它们中的每一个，在下一节中我们将探讨何时最好使用它们。

## 3. 何时使用

在适当的上下文中使用*Stream*和*List*很重要，因为在它们不是最佳选择的情况下使用它们可能会导致性能不佳或意外行为等问题。评估备选方案并选择最适合该问题的备选方案总是好的。

List**非常适合一次性需要所有记录的小型结果集**，而***Stream更适合\******可以\*****逐条处理的大型结果集**，以及客户端需要*Stream*而不是*Collection*的情况。

在*Stream 中查询数据时，*如果两者都可以产生相同的结果，我们应该更喜欢数据库查询而不是中间*Stream方法。*

## 4。结论

在本文中，我们学习了如何在使用 Spring Data Repositories 时使用*List*和*Stream 。*

我们还了解到，当客户端一次需要所有结果时使用*List ，而在**Stream*的情况下，客户端可以在获得第一个结果后立即开始工作。我们还讨论了对底层数据库资源的影响以及何时最好使用它们。