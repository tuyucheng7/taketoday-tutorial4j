## 1. 概述

在列表中查找元素是我们作为开发人员遇到的一项非常常见的任务。

在本快速教程中，我们将介绍使用Java执行此操作的不同方法。

## 延伸阅读：

## [检查列表是否在Java中排序](https://www.baeldung.com/java-check-if-list-sorted)

学习几种检查列表是否在Java中排序的算法。

[阅读更多](https://www.baeldung.com/java-check-if-list-sorted)→

## [一行中的Java列表初始化](https://www.baeldung.com/java-init-list-one-line)

在本快速教程中，我们将研究如何使用单行代码初始化 List。

[阅读更多](https://www.baeldung.com/java-init-list-one-line)→

## 2.设置

首先让我们从定义Customer POJO 开始：

```java
public class Customer {

    private int id;
    private String name;
    
    // getters/setters, custom hashcode/equals
}
```

然后是客户的[ArrayList](https://www.baeldung.com/java-arraylist)：

```java
List<Customer> customers = new ArrayList<>();
customers.add(new Customer(1, "Jack"));
customers.add(new Customer(2, "James"));
customers.add(new Customer(3, "Kelly"));

```

请注意，我们已经覆盖 了Customer类中的hashCode 和equals 。

根据我们当前的equals实现，具有相同id的两个Customer对象将被视为相等。

我们将在此过程中使用此客户列表 。

## 3. 使用Java API

Java 本身提供了几种在列表中查找项目的方法：

-   包含方法_ 
-   索引 方法_ 
-   一个特别的 for 循环
-   流 API _ 

### 3.1. 包含()

List 公开了一个名为 contains的方法：

```java
boolean contains(Object element)
```

顾名思义，如果列表包含指定元素，则此方法返回true ，否则 返回false。 

因此，当我们需要检查列表中是否存在特定项目时，我们可以：

```java
Customer james = new Customer(2, "James");
if (customers.contains(james)) {
    // ...
}
```

### 3.2. 指数()

indexOf 是另一种查找元素的有用方法：

```java
int indexOf(Object element)
```

此方法返回给定列表中指定元素第一次出现的索引，如果列表不包含该元素，则返回 -1 。

所以从逻辑上讲，如果此方法返回 -1 以外的任何值，我们就知道列表包含元素：

```java
if(customers.indexOf(james) != -1) {
    // ...
}
```

使用此方法的主要优点是它可以告诉我们指定元素在给定列表中的位置。

### 3.3. 基本循环

现在如果我们想对元素进行基于字段的搜索怎么办？例如，假设我们要宣布彩票，我们需要将具有特定名称的客户声明为中奖者。

对于这种基于字段的搜索，我们可以转向迭代。

遍历列表的一种传统方法是使用[Java 的循环](https://www.baeldung.com/java-loops)结构之一。在每次迭代中，我们将列表中的当前项与我们要查找的元素进行比较，看它是否匹配：

```java
public Customer findUsingEnhancedForLoop(
  String name, List<Customer> customers) {

    for (Customer customer : customers) {
        if (customer.getName().equals(name)) {
            return customer;
        }
    }
    return null;
}
```

这里的名字是指我们在给定的客户列表中搜索的名字。此方法返回列表中具有匹配名称的第一个Customer对象，如果不存在这样的Customer，则返回null。

### 3.4. 使用迭代器循环

[迭代器](https://www.baeldung.com/java-iterator) 是我们遍历项目列表的另一种方式。

我们可以简单地采用我们之前的例子并稍微调整一下：

```java
public Customer findUsingIterator(
  String name, List<Customer> customers) {
    Iterator<Customer> iterator = customers.iterator();
    while (iterator.hasNext()) {
        Customer customer = iterator.next();
        if (customer.getName().equals(name)) {
            return customer;
        }
    }
    return null;
}
```

因此，行为与以前相同。

### 3.5.Java8流API

从Java8 开始，我们还可以 [使用Stream API](https://www.baeldung.com/java-8-streams) 来查找列表中的元素。

要在给定列表中找到与特定条件匹配的元素，我们：

-   在列表中调用stream()
-    使用适当的Predicate调用 filter ()方法
-   调用 findAny() 构造， 如果存在这样的元素，它将返回与包装在Optional中的过滤器谓词匹配的第 一个元素
    

```java
Customer james = customers.stream()
  .filter(customer -> "James".equals(customer.getName()))
  .findAny()
  .orElse(null);
```

为了方便起见，我们默认为null以防Optional为空，但这可能并不总是每个场景的最佳选择。

## 4. 第三方库

现在，虽然 Stream API 已经绰绰有余，但如果我们卡在Java的早期版本上，该怎么办？

幸运的是，我们可以使用许多第三方库，例如 Google Guava 和 Apache Commons。

### 4.1. 谷歌番石榴

Google Guava 提供的功能类似于我们可以对流执行的操作：

```java
Customer james = Iterables.tryFind(customers,
  new Predicate<Customer>() {
      public boolean apply(Customer customer) {
          return "James".equals(customer.getName());
      }
  }).orNull();
```

就像Stream API 一样，我们可以选择返回默认值而不是null：

```java
Customer james = Iterables.tryFind(customers,
  new Predicate<Customer>() {
      public boolean apply(Customer customer) {
          return "James".equals(customer.getName());
      }
  }).or(customers.get(0));
```

如果找不到匹配项，上面的代码将选择列表中的第一个元素。

另外，不要忘记如果列表或谓词为null ，Guava 会抛出NullPointerException。

### 4.2. 阿帕奇公地

我们可以使用 Apache Commons 以几乎完全相同的方式找到一个元素：

```java
Customer james = IterableUtils.find(customers,
  new Predicate<Customer>() {
      public boolean evaluate(Customer customer) {
          return "James".equals(customer.getName());
      }
  });
```

但是有几个重要的区别：

1.  如果我们传递一个null列表，Apache Commons 只会返回null 。
2.  它 不像 Guava 的tryFind 那样提供默认值功能。

## 5.总结

在本文中，我们学习了在列表中查找元素的不同方法，从快速存在性检查开始，到基于字段的搜索结束。

我们还研究了第三方库 Google Guava和Apache Commons作为Java8 Streams API的替代品。