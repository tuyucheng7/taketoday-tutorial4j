## 1. 概述

在本教程中，我们将概述Kotlin注解。

我们将演示如何应用它们，如何创建和自定义我们自己的。然后，我们将简要讨论Java和Kotlin中注解和关键字之间的相互作用。

最后，我们将给出一个简单的类验证示例，说明我们如何处理注解。

## 2. 应用注解

在Kotlin中，我们通过将带有@符号前缀的名称放在代码元素前面来应用注解。例如，如果我们想应用一个名为Positive的注解，我们应该写成如下：

```kotlin
@Positive val amount: Float
```

通常，注解有参数，**注解参数必须是编译时常量，并且必须是以下类型**：

-   Kotlin基本类型(Int、Byte、Short、Float、Double、Char、Boolean)
-   枚举
-   类引用
-   注解
-   上述类型的数组

如果注解需要参数，我们会在括号中提供它的值，就像在函数调用中一样：

```kotlin
@SinceKotlin(version="1.3")
```

在这种情况下，当注解参数也是注解时，我们应该省略@符号：

```kotlin
@Deprecated(message="Use rem(other) instead", replaceWith=ReplaceWith("rem(other)"))
```

如果注解参数是类对象，我们应该在类名后加上::class，例如：

```kotlin
@Throws(IOException::class)
```

如果我们需要指定注解参数可能有多个值，那么我们只需传递这些值的数组：

```kotlin
@Throws(exceptionClasses=arrayOf(IOException::class, IllegalArgumentException::class))
```

从Kotlin 1.2开始，我们也可以使用以下语法：

```kotlin
@Throws(exceptionClasses=[IOException::class, IllegalArgumentException::class])
```

## 3. 声明注解

为了声明一个注解，我们定义了一个类并将annotation关键字放在class关键字之前，就其本质而言，**注解声明不能包含任何代码**。

最简单的注解没有参数：

```kotlin
annotation class Positive
```

需要参数的注解声明就像具有主构造函数的类：

```kotlin
annotation class Prefix(val prefix: String)
```

当我们声明我们的自定义注解时，我们应该指定它们可能应用到哪些代码元素以及它们应该存储在哪里，用于定义此元信息的注解称为元注解。

在接下来的部分中，我们将简要讨论它们。对于最新的信息，我们可以随时查看[官方文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/index.html)。

### 3.1 @Target

此元注解指定此注解可能引用哪些代码元素，**它有一个必需的参数，该参数必须是AnnotationTarget枚举的实例或其数组**。因此，我们可以指定要将注解应用于以下元素：

-   CLASS
-   ANNOTATION_CLASS
-   TYPE_PARAMETER
-   PROPERTY
-   FIELD
-   LOCAL_VARIABLE
-   VALUE_PARAMETER
-   CONSTRUCTOR
-   FUNCTION
-   PROPERTY_GETTER
-   PROPERTY_SETTER
-   TYPE
-   EXPRESSION
-   FILE
-   TYPEALIAS

如果我们不显式指定，对应的注解可以默认应用于以下元素：

CLASS、PROPERTY、FIELD、LOCAL_VARIABLE、VALUE_PARAMETER、CONSTRUCTOR、FUNCTION、PROPERTY_GETTER、PROPERTY_SETTER。

### 3.2 @Retention

此元注解指定注解是否应存储在.class文件中以及它是否应该对反射可见，**它的必需参数必须是具有以下元素的AnnotationRetention枚举的实例**：

-   SOURCE
-   BINARY
-   RUNTIME

与Java不同，Kotlin中@Retention的默认值为RUNTIME。

### 3.3 @Repeatable

@Repeatable指定一个元素是否可以有多个相同类型的注解，此元注解不接收任何参数。

### 3.4 @MustBeDocumented

@MustBeDocumented指定注解的文档是否应包含在生成的文档中，此元注解也不接收任何参数。

## 4. 与Java注解的互操作性

