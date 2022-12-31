## 1. 概述

在面向对象的编程语言中，我们需要不时地对一组对象进行排序，有时，我们可能需要特定的排序逻辑，可以对对象的多个字段进行排序。在本教程中，我们将了解Kotlin对此类功能的内置支持。

## 2. Comparable接口

一种直接的方法是使用**Comparable接口**，我们可以在compareTo函数中定义所有比较逻辑，并将排序留给Iterable.sort()。

例如，让我们定义一个实现Comparable接口的数据类Student ：

```kotlin
data class Student(val name: String, val age: Int, val country: String? = null) : Comparable<Student> {
    override fun compareTo(other: Student): Int {
        return compareValuesBy(this, other, { it.name }, { it.age })
    }
}
```

如我们所见，我们将首先按名称比较两个Student对象，然后按年龄比较。

让我们用Iterable.sort()来验证它，首先，让我们准备一些测试数据：

```kotlin
private val students = listOf(
    Student(name = "C", age = 9),
    Student(name = "A", age = 11, country = "C1"),
    Student(name = "B", age = 10, country = "C2"),
    Student(name = "A", age = 10),
)
```

现在，让我们测试compareTo函数的功能：

```kotlin
val studentsSortedByNameAndAge = listOf(
    Student(name = "A", age = 10),
    Student(name = "A", age = 11, country = "C1"),
    Student(name = "B", age = 10, country = "C2"),
    Student(name = "C", age = 9),
)

assertEquals(
    studentsSortedByNameAndAge,
    students.sorted()
)
```

## 3. 使用Kotlin内置函数进行排序

Kotlin有许多不同的实用程序和工具函数来对集合进行排序，这些实用程序还可以同时处理多个字段。值得注意的是，当我们无法将接口添加到目标类或我们想要各种排序选项时，它们就会发挥作用。

### 3.1 使用sortedWith和compareBy

让我们尝试使用**函数sortedWith和compareBy**：

```kotlin
assertEquals(
    studentsSortedByNameAndAge,
    students.sortedWith(compareBy({ it.name }, { it.age }))
)
```

我们还可以使用对象属性作为选择器：

```kotlin
assertEquals(
    studentsSortedByNameAndAge,
    students.sortedWith(compareBy(Student::name, Student::age))
)
```

### 3.2 在Comparable中使用比较器

由于compareBy函数将返回一个Comparator对象，我们也可以在Comparable.compareTo中使用它：

```kotlin
override fun compareTo(other: Student): Int {
    return compareBy<Student>({ it.name }, { it.age }).compare(this, other)
}
```

### 3.3 按方向排序

如果需要，我们还可以指定排序方向：

```kotlin
assertEquals(
    listOf(
        Student(name = "C", age = 9),
        Student(name = "B", age = 10, country = "C2"),
        Student(name = "A", age = 11, country = "C1"),
        Student(name = "A", age = 10),
    ),
    students.sortedWith(compareByDescending<Student> { it.name }.thenByDescending { it.age })
)
```

### 3.4 使用可空值排序

可空值在排序时会得到特殊处理，默认情况下，空值将放在结果列表的前面：

```kotlin
assertEquals(
    listOf(
        Student(name = "A", age = 10),
        Student(name = "C", age = 9),
        Student(name = "A", age = 11, country = "C1"),
        Student(name = "B", age = 10, country = "C2"),
    ),
    students.sortedWith(compareBy<Student> { it.country }.thenBy { it.name })
)
```

如果我们想将空值放在结果列表的末尾，我们可以使用内置函数nullsLast：

```kotlin
assertEquals(
    listOf(
        Student(name = "A", age = 11, country = "C1"),
        Student(name = "B", age = 10, country = "C2"),
        Student(name = "A", age = 10),
        Student(name = "C", age = 9),
    ),
    students.sortedWith(compareBy<Student, String?>(nullsLast()) { it.country }.thenBy { it.name })
)
```

同样，对于以下场景，也有函数nullsFirst：

```kotlin
assertEquals(
    listOf(
        Student(name = "A", age = 10),
        Student(name = "C", age = 9),
        Student(name = "B", age = 10, country = "C2"),
        Student(name = "A", age = 11, country = "C1"),
    ),
    students.sortedWith(compareBy<Student, String?>(nullsFirst(reverseOrder())) { it.country }.thenBy { it.name })
)
```

### 3.5 使用具有比较功能的自定义比较器

如果我们需要一些特定的比较逻辑，我们可以使用带有**sortedWith**的自定义比较器，例如：

```kotlin
val defaultCountry = "C11"
assertEquals(
    listOf(
        Student(name = "A", age = 11, country = "C1"),
        Student(name = "A", age = 10),
        Student(name = "C", age = 9),
        Student(name = "B", age = 10, country = "C2"),
    ),
    students.sortedWith(
        comparing<Student?, String?>(
            { it.country },
            { c1, c2 -> (c1 ?: defaultCountry).compareTo(c2 ?: defaultCountry) }
        ).thenComparing(
            { it.age },
            { a1, a2 -> (a1 % 10).compareTo(a2 % 10) }
        )
    )
)
```

## 4. 使用sortWith函数对可变集合进行排序

正如我们在前面的示例中看到的，sortedWith将返回一个新的列表对象，因为原始的students集合是不可变的，**如果我们有一个可变集合并想对其进行就地排序，我们可以使用sortWith函数**：

```kotlin
val mutableStudents = students.toMutableList()
mutableStudents.sortWith(compareBy(Student::name, Student::age))
assertEquals(
    studentsSortedByNameAndAge,
    mutableStudents
)
```

当然，我们可以将sortWith与函数compareByDescending、nullsLast、nullsFirst一起使用，并按照我们在第3节中描述的相同方式进行比较。

## 5. 总结

在本文中，我们探讨了如何在Kotlin中对多个字段上的对象集合进行排序。具体来说，我们了解到我们可以使用Comparable接口和Kotlin工具函数sortedWith、sortWith、comparing和compareBy。