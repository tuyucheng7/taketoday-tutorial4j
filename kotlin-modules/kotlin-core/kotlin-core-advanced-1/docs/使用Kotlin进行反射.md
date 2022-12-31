## 1. 简介

反射是在运行时检查、加载类、字段和方法并与之交互的能力的名称。即使我们在编译时不知道它们是什么，我们也可以这样做。

这有很多用途，这取决于我们正在开发什么，例如，像Spring这样的框架大量使用它。

对此的支持内置于JVM中，因此对于所有基于JVM的语言都是隐式可用的。然而，一些JVM语言在现有的支持之上还有额外的支持。

## 2. Java反射

**所有标准的**[Java反射](https://www.baeldung.com/java-reflection)**构造都可用，并且可以与我们的Kotlin代码完美配合**，这包括java.lang.Class类以及java.lang.reflect包中的所有内容。

如果我们出于任何原因想要使用标准的Java反射API，我们可以使用与在Java中完全相同的方式来实现。例如，要获取Kotlin类中所有公共方法的列表，我们会这样做：

```kotlin
MyClass::class.java.methods
```

这分为以下结构：

-   MyClass::class为我们提供了MyClass类的Kotlin类表示
-   .java给了我们java.lang.Class等价物
-   .methods是对java.lang.Class.getMethods()访问器方法的调用

**无论是从Java还是Kotlin调用，无论是在Java还是Kotlin类上调用，这都将完全相同**，这包括Kotlin特定的构造，例如数据类。

```kotlin
data class ExampleDataClass(
  val name: String, var enabled: Boolean)

ExampleDataClass::class.java.methods.forEach(::println)
```

Kotlin还将返回的类型转换为Kotlin表示。

在上面，我们得到一个kotlin.Array<Method\>，我们可以在其上调用forEach()。

## 3. Kotlin反射增强

**虽然我们可以使用标准的Java反射API，但它并不知道Kotlin为平台带来的所有扩展**。

此外，在某些情况下使用它有时会有点尴尬，Kotlin带来了自己的反射API，我们可以使用它来解决这些问题。

Kotlin反射API的所有入口点都使用引用，早些时候，我们看到了使用::class来提供对Class定义的引用，我们还可以使用它来获取对方法和属性的引用。

### 3.1 Kotlin类引用

**Kotlin反射API允许访问类引用，然后可以使用它来反省Kotlin类的全部细节**，这可以访问Java类引用-java.lang.Class对象，但也可以访问所有Kotlin特定细节。

用于类详细信息的Kotlin API以kotlin.reflect.KClass类为中心，**这可以通过使用来自任何类名或实例的::运算符来访问(例如String::class)**。

或者，如果我们可以使用Java类实例，则可以使用扩展方法java.lang.Class.kotlin访问它：

```kotlin
val listClass: KClass<List> = List::class

val name = "Baeldung"
val stringClass: KClass<String> = name::class

val someClass: Class<MyType>
val kotlinClass: KClass<MyType> = someClass.kotlin
```

**一旦我们获得了一个KClass对象，它就可以告诉我们一些关于所讨论类的简单信息**，其中一些是标准的Java概念，另一些是Kotlin特定的概念。

例如，我们可以很容易地找出一个类是抽象类还是最终类，但我们也可以找出该类是数据类还是伴随类：

```kotlin
val stringClass = String::class
assertEquals("kotlin.String", stringClass.qualifiedName)
assertFalse(stringClass.isData)
assertFalse(stringClass.isCompanion)
assertFalse(stringClass.isAbstract)
assertTrue(stringClass.isFinal)
assertFalse(stringClass.isSealed)
```

我们也有办法在类层次结构中移动，在Java中，我们已经可以从一个类移动到它的超类、接口和它所包含的外部类——如果合适的话。

Kotlin为此添加了为任意类获取伴生对象以及为Object类获取Object实例的能力：

```kotlin
println(TestWithCompanion::class.companionObject)
println(TestWithCompanion::class.companionObjectInstance)
println(TestObject::class.objectInstance)
```

**我们也可以从类引用中创建类的新实例**，这与Java中的方式非常相似：

```kotlin
val listClass = ArrayList::class

val list = listClass.createInstance()
assertTrue(list is ArrayList)
```

或者，我们可以访问构造函数并在需要时使用显式构造函数。这些都是下一节中讨论的所有方法参考。

以非常相似的方式，我们可以访问该类的所有方法、属性、扩展和其他成员：

```kotlin
val bigDecimalClass = BigDecimal::class

println(bigDecimalClass.constructors)
println(bigDecimalClass.functions)
println(bigDecimalClass.memberProperties)
println(bigDecimalClass.memberExtensionFunctions)
```

### 3.2 Kotlin方法引用

除了能够与类进行交互之外，**我们还可以与方法和属性进行交互**。

这包括类属性-用val或var定义，标准类方法和顶级函数，和以前一样，这对用标准Java编写的代码和用Kotlin编写的代码同样有效。

与类完全相同的方式，我们可以使用**::运算符**获取对方法或属性的引用。

这看起来与Java 8中获取方法引用完全相同，我们可以完全相同地使用它。然而，在Kotlin中，这个方法引用也可以用来获取有关目标的反射信息。

**一旦我们获得了一个方法引用，我们就可以调用它，就好像它真的是所讨论的方法一样**，这被称为可调用引用：

```kotlin
val str = "Hello"
val lengthMethod = str::length
        
assertEquals(5, lengthMethod())
```

我们还可以获得有关方法本身的更多详细信息，就像我们可以获取类一样，这包括标准Java细节以及Kotlin特定细节，例如方法是运算符还是内联：

```kotlin
val byteInputStream = String::byteInputStream
assertEquals("byteInputStream", byteInputStream.name)
assertFalse(byteInputStream.isSuspend)
assertFalse(byteInputStream.isExternal)
assertTrue(byteInputStream.isInline)
assertFalse(byteInputStream.isOperator)
```

**除此之外，我们还可以通过这个参考获得更多关于该方法的输入和输出的信息**。

这包括有关返回类型和参数的详细信息，包括Kotlin特定的详细信息——例如可空性和可选性。

```kotlin
val str = "Hello"
val method = str::byteInputStream

assertEquals(
    ByteArrayInputStream::class.starProjectedType,
    method.returnType)
assertFalse(method.returnType.isMarkedNullable)

assertEquals(1, method.parameters.size)
assertTrue(method.parameters[0].isOptional)
assertFalse(method.parameters[0].isVararg)
assertEquals(
    Charset::class.starProjectedType,
    method.parameters[0].type)
```

### 3.3 Kotlin属性引用

**这对于属性也同样有效**，但显然，可以获得的细节是不同的，相反，属性可以告诉我们它们是常量、延迟初始化还是可变的：

```kotlin
lateinit var mutableProperty: String
val mProperty = this::mutableProperty
assertEquals("mutableProperty", mProperty.name)
assertTrue(mProperty.isLateinit)
assertFalse(mProperty.isConst)
assertTrue(mProperty is KMutableProperty<*>)
```

**请注意，属性的概念也适用于任何非Kotlin代码。这些由遵循有关getter和setter方法的JavaBeans约定的字段标识**。

这包括Java标准库中的类，例如，Throwable类有一个属性Throwable.message，因为其中定义了一个方法getMessage()。

**我们可以通过公开的方法引用(getter和setter方法)访问实际的属性**，setter仅在我们使用KMutableProperty时可用(即属性被声明为var)，而getter始终可用。

这些通过get()和set()方法以更易于使用的方式公开，getter和setter值是实际的方法引用，允许我们像使用任何其他方法引用一样使用它们：

```kotlin
val prop = this::mutableProperty

assertEquals(
  String::class.starProjectedType, 
  prop.getter.returnType)

prop.set("Hello")
assertEquals("Hello", prop.get())

prop.setter("World")
assertEquals("World", prop.getter())
```

## 4. 总结

本文概述了在Kotlin中使用反射可以实现的一些事情，包括它与标准Java语言中内置的反射功能的交互方式和区别。