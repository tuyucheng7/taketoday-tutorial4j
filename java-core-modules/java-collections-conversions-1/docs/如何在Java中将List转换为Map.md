## 1. 概述

将List转换为Map是一项常见任务。在本教程中，我们将介绍执行此操作的几种方法。

我们假设List的每个元素都有一个标识符，该标识符将用作生成的Map中的键。

## 延伸阅读：

## [使用自定义供应商将列表转换为地图](https://www.baeldung.com/list-to-map-supplier)

了解使用自定义供应商将列表转换为地图的几种方法。

[阅读更多](https://www.baeldung.com/list-to-map-supplier)→

## [在Java中将列表转换为字符串](https://www.baeldung.com/java-list-to-string)

了解如何使用不同的技术将列表转换为字符串。

[阅读更多](https://www.baeldung.com/java-list-to-string)→

## [Java 列表和集合之间的转换](https://www.baeldung.com/convert-list-to-set-and-set-to-list)

如何使用纯 Java、Guava 或 Apache Commons 集合在列表和集合之间进行转换。

[阅读更多](https://www.baeldung.com/convert-list-to-set-and-set-to-list)→

## 2. 样本数据结构

首先，我们将对元素建模：

```java
public class Animal {
    private int id;
    private String name;

    //  constructor/getters/setters
}
```

id字段是唯一的，因此我们可以将其作为键。

让我们开始使用传统方式进行转换。

## 3.Java 8之前

显然，我们可以使用核心Java方法将List转换 为Map ：

```java
public Map<Integer, Animal> convertListBeforeJava8(List<Animal> list) {
    Map<Integer, Animal> map = new HashMap<>();
    for (Animal animal : list) {
        map.put(animal.getId(), animal);
    }
    return map;
}
```

现在我们测试转换：

```java
@Test
public void givenAList_whenConvertBeforeJava8_thenReturnMapWithTheSameElements() {
    Map<Integer, Animal> map = convertListService
      .convertListBeforeJava8(list);
    
    assertThat(
      map.values(), 
      containsInAnyOrder(list.toArray()));
}
```

## 4. 使用Java8

从Java8 开始，我们可以使用流和收集器将List转换为Map：

```java
 public Map<Integer, Animal> convertListAfterJava8(List<Animal> list) {
    Map<Integer, Animal> map = list.stream()
      .collect(Collectors.toMap(Animal::getId, Function.identity()));
    return map;
}
```

同样，让我们确保转换正确完成：

```java
@Test
public void givenAList_whenConvertAfterJava8_thenReturnMapWithTheSameElements() {
    Map<Integer, Animal> map = convertListService.convertListAfterJava8(list);
    
    assertThat(
      map.values(), 
      containsInAnyOrder(list.toArray()));
}
```

## 5. 使用 Guava 库

除了核心 Java，我们还可以使用第三方库进行转换。

### 5.1. Maven 配置

首先，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

这个库的最新版本总是可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")找到。

### 5.2. 使用 Maps.uniqueIndex() 进行转换

其次，让我们使用Maps.uniqueIndex()方法将List转换为Map：

```java
public Map<Integer, Animal> convertListWithGuava(List<Animal> list) {
    Map<Integer, Animal> map = Maps
      .uniqueIndex(list, Animal::getId);
    return map;
}
```

最后，我们测试转换：

```java
@Test
public void givenAList_whenConvertWithGuava_thenReturnMapWithTheSameElements() {
    Map<Integer, Animal> map = convertListService
      .convertListWithGuava(list);
    
    assertThat(
      map.values(), 
      containsInAnyOrder(list.toArray()));
}

```

## 6. 使用 Apache Commons 库

我们也可以用Apache Commons库的方法进行转换。

### 6.1. Maven 配置

首先，让我们包含 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

此依赖项的最新版本可[在此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")获得。

### 6.2. 地图工具

其次，我们将使用MapUtils.populateMap()进行转换：

```java
public Map<Integer, Animal> convertListWithApacheCommons(List<Animal> list) {
    Map<Integer, Animal> map = new HashMap<>();
    MapUtils.populateMap(map, list, Animal::getId);
    return map;
}
```

最后，我们可以确保它按预期工作：

```java
@Test
public void givenAList_whenConvertWithApacheCommons_thenReturnMapWithTheSameElements() {
    Map<Integer, Animal> map = convertListService
      .convertListWithApacheCommons(list);
    
    assertThat(
      map.values(), 
      containsInAnyOrder(list.toArray()));
}
```

## 7.价值观冲突

让我们看看如果id字段不唯一会发生什么。

### 7.1. 具有重复ID的动物列表

首先，我们创建一个具有非唯一 id的Animal列表：

```java
@Before
public void init() {

    this.duplicatedIdList = new ArrayList<>();

    Animal cat = new Animal(1, "Cat");
    duplicatedIdList.add(cat);
    Animal dog = new Animal(2, "Dog");
    duplicatedIdList.add(dog);
    Animal pig = new Animal(3, "Pig");
    duplicatedIdList.add(pig);
    Animal cow = new Animal(4, "Cow");
    duplicatedIdList.add(cow);
    Animal goat= new Animal(4, "Goat");
    duplicatedIdList.add(goat);
}
```

如上所示， 奶牛和 山羊具有相同的id。

### 7.2. 检查行为

Java Map的put()方法的实现使得最新添加的值覆盖具有相同键的前一个值。

因此，传统转换和 Apache Commons MapUtils.populateMap()的行为方式相同：

```java
@Test
public void givenADupIdList_whenConvertBeforeJava8_thenReturnMapWithRewrittenElement() {

    Map<Integer, Animal> map = convertListService
      .convertListBeforeJava8(duplicatedIdList);

    assertThat(map.values(), hasSize(4));
    assertThat(map.values(), hasItem(duplicatedIdList.get(4)));
}

@Test
public void givenADupIdList_whenConvertWithApacheCommons_thenReturnMapWithRewrittenElement() {

    Map<Integer, Animal> map = convertListService
      .convertListWithApacheCommons(duplicatedIdList);

    assertThat(map.values(), hasSize(4));
    assertThat(map.values(), hasItem(duplicatedIdList.get(4)));
}
```

我们可以看到山羊用相同的 id覆盖了 牛。

但是，Collectors.toMap() 和 MapUtils.populateMap()分别抛出 IllegalStateException和 IllegalArgumentException：

```java
@Test(expected = IllegalStateException.class)
public void givenADupIdList_whenConvertAfterJava8_thenException() {

    convertListService.convertListAfterJava8(duplicatedIdList);
}

@Test(expected = IllegalArgumentException.class)
public void givenADupIdList_whenConvertWithGuava_thenException() {

    convertListService.convertListWithGuava(duplicatedIdList);
}
```

## 八、总结

在这篇简短的文章中，我们介绍了将List转换为 Map 的各种方法， 给出了核心Java示例以及一些流行的第三方库。