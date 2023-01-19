## 一、简介

[Groovy](https://www.baeldung.com/groovy-language)提供了大量增强 Java 核心功能的方法。

在本教程中，我们将展示 Groovy 如何在检查元素并在[多种类型的集合](https://www.baeldung.com/groovy-lists)中找到它时执行此操作。

## 2. 测试元素是否存在

首先，我们将专注于测试给定集合是否包含元素。

### 2.1. 列表

Java 本身提供了几种使用java.util.List检查列表中项目的方法：

-   包含方法_ 
-   索引 方法_ 

由于 Groovy 是一种与 Java 兼容的语言，我们可以安全地使用它们。

让我们看一个例子：

```groovy
@Test
void whenListContainsElement_thenCheckReturnsTrue() {
    def list = ['a', 'b', 'c']

    assertTrue(list.indexOf('a') > -1)
    assertTrue(list.contains('a'))
}
```

除此之外，Groovy 还引入了成员运算符：

```groovy
element in list
```

它是 Groovy 提供的众多语法糖运算符之一。在它的帮助下，我们可以简化代码：

```groovy
@Test
void whenListContainsElement_thenCheckWithMembershipOperatorReturnsTrue() {
    def list = ['a', 'b', 'c']

    assertTrue('a' in list)
}
```

### 2.2. 放

与前面的示例一样，我们可以使用java.util.Set#contains方法和in运算符：

```groovy
@Test
void whenSetContainsElement_thenCheckReturnsTrue() {
    def set = ['a', 'b', 'c'] as Set

    assertTrue(set.contains('a'))
    assertTrue('a' in set)
}
```

### 2.3. 地图

对于Map，我们可以直接检查键或值：

```groovy
@Test
void whenMapContainsKeyElement_thenCheckReturnsTrue() {
    def map = [a: 'd', b: 'e', c: 'f']

    assertTrue(map.containsKey('a'))
    assertFalse(map.containsKey('e'))
    assertTrue(map.containsValue('e'))
}
```

或者使用成员运算符来查找匹配的键：

```groovy
@Test
void whenMapContainsKeyElement_thenCheckByMembershipReturnsTrue() {
    def map = [a: 'd', b: 'e', c: 'f']

    assertTrue('a' in map)
    assertFalse('f' in map)
}
```

当与地图一起使用时，我们应该小心使用成员资格运算符，因为这个运算符与布尔值一起使用有点混乱。底层机制不是测试键的存在，而是从映射中检索相应的值并将其转换为布尔值：

```groovy
@Test
void whenMapContainsFalseBooleanValues_thenCheckReturnsFalse() {
    def map = [a: true, b: false, c: null]

    assertTrue(map.containsKey('b'))
    assertTrue('a' in map)
    assertFalse('b' in map)
    assertFalse('c' in map)
}
```

正如我们在上面的例子中看到的，出于同样的原因，使用空值也有点危险。Groovy 将false和null都转换为布尔值false。

## 3.所有比赛和任何比赛

在大多数情况下，我们处理由更复杂的对象组成的集合。在本节中，我们将展示如何检查给定的集合是否包含至少一个匹配元素，或者是否所有元素都匹配给定的谓词。

让我们从定义一个我们将在整个示例中使用的简单类开始：

```groovy
class Person {
    private String firstname
    private String lastname
    private Integer age

    // constructor, getters and setters
}
```

### 3.1. 列表/集合

这一次，我们将使用一个简单的 Person对象列表：

```groovy
private final personList = [
  new Person("Regina", "Fitzpatrick", 25),
  new Person("Abagail", "Ballard", 26),
  new Person("Lucian", "Walter", 30),
]
```

正如我们之前提到的，Groovy 是一种 Java 兼容语言，所以让我们首先使用Java 8 引入的[Stream API](https://www.baeldung.com/java-8-streams-introduction)创建一个示例：

```groovy
@Test
void givenListOfPerson_whenUsingStreamMatching_thenShouldEvaluateList() {
    assertTrue(personList.stream().anyMatch {it.age > 20})
    assertFalse(personList.stream().allMatch {it.age < 30})
}
```

我们还可以使用 Groovy 方法DefaultGroovyMethods#any 和 DefaultGroovyMethods#every 直接对集合执行检查：

```groovy
@Test
void givenListOfPerson_whenUsingCollectionMatching_thenShouldEvaluateList() {
    assertTrue(personList.any {it.age > 20})
    assertFalse(personList.every {it.age < 30})
}
```

### 3.2. 地图

让我们从定义由Person#firstname映射的 Person 对象 的Map开始：

```groovy
private final personMap = [
  Regina : new Person("Regina", "Fitzpatrick", 25),
  Abagail: new Person("Abagail", "Ballard", 26),
  Lucian : new Person("Lucian", "Walter", 30)
]
```

我们可以通过它的键、值或整个条目来评估它。同样，让我们首先使用Stream API：

```groovy
@Test
void givenMapOfPerson_whenUsingStreamMatching_thenShouldEvaluateMap() {
    assertTrue(personMap.keySet().stream().anyMatch {it == "Regina"})
    assertFalse(personMap.keySet().stream().allMatch {it == "Albert"})
    assertFalse(personMap.values().stream().allMatch {it.age < 30})
    assertTrue(personMap.entrySet().stream().anyMatch
      {it.key == "Abagail" && it.value.lastname == "Ballard"})
}
```

然后，Groovy Collection API：

```groovy
@Test
void givenMapOfPerson_whenUsingCollectionMatching_thenShouldEvaluateMap() {
    assertTrue(personMap.keySet().any {it == "Regina"})
    assertFalse(personMap.keySet().every {it == "Albert"})
    assertFalse(personMap.values().every {it.age < 30})
    assertTrue(personMap.any {firstname, person -> firstname == "Abagail" && person.lastname == "Ballard"})
}
```

如我们所见，Groovy 不仅在操作地图时充分替代了Stream API，而且还允许我们直接在Map对象上执行检查，而不是使用java.util.Map#entrySet方法。

## 4. 在集合中找到一个或多个元素

### 4.1. 列表/集合

我们还可以使用谓词提取元素。让我们从熟悉的Stream API 方法开始：

```groovy
@Test
void givenListOfPerson_whenUsingStreamFind_thenShouldReturnMatchingElements() {
    assertTrue(personList.stream().filter {it.age > 20}.findAny().isPresent())
    assertFalse(personList.stream().filter {it.age > 30}.findAny().isPresent())
    assertTrue(personList.stream().filter {it.age > 20}.findAll().size() == 3)
    assertTrue(personList.stream().filter {it.age > 30}.findAll().isEmpty())
}
```

如我们所见，上面的示例使用java.util.Optional来查找单个元素，因为Stream API 强制采用该方法。

另一方面，Groovy 提供了更紧凑的语法：

```groovy
@Test
void givenListOfPerson_whenUsingCollectionFind_thenShouldReturnMatchingElements() {
    assertNotNull(personList.find {it.age > 20})
    assertNull(personList.find {it.age > 30})
    assertTrue(personList.findAll {it.age > 20}.size() == 3)
    assertTrue(personList.findAll {it.age > 30}.isEmpty())
}
```

通过使用 Groovy 的 API，我们可以跳过创建Stream 和过滤它的过程。

### 4.2. 地图

在地图的情况下， 有几个选项可供选择。我们可以在键、值或完整条目中找到元素。由于前两个基本上是List或Set，在本节中我们将仅展示查找条目的示例。

让我们重用之前的personMap：

```groovy
@Test
void givenMapOfPerson_whenUsingStreamFind_thenShouldReturnElements() {
    assertTrue(
      personMap.entrySet().stream()
        .filter {it.key == "Abagail" && it.value.lastname == "Ballard"}
        .findAny().isPresent())
    assertTrue(
      personMap.entrySet().stream()
        .filter {it.value.age > 20}
        .findAll().size() == 3)
}
```

同样，简化的 Groovy 解决方案：

```groovy
@Test
void givenMapOfPerson_whenUsingCollectionFind_thenShouldReturnElements() {
    assertNotNull(personMap.find {it.key == "Abagail" && it.value.lastname == "Ballard"})
    assertTrue(personMap.findAll {it.value.age > 20}.size() == 3)
}
```

在这种情况下，好处就更加显着了。我们跳过java.util.Map#entrySet 方法并使用闭包和Map上提供的函数。

## 5.总结

在本文中，我们介绍了Groovy 如何简化元素检查并在多种类型的集合中查找它们。