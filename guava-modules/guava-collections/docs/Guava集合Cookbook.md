## 1. 简介

这篇食谱文章被组织成小而集中的食谱和代码片段，用于使用 Guava 风格的集合。

格式是不断增加的代码示例列表，无需额外解释 - 它旨在使 API 的常见用法在开发过程中易于访问。

## 2.食谱

将 List<Parent> 向下转换为 List<Child>

–注意：这是Java中非协变泛型集合的解决方法


```java
class CastFunction<F, T extends F> implements Function<F, T> {
    @Override
    public final T apply(final F from) {
        return (T) from;
    }
}
List<TypeParent> originalList = Lists.newArrayList();
List<TypeChild> theList = Lists.transform(originalList, 
    new CastFunction<TypeParent, TypeChild>());
```

没有 Guava 的更简单的替代方案——涉及 2 个转换操作

```java
List<Number> originalList = Lists.newArrayList();
List<Integer> theList = (List<Integer>) (List<? extends Number>) originalList;
```

向集合中添加一个可迭代对象

```java
Iterable<String> iter = Lists.newArrayList();
Collection<String> collector = Lists.newArrayList();
Iterables.addAll(collector, iter);
```

根据自定义匹配规则检查集合是否包含元素


```java
Iterable<String> theCollection = Lists.newArrayList("a", "bc", "def");
    boolean contains = Iterables.any(theCollection, new Predicate<String>() {
    @Override
    public boolean apply(final String input) {
        return input.length() == 1;
    }
});
assertTrue(contains);
```

使用搜索的替代解决方案

```java
Iterable<String> theCollection = Sets.newHashSet("a", "bc", "def");
boolean contains = Iterables.find(theCollection, new Predicate<String>() {
    @Override
    public boolean apply(final String input) {
       return input.length() == 1;
    }
}) != null;
assertTrue(contains);
```

仅适用于集合的替代解决方案

```java
Set<String> theCollection = Sets.newHashSet("a", "bc", "def");
boolean contains = !Sets.filter(theCollection, new Predicate<String>() {
    @Override
    public boolean apply(final String input) {
        return input.length() == 1;
    }
}).isEmpty();
assertTrue(contains);
```

NoSuchElementException on Iterables.find当什么都找不到时

```java
Iterable<String> theCollection = Sets.newHashSet("abcd", "efgh", "ijkl");
Predicate<String> inputOfLengthOne = new Predicate<String>() {
    @Override
    public boolean apply(final String input) {
        return input.length() == 1;
    }
};
String found = Iterables.find(theCollection, inputOfLengthOne);
```

– 这将抛出NoSuchElementException异常：

```bash
java.util.NoSuchElementException
	at com.google.common.collect.AbstractIterator.next(AbstractIterator.java:154)
	at com.google.common.collect.Iterators.find(Iterators.java:712)
	at com.google.common.collect.Iterables.find(Iterables.java:643)
```

–解决方案：有一个重载的find方法，它将默认返回值作为参数，并且可以用null调用以获得所需的行为：

```java
String found = Iterables.find(theCollection, inputOfLengthOne, null);
```

从集合中删除所有空值

```java
List<String> values = Lists.newArrayList("a", null, "b", "c");
Iterable<String> withoutNulls = Iterables.filter(values, Predicates.notNull());
```

直接创建不可变的 List/Set/Map

```java
ImmutableList<String> immutableList = ImmutableList.of("a", "b", "c");
ImmutableSet<String> immutableSet = ImmutableSet.of("a", "b", "c");
ImmutableMap<String, String> imuttableMap = 
    ImmutableMap.of("k1", "v1", "k2", "v2", "k3", "v3");
```

从标准集合创建不可变列表/集合/地图

```java
List<String> muttableList = Lists.newArrayList();
ImmutableList<String> immutableList = ImmutableList.copyOf(muttableList);

Set<String> muttableSet = Sets.newHashSet();
ImmutableSet<String> immutableSet = ImmutableSet.copyOf(muttableSet);

Map<String, String> muttableMap = Maps.newHashMap();
ImmutableMap<String, String> imuttableMap = ImmutableMap.copyOf(muttableMap);
```

使用构建器的替代解决方案

```java
List<String> muttableList = Lists.newArrayList();
ImmutableList<String> immutableList = 
    ImmutableList.<String> builder().addAll(muttableList).build();

Set<String> muttableSet = Sets.newHashSet();
ImmutableSet<String> immutableSet = 
    ImmutableSet.<String> builder().addAll(muttableSet).build();

Map<String, String> muttableMap = Maps.newHashMap();
ImmutableMap<String, String> imuttableMap = 
    ImmutableMap.<String, String> builder().putAll(muttableMap).build();
```

## 3. 更多番石榴食谱

Guava 是一个全面且非常有用的库——这里有一些以食谱形式介绍的 API：

-   #### [番石榴订购食谱](https://www.baeldung.com/guava-order)

-   #### [番石榴功能食谱](https://www.baeldung.com/guava-functions-predicates)

享受。

## 4. 前进

正如我在开头提到的，我正在试验这种不同的格式——食谱——试图在一个地方收集使用 Guava Collections 的简单常见任务。这种格式的重点是简单和速度，所以大多数食谱除了代码示例本身之外没有额外的解释。

最后——我将其视为一份动态文档——我将在遇到它们时不断添加食谱和示例。欢迎在评论中提供更多内容，我会考虑将它们合并到食谱中。