Kotlin通常比Java更简洁，例如，它会自动为我们创建额外的方法，比如当我们声明一个属性时：

```kotlin
val name: String?
```

编译器会自动为此属性创建一个私有字段以及一个getter和setter，这样一来，一个问题就出现了：如果我们给属性加上注解，会应用到哪里呢？对getter、setter还是字段本身？

**在Kotlin中，如果我们用Java代码中定义的注解装饰一个属性，它就会应用于相应的字段**。

现在我们可能会遇到一个问题，如果注解需要一个字段是公共的，例如，使用JUnit的[@Rule](https://junit.org/junit4/kotlindoc/4.12/org/junit/Rule.html)注解，为了避免歧义，Kotlin有所谓的use-site目标声明。

### 4.1 Use-Site目标声明

Use-Site目标是可选的，我们将它们放在@符号和注解名称之间，使用冒号作为分隔符，该语法允许我们一次指定多个注解名称：

<img src="../assets/img.png">

在将@get:Positive放在Kotlin字段上的情况下，这意味着注解实际上应该针对该字段生成的getter。

Kotlin支持以下对应于use-site目标的值：

-   delegate：存储委托属性的字段
-   field：为属性生成的字段
-   file：包含在该文件中定义的顶级函数和属性的类
-   get/set：属性getter/setter
-   param：构造函数参数
-   property：Kotlin的属性，Java代码无法访问它
-   receiver：扩展函数或属性的接收者参数

### 4.2 JVM相关注解

以下Kotlin注解允许我们自定义如何在Java代码中使用它们：

-   @JvmName：允许更改生成的Java方法或字段的名称
-   @JvmStatic：允许我们指定生成的Java方法或字段应该是静态的
-   @JvmOverloads：表示Kotlin编译器应该生成重载函数来替换默认参数
-   @JvmField：指示生成的Java字段应该是没有getter/setter的公共字段

一些Java注解成为Kotlin的关键字，反之亦然：

| Java  | Kotlin |
|-------|--------|
| @Override   | override     |
| volatile  | @@Volatile  |
| strictfp | @Strictfp  |
| synchronized   | @synchronized    |
| transient	   | @Transient   |
| throws    | @Throws    |

## 5. 处理注解

为了演示我们如何处理注解，让我们创建一个简单的验证器。

假设我们应该决定一个Item的实例是否有效：

```kotlin
class Item(val amount: Float, val name: String)
```

我们假设如果金额的值为正且名称的值为Alice或Bob，则Item实例是有效的。

为此，让我们用自定义注解装饰Item类的属性：Positive和AllowedNames：

```kotlin
class Item(
    @Positive val amount: Float,
    @AllowedNames(["Alice", "Bob"]) val name: String)
```

在我们天真的实现中，我们只获取Item的属性：

```kotlin
val fields = item::class.kotlin.declaredFields
for (field in fields) {...}
```

并遍历每个属性的所有注解：

```kotlin
for (annotation in field.annotations) {...}
```

为了找到我们装饰Item属性的那些。

例如，我们可以通过以下命令检测属性上是否存在AllowedNames：

```kotlin
field.isAnnotationPresent(AllowedNames::class.kotlin)
```

一旦注解出现，我们就可以通过将它与允许的值进行比较来轻松地确定该属性是否具有有效值：

```kotlin
val allowedNames = field.getAnnotation(AllowedNames::class.kotlin)?.names
```

**我们应该知道注解使用**[Java反射API](https://www.baeldung.com/kotlin-reflection)，因此，如果我们严重依赖注解，代码的性能可能会受到影响。

## 6. 总结

在本文中，我们考虑了Kotlin注解及其Java对应物。我们已经描述了如何应用Kotlin注解、如何创建自定义注解以及如何处理它们。

正如我们所看到的，Kotlin注解与Java中的注解非常相似。尽管如此，我们的教程[在Java中创建自定义注解](https://www.baeldung.com/kotlin-custom-annotation)也可能很有用。