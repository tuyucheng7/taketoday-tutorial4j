## 1. 概述

在本教程中，我们将讨论Kotlin提供的用于支持运算符重载的约定。

## 2. operator关键字

在Java中，运算符与特定的Java类型相关联。例如，Java中的字符串和数字类型可以分别使用+运算符进行拼接和加法，没有其他Java类型可以为了自己的利益而重用此运算符。相反，Kotlin提供了一套约定来支持有限的运算符重载。

让我们从一个简单的数据类开始：

```kotlin
data class Point(val x: Int, val y: Int)
```

我们将使用一些运算符来增强这个数据类。

为了将具有预定义名称的Kotlin函数转换为运算符，**我们应该使用operator修饰符标记该函数**。例如，我们可以重载“+”运算符：

```kotlin
operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
```

这样我们就可以用“+”相加两个Point：

```kotlin
>> val p1 = Point(0, 1)
>> val p2 = Point(1, 2)
>> println(p1 + p2)
Point(x=1, y=3)
```

## 3. 一元运算的重载

**一元运算是那些只对一个操作数起作用的运算**。例如，-a、a++或!a是一元运算。通常，要重载一元运算符的函数不带任何参数。

### 3.1 一元加

如何用几个Point构建某种Shape：

```kotlin
val s = shape {
    +Point(0, 0)
    +Point(1, 1)
    +Point(2, 2)
    +Point(3, 4)
}
```

在Kotlin中，使用unaryPlus运算符函数完全可以做到这一点。

由于Shape只是Points的集合，那么我们可以编写一个类，包装一些Points并能够添加更多Point：

```kotlin
class Shape {
    private val points = mutableListOf<Point>()

    operator fun Point.unaryPlus() {
        points.add(this)
    }
}
```

**请注意，给我们shape{...}语法的是将Lambda与Receivers一起使用**：

```kotlin
fun shape(init: Shape.() -> Unit): Shape {
    val shape = Shape()
    shape.init()

    return shape
}
```

### 3.2 一元减

假设我们有一个名为“p”的Point，我们将使用类似“-p”的东西来反转它的坐标(符号)。然后，我们所要做的就是在Point上定义一个名为unaryMinus的运算符函数：

```kotlin
operator fun Point.unaryMinus() = Point(-x, -y)
```

然后，每次我们在Point的实例前添加“-”前缀时，编译器都会将其转换为unaryMinus函数调用：

```kotlin
>> val p = Point(4, 2)
>> println(-p)
Point(x=-4, y=-2)
```

### 3.3 自增

我们可以通过实现一个名为inc的运算符函数来将每个坐标递增1：

```kotlin
operator fun Point.inc() = Point(x + 1, y + 1)
```

后缀“++”运算符，首先返回当前值，然后将值增加1：

```kotlin
>> var p = Point(4, 2)
>> println(p++)
>> println(p)
Point(x=4, y=2)
Point(x=5, y=3)
```

相反，前缀“++”运算符首先增加值，然后返回新增加的值：

```kotlin
>> println(++p)
Point(x=6, y=4)
```

此外，**由于“++”运算符重新分配了应用的变量，因此我们不能对它们使用val**。

### 3.4 递减

与递增非常相似，我们可以通过实现dec运算符函数来递减每个坐标：

```kotlin
operator fun Point.dec() = Point(x - 1, y - 1)
```

dec还支持与常规数字类型一样的前置和后置递减运算符的熟悉语义：

```kotlin
>> var p = Point(4, 2)
>> println(p--)
>> println(p)
>> println(--p)
Point(x=4, y=2)
Point(x=3, y=1)
Point(x=2, y=0)
```

另外，和++一样，我们不能将-与vals一起使用。

### 3.5 否定

仅通过!p翻转坐标怎么样？**我们可以用not做到这一点**：

```kotlin
operator fun Point.not() = Point(y, x)
```

简单地说，编译器将任何“!p”转换为对“not”一元运算符函数的函数调用：

```kotlin
>> val p = Point(4, 2)
>> println(!p)
Point(x=2, y=4)
```

