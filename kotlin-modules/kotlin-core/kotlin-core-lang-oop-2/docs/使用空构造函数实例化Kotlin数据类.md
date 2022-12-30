## 1. 概述

作为数据容器，Kotlin的[数据类](../../kotlin-core-lang-oop-1/docs/Kotlin中的数据类.md)默认实现了一些有价值的方法，如equals()、toString()、copy()等。

在本教程中，让我们探讨如何使用空构造函数实例化Kotlin数据类。

## 2. 问题简介

我们已经提到，作为一个数据容器，Kotlin的数据类带来了很多优势。但是，**数据类的一个要求是其主构造函数必须至少有一个参数**。

这意味着它没有类似Java的“默认构造函数”或“空构造函数”。

由于数据类的目标是保存数据的用例，因此数据类可能具有相当多的参数。因此，当我们要创建一个数据类的实例时，我们可能会向构造函数传递数十个参数。这是一种不便，我们可能会错过类的默认构造函数或空构造函数。

在本教程中，我们将介绍几种使用空构造函数实例化数据类的方法。此外，我们将介绍Kotlin的无参数编译器插件并讨论它的用例。

为简单起见，我们将使用单元测试断言来验证我们创建的实例。

## 3. 添加一个空的辅助构造函数

我们知道Kotlin数据类的主构造函数必须至少有一个参数，让我们看一个例子：

```kotlin
data class ArticleWithoutDefault(
    var title: String,
    var author: String,
    var abstract: String,
    var words: Long
)
```

在上面的数据类中，主构造函数有四个参数，如果我们想要实例化ArticleWithoutDefault类，我们需要提供所有这些。

