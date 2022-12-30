## 1. 概述

**在本教程中，我们将探索Kotlin中对象比较的不同方式**。

===及其对应物!==是用于引用标识的二元运算符，这意味着当两个引用指向同一个对象时，结果将为true。然而，大多数时候，我们需要检查两个对象是否具有相等的值。

因此，让我们深入研究Kotlin中结构相等的不同方式。

## 2. 使用==运算符的相等性

**==及其相反的!=用于比较两个对象**，如果我们用==比较两个字符串，我们会得到想要的结果：

```kotlin
val a = "Tuyucheng"
val b = "Tuyucheng"
assertTrue(a == b)
```

**然而，这可能并不总是我们想要的**，假设我们有一个像下面这样的类：

```kotlin
class Storage(private val name: String, private val capacity: Int)
```

现在让我们比较这个类的两个实例：

```kotlin
val storage1 = Storage("980Pro M.2 NVMe", 1024)
val storage2 = Storage("980Pro M.2 NVMe", 1024)
assertFalse(storage1 == storage2)
```

**结果是false！但这怎么可能呢？原因是**[字符串池](https://www.baeldung.com/java-string-pool)，当我们声明一个字符串对象时，运行时会检查字符串池中的相同值，如果找到匹配项，它会将字符串文本替换为在池中找到的字符串对象的引用，这就是为什么“Tuyucheng”等于“Tuyucheng”。常量池的概念也适用于数字、字符和布尔值。

但是，在Storage类中，我们必须明确指定storage1和storage2是否相等，否则，将进行身份检查。换句话说，它通过匹配引用来检查是否相等，在这种情况下storage1 == storage2返回false。

## 3. 重写equals()和hashCode()

每次我们使用==运算符时，它都会在后台调用equals()函数。如前所述，会进行身份检查，**我们可以覆盖equals()以提供自定义相等性检查实现**：

```kotlin
class StorageOverriddenEquals(val name: String, val capacity: Int) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is StorageOverriddenEquals) return false
        if (name != other.name || capacity != other.capacity) return false
        return true
    }
}
```

现在equals()的答案对我们来说是真实且有意义的：

```kotlin
val storage1 = StorageOverriddenEquals("980Pro M.2 NVMe", 1024)
val storage2 = StorageOverriddenEquals("980Pro M.2 NVMe", 1024)
assertTrue(storage1 == storage2)
```

**重要的一点是我们必须始终同时覆盖equals()和hashCode()方法**，原因是[哈希码合约](https://www.baeldung.com/java-equals-hashcode-contracts)。**如果我们不遵守该规则，像Sets这样的基于哈希的集合将无法正常工作**。为了解决这个问题，我们可以覆盖hashCode()函数：

```kotlin
class StorageOverriddenEqualsAndHashCode(private val name: String, private val capacity: Int) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is StorageOverriddenEqualsAndHashCode) return false
        other as StorageOverriddenEqualsAndHashCode
        if (name != other.name || capacity != other.capacity) return false
        return true
    }
    override fun hashCode(): Int = name.hashCode() * 31 + capacity
}
```

## 4. 使用数据类

**或者，可以使用**[数据类](../../kotlin-core-lang-oop-1/docs/Kotlin中的数据类.md)**来避免我们需要比较对象的样板代码**：

```kotlin
data class StorageDataClass(private val name: String, private val capacity: Int)

val storage1 = StorageDataClass("980Pro M.2 NVMe", 1024)
val storage2 = StorageDataClass("980Pro M.2 NVMe", 1024)
assertTrue(storage1 == storage2)
```

这个比较的结果为true。

## 5. 总结

在本文中，我们了解了如何覆盖==运算符的默认行为。