## 1. 简介

在本教程中，我们将了解[Kotlin中的属性](https://www.baeldung.com/kotlin-delegated-properties)以及如何访问它们，属性类似于Java中的字段，但存在一些重要差异。

例如，属性具有自动生成的getter和setter，它们也可以在顶级包范围内声明-它们不必属于某个类。

## 2. 属性的getters和setters

**在Kotlin中，属性不需要显式的getter或setter方法**：

```kotlin
var author: String = "Frank Herbert"
```

这与定义以下get()和set()方法相同：

```kotlin
var author: String = "Frank Herbert"
    get() {
        return field
    }
    set(value) {
        field = value
    }
```

默认的getter和setter是我们在Java中看到的一种熟悉的模式，但在Kotlin中，我们不必为属性创建私有支持字段。

我们可以使用点语法来调用类属性的getter和setter：

```kotlin
val book = Book()
print(book.author) // prints "Frank Herbert"
book.author = "Kurt Vonnegut"
print(book.author) // prints "Kurt Vonnegut"
```

现在我们了解了属性的基本功能，让我们看一下修改它们的一些方法。

### 2.1 访问支持字段

**我们定义的每个属性都由一个字段支持，该字段只能使用**[特殊field关键字](https://www.baeldung.com/kotlin/backing-fields)**在其get()和set()方法中访问**。field关键字用于访问或修改属性的值，这允许我们在get()和set()方法中定义自定义逻辑：

```kotlin
var rating: Int = 5
    get() {
        if (field < 5) {
            print("Warning: this is a terrible book")
        }
        return field
    }
    set(value) {
        field = when {
            value > 10 -> 10
            value < 0 -> 0
            else -> value
        }
    }
```

定义自定义getter或setter允许我们执行任意数量的有用操作，例如输入验证、日志记录或数据转换。**通过将此业务逻辑直接添加到getter或setter，我们确保它始终在访问属性时执行**。

尽量避免或尽量减少getter和setter方法中的副作用，它使我们的代码更难理解。

### 2.2 val与var

**如果我们希望能够修改一个属性的值，我们使用**[var关键字](https://www.baeldung.com/kotlin-const-var-and-val-keywords)**来标记它。如果我们想要一个不可变的属性，我们用val关键字标记它**，主要区别是val属性不能有setter。

定义自定义getter的结果是val属性实际上可以更改其值，例如，isWorthReading属性使用可变rating属性来计算其值：

```kotlin
val isWorthReading get() = this.rating > 5
```

从这个意义上讲，该属性在使用自定义getter时充当方法。**如果我们需要执行昂贵或缓慢的操作来计算属性，最好使用一种方法来提供清晰度**。开发人员在使用该属性时可能没有意识到它不仅仅是从内存中检索值。

### 2.3 可见性修饰符

在Java中，我们经常希望类字段具有公共读取权限和私有写入权限，使用公共getter方法和私有或受保护的setter方法可以实现这一点。Kotlin通过在属性的set()方法上允许可见性修饰符提供了一种简洁的方式来实现这种访问模式：

```kotlin
var inventory: Int = 0
    private set
```

现在Book类的任何使用者都可以读取inventory属性，但只有Book类可以修改它。请注意，**属性的默认可见性是public**。

getter将始终具有与属性本身相同的可见性。例如，如果属性是私有的，则getter是私有的。

## 3. Java互操作性

从Kotlin调用Java代码时，如果正确命名了getter和setter，编译器会自动将Java字段转换为属性。**如果一个Java类有一个字段可以通过以“get”开头的无参数方法访问，并通过以“set”开头的单参数方法进行修改，那么它就成为Kotlin中的var属性**。

例如Java的日志库中Logger类的getLevel()和setLevel()方法：

```kotlin
val logger = Logger.getGlobal()
logger.level = Level.SEVERE 
print(logger.level) // prints "SEVERE"
```

**如果该字段只有一个公共getter而没有公共setter，则它成为Kotlin中的val属性**，比如Logger类的name字段：

```kotlin
print(logger.name) // prints "global"
logger.name = "newName" // causes a compiler error
```

## 4. 总结

在本教程中，我们研究了属性的getter和setter，它们通过可选的get()和set()方法提供强大而简洁的功能。我们必须小心，不要在这些方法中执行昂贵的计算，并尽量减少副作用，以保持我们的代码干净和易于理解。