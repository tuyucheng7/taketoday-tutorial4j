## 1. 概述

在本文中，我们将研究**Kotlin语言中的泛型类型**。

它们与Java语言中的非常相似，但Kotlin语言的创建者试图通过引入特殊的关键字(如out和in)使它们更加直观和易于理解。

## 2. 创建参数化类

假设我们要创建一个参数化类，我们可以通过使用泛型类型在Kotlin语言中轻松地做到这一点：

```kotlin
class ParameterizedClass<A>(private val value: A) {

    fun getValue(): A {
        return value
    }
}
```

我们可以通过在使用构造函数时显式设置参数化类型来创建此类的实例：

```kotlin
val parameterizedClass = ParameterizedClass<String>("string-value")

val res = parameterizedClass.getValue()

assertTrue(res is String)
```

令人高兴的是，Kotlin可以从参数类型推断泛型类型，因此我们可以在使用构造函数时省略它：

```kotlin
val parameterizedClass = ParameterizedClass("string-value")

val res = parameterizedClass.getValue()

assertTrue(res is String)
```

## 3. Kotlin out和in关键字

### 3.1 out关键字

假设我们想要创建一个生产者类，该类将产生某种类型T的结果。有时；我们想将生成的值分配给类型T的超类型的引用。

为了使用Kotlin实现这一点，**我们需要在泛型类型上使用out关键字，这意味着我们可以将此引用分配给它的任何超类型；out值只能由给定类生成，但不能被使用**：

```kotlin
class ParameterizedProducer<out T>(private val value: T) {
    fun get(): T {
        return value
    }
}
```

我们定义了一个可以产生T类型的值的ParameterizedProducer类。

下一个; 我们可以将ParameterizedProducer类的一个实例分配给作为它的超类型的引用：

```kotlin
val parameterizedProducer = ParameterizedProducer("string")

val ref: ParameterizedProducer<Any> = parameterizedProducer

assertTrue(ref is ParameterizedProducer<Any>)
```

如果ParamaterizedProducer类中的类型T不是out类型，则给定的语句将产生编译器错误。

### 3.2 in关键字

有时，我们有相反的情况，这意味着我们有一个T类型的引用，我们希望能够将它分配给T的子类型。

**如果我们想将泛型类型分配给其子类型的引用，我们可以在泛型类型上使用in关键字。in关键字只能用于使用的参数类型，而不能用于生成的参数类型**：

```kotlin
class ParameterizedConsumer<in T> {
    fun toString(value: T): String {
        return value.toString()
    }
}
```

我们声明toString()方法只会使用T类型的值。

接下来，我们可以将Number类型的引用分配给其子类型-Double的引用：

```kotlin
val parameterizedConsumer = ParameterizedConsumer<Number>()

val ref: ParameterizedConsumer<Double> = parameterizedConsumer

assertTrue(ref is ParameterizedConsumer<Double>)
```

如果ParameterizedCounsumer中的类型T不是in类型，则给定语句将产生编译器错误。

## 4. 类型预测

### 4.1 将子类型数组复制到超类型数组

假设我们有一个某种类型的数组，并且我们想将整个数组复制到Any类型的数组中，这是一个有效的操作，但为了让编译器编译我们的代码，我们需要用out关键字标注输入参数。

这让编译器知道输入参数可以是Any的子类型的任何类型：

```kotlin
fun copy(from: Array<out Any>, to: Array<Any?>) {
    assert(from.size == to.size)
    for (i in from.indices)
        to[i] = from[i]
}
```

如果from参数不是out Any类型，我们将无法将Int类型的数组作为参数传递：

```kotlin
val ints: Array<Int> = arrayOf(1, 2, 3)
val any: Array<Any?> = arrayOfNulls(3)

copy(ints, any)

assertEquals(any[0], 1)
assertEquals(any[1], 2)
assertEquals(any[2], 3)
```

### 4.2 将子类型的元素添加到其超类型的数组中

假设我们有以下情况-我们有一个Any类型的数组，它是Int的超类型，我们想向这个数组添加一个Int元素。我们需要使用in关键字作为目标数组的类型，让编译器知道我们可以将Int值复制到这个数组：

```kotlin
fun fill(dest: Array<in Int>, value: Int) {
    dest[0] = value
}
```

然后，我们可以一个Int类型的值复制到Any的数组中：

```kotlin
val objects: Array<Any?> = arrayOfNulls(1)

fill(objects, 1)

assertEquals(objects[0], 1)
```

### 4.3 星号投影

在某些情况下，我们并不关心特定类型的值。假设我们只想打印一个数组的所有元素，而不管这个数组中元素的类型是什么。

为此，我们可以使用星投影：

```kotlin
fun printArray(array: Array<*>) {
    array.forEach { println(it) }
}
```

然后，我们可以将任何类型的数组传递给printArray()方法：

```kotlin
val array = arrayOf(1, 2, 3)
printArray(array)
```

当使用星投影引用类型时，我们可以从中读取值，但不能写入，因为这会导致编译错误。

## 5. 泛型约束

假设我们要对元素数组进行排序，并且每个元素类型都应该实现一个Comparable接口，我们可以使用泛型约束来指定该要求：

