## 1. 简介

[AssertJ](http://joel-costigliola.github.io/assertj/index.html)是一个为Java提供流式断言的库。你可以在[此处](https://www.baeldung.com/introduction-to-assertj)和[此处](https://www.baeldung.com/assertJ-java-8-features)阅读更多相关信息。

最近，3.6.0版本与两个小错误修复版本3.6.1和3.6.2一起发布。

今天，库的创建者[Joel Costigliola](https://github.com/joel-costigliola)和我们在一起，将告诉你有关发布和未来计划的更多信息。

>   “我们正在努力让[AssertJ](http://joel-costigliola.github.io/assertj/)真正面向社区”

## 2. 版本2.6.0和3.6.0几乎同时发布。它们之间有什么区别？

2.x版本面向Java 7，而3.x版本面向Java 8。另一种理解方式是3.x = 2.x + Java 8特定功能。

## 3. 3.6.0/2.6.0中出现的最显著的更改/添加是什么？

2.6.0最终具有不同的小功能，但没有大的添加。如果我必须选择，最有趣的是与抑制异常相关的那些：
– [hasSuppressedException()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-2.6.0-hasSuppressedException)
– [hasNoSuppressedExceptions()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-2.6.0-hasNoSuppressedExceptions)

3.6.0还提供了一种检查数组/Iterable/Map条目元素上的多个断言的方法：

– [allSatisfy()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-3.6.0-allSatisfy)
– [hasEntrySatisfying()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-3.6.0-hasEntrySatisfying)

## 4. 自3.6.0发布以来，出现了两个错误修复版本(3.6.1、3.6.2)。你能告诉我们更多的信息吗？那里发生了什么以及需要解决什么问题？

在3.6.1中，filteredOn(Predicate)只适用于List而不是Iterable，非常糟糕。

在3.6.2中，我们没有想到从Java 8默认的getter方法中提取属性，经过一些内部重构后发现它并没有开箱即用。

我问用户他们是否可以等待下一个版本，错误报告者告诉我他可以等待，但另一个用户想要它，所以我发布了一个新版本。我们正在努力使[AssertJ](http://joel-costigliola.github.io/assertj/)真正面向社区，因为发布一个版本很便宜(除了文档部分)，我通常看不到任何发布问题。

## 5. 在开发最新版本时，你是否遇到过任何有趣的技术挑战？

我将指出我在下一个版本3.7.0中遇到的问题，该版本应该在几周内发布。

Java 8对“模棱两可”的方法签名很挑剔。我们添加了一个新的assertThat方法，它接收一个ThrowingCallable(一个简单的类，它是一个可抛出异常的Callable)，结果是Java 8将它与另一个接收Iterable的assertThat方法混淆了！

这对我来说是最令人惊讶的，因为我看不出两者之间有任何歧义。

## 6. 你是否计划很快发布任何新的主要版本？任何将利用Java 9附加功能的东西？

在接下来的几周/一个月内。我们通常会尝试每隔几个月或在有主要添加时发布一次。

加入AssertJ团队的Pascal Schumacher已经在Java 9上做了一些工作来检查兼容性，有一些东西不起作用，主要是那些依赖自省的东西，因为Java 9改变了访问规则。我们要做的是启动一个4.x分支，该分支将以Java 9为重点，遵循与3.x与2.x相同的策略，我们将拥有4.x = 3.x + Java 9特性。

一旦4.0正式发布，我们可能会放弃2.x的积极开发，但继续接受PR，因为我们没有能力保持3个版本同步，我的意思是我们报告从nx版本到n+1.x的任何更改版本，因此在2.x中添加一些内容需要在3.x和4.x中同时报告，目前工作量太大。