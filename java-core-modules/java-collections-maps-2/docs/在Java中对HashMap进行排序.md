## 1. 概述

在本快速教程中，我们将学习如何在 Java中对HashMap进行排序。

更具体地说，我们将使用以下方法按键或值对HashMap 条目进行排序：

-   树图
-   ArrayList和Collections.sort()
-   树集
-   使用流API
-   使用番石榴 库

## 2. 使用TreeMap

正如我们所知，TreeMap中的键是使用它们的自然顺序排序的。当我们想按键对键值对进行排序时，这是一个很好的解决方案。所以我们的想法是将我们的HashMap中的所有数据推送到[TreeMap](https://www.baeldung.com/java-treemap)中。

首先，让我们定义一个HashMap并用一些数据初始化它：

```java
Map<String, Employee> map = new HashMap<>();

Employee employee1 = new Employee(1L, "Mher");
map.put(employee1.getName(), employee1);
Employee employee2 = new Employee(22L, "Annie");
map.put(employee2.getName(), employee2);
Employee employee3 = new Employee(8L, "John");
map.put(employee3.getName(), employee3);
Employee employee4 = new Employee(2L, "George");
map.put(employee4.getName(), employee4);
```

对于Employee类，请注意我们实现了Comparable：

```java
public class Employee implements Comparable<Employee> {

    private Long id;
    private String name;

    // constructor, getters, setters

    // override equals and hashCode
    @Override
    public int compareTo(Employee employee) {
        return (int)(this.id - employee.getId());
    }
}
```

接下来，我们使用其构造函数将条目存储在TreeMap 中：

```java
TreeMap<String, Employee> sorted = new TreeMap<>(map);
```

我们也可以使用putAll 方法来数据：

```java
TreeMap<String, Employee> sorted = new TreeMap<>();
sorted.putAll(map);
```

就是这样！为了确保我们的地图条目按键排序，让我们打印出来：

```plaintext
Annie=Employee{id=22, name='Annie'}
George=Employee{id=2, name='George'}
John=Employee{id=8, name='John'}
Mher=Employee{id=1, name='Mher'}
```

正如我们所见，键是按自然顺序排序的。

## 3.使用 数组列表

当然，我们可以借助 ArrayList对地图的条目进行排序。与前一种方法的主要区别在于我们在这里不维护Map接口。

### 3.1. 按键排序

让我们将键集加载到ArrayList中：

```java
List<String> employeeByKey = new ArrayList<>(map.keySet());
Collections.sort(employeeByKey);
```

输出是：

```plaintext
[Annie, George, John, Mher]
```

### 3.2. 按值排序

现在，如果我们想按Employee对象的id字段对映射值进行排序怎么办？我们也可以为此使用ArrayList 。

首先，让我们将值到列表中：

```java
List<Employee> employeeById = new ArrayList<>(map.values());
```

然后我们排序：

```java
Collections.sort(employeeById);
```

请记住，这是有效的，因为Employee 实现了Comparable接口。否则，我们需要为调用 Collections.sort定义一个手动比较器。

为了检查结果，我们打印employeeById：

```plaintext
[Employee{id=1, name='Mher'}, 
Employee{id=2, name='George'}, 
Employee{id=8, name='John'}, 
Employee{id=22, name='Annie'}]
```

如我们所见，对象按其id字段排序。

## 4. 使用 树集 

如果我们不想在我们的排序集合中接受重复值，TreeSet 有一个很好的解决方案。

首先，让我们在初始地图中添加一些重复的条目：

```java
Employee employee5 = new Employee(1L, "Mher");
map.put(employee5.getName(), employee5);
Employee employee6 = new Employee(22L, "Annie");
map.put(employee6.getName(), employee6);
```

### 4.1. 按键排序

要按其关键条目对地图进行排序：

```java
SortedSet<String> keySet = new TreeSet<>(map.keySet());
```

让我们打印keySet 并查看输出：

```plaintext
[Annie, George, John, Mher]
```

现在我们对地图键进行了排序，没有重复项。

### 4.2. 按值排序

同样，对于映射值，转换代码如下所示：

```java
SortedSet<Employee> values = new TreeSet<>(map.values());
```

结果是：

```plaintext
[Employee{id=1, name='Mher'}, 
Employee{id=2, name='George'}, 
Employee{id=8, name='John'}, 
Employee{id=22, name='Annie'}]
```

如我们所见，输出中没有重复项。 当我们覆盖equals和hashCode 时，这适用于自定义对象。

## 5. 使用 Lambda 和流

从Java8 开始，我们可以使用 Stream API 和 lambda 表达式对 map 进行排序。 我们所需要做的就是通过地图的流 管道调用sorted方法。

### 5.1. 按键排序

要按键排序，我们使用 comparingByKey 比较器：

```java
map.entrySet()
  .stream()
  .sorted(Map.Entry.<String, Employee>comparingByKey())
  .forEach(System.out::println);
```

最后的forEach阶段打印出结果：

```plaintext
Annie=Employee{id=22, name='Annie'}
George=Employee{id=2, name='George'}
John=Employee{id=8, name='John'}
Mher=Employee{id=1, name='Mher'}
```

默认情况下，排序方式是升序。

### 5.2. 按值排序

当然，我们也可以按Employee 对象排序：

```java
map.entrySet()
  .stream()
  .sorted(Map.Entry.comparingByValue())
  .forEach(System.out::println);
```

如我们所见，上面的代码打印出一个按Employee对象的id字段排序的映射：

```plaintext
Mher=Employee{id=1, name='Mher'}
George=Employee{id=2, name='George'}
John=Employee{id=8, name='John'}
Annie=Employee{id=22, name='Annie'}
```

此外，我们可以将结果收集到一张新地图中：

```java
Map<String, Employee> result = map.entrySet()
  .stream()
  .sorted(Map.Entry.comparingByValue())
  .collect(Collectors.toMap(
    Map.Entry::getKey, 
    Map.Entry::getValue, 
    (oldValue, newValue) -> oldValue, LinkedHashMap::new));
```

请注意，我们将结果收集到 LinkedHashMap中。默认情况下， Collectors.toMap 返回一个新的 HashMap，但正如我们所知，HashMap不保证迭代 顺序，而 LinkedHashMap可以。

## 6.使用番石榴

最后，允许我们对HashMap进行排序的库是 Guava。在我们开始之前，检查一下我们关于[Guava 地图的](https://www.baeldung.com/guava-maps)文章会很有用。

首先，让我们声明一个 [Ordering](https://www.baeldung.com/guava-ordering)，因为我们想按Employee 的 Id字段对地图进行排序：

```java
Ordering naturalOrdering = Ordering.natural()
  .onResultOf(Functions.forMap(map, null));
```

现在我们只需要使用ImmutableSortedMap 来说明结果：

```java
ImmutableSortedMap.copyOf(map, naturalOrdering);
```

再一次，输出是一个按id字段排序的映射：

```plaintext
Mher=Employee{id=1, name='Mher'}
George=Employee{id=2, name='George'}
John=Employee{id=8, name='John'}
Annie=Employee{id=22, name='Annie'}
```

## 七、总结

在本文中，我们回顾了多种按键或值对HashMap 进行排序的方法。

当属性是自定义类时，我们还学习了如何通过实现Comparable 来做到这一点。