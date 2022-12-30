## 1. 简介

Kotlin编程语言原生支持类属性。

属性通常由相应的字段直接支持，但并不总是需要这样-只要它们正确地暴露给外部世界，它们仍然可以被视为属性。

这可以通过在getter和setter中处理，或利用委托的力量来实现。

## 2. 什么是委托属性？

**简而言之，委托属性不受类字段的支持，并将获取和设置委托给另一段代码**。这允许将委托的功能抽象出来并在多个相似属性之间共享。例如，将属性值存储在Map中而不是单独的字段中。

通过声明属性及其使用的委托来使用委托属性，**by关键字表示该属性由提供的委托而不是它自己的字段控制**。

例如：

```kotlin
class DelegateExample(map: MutableMap<String, Any?>) {
    var name: String by map
}
```

这利用了MutableMap本身就是委托的事实，允许你将其键视为属性。

## 3. 标准委托属性

Kotlin标准库附带了一组随时可以使用的标准委托。

我们已经看到了一个使用MutableMap来支持可变属性的示例。以同样的方式，你可以使用Map支持不可变属性-允许将单个字段作为属性进行访问，但永远不会更改它们。

**惰性委托允许只在第一次访问时计算属性值，然后进行缓存**，这对于计算成本可能很高且你可能永远不需要的属性很有用-例如，从数据库加载的属性：

```kotlin
class DatabaseBackedUser(userId: String) {
    val name: String by lazy {
        queryForValue("SELECT name FROM users WHERE userId = :userId", mapOf("userId" to userId)
    }
}
```

**observable委托允许在属性值发生变化时触发lambda**，例如允许更改通知或更新其他相关属性：

```kotlin
class ObservedProperty {
    var name: String by Delegates.observable("<not set>") { prop, old, new ->
        println("Old value: $old, New value: $new")
    }
}
```

**从Kotlin 1.4开始，也可以直接委托给另一个属性**。例如，如果我们要重命名API类中的属性，我们可以保留旧的属性，只需委托给新的属性：

```kotlin
class RenamedProperty {
    var newName: String = ""

    @Deprecated("Use newName instead")
    var name: String by this::newName
}
```

在这里，每当我们访问name属性时，我们实际上都是在使用newName属性。

## 4. 创建委托

有时你想要编写委托，而不是使用已经存在的委托，**这依赖于编写一个扩展两个接口之一的类-ReadOnlyProperty或ReadWriteProperty**。

这两个接口都定义了一个名为getValue的方法，该方法用于在读取委托属性时提供其当前值，这需要两个参数并返回属性的值：

-   thisRef：对属性所在类的引用
-   property：被委托的属性的反射描述

ReadWriteProperty接口另外定义了一个名为setValue的方法，该方法用于在写入时更新属性的当前值，这需要三个参数并且没有返回值：

-   thisRef：对属性所在类的引用
-   property：被委托的属性的反射描述
-   value：属性的新值

从Kotlin 1.4开始，ReadWriteProperty接口实际上扩展了ReadOnlyProperty，**这允许我们编写一个实现ReadWriteProperty的委托类，并将其用于代码中的只读字段**。以前，我们不得不编写两个不同的委托-一个用于只读字段，另一个用于可变字段。

例如，让我们编写一个始终针对数据库连接而不是本地字段工作的委托：

```kotlin
class DatabaseDelegate<in R, T>(readQuery: String, writeQuery: String, id: Any) : ReadWriteDelegate<R, T> {
    fun getValue(thisRef: R, property: KProperty<*>): T {
        return queryForValue(readQuery, mapOf("id" to id))
    }

    fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        update(writeQuery, mapOf("id" to id, "value" to value))
    }
}
```

这取决于访问数据库的两个顶级函数：

-   queryForValue：这需要一些SQL和一些绑定并返回第一个值
-   update：这需要一些SQL和一些绑定并将其视为UPDATE语句

然后我们可以像使用任何普通委托一样使用它，并让我们的类自动由数据库支持：

```kotlin
class DatabaseUser(userId: String) {
    var name: String by DatabaseDelegate(
        "SELECT name FROM users WHERE userId = :id",
        "UPDATE users SET name = :value WHERE userId = :id",
        userId)
    var email: String by DatabaseDelegate(
        "SELECT email FROM users WHERE userId = :id",
        "UPDATE users SET email = :value WHERE userId = :id",
        userId)
}
```

## 5. 委派委托创建

**我们在Kotlin 1.4中拥有的另一个新特性是能够将我们的委托类的创建委托给另一个类**，这是通过实现PropertyDelegateProvider接口来实现的，该接口有一个方法来实例化某些东西以用作实际的委托。

我们可以使用它来执行一些围绕创建要使用的委托的代码-例如，记录正在发生的事情。我们还可以使用它来根据它所使用的属性动态选择我们将要使用的委托，例如，如果属性可为空，我们可能有不同的委托：

```kotlin
class DatabaseDelegateProvider<code class="language-scala" > < in R, T > (readQuery: String, writeQuery: String, id: Any)
: PropertyDelegateProvider<R, ReadWriteDelegate<R, T>> {
    override operator fun provideDelegate(thisRef: T, prop: KProperty<*>): ReadWriteDelegate<R, T> {
        if (prop.returnType.isMarkedNullable) {
            return NullableDatabaseDelegate(readQuery, writeQuery, id)
        } else {
            return NonNullDatabaseDelegate(readQuery, writeQuery, id)
        }
    }
}
```

这允许我们在每个委托中编写更简单的代码，因为他们只需要关注更有针对性的案例。在上面，我们知道NonNullDatabaseDelegate只会用于不能有null值的属性，所以我们不需要任何额外的逻辑来处理它。

## 6. 总结

属性委托是一种强大的技术，它允许你编写接管其他属性控制权的代码，并有助于在不同类之间轻松共享此逻辑。这允许看起来和感觉起来像常规属性访问的健壮的、可重用的逻辑。