然而，Kotlin允许我们创建[辅助构造函数](https://kotlinlang.org/docs/classes.html#secondary-constructors)。例如，要通过空构造函数实例化ArticleWithoutDefault类，**我们可以创建一个辅助构造函数，该构造函数使用默认值调用主构造函数**：

```kotlin
data class ArticleWithoutDefault(
    var title: String,
    var author: String,
    var abstract: String,
    var words: Long
) {
    constructor() : this("dummy title", "dummy author", "dummy abstract", 0)
}
```

通过这种方式，我们就可以调用新创建的构造函数来获取ArticleWithoutDefault的实例。当然，该实例将包含在辅助构造函数中定义的初始值：

```kotlin
val myInstance = ArticleWithoutDefault()
assertThat(myInstance).isInstanceOf(ArticleWithoutDefault::class.java)
    .extracting("title", "author", "abstract", "words")
    .containsExactly("dummy title", "dummy author", "dummy abstract", 0L)
```

当我们运行测试时，它通过了，这意味着实例已通过空构造函数成功创建。

## 4. 将默认值添加到主构造函数

我们已经学会了添加一个空的辅助构造函数，该构造函数使用初始属性值调用主构造函数。实际上，Kotlin允许我们为函数参数设置[默认值](https://kotlinlang.org/docs/idioms.html#default-values-for-function-parameters)，它也适用于构造函数：

```kotlin
data class ArticleWithDefault(
    var title: String = "default title",
    var author: String = "default author",
    var abstract: String = "",
    var words: Long = 0L
)
```

正如我们在上面的数据类中看到的，**我们可以为主构造函数的参数定义默认值。因此，我们不需要创建辅助构造函数**；相反，我们可以简单地使用空构造函数实例化数据类来应用所有默认值：

```kotlin
val myInstance = ArticleWithDefault()
assertThat(myInstance).isInstanceOf(ArticleWithDefault::class.java)
    .extracting("title", "author", "abstract", "words")
    .containsExactly("default title", "default author", "", 0L)
```

当类构造函数的参数具有默认值时，可以非常灵活地创建该类的实例。例如，对于ArticleWithDefault数据类，我们可以只将title和words传递给主构造函数，并将author和abstract保留默认值：

```kotlin
val myArticle = ArticleWithDefault(title="A Great Article", words=42L)
assertThat(myArticle).isInstanceOf(ArticleWithDefault::class.java)
    .extracting("title", "author", "abstract", "words")
    .containsExactly("A Great Article", "default author", "", 42L)
```

## 5. 关于无参数编译器插件

Kotlin附带一个[无参数编译器插件](https://kotlinlang.org/docs/no-arg-plugin.html)，**该插件在编译期间为具有特定注解的类生成一个额外的空构造函数**。这听起来像是我们问题的一个很好的解决方案，但是，这是出于其他目的。那么接下来，让我们仔细看看无参数插件。

### 5.1 配置无参数插件

我们需要在Maven或Gradle端做一些配置工作以启用无参数插件，在本教程中，我们将以Maven为例。

让我们将插件添加到<build\><plugins\>元素中：

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <configuration>
        <compilerPlugins>
            <plugin>no-arg</plugin>
        </compilerPlugins>
        <pluginOptions>
            <option>no-arg:annotation=cn.tuyucheng.taketoday.kotlin.emptyConstructorOfDataCls.NoArg</option>
        </pluginOptions>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-noarg</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

在上面的配置中，<pluginOptions\>设置非常重要。在那里，我们引用了无参数插件的[注解类](https://kotlinlang.org/docs/annotations.html)。在此示例中，我们将cn.tuyucheng.taketoday.emptyConstructorOfDataCls.NoArg定义为注解类，**这意味着如果我们用@NoArg注解标记一个类，Kotlin编译器将向这个类添加一个空的构造函数**。

接下来，让我们创建注解类。

### 5.2 创建@Noarg注解并将其应用于数据类

首先，让我们在定义的包下创建NoArg注解：

```kotlin
package cn.tuyucheng.taketoday.kotlin.emptyConstructorOfDataCls

annotation class NoArg
```

接下来，让我们创建一个新的数据类并将其使用@NoArg注解标注：

```kotlin
@NoArg
data class Person(var firstName: String = "a nice name", var midName: String?, var lastName: String, var age: Int)
```

如上面的代码所示，我们的Person类有一个带有四个参数的主构造函数。firstName参数有一个默认值，此外，除了midName之外，所有参数都不为空。

现在，如果我们编译这个类，我们可能会期望无参数插件为我们创建一个空的构造函数，这样我们就可以通过类似val person = Person()的东西实例化我们的Person类。

接下来，让我们看看它是否按预期工作。

### 5.3 直接从Kotlin调用空构造函数

首先，让我们在测试方法中调用空构造函数：

```kotlin
val person = Person()
```

然而，当我们编译代码时，我们会发现它没有编译：

```bash
[INFO] ...
[INFO] --- kotlin-maven-plugin:1.6.0:test-compile (test-compile) @ core-kotlin-lang-oop-2 ---
[INFO] Applied plugin: 'no-arg'
[ERROR] /.../EmptyConstructorOfDataClsUnitTest.kt: (53, 29) No value passed for parameter 'midName'
[ERROR] /.../EmptyConstructorOfDataClsUnitTest.kt: (53, 29) No value passed for parameter 'lastName'
[ERROR] /.../EmptyConstructorOfDataClsUnitTest.kt: (53, 29) No value passed for parameter 'age'
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

上面的构建日志显示no-arg插件已经启用，但是为什么它仍然抱怨构造函数参数呢？**这是因为生成的构造函数是合成的，换句话说，它不能直接从Java或Kotlin调用**。因此，它主要用于反射API。

### 5.4 使用Kotlin反射API调用空构造函数

接下来，让我们使用Kotlin反射调用空构造函数：

```kotlin
val person = Person::class.createInstance()
```

这一次，代码编译通过。但是，当我们执行测试方法时，测试失败：

```bash
ERROR!
java.lang.IllegalArgumentException: Class should have a single no-arg constructor: class cn.tuyucheng.taketoday.kotlin.emptyConstructorOfDataCls.Person
at cn.tuyucheng.taketoday.kotlin.emptyConstructorOf...(EmptyConstructorOfDataClsUnitTest.kt:55)
```

这是因为Kotlin的反射API与Kotlin代码元素一起工作，合成空构造函数不是Kotlin代码的一部分。因此，**无参数插件创建的空构造函数无法被Kotlin反射API识别**。

### 5.5 使用Java反射API调用空构造函数

最后，让我们使用[Java反射API](https://www.baeldung.com/java-reflection)调用空构造函数：

```kotlin
val myInstance = Person::class.java.getConstructor().newInstance()
assertThat(myInstance).isInstanceOf(Person::class.java)
    .extracting("firstName", "midName", "lastName", "age").containsExactly(null, null, null, 0)
```

当我们执行上面的测试时，它通过了。因此，**只有Java反射才能使用no-arg插件生成的合成构造函数**。

此外，让我们重新审视Person的主要构造函数的参数：

```kotlin
var firstName: String = "a nice name", 
var midName: String?, 
var lastName: String,
var age: Int
```

测试中的断言表明，**即使Java反射可以使用空构造函数，创建的实例也不遵循Kotlin类的约定**：

-  Kotlin的非空属性(例如firstName和lastName)无论如何都可以保存空值。
-  参数的默认值(例如我们为firstName指定的参数)也不会应用。

基本上，无参数编译器插件仅对库开发有用。例如，[Java Persistence API(JPA)](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)通过其空构造函数实例化一个类，这个插件允许JPA实例化一个Kotlin类，尽管没有定义空构造函数。

## 6. 总结

在本文中，我们探讨了两种使用空构造函数实例化Kotlin数据类的方法。

此外，我们还讨论了no-arg编译器插件，需要注意的是，no-arg插件生成的空构造函数只能被Java反射使用。