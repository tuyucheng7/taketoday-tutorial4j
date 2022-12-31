## 1. 概述

在这个简短的教程中，我们将学习如何在Kotlin中**删除数组中的重复值**。

## 2. toMutableSet扩展函数

让我们从一个基本场景开始，我们有一个包含重复值的字符串数组，我们需要从中删除重复值。

**大多数编程语言都提供Set数据结构，用于将唯一值存储为集合**，Kotlin也不例外，所以让我们继续使用[toMutableSet()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/)函数将数组转换为Set：

```kotlin
val data = arrayOf("pie", "tie", "lie", "pie")
val uniqueData = data.toMutableSet()
assertEquals(3, uniqueData.size)
assertTrue(uniqueData.contains("pie"))
assertTrue(uniqueData.contains("tie"))
assertTrue(uniqueData.contains("lie"))
```

我们可以注意到原始数组有4个值，但生成的uniqueData集合仅包含每个值的唯一值，其大小为3。

## 3. 独特的扩展函数

Kotlin还提供了distinct[扩展函数](https://www.baeldung.com/kotlin/extension-methods)，可用于将数组的不同值作为列表返回：

```kotlin
val data = arrayOf("pie", "tie", "lie", "pie")
assertEquals(4, data.size)
val uniqueData = data.distinct()
assertEquals(3, uniqueData.size)
assertEquals("pie", uniqueData[0])
assertEquals("tie", uniqueData[1])
assertEquals("lie", uniqueData[2])
```

请务必注意，**结果列表中的元素与原始数组中的顺序相同**。在重复值中，只有第一个值可用。

## 4. distinctBy扩展函数

在本节中，我们将学习如何从包含实体对象的数组中删除重复值。

### 4.1 员工数据类

让我们从定义Employee[数据类](https://www.baeldung.com/kotlin/data-classes)开始：

```kotlin
data class Employee(val id: String, val name: String)
```

我们必须注意，两个员工可以有相同的名字，但不能有相同的id。

### 4.2 按标准区分的员工

实体对象可以有多个属性，因此，我们可以有多个条件来检索不同的值。

对于这样的场景，**Kotlin提供了distinctBy扩展函数**，我们可以使用它来指定去除重复值的条件。

假设我们有一组id属性具有重复值的员工：

```kotlin
val emp1 = Employee("Jimmy", "1")
val emp2 = Employee("James", "2")
val emp3 = Employee("Jimmy", "3")
val employees = arrayOf(emp1, emp2, emp1, emp3)
```

我们可以注意到数组中emp3实体对象的重复值。现在，让我们使用distinctBy函数来获取具有不同id的员工：

```kotlin
val uniqueEmployees = employees.distinctBy { it.id }
assertEquals(3, uniqueEmployees.size)
assertEquals("Jimmy", uniqueEmployees.get(0).name)
assertEquals("1", uniqueEmployees.get(0).id)
assertEquals("James", uniqueEmployees.get(1).name)
assertEquals("2", uniqueEmployees.get(1).id)
assertEquals("Jimmy", uniqueEmployees.get(2).name)
assertEquals("3", uniqueEmployees.get(2).id)
```

正如预期的那样，结果列表仅包含3名员工。

接下来，让我们来看一个场景，我们有兴趣获取具有唯一名称的员工列表。

```kotlin
val emp1 = Employee("John", "1")
val emp2 = Employee("John", "2")
val employees = arrayOf(emp1, emp2, emp1)
```

我们可以注意到，不仅原始数组有重复的emp1值，而且emp2实体也与emp1具有相同的名称。

要获得具有唯一名称的员工，让我们使用带有名称属性的distinctBy函数：

```kotlin
val employeesWithUniqueNames = employees.distinctBy { it.name }
assertEquals(1, employeesWithUniqueNames.size)
assertEquals("John", employeesWithUniqueNames.get(0).name)
assertEquals("1", employeesWithUniqueNames.get(0).id)
```

正如预期的那样，生成的员工列表中只有一名员工。

## 5. 总结

在本教程中，我们专注于在Kotlin中编写惯用代码以从数组中删除重复值和实体对象。