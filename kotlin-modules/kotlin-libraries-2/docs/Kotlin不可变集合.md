## 一、简介

在本教程中，我们将研究如何在 Kotlin 中创建不可变集合。

首先，我们将探讨不变性的类型以及 Kotlin 提供的标准。然后，我们将研究如何利用 Google 的 Guava 库来创建真正不可变的集合。

作为替代方案，我们还将查看Kotlin 的 Kotlinx 不可变集合库。

## 2.依赖关系

在创建不可变集合之前，我们需要导入 Guava 和不可变集合库。

### 2.1. 行家

```xml
<!-- https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-collections-immutable -->
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-collections-immutable</artifactId>
    <version>0.1</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>27.1-jre</version>
</dependency>
<repository>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <id>kotlinx</id>
    <name>bintray</name>
    <url>https://dl.bintray.com/kotlin/kotlinx</url>
</repository>
```

### 2.2. 摇篮

```groovy
repositories {
    maven {
        url "https://dl.bintray.com/kotlin/kotlinx"
    }
}

// https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-collections-immutable
compile group: 'org.jetbrains.kotlinx', name: 'kotlinx-collections-immutable', version: '0.1'
// https://mvnrepository.com/artifact/com.google.guava/guava
compile group: 'com.google.guava', name: 'guava', version: '27.1-jre'
```

## 3.不变性的类型

在我们开始之前，让我们看一下集合可以拥有的不同类型的不变性：

1.  可变的——列表的内容可以自由改变
2.  只读- 集合的内容不可更改。但是，可以更改基础数据
3.  不可变的——没有什么可以改变集合的内容

不可变集合在编程中有很多用途。例如，我们可以在不同线程之间自由共享它们，而没有遇到竞争条件的风险。此外，不可变集合实现总是比它们的可变替代方案更节省内存。

此外，不可变集合的使用是一种很好的防御性编程技术——确保我们不会对数据进行不必要的更改。

## 4. 科特林集合

在 Kotlin 中，所有非可变集合(例如 List)在默认情况下都是编译时只读的，并且不是不可变的。虽然定义的接口不支持更改集合中数据的方法，但底层数据仍然可以更改。

让我们通过尝试更改我们的只读List来证明这一点：

```java
@Test
fun givenReadOnlyList_whenCastToMutableList_checkNewElementsAdded(){

    val list: List<String> = listOf("This", "Is", "Totally", "Immutable")

    (list as MutableList<String>)[2] = "Not"

    assertEquals(listOf("This", "Is", "Not", "Immutable"), list)

}
```

在我们上面的示例中，我们创建了一个新的 List 并将其分配给list 变量。默认情况下，Kotlin 的 [List接口](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)是只读的，不允许我们向列表中添加新元素。但是，通过将 List 转换为 MutableList，我们可以通过其add方法自由添加新元素。

## 5.番石榴

