## 1. 概述

[空安全](https://www.baeldung.com/kotlin/null-safety)是Kotlin中的一个很好的特性，它使我们能够全面处理可为空的值。

在本教程中，让我们看看编译错误“Smart cast to <type\> is impossible”，这是由Kotlin将可空类型智能转换为相应的不可空类型引起的。

## 2. 问题简介

首先我们来看一个简单的类：

```kotlin
class NiceOne {
    val myString: String? = null
    val notNullList: MutableList<String> = mutableListOf()

    fun addToList() {
        if (myString != null) {
            notNullList += myString
        }
    }
}
```

如上面的代码所示，NiceOne类有一个不可变且**可为空的字符串属性(myString)**，此外，它还有一个**可变集合属性notNullList，它只接收非空字符串**。

在addToList()函数中，我们首先检查属性myString，如果它不为空，我们将其添加到notNullList。在这里，我们使用内置的MutableCollection的plusAssign()[运算符重载](https://www.baeldung.com/kotlin/operator-overloading)(+=)来添加元素。

该类编译没有任何问题，这是因为**在空检查之后，当我们尝试将可空字符串添加到只接受非空字符串的列表时，Kotlin智能转换可空类型String?到不可为null的类型String**。

现在，假设我们有一个新需求，我们需要使myString变量可变。因此，我们可以将[val关键字](https://www.baeldung.com/kotlin/const-var-and-val-keywords)更改为var：

```kotlin
class NiceOne() {
    var myString: String? = null
}
```

但是，在这个微小的更改之后，如果我们尝试编译该类，我们将看到编译错误消息：

```bash
Kotlin: Smart cast to 'String' is impossible, because 'myString' is a mutable property that could have been changed by this time
```

在我们将myString变量添加到列表的行中出现此错误：

```kotlin
if (myString != null) 
    notNullList += myString
        // ~~ ^^ ~~ compilation error
```

那么，接下来，让我们仔细看看这个错误，了解Kotlin报错的原因以及如何修复它。

## 3. 为什么Kotlin的智能转换在val上有效但在var上失败？

我们知道，通过val关键字声明的变量在Kotlin中是不可变的，因此，**如果我们检查了一个不可变变量不是null，尽管它是一个可以为null的类型，将它转换为对应的不可为null的类型是安全的**，这就是Kotlin的智能转换适用于不可变变量的原因。

另一方面，**如果一个可空变量是可变的，即使我们已经检查了它的值不是空的，我们仍然不能保证它的值在我们将它转换为不可空类型之前不会被更改为空**。

让我们回顾一下我们的例子，在我们的类中，var myString: String?是一个类属性，因此我们可以通过NiceOne类的一个实例来访问它，假设我们有一个实例niceOne。

即使我们已经检查过，niceOne.myString在Kotlin强制转换String?到String之前也不是空的，另一个线程可能已将null值分配给niceOne.myString：

```kotlin
fun addToList(){
    if (myString != null) {
       // at this point, another thread may have changed myString's value to null
       notNullList += myString
```

Kotlin不知道这个变量的值是什么时候被修改的，以及它是否被设置为null，因此，Kotlin拒绝智能转换。

现在我们了解了错误的原因，让我们探讨如何修复它。

## 4. 首选不可为空的类型

当我们遇到上述问题时，首先，我们可以重新考虑我们的设计，看看是否可以将变量声明为不可空类型。但是，当然，这完全取决于设计和要求。

例如，**我们确信在我们第一次在运行时使用它之前总是会设置该变量，但是，我们无法预测它的初始值。在这种情况下，我们可以考虑使用Kotlin的**[惰性初始化](https://www.baeldung.com/kotlin/lazy-initialization)(lateinit var ...)：

```kotlin
class NiceTwo {
    val notNullList: MutableList<String> = mutableListOf()
    private lateinit var myString: String

    private fun determineString() {
        myString = if (notNullList.size % 2 == 0) "Even" else "Odd"
    }

    fun addToList() {
        determineString()
        notNullList += myString
    }
}
```

如上面的示例所示，我们已经使用不可为空的字符串定义了myString，此外，determineString()函数负责初始化可变的myString变量。

让我们创建一个测试来验证它是否有效：

```kotlin
val two = NiceTwo()
two.addToList()
assertThat(two.notNullList).containsExactly("Even")
two.addToList()
assertThat(two.notNullList).containsExactly("Even", "Odd")
```

当然，**如果我们在初始化之前访问lateinit变量，我们将得到一个UninitializedPropertyAccessException**。在这种情况下，我们可能会发现忘记初始化lateinit变量的错误。

我们已经看到使用不可为空的类型可以使我们免于转换问题，然而，我们有时确实希望变量携带一个空值，那么接下来，让我们看看如何处理这种情况。

## 5. 制作局部副本

我们已经了解此错误的原因是另一个线程可以修改可变类属性；此外，我们知道**所有函数局部变量都是线程安全的**，因为它们是在堆栈中创建的。

也就是说，如果我们将类属性的值到复制局部变量中，则局部副本不会被其他线程更改，因此，Kotlin的智能转换应该适用于局部副本。

### 5.1 复制到局部变量

现在，让我们修改addToList()函数以使用myString的本地副本：

```kotlin
class NiceThree {
    var myString: String? = null
    val notNullList: MutableList<String> = mutableListOf()

    fun addToList() {
        val myCopy: String? = myString
        if (myCopy != null) {
            notNullList += myCopy
        }
    }
}
```

接下来，让我们创建一个测试并检查此方法是否按预期工作：

```kotlin
val three = NiceThree()
three.myString = "One"
three.addToList()
assertThat(three.notNullList).containsExactly("One")

three.myString = "Two"
three.addToList()
assertThat(three.notNullList).containsExactly("One", "Two")

three.myString = null
three.addToList()
assertThat(three.notNullList).containsExactly("One", "Two")
```

如果我们运行测试，它就会通过。因此，这意味着智能转换适用于局部副本。

### 5.2 使用安全调用运算符(.?)和作用域函数

刚才，我们学习了智能转换问题的两步解决方案：

-   制作本地副本
-   检查值不为空

在Kotlin中，我们可以使用[安全调用运算符](https://www.baeldung.com/kotlin/null-safety#safe-calls)(.?)和[作用域函数](https://www.baeldung.com/kotlin/scope-functions)来惯用地应用这两个步骤：

```kotlin
class NiceFour {
    var myString: String? = null
    val notNullList: MutableList<String> = mutableListOf()

    fun addToList() {
        myString?.let { notNullList += it }
    }
}
```

如上面的代码所示，只有当myString不为空时，我们才能安全地调用let()函数。另外，我们应该注意到**变量it是let()函数中不可为空的myString值的本地副本**。

接下来，像往常一样，让我们通过测试来验证这个解决方案是否有效：

```kotlin
val four = NiceFour()
four.myString = "One"
four.addToList()
assertThat(four.notNullList).containsExactly("One")

four.myString = "Two"
four.addToList()
assertThat(four.notNullList).containsExactly("One", "Two")

four.myString = null
four.addToList()
assertThat(four.notNullList).containsExactly("One", "Two")
```

同样，如果我们运行它，测试就会通过。

正如我们所见，制作可空变量的本地副本解决了智能转换问题。但是，我们应该注意，**当我们制作本地副本时，我们使用的是变量的快照副本，而不是它的实时值**。

## 6. 使用非空断言运算符(!!)

有时，这取决于需求，我们可能需要使用变量的实时值而不是快照副本，因此，我们不能采取制作本地副本的方式。

如果是这种情况，我们可以使用[非空断言运算符](https://www.baeldung.com/kotlin/not-null-assertion)(!!)：

```kotlin
class NiceFive {
    var myString: String? = null
    val notNullList: MutableList<String> = mutableListOf()

    fun addToList() {
        try {
            if (myString != null) {
                notNullList += myString!!
            }
        } catch (ex: java.lang.NullPointerException) {
            // exception handling omitted
        }
    }
}
```

正如我们在函数NiceFive.addToList()中看到的，我们使用myString!!，这将为以下操作使用变量myString的实时值，将其添加到notNullList。

此外，**我们添加了一个try-catch块作为!!如果变量值为空可能会抛出NullPointerException**。在示例中，我们省略了异常处理，然而，在现实世界中，我们需要妥善处理NullPointerException异常。

最后，让我们创建一个测试来检查上面的代码是否按预期编译和工作，为简单起见，我们跳过NullPointerException情况：

```kotlin
val five = NiceFive()
five.myString = "One"
five.addToList()
assertThat(five.notNullList).containsExactly("One")

five.myString = "Two"
five.addToList()
assertThat(five.notNullList).containsExactly("One", "Two")

five.myString = null
five.addToList()
assertThat(five.notNullList).containsExactly("One", "Two")
```

如果我们运行上面的测试，代码编译没有任何错误，测试也通过了。

## 7. 总结

在本文中，我们讨论了为什么Kotlin编译器在将可空类型智能转换为相应的不可空类型时可能会引发错误，此外，我们探讨了几种不同的方法来解决这个问题。