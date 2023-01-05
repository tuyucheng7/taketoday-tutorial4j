## 1. 概述

RxJava提供了各种运算符来将observable发出的项目转换为其他observables。两个最流行的运算符是flatMap和switchMap。对于响应式编程的初学者来说，两者之间的区别通常很难理解。

有关RxJava的介绍，请参阅[这篇文章](https://www.baeldung.com/rx-java)。

在本教程中，我们将通过一个简单的示例来了解其中的区别。

## 2. flatMap

flatMap运算符使用提供的函数将从源可观察对象返回的每个项目转换为独立的可观察对象，然后将所有可观察对象合并为单个可观察对象。合并observable的顺序不能保证与源Observable中的相同。

我们以搜索引擎为例。假设我们想在输入单词的每个字符后立即显示搜索结果：

为简单起见，我们将搜索查询输入作为单词列表。

此外，我们总是为每个单词返回两个搜索结果。

```java
// given
List<String> actualOutput = new ArrayList<>();
TestScheduler scheduler = new TestScheduler();
List<String> keywordToSearch = Arrays.asList("b", "bo", "boo", "book", "books");

// when
Observable.fromIterable(keywordToSearch)
    .flatMap(s -> Observable.just(s + " FirstResult", s + " SecondResult")
        .delay(10, TimeUnit.SECONDS, scheduler))
    .toList()
    .doOnSuccess(s -> actualOutput.addAll(s))
    .subscribe();

scheduler.advanceTimeBy(1, TimeUnit.MINUTES);

// then
assertThat(actualOutput, hasItems("b FirstResult", "b SecondResult",
    "boo FirstResult", "boo SecondResult",
    "bo FirstResult", "bo SecondResult",
    "book FirstResult", "book SecondResult",
    "books FirstResult", "books SecondResult"));
```

请注意，每次运行的顺序并不总是相同的。

## 3. switchMap

switchMap操作符类似于flatMap，除了它只保留最新的observable的结果，丢弃以前的结果。

让我们改变我们的要求，因为我们只想获得最终的全格式单词(在本例中为“books”)而不是部分查询字符串的搜索结果。为此，我们可以使用switchMap。

如果我们只是在上面的代码示例中将flatMap替换为switchMap，那么以下断言将是有效的：

```java
assertEquals(2, actualOutput.size());
assertThat(actualOutput, hasItems("books FirstResult", "books SecondResult"));
```

正如我们在这里看到的，我们只得到了一个包含来自源observable的最新输入项的observable。所有以前的结果都被丢弃了。

## 4. 总结

总而言之，switchMap与flatMap的不同之处在于，它只保留将提供的函数应用于源Observable发出的最新项的输出，而flatMap保留所有结果并以交错的方式返回它们，而不保证顺序。