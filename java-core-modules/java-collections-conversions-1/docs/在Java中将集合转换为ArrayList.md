## 1. 概述

将Java集合从一种类型转换为另一种类型是一项常见的编程任务。在本教程中，我们会将任何类型的 Collection转换为ArrayList。

在整个教程中，我们假设我们已经有了Foo对象的集合。从那里，我们将 使用各种方法创建一个ArrayList 。

## 2. 定义我们的例子

但在继续之前，让我们对输入和输出进行建模。

我们的源可以是任何类型的集合，因此我们将使用Collection接口声明它：

```java
Collection<Foo> srcCollection;

```

我们需要生成一个具有相同元素类型的ArrayList ：

```java
ArrayList<Foo> newList;
```

## 3. 使用 ArrayList 构造函数

将集合到新集合的最简单方法是使用其构造函数。

在我们之前 [的 ArrayList 指南中](https://www.baeldung.com/java-arraylist)，我们了解到ArrayList构造函数可以接受一个集合参数：

```java
ArrayList<Foo> newList = new ArrayList<>(srcCollection);
```

-   新的ArrayList包含源集合中 Foo 元素的浅表副本。
-   顺序与源集合中的顺序相同。

构造函数的简单性使其成为大多数情况下的绝佳选择。

## 4. 使用流 API

现在，让我们利用 Streams API 从现有 Collection 创建 ArrayList：


```java
ArrayList<Foo> newList = srcCollection.stream().collect(toCollection(ArrayList::new));
```

在这个片段中：

-   我们从源集合中获取流并应用collect()运算符来创建一个列表
-   我们指定ArrayList::new来获取我们想要的列表类型
-   此代码还将生成一个浅拷贝。

如果我们不关心确切的List类型，我们可以简化：

```java
List<Foo> newList = srcCollection.stream().collect(toList());
```

请注意，toCollection()和toList()是从Collectors静态导入的。要了解更多信息，请参阅我们 [的Java8 收集器指南](https://www.baeldung.com/java-8-collectors)。

## 5.深拷贝

在我们提到“浅拷贝”之前。这样，我们的意思是新列表中的元素与源集合中仍然存在的Foo实例完全相同。因此，我们通过引用将Foo到newList。

如果我们修改任一集合中Foo实例的内容，该修改将反映在两个集合中。因此，如果我们想修改其中一个集合中的元素而不修改另一个集合中的元素，我们需要执行“深拷贝”。

为了深度Foo，我们为每个元素创建一个全新的Foo实例。因此，所有Foo字段都需要到新实例中。

让我们定义我们的Foo类，让它知道如何深拷贝自己：

```java
public class Foo {

    private int id;
    private String name;
    private Foo parent;

    public Foo(int id, String name, Foo parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    public Foo deepCopy() {
        return new Foo(
          this.id, this.name, this.parent != null ? this.parent.deepCopy() : null);
    }
}
```

在这里我们可以看到字段 id和name是int和String。这些数据类型按值。因此，我们可以简单地分配它们。

父字段是另一个Foo，它是一个类。如果Foo 发生变异，任何共享该引用的代码都会受到这些变化的影响。我们必须深 父字段。

现在我们可以回到我们的ArrayList转换。我们只需要map运算符将深拷贝插入到流中：

```java
ArrayList<Foo> newList = srcCollection.stream()
  .map(foo -> foo.deepCopy())
  .collect(toCollection(ArrayList::new));
```

我们可以在不影响另一个集合的情况下修改其中一个集合的内容。

根据元素的数量和数据的深度，深拷贝可能是一个漫长的过程。如果需要，在这里使用并行流可能会提高性能。

## 6.控制列表顺序

默认情况下，我们的流将按照元素在源集合中遇到的相同顺序将元素传送到我们的ArrayList 。

如果我们想更改该顺序，我们可以将sorted()运算符应用于 stream。按名称对我们的Foo对象进行排序：

```java
ArrayList<Foo> newList = srcCollection.stream()
  .sorted(Comparator.comparing(Foo::getName))
  .collect(toCollection(ArrayList::new));
```

我们可以在这个 [较早的教程](https://www.baeldung.com/java-stream-ordering)中找到有关流排序的更多详细信息。

## 七、总结

ArrayList构造函数是将 Collection 的内容放入新ArrayList的有效方法。

但是，如果我们需要调整结果列表，Streams API 提供了一种强大的方法来修改过程。