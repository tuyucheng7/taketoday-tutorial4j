## 1. 概述

在编程时，我们经常需要对对象集合进行排序。如果我们想要对多个字段上的对象进行排序，排序逻辑有时会变得难以实现。在本教程中，我们将讨论解决该问题的几种不同方法，以及它们的优缺点。

## 2. 示例人物类

让我们定义一个Person类，它有两个字段，name和age。在整个示例中，我们将首先根据姓名比较Person对象，然后根据年龄比较：

```java
public class Person {
    @Nonnull private String name;
    private int age;

    // constructor
    // getters and setters
}
```

在这里，我们添加了一个@Nonnull注解以保持示例简单。但是在生产代码中，我们可能需要处理可空字段的比较。

## 3.使用Comparator.compare()

Java 提供了[Comparator](https://www.baeldung.com/java-comparator-comparable)接口来比较两个相同类型的对象。我们可以使用自定义逻辑实现其compare(T o1, To o2)方法以执行所需的比较。

### 3.1. 不同的字段一一检查

让我们逐个比较字段：

```java
public class CheckFieldsOneByOne implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        int nameCompare = o1.getName().compareTo(o2.getName());
        if(nameCompare != 0) {
            return nameCompare;
        }
        return Integer.compare(o1.getAge(), o2.getAge());
    }
}
```

在这里，我们使用String类的compareTo()方法和Integer类的compare()方法依次比较name和age字段。

这需要大量的输入，有时还需要处理许多特殊情况。因此，当我们有更多的字段要比较时，很难维护和扩展。通常，不建议在生产代码中使用此方法。

### 3.2. 使用 Guava 的比较链

首先，让我们将 Google Guava 库[依赖](https://search.maven.org/artifact/com.google.guava/guava-bom/31.1-jre/pom)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

我们可以使用这个库中的ComparisonChain类来简化逻辑：

```java
public class ComparisonChainExample implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return ComparisonChain.start()
          .compare(o1.getName(), o2.getName())
          .compare(o1.getAge(), o2.getAge())
          .result();
    }
}
```

在这里，我们分别使用 ComparisonChain 中的compare(int left, int right)和compare(Comparable<?> left, Comparable<?> right)方法来比较name和age。

这种方法隐藏了比较细节，只暴露了我们关心的内容——我们想要比较的字段以及它们应该被比较的顺序。此外，我们应该注意，我们不需要任何额外的空处理逻辑，因为库方法会处理它。因此，它变得更容易维护和扩展。

### 3.3. 使用 Apache Commons 的CompareToBuilder进行排序

首先，让我们将Apache Commons的[依赖项添加到](https://search.maven.org/artifact/org.apache.commons/commons-lang3/3.12.0/jar)pom.xml中：

```java
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

与前面的示例类似，我们可以使用Apache Commons 中的CompareToBuilder来减少所需的样板代码：

```java
public class CompareToBuilderExample implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return new CompareToBuilder()
          .append(o1.getName(), o2.getName())
          .append(o1.getAge(), o2.getAge())
          .build();
    }
}
```

这种方法与 Guava 的ComparisonChain非常相似—— 它也隐藏了比较细节并且易于维护和扩展。

## 4. 使用Comparator.comparing()和 Lambda 表达式

从Java8 开始， Comparator接口中添加了几个静态方法，可以采用 lambda 表达式来创建Comparator对象。我们可以使用它的[comparing()](https://www.baeldung.com/java-8-comparator-comparing)[方法](https://www.baeldung.com/java-8-comparator-comparing)来构造 我们需要的Comparator ：

```java
public static Comparator<Person> createPersonLambdaComparator() {
    return Comparator.comparing(Person::getName)
      .thenComparing(Person::getAge);
}
```

这种方法更加简洁和可读，因为它直接采用Person类的 getter 。

它还保留了我们之前看到的方法的可维护性和可扩展性特征。此外，与之前的方法中的立即评估相比，这里的 getters 是惰性评估的。因此，它的性能更好，更适合需要大量大数据比较的对延迟敏感的系统。

此外，这种方法仅使用核心Java类，不需要任何第三方库作为依赖项。总的来说，这是最推荐的方法。

## 5. 检查比较结果

让我们测试我们看到的四个比较器并检查它们的行为。所有这些比较器都可以以相同的方式调用并且应该产生相同的结果：

```java
@Test
public void testComparePersonsFirstNameThenAge() {
    Person person1 = new Person("John", 21);
    Person person2 = new Person("Tom", 20);
    // Another person named John
    Person person3 = new Person("John", 22);

    List<Comparator<Person>> comparators =
      Arrays.asList(new CheckFieldsOneByOne(),
        new ComparisonChainExample(),
        new CompareToBuilderExample(),
        createPersonLambdaComparator());
    // All comparators should produce the same result
    for(Comparator<Person> comparator : comparators) {
        Assertions.assertIterableEquals(
          Arrays.asList(person1, person2, person3)
            .stream()
            .sorted(comparator)
            .collect(Collectors.toList()),
          Arrays.asList(person1, person3, person2));
    }
}
```

在这里，person1与 person3同名(“John”)，但更年轻(21 < 22)，而person3的名字(“John”)在字典序上小于person2的名字(“Tom”)。所以，最终的顺序是person1、person3、person2。

此外，我们应该注意，如果我们在类变量名称上没有@Nonnull注解，我们需要添加额外的逻辑来处理所有方法中的 null 情况，除了 Apache Commons 的CompareToBuilder(它具有原生 null处理内置)。

## 六，总结

在本文中，我们学习了在对对象集合进行排序时在多个字段上进行比较的不同方法。