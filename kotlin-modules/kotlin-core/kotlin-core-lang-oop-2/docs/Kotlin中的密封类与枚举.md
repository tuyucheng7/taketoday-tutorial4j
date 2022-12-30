## 1. 简介

在编程中，总是需要区分类型常量，过去，我们通过声明静态变量来做到这一点。这样的方法并没有错，但是有更好的方法来实现这一点。在Kotlin语言中，实现这一目标的方法不止一种。

在本教程中，我们将了解此类用例的两种最常用的类类型：[密封类](https://www.baeldung.com/kotlin/sealed-classes)和[枚举类](https://www.baeldung.com/kotlin/enum)。

## 2. 密封类

密封类提供了一个类的层次结构，这些类具有我们只能在编译时声明的子类。

### 2.1 声明

首先我们创建一个密封类来表示不同的操作系统：

```kotlin
sealed class OsSealed {
    object Linux : OsSealed()
    object Windows : OsSealed()
    object Mac : OsSealed()
}
```

**我们还可以添加一个构造函数参数**来表示拥有操作系统的公司：

```kotlin
sealed class OsSealed(val company: String) {
    object Linux : OsSealed("Open-Source")
    object Windows : OsSealed("Microsoft")
    object Mac : OsSealed("Apple")
}
```

注意：不能从父类文件外部扩展密封类。

### 2.2 用法

**使用密封类的一种众所周知的方法是使用when表达式**：

```kotlin
when (osSealed) {
    OsSealed.Linux -> println("${osSealed.company} - Linux Operating System")
    OsSealed.Mac -> println("${osSealed.company} - Mac Operating System")
    OsSealed.Windows -> println("${osSealed.company} - Windows Operating System")
}
```

### 2.3 函数

在密封类中声明函数非常简单，**如果我们需要在所有子类中使用该函数，我们可以在主父类中声明它们，而如果我们想使用具有不同功能的特定函数，我们可以通过在任何对象或子类下创建一个函数来实现**：

```kotlin
sealed class OsSealed(val releaseYear: Int = 0, val company: String = "") {
    constructor(company: String) : this(0, company)

    object Linux : OsSealed("Open-Source") {
        fun getText(value: Int): String {
            return "Linux by $company - value=$value"
        }
    }

    object Windows : OsSealed("Microsoft") {
        fun getNumber(value: String): Int {
            return value.length
        }
    }

    object Mac : OsSealed(2001, "Apple") {
        fun doSomething(): String {
            val s = "Mac by $company - released at $releaseYear"
            println(s)
            return s
        }
    }

    object Unknown : OsSealed()

    fun getTextParent(): String {
        return "Called from parent sealed class"
    }
}
```

在父类内部声明的函数可以直接调用而无需强制转换。另一方面，在子对象或子类中声明的函数必须通过将它们显式转换为等效类型来调用。

**最好使用is运算符来删除when修饰符中不必要的样板代码，这样做将消除将osSealed参数的值转换为OsSealed.Linux对象或其他定义的对象或子类的需要**：

```kotlin
assertEquals("Called from parent sealed class", osSealed.getTextParent())
when (osSealed) {
    is OsSealed.Linux -> assertEquals("Linux by Open-Source - value=1", osSealed.getText(1))
    is OsSealed.Mac -> assertEquals("Mac by Apple - released at 2001", osSealed.doSomething())
    is OsSealed.Windows -> assertEquals(5, osSealed.getNumber("Text!"))
    else -> assertTrue(osSealed is OsSealed.Unknown)
}
```

## 3. 枚举类

我们使用枚举类将每个枚举常量与其父级相关联。

### 3.1 声明

在这里，我们创建了一个不同操作系统的枚举，正如我们所见，它与Java声明枚举的方式非常相似：

```kotlin
enum class OsEnum {
    Linux,
    Windows,
    Mac
}
```

我们还可以向枚举类添加一个构造函数参数：

```kotlin
enum class OsEnum(val company: String) {
    Linux("Open-Source"),
    Windows("Microsoft"),
    Mac("Apple")
}
```

### 3.2 用法

与密封类类似，**我们可以使用带有when表达式的枚举**，它主要用于处理不同的场景，具体取决于传递的枚举常量：

```kotlin
when (osEnum) {
    OsEnum.Linux -> println("${osEnum.company} - Linux Operating System")
    OsEnum.Mac -> println("${osEnum.company} - Mac Operating System")
    OsEnum.Windows -> println("${osEnum.company} - Windows Operating System")
}
```

### 3.3 函数

我们可以通过多种方式在枚举类中声明一个函数，**我们可以将该函数声明为抽象函数并在每个枚举常量中覆盖它，或者我们可以在父枚举类中声明它并将该函数与任何枚举常量一起使用**：

```kotlin
enum class OsEnum(val releaseYear: Int = 0, val company: String = "") {
    Linux(0, "Open-Source") {
        override fun getText(value: Int): String {
            return "Linux by $company - value=$value"
        }
    },
    Windows(0, "Microsoft") {
        override fun getText(value: Int): String {
            return "Windows by $company - value=$value"
        }
    },
    Mac(2001, "Apple") {
        override fun getText(value: Int): String {
            return "Mac by $company - released at $releaseYear"
        }
    },
    Unknown {
        override fun getText(value: Int): String {
            return ""
        }
    };

    abstract fun getText(value: Int): String

    fun getTextParent(): String {
        return "Called from parent enum class"
    }
}
```

由于我们使用的是抽象函数，因此函数名称不会有任何差异。因此，我们只需要使用传递的osEnum参数来访问实现的抽象函数，以及父声明函数：

```kotlin
assertEquals("Called from parent enum class", osEnum.getTextParent())
when (osEnum) {
    OsEnum.Linux -> assertEquals("Linux by Open-Source - value=1", osEnum.getText(1))
    OsEnum.Windows -> assertEquals("Windows by Microsoft - value=2", osEnum.getText(2))
    OsEnum.Mac -> assertEquals("Mac by Apple - released at 2001", osEnum.getText(3))
    else -> assertTrue(osEnum == OsEnum.Unknown)
}
```

## 4. 密封类与枚举

我们已经详细讨论了每一个，所以现在让我们看看它们的区别。

枚举主要用作相互关联的常量，它们也可以与一些父函数配对。

密封类类似于枚举，但允许更多自定义。如前所述，它们是枚举和抽象类的混合体。

假设我们要添加一个没有已知值或不需要实现的子类。

**在密封类中，我们可以根据需要简单地添加多个自定义构造函数**。此外，我们可以定义多个具有不同名称、参数和返回类型的函数。

**然而，在枚举类中，我们不能在每个枚举常量中定义不同的函数**。因此，即使在我们的Unknown枚举常量中，我们也必须为该常量实现一个我们不需要的方法，并且在初始化Linux和Windows枚举常量时我们还必须传递整数值0。

## 5. 使用案例

枚举类和密封类乍一看似乎很相似，而且，尽管枚举似乎没有必要，因为在枚举类中声明函数有点受限，但它们仍然有它们的用例。

例如，如果我们需要保存一些常量并在几乎没有功能的情况下连接它们怎么办？推荐的选项是使用枚举，因为它简单、直接并且可以完成工作。

另一方面，**如果我们需要有一些相关的常量，每个常量保存不同的数据并实现不同的逻辑，最好的选择是使用密封类**。

## 6. 总结

在本文中，我们研究了密封类和枚举，以及如何使用它们。此外，我们还讨论了初始化和声明枚举、密封类及其函数的多种方式。我们还讨论了何时使用枚举以及何时使用密封类。最后，我们讨论了哪些用例最适合每个类。