对于绝对不变性，我们可以利用[Guava 的一组不可变集合](https://github.com/google/guava/wiki/ImmutableCollectionsExplained)。Guava 提供了许多 Java 集合的不可变版本，包括 [ImmutableList、ImmutableSet 和 ](https://github.com/google/guava/wiki/ImmutableCollectionsExplained#Details)[ImmutableMap](https://github.com/google/guava/wiki/ImmutableCollectionsExplained#Details)。

让我们通过使用ImmutableList来看看 ImmutableList 的实际应用。方法：

```java
@Rule
@JvmField
var ee : ExpectedException = ExpectedException.none()

@Test 
fun givenImmutableList_whenAddTried_checkExceptionThrown() { 

    val list: List<String> = ImmutableList.of("I", "am", "actually", "immutable") 
    ee.expect(UnsupportedOperationException::class.java) 
    (list as MutableList<String>).add("Oops") 

}
```

在我们的示例中，我们可以看到即使转换为 MutableList，我们的列表也不会发生变化。不接受新元素，而是在运行时抛出UnsupportedOperationException 。

Guava 还提供了实例化不可变列表的替代方法。

### 5.1. 备份

接下来，让我们看看如何 从先前创建的可变列表实例化一个ImmutableList 。为此，我们可以使用 copyOf方法，该方法将另一个集合作为其参数：

```java
@Rule 
@JvmField 
var ee : ExpectedException = ExpectedException.none()

@Test
fun givenMutableList_whenCopiedAndAddTried_checkExceptionThrown(){

    val mutableList : List<String> = listOf("I", "Am", "Definitely", "Immutable")

    (mutableList as MutableList<String>)[2] = "100% Not"

    assertEquals(listOf("I", "Am", "100% Not", "Immutable"), mutableList)

    val list: List<String> = ImmutableList.copyOf(mutableList)

    ee.expect(UnsupportedOperationException::class.java)

    (list as MutableList<String>)[2] = "Really?"

}
```

在这里，我们使用listOf方法创建了一个完全可变的列表。然后我们使用copyOf 方法创建一个不可变的。最后，我们看到当一个元素被添加到我们的 ImmutableList时，我们的代码抛出了一个异常。

### 5.2. 一个得心应手的建设者

最后，让我们看一下 Guava 提供的用于构建不可变集合的有用构建器。在这个例子中，我们将创建一个ImmutableSet。 [ImmutableSet.B ](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/collect/ImmutableSet.Builder.html)[uilder](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/collect/ImmutableSet.Builder.html)提供了用于添加新的单个元素以及其他集合的副本的所有基本方法： 

```java
@Rule 
@JvmField 
var ee : ExpectedException = ExpectedException.none()

@Test
fun givenImmutableSetBuilder_whenAddTried_checkExceptionThrown(){

    val mutableList : List<String> = ArrayList(listOf("Hello", "Baeldung"))
    val set: ImmutableSet<String> = ImmutableSet.builder<String>()
      .add("I","am","immutable") 
      .addAll(mutableList)
      .build() 

    assertEquals(setOf("Hello", "Baeldung", "I", "am", "immutable"), set)

    ee.expect(UnsupportedOperationException::class.java) 

    (set as MutableSet<String>).add("Oops") 

}
```

在我们上面的例子中，我们使用 Guava 的 ImmutableSet.Builder 类构造了 一个字符串 的ImmutableSet 。首先，在使用构建器的addAll方法添加mutableList的内容之前，我们使用add方法添加一些单个String。

验证内容后，我们可以看到，正如预期的那样，尝试添加更多元素会导致抛出异常。

## 6. Kotlinx 不可变集合库

JetBrain 对 Kotlin 集合的只读性质的回答是[Kotlinx 不可变集合库](https://github.com/Kotlin/kotlinx.collections.immutable)——多么啰嗦，让我们简称为 KICL。KICL为 Kotlin 提供了不可变的集合接口和实现原型。

当我们的应用程序只需要一点不变性时，KICL 的重量仅为 Guava 2.6MB 的一小部分，它提供了 Guava 的轻量级替代方案。

让我们快速浏览一下这个库的运行情况：

```java
@Rule
@JvmField
var ee : ExpectedException = ExpectedException.none()

@Test
fun givenKICLList_whenAddTried_checkExceptionThrown(){

    val list: ImmutableList<String> = immutableListOf("I", "am", "immutable")

    list.add("My new item")

    assertEquals(listOf("I", "am", "immutable"), list)
    
}
```

Kotlinx 的不可变集合库的工作方式与 Guava 的集合略有不同。正如我们在上面看到的，没有抛出UnsupportedOperationException，只是没有向 ImmutableList 添加新元素。

## 七、总结

在本文中，我们了解了 Kotlin 在不可变集合方面提供的内容。

然后，我们深入研究了 Google 的 Guava 可以为我们提供的不可变集合，以及 Kotlinx 不可变集合库。