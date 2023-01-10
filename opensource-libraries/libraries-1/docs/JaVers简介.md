## 1. 概述

在本文中，我们将研究[JaVers](http://javers.org/documentation/)库。

这个库帮助程序员检查和检测简单Java对象状态的变化。当我们在代码中使用可变对象时，每个对象都可能在应用程序的不同位置被修改；JaVers将帮助我们发现和审计这些变化。

## 2.Maven依赖

首先，让我们将javers-core Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.javers</groupId>
    <artifactId>javers-core</artifactId>
    <version>3.1.0</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.javers" AND a%3A"javers-core")上找到最新版本。

## 3.检测POJO状态变化

让我们从一个简单的Person类开始：

```java
public class Person {
    private Integer id;
    private String name;

    // standard getters/constructors
}
```

假设我们在应用程序的一部分创建了一个Person对象，而在代码库的另一部分，更改了具有相同 id 字段的人的姓名。我们想比较它们以找出 person 对象发生了什么样的变化。

我们可以使用JaVers类中的compare()方法比较这两个对象：

```java
@Test
public void givenPersonObject_whenApplyModificationOnIt_thenShouldDetectChange() {
    // given
    Javers javers = JaversBuilder.javers().build();

    Person person = new Person(1, "Michael Program");
    Person personAfterModification = new Person(1, "Michael Java");

    // when
    Diff diff = javers.compare(person, personAfterModification);

    // then
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getPropertyName()).isEqualTo("name");
    assertThat(change.getLeft()).isEqualTo("Michael Program");
    assertThat(change.getRight()).isEqualTo("Michael Java");
}
```

## 4.检测对象列表的状态变化

如果我们正在处理对象集合，我们同样需要通过查看集合中的每个元素来检查状态变化。有时，我们想从列表中添加或删除特定对象，从而改变其状态。

让我们看一个例子；假设我们有一个对象列表，我们从该列表中删除了一个对象。

由于某些原因，该更改可能是不受欢迎的，我们希望审核此列表中发生的更改。JaVers 允许我们使用compareCollections()方法来做到这一点：

```java
@Test
public void givenListOfPersons_whenCompare_ThenShouldDetectChanges() {
    // given
    Javers javers = JaversBuilder.javers().build();
    Person personThatWillBeRemoved = new Person(2, "Thomas Link");
    List<Person> oldList = 
      Lists.asList(new Person(1, "Michael Program"), personThatWillBeRemoved);
    List<Person> newList = 
      Lists.asList(new Person(1, "Michael Not Program"));

    // when
    Diff diff = javers.compareCollections(oldList, newList, Person.class);

    // then
    assertThat(diff.getChanges()).hasSize(3);

    ValueChange valueChange = 
      diff.getChangesByType(ValueChange.class).get(0);
 
    assertThat(valueChange.getPropertyName()).isEqualTo("name");
    assertThat(valueChange.getLeft()).isEqualTo("Michael Program");
    assertThat(valueChange.getRight()).isEqualTo("Michael Not Program");

    ObjectRemoved objectRemoved = diff.getChangesByType(ObjectRemoved.class).get(0);
    assertThat(
      objectRemoved.getAffectedObject().get().equals(personThatWillBeRemoved))
      .isTrue();

    ListChange listChange = diff.getChangesByType(ListChange.class).get(0);
    assertThat(listChange.getValueRemovedChanges().size()).isEqualTo(1);
}
```

## 5.比较对象图

在实际应用程序中，我们经常处理对象图。假设我们有一个PersonWithAddress类，它有一个地址对象列表，我们正在为给定的人添加一个新地址。

我们可以很容易地找到发生的变化类型：

```java
@Test
public void givenListOfPerson_whenPersonHasNewAddress_thenDetectThatChange() {
    // given
    Javers javers = JaversBuilder.javers().build();

    PersonWithAddress person = 
      new PersonWithAddress(1, "Tom", Arrays.asList(new Address("England")));

    PersonWithAddress personWithNewAddress = 
      new PersonWithAddress(1, "Tom", 
        Arrays.asList(new Address("England"), new Address("USA")));


    // when
    Diff diff = javers.compare(person, personWithNewAddress);
    List objectsByChangeType = diff.getObjectsByChangeType(NewObject.class);

    // then
    assertThat(objectsByChangeType).hasSize(1);
    assertThat(objectsByChangeType.get(0).equals(new Address("USA")));
}
```

同样，删除一个地址也会被检测到：

```java
@Test
public void givenListOfPerson_whenPersonRemovedAddress_thenDetectThatChange() {
    // given
    Javers javers = JaversBuilder.javers().build();

    PersonWithAddress person = 
      new PersonWithAddress(1, "Tom", Arrays.asList(new Address("England")));

    PersonWithAddress personWithNewAddress = 
      new PersonWithAddress(1, "Tom", Collections.emptyList());


    // when
    Diff diff = javers.compare(person, personWithNewAddress);
    List objectsByChangeType = diff.getObjectsByChangeType(ObjectRemoved.class);

    // then
    assertThat(objectsByChangeType).hasSize(1);
    assertThat(objectsByChangeType.get(0).equals(new Address("England")));
}
```

## 六. 总结

在这篇快速文章中，我们使用了 JaVers 库，这是一个有用的库，它为我们提供了用于检测对象状态变化的 API。它不仅可以检测简单 POJO 对象的变化，还可以检测对象集合甚至对象图中更复杂的变化。