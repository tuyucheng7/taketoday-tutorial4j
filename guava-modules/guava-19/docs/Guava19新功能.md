## 1. 概述

[Google Guava](https://github.com/google/guava)为库提供了可简化Java开发的实用程序。在本教程中，我们将了解[Guava 19 版本](https://github.com/google/guava/wiki/Release19)中引入的新功能。

## 2. common.base包改动

2.1. 添加了 CharMatcher静态方法

CharMatcher，顾名思义，就是用来检查一个字符串是否符合一组要求。

```java
String inputString = "someString789";
boolean result = CharMatcher.javaLetterOrDigit().matchesAllOf(inputString);

```

在上面的示例中，结果将为true。

当你需要转换字符串时，也可以使用CharMatcher 。

```java
String number = "8 123 456 123";
String result = CharMatcher.whitespace().collapseFrom(number, '-');

```

在上面的示例中，结果将为“8-123-456-123”。

在CharMatcher的帮助下，你可以计算给定字符串中字符出现的次数：

```java
String number = "8 123 456 123";
int result = CharMatcher.digit().countIn(number);
```

在上面的示例中，结果将为 10。

以前版本的 Guava 具有匹配器常量，例如CharMatcher.WHITESPACE和CharMatcher.JAVA_LETTER_OR_DIGIT。

在 Guava 19 中，这些已被等效方法(分别为CharMatcher.whitespace()和CharMatcher.javaLetterOrDigit())取代。这已更改为减少使用CharMatcher时创建的类的数量。

使用静态工厂方法允许只在需要时创建类。在未来的版本中，匹配器常量将被弃用和删除。

2.2. Throwables中的lazyStackTrace方法

此方法返回提供的Throwable的堆栈跟踪元素(行)列表。如果只需要一部分，它可能比遍历整个堆栈跟踪 ( Throwable.getStackTrace() ) 更快，但如果你要遍历整个堆栈跟踪，它可能会更慢。

```java
IllegalArgumentException e = new IllegalArgumentException("Some argument is incorrect");
List<StackTraceElement> stackTraceElements = Throwables.lazyStackTrace(e);

```

## 3. common.collect包改动

3.1. 添加了 FluentIterable.toMultiset()

在之前的 Baeldung 文章，[Whats new in Guava 18](https://www.baeldung.com/whats-new-in-guava-18)中，我们查看了FluentIterable。当你需要将FluentIterable转换为ImmutableMultiSet时，将使用toMultiset()方法。

```java
User[] usersArray = {new User(1L, "John", 45), new User(2L, "Max", 15)};
ImmutableMultiset<User> users = FluentIterable.of(usersArray).toMultiset();
```

Multiset是一个集合，就像Set一样，它支持与顺序无关的相等性。Set和Multiset之间的主要区别在于Multiset可能包含重复元素。Multiset将相等的元素存储为同一单个元素的出现次数，因此你可以调用Multiset.count(java.lang.Object)来获取给定对象出现的总次数。

让我们看几个例子：

```java
List<String> userNames = Arrays.asList("David", "Eugen", "Alex", "Alex", "David", "David", "David");

Multiset<String> userNamesMultiset = HashMultiset.create(userNames);

assertEquals(7, userNamesMultiset.size());
assertEquals(4, userNamesMultiset.count("David"));
assertEquals(2, userNamesMultiset.count("Alex"));
assertEquals(1, userNamesMultiset.count("Eugen"));
assertThat(userNamesMultiset.elementSet(), anyOf(containsInAnyOrder("Alex", "David", "Eugen")));
```

你可以轻松确定重复元素的数量，这比标准Java集合要干净得多。

3.2. 添加了RangeSet.asDescendingSetOfRanges()和asDescendingMapOfRanges()


RangeSet用于操作非空范围(区间)。我们可以将RangeSet描述为一组断开连接的非空范围。当你向RangeSet添加一个新的非空范围时，任何连接的范围都将被合并，而空范围将被忽略：

让我们看一下我们可以用来构建新范围的一些方法：Range.closed()、Range.openClosed()、Range.closedOpen()、Range.open()。

它们之间的区别在于开放范围不包括它们的端点。它们在数学中有不同的名称。开区间用“(”或“)”表示，而闭区间用“[”或“]”表示。

例如 (0,5) 表示“任何大于 0 且小于 5 的值”，而 (0,5] 表示“任何大于 0 且小于或等于 5 的值”：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(1, 10));
```

在这里，我们将范围 [1, 10] 添加到我们的RangeSet中。现在我们想通过添加新范围来扩展它：

```java
rangeSet.add(Range.closed(5, 15));
```

你可以看到这两个范围在 5 处相连，因此RangeSet会将它们合并到一个新的单一范围 [1, 15]：

```java
rangeSet.add(Range.closedOpen(10, 17));
```

这些范围在 10 处相连，因此它们将被合并，从而产生一个闭开范围 [1, 17)。你可以使用contains方法检查值是否包含在范围内：

```java
rangeSet.contains(15);
```

这将返回true，因为范围 [1,17) 包含 15。让我们尝试另一个值：

```java
rangeSet.contains(17);

```

这将返回false，因为范围 [1,17) 不包含它的上端点 17。你还可以使用encloses方法检查范围是否包含任何其他范围：

```java
rangeSet.encloses(Range.closed(2, 3));
```

这将返回true，因为范围 [2,3] 完全落在我们的范围 [1,17) 内。

还有一些方法可以帮助你使用间隔进行操作，例如Range.greaterThan()、Range.lessThan()、Range.atLeast()、Range.atMost()。前两个将添加开区间，后两个将添加闭区间。例如：

```java
rangeSet.add(Range.greaterThan(22));

```

这将为你的RangeSet添加一个新的间隔 (22, +∞) ，因为它与其他间隔没有联系。

借助asDescendingSetOfRanges(对于RangeSet)和asDescendingMapOfRanges(对于RangeSet)等新方法，你可以将RangeSet转换为Set或Map。

3.3. 添加了 Lists.cartesianProduct(List…)和Lists.cartesianProduct(List<List>>)

笛卡尔积返回两个或多个集合的所有可能组合：

```java
List<String> first = Lists.newArrayList("value1", "value2");
List<String> second = Lists.newArrayList("value3", "value4");

List<List<String>> cartesianProduct = Lists.cartesianProduct(first, second);

List<String> pair1 = Lists.newArrayList("value2", "value3");
List<String> pair2 = Lists.newArrayList("value2", "value4");
List<String> pair3 = Lists.newArrayList("value1", "value3");
List<String> pair4 = Lists.newArrayList("value1", "value4");

assertThat(cartesianProduct, anyOf(containsInAnyOrder(pair1, pair2, pair3, pair4)));

```

从此示例中可以看出，结果列表将包含所提供列表的所有可能组合。

3.4. 添加了 Maps.newLinkedHashMapWithExpectedSize(int)

标准LinkedHashMap的初始大小为 16(你可以在LinkedHashMap的源代码中验证这一点)。当它达到HashMap的负载因子(默认为 0.75)时，HashMap将重新哈希并将其大小加倍。但是如果你知道你的HashMap将处理许多键值对，你可以指定一个大于 16 的初始大小，从而避免重复重新散列：

```java
LinkedHashMap<Object, Object> someLinkedMap = Maps.newLinkedHashMapWithExpectedSize(512);
```

3.5. 重新添加Multisets.removeOccurrences(Multiset, Multiset)

此方法用于删除Multiset中的指定事件：

```java
Multiset<String> multisetToModify = HashMultiset.create();
Multiset<String> occurrencesToRemove = HashMultiset.create();

multisetToModify.add("John");
multisetToModify.add("Max");
multisetToModify.add("Alex");

occurrencesToRemove.add("Alex");
occurrencesToRemove.add("John");

Multisets.removeOccurrences(multisetToModify, occurrencesToRemove);
```

在这个操作之后，只有“Max”会留在multisetToModify中。

请注意，如果multisetToModify包含给定元素的多个实例，而occurrencesToRemove仅包含该元素的一个实例，则 removeOccurrences将仅删除一个实例。

## 4. common.hash 包改动

4.1. 添加了 Hashing.sha384()

Hashing.sha384 ()方法返回一个实现 SHA-384 算法的哈希函数：

```java
int inputData = 15;
        
HashFunction hashFunction = Hashing.sha384();
HashCode hashCode = hashFunction.hashInt(inputData);
```

SHA-384 有 15 个是“0904b6277381dcfbddd…2240a621b2b5e3cda8”。

4.2. 添加了 Hashing.concatenating(HashFunction, HashFunction, HashFunction…)和Hashing.concatenating(Iterable<HashFunction>)

在Hashing.concatenating方法的帮助下，你可以连接一系列哈希函数的结果：

```java
int inputData = 15;

HashFunction crc32Function = Hashing.crc32();
HashCode crc32HashCode = crc32Function.hashInt(inputData);

HashFunction hashFunction = Hashing.concatenating(Hashing.crc32(), Hashing.crc32());
HashCode concatenatedHashCode = hashFunction.hashInt(inputData);

```

生成的串联哈希码将为“4acf27794acf2779”，这与与其自身串联的crc32HashCode(“4acf2779”)相同。

在我们的示例中，为清楚起见，使用了单个哈希算法。然而，这并不是特别有用。当你需要使你的哈希更强大时，结合两个哈希函数很有用，因为只有当你的两个哈希被破坏时它才会被破坏。对于大多数情况，使用两个不同的散列函数。

## 5. common.reflect包变化

5.1. 添加了 TypeToken.isSubtypeOf

即使在运行时， TypeToken也用于操作和查询泛型类型，避免[了类型擦除](https://github.com/google/guava/wiki/ReflectionExplained)带来的问题。

Java 在运行时不保留对象的通用类型信息，因此无法知道给定对象是否具有通用类型。但是借助反射，你可以检测方法或类的泛型类型。TypeToken使用此变通方法让你无需额外代码即可使用和查询泛型类型。

在我们的示例中，你可以看到，如果没有TypeToken方法isAssignableFrom ，即使ArrayList<String>不可从ArrayList<Integer>分配，它也会返回true：

```java
ArrayList<String> stringList = new ArrayList<>();
ArrayList<Integer> intList = new ArrayList<>();
boolean isAssignableFrom = stringList.getClass().isAssignableFrom(intList.getClass());
```

为了解决这个问题，我们可以借助TypeToken进行检查。

```java
TypeToken<ArrayList<String>> listString = new TypeToken<ArrayList<String>>() { };
TypeToken<ArrayList<Integer>> integerString = new TypeToken<ArrayList<Integer>>() { };

boolean isSupertypeOf = listString.isSupertypeOf(integerString);
```

在此示例中，isSupertypeOf将返回 false。

在以前的 Guava 版本中，有用于此目的的方法isAssignableFrom，但从 Guava 19 开始，它已被弃用，取而代之的是isSupertypeOf。此外，方法isSubtypeOf(TypeToken)可用于确定一个类是否是另一个类的子类型：

```java
TypeToken<ArrayList<String>> stringList = new TypeToken<ArrayList<String>>() { };
TypeToken<List> list = new TypeToken<List>() { };

boolean isSubtypeOf = stringList.isSubtypeOf(list);
```

ArrayList是List的子类型，因此结果将如预期的那样为true 。

## 6. common.io包更改

6.1. 添加了 ByteSource.sizeIfKnown()


如果可以在不打开数据流的情况下确定，此方法以字节为单位返回源的大小：

```java
ByteSource charSource = Files.asByteSource(file);
Optional<Long> size = charSource.sizeIfKnown();
```

6.2. 添加了 CharSource.length()

在以前的 Guava 版本中，没有方法可以确定CharSource的长度。现在你可以使用CharSource.length()来达到这个目的。

6.3. 添加了 CharSource.lengthIfKnown()

与 ByteSource相同，但使用CharSource.lengthIfKnown()你可以确定文件的字符长度：

```java
CharSource charSource = Files.asCharSource(file, Charsets.UTF_8);
Optional<Long> length = charSource.lengthIfKnown();

```

## 七. 总结

Guava 19 为其不断增长的库引入了许多有用的补充和改进。非常值得考虑在你的下一个项目中使用。