## 1. 简介

在[RxJava简介](https://www.baeldung.com/rx-java)文章之后，我们将看看聚合和数学运算符。

**这些操作必须等待源Observable发出所有元素**。正因为如此，这些运算符在可能表示非常长或无限序列的Observables上使用是危险的。

其次，所有示例都使用TestSubscriber的一个实例，这是一种可用于单元测试的特殊订阅者，用于执行断言、检查接收到的事件或包装模拟的订阅者。

现在，让我们开始研究数学运算符。

## 2. 设置

要使用其他运算符，我们需要将[额外的依赖项](https://search.maven.org/search?q=a:rxjava-math)添加到pom.xml：

```xml
<dependency>
    <groupId>io.reactivex</groupId>
    <artifactId>rxjava-math</artifactId>
    <version>1.0.0</version>
</dependency>
```

或者，对于Gradle项目：

```groovy
compile 'io.reactivex:rxjava-math:1.0.0'
```

## 3. 数学运算符

**MathObservable专用于执行数学运算**，它的运算符使用另一个Observable，该Observable发出可以作为数字进行评估的元素。

### 3.1 average

average运算符发出单个值-源发出的所有值的平均值。

让我们看看实际效果：

```java
Observable<Integer> sourceObservable = Observable.range(1, 20);
TestSubscriber<Integer> subscriber = TestSubscriber.create();

MathObservable.averageInteger(sourceObservable).subscribe(subscriber);

subscriber.assertValue(10);
```

有四个类似的运算符用于处理原始类型值：averageInteger、averageLong、averageFloat和averageDouble。

### 3.2 max

max运算符发出遇到的最大数字。

让我们看看实际效果：

```java
Observable<Integer> sourceObservable = Observable.range(1, 20);
TestSubscriber<Integer> subscriber = TestSubscriber.create();

MathObservable.max(sourceObservable).subscribe(subscriber);

subscriber.assertValue(9);
```

重要的是要注意max运算符有一个接收比较函数的重载方法。

考虑到数学运算符也可以处理可以作为数字进行管理的对象，max重载运算符允许比较自定义类型或对标准类型进行自定义排序。

让我们定义Item类：

```java
class Item {
    private Integer id;

    // standard constructors, getter, and setter
}
```

现在我们可以定义itemObservable，然后使用max运算符发出具有最高id的Item：

```java
Item five = new Item(5);
List<Item> list = Arrays.asList(
    new Item(1), 
    new Item(2), 
    new Item(3), 
    new Item(4), 
    five);
Observable<Item> itemObservable = Observable.from(list);

TestSubscriber<Item> subscriber = TestSubscriber.create();

MathObservable.from(itemObservable)
    .max(Comparator.comparing(Item::getId))
    .subscribe(subscriber);

subscriber.assertValue(five);
```

### 3.3 min

min运算符发出单个元素，其中包含来自源的最小元素：

```java
Observable<Integer> sourceObservable = Observable.range(1, 20);
TestSubscriber<Integer> subscriber = TestSubscriber.create();

MathObservable.min(sourceObservable).subscribe(subscriber);

subscriber.assertValue(1);
```

min运算符有一个重载方法，它接收一个比较器实例：

```java
Item one = new Item(1);
List<Item> list = Arrays.asList(
    one, 
    new Item(2), 
    new Item(3), 
    new Item(4), 
    new Item(5));
TestSubscriber<Item> subscriber = TestSubscriber.create();
Observable<Item> itemObservable = Observable.from(list);

MathObservable.from(itemObservable)
    .min(Comparator.comparing(Item::getId))
    .subscribe(subscriber);

subscriber.assertValue(one);
```

### 3.4 sum

sum运算符发出单个值，表示源Observable发出的所有数字的总和：

```java
Observable<Integer> sourceObservable = Observable.range(1, 20);
TestSubscriber<Integer> subscriber = TestSubscriber.create();

MathObservable.sumInteger(sourceObservable).subscribe(subscriber);

subscriber.assertValue(210);
```

还有原始类型的类似运算符：sumInteger、sumLong、sumFloat和sumDouble。

## 4. 聚合运算符

### 4.1 concat

concat运算符将源发出的元素连接在一起。

现在让我们定义两个Observables并连接它们：

```java
List<Integer> listOne = Arrays.asList(1, 2, 3, 4);
Observable<Integer> observableOne = Observable.from(listOne);

List<Integer> listTwo = Arrays.asList(5, 6, 7, 8);
Observable<Integer> observableTwo = Observable.from(listTwo);

TestSubscriber<Integer> subscriber = TestSubscriber.create();

Observable<Integer> concatObservable = observableOne
    .concatWith(observableTwo);

concatObservable.subscribe(subscriber);

subscriber.assertValues(1, 2, 3, 4, 5, 6, 7, 8);
```

详细来说，concat运算符等待订阅传递给它的每个额外的Observable，直到前一个Observable完成。

出于这个原因，连接一个立即开始发射元素的“热”Observable，将导致“热”Observable在所有先前的元素完成之前发射的任何元素丢失。

### 4.2 count

count运算符发出源发出的所有元素的计数：

让我们计算一个Observable发出的元素数：

```java
List<String> lettersList = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
TestSubscriber<Integer> subscriber = TestSubscriber.create();

Observable<Integer> sourceObservable = Observable
    .from(lettersList).count();
sourceObservable.subscribe(subscriber);

subscriber.assertValue(7);
```

如果源Observable因错误而终止，则count将传递一个通知错误而不会发出任何元素。但是，如果它根本不终止，则count既不会发出元素也不会终止。

对于count操作，还有countLong运算符，它最终会为那些可能超过Integer容量的序列发出一个Long值。

### 4.3 reduce

reduce运算符通过应用累加器函数将所有发出的元素归约为单个元素。

这个过程一直持续到所有元素都被发出，然后Observable从reduce发出函数返回的最终值。

现在，让我们看看如何归约String列表，以相反的顺序拼接它们：

```java
List<String> list = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
TestSubscriber<String> subscriber = TestSubscriber.create();

Observable<String> reduceObservable = Observable.from(list)
    .reduce((letter1, letter2) -> letter2 + letter1);
reduceObservable.subscribe(subscriber);

subscriber.assertValue("GFEDCBA");
```

### 4.4 collect

collect运算符类似于reduce运算符，但它专用于将元素收集到单个可变数据结构中。

它需要两个参数：

-   返回空可变数据结构的函数
-   一个函数，当给定数据结构和发出的元素时，适当地修改数据结构

让我们看看如何从Observable返回一组元素：

```java
List<String> list = Arrays.asList("A", "B", "C", "B", "B", "A", "D");
TestSubscriber<HashSet> subscriber = TestSubscriber.create();

Observable<HashSet<String>> reduceListObservable = Observable
    .from(list)
    .collect(HashSet::new, HashSet::add);
reduceListObservable.subscribe(subscriber);

subscriber.assertValues(new HashSet(list));
```

### 4.5 toList

toList运算符的工作方式与collect操作类似，但它会将所有元素收集到一个列表中-想想Stream API中的Collectors.toList()：

```java
Observable<Integer> sourceObservable = Observable.range(1, 5);
TestSubscriber<List> subscriber = TestSubscriber.create();

Observable<List<Integer>> listObservable = sourceObservable
    .toList();
listObservable.subscribe(subscriber);

subscriber.assertValue(Arrays.asList(1, 2, 3, 4, 5));
```

### 4.6 toSortedList

就像前面的例子一样，但发出的列表是排序的：

```java
Observable<Integer> sourceObservable = Observable.range(10, 5);
TestSubscriber<List> subscriber = TestSubscriber.create();

Observable<List<Integer>> listObservable = sourceObservable
    .toSortedList();
listObservable.subscribe(subscriber);

subscriber.assertValue(Arrays.asList(10, 11, 12, 13, 14));
```

如我们所见，toSortedList使用默认比较，但可以提供自定义比较器函数。现在，我们可以看到如何使用自定义排序函数以相反的顺序对整数进行排序：

```java
Observable<Integer> sourceObservable = Observable.range(10, 5);
TestSubscriber<List> subscriber = TestSubscriber.create();

Observable<List<Integer>> listObservable 
  = sourceObservable.toSortedList((int1, int2) -> int2 - int1);
listObservable.subscribe(subscriber);

subscriber.assertValue(Arrays.asList(14, 13, 12, 11, 10));
```

### 4.7 toMap

toMap运算符将Observable发出的元素序列转换为由指定键函数键入的Map。

特别是，toMap运算符具有不同的重载方法，这些方法需要以下一个、两个或三个参数：

1.  从元素中生成键的keySelector
2.  valueSelector从发出的元素中产生将存储在Map中的实际值
3.  创建将保存元素的集合的mapFactory

让我们开始定义一个简单的类Book：

```java
class Book {
    private String title;
    private Integer year;

    // standard constructors, getters, and setters
}
```

现在，我们可以看到如何将一系列发出的Book元素转换为Map，将书名作为键，将年份作为值：

```java
Observable<Book> bookObservable = Observable.just(
    new Book("The North Water", 2016), 
    new Book("Origin", 2017), 
    new Book("Sleeping Beauties", 2017)
);
TestSubscriber<Map> subscriber = TestSubscriber.create();

Observable<Map<String, Integer>> mapObservable = bookObservable
    .toMap(Book::getTitle, Book::getYear, HashMap::new);
mapObservable.subscribe(subscriber);

subscriber.assertValue(new HashMap() {{
    put("The North Water", 2016);
    put("Origin", 2017);
    put("Sleeping Beauties", 2017);
}});
```

### 4.8 toMultiMap

映射时，许多值共享同一个键是很常见的。将一个键映射到多个值的数据结构称为Multimap。

这可以通过toMultiMap运算符来实现，该运算符将Observable发出的元素序列转换为一个列表，该列表也是一个由指定键函数键入的Map。

该运算符向toMap运算符的参数添加了另一个参数，即collectionFactory。此参数允许指定应将值存储在哪种集合类型中。让我们看看如何做到这一点：

```java
Observable<Book> bookObservable = Observable.just(
    new Book("The North Water", 2016), 
    new Book("Origin", 2017), 
    new Book("Sleeping Beauties", 2017)
);
TestSubscriber<Map> subscriber = TestSubscriber.create();

Observable multiMapObservable = bookObservable.toMultimap(
    Book::getYear, 
    Book::getTitle, 
    () -> new HashMap<>(), 
    (key) -> new ArrayList<>()
);
multiMapObservable.subscribe(subscriber);

subscriber.assertValue(new HashMap() {{
    put(2016, Arrays.asList("The North Water"));
    put(2017, Arrays.asList("Origin", "Sleeping Beauties"));
}});
```

## 5. 总结

在本文中，我们探讨了RxJava中可用的数学和聚合运算符，当然，还有如何使用它们的简单示例。