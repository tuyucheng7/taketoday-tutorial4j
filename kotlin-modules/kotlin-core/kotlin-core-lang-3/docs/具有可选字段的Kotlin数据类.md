## 1. 概述

在本教程中，我们将展示如何创建带有可选字段的类实例。首先，我们将演示如何使用默认值设置为null的主构造函数来完成此操作；此外，我们将演示如何使用具有默认值的辅助构造函数。

## 2. 使用null初始化值

在Kotlin中，一个[数据类](https://www.baeldung.com/kotlin/data-classes)代表一个数据容器对象，默认情况下，它公开一个[主构造函数](https://www.baeldung.com/kotlin/constructors#primary-constructor)，这需要提供所有字段。让我们创建一个示例：

```kotlin
class DataClassWithMandatoryFields(
    val name: String,
    val surname: String,
    val age: Number
)
```

现在，实例创建如下所示：

```kotlin
val objectWithAllValuesProvided = DataClassWithMandatoryFields("John", "Deere", 82)
```

**默认情况下，在数据类中，所有字段在主构造函数中都是必需的**，如果我们没有为任何字段提供值，编译器会抛出异常：“No value passed for parameter ‘<parameter name\>'”。

**让我们编写一个新的数据类，其中的字段不是必需的**，我们将修改构造函数并使用null初始化所有值：

```kotlin
class DataClassWithNullInitializedFields(
    val name: String? = null,
    val surname: String? = null,
    val age: Number? = null
)
```

现在，我们可以创建一个没有提供值的实例；此外，我们可以提供选定的值：

```kotlin
val objectWithNullInitializedFields = DataClassWithNullInitializedFields()
val objectWithNameInitializedFields = DataClassWithNullInitializedFields(name = "John")
```

但是，**这种方法意味着我们必须在访问字段的属性之前检查是否为空**：

```kotlin
assertThat(objectWithNameInitializedFields.name?.length).isEqualTo("4")
```

## 3. 在主构造函数中设置默认值

之后，让我们改进以前的版本并消除可空性；对于所有构造函数参数，我们设置一个默认值：

```kotlin
class DataClassWithDefaultValues(
    val name: String = "",
    val surname: String = "",
    val age: Number = Int.MIN_VALUE
)
```

**由于这一点，我们避免了使用**[“安全调用”来处理空值](https://www.baeldung.com/kotlin/null-safety#safe-calls)**的需要**；同样，我们可以创建一个对象，其中包含前面示例中提供的一些字段：

```kotlin
val dataClassWithDefaultValues = DataClassWithDefaultValues()
val dataClassWithNameProvided = DataClassWithDefaultValues(name = "John")
```

现在，我们不必检查对象字段的可空性：

```kotlin
assertThat(dataClassWithNameProvided.name.length).isEqualTo(4)
```

## 4. 具有默认值的辅助构造函数

现在，让我们使用默认值修改方法，我们创建[辅助构造函数](https://www.baeldung.com/kotlin/constructors#secondary-constructor)，而不是主构造函数中的默认值：

```kotlin
class DataClassWithSecondaryConstructors(
    val name: String,
    val surname: String,
    val age: Number
) {
    constructor() : this("", "Doe", Int.MIN_VALUE)
    constructor(name: String) : this(name, "Deere", Int.MIN_VALUE)
    constructor(name: String, surname: String) : this(name, surname, Int.MIN_VALUE)
}
```

对象的构造看起来与前面的示例相同：

```kotlin
val dataClassWithSecondaryConstructors = DataClassWithSecondaryConstructors()
val objectWithNameSet = DataClassWithSecondaryConstructors(name = "John" )
```

**因此，我们可以根据构造函数设置不同的默认值**，在我们的示例中，当未提供任何值时，我们将surname值设置为“Doe”。如果仅提供name，我们将surname设置为“Deere”：

```kotlin
assertThat(objectWithNameSet.surname).isEqualTo("Deere")
```

## 5. 总结

在这篇简短的文章中，我们演示了如何创建带有可选字段的类实例。首先，我们演示了如何使用主构造函数来实现。之后，我们演示了如何使用具有默认值的辅助构造函数。