## 4. 二元运算的重载

**顾名思义，二元运算符是对两个操作数起作用的运算符**。因此，重载二元运算符的函数应该至少接收一个参数。

让我们从算术运算符开始。

### 4.1 加算术运算符

正如我们之前看到的，我们可以在Kotlin中重载基本的数学运算符，我们可以使用“+”将两个点相加：

```kotlin
operator fun Point.plus(other: Point): Point = Point(x + other.x, y + other.y)
```

然后我们可以写：

```kotlin
>> val p1 = Point(1, 2)
>> val p2 = Point(2, 3)
>> println(p1 + p2)
Point(x=3, y=5)
```

由于plus是一个二元运算符函数，我们应该为该函数声明一个参数。

现在，我们大多数人都经历过将两个BigInteger相加的不雅之处：

```java
BigInteger zero = BigInteger.ZERO;
BigInteger one = BigInteger.ONE;
one = one.add(zero);
```

[事实证明](https://github.com/JetBrains/kotlin/blob/ba6da7c40a6cc502508faf6e04fa105b96bc7777/libraries/stdlib/jvm/src/kotlin/util/BigIntegers.kt#L19)，有一种更好的方法可以在Kotlin中相加两个BigIntegers：

```kotlin
>> val one = BigInteger.ONE
println(one + one)
```

这是有效的，**因为Kotlin标准库本身在内置类型(如BigInteger)上添加了相当一部分扩展运算符**。

### 4.2 其他算术运算符

与加法类似，**减法、乘法、除法和余数，它们的工作方式相同**：

```kotlin
operator fun Point.minus(other: Point): Point = Point(x - other.x, y - other.y)
operator fun Point.times(other: Point): Point = Point(x * other.x, y * other.y)
operator fun Point.div(other: Point): Point = Point(x / other.x, y / other.y)
operator fun Point.rem(other: Point): Point = Point(x % other.x, y % other.y)
```

然后，Kotlin编译器将对“-”、“*”、“/”或“%”的任何调用分别转换为“minus”、“times”、“div”或“rem”：

```kotlin
>> val p1 = Point(2, 4)
>> val p2 = Point(1, 4)
>> println(p1 - p2)
>> println(p1 * p2)
>> println(p1 / p2)
Point(x=1, y=0)
Point(x=2, y=16)
Point(x=2, y=1)
```

**或者，如何通过数字因子缩放Point**：

```kotlin
operator fun Point.times(factor: Int): Point = Point(x * factor, y * factor)
```

这样我们就可以写出类似“p1 * 2”的东西：

```kotlin
>> val p1 = Point(1, 2)
>> println(p1 * 2)
Point(x=2, y=4)
```

从前面的例子中我们可以看出，两个操作数没有义务必须是同一类型，**返回类型也是如此**。

### 4.3 交换性

重载运算符并不总是可交换的，也就是说，我们不能交换操作数并期望事情尽可能顺利地进行。

例如，我们可以通过将Point乘以Int来按整数因子缩放Point，比如 “p1 * 2”，但反之则不行。

好消息是，我们可以在Kotlin或Java内置类型上定义运算符函数。为了使“2 * p1”起作用，我们可以在Int上定义一个运算符：

```kotlin
operator fun Int.times(point: Point): Point = Point(point.x * this, point.y * this)
```

现在我们也可以愉快地使用“2 * p1”了：

```kotlin
>> val p1 = Point(1, 2)
>> println(2 * p1)
Point(x=2, y=4)
```

### 4.4 复合赋值

现在我们可以使用“+”运算符相加两个BigIntegers，我们可以使用“+”的复合赋值，即“+=”。让我们试试这个想法：

```kotlin
var one = BigInteger.ONE
one += one
```

默认情况下，当我们实现其中一个算术运算符时，比如“plus”，Kotlin不仅支持熟悉的“+”运算符，**它还为相应的复合赋值做同样的事情，即“+=”**。

这意味着，无需更多工作，我们还可以执行以下操作：

```kotlin
var point = Point(0, 0)
point += Point(2, 2)
point -= Point(1, 1)
point *= Point(2, 2)
point /= Point(1, 1)
point /= Point(2, 2)
point *= 2
```

但有时这种默认行为并不是我们想要的，假设我们要使用“+=”向MutableCollection添加一个元素。 

对于这些场景，我们可以通过实现一个名为plusAssign的运算符函数来明确说明：

```kotlin
operator fun <T> MutableCollection<T>.plusAssign(element: T) {
    add(element)
}
```

**对于每个算术运算符，都有一个相应的复合赋值运算符，它们都带有“Assign”后缀**。即有plusAssign、minusAssign、timesAssign、divAssign、remAssign：

```kotlin
>> val colors = mutableListOf("red", "blue")
>> colors += "green"
>> println(colors)
[red, blue, green]
```

所有复合赋值运算符函数都必须返回Unit。

### 4.5 相等约定

**如果我们覆盖equals方法，那么我们也可以使用“==”和“!=”运算符**：

```kotlin
class Money(val amount: BigDecimal, val currency: Currency) : Comparable<Money> {

    // omitted

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Money) return false

        if (amount != other.amount) return false
        if (currency != other.currency) return false

        return true
    }

    // An equals compatible hashcode implementation
}
```

Kotlin将对“==”和“!=”运算符的任何调用转换为equals函数调用，显然为了使“!=”起作用，函数调用的结果被反转。请注意，**在这种情况下，我们不需要operator关键字**。

### 4.6 比较运算符

是时候再次抨击BigInteger了！

假设如果一个BigInteger大于另一个，我们将有条件地运行一些逻辑。在Java中，解决方案并不是那么干净：

```java
if (BigInteger.ONE.compareTo(BigInteger.ZERO) > 0 ) {
    // some logic
}
```

当在Kotlin中使用完全相同的BigInteger时，我们可以神奇地写出这样：

```kotlin
if (BigInteger.ONE > BigInteger.ZERO) {
    // the same logic
}
```

这种魔法是可能的，**因为Kotlin对Java的Comparable进行了特殊的处理**。

简单地说，我们可以通过一些Kotlin约定调用Comparable接口中的compareTo方法，事实上，任何由“<”、“<=”、“>”或“>=”进行的比较都会被转换为compareTo函数调用。

为了在Kotlin类型上使用比较运算符，我们需要实现它的Comparable接口：

```kotlin
class Money(val amount: BigDecimal, val currency: Currency) : Comparable<Money> {

    override fun compareTo(other: Money): Int =
        convert(Currency.DOLLARS).compareTo(other.convert(Currency.DOLLARS))

    fun convert(currency: Currency): BigDecimal = // omitted
}
```

然后我们可以比较简单的货币值：

```kotlin
val oneDollar = Money(BigDecimal.ONE, Currency.DOLLARS)
val tenDollars = Money(BigDecimal.TEN, Currency.DOLLARS)
if (oneDollar < tenDollars) {
    // omitted
}
```

由于Comparable接口中的compareTo函数已经标上了operator修饰符，因此我们不需要自己添加它。

### 4.7 in约定

为了检查一个元素是否属于Page，我们可以使用“in”约定：

```kotlin
operator fun <T> Page<T>.contains(element: T): Boolean = element in elements()
```

同样，**编译器会将“in”和“!in”约定转换为对contains运算符函数的函数调用**：

```kotlin
>> val page = firstPageOfSomething()
>> "This" in page
>> "That" !in page
```

**“in”左侧的对象将作为参数传递给contains，contains函数将在右侧操作数上调用**。

### 4.8 获取索引器

**索引器允许对某个类型的实例进行索引，就像数组或集合一样**，假设我们要将一个分页的元素集合建模为Page<T\>，无耻地从[Spring Data](https://www.baeldung.com/spring-data-jpa-query)中窃取一个想法：

```kotlin
interface Page<T> {
    fun pageNumber(): Int
    fun pageSize(): Int
    fun elements(): MutableList<T>
}
```

通常，为了从页面中检索元素，我们应该首先调用elements函数：

```kotlin
>> val page = firstPageOfSomething()
>> page.elements()[0]
```

由于Page本身只是另一个集合的花哨包装器，因此我们可以使用索引器运算符来增强其API：

```kotlin
operator fun <T> Page<T>.get(index: Int): T = elements()[index]
```

Kotlin编译器将Page上的任何page\[index\]替换为get(index)函数调用：

```kotlin
>> val page = firstPageOfSomething()
>> page[0]
```

我们可以更进一步，在get方法声明中添加任意数量的参数。

假设我们要检索包装集合的一部分：

```kotlin
operator fun <T> Page<T>.get(start: Int, endExclusive: Int):
        List<T> = elements().subList(start, endExclusive)
```

然后我们可以像这样对Page进行切片：

```kotlin
>> val page = firstPageOfSomething()
>> page[0, 3]
```

此外，**我们可以为get运算符函数使用任何参数类型，而不仅仅是Int**。

### 4.9 设置索引器

除了使用索引器来实现类似get的语义之外，**我们还可以利用它们来模拟类似set的操作**，我们所要做的就是定义一个名为set的运算符函数，其中包含至少两个参数：

```kotlin
operator fun <T> Page<T>.set(index: Int, value: T) {
    elements()[index] = value
}
```

当我们声明一个只有两个参数的set函数时，第一个应该在括号内使用，另一个应该在赋值之后使用：

```kotlin
val page: Page<String> = firstPageOfSomething()
page[2] = "Something new"
```

set函数也可以有不止两个参数，如果是这样，则最后一个参数是值，其余参数应在括号内传递。

### 4.10 调用

在Kotlin和许多其他编程语言中，可以使用functionName(args)语法调用函数，**也可以使用invoke运算符函数来模拟函数调用语法**。例如，为了使用page(0)而不是page\[0\]来访问第一个元素，我们可以声明一个扩展：

```kotlin
operator fun <T> Page<T>.invoke(index: Int): T = elements()[index]
```

然后，我们可以使用以下方法来检索特定的页面元素：

```kotlin
assertEquals(page(1), "Kotlin")
```

**在这里，Kotlin将括号转换为对具有适当数量参数的invoke方法的调用**。此外，我们可以使用任意数量的参数声明invoke运算符。

### 4.11 迭代器约定

如何像其他集合一样迭代页面？我们只需声明一个名为iterator的运算符函数，并将Iterator<T\>作为返回类型：

```kotlin
operator fun <T> Page<T>.iterator() = elements().iterator()
```

然后我们可以遍历一个Page：

```kotlin
val page = firstPageOfSomething()
for (e in page) {
    // Do something with each element
}
```

### 4.12 范围约定

在Kotlin中，**我们可以使用“..”运算符创建一个范围**。例如，“1..42”创建一个介于1到42之间的数字的范围。

有时，在其他非数字类型上使用范围运算符是明智的，**Kotlin标准库为所有Comparables对象提供了一个rangeTo约定**：

```kotlin
operator fun <T : Comparable<T>> T.rangeTo(that: T): ClosedRange<T> = ComparableRange(this, that)
```

我们可以使用它来获得连续几天的范围：

```kotlin
val now = LocalDate.now()
val days = now..now.plusDays(42)
```

与其他运算符一样，Kotlin编译器将任何“..”替换为rangeTo函数调用。

## 5. 明智地使用运算符

**运算符重载是Kotlin中的一个强大功能**，它使我们能够编写更简洁、有时更易读的代码。然而，能力越大，责任也越大。

**运算符重载会使我们的代码变得混乱**，或者当它被频繁使用或偶尔被误用时甚至难以阅读。

因此，在向特定类型添加新运算符之前，首先要询问该运算符在语义上是否适合我们要实现的目标，或者问我们是否可以用正常的和不那么神奇的抽象来达到同样的效果。

## 6. 总结

在本文中，我们详细了解了Kotlin中运算符重载的机制以及它如何使用一组约定来实现它。