## **一、简介**

[AssertJ](http://joel-costigliola.github.io/assertj/index.html)是一个为 Java 提供流畅断言的库。[您可以在此处](https://www.baeldung.com/introduction-to-assertj)和[此处](https://www.baeldung.com/assertJ-java-8-features)阅读更多相关信息。

最近，3.6.0 版本与两个小错误修复版本 3.6.1 和 3.6.2 一起发布。

今天，库的创建者[Joel Costigliola](https://github.com/joel-costigliola)和我们在一起，他会告诉你更多关于发布和未来计划的信息。

>   “我们正在努力让[AssertJ](http://joel-costigliola.github.io/assertj/)真正面向社区”

### **2. 版本 2.6.0 和 3.6.0 几乎同时发布。它们之间有什么区别？**

2.x 版本以 Java 7 为目标，而 3.x 以 Java 8 为目标。另一种理解方式是 3.x = 2.x + Java 8 特定功能。

### **3. 3.6.0/2.6.0 中出现的最显着的更改/添加是什么？**

2.6.0 最终具有不同的小功能，但没有大的添加。如果我必须选择，最有趣的是与抑制异常相关的那些：
– *[hasSuppresedException()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-2.6.0-hasSuppressedException)*
– *[hasNoSuppresedExceptions()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-2.6.0-hasNoSuppressedExceptions)*

3.6.0 还获得了一种检查数组/可迭代/映射条目元素上的多个断言的方法：

–*[全部满足（）](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-3.6.0-allSatisfy)*

– *[hasEntrySatisfying()](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-3.6.0-hasEntrySatisfying)*

### **4. 自 3.6.0 发布以来，出现了两个 Bugfix 版本（3.6.1、3.6.2）。您能告诉我们更多的信息吗？那里发生了什么以及需要解决什么问题？**

在 3.6.1 中，*filteredOn(Predicate)*只适用于*List*而不是*Iterable，*非常糟糕。

在 3.6.2 中，我们没有想到从 Java 8 默认的 getter 方法中提取属性，经过一些内部重构后发现它并没有开箱即用。

我问用户他们是否可以等待下一个版本，错误报告者告诉我他可以等待，但另一个用户想要它，所以我发布了一个新版本。**我们正在努力使[AssertJ](http://joel-costigliola.github.io/assertj/)真正面向社区**，因为发布一个版本很便宜（除了文档部分）我通常看不到任何发布问题。

### **5. 在开发最新版本时，您是否遇到过任何有趣的技术挑战？**

我将指出我在下一个版本 3.7.0 中遇到的问题，该版本应该在几周内发布。

Java 8 对“模棱两可”的方法签名很挑剔。我们添加了一个新的 assertThat 方法，它接受一个*ThrowingCallable*（一个简单的类，它是一个可抛出异常的*Callable*），结果是 Java 8 将它与另一个接受Iterable 的*assertThat方法混淆了**！*

这对我来说是最令人惊讶的，因为我看不出两者之间有任何歧义。

### **6. 您是否计划很快发布任何新的主要版本？任何将利用 Java 9 附加功能的东西？**

在接下来的几周/一个月内。**我们通常会尝试每隔几个月或在有主要添加时发布一次。**

加入 AssertJ 团队的 Pascal Schumacher 已经在 Java 9 上做了一些工作来检查兼容性，有一些东西不起作用，主要是那些依赖自省的东西，因为 Java 9 改变了访问规则。我们要做的是启动一个 4.x 分支，该分支将以 Java 9 为重点，遵循与 3.x vs 2.x 相同的策略，我们将拥有 4.x = 3.x + Java 9 特性。

**一旦 4.0 正式发布，我们可能会放弃 2.x 的积极开发**，但继续接受 PR，因为我们没有能力保持 3 个版本同步，我的意思是我们报告从 nx 版本到 n+1.x 的任何更改版本，因此在 2.x 中添加一些内容需要在 3.x 和 4.x 中同时报告，目前工作量太大。