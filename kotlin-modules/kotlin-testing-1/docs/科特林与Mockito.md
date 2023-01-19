## 一、简介

Kotlin 和 Java 携手并进。这意味着我们可以在 Kotlin 项目中利用大量现有的 Java 库。

在这篇简短的文章中，我们将了解如何在 Kotlin 中使用[Mockito进行模拟。](http://site.mockito.org/)如果你想了解有关该库的更多信息，[请查看这篇文章。](https://www.baeldung.com/mockito-annotations)

## 2.设置

首先，让我们创建一个[Maven](https://maven.apache.org/)项目并在pom.xml中添加[JUnit](https://search.maven.org/artifact/junit/junit)和[Mockito](https://search.maven.org/artifact/org.mockito/mockito-core)依赖项：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>3.3.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

我们还需要告诉 Maven 我们正在使用 Kotlin，以便它为我们编译源代码。查看[官方 Kotlin 文档](https://kotlinlang.org/docs/reference/using-maven.html)，了解有关如何在pom.xml中进行配置的更多信息。

## 3. 将Mockito与Kotlin结合使用

假设，我们有一个要测试的实现——LendBookManager 。此类依赖于尚未实现的名为BookService的服务：

```xml
interface BookService {
    fun inStock(bookId: Int): Boolean
    fun lend(bookId: Int, memberId: Int)
}

```

BookService在LendBookManager的实例化期间被注入，并在整个结帐方法中使用了两次，这是我们需要编写测试的方法：

```xml
class LendBookManager(val bookService:BookService) {
    fun checkout(bookId: Int, memberId: Int) {
        if(bookService.inStock(bookId)) {
            bookService.lend(bookId, memberId)
        } else {
            throw IllegalStateException("Book is not available")
        }
    }
}

```

如果无法模拟BookService ，则很难为该方法编写单元测试——这正是 Mockito 派上用场的地方。

只需两行代码，我们就可以创建BookService接口的模拟，并指示它在调用inStock()方法时返回一个固定值：

```xml
val mockBookService = Mockito.mock(BookService::class.java)
Mockito.`when`(mockBookService. inStock(100)).thenReturn(true)

```

这将强制mockBookService实例在使用参数 100 调用inStock()方法时返回true(请注意，我们必须使用反引号转义when()方法；这是必需的，因为when是Kotlin 语言中的[保留关键字](https://kotlinlang.org/docs/reference/control-flow.html#when-expression)).

然后我们可以在实例化期间将这个模拟实例传递给LendBookManager，调用我们想要测试的方法，并验证lend()方法作为操作的结果被调用：

```xml
val manager = LendBookManager(mockBookService)
manager.checkout(100, 1)		
Mockito.verify(mockBookService).lend(100, 1)

```

我们可以快速测试我们方法实现的其他逻辑路径，如果所需的书没有库存，它应该抛出异常：

```xml
@Test(expected = IllegalStateException::class)
fun whenBookIsNotAvailable_thenAnExceptionIsThrown() {
    val mockBookService = Mockito.mock(BookService::class.java)
    Mockito.`when`(mockBookService. inStock(100)).thenReturn(false)
    val manager = LendBookManager(mockBookService)
    manager.checkout(100, 1)
}

```

请注意，对于此测试，当询问 id 为 100 的书是否有货时，我们告诉 mockBookService返回false 。这应该会导致checkout()调用抛出IllegalStateException。

我们在@Test注解上使用了expected属性，表明我们希望这个测试抛出异常。

## 4. Mockito Kotlin库

[通过使用名为mockito-kotlin](https://github.com/nhaarman/mockito-kotlin/)的开源库，我们可以使我们的代码看起来更像 Kotlin 。这个库围绕它的方法包装了一些 Mockito 的功能，提供了一个更简单的 API：

```java
@Test
fun whenBookIsAvailable_thenLendMethodIsCalled() {
    val mockBookService : BookService = mock()
    whenever(mockBookService.inStock(100)).thenReturn(true)
    val manager = LendBookManager(mockBookService)
    manager.checkout(100, 1)
    verify(mockBookService).lend(100, 1)
}
```

它还提供了mock()方法的版本。使用此方法时，我们可以利用类型推断，因此我们可以在不传递任何其他参数的情况下调用该方法。

最后，这个库公开了一个新的whenever()方法，可以自由使用，而不需要像我们在使用 Mockito 的原生when()方法时那样需要反引号。

查看[他们的 wiki](https://github.com/nhaarman/mockito-kotlin/wiki/)以获取完整的增强列表。

## 5.总结

在这个快速教程中，我们了解了如何设置我们的项目以同时使用 Mockito 和 Kotlin，以及我们如何利用这种组合来创建模拟和编写有效的单元测试。