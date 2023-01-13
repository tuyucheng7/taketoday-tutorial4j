## 一、简介

[Promises](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise)是管理异步代码的绝佳方式，例如我们需要响应但愿意等待它可用的地方。

在本教程中，我们将了解[Kovenant](http://kovenant.komponents.nl/)如何将承诺引入 Kotlin。

## 2.什么是承诺？

在最基本的情况下，Promise 表示尚未发生的结果。例如，一段代码可能会为某些复杂的计算或某些网络资源的检索返回 Promise。该代码实际上承诺结果将可用，但它可能暂时不可用。

在许多方面，Promises 类似于已经成为核心 Java 语言一部分的[Futures 。](https://www.baeldung.com/java-future)然而，正如我们将看到的，Promises 更加灵活和强大，允许失败案例、链和其他组合。

## 3.Maven依赖

Kovenant 是一个标准的 Kotlin 组件，然后是与其他各种库一起工作的适配器模块。

在我们可以在项目中使用 Kovenant 之前，我们需要[添加正确的依赖项](https://search.maven.org/artifact/nl.komponents.kovenant/kovenant/3.3.0/pom)。Kovenant 使用[pom工件](https://stackoverflow.com/questions/16894032/how-to-use-poms-as-a-dependency-in-maven/16894086)使这变得容易：

```xml
<dependency>
    <groupId>nl.komponents.kovenant</groupId>
    <artifactId>kovenant</artifactId>
    <type>pom</type>
    <version>3.3.0</version>
</dependency>
```

在此 POM 文件中， Kovenant 包括几个不同的组合工作的组件。

还有一些模块可以与其他库一起工作或在其他平台上工作，例如 RxKotlin 或在 Android 上。完整的组件列表在[Kovenant 网站上](http://kovenant.komponents.nl/#getting-started)。

## 4.创造承诺

我们要做的第一件事是创建一个承诺。我们可以通过多种方式实现这一目标，但最终结果总是相同的：一个值代表对可能发生或可能尚未发生的结果的承诺。

### 4.1. 手动创建延迟操作

一种使用 Kovenant 的 Promise API 的方法是延迟一个动作。

我们可以使用 deferred<V, E> 函数手动推迟一个动作。这将返回一个 Deferred<V, E>类型的对象， 其中 V是预期的成功类型， E 是预期的错误类型：

```java
val def = deferred<Long, Exception>()
```

一旦我们创建了 Deferred<V, E>，我们就可以根据需要选择解析或拒绝它：

```java
try {
    def.resolve(someOperation())
} catch (e: Exception) {
    def.reject(e)
}
```

但是，我们只能在单个Deferred上执行其中一个，并且尝试再次调用其中一个将导致错误。

### 4 .2。从延迟操作中提取承诺

一旦我们创建了一个延迟动作，我们就可以从中提取一个 Promise<V, E>：

```java
val promise = def.promise
```

这个承诺是延迟操作的实际结果，在延迟被解决或拒绝之前它不会有值：

```java
val def = deferred<Long, Exception>()
try {
    def.resolve(someOperation())
} catch (e: Exception) {
    def.reject(e)
}
return def.promise
```

当此方法返回时，它将返回一个 promise 要么 resolved 要么 rejected ， 这取决于 someOperation的执行情况。

请注意，一个Deferred<V, E>包装了一个Promise<V, E>，我们可以根据需要多次提取这个承诺。每次调用Deferred.promise都会返回具有相同状态的相同Promise 。

### 4.3. 简单任务执行

大多数时候，我们希望通过简单地执行一些长时间运行的任务来创建Promise，类似于我们希望通过在Thread中执行长时间运行的任务来创建Future的方式。

Kovenant 有一个非常简单的方法来做到这一点，即使用 task<V> 函数。

我们通过提供要执行的代码块来调用它，Kovenant 将异步执行它并立即为结果返回 Promise<V, Exception>：

```java
val result = task {
    updateDatabase()
}
```

请注意，我们不需要实际指定通用边界，因为 Kotlin 可以从我们的块的返回类型自动推断出这些。

### 4.4. 懒惰的承诺代表

我们还可以使用 Promises 作为标准 [lazy()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/lazy.html)委托的替代品。这完全相同，但属性类型是 Promise<V, Exception>。

与 任务处理程序一样，这些在后台线程上进行评估并在适当的时候可用：

```java
val webpage: Promise<String, Exception> by lazyPromise { getWebPage("http://www.example.com") }
```

## 5. 对承诺做出反应

一旦我们掌握了 Promise，我们就需要能够用它做一些事情，最好是以反应式或事件驱动的方式。

 当我们使用 resolve方法完成Deferred<V, E>时 ，或者当我们的 task<V>成功完成时，承诺就成功实现了 。

或者，当 Deferred<V, E>使用 reject 方法完成时，或者当 task<V> 通过抛出异常完成时，它会失败。

承诺在他们的生命中只能实现一次，而第二次尝试这样做是错误的。

### 5.1. 承诺回调

一旦承诺被解决或拒绝，我们可以注册针对 Kovenant 承诺的回调。

如果我们希望在延迟操作成功时发生某些事情，我们可以使用 Promise 上的成功函数来注册回调：

```java
val promise = task { 
    fetchData("http://www.example.com") 
}

promise.success { response -> println(response) }
```

如果我们希望在延迟操作失败时发生某些事情，我们可以以相同的方式使用 fail ：

```java
val promise = task { 
    fetchData("http://www.example.com") 
}

promise.fail { error -> println(error) }
```

或者，我们可以使用 Promise.always注册一个回调，无论承诺是否成功都会被触发：

```java
val promise = task {
    fetchData("http://www.example.com")
}
promise.always { println("Finished fetching data") }
```

Kovenant 也让我们将它们链接在一起，这意味着如果我们愿意，我们可以更简洁地编写代码：

```java
task {
    fetchData("http://www.example.com")
} success { response ->
    println(response)
} fail { error ->
    println(error)
} always {
    println("Finished fetching data")
}
```

有时，我们希望根据承诺的状态发生多件事情，我们可以单独注册每件事情。

当然，我们可以用与上面相同的方式将它们链接起来，尽管这种情况不太常见，因为我们可能只有一个回调来完成所有工作。

```java
val promise = task {
    fetchData("http://www.example.com")
}

promise.success { response ->
    logResponse(response)
} success { response ->
    renderData(response)
} success { response ->
    updateStatusBar(response)
}
```

所有适当的回调都按照我们列出的顺序依次执行。

这包括不同类型回调之间的交错：

```java
task {
    fetchData("http://www.example.com")
} success { response ->
    // always called first on success
} fail { error ->
    // always called first on failure
} always {
    // always called second regardless
} success { response ->
    // always called third on success
} fail { error ->
    // always called third on failure
}
```

### 5.2. 链接承诺

一旦我们得到一个承诺，我们就可以将它与其他承诺链接起来，根据结果触发额外的工作。

这允许我们获取一个 promise 的输出，对其进行调整——可能作为另一个长期运行的过程——并返回另一个 promise：

```java
task {
    fetchData("http://www.example.com")
} then { response -> 
    response.data
} then { responseBody ->
    sendData("http://archive.example.com/savePage", responseBody)
}
```

如果此链中的任何步骤失败，则整个链都会失败。这使我们能够缩短链中无意义的步骤，并且仍然拥有干净、易于理解的代码：

```java
task {
    fetchData("http://bad.url") // fails
} then { response -> 
    response.data // skipped, due to failure
} then { body -> 
    sendData("http://good.url", body) // skipped, due to failure
} fail { error ->
    println(error) // called, due to failure
}
```

此代码尝试从错误的 URL 加载数据，失败，并立即掉落到 失败回调。

这类似于我们将其包装在 try/catch 块中，除了我们可以为相同的错误条件注册多个不同的处理程序。

### 5.3. 阻止承诺结果

有时，我们需要同步地从承诺中获取价值。

Kovenant 使用 get 方法使这成为可能，如果承诺已成功实现，该方法将返回值，如果未成功解决则抛出异常。

或者，在 promise 尚未实现的情况下，这将阻塞直到它：

```java
val promise = task { getWebPage() }

try {
    println(promise.get())
} catch (e: Exception) {
    println("Failed to get the web page")
}
```

这里存在承诺永远不会实现的风险，因此对 get()的调用将永远不会返回。

如果这是一个问题，那么我们可以更加谨慎并使用 isDone、 isSuccess 和 isFailure来检查承诺的状态：

```java
val promise = doSomething()
println("Promise is done? " + promise.isDone())
println("Promise is successful? " + promise.isSuccess())
println("Promise failed? " + promise.isFailure())
```

### 5.4. 超时阻塞

目前，Kovenant 在等待这样的承诺时不支持超时。不过，此功能有望在[未来的版本](http://kovenant.komponents.nl/roadmap/#v340)中出现 。

然而，我们可以通过一些努力来实现这一点：

```java
fun <T> timedTask(millis: Long, body: () -> T) : Promise<T?, List<Exception>> {
    val timeoutTask = task {
        Thread.sleep(millis)
        null     
    }
    val activeTask = task(body = body)
    return any(activeTask, timeoutTask)
}
```

(请注意，这使用了 any()调用，我们将在稍后讨论。)

然后我们可以调用此代码来创建任务并为其提供超时。如果超时到期，那么承诺将立即解析为 null：

```java
timedTask(5000) {
    getWebpage("http://slowsite.com")
}
```

## 6.取消承诺

Promises 通常表示异步运行的代码，最终会产生已解决或被拒绝的结果。

有时我们决定我们根本不需要结果。

在那种情况下，我们可能想要取消承诺而不是让它继续使用资源。

每当我们使用 task 或 then 来生成 Promise 时，这些都是默认可取消的。但是你仍然需要将其转换为 CancelablePromise才能执行此操作， 因为 API 会返回一个没有 取消 方法的超类型：

```java
val promise = task { downloadLargeFile() }
(promise as CancelablePromise).cancel(UserGotBoredException())
```

或者，如果我们使用 deferred 创建一个 promise，那么除非我们首先提供一个“on-cancel”回调，否则这些是不可取消的：

```java
val deferred = deferred<Long, String> { e ->
    println("Deferred was cancelled by $e")
}
deferred.promise.cancel(UserGotBoredException())
```

当我们调用cancel时，其结果与 promise 被任何其他方式 拒绝的结果非常相似，例如调用Deferred.reject或 任务块抛出异常。

主要区别在于 取消将主动中止运行 promise 的线程(如果有的话)，并在该线程内引发 InterruptedException 。

传递给 cancel的值是 promise 被拒绝的值。这将提供给你可能已设置的任何 失败处理程序，其方式与任何其他形式的拒绝承诺完全相同。

现在，Kovenant 声明取消是尽力而为的请求。这可能意味着工作从一开始就没有安排好。或者，如果它已经在执行，那么它将尝试中断线程。

## 7. 结合承诺

现在，假设我们有许多异步任务在运行，我们想等待它们全部完成。或者我们想对第一个完成的那个做出反应。

Kovenant 支持使用多个 promise并以各种方式组合它们。

### 7.1. 等待一切成功

当我们需要等待所有的 promise 完成后才能做出反应时，我们可以使用 Kovenant 的 all：

```java
all(
    task { getWebsite("http://www.example.com/page/1") },
    task { getWebsite("http://www.example.com/page/2") },
    task { getWebsite("http://www.example.com/page/3") }
) success { websites: List<String> ->
    println(websites)
} fail { error: Exception ->
    println("Failed to get website: $error")
}
```

all 会将几个 promise 组合在一起并产生一个新的 promise。这个新的 promise解析为所有成功值的列表，或者因其中任何一个抛出的第一个错误而失败。

这意味着所有提供的承诺必须具有完全相同的类型Promise<V , E>并且组合采用类型Promise<List<V>, E>。

### 7.2. 等待任何成功

或者，也许我们只关心第一个完成的，为此，我们有 ：

```java
any(
    task { getWebsite("http://www.example.com/page/1") },
    task { getWebsite("http://www.example.com/page/2") },
    task { getWebsite("http://www.example.com/page/3") }
) success { result ->
    println("First web page loaded: $result")
} fail { errors ->
    println("All web pages failed to load: $errors)
}
```

由此产生的 promise 与我们在all中看到的相反 。如果任何一个提供的承诺成功解决，它就成功了，如果每个提供的承诺都失败了，它就失败了。

此外，这意味着成功采用单个 Promise<V, E>而 失败 采用 Promise<V, List<E>>。

如果任何一个承诺返回成功的结果，Kovenant 将尝试取消剩余的未解决的承诺。

### 7.3. 组合不同类型的 Promise

现在，假设我们有一个all 情况，但每个 promise 都是不同的类型。这是 所有人都支持的更一般的情况，但 Kovenant 也支持这一点。

此功能由 kovenant-combine 库提供，而不是由 我们目前使用的kovenant-core提供。不过，因为我们添加了pom依赖项，所以两者都可用。

为了组合任意数量的不同类型的承诺，我们可以使用 combine：

```java
combine(
    task { getMessages(userId) },
    task { getUnreadCount(userId) },
    task { getFriends(userId) }
) success { 
  messages: List<Message>, 
  unreadCount: Int, 
  friends: List<User> ->
    println("Messages in inbox: $messages")
    println("Number of unread messages: $unreadCount")
    println("List of users friends: $friends")
}
```

成功的结果是组合结果的元组。但是，promise 必须具有相同的失败类型，因为它们不会合并在一起。

kovenant-combine还为通过and 扩展方法 恰好将两个 promise 组合在一起提供特殊支持 。最终结果与在恰好两个 promise 上使用combine完全相同，但代码可读性更高。

和以前一样，在这种情况下类型不需要匹配：

```java
val promise = 
  task { computePi() } and 
  task { getWebsite("http://www.example.com") }

promise.success { pi, website ->
    println("Pi is: $pi") 
    println("The website was: $website") 
}
```

## 8. 测试承诺

Kovenant 被特意设计成一个异步库。它在后台线程中运行任务，并在任务完成时提供结果。

这对我们的生产代码来说很棒，但会使测试变得更加复杂。如果我们正在测试一些本身使用 promises 的代码，这些 promises 的异步性质可能会使测试变得复杂，在最坏的情况下变得不可靠。

例如，假设我们要测试一个方法，其返回类型包含一些异步填充的属性：

```java
@Test
fun testLoadUser() {
    val user = userService.loadUserDetails("user-123")
    Assert.assertEquals("Test User", user.syncName)
    Assert.assertEquals(5, user.asyncMessageCount) 
}
```

这是一个问题，因为asyncMessageCount可能不会在调用断言时填充。

为此，我们可以将 Kovenant 配置为使用测试模式。这将导致一切都是同步的。

它还为我们提供了一个在出现任何问题时触发的回调，我们可以在其中处理这个意外错误：

```java
@Before 
fun setupKovenant() {
    Kovenant.testMode { error ->
        Assert.fail(error.message)
    }
}
```

此测试模式是全局设置。 一旦我们调用它，它就会影响由测试套件创建的所有 Kovenant 承诺。通常情况下，我们会从 @Before 注解的方法中调用它，以确保所有测试都以相同的方式运行。

请注意，目前无法关闭测试模式，它会在全球范围内影响 Kovenant。因此，当我们还想测试 promises 的异步性质时，我们需要小心使用它。

## 9.总结

在本文中，我们展示了 Kovenant 基础知识以及一些关于 promise 架构的基础知识。具体来说，我们讨论了 deferred 和 task，注册回调，以及链接、取消和组合承诺。

然后，我们结束了讨论测试异步代码。

这个库可以为我们做更多的事情，包括更复杂的核心功能以及与其他库的交互。