```kotlin
fun <T : Comparable<T>> sort(list: List<T>): List<T> {
    return list.sorted()
}
```

在给定的示例中，我们定义了实现Comparable接口所需的所有元素T。否则，如果我们尝试传递未实现此接口的元素列表，则会导致编译器错误。

我们定义了一个sort函数，该函数将实现Comparable的元素列表作为参数，因此我们可以对其调用sorted()方法。让我们看一下该方法的测试用例：

```kotlin
val listOfInts = listOf(5, 2, 3, 4, 1)

val sorted = sort(listOfInts)

assertEquals(sorted, listOf(1, 2, 3, 4, 5))
```

我们可以很容易地传递一个Int列表，因为Int类型实现了Comparable接口。

### 5.1 多个上限

使用尖括号表示法，我们最多可以声明一个泛型上限，**如果一个类型参数需要多个泛型上限，那么我们应该为该特定类型参数使用单独的where子句**。例如：

```kotlin
fun <T> sort(xs: List<T>) where T : CharSequence, T : Comparable<T> {
    // sort the collection in place
}
```

如上所示，参数T必须同时实现CharSequence和Comparable接口。同样，我们可以声明具有多个泛型上限的类：

```kotlin
class StringCollection<T>(xs: List<T>) where T : CharSequence, T : Comparable<T> {
    // omitted
}
```

## 6. 运行时的泛型

### 6.1 类型擦除

与Java一样，Kotlin的泛型在运行时被擦除。也就是说，**泛型类的实例在运行时不保留其类型参数**。

例如，如果我们创建一个Set<String\>并将一些字符串放入其中，则在运行时我们只能将其视为Set。

让我们创建两个具有两个不同类型参数的集合：

```kotlin
val books: Set<String> = setOf("1984", "Brave new world")
val primes: Set<Int> = setOf(2, 3, 11)
```

在运行时，Set<String\>和Set<Int\>的类型信息将被擦除，我们将它们视为普通Set。因此，即使在运行时完全有可能发现value是一个Set，我们也无法判断它是一组字符串、整数还是其他东西：**该信息已被删除**。

那么，Kotlin的编译器如何阻止我们将非字符串元素添加到Set<String\>中？或者，当我们从Set<String\>中获取一个元素时，它如何知道该元素是一个字符串？

答案很简单，**编译器是负责擦除类型信息的人**，但在此之前，它实际上知道books变量包含String元素。

因此，每次我们从中获取一个元素时，编译器都会将其转换为字符串，或者当我们要向其中添加一个元素时，编译器会对输入进行类型检查。

### 6.2 具体化类型参数

让我们从泛型中获得更多乐趣，并创建一个扩展函数来根据类型过滤集合元素：

```kotlin
fun <T> Iterable<*>.filterIsInstance() = filter { it is T }
Error: Cannot check for instance of erased type: T
```

对于每个集合元素，“it is T”部分检查该元素是否是类型T的实例，但由于类型信息在运行时已被擦除，因此我们不能以这种方式反映类型参数。

或者我们可以吗？

类型擦除规则在一般情况下是正确的，但有一种情况我们可以避免这种限制：内联函数。**内联函数的类型参数可以被具体化，所以我们可以在运行时引用这些类型参数**。

内联函数的主体是内联的，也就是说，编译器将主体直接替换到调用函数的地方，而不是正常的函数调用。

如果我们将前面的函数声明为inline并将类型参数标记为reified，那么我们就可以在运行时访问泛型类型信息：

```kotlin
inline fun <reified T> Iterable<*>.filterIsInstance() = filter { it is T }
```

内联具体化就像一个魅力：

```kotlin
>> val set = setOf("1984", 2, 3, "Brave new world", 11)
>> println(set.filterIsInstance<Int>())
[2, 3, 11]
```

让我们再写一个例子，我们都熟悉那些典型的SLF4j Logger定义：

```kotlin
class User {
    private val log = LoggerFactory.getLogger(User::class.java)

    // ...
}
```

使用具体化的内联函数，我们可以编写更优雅且语法更简单的Logger定义：

```kotlin
inline fun <reified T> logger(): Logger = LoggerFactory.getLogger(T::class.java)
```

然后我们可以写：

```kotlin
class User {
    private val log = logger<User>()

    // ...
}
```

这为我们提供了一个更简洁的选项来实现日志记录，[即Kotlin方式](https://www.baeldung.com/kotlin-logging)。

### 6.3 深入探讨内联具体化

那么，内联函数有什么特别之处以至于类型具体化只适用于它们呢？正如我们所知，Kotlin的编译器将内联函数的字节码复制到函数被调用的地方。

**由于在每个调用站点中，编译器都知道确切的参数类型，因此它可以用实际类型引用替换泛型类型参数**。

例如，当我们写：

```kotlin
class User {
    private val log = logger<User>()

    // ...
}
```

**当编译器内联logger<User\>()函数调用时，它知道实际的泛型类型参数–User**。因此，编译器没有擦除类型信息，而是抓住了具体化的机会并具体化了实际的类型参数。

## 7. 总结

在本文中，我们研究了Kotlin泛型类型，我们看到了如何正确地使用它们out和in关键字，并使用了类型投影并定义了一个使用泛型约束的泛型方法。