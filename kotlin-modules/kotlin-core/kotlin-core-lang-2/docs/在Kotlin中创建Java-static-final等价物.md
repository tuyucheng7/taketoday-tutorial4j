## 1. 概述

在这个快速教程中，我们将讨论Java中的静态最终变量并了解它们在Kotlin中的等价物。

在Java中，**声明static final变量有助于我们创建常量**，在Kotlin中，我们有多种方法可以实现相同的目标。

## 2. object 

首先，让我们看一下在Kotlin [object](https://www.baeldung.com/kotlin/objects)中声明常量：

```kotlin
object TestKotlinConstantObject {
    const val COMPILE_TIME_CONST = 10

    val RUN_TIME_CONST: Int

    init {
        RUN_TIME_CONST = TestKotlinConstantObject.COMPILE_TIME_CONST + 20;
    }
}
```

在上面的例子中，**我们使用const val来声明一个编译时常量，使用val来声明一个运行时常量**。

我们在Kotlin代码中调用它们的方式与Java静态最终变量相同：

```kotlin
// Kotlin
assertEquals(10, TestKotlinConstantObject.COMPILE_TIME_CONST)
assertEquals(30, TestKotlinConstantObject.RUN_TIME_CONST)
```

**但是请注意，我们不能在Java代码中使用TestKotlinConstantObject.RUN_TIME_CONST，val关键字本身(没有const关键字)不会将Kotlin字段公开为公共字段以供Java类调用**。

要在我们的Java代码中使用RUN_TIME_CONST常量，我们应该通过TestKotlinConstantObject.INSTANCE.getRun_TIME_CONST()访问它：

```java
// Java
assertEquals(10, TestKotlinConstantObject.COMPILE_TIME_CONST);
assertEquals(30, TestKotlinConstantObject.INSTANCE.getRUN_TIME_CONST());
```

或者，我们可以使用@JvmField公开val变量以创建Java友好的静态最终变量：

```kotlin
@JvmField
val JAVA_STATIC_FINAL_FIELD = 20
```

我们可以**像Kotlin和Java类中的const val变量一样调用它**：

```kotlin
// Kotlin
assertEquals(20, TestKotlinConstantObject.JAVA_STATIC_FINAL_FIELD)
```

和

```java
// Java
assertEquals(20, TestKotlinConstantObject.JAVA_STATIC_FINAL_FIELD);
```

此外，我们还有@JvmStatic，我们可以以类似于@JvmField的方式使用它。但是我们在这里没有使用它，因为**@JvmStatic在Java中使属性访问器成为静态的，而不是变量本身**。

## 3. Kotlin类内部

**这些常量的声明在Kotlin类中类似，但它是在其**[伴生对象](https://www.baeldung.com/kotlin/companion-object)**中完成的**：

```kotlin
class TestKotlinConstantClass {
    companion object {
        const val COMPANION_OBJECT_NUMBER = 40
    }
}
```

我们可以像以前一样做：

```kotlin
// Kotlin
assertEquals(40, TestKotlinConstantClass.COMPANION_OBJECT_NUMBER)
```

和

```java
// Java
assertEquals(40, TestKotlinConstantClass.COMPANION_OBJECT_NUMBER);
```

## 4. 在kt文件中

除了在Kotlin对象和伴生对象中声明静态变量外，**我们还可以在Kotlin文件(*.kt)的顶层声明它们**。

让我们看一个例子：

```kotlin
//KotlinFile.kt

package cn.tuyucheng.taketoday.constant

const val VALUE_IN_KT_FILE = 42
val greeting="Hello"
```

如示例所示，我们已经创建了KotlinFile.kt，在这个文件中，我们没有任何类，但我们在文件的顶层声明了两个变量。

然后，在Kotlin中，我们可以直接在代码中使用它们：

```kotlin
// Kotlin
assertEquals(42, VALUE_IN_KT_FILE)
assertEquals("Hello", greeting)
```

但是，我们不能在Java中直接访问这些变量。首先，在Java中，我们不能在没有类或对象的情况下访问变量。**编译器会将Filename.kt文件编译为名为FilenameKt的类**，对于文件名中的特殊情况，有一些规则：

-   第一个字母转换为大写：myKotlin.kt -> MyKotlinKt
-   如果第一个字符对于Java类名无效，添加前导下划线(“_”)：7myKotlin.kt -> _7myKotlinKt
-   用下划线(“_”)替换文件名中的句点(“.”)：my.Kotlin.kt -> My_KotlinKt

在我们的例子中，文件名是KotlinFile.kt。因此，我们可以通过“KotlinFileKt”类访问变量：

```java
assertEquals(42, KotlinFileKt.VALUE_IN_KT_FILE);
assertEquals("Hello", KotlinFileKt.getGreeting());
```

如代码所示，类似于访问Java中Kotlin对象中声明的变量，**如果它是一个const变量，我们可以直接访问它。但是，如果它是一个普通的val变量，我们需要通过getter方法获取它的值**。同样的，我们可以添加@JvmField注解，让这个变量成为Java中的static final变量，稍后我们将在另一个示例中看到这一点。

正如我们所看到的，在kt文件中声明变量非常简单，然而，获取编译后的类名并不是那么简单。为了简化此操作，**我们可以使用**[@file:JvmName](https://www.baeldung.com/kotlin/jvm-annotations#1-file-names)**注解来定义编译后的类名**。

接下来，让我们使用@file:JvmName("NiceKotlinUtil")注解创建KotlinFileWithAnnotation.kt文件，并使用@JvmField注解标注常规val变量：

```kotlin
// KotlinFileWithAnnotation.kt

@file:JvmName("NiceKotlinUtil")

package cn.tuyucheng.taketoday.constant

const val VALUE_IN_KT_FILE_WITH_ANNOTATION = 4242
@JvmField
val greetingFromFileWithAnnotation = "Hello world"
```

现在，我们可以通过NiceKotlinUtil类访问静态变量：

```java
// Java
assertEquals(4242, NiceKotlinUtil.VALUE_IN_KT_FILE_WITH_ANNOTATION);
assertEquals("Hello world", NiceKotlinUtil.greetingFromFileWithAnnotation);
```

## 5. 总结

在本文中，我们介绍了在Kotlin中使用const、val、@JvmField和file:@JvmName来创建静态最终变量。