## 1. 简介

在本文中，我们将了解如何创建Kotlin数据类的深拷贝。

对象的深拷贝是对象的“实际”克隆，深拷贝对象不依赖于之前创建的任何对象引用，并且所有内部对象都将在内存中重新分配，这与对象的浅拷贝不同。

考虑阅读我们的文章，[深拷贝和浅拷贝之间的差异](https://www.baeldung.com/cs/deep-vs-shallow-copy)，以更好地理解我们将在这里解决的问题。

## 2. 浅拷贝

首先，让我们定义一些要使用的数据类：

```kotlin
data class Person(var firstName: String, var lastName: String)

data class Movie(var title: String, var year: Int, var director: Person)
```

### 2.1 简单对象

Kotlin数据类的特性之一是编译器自动从主构造函数中声明的所有属性派生copy()函数，调用copy()函数将创建一个新对象，其中包含当前对象中所有值的副本。

让我们copy()一个Person数据类的实例：

```kotlin
@Test
fun givenSimpleObject_whenCopyCalled_thenStructurallyEqualAndDifferentReference() {
    val person = Person("First name", "Last name")

    val personCopy = person.copy()

    assertNotSame(person, personCopy)
    assertEquals(person, personCopy)
}
```

由于Person对象只有一个级别的属性，因此浅拷贝与深拷贝一样有效。

### 2.2 复杂对象

当对象具有多层对象作为属性时，那么浅拷贝会维护子属性的对象引用：

```kotlin
@Test
fun givenComplexObject_whenCopyCalled_thenEqualAndDifferentReferenceAndEqualInternalReferences() {
    val movie = Movie("Avatar 2", 2022, Person("James", "Cameron"))

    val movieCopy = movie.copy()

    assertNotSame(movieCopy, movie)
    assertEquals(movieCopy, movie)
    assertSame(movieCopy.director, movie.director)

    movie.director.lastName = "Brown"
    assertEquals(movieCopy.director.lastName, movie.director.lastName)
}
```

movieCopy的引用与movie不同，两个对象的内容是相等的，就像上面的例子一样。但在这种情况下，movie.director和movieCopy.director是同一个对象，因为**该函数只复制对初始对象的引用**。因此，当我们修改初始对象中的属性时，它会相应地反映在它的所有副本中。

### 2.3 保护我们的对象免受浅拷贝

通过将var更改为val使lastName属性不可变将防止意外修改任何副本的director属性，尽管如此，movie.director和movieCopy.director仍然指的是同一个对象。这同样适用于可变集合，例如使用MutableList而不是不可变的List。

## 3. 深拷贝

为了防止与浅拷贝方法相关的问题，我们需要使用深拷贝。它**以递归方式复制对象树中的所有内容，使其完全独立于之前创建的任何对象**。这里的关键点是我们需要明确指定如何复制每个特定属性，否则，我们最终会得到对象的浅拷贝。

让我们看看我们有哪些选项可以创建Kotlin数据类的深层副本。

### 3.1 将copy()与自定义参数一起使用

正如我们之前提到的，copy()函数自动随Kotlin数据类一起提供，我们无法显式定义它，但在内部，它看起来像：

```kotlin
fun copy(title: String = this.title, year: Int = this.year, director: Person = this.director) =
    Movie(title, year, director)
```

这意味着我们可以在调用copy()时自定义参数，为了解决上面示例中的问题，我们需要强制创建新引用。我们明确指定如何复制director，因为它是唯一通过引用传递的参数：

```kotlin
@Test
fun givenComplexObject_whenCustomCopyCalled_thenEqualAndNewReferenceAndDifferentInternalReferences() {
    val movie = Movie("Avatar 2", 2022, Person("James", "Cameron"))

    val movieCopy = movie.copy(director = movie.director.copy())

    assertNotSame(movieCopy, movie)
    assertEquals(movieCopy, movie)
    assertNotSame(movieCopy.director, movie.director)

    movie.director.lastName = "Brown"
    assertNotEquals(movieCopy.director.lastName, movie.director.lastName)
}
```

结果，movieCopy现在是movie对象的深拷贝。

### 3.2 实现Cloneable接口

由于Kotlin和Java之间的紧密联系，借用这种方法来执行深拷贝似乎是显而易见的。

我们所要做的就是实现Cloneable接口并覆盖clone()方法，同时将其设为public，因为默认情况下它是受保护的。在方法主体中，我们指定如何复制对象的内部：

```kotlin
data class Person(var firstName: String, var lastName: String) : Cloneable {
    public override fun clone(): Person = super.clone() as Person
}
```

与我们使用copy()的第一种方法相比，我们可能会注意到一些相似之处。例如，在Person类中，我们只调用super.clone()，因为它的所有属性都将按值复制。copy()和clone()函数之间的另一个相似之处是它们都默认创建一个浅拷贝，这就是为什么对于Movie对象，我们明确定义了如何复制director属性：

```kotlin
data class Movie(var title: String, var year: Int, var director: Person) : Cloneable {
    public override fun clone() = Movie(title, year, director.clone())
}
```

正如我们可能注意到的，与前面的示例类似，初始对象的修改不会影响其副本：

```kotlin
@Test
fun givenComplexObject_whenCloneCalled_thenEqualAndDifferentReferenceAndDifferentInternalReferences() {
    val movie = Movie("Avatar 2", 2022, Person("James", "Cameron"))
    val movieCopy = movie.clone()

    assertNotSame(movieCopy, movie)
    assertEquals(movieCopy, movie)
    assertNotSame(movieCopy.director, movie.director)

    movie.director.lastName = "Brown"
    assertNotEquals(movieCopy.director.lastName, movie.director.lastName)
}
```

### 3.3 使用JSON转换创建深拷贝

由于所有对象属性和任何子对象属性都需要依赖clone()的良好实现，**另一种保证对象深拷贝的方法是将其写入JSON，然后再读回**。有许多不同的方法可以做到这一点，本文不会介绍这些方法，但有关这可能如何工作的示例，请查看这篇[关于在Kotlin中处理JSON的文章](https://www.baeldung.com/kotlin/json-convert-data-class)，它解释了如何使用[Gson](https://github.com/google/gson)库将Kotlin数据类转换为JSON。

我们应该注意，在Kotlin中处理JSON不仅限于这个特定的库，我们可以使用任何现有的解决方案，例如[KotlinX Serialization](https://kotlinlang.org/docs/serialization.html)、[Jackson](https://github.com/FasterXML/jackson)或[Moshi](https://github.com/square/moshi)等等。

这里的关键点是**反序列化在JSON转换回Kotlin数据类的过程中创建新的对象引用**，这正是我们在创建对象的深拷贝时所需要的。

## 4. 总结

在本教程中，我们研究了如何创建Kotlin数据类的深拷贝。

我们已经看到，使用现有工具的默认实现允许我们创建浅拷贝。要创建Kotlin数据类的深层副本，我们需要提供一个显式实现来处理对象引用。

我们可能会注意到，这两种描述的方法看起来非常相似，使用copy()函数似乎更加轻松和灵活，而使用Cloneable.clone()则需要更多代码。