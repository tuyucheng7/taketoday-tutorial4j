## 1. 简介

在使用遗留代码库、使用外部库或针对框架进行集成时，我们经常会遇到需要在不同对象或数据结构之间进行映射的用例。

**在本教程中，我们将了解如何使用内置的Kotlin功能轻松实现这一目标**。

## 2. 简单的扩展函数

让我们使用以下示例：我们有一个类User，它可能是我们核心域中的一个类，它也可能是我们从关系数据库加载的实体。

```kotlin
data class User(
    val firstName: String,
    val lastName: String,
    val street: String,
    val houseNumber: String,
    val phone: String,
    val age: Int,
    val password: String)
```

现在我们想提供对这些数据的不同看法，我们决定将此类称为UserView，我们可以想象它被用作从Web控制器发送的响应，虽然它代表我们域中的相同数据，但某些字段是我们User类字段的集合，而某些字段只是具有不同的名称：

```kotlin
data class UserView(
    val name: String,
    val address: String,
    val telephone: String,
    val age: Int
)
```

我们现在需要的是一个映射函数，它将映射User -> UserView，由于UserView在我们应用程序的外层，我们不想将此功能添加到我们的User类中，我们也不想破坏我们的User类的封装并使用帮助类来访问我们的User对象并提取其数据，以创建UserView对象。

幸运的是，Kotlin提供了一种称为[扩展函数](https://kotlinlang.org/docs/reference/extensions.html)的语言特性，我们可以在我们的User类上定义一个扩展函数，并使其只能在我们定义它的包范围内访问：

```kotlin
fun User.toUserView() = UserView(
    name = "$firstName $lastName",
    address = "$street $houseNumber",
    telephone = phone,
    age = age
)
```

让我们在测试中使用这个函数来感受一下如何使用它：

```kotlin
class UserTest {

    @Test
    fun `maps User to UserResponse using extension function`() {
        val p = buildUser()
        val view = p.toUserView()
        assertUserView(view)
    }

    private fun buildUser(): User {
        return User(
            "Java",
            "Duke",
            "Javastreet",
            "42",
            "1234567",
            30,
            "s3cr37"
        )
    }

    private fun assertUserView(pr: UserView) {
        assertAll(
            { assertEquals("Java Duke", pr.name) },
            { assertEquals("Javastreet 42", pr.address) },
            { assertEquals("1234567", pr.telephone) },
            { assertEquals(30, pr.age) }
        )
    }
}
```

## 3. Kotlin反射特性

虽然上面的示例非常简单(因此推荐用于大多数用例)，但它仍然涉及一些样板代码，如果我们有一个包含很多字段(可能有数百个)的类，并且其中大部分必须映射到目标类中具有相同名称的字段怎么办？

在这种情况下，我们可以考虑使用[Kotlin的反射](https://kotlinlang.org/docs/reference/reflection.html)特性来避免编写大部分映射代码。

使用反射的映射函数如下所示：

```kotlin
fun User.toUserViewReflection() = with(::UserView) {
    val propertiesByName = User::class.memberProperties.associateBy { it.name }
    callBy(parameters.associate { parameter ->
        parameter to when (parameter.name) {
            UserView::name.name -> "$firstName $lastName"
            UserView::address.name -> "$street $houseNumber"
            UserView::telephone.name -> phone
            else -> propertiesByName[parameter.name]?.get(this@toUserViewReflection)
        }
    })
}
```

我们通过使用Kotlin with()函数将UserView默认构造函数用作方法调用接收器，在提供给with()的lambda函数中，我们使用User::class.memberProperties.associateBy{ it.name }使用反射获取成员属性的Map(以成员名称为键，成员属性为值)。

接下来，我们使用自定义参数映射调用UserView构造函数。在lambda内部，我们使用when关键字提供了一个条件映射。

一个有趣的事实是，我们可以映射我们使用反射检索的实际参数名称，例如UserView::name.name而不是简单的Strings，**这意味着我们可以在这里完全利用Kotlin编译器**，在重构的情况下帮助我们，而不用担心我们的代码可能会崩溃。

我们对参数名称、地址和电话有一些特殊的映射，而我们对每个其他字段使用默认的基于名称的映射。

虽然基于反射的方法乍一看似乎非常有趣，但请记住，这会给代码库带来额外的复杂性，并且使用反射可能会对运行时性能产生负面影响。

## 4. 总结

我们已经看到，我们可以使用内置的Kotlin语言功能轻松解决简单的数据映射用例，虽然手动编写映射代码对于简单的用例来说很好，但我们也可以使用反射编写更复杂的解决方案。