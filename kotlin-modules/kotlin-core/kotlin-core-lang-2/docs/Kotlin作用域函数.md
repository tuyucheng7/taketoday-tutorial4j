## 1. 概述

作用域函数非常有用，我们在Kotlin代码中经常使用它们。

在本教程中，我们将解释它们是什么，并提供一些示例说明何时使用它们。

## 2. also

首先，让我们看一下突变函数also和apply。

简而言之，**变异函数对给定对象进行操作并返回它**。

在also的情况下，一个扩展方法，我们提供了一个对扩展对象进行操作的lambda：

```kotlin
inline fun T.also(block: (T) -> Unit): T
```

它会返回它被调用的对象，当我们想在调用链上生成一些辅助逻辑时，它会很方便：

```kotlin
val headers = restClient
    .getResponse()
    .also { logger.info(it.toString()) }
    .getHeaders()
```

**请注意我们对it的使用**，因为这将在以后变得很重要。

我们也可以使用来初始化对象：

```kotlin
val aStudent = Student().also { it.name = "John" }
```

当然，既然我们可以将实例称为it，那么我们也可以重命名它，**通常会创建更具可读性的内容**：

```kotlin
val aStudent = Student().also { newStudent -> newStudent.name = "John" }
```

当然，如果lambda包含复杂的逻辑，能够命名实例将有助于我们的读者。

## 3. apply

但是，**也许我们不希望it lambda参数过于冗长**。

apply就像also一样，但是有一个隐含的this：

```kotlin
inline fun T.apply(block: T.() -> Unit): T
```

我们也可以像以前一样使用apply来初始化一个对象，请注意，我们不使用it：

```kotlin
val aStudent = Student().apply {
    studentId = "1234567"
    name = "Mary"
    surname = "Smith"
}
```

或者，我们可以使用它来轻松创建构建器样式的对象：

```kotlin
data class Teacher(var id: Int = 0, var name: String = "", var surname: String = "") {
    fun id(anId: Int): Teacher = apply { id = anId }
    fun name(aName: String): Teacher = apply { name = aName }
    fun surname(aSurname: String): Teacher = apply { surname = aSurname }
}

val teacher = Teacher()
    .id(1000)
    .name("Martha")
    .surname("Spector")
```

**这里的主要区别在于also使用it，而apply则不使用**。

## 4. let

现在，让我们看一下转换函数let、run和with，它们只是比突变函数复杂一步。

简而言之，转换函数接收一种类型的源并**返回另一种类型的目标**。

首先，let：

```kotlin
inline fun <T, R> T.let(block: (T) -> R): R
```

除了我们的块返回R而不是Unit之外，这也很像also。

让我们看看这有什么不同。

首先，我们可以使用let将一种对象类型转换为另一种对象类型，**例如使用StringBuilder并计算其长度**：

```kotlin
val stringBuilder = StringBuilder()
val numberOfCharacters = stringBuilder.let {
    it.append("This is a transformation function.")
    it.append
    ("It takes a StringBuilder instance and returns the number of characters in the generated String")
    it.length
}
```

或者其次，我们可以使用Elvis运算符有条件地调用它，同时给它一个默认值：

```kotlin
val message: String? = "hello there!"
val charactersInMessage = message?.let {
    "value was not null: $it"
} ?: "value was null"
```

**let与also的不同之处在于返回类型会发生变化**。

## 5. run

**run与let的关联方式与apply与also的关联方式相同**：

```kotlin
inline fun <T, R> T.run(block: T.() -> R): R
```

请注意，我们返回一个类型R，如let，使它成为一个转换函数，但我们采用隐式this，如apply。

这种差异虽然微妙，但通过一个例子就变得明显了：

```kotlin
val message = StringBuilder()
val numberOfCharacters = message.run {
    append("This is a transformation function.")
    append("It takes a StringBuilder instance and returns the number of characters in the generated String")
    length
}
```

对于let，**我们将message实例称为it，但在这里，message是lambda内部的隐式this**。

我们可以使用与let相同的方法来处理可空性：

```kotlin
val message: String? = "hello there!"
val charactersInMessage = message?.run {
    "value was not null: $this"
} ?: "value was null"
```

## 6. with

我们最后一个转换函数是with，就像run一样，它有一个隐含的this，但它不是一个扩展方法：

```kotlin
inline fun <T, R> with(receiver: T, block: T.() -> R): R
```

**我们可以使用with将对象限制在一个范围内**，另一种看待它的方式是在逻辑上将对给定对象的多个调用分组：

```kotlin
with(bankAccount) {
    checkAuthorization(...)
    addPayee(...)
    makePayment(...)
}
```

## 7. 总结

在本文中，我们探讨了不同的作用域函数，对它们进行了分类，并根据它们的结果对其进行了解释。它们的用法有一些重叠，但通过一些实践和常识，我们可以了解应用哪个作用域函数以及何时应用。