## 1. 概述

在本文中，我们将研究 Guava 库中的Ordering类。

Ordering类实现了Comparator接口，并为我们提供了一个有用的流畅 API 来创建和链接比较器。

[作为一个快速的旁注，新的Comparator.comparing()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Comparator.html#comparing(java.util.function.Function)) API也值得一看——它提供了类似的功能；这是一个使用该 API[的实际示例。](https://www.baeldung.com/java-8-sort-lambda)

## 2.创建订单

Ordering有一个有用的构建器方法，它返回一个适当的实例，可以在集合的sort()方法中或任何其他需要Comparator实例的地方使用。

我们可以通过执行方法natural() 创建自然顺序实例：

```java
List<Integer> integers = Arrays.asList(3, 2, 1);

integers.sort(Ordering.natural());

assertEquals(Arrays.asList(1,2,3), integers);
```

假设我们有一组Person对象：

```java
class Person {
    private String name;
    private Integer age;
    
    // standard constructors, getters
}
```

我们想按年龄字段对此类对象的列表进行排序。我们可以创建自定义Ordering，通过扩展它来做到这一点：

```java
List<Person> persons = Arrays.asList(new Person("Michael", 10), new Person("Alice", 3));
Ordering<Person> orderingByAge = new Ordering<Person>() {
    @Override
    public int compare(Person p1, Person p2) {
        return Ints.compare(p1.age, p2.age);
    }
};

persons.sort(orderingByAge);

assertEquals(Arrays.asList(new Person("Alice", 3), new Person("Michael", 10)), persons);
```

然后我们可以使用我们的orderingByAge并将其传递给sort()方法。

## 3. 链式排序

这个类的一个有用的特性是我们可以链接不同的排序方式。假设我们有一个人的集合，我们想按年龄字段对它进行排序，并且在列表的开头有空的年龄字段值：

```java
List<Person> persons = Arrays.asList(
  new Person("Michael", 10),
  new Person("Alice", 3), 
  new Person("Thomas", null));
 
Ordering<Person> ordering = Ordering
  .natural()
  .nullsFirst()
  .onResultOf(new Function<Person, Comparable>() {
      @Override
      public Comparable apply(Person person) {
          return person.age;
      }
});

persons.sort(ordering);
        
assertEquals(Arrays.asList(
  new Person("Thomas", null), 
  new Person("Alice", 3), 
  new Person("Michael", 10)), persons);
```

这里需要注意的重要一点是执行特定Ordering的顺序——顺序是从右到左。所以首先执行onResultOf()并且该方法提取我们想要比较的字段。

然后，执行nullFirst()比较器。因此，生成的排序集合将有一个Person对象，该对象在列表的开头有一个null作为年龄字段。

在排序过程结束时，使用natural()方法指定的自然顺序比较年龄字段。

## 4. 总结

在本文中，我们查看了 Guava 库中的Ordering类，它允许我们创建更流畅和优雅的Comparator。我们创建了自定义排序，我们使用了 API 中的预定义排序，并将它们链接起来以实现更多自定义排序。