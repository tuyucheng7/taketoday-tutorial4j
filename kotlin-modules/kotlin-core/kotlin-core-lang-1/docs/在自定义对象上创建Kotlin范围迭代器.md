## 1. 简介

在上一篇文章中，我们演示了[如何在Kotlin中创建一个范围](如何使用Kotlin范围表达式.md)，以及迭代Int、Long和Char类型是多么容易。

但是如果我们想**遍历自定义类型**怎么办？可能吗？答案是肯定的！因此，让我们跳入代码，看看如何操作。

## 2. 多样的类型

假设我们有一个表示RGB颜色的简单类：

```kotlin
class CustomColor(val rgb: Int): Comparable<CustomColor> {}
```

能够迭代一系列RGB颜色会很好：

```kotlin
val a = CustomColor(0x000000)
val b = CustomColor(0xCCCCCC)
for (cc in a..b) {
    // do things
}
```

## 3. 快速了解IntRange

简而言之，**我们需要实现Comparable、Iterable和ClosedRange**。从我们[之前的文章](如何使用Kotlin范围表达式.md)中，我们已经知道我们必须实现Comparable。

对于其他两个接口，让我们深入了解IntRange类声明以获得一些提示：

```kotlin
public class IntRange(start: Int, endInclusive: Int) :
    IntProgression(start, endInclusive, 1), ClosedRange<Int>
```

然后，IntProgression的声明表明它实现了Iterable<Int\>：

```kotlin
public open class IntProgression : Iterable<Int>
```

所以，我们要做一些类似的事情来完成这项工作。

## 4. ColorRange类

像IntRange一样，让我们创建一个ColorRange类。

出于我们的目的，我们也将跳过模仿IntProgression，因为**我们可以接受默认步长1**，这将稍微简化事情并允许我们简单地**直接实现ClosedRange和Iterable**：

```kotlin
class ColorRange(override val start: CustomColor,
                 override val endInclusive: CustomColor) : ClosedRange<CustomColor>, Iterable<CustomColor>{

    override fun iterator(): Iterator<CustomColor> {
        return ColorIterator(start, endInclusive)
    }
}
```

对于iterator()的实现，我们将**返回一个ColorIterator类，该类将完成实际遍历范围的繁重工作**。

因为ColorRange实现了ClosedRange<T: Comparable<T\>>接口，所以我们必须在CustomColor类上实现compareTo方法：

```kotlin
override fun compareTo(other: CustomColor): Int {
    return this.rgb.compareTo(other.rgb)
}
```

## 5. ColorIterator类

ColorIterator是拼图的最后一块：

```kotlin
class ColorIterator(val start: CustomColor, val endInclusive: CustomColor) : Iterator<CustomColor> {

    var initValue = start

    override fun hasNext(): Boolean {
        return initValue <= endInclusive
    }

    override fun next(): CustomColor {
        return initValue++
    }
}
```

请注意，initValue的类型是CustomColor，因此，要使用++运算符对其进行更改，我们还需要将inc()方法添加到CustomColor中：

```kotlin
operator fun inc(): CustomColor {
    return CustomColor(rgb + 1)
}
```

## 6. 使用自定义范围

由于我们正在定义自定义范围，因此CustomColor类必须实现rangeTo方法。**rangeTo方法将允许我们使用..运算符迭代我们的范围**，有点像添加inc允许我们使用++运算符的方式。

让我们看看最终产品：

```kotlin
class CustomColor(val rgb: Int) : Comparable<CustomColor> {

    override fun compareTo(other: CustomColor): Int {
        return this.rgb.compareTo(other.rgb)
    }

    operator fun rangeTo(that: CustomColor) = ColorRange(this, that)

    operator fun inc(): CustomColor {
        return CustomColor(rgb + 1)
    }
}
```

这就是我们所需要的！

最后，让我们通过使用我们的一系列CustomColor类，看看这一切是如何协同工作的：

```kotlin
@Test
fun assertHas10Colors(){
    assertTrue {
        val a = CustomColor(1)
        val b = CustomColor(10)
        val range = a..b
        for (cc in range) {
            println(cc)
        }
        range.toList().size == 10
    }
}
```

在此测试中，我们定义了一个range变量并用于循环访问CustomColor对象，并将其转换为列表。

让我们看另一个在范围上使用标准contains方法的例子：

```kotlin
@Test
fun assertContains0xCCCCCC(){
    assertTrue {
        val a = CustomColor(0xBBBBBB)
        val b = CustomColor(0xDDDDDD)
        val range = a..b
        range.contains(CustomColor(0xCCCCCC))
    }
}
```

## 7. 总结

Kotlin具有Int、Long和Char值范围的原生实现，在本文中，我们学习了如何在自定义类中实现范围。