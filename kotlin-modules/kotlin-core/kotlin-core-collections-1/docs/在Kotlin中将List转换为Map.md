## 1. 简介

在本快速教程中，我们将了解如何在Kotlin中将List转换为Map。

## 2. 实现

Kotlin提供了方便的[toMap](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/to-map.html)方法，给定一个复杂对象列表，该方法将允许我们将列表中的元素映射到我们提供的任何值：

```kotlin
val user1 = User("John", 18, listOf("Hiking"))
val user2 = User("Sara", 25, listOf("Chess"))
val user3 = User("Dave", 34, listOf("Games"))

@Test
fun givenList_whenConvertToMap_thenResult() {
    val myList = listOf(user1, user2, user3)
    val myMap = myList.map { it.name to it.age }.toMap()

    assertTrue(myMap.get("John") == 18)
}
```

请记住，此处使用“to”关键字来创建姓名和年龄对。此方法应返回一个Map，该Map保留数组中元素的输入顺序：

```kotlin
{John=18, Sara=25, Dave=34}
```

当我们映射一个较小的String数组时，也会发生同样的情况：

```kotlin
@Test
fun givenStringList_whenConvertToMap_thenResult() {
    val myList = listOf("a", "b", "c")
    val myMap = myList.map { it to it }.toMap()

    assertTrue(myMap.get("a") == "a")
}
```

唯一的区别是我们没有为它指定值，因为它只会被那个映射。

然后，将List转换为Map的第二种方法是使用associatedBy方法：

```kotlin
@Test
fun givenList_whenAssociatedBy_thenResult() {
    val myList = listOf(user1, user2, user3)
    val myMap = myList.associateBy({ it.name }, { it.hobbies })
    
    assertTrue(myMap.get("John")!!.contains("Hiking"))
}
```

我们修改了测试，使其使用数组作为值：

```kotlin
{
    John=[Hiking, Swimming], 
    Sara=[Chess, Board Games], 
    Dave=[Games, Racing sports]
}
```

## 3. 使用哪一个？

如果两种方法本质上实现了相同的功能，我们应该使用哪一种？

toMap，在实现方面，更直观。但是，使用此方法需要我们先将Array转换为Pairs，之后必须将其转换为我们的Map，**因此如果我们已经在对Pairs的集合上进行操作，则此操作将特别有用**。

对于其他类型的集合，associate API将是最佳选择。

## 4. 使用associate*方法映射

在我们之前的示例中，我们使用了associateBy方法，但是，Kotlin集合包针对不同的用例有不同的版本。

### 4.1 associate()方法

我们将从使用associate方法开始-它通过对数组元素使用转换函数简单地返回一个Map：

```kotlin
@Test
fun givenStringList_whenAssociate_thenResult() {
    val myList = listOf("a", "b", "c", "d")
    val myMap = myList.associate{ it to it }

    assertTrue(myMap.get("a") == "a")
}
```

### 4.2 associateTo方法

使用这种方法，我们可以将我们的元素收集到一个已经存在的Map中：

```kotlin
@Test
fun givenStringList_whenAssociateTo_thenResult() {
    val myList = listOf("a", "b", "c", "c", "b")
    val myMap = mutableMapOf<String, String>()

    myList.associateTo(myMap) {it to it}

    assertTrue(myMap.get("a") == "a")
}
```

重要的是要记住使用可变Map-这个例子不适用于不可变的。

### 4.3 associateByTo方法

associateByTo为我们提供了三者中最大的灵活性，因为我们可以传递将要填充的Map，即keySelector函数。对于每个指定的键，关联的值将是从中提取键的对象：

```kotlin
@Test
fun givenStringList_whenAssociateByToUser_thenResult() {
    val myList = listOf(user1, user2, user3, user4)
    val myMap = mutableMapOf<String, User>()

    myList.associateByTo(myMap) {it.name}

    assertTrue(myMap.get("Dave")!!.age == 34)
}
```

或者我们可以使用valueTransform函数：

```kotlin
@Test
fun givenStringList_whenAssociateByTo_thenResult() {
    val myList = listOf(user1, user2, user3, user4)
    val myMap = mutableMapOf<String, Int>()

    myList.associateByTo(myMap, {it.name}, {it.age})

    assertTrue(myMap.get("Dave") == 34)
}
```

重要的是要记住，**如果发生键冲突，只会保留最后添加的值**。

## 5. 总结

在本文中，我们探讨了在Kotlin中将List转换为Map的不同方法。