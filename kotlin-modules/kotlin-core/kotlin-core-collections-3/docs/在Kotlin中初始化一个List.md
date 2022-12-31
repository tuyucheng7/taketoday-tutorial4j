## 1. 概述

在本教程中，我们将探索在Kotlin中初始化List的各种方式，Kotlin标准库提供了List数据类型的实现，List实现及其实用方法在kotlin.collections包的Collections.kt文件中定义。

## 2. 创建一个空的不可变List

我们可**以使用emptyList方法创建一个空的不可变List**：

```kotlin
@Test
fun emptyList() {
    val emptyList = emptyList<String>()
    assertTrue(emptyList.isEmpty(), "List is empty")
}
```

## 3. 创建一个不可变List

我们可以使用listOfList构建器来创建一个不可变的List，我们需要在List创建期间指定所有元素，**listOf构建器返回List接口的实例，因此是不可变的**。

让我们看看它的实际效果：

```kotlin
val readOnlyList = listOf<String>("John", "Doe")
```

在前面的示例中，我们指定了所有元素，我们还可以使用带有listOf构建器的另一个List来初始化List：

```kotlin
val readOnlyList = listOf<String>("John", "Doe")
//using another list and spread operator
val secondList = listOf<String>(readOnlyList.toTypedArray())
```

我们还可以在过滤掉空值时使用不可变List，为此，我们需要使用listOfNotNull方法。让我们看一个示例：

```kotlin
val filteredList = listOfNotNull("A", "B", null)
```

## 4. 创建可变List

### 4.1 使用mutableListOf

我们可以使用mutableListOf List构建器方法创建可变List，它返回MutableList接口的实例，该接口提供add、remove和其他List操作方法，让我们看看如何在我们的代码中使用这个方法：

```kotlin
@Test
fun readWriteList() {
    var mutableList = mutableListOf<String>()
    mutableList.add("Sydney")
    mutableList.add("Tokyo")
    assert(mutableList.get(0) == "Sydney")
    mutableList = mutableListOf("Paris", "London")
    assert(mutableList.get(0) == "Paris")
}
```

### 4.2 使用arrayListOf

我们可以使用arrayListOf方法创建ArrayList的实例。

ArrayList是MutableList的子项并实现了RandomAccess接口，让我们看看如何使用它：

```kotlin
@Test
fun readWriteList() {
    var arrList = arrayListOf<Int>()
    arrList.add(1)
    arrList.remove(1)
    assert(arrList.size == 0)
    arrList = arrayListOf(1, 2, 3, 4)
    assert(arrList.size == 4)
}
```

## 5. 转换为List

我们还可以将Map等数据类型转换为List，**Map上的toList扩展函数将其元素转换为List**：

```kotlin
@Test
fun fromMaps() {
    val userAddressMap = mapOf(
        "A" to "India",
        "B" to "Australia",
        "C" to null
    )
    val newList : List<Pair<String,String?>> = userAddressMap.toList()
    assert(newList.size == 3)
}
```

## 6. List生成器

我们可以通过指定大小和初始化lambda函数来创建List或MutableList对象：

```kotlin
fun dynamicBuild(){
    val myList = List(10){
        it.toString()
    }
    println(myList) 
    val myMutableList = MutableList(10){
        it.toString()
    }
    myMutableList.add("11")
    println(myMutableList)
}
```

让我们看看上面示例的输出：

```kotlin
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11]
```

### 6.1 使用buildList()

使用buildList方法允许我们定义一个builderAction lambda，我们可以在其中操作一个MutableList，**buildList方法然后返回具有相同元素的只读List的实例**。

让我们看看如何用一个例子来做到这一点：

```kotlin
fun build(){
    val students = listOf<String>("Hertz","Jane")
    val myList = buildList<String>(students.size + 1) {
        add("Jitendra")
        addAll(students)
    }
    println(myList)
}
```

前面的示例产生输出：

```kotlin
[Jitendra, Hertz, Jane]
```

## 7. 总结

在本文中，我们了解了在Kotlin中定义List的不同方式。