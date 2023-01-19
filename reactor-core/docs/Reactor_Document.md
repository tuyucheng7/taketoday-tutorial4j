# Reactor 3参考指南

## 1. 关于文档

本节简要概述了Reactor参考文档。你无需以线性方式阅读本指南，每一个章节都是独立的，尽管它们经常引用其他章节中的内容。

### 1.1 最新版本和版权声明

Reactor参考指南以HTML文档的形式提供，最新副本位于https://projectreactor.io/docs/core/release/reference/index.html

本文档的副本可以供你自己使用和分发给其他人，前提是你不对此类副本收取任何费用，并且进一步前提是每份副本都包含本版权声明，无论是印刷版还是电子版。

### 1.2 为文档做贡献

参考指南是用[Asciidoc](https://asciidoctor.org/docs/asciidoc-writers-guide/)编写的，你可以在https://github.com/reactor/reactor-core/tree/main/docs/asciidoc找到它的来源。

如果你有改进或建议，我们将很高兴收到你的Pull Request！

我们建议你签出存储库的本地副本，以便你可以通过运行asciidoctorgradle任务并检查渲染来生成文档。有些部分依赖于包含的文件，因此GitHub渲染并不总是完整的。

|      | 为了便于文档编辑，大多数部分的末尾都有一个链接，可以直接在 GitHub 上打开该部分的主要源文件的编辑 UI。这些链接仅出现在本参考指南的HTML5版本中。它们如下所示：[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/aboutDoc.adoc)到[关于文档](https://projectreactor.io/docs/core/release/reference/#about-doc)。 |
| ---- | ------------------------------------------------------------ |


### 1.3 获得帮助

你可以通过多种方式使用Reactor寻求帮助：

-   [在Gitter](https://gitter.im/reactor/reactor)上与社区联系。
-   在stackoverflow.com上提问[project-reactor](https://stackoverflow.com/tags/project-reactor)。
-   报告Github问题中的错误，我们密切监视以下存储库：[reactor-core](https://github.com/reactor/reactor-core/issues)(涵盖基本功能)和[reactor-addons](https://github.com/reactor/reactor-addons/issues)(涵盖reactor-test和适配器问题)。

|      | 所有的Reactor都是开源的，[包括这个文档](https://github.com/reactor/reactor-core/tree/main/docs/asciidoc)。如果你发现文档存在问题或想要改进它们，请[参与其中](https://github.com/reactor/.github/blob/main/CONTRIBUTING.md)。 |
| :--: | ------------------------------------------------------------ |

### 1.4 从这往哪儿走

-   如果你想直接跳入代码，请前往[入门。](https://projectreactor.io/docs/core/release/reference/#getting-started)
-   但是，如果你是响应式编程的新手，你可能应该从响应式编程[简介](https://projectreactor.io/docs/core/release/reference/#intro-reactive)开始。
-   如果你熟悉Reactor概念并且只是在寻找适合该工作的工具但想不出相关的运算符，请尝试[我需要哪个运算符？](https://projectreactor.io/docs/core/release/reference/#which-operator)附录。
-   为了更深入地了解Reactor的核心特性，请前往[Reactor 核心特性](https://projectreactor.io/docs/core/release/reference/#core-features)学习：
    -   有关Reactor的响应类型的更多信息，请参见[Flux0-N 项的异步序列](https://projectreactor.io/docs/core/release/reference/#flux)和[Mono0-1 异步结果](https://projectreactor.io/docs/core/release/reference/#mono)部分。
    -   如何使用[调度程序](https://projectreactor.io/docs/core/release/reference/#schedulers)切换线程上下文。
    -   如何处理“[处理错误”](https://projectreactor.io/docs/core/release/reference/#error.handling)部分中的错误。
-   单元测试？是的，这个reactor-test项目是可能的！请参阅[测试](https://projectreactor.io/docs/core/release/reference/#testing)。
-   [以编程方式创建序列](https://projectreactor.io/docs/core/release/reference/#producing)提供了一种更高级的创建响应源的方法。
-   [高级功能和概念](https://projectreactor.io/docs/core/release/reference/#advanced)中涵盖了其他高级主题。

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/aboutDoc.adoc)到“[关于文档](https://projectreactor.io/docs/core/release/reference/#about-doc)”

## 2. 入门

本节包含可以帮助你开始使用Reactor的信息。它包括以下部分：

-   [介绍Reactor](https://projectreactor.io/docs/core/release/reference/#getting-started-introducing-reactor)
-   [先决条件](https://projectreactor.io/docs/core/release/reference/#prerequisites)
-   [了解BOM和版本控制方案](https://projectreactor.io/docs/core/release/reference/#getting-started-understanding-bom)
-   [获得Reactor](https://projectreactor.io/docs/core/release/reference/#getting)

### 2.1 介绍Reactor

Reactor是JVM的完全非阻塞响应式编程基础，具有高效的需求管理(以管理“背压”的形式)。它直接与Java 8函数式API集成，特别是CompletableFuture、Stream和 Duration。它提供了可组合的异步序列API：Flux(用于[N]个元素)和Mono(用于[0|1]个元素)，并广泛实现了[Reactive Streams](https://www.reactive-streams.org/)规范。

Reactor还支持与reactor-netty项目的非阻塞进程间通信，Reactor Netty适用于微服务架构，为HTTP(包括Websockets)、TCP和UDP提供背压就绪的网络引擎。完全支持响应式编码和解码。

### 2.2 先决条件

Reactor Core在Java 8上面运行。

它对有org.reactivestreams:reactive-streams:1.0.3传递依赖。

|      | 安卓支持Reactor 3 不正式支持或针对 Android(如果这种支持是强烈要求，请考虑使用 RxJava 2)。但是，它应该适用于 Android SDK 26 (Android O) 及更高版本。启用脱糖功能后，它可能会在 Android SDK 21 (Android 5.0) 及更高版本上正常工作。见https://developer.android.com/studio/write/java8-support#library-desugaring我们愿意以最大努力的方式评估有利于 Android 支持的更改。但是，我们无法做出保证。每个决定都必须根据具体情况做出。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 2.3 了解BOM和版本控制方案

Reactor 3使用BOM模型(因为reactor-core 3.0.4，带有Aluminium发布序列)。此精选列表将旨在协同工作的工件分组，提供相关版本，尽管这些工件中可能存在不同的版本控制方案。

请注意，版本控制方案在 3.3.x 和 3.4.x(镝和铕)之间发生了变化。

工件遵循版本控制方案，MAJOR.MINOR.PATCH-QUALIFIER而 BOM 使用受 CalVer 启发的方案进行版本控制YYYY.MINOR.PATCH-QUALIFIER，其中：

-   MAJOR是当前一代的 Reactor，每一代都可以对项目结构带来根本性的变化(这可能意味着更重要的迁移工作)
-   YYYY是给定发布周期中第一个 GA 发布的年份(如 3.4.0 对应 3.4.x)
-   .MINOR是一个从 0 开始的数字，随着每个新的发布周期递增
    -   在项目的情况下，它通常反映更广泛的变化，并且可以表明适度的迁移工作
    -   在 BOM 的情况下，它允许区分发布周期，以防两个在同一年首次发布
-   .PATCH是一个从 0 开始的数字，随着每个服务版本递增
-   -QUALIFIER是一个文本限定符，在 GA 版本中被省略(见下文)

因此，遵循该约定的第一个发布周期是2020.0.x代号Europium。该方案按顺序使用以下限定符(注意使用破折号分隔符)：

-   -M1.. -M9: 里程碑(我们预计每个服务版本不会超过 9 个)
-   -RC1.. -RC9: 候选版本(我们预计每个服务版本不会超过 9 个)
-   -SNAPSHOT: 快照
-   没有GA版本的限定符

|      | 快照在上面的顺序中显示得更高，因为从概念上讲，它们始终是任何给定 PATCH 的“最新预发布”。即使 PATCH 周期的第一个部署工件将始终是 -SNAPSHOT，但名称相似但更新的快照也将在例如之后发布。里程碑或发布候选之间。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

每个发布周期也有一个代号，与之前基于代号的方案保持一致，可用于更非正式地引用它(如在讨论、博客文章等中......)。代号代表传统上的 MAJOR.MINOR 数字。它们(大部分)来自[元素周期表](https://en.wikipedia.org/wiki/Periodic_table#Overview)，按字母顺序递增。

|      | 直到 Dysprosium 之前，BOM 都是使用发布火车方案进行版本控制的，其中代号后跟限定符，限定符略有不同。例如：Aluminium-RELEASE(第一个 GA 版本，现在类似于 YYYY.0.0)、Bismuth-M1、Californium-SR1(服务版本现在类似于 YYYY.0.1)、Dysprosium-RC1、Dysprosium-BUILD-SNAPSHOT (在每个补丁之后，我们会回到相同的快照版本。现在类似于 YYYY.0.X-SNAPSHOT 所以我们每个补丁获得 1 个快照) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 2.4 获得Reactor

如前所述[，](https://projectreactor.io/docs/core/release/reference/#getting-started-understanding-bom)在你的核心中使用Reactor的最简单方法是使用 BOM 并将相关依赖项添加到你的项目中。请注意，当你添加此类依赖项时，你必须省略版本，以便从 BOM 中获取版本。

但是，如果你想强制使用特定工件的版本，你可以像往常一样在添加依赖项时指定它。你还可以完全放弃 BOM，并通过其工件版本指定依赖关系。

|      | 从这个版本(reactor-core 3.4.24)开始，相关的发布序列行中最新的稳定 BOM 是2020.0.24，这就是下面的片段中使用的。从那时起可能会有更新的版本(包括快照、里程碑和新的发布火车线)，请参阅https://projectreactor.io/docs了解最新的工件和 BOM。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 2.4.1 Maven安装

Maven 原生支持 BOM 概念。首先，你需要通过将以下代码段添加到你的 中来导入 BOM pom.xml：

```xml
<dependencyManagement> 
    <dependencies>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-bom</artifactId>
            <version>2020.0.24</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

|      | 注意dependencyManagement标签。这是对常规dependencies部分的补充。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

dependencyManagement如果你的 pom 中已经存在顶部部分 ( )，请仅添加内容。

接下来，像往常一样将你的依赖项添加到相关的响应器项目中，除了没有 <version>，如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-core</artifactId> 
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId> 
        <scope>test</scope>
    </dependency>
</dependencies>
```

|      | 对核心库的依赖。                               |
| ---- | ---------------------------------------------- |
|      | 这里没有版本标签。                             |
|      | reactor-test提供对响应流进行单元测试的工具。 |

#### 2.4.2 Gradle安装

在 5.0 版本之前，Gradle 没有对 Maven BOM 的核心支持，但你可以使用 Spring 的 [gradle-dependency-management](https://github.com/spring-gradle-plugins/dependency-management-plugin) 插件。

首先，从 Gradle 插件门户应用插件，如下所示：

```groovy
plugins {
    id "io.spring.dependency-management" version "1.0.7.RELEASE" 
}
```

|      | 在撰写本文时，1.0.7.RELEASE 是该插件的最新版本。检查更新。 |
| ---- | ---------------------------------------------------------- |
|      |                                                            |

然后用它来导入BOM，如下：

```groovy
dependencyManagement {
     imports {
          mavenBom "io.projectreactor:reactor-bom:2020.0.24"
     }
}
```

最后给你的项目添加一个依赖，不带版本号，如下：

```groovy
dependencies {
     implementation 'io.projectreactor:reactor-core' 
}
```

|      | 该版本没有第三个:单独的部分。它取自 BOM。 |
| ---- | ------------------------------------------- |
|      |                                             |

从 Gradle 5.0 开始，你可以使用对 BOM 的原生 Gradle 支持：

```groovy
dependencies {
     implementation platform('io.projectreactor:reactor-bom:2020.0.24')
     implementation 'io.projectreactor:reactor-core' 
}
```

|      | 该版本没有第三个:单独的部分。它取自 BOM。 |
| ---- | ------------------------------------------- |
|      |                                             |

#### 2.4.3 里程碑和快照

里程碑和开发人员预览通过 Spring Milestones 存储库而不是 Maven Central 分发。要将其添加到你的构建配置文件中，请使用以下代码段：

示例 1. Maven 中的里程碑

```xml
<repositories>
	<repository>
		<id>spring-milestones</id>
		<name>Spring Milestones Repository</name>
		<url>https://repo.spring.io/milestone</url>
	</repository>
</repositories>
```

对于 Gradle，使用以下代码段：

示例 2. Gradle 中的里程碑

```groovy
repositories {
  maven { url 'https://repo.spring.io/milestone' }
  mavenCentral()
}
```

同样，快照也可以在单独的专用存储库中使用，如以下示例所示：

示例 3. Maven 中的 -SNAPSHOTs

```xml
<repositories>
	<repository>
		<id>spring-snapshots</id>
		<name>Spring Snapshot Repository</name>
		<url>https://repo.spring.io/snapshot</url>
	</repository>
</repositories>
```

示例 4. Gradle 中的 -SNAPSHOT

```groovy
repositories {
  maven { url 'https://repo.spring.io/snapshot' }
  mavenCentral()
}
```

### 2.5 支持和政策

下面的条目是镜像https://github.com/reactor/.github/blob/main/SUPPORT.adoc

#### 2.5.1. 你有问题吗？

|      | 先搜索 Stack Overflow；必要时讨论 |
| ---- | --------------------------------- |
|      |                                   |

如果你不确定为什么某些事情不工作或想知道是否有更好的方法，请先检查Stack Overflow，如有必要，开始讨论。在我们为此目的监控的标签中使用相关标签：

-   [reactor-netty](https://stackoverflow.com/questions/tagged/reactor-netty)针对特定的响应堆网络问题
-   [project-reactor](https://stackoverflow.com/questions/tagged/project-reactor)对于一般响应堆问题

如果你更喜欢实时讨论，我们还有一些Gitter 频道：

-   [reactor](https://gitter.im/reactor/reactor)是历史上最活跃的一个，大多数社区都可以提供帮助
-   [reactor-core](https://gitter.im/reactor/reactor-core)旨在围绕图书馆的内部工作进行更高级的精确讨论
-   [reactor-netty](https://gitter.im/reactor/reactor-netty)旨在解决特定于网络的问题

请参阅每个项目的 README 以获取潜在的其他信息来源。

我们通常不鼓励为问题打开 GitHub 问题，而是支持上述两个渠道。

#### 2.5.2 我们的弃用政策

在处理弃用时，给定一个版本A.B.C，我们将确保：

-   版本中引入的弃用A。B. 0将不早于version被删除A。B+1.0
-   版本中引入的弃用A。B. 1+将不早于version被删除A。B+2.0
-   我们将努力在弃用的 javadoc 中提及以下内容：
    -   删除的目标最低版本
    -   指向已弃用方法的替换的指针
    -   不推荐使用方法的版本

|      | 该政策于 2021 年 1 月正式生效，适用于 BOM 和较新版本系列中的所有模块，2020.0以及. DysprosiumDysprosium-SR15 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 弃用移除目标不是一个硬性承诺，弃用的方法可以比这些最低目标 GA 版本更有效(即，只有最有问题的弃用方法才会被积极移除)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 也就是说，超过其最低删除目标版本的已弃用代码可能会在任何后续版本(包括补丁版本，也称为服务版本)中被删除，恕不另行通知。所以用户仍然应该努力尽早更新他们的代码。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 2.5.3 积极发展

下表总结了各种Reactor发布系列的开发状态：

| 版本                                                 | 支持的 |
| :--------------------------------------------------- | :----- |
| 2020.0.0(代号 Europium)(核心 3.4.x，netty 1.0.x) |        |
| 镝火车(核心 3.3.x，netty 0.9.x)                    |        |
| Califonium 及以下 (core < 3.3, netty < 0.9)          |        |
| Reactor 1.x 和 2.x 代                                |        |

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/gettingStarted.adoc)到“[入门](https://projectreactor.io/docs/core/release/reference/#getting-started)”

## 3. 响应式编程简介

Reactor是响应式编程范式的一个实现，可以总结如下：

>   响应式编程是一种涉及数据流和变化传播的异步编程范式。这意味着可以通过所采用的编程语言轻松表达静态(例如数组)或动态(例如事件发射器)数据流。
>
>   wiki — https://en.wikipedia.org/wiki/Reactive_programming

作为响应式编程方向的第一步，微软在.NET生态系统中创建了Reactive Extensions(Rx)库，然后RxJava在JVM上实现了响应式编程。随着时间的推移，通过Reactive Streams的努力，Java标准化出现了。该规范为JVM上的响应式库定义了一组接口和交互规则，它的接口已经集成到Java 9的Flow类下。

响应式编程范式通常以面向对象的语言表示，作为观察者设计模式的扩展。你还可以将主要的响应流模式与熟悉的迭代器设计模式进行比较，因为在所有这些库中，Iterable-Iterator对都有对偶性。一个主要区别是，迭代器是基于拉的模型，而响应流体现的是一种数据的推送。

使用迭代器是一种命令式编程模式，尽管访问值的方法完全由Iterable负责。实际上，由开发人员选择何时访问序列中的next()元素。在响应流中，上述Iterable-Iterator对的等价物是Publisher-Subscriber。但是，Publisher会在新的可用值到来时通知订阅者，而这个推送方面是响应的关键。此外，应用于推送值的操作以声明方式而不是命令方式表示：程序员表达计算的逻辑，而不是描述其精确的控制流。

除了推送值之外，错误处理和完成方面也以明确定义的方式进行了介绍。Publisher可以向其Subscriber推送新值(通过调用onNext)，但也可以发出错误信号(通过调用onError)或完成信号(通过调用onComplete)，错误和完成都会终止序列。这可以总结如下：

```text
onNext x 0...N [onError | onComplete]
```

这种方法非常灵活。该模式支持没有值、一个值或n个值(包括无限序列的值，例如时钟的连续滴答声)的用例。

但是为什么我们首先需要这样一个异步响应库呢？

### 3.1 阻塞可能是浪费

现代应用程序可以达到大量并发用户，尽管现代硬件的性能不断提高，但现代软件的性能仍然是一个关键问题。

总的来说，有两种方法可以提高程序的性能：

-   并行化以使用更多线程和更多硬件资源。
-   在如何使用现有资源方面寻求更高的效率。

通常，Java开发人员使用阻塞代码编写程序，这种做法很好，直到出现性能瓶颈。然后是时候引入其他线程了，运行类似的阻塞代码。但是这种资源利用率的扩展会很快引入争用和并发问题。

更糟糕的是，阻塞会浪费资源。如果仔细观察，一旦程序涉及一些延迟(特别是I/O，例如数据库请求或网络调用)，就会浪费资源，因为线程(可能很多线程)现在处于空闲状态，等待数据。

所以并行化方法不是灵丹妙药。访问硬件的全部功能是必要的，但异步推理起来也很复杂，并且容易造成资源浪费。

### 3.2 异步前来救援？

前面提到的第二种方法，寻求更高的效率，可以解决资源浪费问题。通过编写异步、非阻塞代码，你可以让执行切换到另一个使用相同底层资源的活动任务，然后在异步处理完成后返回到当前进程。

但是如何在JVM上生成异步代码呢？Java提供了两种异步编程模型：

-   回调：异步方法没有返回值，但需要一个额外的callback参数(lambda或匿名类)，当结果可用时会调用该参数。一个众所周知的例子是Swing的EventListener层次结构。
-   Futures：异步方法立即返回一个Future<T>，异步进程计算一个T值，但Future对象封装了对它的访问。该值不是立即可用的，并且可以轮询对象，直到该值可用为止。例如，运行Callable<T>任务的ExecutorService使用Future对象。

这些技术足够好吗？并非适用于所有用例，这两种方法都有局限性。

回调很难组合在一起，并且很快就会导致代码难以阅读和维护(称为“回调地狱”)。

举个例子：在UI上显示用户的前五个收藏夹，如果他没有收藏夹，则给出建议。这会通过三个服务(一个提供最喜欢的ID，第二个获取最喜欢的详细信息，第三个提供带有详细信息的建议)，如下所示：

示例5. 回调地狱示例

```java
userService.getFavorites(userId, new Callback<List<String>>() { // 1
	public void onSuccess(List<String> list) { // 2
		if (list.isEmpty()) { // 3
			suggestionService.getSuggestions(new Callback<List<Favorite>>() {
				public void onSuccess(List<Favorite> list) { // 4
					UiUtils.submitOnUiThread(() -> { // 5
						list.stream()
							.limit(5)
							.forEach(uiList::show); // 6
					});
				}
				public void onError(Throwable error) { // 7
					UiUtils.errorPopup(error);
				}
			});
		} else {
			list.stream() // 8
					.limit(5)
					.forEach(favId -> favoriteService.getDetails(favId, // 9
							new Callback<Favorite>() {
								public void onSuccess(Favorite details) {
									UiUtils.submitOnUiThread(() -> uiList.show(details));
								}
								public void onError(Throwable error) {
									UiUtils.errorPopup(error);
								}
							}
					));
		}
	}
    
	public void onError(Throwable error) {
		UiUtils.errorPopup(error);
	}
});
```

1.   我们有基于回调的服务：一个Callback接口，在异步进程成功时调用onSuccess，在发生错误时调用另一个方法onError。
2.   第一个服务使用收藏夹ID列表调用其回调。
3.   如果列表为空，我们必须转到suggestionService。
4.   suggestionService向第二个回调提供List<Favorite>。
5.   由于我们处理的是UI，我们需要确保我们的消费代码在UI线程中运行。
6.   我们使用Java 8 Stream将处理的建议数量限制为5个，并在UI的图形列表中显示它们。
7.   在每个级别，我们以相同的方式处理错误：在弹出窗口中显示它们。
8.   回到favorite ID级别。如果服务返回了一个完整的列表，我们需要转到favoriteService获取详细的Favorite对象。由于我们只需要5个，我们首先流式处理ID列表以将其限制为5个。
9.   再次，回调。这次我们得到了一个完整的Favorite对象，我们将其推送到UI线程内的UI。

上面是大量的代码，而且有点难以理解并且有重复的部分。考虑它在Reactor中的等价物：

示例6. 等效于回调代码的Reactor代码示例

```java
userService.getFavorites(userId) // 1
           .flatMap(favoriteService::getDetails) // 2 
           .switchIfEmpty(suggestionService.getSuggestions()) // 3 
           .take(5)  // 4
           .publishOn(UiUtils.uiThreadScheduler()) // 5
           .subscribe(uiList::show, UiUtils::errorPopup); // 6
```

1.   我们从favorite ID流开始。
2.   我们将这些异步转换为详细Favorite对象(flatMap)。我们现在有一个Favorite流。
3.   如果Favorite流为空，我们通过suggestionService切换到fallback。
4.   我们最多只对结果流中的5个元素感兴趣。
5.   最后，我们希望在UI线程中处理每一条数据。
6.   我们通过描述如何处理数据的最终形式(在UI列表中显示)以及在出现错误时如何处理(显示弹出窗口)来触发整个流。

如果你想确保在800毫秒内检索到favorite ID，或者如果需要更长的时间，从缓存中获取它们，该怎么办？在基于回调的代码中，这是一项复杂的任务。在Reactor中，它变得像在链中添加一个timeout运算符一样简单，如下所示：

示例7. 具有超时和fallback的Reactor代码示例

```java
userService.getFavorites(userId)
           .timeout(Duration.ofMillis(800)) // 1
           .onErrorResume(cacheService.cachedFavoritesFor(userId)) // 2
           .flatMap(favoriteService::getDetails) //3
           .switchIfEmpty(suggestionService.getSuggestions())
           .take(5)
           .publishOn(UiUtils.uiThreadScheduler())
           .subscribe(uiList::show, UiUtils::errorPopup);
```

1.   如果上面的部分在超过800毫秒内没有发出任何信号，则传播一个错误。
2.   如果出现错误，则回退到cacheService。
3.   代码链的其余部分与前面的示例类似。

Future对象比回调要好一点，但它们在组合方面仍然做得不好，尽管CompletableFuture在Java 8中带来了改进，将多个Future对象编排在一起是可行的，但并不容易。此外，Future还有其他问题：

-   通过调用get()方法，很容易导致Future对象的另一种阻塞情况。
-   它们不支持惰性计算。
-   它们缺乏对多值和高级错误处理的支持。

考虑另一个例子：我们得到一个ID列表，我们想从中获取名称和统计信息，然后将它们成对组合，所有这些都是异步的。以下示例使用CompletableFuture类型的列表执行此操作：

示例8.CompletableFuture组合示例

```java
CompletableFuture<List<String>> ids = ifhIds(); // 1

CompletableFuture<List<String>> result = ids.thenComposeAsync(l -> { // 2
	Stream<CompletableFuture<String>> zip =
			l.stream().map(i -> { // 3
				CompletableFuture<String> nameTask = ifhName(i); // 4
				CompletableFuture<Integer> statTask = ifhStat(i); // 5
        
				return nameTask.thenCombineAsync(statTask, (name, stat) -> "Name " + name + " has stats " + stat); // 6
			});
	List<CompletableFuture<String>> combinationList = zip.collect(Collectors.toList()); // 7
	CompletableFuture<String>[] combinationArray = combinationList.toArray(new CompletableFuture[combinationList.size()]);
    
	CompletableFuture<Void> allDone = CompletableFuture.allOf(combinationArray); // 8
	return allDone.thenApply(v -> combinationList.stream()
			.map(CompletableFuture::join) // 9
			.collect(Collectors.toList()));
});

List<String> results = result.join(); // 10
assertThat(results).contains(
	"Name NameJoe has stats 103",
			"Name NameBart has stats 104",
			"Name NameHenry has stats 105",
			"Name NameNicole has stats 106",
			"Name NameABSLAJNFOAJNFOANFANSF has stats 121");
```

1.   我们从一个Future开始，它为我们提供了一系列要处理的id值。
2.   一旦我们得到列表，我们想开始一些更深层次的异步处理。
3.   对于列表中的每个元素
4.   异步获取关联的名称。
5.   异步获取关联的任务。
6.   组合两个结果。
7.   我们现在有一个代表所有组合任务的Future列表。要执行这些任务，我们需要将列表转换为数组。
8.   将数组传递给CompletableFuture.allOf，它会在所有任务完成后输出一个Future。
9.   棘手的一点是allOf返回CompletableFuture<Void>，所以我们重新遍历Future列表，通过使用join()收集它们的结果(这里不会阻塞，因为allOf确保Future全部完成)。
10.   一旦触发了整个异步管道，我们等待它被处理并返回我们可以断言的结果列表。

由于Reactor有更多开箱即用的组合运算符，因此可以简化此过程，如下所示：

示例9. 等效于Future代码的Reactor代码示例

```java
Flux<String> ids = ifhrIds(); // 1

Flux<String> combinations =
		ids.flatMap(id -> { // 2
			Mono<String> nameTask = ifhrName(id); // 3
			Mono<Integer> statTask = ifhrStat(id); // 4
            
			return nameTask.zipWith(statTask, // 5
					(name, stat) -> "Name " + name + " has stats " + stat);
		});

Mono<List<String>> result = combinations.collectList(); // 6

List<String> results = result.block(); // 7
assertThat(results).containsExactly( // 8
	"Name NameJoe has stats 103",
			"Name NameBart has stats 104",
			"Name NameHenry has stats 105",
			"Name NameNicole has stats 106",
			"Name NameABSLAJNFOAJNFOANFANSF has stats 121"
);
```

1.   这一次，我们从异步提供的ids(Flux<String>)序列开始。
2.   对于序列中的每个元素，我们异步处理它(在函数体flatMap调用中)两次。
3.   获取关联的名称。
4.   获取相关的统计信息。
5.   异步组合这两个值。
6.   当值可用时，将它们聚合到List中。
7.   在生产中，我们将Flux通过进一步组合或订阅它来继续异步处理它。最有可能的是，我们会返回结果 Mono。因为我们只是进行测试，所以我们简单的阻塞，等待处理完成，然后直接返回值的聚合列表。
8.   断言结果。

使用回调和Future对象的风险是相似的，并且是响应式编程使用Publisher-Subscriber对解决的问题。

### 3.3 从命令式编程到响应式编程

响应式库(例如Reactor)旨在解决JVM上“经典”异步方法的这些缺点，同时还关注一些其他方面：

-   可组合性和可读性
-   使用丰富的运算符词汇表将数据作为流进行操作
-   在你订阅之前什么事情都不会发生
-   背压或消费者向生产者发出发射速率过高的信号的能力
-   高级别但高价值的抽象，与并发无关

#### 3.3.1 可组合性和可读性

所谓“可组合性”，我们指的是编排多个异步任务的能力，其中我们使用先前任务的结果向后续任务提供输入。或者，我们可以以fork-join风格运行多个任务。此外，我们可以将异步任务重用为更高级别系统中的离散组件。

编排任务的能力与代码的可读性和可维护性紧密相关。随着异步进程的层数和复杂性的增加，编写和阅读代码变得越来越困难。正如我们所看到的，回调模型很简单，但它的一个主要缺点是，对于复杂的流程，你需要从一个回调中执行一个回调，它本身嵌套在另一个回调中，等等。这种混乱被称为“回调地狱”。正如你可以猜到的(或从经验中知道的)，这样的代码很难回溯和推理。

Reactor提供了丰富的组合选项，其中代码反映了抽象过程的组织，并且所有内容通常都保持在同一级别(嵌套被最小化)。

#### 3.3.2 流水线类比

你可以将响应式应用程序处理的数据视为通过装配线移动的原材料，Reactor既是传送带，又是工作站。原材料从源头(原始Publisher)倾泻而出，最终成为准备好推向消费者(或Subscriber)的成品。

原材料可以经过各种转换和其他中间步骤，也可以是将中间部件聚集在一起的大型装配线的一部分。如果在某一点出现故障或堵塞(可能产品装箱需要的时间过长)，受影响的工作站可以向上游发出信号，限制原材料的流动。

#### 3.3.3 运算符

在Reactor中，运算符是我们流水线类比中的工作站。每个运算符都会向Publisher添加行为，并将上一步的Publisher包装到一个新实例中。因此，整个链被链接起来，使得数据从第一个Publisher开始并沿着链条向下移动，并通过每个链接进行转换。最终，Subscriber完成该过程。请记住，在Subscriber订阅Publisher之前，什么都不会发生，我们很快就会看到这一点。

>   理解运算符会创建新实例可以帮助你避免一个常见错误，该错误会导致你相信你在链中使用的运算符没有被应用。请参阅常见问题解答中的[此项](https://projectreactor.io/docs/core/release/reference/#faq.chain)。

尽管Reactive Streams规范本身没有指定运算符，但响应库(例如Reactor)的最佳附加价值之一是它们提供的丰富的运算符词汇表。这些涵盖了很多领域，从简单的转换和过滤到复杂的编排和错误处理。

#### 3.3.4 在你subscribe()之前什么都不会发生

在Reactor中，当你编写Publisher链时，默认情况下数据不会开始泵入其中。相反，你只是创建了异步过程的抽象描述(这有助于重用和组合)。

通过订阅行为，你将Publisher绑定到Subscriber，从而触发整个链中的数据流。这是通过来自Subscriber的单个request信号在内部实现的，该请求信号向上游传播，一直返回到源Publisher。

#### 3.3.5 背压

向上游传播信号也可用于实现背压，我们在流水线类比中将其描述为当工作站处理速度比上游工作站慢时向上发送的反馈信号。

Reactive Streams规范定义的真实机制也非常接近这个类比：订阅者可以在无限制模式下工作，并让源以可实现的最快速率推送所有数据，或者它可以使用request机制向源发出信号，表示它准备处理最多n个元素。

中间运算符也可以在传输过程中更改请求。想象一个buffer 运算符，它以十个为一批对元素进行分组。如果订阅者请求一个缓冲区，则源生成十个元素是可以接受的。一些运算符还实现了预取策略，这避免了request(1)往返，如果在请求元素之前生成元素的成本不太高，这是有益的。

这将推送模型转换为推拉混合模型，其中下游可以从上游拉取n个元素(如果它们随时可用)。但是如果元素还没有准备好，它们就会在生产时被上游推送。

#### 3.3.6 热与冷

Rx响应库家族区分了两大类响应序列：热和冷。这种区别主要与响应流如何对订阅者做出响应有关：

-   冷序列为每个Subscriber重新开始，包括在数据源处。例如，如果源包装了一个HTTP调用，则会为每个订阅发出一个新的HTTP请求。
-   热序列不会对每个Subscriber从头开始。相反，迟来的订阅者只会收到订阅后发出的信号。但是请注意，一些热响应流可以缓存或回放全部或部分排放历史。从一般的角度来看，热序列甚至可以在没有订阅者监听时发出(“订阅之前什么都不会发生”规则的一个例外)。

有关Reactor上下文中热流与冷流的更多信息，请参阅[此特定于Reactor的部分](https://projectreactor.io/docs/core/release/reference/#reactor.hotCold)。

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/reactiveProgramming.adoc)到“[响应式编程简介](https://projectreactor.io/docs/core/release/reference/#intro-reactive)”

## 4. Reactor核心功能

Reactor项目的主要依赖是reactor-core，这是一个专注于Reactive Streams规范并以Java 8为目标的响应库。

Reactor引入了实现Publisher的可组合响应类型，也提供了丰富的运算符词汇表：Flux和Mono。Flux对象代表0...N个元素的响应序列，而Mono对象代表单个值或空结果(0...1)。

这种区别在类型中携带了一些语义信息，表明异步处理的大致基数。例如，一个HTTP请求只产生一个响应，所以执行一个count操作没有多大意义。因此，将这种HTTP调用的结果表示为Mono<HttpResponse>比将其表示为Flux<HttpResponse>更有意义，因为它仅提供与零项或一项的上下文相关的运算符。

更改处理的最大基数的运算符也会切换到相关类型。例如，count运算符存在于Flux中，但它返回一个Mono<Long>。

### 4.1 Flux，一个0-N项的异步序列

下图显示了Flux如何转换元素：

![通量](https://projectreactor.io/docs/core/release/reference/images/flux.svg)

Flux<T>是一个标准的Publisher<T>，它表示0到N个发射元素的异步序列，可选择由完成信号或错误终止。与Reactive Streams规范一样，这三种类型的信号转换为对下游订阅者的onNext、onComplete和onError方法的调用。

由于可能的信号范围很大，Flux是通用的响应类型。请注意，所有事件(甚至是终止事件)都是可选的：如果没有onNext事件，只有onComplete事件表示一个空的有限序列，但是删除onComplete，你将拥有一个无限的空序列(除了取消测试外，没有什么特别的用处)。同样，无限序列不一定是空的；例如，Flux.interval(Duration)产生一个无限的Flux<Long>，并从时钟发出规则的滴答声。

### 4.2 Mono，异步0-1结果

下图显示了Mono如何转换元素：

![单核细胞增多症](https://projectreactor.io/docs/core/release/reference/images/mono.svg)

Mono<T>是一个专用的Publisher<T>，它通过onNext信号最多发出一个元素，然后以onComplete信号终止(成功的Mono，有或没有值)，或者只发出一个onError信号(失败的Mono)。

大多数Mono实现在调用onNext之后，都会立即在Subscriber上调用onComplete。Mono.never()是一个异常值：它不发出任何信号，这在技术上并没有被禁止，尽管在测试之外并不是非常有用。另一方面，onNext和onError的组合被明确禁止。

Mono仅提供可用于Flux的运算符的子集，并且一些运算符(特别是那些将Mono与另一个Publisher组合的运算符)切换到Flux。例如，Mono#concatWith(Publisher)返回一个Flux，而Mono#then(Mono)返回另一个Mono。

请注意，你可以使用Mono来表示只有完成概念的无值异步进程(类似于Runnable)。要创建一个，你可以使用一个空的Mono<Void>。

### 4.3 创建Flux或Mono并订阅它的简单方法

入门Flux和Mono的最简单方法是使用它们各自类中的众多工厂方法之一。

例如，要创建一个String序列，你可以枚举它们，或将它们放入一个集合中并从中创建Flux，如下所示：

```java
Flux<String> seq1 = Flux.just("foo", "bar", "foobar");

List<String> iterable = Arrays.asList("foo", "bar", "foobar");
Flux<String> seq2 = Flux.fromIterable(iterable);
```

一些其他的工厂方法示例包括：

```java
Mono<String> noData = Mono.empty(); // 1

Mono<String> data = Mono.just("foo");

Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3); //2
```

1.   请注意，工厂方法接受泛型类型，即使它没有值。
2.   第一个参数是范围的开始，而第二个参数是要生产的元素数。

在订阅方面，Flux和Mono使用Java 8 lambda。你可以选择多种.subscribe()方法重载，这些变体为不同的回调组合接收lambda，如以下方法签名所示：

示例10. 基于 Lambda 的订阅变体Flux

```java
subscribe(); // 1

subscribe(Consumer<? super T> consumer); // 2

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer);  // 3

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer);  // 4

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer,
          Consumer<? super Subscription> subscriptionConsumer); // 5
```

1. 订阅并触发序列。
1. 对每个产生的值做一些事情。
1. 对每个产生的值做一些事情，但也要对错误做出响应。
1. 处理值和错误，但还要在序列成功完成时运行一些代码。
1. 处理值和错误并成功完成，但也要对由此subscribe调用产生的Subscription做一些事情。

>   这些方法重载返回对Subscription的引用，你可以在不需要更多数据时使用该引用取消订阅。取消后，源应停止生成值并清理它创建的任何资源。这种取消和清理行为在Reactor中由通用的Disposable接口表示。

#### 4.3.1 subscribe方法示例

本节包含subscribe方法的五个重载中的每一个的最小示例，下面的代码显示了不带参数的基本方法的示例：

```java
Flux<Integer> ints = Flux.range(1, 3); // 1
ints.subscribe(); // 2
```

1.   设置一个Flux，当订阅者连接时产生3个值。
2.   以最简单的方式订阅。

上面的代码没有产生可见的输出，但它确实有效，Flux产生三个值。如果我们提供一个lambda，我们可以使这些值可见。subscribe方法的下一个示例显示了一种使值可见的方法：

```java
Flux<Integer> ints = Flux.range(1, 3); // 1
ints.subscribe(i -> System.out.println(i)); // 2
```

1.   设置一个Flux，当订阅者连接时产生3个值。
2.   订阅将打印值的订阅者。

上面的代码生成以下输出：

```none
1
2
3
```

为了演示下一个重载，我们故意引入一个错误，如下例所示：

```java
Flux<Integer> ints = Flux.range(1, 4) // 1
		.map(i -> { // 2
			if (i <= 3) return i; // 3
			throw new RuntimeException("Got to 4"); // 4
		});
ints.subscribe(i -> System.out.println(i), // 5
		error -> System.err.println("Error: " + error));
```

1.   设置一个Flux，当订阅者连接时产生4个值。
2.   我们需要一个Map，以便我们可以以不同的方式处理某些值。
3.   对于小于等于3的值，简单返回该值。
4.   对于大于3的值，触发错误。
5.   订阅包含错误处理程序的订阅者。

我们现在有两个lambda表达式：一个用于我们期望的内容，一个用于错误。上面的代码生成以下输出：

```none
1
2
3
Error: java.lang.RuntimeException: Got to 4
```

subscribe方法的下一个签名包括错误处理程序和完成事件处理程序，如下例所示：

```java
Flux<Integer> ints = Flux.range(1, 4); // 1
ints.subscribe(i -> System.out.println(i),
		error -> System.err.println("Error " + error),
		() -> System.out.println("Done")); // 2
```

1.   设置一个Flux，当订阅者连接时产生4个值。
2.   使用包含完成事件处理程序的订阅者订阅。

错误信号和完成信号都是终端事件，并且相互排斥(永远不可能同时得到这两种信号)。为了使完成消费者工作，我们必须注意不要触发错误。

完成回调没有输入，由一对空括号表示：它与Runnable接口中的run方法匹配。上述代码生成以下输出：

```none
1
2
3
4
Done
```

#### 4.3.2 使用Disposable取消subscribe()

所有这些基于lambda的subscribe()重载都有一个Disposable返回类型。在这种情况下，Disposable接口表示可以通过调用其dispose()方法取消订阅。

对于Flux或Mono，取消是源应该停止生成元素的信号。但是，不能保证立即生效：某些源可能会非常快地生成元素，以至于它们甚至可以在收到取消指令之前就完成。

Disposable类中提供了一些关于Disposables的工具方法。其中，Disposables.swap()创建一个Disposable包装器，允许你可以原子地取消和替换具体的Disposable。这可能很有用，例如，在你想要取消请求并在用户单击按钮时将其替换为新请求的UI场景中。处理包装器本身会关闭它，这样做会释放当前的具体值和所有未来尝试的替换。

另一个有趣的工具方法是Disposables.composite(...)，这种组合让你可以收集多个Disposable(例如，与服务调用相关联的多个in-flight请求)并在以后一次性处理所有这些请求。一旦调用了组合的dispose()方法，任何添加另一个Disposable的尝试都会立即释放它。

#### 4.3.3 Lambda的替代方案：BaseSubscriber

还有一种更通用的subscribe方法，它接收一个完整的Subscriber，而不是由lambda组成。为了帮助编写这样的Subscriber代码，我们提供了一个名为BaseSubscriber的可扩展类。

>   BaseSubscriber的实例(或其子类)是一次性使用的，这意味着如果BaseSubscriber订阅了第二个Publisher，则会取消对第一个Publisher的订阅。这是因为两次使用实例会违反Reactive Stream规则，即不得并行调用Subscriber的onNext方法。只有在对Publisher#subscribe(Subscriber)的调用中直接声明匿名实现时，它们才适用。

现在我们可以实现其中之一。我们称之为SampleSubscriber。以下示例显示了如何将其附加到Flux：

```java
SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
Flux<Integer> ints = Flux.range(1, 4);
ints.subscribe(ss);
```

以下示例显示了SampleSubscriber的外观，作为BaseSubscriber的简约实现：

```java
package io.projectreactor.samples;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;

public class SampleSubscriber<T> extends BaseSubscriber<T> {

	public void hookOnSubscribe(Subscription subscription) {
		System.out.println("Subscribed");
		request(1);
	}

	public void hookOnNext(T value) {
		System.out.println(value);
		request(1);
	}
}
```

SampleSubscriber类继承了BaseSubscriber，这是Reactor中用户定义Subscribers的推荐抽象类。该类提供了可以被重写以调整订阅者行为的钩子。默认情况下，它会触发一个无限制的请求，并且行为与subscribe()完全相同。但是，当你需要自定义请求数量时，继承BaseSubscriber会更有用。

对于自定义请求量，最基本的要求是实现hookOnSubscribe(Subscription subscription)和hookOnNext(T value)，就像我们所做的那样。在我们的例子中，hookOnSubscribe方法将一条语句打印到标准输出并发出第一个请求。然后hookOnNext方法打印一条语句并执行附加请求，一次一个请求。

该类SampleSubscriber生成以下输出：

```none
Subscribed
1
2
3
4
```

BaseSubscriber还提供了一个requestUnbounded()方法来切换到无界模式(相当于request(Long.MAX_VALUE))，以及一个cancel()方法。

它还有额外的钩子：hookOnComplete，hookOnError，hookOnCancel，和hookFinally(当序列终止时总是调用，终止类型作为SignalType参数传入)。

>   你几乎肯定想要实现hookOnError、hookOnCancel和hookOnComplete方法。你可能还想实现hookFinally方法。SampleSubscriber是执行有界请求的Subscriber的绝对最小实现。

#### 4.3.4 关于背压和重塑请求的方法

在Reactor中实现背压时，消费者压力传播回源的方式是向上游操作员发送request。当前请求的总和有时被称为当前“需求”或“待处理请求”。需求上限为Long.MAX_VALUE时，表示一个无限制的请求(意思是“尽可能快地生产”，基本上是禁用背压)。

第一个请求来自订阅时的最终订阅者，但最直接的订阅方式都会立即触发Long.MAX_VALUE的无限制请求：

-   subscribe()以及它的大多数基于lambda的重载(具有Consumer<Subscription>的重载除外)
-   block(),blockFirst()和blockLast()
-   迭代一个toIterable()或toStream()

自定义原始请求的最简单方法是使用重写了hookOnSubscribe方法的BaseSubscriber进行订阅，如下例所示：

```java
Flux.range(1, 10)
		.doOnRequest(r -> System.out.println("request of " + r))
		.subscribe(new BaseSubscriber<Integer>() {
            
			@Override
			public void hookOnSubscribe(Subscription subscription) {
				request(1);
			}
            
			@Override
			public void hookOnNext(Integer integer) {
				System.out.println("Cancelling after having received " + integer);
				cancel();
			}
		});
```

上面的代码段打印出以下内容：

```none
request of 1
Cancelling after having received 1
```

>   在处理请求时，你必须小心为序列的推进产生足够的需求，否则你的Flux可能会“卡住”。这就是为什么BaseSubscriber在hookOnSubscribe中默认为无限制请求的原因。当重写这个钩子时，你通常应该至少调用一次request。

##### 从下游改变需求的运算符

需要记住的一点是，在订阅级别表达的需求可以由上游链中的每个运算符重新塑造。一个典型的例子是buffer(N)运算符：如果它接收到一个request(2)，它被解释为对两个完整缓冲区的需求。由于缓冲区需要N个元素才能被视为已满，因此buffer运算符将请求重新塑造为2  N。

你可能还注意到，某些运算符的重载接收名为prefetch的int输入参数，这是修改下游请求的另一类操作符。这些通常是处理内部序列的运算符，从每个传入元素(如flatMap)派生一个Publisher。

预取(prefetch)是一种调整对这些内部序列发出的初始请求的方法。如果未指定，这些运算符中的大多数以32的需求开始。

这些运算符通常还实现了补货优化：一旦运算符完成了75%的预取请求，它就会从上游重新请求75%。这是一种启发式优化，以便这些运算符主动预测即将到来的请求。

最后，有几个运算符可以让你直接调整请求：limitRate和limitRequest。

limitRate(N)拆分下游请求，以便它们以较小的批次向上游传播。例如，向limitRate(10)发出的100请求将导致最多10个10的请求被传播到上游。请注意，在这种形式中，limitRate实际上实现了前面讨论的补货优化。

该运算符有一个重载，它还可以让你调整补充量(称为lowTide)：limitRate(highTide, lowTide)。选择lowTide为0会产生严格的highTide请求批次，而不是通过补充策略进一步修改的批次。

另一方面，limitRequest(N)将下游请求限制为最大总需求，它将请求累加到N。如果单个请求没有使总需求溢出 N，则该特定请求将完全传播到上游。在源发出该数量后，limitRequest认为序列完成，向下游发送一个onComplete信号，并取消源。

### 4.4 以编程方式创建序列

在本节中，我们通过编程定义其关联事件(onNext、onError和onComplete)来介绍Flux或Mono的创建，所有这些方法都有一个共同点，即它们公开了一个API来触发我们称之为sink(接收器)的事件。实际上有一些接收器变体，我们很快就会谈到。

#### 4.4.1 同步generate

以编程方式创建Flux的最简单形式是通过generate方法，该方法接收一个生成器(generator)函数。

这是用于同步和一对一的发射，这意味着接收器是一个SynchronousSink，并且它的next()方法在每个回调调用中最多只能调用一次。然后你可以另外调用error(Throwable)或者complete()，但这是可选的。

最有用的变体可能还允许你保持一种状态，你可以在使用接收器时参考该状态来决定接下来要发出什么，然后生成器函数变成一个BiFunction<S, SynchronousSink<T>, S>, 其中<S>是状态对象的类型。你必须为初始状态提供一个Supplier<S>，并且你的生成器函数现在每轮返回一个新状态。

例如，你可以使用int作为状态：

示例11. 基于状态的generate示例

```java
Flux<String> flux = Flux.generate(
		() -> 0, // 1
		(state, sink) -> {
			sink.next("3 x " + state + " = " + 3  state); // 2
			if (state == 10) sink.complete(); // 3
			return state + 1; // 4
		});
```

1.   我们提供初始状态值为0
2.   我们使用state值来选择发射什么(乘法表中的一行3)
3.   我们还使用它来选择何时停止
4.   我们返回一个新状态，我们将在下一次调用中使用(除非序列在本次调用中终止)

上述代码生成表3，顺序如下：

```text
3 x 0 = 0 
3 x 1 = 3 
3 x 2 = 6 
3 x 3 = 9 
3 x 4 = 12 
3 x 5 = 15 
3 x 6 = 18 
3 x 7 = 21 
3 x 8 = 24 
3 x 9 = 27 
3 x 10 = 30
```

你还可以使用可变的<S>。例如，可以使用一个AtomicLong作为状态重写上面的示例，并在每一轮中对其进行修改：

示例12. 可变状态变体

```java
Flux<String> flux = Flux.generate(
		AtomicLong::new, // 1
		(state, sink) -> {
			long i = state.getAndIncrement(); // 2
			sink.next("3 x " + i + " = " + 3  i);
			if (i == 10) sink.complete();
			return state; // 3
		});
```

1. 这一次，我们生成一个可变对象作为状态。
1. 我们在这里改变状态。
1. 我们返回与新状态相同的实例。

>   如果你的状态对象需要清理一些资源，请使用generate(Supplier<S>, BiFunction, Consumer<S>)变体来清理最后一个状态实例。

以下示例演示了包含Consumer的generate方法：

```java
Flux<String> flux = Flux.generate(
		AtomicLong::new,
		(state, sink) -> { // 1
			long i = state.getAndIncrement(); // 2
			sink.next("3 x " + i + " = " + 3  i);
			if (i == 10) sink.complete();
			return state; // 3
		}, (state) -> System.out.println("state: " + state)); // 4
```

1.   同样，我们生成一个可变对象作为状态。
2.   我们在这里改变状态。
3.   我们返回与新状态相同的实例。
4.   我们将最后一个状态值(11)视为此Consumer lambda的输出。

如果状态包含需要在进程结束时处理的数据库连接或其他资源，则Consumer lambda可以关闭连接或以其他方式处理应在进程结束时完成的任何任务。

#### 4.4.2 异步和多线程：create

create是一种更高级的Flux编程创建形式，它适用于每轮多次发射，甚至可以从多个线程进行。

它公开了FluxSink, 及其next、error和complete方法。与generate相反，它没有基于状态的变体。另一方面，它可以在回调中触发多线程事件。

>   create对于将现有API与响应式世界连接起来非常有用 - 例如基于监听器的异步API。

>create不会并行化你的代码，也不会使其异步，即使它可以与异步API一起使用。如果你在create lambda中阻塞，你就会暴露在死锁和类似的副作用中。即使使用了subscribeOn，也有一个警告，即长阻塞create lambda(例如调用sink.next(t)的无限循环)可能会锁定管道：由于循环饿死了它们应该运行的同一线程，因此请求将永远不会被执行。使用subscribeOn(Scheduler, false)变体：requestOnSeparateThread = false将使用Scheduler线程进行创建，并仍然通过在原始线程中执行请求来让数据流动。

假设你使用了一个基于监听器的API，它按块处理数据，有两个事件：(1)数据块准备好，(2)处理完成(终端事件)，如MyEventListener接口所示：

```java
interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);
    void processComplete();
}
```

你可以使用create将其桥接到Flux<T>：

```java
Flux<String> bridge = Flux.create(sink -> {
	myEventProcessor.register( // 4
			new MyEventListener<String>() { // 1
                
				public void onDataChunk(List<String> chunk) {
					for (String s : chunk) {
						sink.next(s); // 2
					}
				}
                
				public void processComplete() {
					sink.complete(); // 3
				}
			});
});
```

| 1    | 桥接到MyEventListener API                          |
| ---- | -------------------------------------------------- |
| 2    | 块中的每个元素都成为Flux中的一个元素               |
| 3    | processComplete事件被转换为onComplete              |
| 4    | 每当myEventProcessor执行时，所有这些都是异步完成的 |

1.   桥接到MyEventListener API
2.   块中的每个元素都成为Flux中的一个元素
3.   processComplete事件被转换为onComplete
4.   每当myEventProcessor执行时，所有这些都是异步完成的

此外，由于create可以桥接异步API并管理背压，因此你可以通过指示OverflowStrategy来细化背压行为：

-   IGNORE：完全忽略下游背压请求。当下游队列满时，这可能会产生IllegalStateException。
-   ERROR：当下游无法跟上时发出IllegalStateException的错误信号。
-   DROP：如果下游还没有准备好接收传入的信号，则丢弃它。
-   LATEST：让下游只从上游获取最新信号。
-   BUFFER(默认)：如果下游跟不上，缓冲所有信号(这会进行无界缓冲，并可能导致OutOfMemoryError)。

>   Mono还有一个create生成器，Mono创建的MonoSink不允许多次发射。它将在第一个信号之后丢弃所有信号。

#### 4.4.3 异步但单线程：push

push介于generate和create之间，适用于处理来自单个生产者的事件。它在某种意义上类似于create，因为它也可以是异步的，并且可以使用create支持的任何溢出策略来管理背压。但是，一次只有一个生产线程可以调用 next、complete或erro。

```java
Flux<String> bridge = Flux.push(sink -> {
	myEventProcessor.register(
			new SingleThreadEventListener<String>() { // 1
                
				public void onDataChunk(List<String> chunk) {
					for (String s : chunk) {
						sink.next(s); // 2
					}
				}
                
				public void processComplete() {
					sink.complete(); // 3
				}
                
				public void processError(Throwable e) {
					sink.error(e); // 4
				}
			});
});
```

1.   桥接到SingleThreadEventListener API
2.   使用next从单个监听器线程将事件推送到接收器
3.   从同一监听器线程生成的complete事件
4.   error事件也从同一个监听器线程生成

##### 混合推/拉模型

大多数Reactor运算符，例如create，都遵循混合推/拉模型。我们的意思是，尽管大部分处理是异步的(建议采用push方法)，但它有一个小的pull组件：request。

消费者从源中拉取数据，即在第一次请求之前它不会发出任何东西。当数据可用时，源就会将数据推送给消费者，但要在其请求数量的范围内。

请注意，push()和create()都允许设置onRequest Consumer以管理请求量并确保仅在有待处理请求时才通过接收器推送数据。

```java
Flux<String> bridge = Flux.create(sink -> {
	myMessageProcessor.register(
			new MyMessageListener<String>() {
                
				public void onMessage(List<String> messages) {
					for (String s : messages) {
						sink.next(s); // 3
					}
				}
			});
	sink.onRequest(n -> {
		List<String> messages = myMessageProcessor.getHistory(n); // 1
		for (String s : messages) {
			sink.next(s); // 2
		}
	});
});
```

1. 发出请求时轮询消息
2. 如果消息立即可用，则将它们推送到接收器
3. 稍后异步到达的剩余消息也会被传递

##### 在push()或create()之后清理

两个回调onDispose和onCancel在取消或终止时执行任何清理。onDispose可用于在Flux完成、出错或被取消时执行清理。onCancel可用于在使用onDispose进行清理之前执行任何特定于取消的操作。

```java
Flux<String> bridge = Flux.create(sink -> {
	sink.onRequest(n -> channel.poll(n))
			.onCancel(() -> channel.cancel()) // 1
			.onDispose(() -> channel.close()) // 2
});
```

1. onCancel首先被调用，仅用于取消信号
2. onDispose用于完成、错误或取消信号

#### 4.4.4 Handle

handle方法有点不同：它是一个实例方法，这意味着它链接在一个现有的源上(与常见的运算符一样)。它存在于Mono和Flux中。

从某种意义上说，它接近generate，因为它使用SynchronousSink并且只允许一个接一个的发射。但是，handle可用于从每个源元素中生成任意值，可能会跳过某些元素。通过这种方式，它可以作为map和filter的组合。handle方法的签名如下：

```java
Flux<R> handle(BiConsumer<T, SynchronousSink<R>>);
```

让我们考虑一个例子。响应流规范不允许序列中的null值。如果你想执行map，但又想使用预先存在的方法作为map 函数，并且该方法有时返回null怎么办？

例如，以下方法可以安全地应用于整数源：

```java
public String alphabet(int letterNumber) {
	if (letterNumber < 1 || letterNumber > 26) {
		return null;
	}
	int letterIndexAscii = 'A' + letterNumber - 1;
	return "" + (char) letterIndexAscii;
}
```

然后我们可以使用handle来删除任何空值：

示例13. 将handle用于“映射并消除空值”的场景

```java
Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
		.handle((i, sink) -> {
			String letter = alphabet(i); // 1
			if (letter != null) // 2
				sink.next(letter); // 3
		});
alphabet.subscribe(System.out::println);
```

1.   映射到字母
2.   如果“map函数”返回null...
3.   通过不调用sink.next将其过滤掉

这将打印出：

```
M
I
T
```

### 4.5 线程和调度程序

像RxJava一样，Reactor可以被认为是与并发无关的。也就是说，它不强制执行并发模型。相反，它让你(开发人员)来指挥。但是，这并不妨碍Reactor帮助你处理并发问题。

获得Flux或Mono并不一定意味着它在专用线程中运行，相反，大多数运算符继续在前一个运算符执行的线程中工作。除非明确指定，否则最顶层的运算符(源)本身在执行subscribe()调用的线程上运行。以下示例在新线程中运行Mono：

```java
public static void main(String[] args) throws InterruptedException {
	final Mono<String> mono = Mono.just("hello "); // 1
	Thread t = new Thread(() -> mono
			.map(msg -> msg + "thread ")
			.subscribe(v -> // 2
					System.out.println(v + Thread.currentThread().getName()) // 3
			)
	);
    
	t.start();
	t.join();
}
```

1.   Mono<String>在main线程中组装
2.   但是，它是在线程Thread-0中订阅的
3.   因此，map和onNext回调实际上都在Thread-0中运行

上面的代码输出如下：

```none
hello thread Thread-0
```

在Reactor中，执行模型和执行发生的位置由所使用的Scheduler决定。[Scheduler](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Scheduler.html)具有类似于ExecutorService的调度职责，但具有专用的抽象使其可以做更多事情，特别是充当时钟并支持更广泛的实现(测试的虚拟时间、蹦床或即时调度等)。

[Schedulers](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html)类具有静态方法，可以访问以下执行上下文：

-   无执行上下文(Schedulers.immediate())：在处理时，提交的Runnable将被直接执行，有效地在当前Thread上运行它们(可以看作是“空对象”或无操作Scheduler)。
-   一个可重复使用的线程(Schedulers.single())。请注意，此方法为所有调用者重用相同的线程，直到调度器被释放。如果你想要一个每次调用的专用线程，请为每个调用使用Schedulers.newSingle()。
-   一个无界的弹性线程池(Schedulers.elastic())。随着Schedulers.boundedElastic()的引入，这种方法不再是首选，因为它倾向于隐藏背压问题并导致线程过多(见下文)。
-   有界弹性线程池(Schedulers.boundedElastic())。像它的前身elastic()一样，它根据需要创建新的工作池并重用空闲的工作池。闲置时间过长(默认为60秒)的工作池也会被处理掉。与它的elastic()前身不同，它对可以创建的支持线程数有一个上限(默认为CPU内核数 x 10)，在达到上限后提交的最多10万个任务被排入队列，并在线程可用时重新调度(当有延迟调度时，延迟从线程可用时开始)。这是I/O阻塞工作的更好选择。 Schedulers.boundedElastic()是一种方便的方法，可以为阻塞进程提供自己的线程，这样它就不会占用其他资源。请参阅[如何包装同步阻塞调用？](https://projectreactor.io/docs/core/release/reference/#faq.wrap-blocking)，但不会用新线程对系统造成太大压力。
-   为并行工作(Schedulers.parallel())调整的固定工作池，它创建与CPU内核一样多的工作线程数。

此外，你可以使用Schedulers.fromExecutorService(ExecutorService)从任何预先存在的 ExecutorService创建调度器(也可以从Executor创建，但不鼓励这样做)。

你还可以使用newXXX方法创建各种调度器类型的新实例。例如，Schedulers.newParallel(yourScheduleName)创建一个名为yourScheduleName的新并行调度器。

>   虽然boundedElastic旨在帮助处理无法避免的遗留阻塞代码，但single和paraller却不是。因此，使用 Reactor阻塞API(block()、blockFirst()、blockLast()，以及在默认的single和paraller调度器中迭代toIterable()或toStream())会导致抛出IllegalStateException。

>   通过创建实现NonBlocking标记接口的Thread实例，也可以将自定义调度器标记为“non-blocking only”。

一些运算符默认使用Schedulers中的特定调度器(并且通常让你选择提供不同的调度器)。例如，调用 Flux.interval(Duration.ofMillis(300))工厂方法会产生Flux<Long>每300毫秒滴答一次的。默认情况下，这是由Schedulers.parallel()启用的。下面一行代码将调度器更改为类似于Schedulers.single()的新实例：

```java
Flux.interval(Duration.ofMillis(300), Schedulers.newSingle("test"))
```

SchedulerReactor提供了两种在响应链中切换执行上下文(或)的方法：publishOn和subscribeOn。两者都采用Scheduler并让你将执行上下文切换到该调度程序。但是publishOn链中的位置很重要，而 的位置不重要subscribeOn。要了解这种差异，你首先必须记住，[在你订阅之前不会发生任何事情](https://projectreactor.io/docs/core/release/reference/#reactive.subscribe)。

在Reactor中，当你链接操作符时，你可以根据需要将任意数量的实现封装Flux在一起Mono 。订阅后，将 Subscriber创建一个对象链，向后(在链上)到第一个发布者。这实际上是对你隐藏的。Flux你所能看到的只是(or Mono) and的外层 Subscription，但是这些中间的特定于运营商的订阅者才是真正的工作发生的地方。

有了这些知识，我们就可以仔细看看publishOnandsubscribeOn 运算符：

#### 4.5.1 publishOn方法

publishOn与任何其他运营商一样，在订阅者链的中间应用。它从上游获取信号并在下游重放它们，同时对关联的Scheduler. 因此，它 会影响后续运算符的执行位置(直到publishOn链接另一个运算符)，如下所示：

-   将执行上下文更改Thread为由Scheduler
-   根据规范，onNext调用是按顺序发生的，所以这会占用一个线程
-   除非他们在同一个线程上继续执行之后在特定的Scheduler, 运算符上工作publishOn

以下示例使用该publishOn方法：

```java
Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); 

final Flux<String> flux = Flux
    .range(1, 2)
    .map(i -> 10 + i)  
    .publishOn(s)  
    .map(i -> "value " + i);  

new Thread(() -> flux.subscribe(System.out::println));  
```

|      | 创建一个Scheduler由四个Thread实例支持的新的。            |
| ---- | ------------------------------------------------------------ |
|      | 第一个map在 <5> 中的匿名线程上运行。                       |
|      | 切换从 <1> 中选取的publishOn整个序列。Thread             |
|      | 第二个map在Threadfrom <1> 上运行。                       |
|      | 这个匿名Thread是订阅发生的地方。打印发生在最新的执行上下文中，即来自publishOn. |

#### 4.5.2 subscribeOn方法

subscribeOn当构建反向链时，适用于订阅过程。因此，无论你将subscribeOn放在链中的哪个位置， 它总是会影响源发射的上下文。但是，这不会影响后续调用的行为publishOn ——它们仍然会为它们之后的链部分切换执行上下文。

-   更改Thread整个运营商链订阅的
-   从中挑选一根线Scheduler

|      | subscribeOn实际上只考虑了链中 最早的调用。 |
| ---- | -------------------------------------------- |
|      |                                              |

以下示例使用该subscribeOn方法：

```java
Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); 

final Flux<String> flux = Flux
    .range(1, 2)
    .map(i -> 10 + i)  
    .subscribeOn(s)  
    .map(i -> "value " + i);  

new Thread(() -> flux.subscribe(System.out::println));  
```

|      | 创建一个Scheduler由四个支持的新对象Thread。              |
| ---- | ------------------------------------------------------------ |
|      | 第map一个在这四个线程中的一个上运行……                      |
|      | …因为subscribeOn从订阅时间(<5>)开始切换整个序列。        |
|      | 第二个map也在同一个线程上运行。                            |
|      | 这个匿名Thread是订阅最初发生的地方，但会subscribeOn立即将其转移到四个调度程序线程之一。 |

### 4.6 处理错误

|      | 要快速查看可用于错误处理的运算符，请参阅[相关的运算符决策树](https://projectreactor.io/docs/core/release/reference/#which.errors)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

在 Reactive Streams 中，错误是终端事件。一旦发生错误，它就会停止序列并沿着运算符链向下传播到最后一步，即 Subscriber你定义的方法及其onError方法。

此类错误仍应在应用程序级别处理。例如，你可能会在 UI 中显示错误通知或在 REST 端点中发送有意义的错误负载。因此，onError应始终定义订阅者的方法。

|      | 如果未定义，onError则抛出UnsupportedOperationException. 你可以使用该方法进一步检测和分类它Exceptions.isErrorCallbackNotImplemented。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

Reactor 还提供了处理链中间错误的替代方法，作为错误处理运算符。以下示例显示了如何执行此操作：

```java
Flux.just(1, 2, 0)
    .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
    .onErrorReturn("Divided by zero :("); // error handling example
```

|      | 在学习错误处理运算符之前，你必须记住， 响应序列中的任何错误都是终端事件。即使使用了错误处理运算符，它也不会让原始序列继续下去。相反，它将onError信号转换为新序列的开始(后备序列)。换句话说，它取代了它上游的终止序列。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

现在我们可以一一考虑每种错误处理的方法。当相关时，我们与命令式编程的try模式进行平行。

#### 4.6.1 错误处理运算符

你可能熟悉在 try-catch 块中处理异常的几种方法。最值得注意的是，这些包括以下内容：

-   捕获并返回静态默认值。
-   使用回退方法捕获并执行替代路径。
-   捕获并动态计算后备值。
-   抓住，换成BusinessException，然后重新抛出。
-   捕获、记录特定于错误的消息，然后重新抛出。
-   使用finally块来清理资源或Java7 “try-with-resource”结构。

所有这些都在Reactor中以错误处理运算符的形式等效。在研究这些运算符之前，我们首先要在响应链和 try-catch 块之间建立一个并行。

订阅时，onError链末端的回调类似于一个catch 块。在那里，如果抛出 an，执行会跳到 catch Exception，如以下示例所示：

```java
Flux<String> s = Flux.range(1, 10)
    .map(v -> doSomethingDangerous(v)) 
    .map(v -> doSecondTransform(v)); 
s.subscribe(value -> System.out.println("RECEIVED " + value), 
            error -> System.err.println("CAUGHT " + error) 
);
```

|      | 执行可能引发异常的转换。                 |
| ---- | ---------------------------------------- |
|      | 如果一切顺利，将执行第二次转换。         |
|      | 每个成功转换的值都被打印出来。           |
|      | 如果出现错误，序列将终止并显示错误消息。 |

前面的示例在概念上类似于以下 try-catch 块：

```java
try {
    for (int i = 1; i < 11; i++) {
        String v1 = doSomethingDangerous(i); 
        String v2 = doSecondTransform(v1); 
        System.out.println("RECEIVED " + v2);
    }
} catch (Throwable t) {
    System.err.println("CAUGHT " + t); 
}
```

|      | 如果这里抛出异常…… |
| ---- | ------------------ |
|      | …跳过其余的循环…   |
|      | ……执行直接到这里。 |

现在我们已经建立了一个并行，我们可以看看不同的错误处理案例和它们的等效运算符。

##### 静态回退值

“捕获并返回静态默认值”的等价物是onErrorReturn. 以下示例显示了如何使用它：

```java
try {
  return doSomethingDangerous(10);
}
catch (Throwable error) {
  return "RECOVERED";
}
```

以下示例显示了Reactor等效项：

```java
Flux.just(10)
    .map(this::doSomethingDangerous)
    .onErrorReturn("RECOVERED");
```

你还可以选择Predicate对异常应用 a 来决定是否恢复，如以下示例所示：

```java
Flux.just(10)
    .map(this::doSomethingDangerous)
    .onErrorReturn(e -> e.getMessage().equals("boom10"), "recovered10"); 
```

|      | 仅当异常消息为"boom10" |
| ---- | ------------------------ |
|      |                          |

##### 抓住并吞下错误

如果你甚至不想用回退值替换异常，而是忽略它并只传播到目前为止已经产生的元素，那么你想要的本质上就是用onError信号替换onComplete信号。这可以由onErrorComplete操作员完成：

```java
Flux.just(10,20,30)
    .map(this::doSomethingDangerousOn30)
    .onErrorComplete(); 
```

|      | 通过将onError变成onComplete |
| ---- | ------------------------------- |
|      |                                 |

Like onErrorReturn,onErrorComplete具有允许你根据异常的类或基于Predicate.

##### 后备方法

如果你想要多个默认值并且你有另一种(更安全)的方式来处理你的数据，你可以使用onErrorResume. 这相当于“使用回退方法捕获并执行替代路径”。

例如，如果你的名义进程正在从外部且不可靠的服务中获取数据，但你还保留了相同数据的本地缓存，这些数据可能有点过时但更可靠，你可以执行以下操作：

```java
String v1;
try {
  v1 = callExternalService("key1");
}
catch (Throwable error) {
  v1 = getFromCache("key1");
}

String v2;
try {
  v2 = callExternalService("key2");
}
catch (Throwable error) {
  v2 = getFromCache("key2");
}
```

以下示例显示了Reactor等效项：

```java
Flux.just("key1", "key2")
    .flatMap(k -> callExternalService(k) 
        .onErrorResume(e -> getFromCache(k)) 
    );
```

|      | 对于每个键，异步调用外部服务。                               |
| ---- | ------------------------------------------------------------ |
|      | 如果外部服务调用失败，则回退到该键的缓存。请注意，无论源错误 ,e是什么，我们总是应用相同的回退。 |

Like onErrorReturn,onErrorResume具有允许你根据异常的类或基于Predicate. 它需要 a 的事实Function还允许你根据遇到的错误选择不同的回退序列来切换。以下示例显示了如何执行此操作：

```java
Flux.just("timeout1", "unknown", "key2")
    .flatMap(k -> callExternalService(k)
        .onErrorResume(error -> { 
            if (error instanceof TimeoutException) 
                return getFromCache(k);
            else if (error instanceof UnknownKeyException)  
                return registerNewEntry(k, "DEFAULT");
            else
                return Flux.error(error); 
        })
    );
```

|      | 该功能允许动态选择如何继续。           |
| ---- | -------------------------------------- |
|      | 如果源超时，则命中本地缓存。           |
|      | 如果来源说密钥未知，请创建一个新条目。 |
|      | 在所有其他情况下，“重新抛出”。         |

##### 动态后备值

即使你没有替代(更安全)的数据处理方式，你也可能希望从收到的异常中计算回退值。这相当于“捕获并动态计算后备值”。

例如，如果你的返回类型 ( MyWrapper) 有一个专用于保持异常的变体(想想 Future.complete(T success)vs Future.completeExceptionally(Throwable error))，你可以实例化保持错误的变体并传递异常。

一个命令式示例如下所示：

```java
try {
  Value v = erroringMethod();
  return MyWrapper.fromValue(v);
}
catch (Throwable error) {
  return MyWrapper.fromError(error);
}
```

onErrorResume你可以通过使用, 和少量样板，以与后备方法解决方案相同的方式响应性地执行此操作，如下所示：

```java
erroringFlux.onErrorResume(error -> Mono.just( 
        MyWrapper.fromError(error) 
));
```

|      | 由于你期望MyWrapper错误的表示，因此你需要获取 Mono<MyWrapper>for onErrorResume。我们Mono.just()为此使用。 |
| ---- | ------------------------------------------------------------ |
|      | 我们需要计算出异常的值。在这里，我们通过使用相关的MyWrapper工厂方法包装异常来实现这一点。 |

##### 抓住并重新抛出

“Catch, wrap to a BusinessException, and re-throw” 在命令式世界中如下所示：

```java
try {
  return callExternalService(k);
}
catch (Throwable error) {
  throw new BusinessException("oops, SLA exceeded", error);
}
```

在“后备方法”示例中，最后一行中的最后一行flatMap给了我们以响应方式实现相同的提示，如下所示：

```java
Flux.just("timeout1")
    .flatMap(k -> callExternalService(k))
    .onErrorResume(original -> Flux.error(
            new BusinessException("oops, SLA exceeded", original))
    );
```

但是，有一种更直接的方法可以实现相同的效果onErrorMap：

```java
Flux.just("timeout1")
    .flatMap(k -> callExternalService(k))
    .onErrorMap(original -> new BusinessException("oops, SLA exceeded", original));
```

##### 记录或响应在一边

对于你希望错误继续传播但仍想对其做出响应而不修改序列(例如记录它)的情况，你可以使用doOnError 运算符。这等效于“捕获、记录特定于错误的消息并重新抛出”模式，如以下示例所示：

```java
try {
  return callExternalService(k);
}
catch (RuntimeException error) {
  //make a record of the error
  log("uh oh, falling back, service failed for key " + k);
  throw error;
}
```

doOnError运算符以及所有前缀为的运算符doOn有时被称为具有“副作用”。它们让你无需修改即可窥视序列的事件。

与前面显示的命令式示例一样，以下示例仍然传播错误，但确保我们至少记录外部服务发生故障：

```java
LongAdder failureStat = new LongAdder();
Flux<String> flux =
Flux.just("unknown")
    .flatMap(k -> callExternalService(k) 
        .doOnError(e -> {
            failureStat.increment();
            log("uh oh, falling back, service failed for key " + k); 
        })
        
    );
```

|      | 可能失败的外部服务调用……                                     |
| ---- | ------------------------------------------------------------ |
|      | …装饰有日志记录和统计信息的副作用…                           |
|      | …之后，它仍然会以错误终止，除非我们在这里使用错误恢复运算符。 |

我们也可以想象我们有统计计数器来增加第二个错误的副作用。

##### 使用资源和 finally 块

使用命令式编程绘制的最后一个平行点是清理，可以通过使用“使用finally块清理资源”或使用“Java 7 try-with-resource 构造”来完成，两者都如下所示：

示例 14. finally 的命令式使用

```java
Stats stats = new Stats();
stats.startTimer();
try {
  doSomethingDangerous();
}
finally {
  stats.stopTimerAndRecordTiming();
}
```

示例 15. 强制使用 try-with-resource

```java
try (SomeAutoCloseable disposableInstance = new SomeAutoCloseable()) {
  return disposableInstance.toString();
}
```

两者都有其Reactor等价物：doFinally和using.

doFinallyonComplete是关于你希望在序列终止(使用or onError)或被取消时执行的副作用。它会提示你哪种终止触发了副作用。下面的例子展示了如何使用doFinally：

最后响应：doFinally()

```java
Stats stats = new Stats();
LongAdder statsCancel = new LongAdder();

Flux<String> flux =
Flux.just("foo", "bar")
    .doOnSubscribe(s -> stats.startTimer())
    .doFinally(type -> { 
        stats.stopTimerAndRecordTiming();
        if (type == SignalType.CANCEL) 
          statsCancel.increment();
    })
    .take(1); 
```

|      | doFinallySignalType为终止类型消耗 a 。   |
| ---- | -------------------------------------------- |
|      | 与块类似finally，我们总是记录时间。        |
|      | 在这里，我们还仅在取消的情况下增加统计信息。 |
|      | take(1)发出一项后取消。                    |

另一方面，using处理Flux从资源派生的情况，并且每当处理完成时都必须对该资源采取行动。在以下示例中，我们将AutoCloseable“try-with-resource”的接口替换为 Disposable：

示例 16. Disposable 资源

```java
AtomicBoolean isDisposed = new AtomicBoolean();
Disposable disposableInstance = new Disposable() {
    @Override
    public void dispose() {
        isDisposed.set(true); 
    }

    @Override
    public String toString() {
        return "DISPOSABLE";
    }
};
```

现在我们可以在其上执行“try-with-resource”的响应式等效操作，如下所示：

示例 17. 响应式 try-with-resource：using()

```java
Flux<String> flux =
Flux.using(
        () -> disposableInstance, 
        disposable -> Flux.just(disposable.toString()), 
        Disposable::dispose 
);
```

|      | 第一个 lambda 生成资源。在这里，我们返回我们的 mock Disposable。 |
| ---- | ------------------------------------------------------------ |
|      | 第二个 lambda 处理资源，返回一个Flux<T>.                   |
|      | 第三个 lambda 在Fluxfrom <2> 终止或被取消时调用，以清理资源。 |
|      | 订阅并执行序列后，isDisposed原子布尔值变为true.          |

##### 展示终端方面onError

为了证明所有这些操作符都会导致上游原始序列在错误发生时终止，我们可以使用一个更直观的示例 Flux.interval。interval操作员每 x 单位时间以递增的 值Long打勾。以下示例使用interval运算符：

```java
Flux<String> flux =
Flux.interval(Duration.ofMillis(250))
    .map(input -> {
        if (input < 3) return "tick " + input;
        throw new RuntimeException("boom");
    })
    .onErrorReturn("Uh oh");

flux.subscribe(System.out::println);
Thread.sleep(2100); 
```

|      | 请注意，默认情况下在计时器interval上执行。如果我们想在主类中运行该示例，我们需要在此处添加一个调用，以便应用程序不会立即退出而不产生任何值。 Schedulersleep |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

前面的示例每 250 毫秒打印出一行，如下所示：

```none
tick 0
tick 1
tick 2
Uh oh
```

即使有额外一秒的运行时间，也不会再从interval. 该序列确实因错误而终止。

##### 重试

还有一个与错误处理有关的运算符感兴趣，你可能很想在上一节中描述的情况下使用它。retry，顾名思义，让你重试产生错误的序列。

要记住的是，它通过重新订阅upstream 来工作Flux。这真的是一个不同的序列，原来的序列仍然被终止。为了验证这一点，我们可以重复使用前面的示例并附加 aretry(1)以重试一次，而不是使用onErrorReturn. 以下示例显示了如何执行 sl：

```java
Flux.interval(Duration.ofMillis(250))
    .map(input -> {
        if (input < 3) return "tick " + input;
        throw new RuntimeException("boom");
    })
    .retry(1)
    .elapsed() 
    .subscribe(System.out::println, System.err::println); 

Thread.sleep(2100); 
```

|      | elapsed将每个值与自发出前一个值以来的持续时间相关联。 |
| ---- | ------------------------------------------------------- |
|      | 我们还想看看什么时候有onError.                        |
|      | 确保我们有足够的时间进行 4x2 滴答。                     |

前面的示例产生以下输出：

```none
259,tick 0
249,tick 1
251,tick 2
506,tick 0 
248,tick 1
253,tick 2
java.lang.RuntimeException: boom
```

|      | 一个新的interval开始，从第 0 个滴答开始。额外的 250 毫秒持续时间来自第 4 个滴答，即导致异常和后续重试的那个。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

从前面的例子可以看出，retry(1)只是重新订阅了interval 一次，从 0 重新开始计时。第二次，由于异常仍然发生，它放弃并将错误传播到下游。

有一个更高级的retry(称为retryWhen) 版本，它使用“伴侣” Flux来判断特定失败是否应该重试。该伴侣Flux由操作员创建但由用户修饰，以自定义重试条件。

同伴Flux是Flux<RetrySignal>传递给Retry策略/函数的，作为 的唯一参数提供retryWhen。作为用户，你定义该函数并使其返回一个新的 Publisher<?>. 该类Retry是一个抽象类，但如果你想用一个简单的 lambda ( Retry.from(Function)) 转换同伴，它提供了一个工厂方法。

重试周期如下：

1.  每次发生错误(可能会重试)时，都会将 a 发送到由你的函数修饰RetrySignal的同伴中。Flux拥有一个Flux这里可以鸟瞰迄今为止的所有尝试。RetrySignal可以访问错误及其周围的元数据。
2.  如果同伴Flux发出一个值，则会发生重试。
3.  如果同伴Flux完成，错误被吞下，重试循环停止，结果序列也完成。
4.  如果伴随程序Flux产生错误 ( e)，重试循环将停止并且生成的序列错误并带有e。

前两种情况之间的区别很重要。简单地完成同伴将有效地吞下一个错误。考虑使用以下模拟 retry(3)方式retryWhen：

```java
Flux<String> flux = Flux
    .<String>error(new IllegalArgumentException()) 
    .doOnError(System.out::println) 
    .retryWhen(Retry.from(companion -> 
        companion.take(3))); 
```

|      | 这会不断产生错误，要求重试。                              |
| ---- | --------------------------------------------------------- |
|      | doOnError在重试之前让我们记录并查看所有失败。           |
|      | Retry改编自一个非常简单的Functionlambda               |
|      | 在这里，我们将前三个错误视为可重试(take(3))然后放弃。 |

实际上，前面的示例结果为空Flux，但它成功完成。由于 retry(3)on the sameFlux会因最新错误而终止，因此此 retryWhen示例与 a 不完全相同retry(3)。

达到相同的行为涉及一些额外的技巧：

```java
AtomicInteger errorCount = new AtomicInteger();
Flux<String> flux =
		Flux.<String>error(new IllegalArgumentException())
				.doOnError(e -> errorCount.incrementAndGet())
				.retryWhen(Retry.from(companion -> 
						companion.map(rs -> { 
							if (rs.totalRetries() < 3) return rs.totalRetries(); 
							else throw Exceptions.propagate(rs.failure()); 
						})
				));
```

|      | 我们Retry通过适应Functionlambda 而不是提供具体类来定制   |
| ---- | ------------------------------------------------------------ |
|      | 同伴发出RetrySignal对象，这些对象承担到目前为止的重试次数和最后一次失败 |
|      | 为了允许重试三次，我们考虑索引 < 3 并返回一个要发出的值(这里我们简单地返回索引)。 |
|      | 为了终止错误的序列，我们在这三次重试后抛出原始异常。         |

|      | 可以使用暴露的构建器Retry以更流畅的方式实现相同的目标，以及更精细的重试策略。例如：errorFlux.retryWhen(Retry.max(3));。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 你可以使用类似的代码来实现“指数退避和重试”模式，如[常见问题解答](https://projectreactor.io/docs/core/release/reference/#faq.exponentialBackoff)中所示。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

核心提供的Retry帮助器RetrySpec和RetryBackoffSpec都允许高级自定义，例如：

-   filter(Predicate)为可以触发重试的异常设置
-   通过修改这样的先前设置的过滤器modifyErrorFilter(Function)
-   触发副作用，例如记录重试触发器(即延迟之前和之后的退避)，前提是重试得到验证(doBeforeRetry()并且doAfterRetry()是附加的)
-   在重试触发器周围触发异步Mono<Void>，这允许在基本延迟之上添加异步行为，但因此会进一步延迟触发器(doBeforeRetryAsync并且doAfterRetryAsync是附加的)
-   在达到最大尝试次数的情况下自定义异常，通过onRetryExhaustedThrow(BiFunction). 默认情况下，Exceptions.retryExhausted(…)使用，可以用Exceptions.isRetryExhausted(Throwable)
-   激活瞬态错误的处理(见下文)

###### 重试出现暂时性错误

一些长期存在的源可能会看到零星的错误爆发，然后是更长的时间段，在此期间一切都在顺利运行。本文档将这种错误模式称为瞬态错误。

在这种情况下，最好单独处理每个突发，以便下一个突发不会继承前一个突发的重试状态。例如，使用指数退避策略，每个后续突发应该延迟从最小退避开始的重试尝试，Duration而不是不断增长的退避。

RetrySignal表示retryWhen状态的接口具有totalRetriesInARow()可用于此的值。与通常的单调递增totalRetries()索引不同，每次重试从错误中恢复时(即，当重试尝试导致传入onNext而不是onError再次出现时)，此二级索引都会重置为 0。

将transientErrors(boolean)配置参数设置true为RetrySpecorRetryBackoffSpec时，生成的策略会使用该totalRetriesInARow()索引，有效地处理瞬态错误。这些规范根据索引计算重试模式，因此规范的所有其他配置参数实际上独立应用于每个错误突发。

```java
AtomicInteger errorCount = new AtomicInteger(); 
Flux<Integer> transientFlux = httpRequest.get() 
        .doOnError(e -> errorCount.incrementAndGet());

transientFlux.retryWhen(Retry.max(2).transientErrors(true))  
             .blockLast();
assertThat(errorCount).hasValue(6); 
```

|      | 我们将计算重试序列中的错误数以进行说明。                     |
| ---- | ------------------------------------------------------------ |
|      | 我们假设一个http请求源，例如。一个流式端点，有时会连续失败两次，然后恢复。 |
|      | 我们retryWhen在该源上使用，配置为最多 2 次重试尝试，但处于transientErrors模式。 |
|      | 最后，获得有效响应，并在尝试注册transientFlux后成功完成。6errorCount |

如果没有transientErrors(true)，第二次突发将超过配置的最大尝试次数，2整个序列最终将失败。

|      | 如果你想在没有实际 http 远程端点的情况下在本地尝试此操作，则可以将伪httpRequest方法实现为 a Supplier，如下所示：final AtomicInteger transientHelper = new AtomicInteger(); Supplier<Flux<Integer>> httpRequest = () ->    Flux.generate(sink -> {         int i = transientHelper.getAndIncrement();        if (i == 10) {             sink.next(i);            sink.complete();        }        else if (i % 3 == 0) {             sink.next(i);        }        else {            sink.error(new IllegalStateException("Transient error at " + i));         }    });我们generate是一个有突发错误的来源。当计数器达到 10 时，它将成功完成。如果transientHelper原子是 的倍数3，我们发出onNext并结束当前的突发。在其他情况下，我们发出一个onError. 这是 3 次中的 2 次，所以 2 的爆发onError被 1 打断onNext。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 4.6.2 处理运算符或函数中的异常

通常，所有运算符本身都可以包含可能触发异常的代码或对用户定义的回调的调用，这些回调可能同样失败，因此它们都包含某种形式的错误处理。

根据经验，未经检查的异常总是通过onError. 例如，在函数RuntimeException内部抛出 amap会转换为 onError事件，如以下代码所示：

```java
Flux.just("foo")
    .map(s -> { throw new IllegalArgumentException(s); })
    .subscribe(v -> System.out.println("GOT VALUE"),
               e -> System.out.println("ERROR: " + e));
```

前面的代码打印出以下内容：

```none
ERROR: java.lang.IllegalArgumentException: foo
```

|      | 你可以Exception在它被传递到之前调整它onError，通过使用一个 [钩子](https://projectreactor.io/docs/core/release/reference/#hooks-internal)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

OutOfMemoryError然而，Reactor 定义了一组总是被认为是致命的异常(例如)。见Exceptions.throwIfFatal方法。这些错误意味着Reactor无法继续运行并被抛出而不是传播。

|      | 在内部，还有一些情况仍然无法传播未经检查的异常(最明显的是在订阅和请求阶段)，因为并发竞争可能导致双重onError或onComplete条件。当这些竞争发生时，无法传播的错误被“丢弃”。这些情况仍然可以通过使用可定制的钩子在某种程度上进行管理。请参阅[丢弃挂钩](https://projectreactor.io/docs/core/release/reference/#hooks-dropping)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

你可能会问：“检查异常呢？”

例如，如果你需要调用某个声明throws异常的方法，你仍然必须在块中处理这些异常try-catch。但是，你有多种选择：

1.  捕获异常并从中恢复。序列正常继续。
2.  捕获异常，将其包装成未经检查的异常，然后将其抛出(中断序列)。实用程序类可以帮助你解决这个Exceptions问题(我们接下来会介绍)。
3.  如果你需要返回 a Flux(例如，你在 a 中flatMap)，请将异常包装在产生错误的 中Flux，如下所示return Flux.error(checkedException)：(序列也终止。)

Reactor 有一个Exceptions实用程序类，你可以使用它来确保仅在检查异常时才包装异常：

-   如有必要，使用该Exceptions.propagate方法包装异常。它也调用 throwIfFatalfirst 并且不换行RuntimeException。
-   使用该Exceptions.unwrap方法获取原始解包异常(回到特定于响应器的异常层次结构的根本原因)。

考虑以下map使用可以抛出 的转换方法 的 a 示例IOException：

```java
public String convert(int i) throws IOException {
    if (i > 3) {
        throw new IOException("boom " + i);
    }
    return "OK " + i;
}
```

现在假设你想在map. 你现在必须显式捕获异常，并且你的 map 函数不能重新抛出它。因此，你可以将其作为 a 传播到地图的onError方法中RuntimeException，如下所示：

```java
Flux<String> converted = Flux
    .range(1, 10)
    .map(i -> {
        try { return convert(i); }
        catch (IOException e) { throw Exceptions.propagate(e); }
    });
```

稍后，在订阅上述内容Flux并对错误做出响应时(例如在 UI 中)，如果你想为 IOExceptions 做一些特殊的事情，你可以恢复到原始异常。以下示例显示了如何执行此操作：

```java
converted.subscribe(
    v -> System.out.println("RECEIVED: " + v),
    e -> {
        if (Exceptions.unwrap(e) instanceof IOException) {
            System.out.println("Something bad happened with I/O");
        } else {
            System.out.println("Something bad happened");
        }
    }
);
```

### 4.7 处理器和接收器

处理器是一种特殊的类型Publisher，也是Subscriber. 它们最初旨在作为中间步骤的可能表示，然后可以在 Reactive Streams 实现之间共享。然而，在Reactor中，这些步骤由Publisher.

第一次遇到的一个常见错误是直接从接口Processor调用暴露的onNext,onComplete和onError方法的诱惑。Subscriber

应该小心进行此类手动调用，尤其是关于响应式流规范的调用外部同步。处理器实际上可能有点用处，除非遇到需要Subscriber传递 a 的基于响应式流的 API，而不是公开Publisher.

水槽通常是更好的选择。在Reactor中，sink 是一个允许安全手动触发信号的类。它可以与订阅相关联(从运营商内部)或完全独立。

由于3.4.0，sinks 成为一等公民并被Processor完全淘汰：

-   抽象和具体FluxProcessor，MonoProcessor已弃用并计划在 3.5.0 中删除
-   不是由操作员产生的接收器是通过Sinks类中的工厂方法构造的。
-   我们希望所有处理器的使用都可以用现有的运营商或来自Sinks. 用户有时间在 3.5 之前发现情况并非如此，同时回退到使用已弃用的 API。

#### 4.7.1. 通过使用Sinks.One和安全地从多个线程生产Sinks.Many

reactor-core 公开的默认风格Sinks确保检测到多线程使用，并且不会导致从下游订阅者的角度来看违反规范或未定义的行为。使用tryEmitAPI 时，并行调用会很快失败。使用emit API 时，提供的EmissionFailureHandler可能允许重试争用(例如忙循环)，否则接收器将因错误而终止。

这是对 的改进Processor.onNext，它必须在外部同步，否则从下游订阅者的角度来看会导致未定义的行为。

构建器Sinks为主要支持的生产者类型提供了一个引导式 API。你将认识到在Flux诸如onBackpressureBuffer.

```java
Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
```

多个生产者线程可以通过执行以下操作同时在接收器上生成数据：

```java
//thread1
replaySink.emitNext(1, FAIL_FAST);

//thread2, later
replaySink.emitNext(2, FAIL_FAST);

//thread3, concurrently with thread 2
EmitResult result = replaySink.tryEmitNext(3); //would return FAIL_NON_SERIALIZED
```

Sinks.Many可以作为 呈现给下游消费者，Flux如下例所示：

```java
Flux<Integer> fluxView = replaySink.asFlux();
fluxView
	.takeWhile(i -> i < 10)
	.log()
	.blockLast();
```

类似地，Sinks.Empty和Sinks.One风味可以看作是一种Mono与asMono()方法。

Sinks类别是：

1.  many().multicast()：一个接收器，它只会将新推送的数据传输给它的订阅者，尊重他们的背压(新推送，如“订阅者订阅之后”)。
2.  many().unicast(): 和上面一样，只是在第一个订阅者注册之前推送的数据被缓冲了。
3.  many().replay()：一个接收器，它将向新订阅者重播指定历史大小的推送数据，然后继续实时推送新数据。
4.  one()：将向其订阅者播放单个元素的接收器
5.  empty(): 只向其订阅者播放终端信号(错误或完成)的接收器，但仍可以视为一个Mono<T>(注意泛型类型<T>)。

#### 4.7.2 可用接收器概述

##### Sinks.many().unicast().onBackpressureBuffer(args?)

单播Sinks.Many可以通过使用内部缓冲区来处理背压。权衡是它最多可以有一个 Subscriber.

基本的单播接收器是通过创建的Sinks.many().unicast().onBackpressureBuffer()。但是还有一些额外的unicast静态工厂方法Sinks.many().unicast()可以进行更精细的调整。

例如，默认情况下，它是无界的：如果你在它Subscriber尚未请求数据时通过它推送任何数量的数据，它会缓冲所有数据。你可以通过为工厂方法Queue中的内部缓冲提供自定义实现来更改此设置。Sinks.many().unicast().onBackpressureBuffer(Queue)如果该队列是有界的，则当缓冲区已满且未收到来自下游的足够请求时，接收器可能会拒绝推送值。

##### Sinks.many().multicast().onBackpressureBuffer(args?)

多播Sinks.Many可以发送给多个订阅者，同时为每个订阅者提供背压。订阅者在订阅后仅接收通过接收器推送的信号。

基本的多播接收器是通过创建的Sinks.many().multicast().onBackpressureBuffer()。

默认情况下，如果它的所有订阅者都被取消(这基本上意味着它们都已取消订阅)，它会清除其内部缓冲区并停止接受新订阅者。你可以通过使用静态工厂方法autoCancel中的参数来调整它。multicastSinks.many().multicast()

##### Sinks.many().multicast().directAllOrNothing()

Sinks.Many对背压进行简单处理的多播：如果任何订阅者太慢(需求为零)，则所有订阅者都将onNext被丢弃。

但是，慢速订阅者不会被终止，一旦慢速订阅者再次开始请求，所有订阅者都将继续接收从那里推送的元素。

一旦Sinks.Many终止(通常通过调用它的emitError(Throwable)or emitComplete()方法)，它会允许更多订阅者订阅，但会立即向他们重放终止信号。

##### Sinks.many().multicast().directBestEffort()

尽最大努力处理背压的多播Sinks.Many：如果订阅者太慢(需求为零)，则仅针对该慢速订阅者onNext丢弃。

但是，慢速订阅者不会被终止，一旦他们再次开始请求，他们将继续接收新推送的元素。

一旦Sinks.Many终止(通常通过调用它的emitError(Throwable)or emitComplete()方法)，它会允许更多订阅者订阅，但会立即向他们重放终止信号。

##### sinks.many().replay()

重播Sinks.Many缓存发出的元素并将它们重播给迟到的订阅者。

它可以在多种配置中创建：

-   缓存有限历史 ( Sinks.many().replay().limit(int)) 或无限历史 ( Sinks.many().replay().all())。
-   缓存基于时间的回放窗口 ( Sinks.many().replay().limit(Duration))。
-   缓存历史大小和时间窗口 ( Sinks.many().replay().limit(int, Duration)) 的组合。

还可以在 下找到用于微调上述内容的其他重载Sinks.many().replay()，以及允许缓存单个元素 (latest()和latestOrDefault(T)) 的变体。

##### sinks.unsafe().many()

高级用户和运营商构建者可能想要考虑使用Sinks.unsafe().many() 哪个将提供相同的Sinks.Many工厂，而无需额外的生产者线程安全。因此，每个接收器的开销将减少，因为线程安全接收器必须检测多线程访问。

库开发人员不应公开不安全的接收器，但可以在受控调用环境中内部使用它们，在这种环境中，他们可以确保 根据响应流规范，导致 和信号onNext的onComplete调用的外部同步。onError

##### Sinks.one()

该方法直接构造一个简单的Sinks.One<T>. 这种风格的Sinks可被视为Mono(通过其asMono()视图方法)，并且具有稍微不同的emit方法来更好地传达这种类似 Mono 的语义：

-   emitValue(T value)生成一个onNext(value)信号 - 在大多数实现中 - 也会触发一个隐式onComplete()
-   emitEmpty()生成一个孤立的onComplete()信号，旨在生成相当于一个空的信号Mono
-   emitError(Throwable t)产生一个onError(t)信号

Sinks.one()接受这些方法中的任何一个的调用，有效地生成一个Mono 以值完成、完成为空或失败的方法。

##### 水槽.empty()

该方法直接构造一个简单的Sinks.Empty<T>. 这种风格Sinks类似于Sinks.One<T>，只是它不提供emitValue方法。

结果，它只能生成一个Mono完全为空或失败的。

<T>尽管无法触发，但sink 仍然使用泛型类型onNext，因为它允许轻松组合并包含在需要特定类型的运算符链中。

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/coreFeatures.adoc) 到“[响应堆核心特征](https://projectreactor.io/docs/core/release/reference/#core-features)”

## 5. Kotlin支持

[Kotlin](https://kotlinlang.org/)是一种针对 JVM(和其他平台)的静态类型语言，它允许编写简洁优雅的代码，同时提供 与用Java编写的现有库非常好的[互操作性。](https://kotlinlang.org/docs/reference/java-interop.html)

本节介绍Reactor对 Kotlin 的支持。

### 5.1 要求

Reactor 支持 Kotlin 1.1+ 并且需要 [kotlin-stdlib](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-stdlib) (或其[kotlin-stdlib-jdk7](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk7) 变体之一[kotlin-stdlib-jdk8](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8))。

### 5.2 扩展

|      | 从Dysprosium-M1(即reactor-core 3.3.0.M1)开始，Kotlin 扩展被移动到一个专用[reactor-kotlin-extensions](https://github.com/reactor/reactor-kotlin-extensions) 模块，新的包名称以开头reactor.kotlin而不是简单的reactor.因此，reactor-core模块中的 Kotlin 扩展已被弃用。新依赖的 groupId 和 artifactId 是：io.projectreactor.kotlin:reactor-kotlin-extensions |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

由于其出色的[Java 互操作性](https://kotlinlang.org/docs/reference/java-interop.html) 和[Kotlin 扩展](https://kotlinlang.org/docs/reference/extensions.html)，Reactor Kotlin API 利用了常规JavaAPI，并通过一些在Reactor工件中开箱即用的特定于 Kotlin 的 API 得到了额外的增强。

|      | 请记住，需要导入 Kotlin 扩展才能使用。这意味着例如Throwable.toFluxKotlin 扩展只有在import reactor.kotlin.core.publisher.toFlux被导入时才可用。也就是说，与静态导入类似，IDE 在大多数情况下应该自动建议导入。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

例如，[Kotlin reified 类型参数](https://kotlinlang.org/docs/reference/inline-functions.html#reified-type-parameters) 为 JVM[泛型类型擦除](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html)提供了一种解决方法，并且Reactor提供了一些扩展来利用这个特性。

下表将Reactor与Java与Reactor与 Kotlin 和扩展进行了比较：

| 爪哇                                     | 带有扩展的 Kotlin                             |
| -------------------------------------------- | ------------------------------------------------- |
| Mono.just("foo")                           | "foo".toMono()                                  |
| Flux.fromIterable(list)                    | list.toFlux()                                   |
| Mono.error(new RuntimeException())         | RuntimeException().toMono()                     |
| Flux.error(new RuntimeException())         | RuntimeException().toFlux()                     |
| flux.ofType(Foo.class)                     | flux.ofType<Foo>()或者flux.ofType(Foo::class) |
| StepVerifier.create(flux).verifyComplete() | flux.test().verifyComplete()                    |

[Reactor KDoc API](https://projectreactor.io/docs/kotlin/release/kdoc-api/)列出并记录了所有可用的 Kotlin 扩展。

### 5.3 零安全

Kotlin 的关键特性之一是[null 安全性](https://kotlinlang.org/docs/reference/null-safety.html)，它在编译时干净地处理值，而不是在运行时null碰到著名 的值。NullPointerException这通过可空性声明和表达的“值或无值”语义使应用程序更安全，而无需支付诸如Optional. (Kotlin 允许使用具有可为空值的函数构造。请参阅 [Kotlin null-safety 综合指南](https://www.baeldung.com/kotlin-null-safety)。)

尽管Java不允许在其类型系统中表达 null 安全性，但 Reactor[现在](https://projectreactor.io/docs/core/release/reference/#null-safety)reactor.util.annotation通过在包中声明的工具友好的注解提供整个ReactorAPI 的null 安全性。默认情况下，Kotlin 中使用的JavaAPI 的类型被识别为 放宽空检查的 [平台类型。](https://kotlinlang.org/docs/reference/java-interop.html#null-safety-and-platform-types)[Kotlin 对 JSR 305 注解](https://github.com/Kotlin/KEEP/blob/jsr-305/proposals/jsr-305-custom-nullability-qualifiers.md)null和Reactor可空性注解的支持为 Kotlin 开发人员提供了整个ReactorAPI 的空安全性，并具有在编译时 处理相关问题的优势。

-Xjsr305你可以通过添加带有以下选项的编译器标志来配置 JSR 305 检查： -Xjsr305={strict|warn|ignore}.

对于 kotlin 版本 1.1.50+，默认行为与-Xjsr305=warn. 该strict值需要考虑到ReactorAPI 的完全空安全性，但应该被视为实验性的，因为ReactorAPI 可空性声明甚至可以在次要版本之间演变，因为将来可能会添加更多检查)。

|      | 尚不支持泛型类型参数、变量参数和数组元素的可空性，但它应该在即将发布的版本中。 有关最新信息， 请参阅[此讨论。](https://github.com/Kotlin/KEEP/issues/79) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/kotlin.adoc) 到“ [Kotlin 支持](https://projectreactor.io/docs/core/release/reference/#kotlin)”

## 6. 测试

无论你是编写了简单的Reactor运算符链还是你自己的运算符，自动化测试始终是一个好主意。

Reactor 附带了一些专门用于测试的元素，这些元素收集到了他们自己的工件中：reactor-test. 你可以在存储库内的[Github 上](https://github.com/reactor/reactor-core/tree/main/reactor-test/src)找到该项目 reactor-core。

要在测试中使用它，你必须将其添加为测试依赖项。以下示例显示了如何reactor-test在 Maven 中添加为依赖项：

示例 18. Maven 中的 reactor-test，在<dependencies>

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
    
</dependency>
```

|      | 如果使用[BOM](https://projectreactor.io/docs/core/release/reference/#getting)，则无需指定<version>. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

以下示例显示了如何reactor-test在 Gradle 中添加为依赖项：

示例 19. Gradle 中的 reactor-test，修改dependencies块

```groovy
dependencies {
   testCompile 'io.projectreactor:reactor-test'
}
```

的三个主要用途reactor-test如下：

-   使用 .逐步测试序列是否遵循给定的场景StepVerifier。
-   生成数据以测试下游运营商(包括你自己的运营商)的行为TestPublisher。
-   在可以通过多个替代方案的序列中Publisher(例如，使用 的链 switchIfEmpty，探测这样的 aPublisher以确保它被使用(即订阅)。

### 6.1 测试一个场景StepVerifier

测试Reactor序列的最常见情况是在代码中定义 aFlux或 a Mono(例如，它可能由方法返回)并且想要测试它在订阅时的行为方式。

这种情况很好地转化为定义“测试场景”，你可以在其中一步一步地根据事件定义你的期望。你可以提出和回答以下问题：

-   下一个预期事件是什么？
-   你是否期望Flux发出特定的值？
-   或者也许在接下来的 300 毫秒内什么都不做？

你可以通过StepVerifierAPI 表达所有这些。

例如，你可以在代码库中使用以下实用方法来装饰 a Flux：

```java
public <T> Flux<T> appendBoomError(Flux<T> source) {
  return source.concatWith(Mono.error(new IllegalArgumentException("boom")));
}
```

为了对其进行测试，你需要验证以下场景：

>   我希望这Flux首先发出thing1，然后发出thing2，然后在消息中产生错误，boom。订阅并验证这些期望。

在StepVerifierAPI 中，这转换为以下测试：

```java
@Test
public void testAppendBoomError() {
  Flux<String> source = Flux.just("thing1", "thing2"); 

  StepVerifier.create( 
    appendBoomError(source)) 
    .expectNext("thing1") 
    .expectNext("thing2")
    .expectErrorMessage("boom") 
    .verify(); 
}
```

|      | 由于我们的方法需要一个 source Flux，因此定义一个简单的用于测试目的。 |
| ---- | ------------------------------------------------------------ |
|      | 创建一个StepVerifier包装和验证Flux.                      |
|      | 通过Flux要测试的(调用我们的实用程序方法的结果)。         |
|      | 我们期望在订阅时发生的第一个信号是onNext，值为thing1。   |
|      | 我们期望发生的最后一个信号是序列以 onError. 异常应该boom作为消息。 |
|      | 通过调用来触发测试很重要verify()。                         |

API 是一个构建器。你首先创建一个StepVerifier并传递要测试的序列。这提供了一种方法选择，让你：

-   表达对下一个信号的预期。如果接收到任何其他信号(或信号的内容与预期不符)，则整个测试以有意义的AssertionError. 例如，你可以使用expectNext(T…)and expectNextCount(long)。
-   消费下一个信号。当你想要跳过部分序列或想要对assertion信号的内容应用自定义时使用此选项(例如，检查是否存在onNext事件并断言发出的项目是大小为 5 的列表) . 例如，你可以使用consumeNextWith(Consumer<T>).
-   采取杂项操作，例如暂停或运行任意代码。例如，如果你想操作特定于测试的状态或上下文。为此，你可以使用 thenAwait(Duration)and then(Runnable)。

对于终端事件，相应的期望方法(expectComplete()及其 expectError()所有变体)切换到你无法再表达期望的 API。在最后一步中，你所能做的就是在其上执行一些额外的配置StepVerifier，然后触发验证，通常使用verify()其变体或其中一个变体。

此时发生的情况是StepVerifier订阅测试Flux或 Mono播放序列，将每个新信号与场景中的下一步进行比较。只要这些匹配，测试就被认为是成功的。一旦有差异，AssertionError就会抛出一个。

|      | 记住verify()触发验证的步骤。为了提供帮助，API 包含一些快捷方法，将终端期望与对verify(): verifyComplete()、verifyError()、verifyErrorMessage(String)等的调用结合起来。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

请注意，如果其中一个基于 lambda 的期望抛出一个AssertionError，则按原样报告，未通过测试。这对于自定义断言很有用。

|      | 默认情况下，该verify()方法和派生的快捷方法(verifyThenAssertThat、 verifyComplete()等)没有超时。他们可以无限期地阻止。你可以使用 StepVerifier.setDefaultTimeout(Duration)为这些方法全局设置超时，或使用 为每次调用指定一个超时verify(Duration)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 6.1.1 更好地识别测试失败

StepVerifier提供了两个选项来更好地确定哪个期望步骤导致测试失败：

-   as(String): 在大多数expect方法之后使用，以描述前面的期望。如果期望失败，则其错误消息包含描述。终端期望，verify不能这样描述。
-   StepVerifierOptions.create().scenarioName(String): 通过使用StepVerifierOptionsto create your StepVerifier，你可以使用该scenarioName方法为整个场景命名，该名称也用于断言错误消息中。

请注意，在这两种情况下，消息中描述或名称的使用仅保证用于 StepVerifier产生自己的方法AssertionError(例如，手动抛出异常或通过断言库 inassertNext不会将描述或名称添加到错误消息中)。

### 6.2 操纵时间

你可以使用StepVerifier基于时间的运算符来避免相应测试的长时间运行。你可以通过StepVerifier.withVirtualTime构建器执行此操作。

它看起来像以下示例：

```java
StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
//... continue expectations here
```

这个虚拟时间功能插入了SchedulerReactorSchedulers 工厂中的自定义项。由于这些定时运算符通常使用默认Schedulers.parallel() 调度程序，因此将其替换为 aVirtualTimeScheduler就可以了。然而，一个重要的先决条件是在虚拟时间调度程序被激活之后，操作符被实例化。

为了增加正确发生这种情况的机会，StepVerifier不接受简单Flux的输入。withVirtualTime需要 a ，它会引导你在完成调度程序设置后Supplier懒惰地创建测试通量的实例。

|      | 请格外小心，以确保Supplier<Publisher<T>>可以以懒惰的方式使用它。否则，无法保证虚拟时间。尤其要避免 Flux在测试代码中实例化早期并Supplier返回该变量。相反，始终实例化Fluxlambda 内部。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

有两种处理时间的期望方法，无论有无虚拟时间，它们都有效：

-   thenAwait(Duration)：暂停对步骤的评估(允许出现一些信号或延迟耗尽)。
-   expectNoEvent(Duration)：也让序列在给定的持续时间内播放，但如果在此期间出现任何信号，则测试失败。

两种方法都在经典模式下暂停线程给定的持续时间，并在虚拟模式下提前虚拟时钟。

|      | expectNoEvent也将subscription视为一个事件。如果你将其用作第一步，它通常会因为检测到订阅信号而失败。改为使用 expectSubscription().expectNoEvent(duration)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

为了快速评估我们Mono.delay上面的行为，我们可以完成编写代码如下：

```java
StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
    .expectSubscription() 
    .expectNoEvent(Duration.ofDays(1)) 
    .expectNext(0L) 
    .verifyComplete(); 
```

|      | 请参阅前面的[提示](https://projectreactor.io/docs/core/release/reference/#tip-expectNoEvent)。 |
| ---- | ------------------------------------------------------------ |
|      | 期待一整天什么都不会发生。                                   |
|      | 然后期待一个发出的延迟0。                                  |
|      | 然后期待完成(并触发验证)。                                 |

我们可以在thenAwait(Duration.ofDays(1))上面使用，但expectNoEvent它的好处是保证没有任何事情发生在它应该有的时间之前。

注意verify()返回一个Duration值。这是整个测试的实时持续时间。

|      | 虚拟时间不是灵丹妙药。全部Schedulers替换为相同的VirtualTimeScheduler. 在某些情况下，你可以锁定验证过程，因为在表示期望之前虚拟时钟尚未向前移动，导致期望等待只能通过提前时间产生的数据。在大多数情况下，你需要提前虚拟时钟才能发出序列。虚拟时间也因无限序列而变得非常有限，这可能会占用运行序列及其验证的线程。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 6.3 执行执行后断言StepVerifier

在描述了场景的最终预期之后，你可以切换到补充断言 API 而不是触发verify()。为此，请 verifyThenAssertThat()改用。

verifyThenAssertThat()返回一个StepVerifier.Assertions对象，一旦整个场景成功播放，你就可以使用它来断言状态的一些元素(因为它也调用verify())。典型的(尽管是高级的)用法是捕获已被某些操作员删除的元素并断言它们(参见 [Hooks](https://projectreactor.io/docs/core/release/reference/#hooks)部分)。

### 6.4 测试Context

有关 的更多信息Context，请参阅[向响应序列添加上下文](https://projectreactor.io/docs/core/release/reference/#context)。

StepVerifier围绕 a 的传播有几个期望Context：

-   expectAccessibleContext: 返回一个ContextExpectations对象，你可以使用它来设置传播的期望Context。一定要调用then()返回到期望的序列集。
-   expectNoAccessibleContext：建立一个期望，即 NOContext可以在被测算子链上传播。这很可能发生Publisher在被测对象不是Reactor或没有任何可以传播的操作符Context (例如，生成器源)时。

此外，你可以使用创建验证器将特定于测试的首字母Context与 a相关联。StepVerifierStepVerifierOptions

这些功能在以下代码段中演示：

```java
StepVerifier.create(Mono.just(1).map(i -> i + 10),
				StepVerifierOptions.create().withInitialContext(Context.of("thing1", "thing2"))) 
		            .expectAccessibleContext() 
		            .contains("thing1", "thing2") 
		            .then() 
		            .expectNext(11)
		            .verifyComplete(); 
```

|      | StepVerifier通过使用创建StepVerifierOptions并传入初始值Context |
| ---- | ------------------------------------------------------------ |
|      | 开始设置有关Context传播的期望。仅此一项就可以确保 a Context被传播。 |
|      | Context特定期望的示例。它必须包含键“thing1”的值“thing2”。  |
|      | 我们then()切换回对数据设置正常预期。                       |
|      | 让我们不要忘记verify()整套期望。                           |

### 6.5. 手动发射TestPublisher

对于更高级的测试用例，完全掌握数据源可能会很有用，以触发与你要测试的特定情况密切匹配的精选信号。

另一种情况是当你实现了自己的操作符并且你想要验证它在 Reactive Streams 规范方面的行为方式，特别是在其源代码行为不佳的情况下。

对于这两种情况，reactor-test提供TestPublisher课程。这是一个Publisher<T> 让你以编程方式触发各种信号的方法：

-   next(T)并next(T, T…)触发 1-nonNext信号。
-   emit(T…)触发 1-n 个onNext信号并执行complete().
-   complete()以一个onComplete信号结束。
-   error(Throwable)以一个onError信号结束。

TestPublisher你可以通过create工厂方法获得良好的表现。TestPublisher此外，你可以使用createNonCompliant工厂方法创建行为不端。TestPublisher.Violation 后者从枚举中获取一个或多个值。这些值定义了发布者可以忽略规范的哪些部分。这些枚举值包括：

-   REQUEST_OVERFLOW: 允许next在请求不足的情况下进行调用，而不触发IllegalStateException.
-   ALLOW_NULL: 允许next使用值进行调用null而不触发 NullPointerException.
-   CLEANUP_ON_TERMINATE：允许连续多次发送终止信号。这包括complete()、error()和emit()。
-   DEFER_CANCELLATION：允许TestPublisher忽略取消信号并继续发出信号，就像取消与所述信号的竞争一样。

最后，TestPublisher订阅后跟踪内部状态，可以通过其各种assert方法进行断言。

你可以将其用作 aFlux或Mono通过使用转换方法flux()和 mono().

### 6.6. 检查执行路径PublisherProbe

在构建复杂的运算符链时，你可能会遇到存在多个可能执行路径的情况，这些路径由不同的子序列具体化。

大多数时候，这些子序列会产生一个足够具体的onNext信号，你可以通过查看最终结果来断言它已被执行。

例如，考虑以下方法，该方法从源构建运算符链，并switchIfEmpty在源为空时使用 a 回退到特定的替代方案：

```java
public Flux<String> processOrFallback(Mono<String> source, Publisher<String> fallback) {
    return source
            .flatMapMany(phrase -> Flux.fromArray(phrase.split("s+")))
            .switchIfEmpty(fallback);
}
```

可以测试使用了switchIfEmpty的哪个逻辑分支，如下：

```java
@Test
public void testSplitPathIsUsed() {
    StepVerifier.create(processOrFallback(Mono.just("just a  phrase with    tabs!"),
            Mono.just("EMPTY_PHRASE")))
                .expectNext("just", "a", "phrase", "with", "tabs!")
                .verifyComplete();
}

@Test
public void testEmptyPathIsUsed() {
    StepVerifier.create(processOrFallback(Mono.empty(), Mono.just("EMPTY_PHRASE")))
                .expectNext("EMPTY_PHRASE")
                .verifyComplete();
}
```

但是，请考虑一个该方法生成 a 的示例Mono<Void>。它等待源完成，执行附加任务，然后完成。如果源为空，则Runnable必须改为执行类似回退的任务。以下示例显示了这种情况：

```java
private Mono<String> executeCommand(String command) {
    return Mono.just(command + " DONE");
}

public Mono<Void> processOrFallback(Mono<String> commandSource, Mono<Void> doWhenEmpty) {
    return commandSource
            .flatMap(command -> executeCommand(command).then()) 
            .switchIfEmpty(doWhenEmpty); 
}
```

|      | then()忘记命令结果。它只关心它是否已完成。 |
| ---- | -------------------------------------------- |
|      | 如何区分都是空序列的两种情况？               |

要验证你的processOrFallback方法确实通过了该doWhenEmpty路径，你需要编写一些样板文件。即你需要一个Mono<Void>：

-   捕获它已被订阅的事实。
-   让你在整个过程终止后断言该事实。

在 3.1 版之前，你需要手动维护AtomicBoolean每个你想要断言的状态，并将相应的doOn回调附加到你想要评估的发布者。当必须定期应用此模式时，这可能是很多样板。幸运的是，3.1.0 引入了PublisherProbe. 以下示例显示了如何使用它：

```java
@Test
public void testCommandEmptyPathIsUsed() {
    PublisherProbe<Void> probe = PublisherProbe.empty(); 

    StepVerifier.create(processOrFallback(Mono.empty(), probe.mono())) 
                .verifyComplete();

    probe.assertWasSubscribed(); 
    probe.assertWasRequested(); 
    probe.assertWasNotCancelled(); 
}
```

|      | 创建一个转换为空序列的探针。                                |
| ---- | ----------------------------------------------------------- |
|      | Mono<Void>通过调用来使用探针代替probe.mono()。          |
|      | 序列完成后，探针让你断言它已被使用。你可以检查是否已订阅... |
|      | …以及实际请求的数据…                                        |
|      | …以及它是否被取消。                                         |

你还可以Flux<T>通过调用.flux()而不是 使用探测器来代替 a .mono()。对于需要探测执行路径但又需要探测器发出数据的情况，可以Publisher<T>使用PublisherProbe.of(Publisher).

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/testing.adoc) 到“[测试](https://projectreactor.io/docs/core/release/reference/#testing)”

## 7. 调试响应堆

从命令式和同步编程范式切换到响应式和异步编程范式有时会令人生畏。学习曲线中最陡峭的步骤之一是如何在出现问题时进行分析和调试。

在命令式世界中，调试通常非常简单。你可以阅读堆栈跟踪并查看问题的根源。这完全是你的代码失败吗？失败是否发生在某些库代码中？如果是这样，代码的哪一部分调用了库，可能传入了最终导致失败的不正确参数？

### 7.1. 典型的响应堆堆栈跟踪

当你转向异步代码时，事情会变得更加复杂。

考虑以下堆栈跟踪：

示例 20. 一个典型的Reactor堆栈跟踪

```none
java.lang.IndexOutOfBoundsException: Source emitted more than one item
    at reactor.core.publisher.MonoSingle$SingleSubscriber.onNext(MonoSingle.java:129)
    at reactor.core.publisher.FluxFlatMap$FlatMapMain.tryEmitScalar(FluxFlatMap.java:445)
    at reactor.core.publisher.FluxFlatMap$FlatMapMain.onNext(FluxFlatMap.java:379)
    at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onNext(FluxMapFuseable.java:121)
    at reactor.core.publisher.FluxRange$RangeSubscription.slowPath(FluxRange.java:154)
    at reactor.core.publisher.FluxRange$RangeSubscription.request(FluxRange.java:109)
    at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.request(FluxMapFuseable.java:162)
    at reactor.core.publisher.FluxFlatMap$FlatMapMain.onSubscribe(FluxFlatMap.java:332)
    at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onSubscribe(FluxMapFuseable.java:90)
    at reactor.core.publisher.FluxRange.subscribe(FluxRange.java:68)
    at reactor.core.publisher.FluxMapFuseable.subscribe(FluxMapFuseable.java:63)
    at reactor.core.publisher.FluxFlatMap.subscribe(FluxFlatMap.java:97)
    at reactor.core.publisher.MonoSingle.subscribe(MonoSingle.java:58)
    at reactor.core.publisher.Mono.subscribe(Mono.java:3096)
    at reactor.core.publisher.Mono.subscribeWith(Mono.java:3204)
    at reactor.core.publisher.Mono.subscribe(Mono.java:3090)
    at reactor.core.publisher.Mono.subscribe(Mono.java:3057)
    at reactor.core.publisher.Mono.subscribe(Mono.java:3029)
    at reactor.guide.GuideTests.debuggingCommonStacktrace(GuideTests.java:995)
```

那里发生了很多事情。我们得到一个IndexOutOfBoundsException，它告诉我们一个source emitted more than one item.

我们可能很快就可以假设这个源是 Flux 或 Mono，正如下一行所证实的那样，其中提到MonoSingle. single所以这似乎是来自运营商的某种抱怨。

参考Mono#single运算符的 Javadoc，我们看到它single有一个约定：源必须恰好发出一个元素。看来我们有一个发射多个源的源，因此违反了该合同。

我们可以更深入地挖掘并确定那个来源吗？以下行不是很有帮助。subscribe它们通过多次调用和带我们了解似乎是响应链的内部结构request。

通过浏览这些行，我们至少可以开始形成一种出错链的图片：它似乎涉及 a MonoSingle、 aFluxFlatMap和 a FluxRange (每个在跟踪中都有几行，但总的来说这三个类都涉及)。所以 range().flatMap().single()可能是链条？

但是如果我们在应用程序中大量使用这种模式呢？这仍然不能告诉我们太多，简单地搜索single是不会找到问题的。然后最后一行引用了我们的一些代码。终于，我们越来越近了。

不过，等一下。当我们转到源文件时，我们看到的只是一个预先存在的Flux被订阅，如下所示：

```java
toDebug
    .subscribeOn(Schedulers.immediate())
    .subscribe(System.out::println, Throwable::printStackTrace);
```

所有这一切都发生在订阅时，但它Flux本身并没有在那里声明。更糟糕的是，当我们转到声明变量的位置时，我们会看到以下内容：

```java
public Mono<String> toDebug; //please overlook the public class attribute
```

变量未在声明的地方实例化。我们必须假设一个最坏的情况，我们发现可能有几个不同的代码路径在应用程序中设置它。我们仍然不确定是哪一个导致了这个问题。

|      | 与编译错误相反，这是一种相当于Reactor的运行时错误。 |
| ---- | ----------------------------------------------------- |
|      |                                                       |

我们想要更容易找到的是运算符被添加到链中的位置 - 即Flux声明的位置。我们通常将其称为Flux.

### 7.2. 激活调试模式 - 也就是回溯

|      | 本节描述了启用调试功能的最简单但也是最慢的方法，因为它捕获了每个运算符的堆栈跟踪。有关更细粒度的调试方式，请参阅[替代方案，有关更高级和性能更高的全局选项](https://projectreactor.io/docs/core/release/reference/#checkpoint-alternative)[，请参阅checkpoint()](https://projectreactor.io/docs/core/release/reference/#checkpoint-alternative)[生产就绪全局调试](https://projectreactor.io/docs/core/release/reference/#reactor-tools-debug)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

尽管堆栈跟踪仍然能够为有一点经验的人传达一些信息，但我们可以看到，在更高级的情况下，它本身并不理想。

幸运的是，Reactor 附带了为调试而设计的汇编时工具。

Hooks.onOperatorDebug()这是通过在应用程序启动时(或至少在被指控Flux或可以实例化之前)通过方法激活全局调试模式来完成的Mono，如下所示：

```java
Hooks.onOperatorDebug();
```

这开始通过包装操作符的构造并在那里捕获堆栈跟踪来检测对Reactor操作符方法的调用(它们被组装到链中)。由于这是在声明运算符链时完成的，因此应该在此之前激活钩子，因此最安全的方法是在应用程序开始时立即激活它。

稍后，如果发生异常，失败的操作员能够引用该捕获并重新处理堆栈跟踪，附加附加信息。

|      | 我们将此捕获的程序集信息(以及通常由Reactor添加到异常中的其他信息)称为traceback。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

在下一节中，我们将看到堆栈跟踪有何不同以及如何解释新信息。

### 7.3. 在调试模式下读取堆栈跟踪

当我们重用初始示例但激活operatorStacktrace调试功能时，会发生几件事：

1.  指向订阅站点并因此不太有趣的堆栈跟踪在第一帧之后被剪切并放在一边。
2.  一个特殊的抑制异常被添加到原始异常中(或者如果已经存在则修改)。
3.  为具有几个部分的特殊异常构造一条消息。
4.  第一部分将追溯到失败的操作员的组装地点。
5.  第二部分将尝试显示从该运算符构建并看到错误传播的链
6.  最后一部分是原始堆栈跟踪

打印后的完整堆栈跟踪如下：

```none
java.lang.IndexOutOfBoundsException: Source emitted more than one item
    at reactor.core.publisher.MonoSingle$SingleSubscriber.onNext(MonoSingle.java:127) 
    Suppressed: The stacktrace has been enhanced by Reactor, refer to additional information below: 
Assembly trace from producer [reactor.core.publisher.MonoSingle] : 
    reactor.core.publisher.Flux.single(Flux.java:7915)
    reactor.guide.GuideTests.scatterAndGather(GuideTests.java:1017)
Error has been observed at the following site(s): 
    _______Flux.single ⇢ at reactor.guide.GuideTests.scatterAndGather(GuideTests.java:1017) 
    |_ Mono.subscribeOn ⇢ at reactor.guide.GuideTests.debuggingActivated(GuideTests.java:1071) 
Original Stack Trace: 
        at reactor.core.publisher.MonoSingle$SingleSubscriber.onNext(MonoSingle.java:127)
...

...
        at reactor.core.publisher.Mono.subscribeWith(Mono.java:4363)
        at reactor.core.publisher.Mono.subscribe(Mono.java:4223)
        at reactor.core.publisher.Mono.subscribe(Mono.java:4159)
        at reactor.core.publisher.Mono.subscribe(Mono.java:4131)
        at reactor.guide.GuideTests.debuggingActivated(GuideTests.java:1067)
```

|      | 原始堆栈跟踪被截断为单个帧。                                 |
| ---- | ------------------------------------------------------------ |
|      | 这是新的：我们看到了捕获堆栈的包装运算符。这是追溯开始出现的地方。 |
|      | 首先，我们得到一些关于操作员组装地点的详细信息。             |
|      | 其次，我们得到了错误传播的运营商链的概念，从第一个到最后一个(错误站点到订阅站点)。 |
|      | 每个看到错误的操作员都与用户类和使用它的行一起被提及。这里我们有一个“根”。 |
|      | 在这里，我们有一个简单的链条部分。                           |
|      | 堆栈跟踪的其余部分在最后移动......                           |
|      | …展示了一些操作员的内部结构(所以我们在这里删除了一些片段)。 |

捕获的堆栈跟踪作为抑制附加到原始错误OnAssemblyException。它分为三个部分，但第一部分是最有趣的。它显示了触发异常的运算符的构造路径。在这里，它表明single导致我们的问题实际上是在 scatterAndGather方法中创建的。

现在我们已经掌握了足够的信息来找到罪魁祸首，我们可以对该scatterAndGather方法进行有意义的研究：

```java
private Mono<String> scatterAndGather(Flux<String> urls) {
    return urls.flatMap(url -> doRequest(url))
           .single(); 
}
```

|      | 果然，这是我们的single. |
| ---- | ------------------------- |
|      |                           |

现在我们可以看到错误的根本原因是，它对flatMap几个 URL 执行了几次 HTTP 调用，但它与 链接在一起single，这太严格了。在与该行的作者进行了简短git blame而快速的讨论后，我们发现他打算使用限制较少的内容take(1)。

我们已经解决了我们的问题。

现在考虑堆栈跟踪中的以下部分：

```none
Error has been observed at the following site(s):
```

在这个特定示例中，回溯的第二部分不一定有趣，因为错误实际上发生在链中的最后一个运算符(最接近 的那个subscribe)中。考虑另一个例子可能会更清楚：

```java
FakeRepository.findAllUserByName(Flux.just("pedro", "simon", "stephane"))
              .transform(FakeUtils1.applyFilters)
              .transform(FakeUtils2.enrichUser)
              .blockLast();
```

现在想象一下，在里面findAllUserByName，有一个map失败的。在这里，我们将在回溯的第二部分中看到以下内容：

```none
Error has been observed at the following site(s):
    ________Flux.map ⇢ at reactor.guide.FakeRepository.findAllUserByName(FakeRepository.java:27)
    |_       Flux.map ⇢ at reactor.guide.FakeRepository.findAllUserByName(FakeRepository.java:28)
    |_    Flux.filter ⇢ at reactor.guide.FakeUtils1.lambda$static$1(FakeUtils1.java:29)
    |_ Flux.transform ⇢ at reactor.guide.GuideDebuggingExtraTests.debuggingActivatedWithDeepTraceback(GuideDebuggingExtraTests.java:39)
    |_   Flux.elapsed ⇢ at reactor.guide.FakeUtils2.lambda$static$0(FakeUtils2.java:30)
    |_ Flux.transform ⇢ at reactor.guide.GuideDebuggingExtraTests.debuggingActivatedWithDeepTraceback(GuideDebuggingExtraTests.java:40)
```

这对应于收到错误通知的运算符链部分：

1.  异常起源于第一个map. 这个被连接器识别为根，事实_ 用于缩进。
2.  该异常被第二个看到map(实际上都对应于findAllUserByName 方法)。
3.  然后由 afilter和 a看到transform，这表明链的一部分是由可重用的转换函数(这里是applyFilters实用程序方法)构造的。
4.  最后，它被 anelapsed和 a看到transform。再次elapsed由第二个变换的变换函数应用。

在某些情况下，相同的异常通过多个链传播，“根”标记_ 允许我们更好地分离这些链。(observed x times)如果一个站点被多次看到，在调用站点信息之后会有一个。

例如，让我们考虑以下代码段：

```java
public class MyClass {
    public void myMethod() {
        Flux<String> source = Flux.error(sharedError);
        Flux<String> chain1 = source.map(String::toLowerCase).filter(s -> s.length() < 4);
        Flux<String> chain2 = source.filter(s -> s.length() > 5).distinct();

        Mono<Void> when = Mono.when(chain1, chain2);
    }
}
```

when在上面的代码中，错误通过两个独立的链传播chain1到chain2. 这将导致包含以下内容的回溯：

```none
Error has been observed at the following site(s):
    _____Flux.error ⇢ at myClass.myMethod(MyClass.java:3) (observed 2 times)
    |_      Flux.map ⇢ at myClass.myMethod(MyClass.java:4)
    |_   Flux.filter ⇢ at myClass.myMethod(MyClass.java:4)
    _____Flux.error ⇢ at myClass.myMethod(MyClass.java:3) (observed 2 times)
    |_   Flux.filter ⇢ at myClass.myMethod(MyClass.java:5)
    |_ Flux.distinct ⇢ at myClass.myMethod(MyClass.java:5)
    ______Mono.when ⇢ at myClass.myMethod(MyClass.java:7)
```

我们看到：

1.  有 3 个“根”元素(这when是真正的根)。
2.  从 开始的两条链Flux.error是可见的。
3.  两条链似乎都基于相同的Flux.error来源(observed 2 times)。
4.  第一条链是Flux.error().map().filter
5.  第二个链是Flux.error().filter().distinct()

|      | 关于回溯和抑制异常的说明：由于回溯作为抑制异常附加到原始错误，这可能会在一定程度上干扰使用此机制的另一种类型的异常：复合异常。此类异常可以通过 直接创建Exceptions.multiple(Throwable…)，也可以由可能加入多个错误源(如Flux#flatMapDelayError)的某些运算符创建。它们可以展开到ListviaExceptions.unwrapMultiple(Throwable)中，在这种情况下，traceback 将被视为组合的一个组件，并且是返回的一部分List。如果这是不可取的，则可以通过Exceptions.isTraceback(Throwable) 检查来识别回溯，并通过使用而不是从这种展开中排除Exceptions.unwrapMultipleExcludingTracebacks(Throwable) 。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

我们在这里处理一种检测形式，创建堆栈跟踪的成本很高。这就是为什么这个调试功能只能以受控方式激活，作为最后的手段。

#### 7.3.1. 替代checkpoint()方案

Flux调试模式是全局的，会影响应用程序内部组装到 a或 a 中的每个运算符Mono。这具有允许事后调试的好处：无论错误是什么，我们都可以获得额外的信息来调试它。

正如我们之前看到的，这种全局知识是以影响性能为代价的(由于填充的堆栈跟踪的数量)。如果我们对可能有问题的运营商有所了解，就可以降低成本。但是，我们通常不知道哪些操作符可能有问题，除非我们在野外观察到错误，看到我们缺少组装信息，然后修改代码以激活组装跟踪，希望再次观察到相同的错误。

在这种情况下，我们必须切换到调试模式并做好准备，以便更好地观察错误的第二次发生，这次捕获所有附加信息。

如果你可以识别你在应用程序中组装的响应链，这些链的可维护性至关重要，你可以使用 checkpoint()操作员实现这两种技术的混合。

你可以将此运算符链接到方法链中。运算符的checkpoint工作方式与钩子版本类似，但仅适用于该特定链的链接。

还有一个checkpoint(String)变体可让你String向程序集回溯添加唯一标识符。这样，堆栈跟踪就被省略了，你可以依靠描述来识别组装站点。checkpoint(String)比普通的checkpoint.

最后但并非最不重要的一点是，如果你想向检查点添加更通用的描述，但仍依赖堆栈跟踪机制来识别程序集站点，你可以通过使用checkpoint("description", true)版本来强制执行该行为。我们现在回到回溯的初始消息，增加了 a description，如以下示例所示：

```none
Assembly trace from producer [reactor.core.publisher.ParallelSource], described as [descriptionCorrelation1234] : 
	reactor.core.publisher.ParallelFlux.checkpoint(ParallelFlux.java:215)
	reactor.core.publisher.FluxOnAssemblyTest.parallelFluxCheckpointDescriptionAndForceStack(FluxOnAssemblyTest.java:225)
Error has been observed at the following site(s):
	|_	ParallelFlux.checkpoint ⇢ reactor.core.publisher.FluxOnAssemblyTest.parallelFluxCheckpointDescriptionAndForceStack(FluxOnAssemblyTest.java:225)
```

|      | descriptionCorrelation1234是中提供的描述checkpoint。 |
| ---- | -------------------------------------------------------- |
|      |                                                          |

描述可以是静态标识符或用户可读描述或更广泛的相关 ID(例如，在 HTTP 请求的情况下来自标头)。

|      | 当全局调试与检查点一起启用时，将应用全局调试回溯样式，检查点仅反映在“已观察到错误...”部分。因此，在这种情况下，重检查点的名称是不可见的。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 7.4. 生产就绪的全局调试

ProjectReactor带有一个单独的Java代理，它可以检测你的代码并添加调试信息，而无需支付在每次操作员调用时捕获堆栈跟踪的成本。[该行为与Activating Debug Mode - aka tracebacks](https://projectreactor.io/docs/core/release/reference/#debug-activate)非常相似，但没有运行时性能开销。

要在你的应用程序中使用它，你必须将其添加为依赖项。

以下示例显示了如何reactor-tools在 Maven 中添加为依赖项：

示例 21. Maven 中的 reactor-tools，在<dependencies>

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-tools</artifactId>
    
</dependency>
```

|      | 如果使用[BOM](https://projectreactor.io/docs/core/release/reference/#getting)，则无需指定<version>. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

以下示例显示了如何reactor-tools在 Gradle 中添加为依赖项：

示例 22. Gradle 中的 reactor-tools，修改dependencies块

```groovy
dependencies {
   compile 'io.projectreactor:reactor-tools'
}
```

它还需要显式初始化：

```java
ReactorDebugAgent.init();
```

|      | 由于实现将在加载类时对它们进行检测，因此最好将其放置在 main(String[]) 方法中的所有其他内容之前： |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

```java
public static void main(String[] args) {
    ReactorDebugAgent.init();
    SpringApplication.run(Application.class, args);
}
processExistingClasses()如果你不能急切地运行 init ，你也可以重新处理现有的类。例如，在来自一个或什至在类初始化块中的[JUnit5 测试中：TestExecutionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom)static
ReactorDebugAgent.init();
ReactorDebugAgent.processExistingClasses();
```

|      | 请注意，由于需要遍历所有加载的类并应用转换，因此重新处理需要几秒钟。仅当你看到某些呼叫站点未检测时才使用它。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 7.4.1. 限制

ReactorDebugAgent实现为Java代理并使用[ByteBuddy](https://bytebuddy.net/#/) 执行自连接。自附加可能无法在某些 JVM 上运行，请参阅 ByteBuddy 的文档了解更多详细信息。

#### 7.4.2. 将ReactorDebugAgent作为Java代理运行

如果你的环境不支持 ByteBuddy 的自连接，你可以reactor-tools作为Java代理运行：

```shell
java -javaagent reactor-tools.jar -jar app.jar
```

#### 7.4.3. 在构建时运行ReactorDebugAgent

也可以reactor-tools在构建时运行。为此，你需要将其作为 ByteBuddy 构建工具的插件应用。

|      | 转换只会应用于你项目的类。不会检测类路径库。 |
| ---- | -------------------------------------------- |
|      |                                              |

示例 23. 带有[ByteBuddy 的 Maven 插件的reactor-tools](https://github.com/raphw/byte-buddy/tree/byte-buddy-1.10.9/byte-buddy-maven-plugin)

```xml
<dependencies>
	<dependency>
		<groupId>io.projectreactor</groupId>
		<artifactId>reactor-tools</artifactId>
		
		<classifier>original</classifier> 
		<scope>runtime</scope>
	</dependency>
</dependencies>

<build>
	<plugins>
		<plugin>
			<groupId>net.bytebuddy</groupId>
			<artifactId>byte-buddy-maven-plugin</artifactId>
			<configuration>
				<transformations>
					<transformation>
						<plugin>reactor.tools.agent.ReactorDebugByteBuddyPlugin</plugin>
					</transformation>
				</transformations>
			</configuration>
		</plugin>
	</plugins>
</build>
```

|      | 如果使用[BOM](https://projectreactor.io/docs/core/release/reference/#getting)，则无需指定<version>. |
| ---- | ------------------------------------------------------------ |
|      | classifier这里很重要。                                     |

示例 24. 带有[ByteBuddy 的 Gradle 插件的响应器工具](https://github.com/raphw/byte-buddy/tree/byte-buddy-1.10.9/byte-buddy-gradle-plugin)

```groovy
plugins {
	id 'net.bytebuddy.byte-buddy-gradle-plugin' version '1.10.9'
}

configurations {
	byteBuddyPlugin
}

dependencies {
	byteBuddyPlugin(
			group: 'io.projectreactor',
			name: 'reactor-tools',
			
			classifier: 'original', 
	)
}

byteBuddy {
	transformation {
		plugin = "reactor.tools.agent.ReactorDebugByteBuddyPlugin"
		classPath = configurations.byteBuddyPlugin
	}
}
```

|      | 如果使用[BOM](https://projectreactor.io/docs/core/release/reference/#getting)，则无需指定version. |
| ---- | ------------------------------------------------------------ |
|      | classifier这里很重要。                                     |

### 7.5. 记录序列

除了堆栈跟踪调试和分析之外，工具包中的另一个强大工具是以异步序列跟踪和记录事件的能力。

log()操作员可以做到这一点。链接在一个序列中，它会查看它的Flux或Mono上游的每个事件(包括onNext、onError和 onComplete以及订阅、取消和请求)。

关于日志记录实现的说明

log操作员使用实用程序类，该类通过使用Loggers常用的日志记录框架(例如 Log4J 和 Logback)SLF4J，如果 SLF4J 不可用，则默认记录到控制台。

控制台回退System.err用于WARN和ERROR日志级别以及 System.out其他所有内容。

如果你更喜欢 JDKjava.util.logging后备，如 3.0.x，你可以通过将reactor.logging.fallback系统属性设置为JDK.

在所有情况下，在生产环境中登录时，你应该注意配置底层日志框架以使用其最异步和非阻塞的方法 ——例如，AsyncAppender在 Logback 或AsyncLoggerLog4j 2 中。

例如，假设我们已经激活并配置了 Logback 和一个类似 range(1,10).take(3). log()通过在之前放置 a take，我们可以深入了解它的工作原理以及它向上游传播的事件类型，如下例所示：

```java
Flux<Integer> flux = Flux.range(1, 10)
                         .log()
                         .take(3);
flux.subscribe();
```

这将打印出以下内容(通过记录器的控制台附加程序)：

```
10:45:20.200 [主] INFO reactor.Flux.Range.1 - | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
10:45:20.205 [主] INFO reactor.Flux.Range.1 - | 请求(无界)
10:45:20.205 [主] INFO reactor.Flux.Range.1 - | onNext(1)
10:45:20.205 [主] INFO reactor.Flux.Range.1 - | onNext(2) 
10:45:20.205 [main] INFO reactor.Flux.Range.1 - | onNext(3) 
10:45:20.205 [main] INFO reactor.Flux.Range.1 - | 取消()
```

这里，除了记录器自己的格式化程序(时间、线程、级别、消息)之外， log()操作员还以自己的格式输出了一些东西：

|      | reactor.Flux.Range.1是日志的自动类别，以防你在链中多次使用运算符。它使你可以区分记录了哪些操作员的事件(在本例中为range)。log(String)你可以使用方法签名用你自己的自定义类别覆盖标识符。在几个分隔字符之后，实际事件被打印出来。在这里，我们接到一个onSubscribe电话，一个 request电话，三个onNext电话，一个cancel电话。对于第一行， onSubscribe我们得到 的实现Subscriber，它通常对应于特定于操作符的实现。在方括号之间，我们得到了额外的信息，包括是否可以通过同步或异步融合来自动优化算子。 |
| ---- | ------------------------------------------------------------ |
|      | 在第二行，我们可以看到从下游向上传播了一个无限制的请求。     |
|      | 然后范围连续发送三个值。                                     |
|      | 在最后一行，我们看到cancel().                              |

最后一行 (4) 是最有趣的。我们可以看到take那里的行动。它通过在看到足够多的元素发射后缩短序列来操作。简而言之，take()使源cancel()一旦发出用户请求的数量。

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/debugging.adoc) 到“[调试响应堆](https://projectreactor.io/docs/core/release/reference/#debugging)”

## 8. 公开Reactor指标

ProjectReactor是一个为提高性能和更好地利用资源而设计的库。但是要真正了解一个系统的性能，最好能够监控它的各个组件。

[这就是Reactor提供与Micrometer](https://micrometer.io/)的内置集成的原因。

|      | 如果 Micrometer 不在类路径上，则度量标准将是无操作的。 |
| ---- | ------------------------------------------------------ |
|      |                                                        |

### 8.1. 调度程序指标

Reactor 中的每个异步操作都是通过[Threading 和 Schedulers](https://projectreactor.io/docs/core/release/reference/#schedulers)中描述的 Scheduler 抽象来完成的。这就是为什么监控你的调度程序很重要，注意那些开始看起来可疑的关键指标并做出相应的响应。

要启用调度程序指标，你需要使用以下方法：

```java
Schedulers.enableMetrics();
```

|      | 在创建调度程序时执行检测。建议尽早调用此方法。 |
| ---- | ---------------------------------------------- |
|      |                                                |

|      | 如果你使用的是 Spring Boot，最好在调用之前放置调用SpringApplication.run(Application.class, args)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

一旦启用了调度器指标并提供它在类路径上，Reactor 将使用 Micrometer 的支持来检测支持大多数调度器的执行器。

有关公开的指标，请参阅[Micrometer 的文档](https://micrometer.io/docs/ref/jvm)，例如：

-   executor_active_threads
-   executor_completed_tasks_total
-   executor_pool_size_threads
-   executor_queued_tasks
-   executor_secounds_{计数，最大值，总和}

由于一个调度程序可能有多个执行程序，因此每个执行程序指标都有一个reactor_scheduler_id标签。

|      | Grafana + Prometheus 用户可以使用[预先构建的仪表板](https://raw.githubusercontent.com/reactor/reactor-monitoring-demo/master/dashboards/schedulers.json)，其中包括线程面板、已完成任务、任务队列和其他方便的指标。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 8.2. 发布商指标

有时，能够在响应式管道的某个阶段记录指标很有用。

一种方法是将值手动推送到你选择的指标后端。另一种选择是使用Reactor的内置指标集成来Flux/Mono并解释它们。

考虑以下管道：

```java
listenToEvents()
    .doOnNext(event -> log.info("Received {}", event))
    .delayUntil(this::processEvent)
    .retry()
    .subscribe();
```

要启用此源的指标Flux(从 返回listenToEvents())，我们需要打开指标收集：

```java
listenToEvents()
    .name("events") 
    .metrics() 
    .doOnNext(event -> log.info("Received {}", event))
    .delayUntil(this::processEvent)
    .retry()
    .subscribe();
```

|      | 此阶段的每个指标都将被标识为“事件”(可选)。                 |
| ---- | ------------------------------------------------------------ |
|      | Flux#metrics操作员使用调用Flux#name操作员时提供的名称启用指标报告。如果Flux#name未使用运算符，则默认名称为reactor. |

只需添加这两个运算符就会暴露一大堆有用的指标！

| 指标名称                | 类型     | 描述                                                         |
| :---------------------- | :------- | :----------------------------------------------------------- |
| [名称].subscribed       | 柜台     | 计算订阅了多少Reactor序列                                  |
| [名称].malformed.source | 柜台     | 计算从格式错误的源接收到的事件数(即 onComplete 之后的 onNext) |
| [名称].请求             | 分布总结 | 计算所有订阅者向命名 Flux 请求的数量，直到至少有一个请求无限数量 |
| [名称].onNext.delay     | 定时器   | 测量 onNext 信号之间(或 onSubscribe 和第一个 onNext 之间)的延迟 |
| [名称].flow.duration    | 定时器   | 乘以订阅与序列终止或取消之间经过的持续时间。添加状态标签以指定导致计时器结束的事件(completed, completedEmpty, error, cancelled)。 |

想知道你的事件处理由于某些错误而重新启动了多少次？Read [name].subscribed，因为retry()操作员会在出错时重新订阅源发布者。

对“每秒事件数”指标感兴趣？测量 的[name].onNext.delay计数率。

想要在侦听器抛出错误时收到警报？[name].flow.duration带status=error标签是你的朋友。同样，status=completedandstatus=completedEmpty将允许你区分使用元素完成的序列和完成空的序列。

请注意，当给序列命名时，该序列不能再与其他序列聚合。如果你想识别你的序列但仍然可以与其他视图聚合，作为一种折衷方案，你可以通过调用来使用[标签](https://projectreactor.io/docs/core/release/reference/#_tags)(tag("flow", "events"))作为名称。

#### 8.2.1. 标签

每个指标都有一个type共同的标签，其值将取决于Flux或Mono取决于发布商的性质。

允许用户向他们的响应链添加自定义标签：

```java
listenToEvents()
    .name("events") 
    .tag("source", "kafka") 
    .metrics() 
    .doOnNext(event -> log.info("Received {}", event))
    .delayUntil(this::processEvent)
    .retry()
    .subscribe();
```

|      | 此阶段的每个指标都将被标识为“事件”。                         |
| ---- | ------------------------------------------------------------ |
|      | 将自定义标签“source”设置为值“kafka”。                        |
|      | source=kafka除了上面描述的通用标签之外，所有报告的指标都将分配标签。 |

请注意，根据你使用的监控系统，在使用标签时使用名称可能被认为是强制性的，否则会导致两个默认命名序列之间的标签集不同。像 Prometheus 这样的一些系统可能还需要为每个具有相同名称的指标设置完全相同的标签集。

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/metrics.adoc) 到“[暴露响应堆指标](https://projectreactor.io/docs/core/release/reference/#metrics)”

## 9. 高级功能和概念

本章介绍了Reactor的高级特性和概念，包括以下内容：

-   [相互使用运算符](https://projectreactor.io/docs/core/release/reference/#advanced-mutualizing-operator-usage)
-   [热与冷](https://projectreactor.io/docs/core/release/reference/#reactor.hotCold)
-   [向多个订阅者广播ConnectableFlux](https://projectreactor.io/docs/core/release/reference/#advanced-broadcast-multiple-subscribers-connectableflux)
-   [三类配料](https://projectreactor.io/docs/core/release/reference/#advanced-three-sorts-batching)
-   [并行工作ParallelFlux](https://projectreactor.io/docs/core/release/reference/#advanced-parallelizing-parralelflux)
-   [替换默认值Schedulers](https://projectreactor.io/docs/core/release/reference/#scheduler-factory)
-   [使用全局挂钩](https://projectreactor.io/docs/core/release/reference/#hooks)
-   [将上下文添加到响应序列](https://projectreactor.io/docs/core/release/reference/#context)
-   [零安全](https://projectreactor.io/docs/core/release/reference/#null-safety)
-   [处理需要清理的对象](https://projectreactor.io/docs/core/release/reference/#cleanup)

### 9.1. 相互使用运算符

从干净代码的角度来看，代码重用通常是一件好事。Reactor 提供了一些模式可以帮助你重用和共享代码，特别是对于你可能希望在代码库中定期应用的运算符或运算符组合。如果你将操作员链视为配方，则可以创建操作员配方的“食谱”。

#### 9.1.1. 使用transform运算符

运算符允许你将transform运算符链的一部分封装到函数中。该函数在组装时应用于原始运算符链，以使用封装的运算符对其进行扩充。这样做将相同的操作应用于序列的所有订阅者，基本上相当于直接链接操作符。以下代码显示了一个示例：

```java
Function<Flux<String>, Flux<String>> filterAndMap =
f -> f.filter(color -> !color.equals("orange"))
      .map(String::toUpperCase);

Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
	.doOnNext(System.out::println)
	.transform(filterAndMap)
	.subscribe(d -> System.out.println("Subscriber to Transformed MapAndFilter: "+d));
```

下图显示了transform操作符如何封装流：

![转换运算符：封装流](https://projectreactor.io/docs/core/release/reference/images/gs-transform.png)

前面的示例产生以下输出：

```
蓝色
已转换 MapAndFilter 的订阅者：蓝色
绿色
已转换 MapAndFilter 的订阅者：绿色
橙色
紫色
已转换 MapAndFilter 的订阅者：紫色
```

#### 9.1.2. 使用transformDeferred运算符

transformDeferred运算符类似于并且还允许你将transform运算符封装在函数中。主要区别在于，此函数基于每个订阅者应用于原始序列。这意味着该函数实际上可以为每个订阅生成不同的运算符链(通过维护某些状态)。以下代码显示了一个示例：

```java
AtomicInteger ai = new AtomicInteger();
Function<Flux<String>, Flux<String>> filterAndMap = f -> {
	if (ai.incrementAndGet() == 1) {
return f.filter(color -> !color.equals("orange"))
        .map(String::toUpperCase);
	}
	return f.filter(color -> !color.equals("purple"))
	        .map(String::toUpperCase);
};

Flux<String> composedFlux =
Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
    .doOnNext(System.out::println)
    .transformDeferred(filterAndMap);

composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :"+d));
composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: "+d));
```

下图显示了transformDeferred运算符如何处理每个订阅者的转换：

![撰写运算符：每个订阅者转换](https://projectreactor.io/docs/core/release/reference/images/gs-compose.png)

前面的示例产生以下输出：

```
蓝色
用户 1 到 Composed MapAndFilter ：蓝色
绿色
用户 1 到 Composed MapAndFilter ：绿色
橙色
紫色
用户 1 到 Composed MapAndFilter ：紫色
蓝色
用户 2 到 Composed MapAndFilter：蓝色
绿色
用户 2 到 Composed MapAndFilter：绿色
橙色
用户 2 到 Composed MapAndFilter：橙色
紫色
```

### 9.2. 热与冷

到目前为止，我们认为所有Flux(and Mono) 都是相同的：它们都表示异步数据序列，在你订阅之前什么都不会发生。

但实际上，有两大类出版商：热的和冷的。

前面的描述适用于冷酷的出版商家族。他们为每个订阅重新生成数据。如果未创建订阅，则永远不会生成数据。

考虑一个 HTTP 请求：每个新订阅者都会触发一个 HTTP 调用，但如果没有人对结果感兴趣，则不会进行调用。

另一方面，热门发布者不依赖于任何数量的订阅者。他们可能会立即开始发布数据，并在有新数据进入时继续这样做 Subscriber(在这种情况下，订阅者只会看到 订阅后发出的新元素)。对于热门发布者，在你订阅之前确实会发生一些事情。

Reactor 中少数热门运算符的一个示例是just：它直接在组装时捕获值，并将其重放给以后订阅它的任何人。再用 HTTP 调用类比，如果捕获的数据是 HTTP 调用的结果，那么在实例化just.

要转变just为冷发布者，你可以使用defer. 它将我们示例中的 HTTP 请求推迟到订阅时间(并且会导致每个新订阅的单独网络调用)。

相反，share()可replay(…)用于将冷发布者转变为热发布者(至少在第一次订阅发生后)。这两者 Sinks.Many在类中也有等价物Sinks，允许以编程方式输入序列。

考虑两个示例，一个演示冷通量，另一个使用 Sinks模拟热通量。以下代码显示了第一个示例：

```java
Flux<String> source = Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                          .map(String::toUpperCase);

source.subscribe(d -> System.out.println("Subscriber 1: "+d));
source.subscribe(d -> System.out.println("Subscriber 2: "+d));
```

第一个示例产生以下输出：

```
用户 1：蓝色
用户 1：绿色
用户 1：橙色
用户 1：紫色
用户 2：蓝色
用户 2：绿色
用户 2：橙色
用户 2：紫色
```

下图显示了重放行为：

![重播行为](https://projectreactor.io/docs/core/release/reference/images/gs-cold.png)

两个订阅者都捕捉到所有四种颜色，因为每个订阅者都会导致操作员定义的进程Flux运行。

比较第一个示例和第二个示例，如下代码所示：

```java
Sinks.Many<String> hotSource = Sinks.unsafe().many().multicast().directBestEffort();

Flux<String> hotFlux = hotSource.asFlux().map(String::toUpperCase);

hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: "+d));

hotSource.emitNext("blue", FAIL_FAST); 
hotSource.tryEmitNext("green").orThrow(); 

hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: "+d));

hotSource.emitNext("orange", FAIL_FAST);
hotSource.emitNext("purple", FAIL_FAST);
hotSource.emitComplete(FAIL_FAST);
```

|      | 有关接收器的更多详细信息，请参阅[使用Sinks.One和从多个线程安全生产Sinks.Many](https://projectreactor.io/docs/core/release/reference/#sinks) |
| ---- | ------------------------------------------------------------ |
|      | 旁注：这是适用于测试的+orThrow()的替代方案 ，因为在那里投掷是可以接受的(比在响应式应用程序中更是如此)。emitNextSinks.EmitFailureHandler.FAIL_FAST |

第二个示例产生以下输出：

```
热源的用户 1 ：蓝色 热源
的用户 1：绿色 热源
的用户 1：橙色 热源
的用户 2：橙色 热源
的用户 1：紫色 热源
的用户 2：紫色
```

下图显示了订阅是如何广播的：

![广播订阅](https://projectreactor.io/docs/core/release/reference/images/gs-hot.png)

订阅者 1 捕获所有四种颜色。在产生前两种颜色之后创建的订阅者 2 仅捕获最后两种颜色。ORANGE这种差异导致产量翻倍PURPLE。无论何时附加订阅，操作员在此 Flux 上描述的过程都会运行。

### 9.3. 向多个订阅者广播ConnectableFlux

有时，你可能不希望仅将某些处理推迟到一个订阅者的订阅时间，但你实际上可能希望其中几个会合，然后触发订阅和数据生成。

这就是ConnectableFlux为之而生的。Flux API中涵盖了两种主要模式，它们返回ConnectableFlux:publish和replay.

-   publish通过将这些请求转发给源，动态地尝试在背压方面尊重来自其各个订阅者的需求。最值得注意的是，如果任何订阅者有未决的需求0，则发布会暂停其对源的请求。
-   replay缓冲通过第一次订阅看到的数据，直到可配置的限制(时间和缓冲区大小)。它将数据重播给后续订阅者。

AConnectableFlux提供了额外的方法来管理下游订阅与对原始源的订阅。这些附加方法包括：

-   connect()一旦你达到足够的订阅量，就可以手动调用Flux. 这会触发对上游源的订阅。
-   autoConnect(n)n订阅后可以自动完成相同的工作。
-   refCount(n)不仅会自动跟踪传入的订阅，而且还会检测这些订阅何时被取消。如果没有足够的订阅者被跟踪，源将“断开连接”，如果出现其他订阅者，则会导致对源的新订阅。
-   refCount(int, Duration)添加“宽限期”。一旦跟踪的订阅者数量变得太少，它会Duration在断开源之前等待，可能会允许足够的新订阅者进入并再次超过连接阈值。

考虑以下示例：

```java
Flux<Integer> source = Flux.range(1, 3)
                           .doOnSubscribe(s -> System.out.println("subscribed to source"));

ConnectableFlux<Integer> co = source.publish();

co.subscribe(System.out::println, e -> {}, () -> {});
co.subscribe(System.out::println, e -> {}, () -> {});

System.out.println("done subscribing");
Thread.sleep(500);
System.out.println("will now connect");

co.connect();
```

前面的代码产生以下输出：

```
完成订阅
现在将连接
订阅源
1 
1 
2 
2 
3 
3
```

以下代码使用autoConnect：

```java
Flux<Integer> source = Flux.range(1, 3)
                           .doOnSubscribe(s -> System.out.println("subscribed to source"));

Flux<Integer> autoCo = source.publish().autoConnect(2);

autoCo.subscribe(System.out::println, e -> {}, () -> {});
System.out.println("subscribed first");
Thread.sleep(500);
System.out.println("subscribing second");
autoCo.subscribe(System.out::println, e -> {}, () -> {});
```

前面的代码产生以下输出：

```
订阅第一个
订阅第二个
订阅源
1 
1 
2 
2 
3 
3
```

### 9.4. 三类配料

当你有很多元素并且想要将它们分成批次时，你可以在Reactor中使用三种广泛的解决方案：分组、窗口化和缓冲。这三个在概念上很接近，因为它们将 a 重新分配Flux<T>到一个聚合中。分组和开窗创建一个Flux<Flux<T>>，同时将聚合缓冲到一个Collection<T>.

#### 9.4.1. 分组Flux<GroupedFlux<T>>

分组是将源Flux<T>分成多个批次的行为，每个批次都匹配一个键。

关联的运算符是groupBy。

每个组都表示为GroupedFlux<T>，它允许你通过调用其 key()方法来检索密钥。

组的内容没有必要的连续性。一旦一个源元素产生一个新的键，这个键的组就会打开，并且与键匹配的元素最终会出现在组中(可以同时打开几个组)。

这意味着组：

1.  总是不相交的(源元素属于一个且仅属于一个组)。
2.  可以包含来自原始序列中不同位置的元素。
3.  永远是空的。

以下示例按值是偶数还是奇数对值进行分组：

```java
StepVerifier.create(
	Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
		.groupBy(i -> i % 2 == 0 ? "even" : "odd")
		.concatMap(g -> g.defaultIfEmpty(-1) //if empty groups, show them
				.map(String::valueOf) //map to string
				.startWith(g.key())) //start with the group's key
	)
	.expectNext("odd", "1", "3", "5", "11", "13")
	.expectNext("even", "2", "4", "6", "12")
	.verifyComplete();
```

|      | 分组最适合当你的组数中到低时。这些组也必须被强制使用(例如由 a flatMap)，以便groupBy 继续从上游获取数据并为更多组提供数据。有时，这两个约束相乘并导致挂起，例如当你具有高基数并且flatMap消费组的并发性太低时。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.4.2. 开窗Flux<Flux<T>>

窗口化是根据大小、时间、定义边界的谓词或定义边界的标准将源拆分Flux<T>为窗口Publisher的行为。

相关的运算符是window、windowTimeout、windowUntil、windowWhile和 windowWhen。

与groupBy根据传入键随机重叠的 ，窗口(大部分时间)按顺序打开。

但是，某些变体仍然可以重叠。例如，in参数是窗口关闭后的元素数window(int maxSize, int skip) ，参数是源中的元素数，然后打开新窗口。因此，如果，在前一个窗口关闭之前打开一个新窗口并且两个窗口重叠。maxSizeskipmaxSize > skip

以下示例显示重叠窗口：

```java
StepVerifier.create(
	Flux.range(1, 10)
		.window(5, 3) //overlapping windows
		.concatMap(g -> g.defaultIfEmpty(-1)) //show empty windows as -1
	)
		.expectNext(1, 2, 3, 4, 5)
		.expectNext(4, 5, 6, 7, 8)
		.expectNext(7, 8, 9, 10)
		.expectNext(10)
		.verifyComplete();
```

|      | 使用反向配置 ( maxSize< skip)，源中的一些元素将被删除并且不属于任何窗口。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

windowUntil在通过and进行基于谓词的窗口化的情况下windowWhile，具有与谓词不匹配的后续源元素也可能导致空窗口，如以下示例所示：

```java
StepVerifier.create(
	Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
		.windowWhile(i -> i % 2 == 0)
		.concatMap(g -> g.defaultIfEmpty(-1))
	)
		.expectNext(-1, -1, -1) //respectively triggered by odd 1 3 5
		.expectNext(2, 4, 6) // triggered by 11
		.expectNext(12) // triggered by 13
		// however, no empty completion window is emitted (would contain extra matching elements)
		.verifyComplete();
```

#### 9.4.3. 缓冲Flux<List<T>>

缓冲类似于开窗，但有以下不同：它不是发出 窗口(每个窗口都是 a Flux<T>)，而是发出缓冲区(Collection<T> 默认情况下是List<T>)。

用于缓冲的运算符反映了用于窗口的运算符：buffer、bufferTimeout、 bufferUntil、bufferWhile和bufferWhen.

在相应的窗口操作符打开一个窗口时，缓冲操作符创建一个新集合并开始向其中添加元素。在窗口关闭的地方，缓冲运算符发出集合。

缓冲也可能导致丢弃源元素或具有重叠的缓冲区，如以下示例所示：

```java
StepVerifier.create(
	Flux.range(1, 10)
		.buffer(5, 3) //overlapping buffers
	)
		.expectNext(Arrays.asList(1, 2, 3, 4, 5))
		.expectNext(Arrays.asList(4, 5, 6, 7, 8))
		.expectNext(Arrays.asList(7, 8, 9, 10))
		.expectNext(Collections.singletonList(10))
		.verifyComplete();
```

与窗口化不同，bufferUntil并且bufferWhile不发出空缓冲区，如以下示例所示：

```java
StepVerifier.create(
	Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
		.bufferWhile(i -> i % 2 == 0)
	)
	.expectNext(Arrays.asList(2, 4, 6)) // triggered by 11
	.expectNext(Collections.singletonList(12)) // triggered by 13
	.verifyComplete();
```

### 9.5. 并行工作ParallelFlux

如今，多核架构已成为一种商品，因此能够轻松并行化工作非常重要。Reactor 通过提供一种特殊的类型来帮助实现这一点 ParallelFlux，它公开了针对并行工作进行了优化的运算符。

要获得一个ParallelFlux，你可以使用parallel()任何运算符Flux。就其本身而言，此方法不会并行化工作。相反，它将工作负载划分为“rails”(默认情况下，rails 的数量与 CPU 内核的数量一样多)。

为了告诉结果ParallelFlux在哪里运行每个轨道(并且，通过扩展，并行运行轨道)，你必须使用runOn(Scheduler). 请注意，有一个推荐的专用Scheduler于并行工作：Schedulers.parallel().

比较接下来的两个示例：

```java
Flux.range(1, 10)
    .parallel(2) 
    .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
```

|      | 我们强制使用多个导轨，而不是依赖 CPU 内核的数量。 |
| ---- | ------------------------------------------------- |
|      |                                                   |

```java
Flux.range(1, 10)
    .parallel(2)
    .runOn(Schedulers.parallel())
    .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
```

第一个示例产生以下输出：

```
主 -> 1
主 -> 2
主 -> 3
主 -> 4
主 -> 5
主 -> 6
主 -> 7
主 -> 8
主 -> 9
主 -> 10
```

第二个在两个线程上正确并行化，如以下输出所示：

```
并行1 -> 1
并行2 -> 2
并行1 -> 3
并行2 -> 4
并行1 -> 5
并行2 -> 6
并行1 -> 7
并行1 -> 9
并行- 2 -> 8
并行-2 -> 10
```

如果，一旦你并行处理你的序列，你想恢复到“正常” Flux并以顺序方式应用运算符链的其余部分，你可以使用 sequential()on 方法ParallelFlux。

请注意，sequential()如果你subscribe使用ParallelFlux aSubscriber而不是使用基于 lambda 的subscribe.

另请注意，subscribe(Subscriber<T>)合并所有轨道，同时 subscribe(Consumer<T>)运行所有轨道。如果该subscribe()方法有一个 lambda，则每个 lambda 的执行次数与 rails 的次数一样多。

你还可以通过该方法访问单个轨道或“组”，并Flux<GroupedFlux<T>>通过该 groups()方法将其他运算符应用于它们composeGroup() 。

### 9.6. 替换默认值Schedulers

正如我们在[线程和调度](https://projectreactor.io/docs/core/release/reference/#schedulers)器部分中描述的，Reactor Core 带有几个 Scheduler实现。虽然你始终可以通过new 工厂方法创建新实例，但每种Scheduler风格也有一个默认单例实例，可通过直接工厂方法(例如Schedulers.boundedElastic()vs Schedulers.newBoundedElastic(…))访问。

Scheduler当你没有明确指定一个时，这些默认实例是需要 a 工作的操作员使用的那些。例如，Flux#delayElements(Duration)使用Schedulers.parallel()实例。

但是，在某些情况下，你可能需要以交叉方式使用其他方式更改这些默认实例，而不必确保你调用的每个操作员都具有你的特定Scheduler作为参数。一个例子是通过包装真正的调度程序来测量每个调度任务所花费的时间，以用于检测目的。换句话说，你可能想要更改默认值Schedulers。

可以通过Schedulers.Factory类更改默认调度程序。默认情况下，a通过类似命名Factory的方法创建所有标准。Scheduler你可以使用自定义实现覆盖其中的每一个。

此外，工厂还公开了一种额外的自定义方法： decorateExecutorService. 它在创建每个 Scheduler由 a 支持的Reactor Core 期间调用ScheduledExecutorService(甚至是非默认实例，例如通过调用创建的实例Schedulers.newParallel())。

这使你可以调整ScheduledExecutorService要使用的实例：默认实例公开为 a Supplier，并且根据Scheduler配置的类型，你可以选择完全绕过该供应商并返回你自己的实例，或者你可以get()选择默认实例并包装它。

|      | 一旦你创建了一个Factory适合你需要的，你必须通过调用来安装它 Schedulers.setFactory(Factory)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

Schedulers最后，在:中有最后一个可自定义的钩子onHandleError。Runnable每当提交给 a 的任务Scheduler抛出 an时，都会调用此钩子Exception(请注意，如果存在运行任务的UncaughtExceptionHandler集合，Thread则调用处理程序和钩子)。

### 9.7. 使用全局挂钩

Reactor 有另一类可配置的回调，由Reactor操作员在各种情况下调用。它们都是在Hooks类中设置的，它们分为三类：

-   [掉落钩子](https://projectreactor.io/docs/core/release/reference/#hooks-dropping)
-   [内部错误挂钩](https://projectreactor.io/docs/core/release/reference/#hooks-internal)
-   [装配挂钩](https://projectreactor.io/docs/core/release/reference/#hooks-assembly)

#### 9.7.1. 掉落钩子

当操作符的来源不符合 Reactive Streams 规范时，会调用丢弃钩子。这些类型的错误超出了正常的执行路径(也就是说，它们不能通过 传播onError)。

通常，尽管之前已经调用过操作员，但仍会 Publisher调用操作员。在这种情况下，该值将被删除。对于无关信号也是如此。onNextonCompletedonNextonError

相应的钩子onNextDropped和onErrorDropped，让你 Consumer为这些 drop 提供一个全局变量。例如，你可以使用它来记录删除并在需要时清理与值关联的资源(因为它永远不会进入响应链的其余部分)。

连续两次设置钩子是附加的：你提供的每个消费者都会被调用。Hooks.resetOnDropped()可以使用这些方法将挂钩完全重置为其默认值。

#### 9.7.2. 内部错误挂钩

当在执行他们的 、 和 方法期间抛出意外时，onOperatorError操作员会调用一个钩子。ExceptiononNextonErroronComplete

与上一个类别不同，这仍然在正常的执行路径内。一个典型的例子是map带有一个映射函数的运算符，它抛出一个Exception(例如除以零)。此时仍然可以通过通常的渠道 onError，这就是运营商所做的。

首先，它通过Exception了onOperatorError。该挂钩可让你检查错误(以及相关值，如果相关)并更改Exception. 当然，你也可以在旁边做一些事情，比如登录并返回原件Exception。

请注意，你可以onOperatorError多次设置挂钩。你可以为 String特定BiFunction和后续调用提供标识符，使用不同的键连接所有执行的函数。另一方面，重复使用同一个键两次可以替换之前设置的功能。

因此，默认挂钩行为既可以完全重置(使用 Hooks.resetOnOperatorError())，也可以仅针对特定的部分重置key(使用 Hooks.resetOnOperatorError(String))。

#### 9.7.3. 装配挂钩

这些钩子与运营商的生命周期联系在一起。它们在一系列操作符被组装(即实例化)时被调用。onEachOperator允许你通过返回不同的Publisher. onLastOperator类似，只是它仅在调用之前链中的最后一个运算符上subscribe调用。

如果你想用横切Subscriber实现来装饰所有运算符，你可以查看Operators#lift帮助你处理各种类型的Reactor的方法Publishers(Flux、Mono、ParallelFlux、GroupedFlux和ConnectableFlux)，以及它们的Fuseable版本。

就像onOperatorError，这些钩子是累积的，可以用一个键来识别。它们也可以部分或全部重置。

#### 9.7.4. 挂钩预设

实用程序类提供了Hooks两个预设挂钩。这些是默认行为的替代方案，你可以通过调用其相应方法来使用它们，而不是自己提出钩子：

-   onNextDroppedFail():onNextDropped用于抛出Exceptions.failWithCancel() 异常。它现在默认在 DEBUG 级别记录删除的值。要返回旧的默认投掷行为，请使用onNextDroppedFail().
-   onOperatorDebug(): 此方法激活[调试模式](https://projectreactor.io/docs/core/release/reference/#debug-activate)。它与onOperatorError钩子联系在一起，因此调用resetOnOperatorError()也会重置它。你可以使用 独立重置它 resetOnOperatorDebug()，因为它在内部使用特定键。

### 9.8. 将上下文添加到响应序列

从命令式编程视角切换到响应式编程思维方式时遇到的重大技术挑战之一在于如何处理线程。

与你可能习惯的相反，在响应式编程中，你可以使用 aThread 来处理几个大致同时运行的异步序列(实际上，在非阻塞锁步中)。执行也可以轻松且经常从一个线程跳转到另一个线程。

对于使用依赖于更“稳定”线程模型的功能的开发人员来说，这种安排尤其困难，例如ThreadLocal. 由于它允许你将数据与线程相关联，因此在响应式上下文中使用它变得很棘手。因此，依赖于ThreadLocalReactor 的库至少会带来新的挑战。在最坏的情况下，他们工作很糟糕甚至失败。使用 Logback 的 MDC 来存储和记录相关 ID 就是这种情况的一个典型例子。

使用的常用解决方法是通过使用(例如)ThreadLocal移动上下文数据C，沿着你的业务数据，按顺序排列。这看起来不太好，并且会将正交关注点(上下文数据)泄漏到你的方法和 签名中。TTuple2<T, C>Flux

从版本3.1.0开始，Reactor 带有一个高级特性，它有点类似于ThreadLocal但可以应用于 aFlux或 aMono而不是 a Thread。此功能称为Context.

为了说明它的外观，以下示例既读取又写入Context：

```java
String key = "message";
Mono<String> r = Mono.just("Hello")
    .flatMap(s -> Mono.deferContextual(ctx ->
         Mono.just(s + " " + ctx.get(key))))
    .contextWrite(ctx -> ctx.put(key, "World"));

StepVerifier.create(r)
            .expectNext("Hello World")
            .verifyComplete();
```

在以下部分中，我们将介绍Context它以及如何使用它，以便你最终理解前面的示例。

|      | 这是一项高级功能，更针对库开发人员。它需要对[a 的生命周期有Subscription](https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3-subscription-code)很好的理解，并且适用于负责订阅的库。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.8.1. ContextAPI _

Context是一个让人想起 . 它的接口。Map它存储键值对，并允许你获取通过其键存储的值。它有一个简化版本，只公开读取方法，ContextView. 进一步来说：

-   key 和 values 都是 type Object，因此Context(and ContextView) 实例可以包含来自不同库和源的任意数量的高度不同的值。
-   AContext是不可变的。它公开了 write 方法put，putAll但它们会产生一个新实例。
-   对于甚至不公开此类写入方法的只读 API，ContextView自 3.4.0 以来就有超级接口
-   你可以使用 来检查密钥是否存在hasKey(Object key)。
-   用于getOrDefault(Object key, T defaultValue)检索一个值(转换为 a )或在实例没有该键T时回退到默认值。Context
-   用于getOrEmpty(Object key)获取Optional<T>(Context实例尝试将存储的值转换为T)。
-   用于put(Object key, Object value)存储键值对，返回一个新 Context实例。你还可以使用 将两个上下文合并为一个新上下文 putAll(ContextView)。
-   用于delete(Object key)删除与键关联的值，返回一个新的 Context.

|      | 创建 时，你可以使用静态方法创建具有最多五个键值对的Context预值实例。它们需要 2、4、6、8 或 10 个 实例，每两个实例都是要添加到.ContextContext.ofObjectObjectContext或者，你也可以Context使用Context.empty(). |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.8.2. 将 a绑定Context到 aFlux并写入

为了使 aContext有用，它必须绑定到特定的序列，并且链中的每个操作员都可以访问它。请注意，运算符必须是 Reactor-native 运算符，这 Context是特定于Reactor的。

实际上，a与链中的Context每个相关联。Subscriber它使用Subscription 传播机制使每个运营商都可以使用自己，从最终开始 subscribe并向上移动。

为了填充Context只能在订阅时完成的 ，你需要使用contextWrite运算符。

contextWrite(ContextView)合并ContextView你提供的和 Context下游的(请记住，Context从链的底部向顶部传播)。这是通过调用 来完成的putAll，从而为上游生成一个 NEW Context。

|      | 你还可以使用更高级的contextWrite(Function<Context, Context>). 它从下游接收副本Context，让你根据需要放置或删除值，并返回新值Context以供使用。你甚至可以决定返回一个完全不同的实例，尽管确实不建议这样做(这样做可能会影响依赖Context. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.8.3. 阅读 a Context, 通过ContextView

一旦你填充了 a Context，你可能想在运行时查看它。大多数时候，将信息放入Context 最终用户方面的责任，而利用该信息则在第三方库方面，因为这些库通常位于客户端代码的上游。

Context面向读取的运算符允许通过暴露其从运算符链中获取数据ContextView：

-   要从类似源的运算符访问上下文，请使用deferContextual工厂方法
-   要从操作员链的中间访问上下文，请使用transformDeferredContextual(BiFunction)
-   或者，在处理内部序列时(例如在 a 内部flatMap)，ContextView 可以使用Mono.deferContextual(Mono::just). 通常，你可能希望直接在 defer 的 lambda 中执行有意义的工作，例如。Mono.deferContextual(ctx → doSomethingAsyncWithContextData(v, ctx.get(key))) flatMappedv的值在哪里。

|      | 为了在Context不误导用户认为可以在数据通过管道运行时对其进行写入的情况下进行读取，只有ContextView上面的操作员会公开 。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.8.4. 简单的Context例子

本节中的示例旨在更好地理解使用Context.

我们首先更详细地回顾一下介绍中的简单示例，如以下示例所示：

```java
String key = "message";
Mono<String> r = Mono.just("Hello")
    .flatMap(s -> Mono.deferContextual(ctx ->
         Mono.just(s + " " + ctx.get(key)))) 
    .contextWrite(ctx -> ctx.put(key, "World")); 

StepVerifier.create(r)
            .expectNext("Hello World") 
            .verifyComplete();
```

|      | contextWrite(Function)操作符 链"World"以Context对"message". |
| ---- | ------------------------------------------------------------ |
|      | 我们flatMap在源元素上，具体化ContextViewwithMono.deferContextual() 并直接提取与原始单词关联的数据并将"message"其与原始单词连接。 |
|      | 结果Mono<String>发出"Hello World".                       |

|      | 上面的编号与实际的线序不是错误的。它代表执行顺序。即使contextWrite是链的最后一部分，它也是最先执行的部分(由于其订阅时间性质以及订阅信号从下到上流动的事实)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 在你的运算符链中，你写入的位置 Context和读取的位置的相对位置很重要。是不可变的Context ，其内容只能由其上方的运算符看到，如下例所示： |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

```java
String key = "message";
Mono<String> r = Mono.just("Hello")
    .contextWrite(ctx -> ctx.put(key, "World")) 
    .flatMap( s -> Mono.deferContextual(ctx ->
        Mono.just(s + " " + ctx.getOrDefault(key, "Stranger")))); 

StepVerifier.create(r)
            .expectNext("Hello Stranger") 
            .verifyComplete();
```

|      | Context被写入链中太高的位置。                              |
| ---- | ------------------------------------------------------------ |
|      | 结果，在 中flatMap，没有与我们的键关联的值。而是使用默认值。 |
|      | 由此Mono<String>产生"Hello Stranger".                    |

同样，在多次尝试向 写入相同键的情况下，写入Context的相对顺序也很重要。读取的操作员会Context看到最接近其下方设置的值，如以下示例所示：

```java
String key = "message";
Mono<String> r = Mono
    .deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
    .contextWrite(ctx -> ctx.put(key, "Reactor")) 
    .contextWrite(ctx -> ctx.put(key, "World")); 

StepVerifier.create(r)
            .expectNext("Hello Reactor") 
            .verifyComplete();
```

|      | 对 key 的写尝试"message"。                                 |
| ---- | ------------------------------------------------------------ |
|      | 对 key 的另一次写入尝试"message"。                         |
|      | deferContextual唯一看到最接近它(并低于它)的值集："Reactor". |

在前面的示例中，是在订阅期间Context填充的。"World"然后订阅信号向上游移动并发生另一次写入。这会产生第二个不可变Context的值为"Reactor". 之后，数据开始流动。deferContextual看到Context最接近它的，这是我们的第二个值Context( "Reactor"作为 a 暴露给用户ContextView)。

你可能想知道 是否Context与数据信号一起传播。如果是这种情况，flatMap在这两次写入之间放置另一个将使用来自 top 的值Context。但事实并非如此，如以下示例所示：

```java
String key = "message";
Mono<String> r = Mono
    .deferContextual(ctx -> Mono.just("Hello " + ctx.get(key))) 
    .contextWrite(ctx -> ctx.put(key, "Reactor")) 
    .flatMap( s -> Mono.deferContextual(ctx ->
        Mono.just(s + " " + ctx.get(key)))) 
    .contextWrite(ctx -> ctx.put(key, "World")); 

StepVerifier.create(r)
            .expectNext("Hello Reactor World") 
            .verifyComplete();
```

|      | 这是第一次发生的写入。                              |
| ---- | --------------------------------------------------- |
|      | 这是发生的第二次写入。                              |
|      | 顶部上下文读取看到第二次写入。                      |
|      | 将flatMap初始读取的结果与第一次写入的值连接起来。 |
|      | Mono发出"Hello Reactor World"。_                |

原因是 与Context相关联，Subscriber并且每个操作员Context通过从其下游请求它来访问Subscriber。

最后一个有趣的传播案例是在Contexta 中也写入 的案例flatMap，如下例所示：

```java
String key = "message";
Mono<String> r = Mono.just("Hello")
    .flatMap( s -> Mono
        .deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key)))
    )
    .flatMap( s -> Mono
        .deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key)))
        .contextWrite(ctx -> ctx.put(key, "Reactor")) 
    )
    .contextWrite(ctx -> ctx.put(key, "World")); 

StepVerifier.create(r)
            .expectNext("Hello World Reactor")
            .verifyComplete();
```

|      | 这contextWrite不会影响其flatMap.     |
| ---- | ---------------------------------------- |
|      | 这contextWrite会影响主序列的Context. |

在前面的示例中，最终发出的值是"Hello World Reactor"“Hello Reactor World”而不是“Hello Reactor World”，因为contextWrite写入"Reactor"的 是作为 second 的内部序列的一部分flatMap。结果，它是不可见的或通过主序列传播的，并且第一个flatMap看不到它。传播和不变性隔离了Context创建中间内部序列的 in 运算符，例如flatMap.

#### 9.8.5. 完整示例

现在我们可以考虑一个更真实的图书馆从 读取信息的例子Context：一个响应式 HTTP 客户端，它以 aMono<String>作为 a 的数据源，PUT但也查找特定的 Context 键以将相关 ID 添加到请求的标头中。

从用户的角度来看，它是这样调用的：

```java
doPut("www.example.com", Mono.just("Walter"))
```

为了传播相关 ID，它会被调用如下：

```java
doPut("www.example.com", Mono.just("Walter"))
	.contextWrite(Context.of(HTTP_CORRELATION_ID, "2-j3r9afaf92j-afkaf"))
```

如前面的代码片段所示，用户代码使用键值对contextWrite填充 a 。操作符的上游是HTTP 客户端库返回的(HTTP 响应的简单表示)。因此它有效地将信息从用户代码传递到库代码。ContextHTTP_CORRELATION_IDMono<Tuple2<Integer, String>>

以下示例从库的角度显示了模拟代码，该代码读取上下文并在找到相关 ID 时“增强请求”：

```java
static final String HTTP_CORRELATION_ID = "reactive.http.library.correlationId";

Mono<Tuple2<Integer, String>> doPut(String url, Mono<String> data) {
  Mono<Tuple2<String, Optional<Object>>> dataAndContext =
      data.zipWith(Mono.deferContextual(c -> 
          Mono.just(c.getOrEmpty(HTTP_CORRELATION_ID))) 
      );

  return dataAndContext.<String>handle((dac, sink) -> {
      if (dac.getT2().isPresent()) { 
        sink.next("PUT <" + dac.getT1() + "> sent to " + url +
            " with header X-Correlation-ID = " + dac.getT2().get());
      }
      else {
        sink.next("PUT <" + dac.getT1() + "> sent to " + url);
      }
        sink.complete();
      })
      .map(msg -> Tuples.of(200, msg));
}
```

|      | 实现ContextView贯穿Mono.deferContextual和... |
| ---- | ------------------------------------------------ |
|      | 在延迟中，提取相关 ID 键的值，作为Optional.    |
|      | 如果密钥存在于上下文中，则使用相关 ID 作为标头。 |

库片段Mono使用.zip 压缩数据Mono.deferContextual(Mono::just)。这给了库 a Tuple2<String, ContextView>，并且该上下文包含HTTP_CORRELATION_ID来自下游的条目(因为它位于订阅者的直接路径上)。

然后，库代码用于为该键map提取一个Optional<String>，并且，如果该条目存在，它使用传递的相关 ID 作为X-Correlation-ID标头。最后一部分由handle.

验证使用相关 ID 的库代码的整个测试可以编写如下：

```java
@Test
public void contextForLibraryReactivePut() {
  Mono<String> put = doPut("www.example.com", Mono.just("Walter"))
      .contextWrite(Context.of(HTTP_CORRELATION_ID, "2-j3r9afaf92j-afkaf"))
      .filter(t -> t.getT1() < 300)
      .map(Tuple2::getT2);

  StepVerifier.create(put)
              .expectNext("PUT <Walter> sent to www.example.com" +
                  " with header X-Correlation-ID = 2-j3r9afaf92j-afkaf")
              .verifyComplete();
}
```

### 9.9. 处理需要清理的对象

在非常特殊的情况下，你的应用程序可能会处理一旦不再使用就需要某种形式的清理的类型。这是一个高级场景——例如，当你有引用计数对象或处理堆外对象时。NettyByteBuf是两者的典型例子。

为了确保正确清理此类对象，你需要逐个考虑它Flux，Flux以及在几个全局钩子中(请参阅[使用全局钩子](https://projectreactor.io/docs/core/release/reference/#hooks))：

-   /doOnDiscard Flux运算Mono符
-   onOperatorError钩子_
-   onNextDropped钩子_
-   特定于操作员的处理程序

这是必要的，因为每个钩子都考虑到了特定的清理子集，并且用户可能希望(例如)在onOperatorError.

请注意，一些运算符不太适合处理需要清理的对象。例如，bufferWhen可以引入重叠缓冲区，这意味着我们之前使用的丢弃“本地挂钩”可能会将第一个缓冲区视为被丢弃并清理其中仍然有效的第二个缓冲区中的元素。

|      | 出于清理的目的，所有这些钩子必须是 IDEMPOTENT。在某些情况下，它们可能会多次应用于同一个对象。与doOnDiscard执行类级别instanceOf检查的运算符不同，全局钩子还处理可以是 any 的实例Object。由用户的实现来区分哪些实例需要清理，哪些不需要。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.9.1. 运算符或doOnDiscard本地挂钩

这个钩子专门用于清理对象，否则这些对象永远不会暴露给用户代码。它旨在作为在正常情况下运行的流的清理钩子(不是推送太多项目的格式错误的源，由 涵盖onNextDropped)。

它是本地的，从某种意义上说，它是通过运算符激活的，并且仅适用于给定的Fluxor Mono。

明显的情况包括从上游过滤元素的运算符。这些元素永远不会到达下一个操作员(或最终订阅者)，但这是正常执行路径的一部分。因此，它们被传递给doOnDiscard钩子。何时可以使用该doOnDiscard钩子的示例包括：

-   filter：不符合过滤条件的项目被视为“丢弃”。
-   skip：跳过的项目被丢弃。
-   buffer(maxSize, skip)with maxSize < skip：一个“丢弃缓冲区”——缓冲区之间的项目被丢弃。

但doOnDiscard不限于过滤操作符，也被用于在内部对数据进行排队以达到背压目的的操作符使用。更具体地说，在大多数情况下，这在取消期间很重要。从其源预取数据并随后根据需要将其排放到其订户的操作员在被取消时可能具有未发送的数据。此类操作员doOnDiscard在取消期间使用挂钩来清除其内部背压Queue。

|      | 每个调用doOnDiscard(Class, Consumer)都是与其他调用相加的，只要它是可见的，并且只有上游的操作员可以使用它。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 9.9.2. onOperatorError钩子_

该onOperatorError钩子旨在以横向方式修改错误(类似于 AOP 的 catch-and-rethrow)。

当在onNext信号处理过程中发生错误时，正在发出的元素被传递给onOperatorError.

如果该类型的元素需要清理，你需要在onOperatorError钩子中实现它，可能在错误重写代码之上。

#### 9.9.3. onNextDropped钩子_

使用 malformedPublishers时，可能会出现操作员在预期没有元素的情况下接收到元素的情况(通常是在接收到onErrororonComplete信号之后)。在这种情况下，意外元素被“丢弃”——即传递给onNextDropped钩子。如果你有需要清理的类型，则必须在onNextDropped钩子中检测这些类型并在那里实现清理代码。

#### 9.9.4. 特定于操作员的处理程序

一些处理缓冲区或收集值作为其操作的一部分的操作员具有特定的处理程序，用于收集的数据未向下游传播的情况。如果将此类运算符与需要清理的类型一起使用，则需要在这些处理程序中执行清理。

例如，distinct当操作符终止(或被取消)时调用这样一个回调，以清除它用来判断元素是否不同的集合。默认情况下，集合是 a HashSet，清理回调是 a HashSet::clear。但是，如果你处理引用计数的对象，你可能希望将其更改为更复杂的处理程序，该处理程序将release在调用集合中的每个元素之前clear()对其进行处理。

### 9.10. 零安全

尽管Java不允许使用其类型系统表达 null 安全性，但Reactor现在提供了注解来声明 API 的可空性，类似于 Spring Framework 5 提供的那些。

Reactor 使用这些注解，但它们也可以在任何基于Reactor的Java项目中用于声明空安全 API。方法体内使用的类型的可空性超出了此功能的范围。

这些注解使用[JSR 305](https://jcp.org/en/jsr/detail?id=305) 注解(由 IntelliJ IDEA 等工具支持的休眠 JSR)进行元注解，以向Java开发人员提供与空安全相关的有用警告，以避免 NullPointerException在运行时。JSR 305 元注解允许工具供应商以通用方式提供空安全支持，而无需对Reactor注解的硬编码支持。

|      | Kotlin 1.1.5+ 没有必要也不建议在项目类路径中依赖 JSR 305。 |
| ---- | ---------------------------------------------------------- |
|      |                                                            |

Kotlin 也使用它们，它本机支持 [null 安全性](https://kotlinlang.org/docs/reference/null-safety.html)。有关更多详细信息，请参阅 [此专用部分](https://projectreactor.io/docs/core/release/reference/#kotlin-null-safety)。

包中提供了以下注解reactor.util.annotation：

-   [@NonNull](https://projectreactor.io/docs/core/release/api/reactor/util/annotation/NonNull.html)：表示特定的参数、返回值或字段不能是null。(适用的参数和返回值不需要@NonNullApi)。
-   [@Nullable](https://projectreactor.io/docs/core/release/api/reactor/util/annotation/Nullable.html)：表示参数、返回值或字段可以是null。
-   [@NonNullApi](https://projectreactor.io/docs/core/release/api/reactor/util/annotation/NonNullApi.html): 指示非空的包级注解是参数和返回值的默认行为。

|      | 尚不支持泛型类型参数、变量参数和数组元素的可空性。有关最新信息， 请参阅[问题 #878 。](https://github.com/reactor/reactor-core/issues/878) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/advancedFeatures.adoc) 到“[高级功能和概念](https://projectreactor.io/docs/core/release/reference/#advanced)”

## 附录 A：我需要哪个运算符？

|      | 在本节中，如果一个运算符特定于[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)或[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)，它会相应地添加前缀和链接，如下所示：[Flux#fromArray](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#fromArray-T:A-)。通用运算符没有前缀，并且提供了两种实现的链接，例如：( justFlux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#just-T...-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#just-T-)。当一个特定的用例被运算符组合覆盖时，它被表示为一个方法调用，带有一个前导点和括号中的参数，如下所示：.methodCall(parameter). |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

我想处理：

-   [创建一个新序列...](https://projectreactor.io/docs/core/release/reference/#which.create)
-   [转换现有序列](https://projectreactor.io/docs/core/release/reference/#which.values)
-   [过滤序列](https://projectreactor.io/docs/core/release/reference/#which.filtering)
-   [窥视序列](https://projectreactor.io/docs/core/release/reference/#which.peeking)
-   [处理错误](https://projectreactor.io/docs/core/release/reference/#which.errors)
-   [与时间一起工作](https://projectreactor.io/docs/core/release/reference/#which.time)
-   [拆分](https://projectreactor.io/docs/core/release/reference/#which.window)[通量](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)
-   [回到同步世界](https://projectreactor.io/docs/core/release/reference/#which.blocking)
-   [将Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)[多播](https://projectreactor.io/docs/core/release/reference/#which.multicasting)到多个[订阅者](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Subscriber.html?is-external=true)

### A.1. 创建一个新序列...

-   发出 a T，我已经有了just：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#just-T...-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#just-T-))
    -   …来自[Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) : [Mono#justOrEmpty(Optional)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#justOrEmpty-java.util.Optional-)
    -   …来自潜在的nullT：[Mono#justOrEmpty(T)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#justOrEmpty-T-)
-   发出T由方法返回的a：(justFlux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#just-T...-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#just-T-)以及
    -   …但懒惰地捕获：使用[Mono#fromSupplier](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromSupplier-java.util.function.Supplier-)或将just([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#just-T...-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#just-T-))包装在内部defer([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#defer-java.util.function.Supplier-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#defer-java.util.function.Supplier-))
-   发出几个T我可以明确列举的：[Flux#just(T...)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#just-T...-)
-   迭代：
    -   一个数组：[通量#fromArray](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#fromArray-T:A-)
    -   集合或可迭代：[Flux#fromIterable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#fromIterable-java.lang.Iterable-)
    -   整数范围：[通量#range](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#range-int-int-)
    -   为每个订阅提供的[流： ](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html)[Flux#fromStream(Supplier)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#fromStream-java.util.function.Supplier-)
-   从各种单值源发出，例如：
    -   供应[商](https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html)：[Mono#fromSupplier](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromSupplier-java.util.function.Supplier-)
    -   一个任务：[Mono#fromCallable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromCallable-java.util.concurrent.Callable-)，[Mono#fromRunnable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromRunnable-java.lang.Runnable-)
    -   一个[CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) : [Mono#fromFuture](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromFuture-java.util.concurrent.CompletableFuture-)
-   完成：(empty通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#empty--)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#empty--))
-   立即出错error：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#error-java.lang.Throwable-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#error-java.lang.Throwable-))
    -   …但懒惰地构建[Throwable](https://docs.oracle.com/javase/8/docs/api/java/lang/Throwable.html) : error(Supplier<Throwable>)( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#error-java.util.function.Supplier-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#error-java.util.function.Supplier-) )
-   从不做任何事情：(never通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#never--)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#never--))
-   在订阅时决定defer：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#defer-java.util.function.Supplier-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#defer-java.util.function.Supplier-))
-   这取决于一次性资源using：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#using-java.util.concurrent.Callable-java.util.function.Function-java.util.function.Consumer-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#using-java.util.concurrent.Callable-java.util.function.Function-java.util.function.Consumer-))
-   以编程方式生成事件(可以使用状态)：
    -   同步和一对一：[Flux#generate](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#generate-java.util.concurrent.Callable-java.util.function.BiFunction-)
    -   异步(也可以是同步的)，一次可以进行多次发射：[Flux#create](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#create-java.util.function.Consumer-) (也可以是[Mono#create](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#create-java.util.function.Consumer-)，没有多重发射方面)

### A.2. 转换现有序列

-   我想转换现有数据：
    -   一对一的基础上(例如字符串到它们的长度)map：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#map-java.util.function.Function-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#map-java.util.function.Function-))
        -   …只需投射它：(cast通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#cast-java.lang.Class-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#cast-java.lang.Class-))
        -   …为了实现每个源值的索引：[Flux#index](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#index--)
    -   在一对一的基础上(例如字符串到他们的字符)：(flatMapFlux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#flatMap-java.util.function.Function-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMap-java.util.function.Function-) +使用工厂方法
    -   每个源元素和/或状态的编程行为是一对一的handle：( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#handle-java.util.function.BiConsumer-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#handle-java.util.function.BiConsumer-) )
    -   为每个源项目运行一个异步任务(例如 http 请求的 url)：(flatMapFlux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#flatMap-java.util.function.Function-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMap-java.util.function.Function-) +一个异步[发布](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true)者 -returning 方法
        -   …忽略一些数据：有条件地在 flatMap lambda 中返回[Mono.empty()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#empty--)
        -   …保留原始序列顺序：[Flux#flatMapSequential](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#flatMapSequential-java.util.function.Function-)(这会立即触发异步进程，但会重新排序结果)
        -   …异步任务可以从[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)源返回多个值：[Mono#flatMapMany](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMapMany-java.util.function.Function-)
-   我想将预设元素添加到现有序列中：
    -   开头：[Flux#startWith(T...)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#startWith-T...-)
    -   最后：[Flux#concatWithValues(T...)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#concatWithValues-T...-)
-   我想聚合一个[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)：(Flux#前缀假设如下)
    -   进入一个列表：[collectList](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#collectList--)，[collectSortedList](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#collectSortedList--)
    -   进入地图：[collectMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#collectMap-java.util.function.Function-)，[collectMultiMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#collectMultimap-java.util.function.Function-)
    -   进入任意容器：[收集](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#collect-java.util.stream.Collector-)
    -   进序列的大小：[count](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#count--)
    -   通过在每个元素之间应用一个函数(例如运行总和)：[减少](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#reduce-A-java.util.function.BiFunction-)
        -   …但发出每个中间值：[扫描](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#scan-A-java.util.function.BiFunction-)
    -   从谓词转换为布尔值：
        -   应用于所有值 (AND)：[全部](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#all-java.util.function.Predicate-)
        -   应用于至少一个值 (OR)：[任何](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#any-java.util.function.Predicate-)
        -   测试是否存在任何值：[hasElements (在](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#hasElements--) [hasElement](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#hasElement--)中有一个[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)等效项)
        -   测试特定值的存在：[hasElement(T)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#hasElement-T-)
-   我想合并出版商……
    -   按顺序：[Flux#concat](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#concat-org.reactivestreams.Publisher...-)或.concatWith(other)( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#concatWith-org.reactivestreams.Publisher-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#concatWith-org.reactivestreams.Publisher-) )
        -   …但延迟任何错误，直到发出剩余的发布者：[Flux#concatDelayError](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#concatDelayError-org.reactivestreams.Publisher-)
        -   …但急切地订阅后续发布者：[Flux#mergeSequential](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#mergeSequential-int-org.reactivestreams.Publisher...-)
    -   按发射顺序(组合项目在它们到来时发射)：[Flux#merge](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#merge-int-org.reactivestreams.Publisher...-) / .mergeWith(other)( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#mergeWith-org.reactivestreams.Publisher-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#mergeWith-org.reactivestreams.Publisher-) )
        -   …使用不同的类型(转换合并)：[Flux#zip](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#zip-java.util.function.Function-org.reactivestreams.Publisher...-) / [Flux#zipWith](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#zipWith-org.reactivestreams.Publisher-)
    -   通过配对值：
        -   从 2 个 Monos 到[Tuple2](https://projectreactor.io/docs/core/release/api/reactor/util/function/Tuple2.html)：[Mono#zipWith](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#zipWith-reactor.core.publisher.Mono-)
        -   全部完成时来自 n 个 Monos：[Mono#zip](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#zip-java.util.function.Function-reactor.core.publisher.Mono...-)
    -   通过协调他们的终止：
        -   从 1 Mono 和任何来源到[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)：[Mono#and](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#and-org.reactivestreams.Publisher-)
        -   全部完成时来自 n 个来源：[Mono#when](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#when-java.lang.Iterable-)
        -   进入任意容器类型：
            -   每次所有面都发出：[Flux#zip](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#zip-java.util.function.Function-org.reactivestreams.Publisher...-)(直到最小的基数)
            -   每次新值到达任一侧时：[Flux#combineLatest](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#combineLatest-java.util.function.Function-int-org.reactivestreams.Publisher...-)
    -   选择第一个出版商...
        -   产生一个值( onNext): firstWithValue( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#firstWithValue-java.lang.Iterable-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#firstWithValue-java.lang.Iterable-) )
        -   产生任何信号：([通量](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#firstWithSignal-java.lang.Iterable-)|单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#firstWithSignal-java.lang.Iterable-)firstWithSignal)
    -   由源序列中的元素触发：[switchMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#switchMap-java.util.function.Function-)(每个源元素映射到一个发布者)
    -   由发布者序列中的下一个发布者开始触发：[switchOnNext](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#switchOnNext-org.reactivestreams.Publisher-)
-   我想重复一个现有的序列repeat：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#repeat--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#repeat--))
    -   …但在时间间隔：Flux.interval(duration).flatMap(tick → myExistingPublisher)
-   我有一个空序列但是......
    -   我想要一个值defaultIfEmpty：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#defaultIfEmpty-T-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#defaultIfEmpty-T-))
    -   我想要另一个序列switchIfEmpty：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#switchIfEmpty-org.reactivestreams.Publisher-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#switchIfEmpty-reactor.core.publisher.Mono-))
-   我有一个序列，但我对值不感兴趣：(ignoreElementsFlux.ignoreElements [()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#ignoreElements--) | [Mono.ignoreElement()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#ignoreElement--))
    -   …我希望完成表示为[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html) : then( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#then--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#then--) )
    -   …我想在最后等待另一个任务完成thenEmpty：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#thenEmpty-org.reactivestreams.Publisher-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#thenEmpty-org.reactivestreams.Publisher-))
    -   …最后我想切换到另一个[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)：[Mono#then(mono)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#then-reactor.core.publisher.Mono-)
    -   …我想在最后发出一个值：[Mono#thenReturn(T)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#thenReturn-V-)
    -   …最后我想切换到[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#thenMany-org.reactivestreams.Publisher-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#thenMany-org.reactivestreams.Publisher-)thenMany)
-   我有一个 Mono，我想推迟完成......
    -   …直到另一个发布者，从这个值派生，完成：[Mono#delayUntil(Function)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#delayUntil-java.util.function.Function-)
-   我想递归地将元素扩展为序列图并发出组合......
    -   …先扩展图的宽度expand(Function)：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#expand-java.util.function.Function-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#expand-java.util.function.Function-))
    -   …先扩展图深度expandDeep(Function)：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#expandDeep-java.util.function.Function-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#expandDeep-java.util.function.Function-))

### A.3. 窥视序列

-   在不修改最终序列的情况下，我想：
    -   在以下方面获得通知/执行其他行为(有时称为“副作用”)：
        -   排放：(doOnNext通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnNext-java.util.function.Consumer-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnNext-java.util.function.Consumer-))
        -   完成：[Flux#doOnComplete](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnComplete-java.lang.Runnable-)，[Mono#doOnSuccess](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnSuccess-java.util.function.Consumer-)(包括结果，如果有的话)
        -   错误终止：(doOnError通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnError-java.util.function.Consumer-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnError-java.util.function.Consumer-))
        -   取消：(doOnCancel通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnCancel-java.lang.Runnable-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnCancel-java.lang.Runnable-))
        -   序列的“开始” doFirst：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doFirst-java.lang.Runnable-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doFirst-java.lang.Runnable-))
            -   这与[Publisher#subscribe(Subscriber)相关联](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true#subscribe(org.reactivestreams.Subscriber))
        -   订阅后：(doOnSubscribe通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnSubscribe-java.util.function.Consumer-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnSubscribe-java.util.function.Consumer-))
            -   Subscription确认后subscribe
            -   这与[订阅者#onSubscribe(Subscription)相关联](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Subscriber.html?is-external=true#onSubscribe(org.reactivestreams.Subscription))
        -   请求：(doOnRequest通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnRequest-java.util.function.LongConsumer-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnRequest-java.util.function.LongConsumer-))
        -   完成或错误doOnTerminate：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnTerminate-java.lang.Runnable-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnTerminate-java.lang.Runnable-))
            -   但是在它被传播到下游之后doAfterTerminate：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doAfterTerminate-java.lang.Runnable-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doAfterTerminate-java.lang.Runnable-))
        -   任何类型的信号，表示为[Signal](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Signal.html) : doOnEach( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnEach-java.util.function.Consumer-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnEach-java.util.function.Consumer-) )
        -   任何终止条件(完成、错误、取消)doFinally：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doFinally-java.util.function.Consumer-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doFinally-java.util.function.Consumer-))
    -   记录内部发生的事情log：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#log--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#log--))
-   我想知道所有事件：
    -   每个都表示为[Signal](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Signal.html)对象：
        -   在序列外的回调中doOnEach：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnEach-java.util.function.Consumer-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doOnEach-java.util.function.Consumer-))
        -   而不是原来的 onNext 排放materialize：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#materialize--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#materialize--))
            -   …然后回到 onNexts dematerialize：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#dematerialize--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#dematerialize--))
    -   作为日志中的一行log：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#log--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#log--))

### A.4. 过滤序列

-   我想过滤一个序列：
    -   基于任意标准filter：( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#filter-java.util.function.Predicate-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#filter-java.util.function.Predicate-) )
        -   …这是异步计算的filterWhen：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#filterWhen-java.util.function.Function-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#filterWhen-java.util.function.Function-))
    -   限制发射对象的类型ofType：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#ofType-java.lang.Class-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#ofType-java.lang.Class-))
    -   通过完全忽略这些值：(ignoreElementsFlux.ignoreElements [()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#ignoreElements--) | [Mono.ignoreElement()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#ignoreElement--))
    -   通过忽略重复：
        -   在整个序列中(逻辑集)：[Flux#distinct](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#distinct--)
        -   随后发出的项目之间(重复数据删除)：[Flux#distinctUntilChanged](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#distinctUntilChanged--)
-   我只想保留序列的一个子集：
    -   通过取 N 个元素：
        -   在序列的开头：[Flux#take(long, true)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#take-long-boolean-)
            -   …从上游请求无限量：[Flux#take(long, false)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#take-long-boolean-)
            -   …基于持续时间：[Flux#take(Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#take-java.time.Duration-)
            -   …只有第一个元素，作为[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)：[Flux#next()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#next--)
            -   …使用[request(N)](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Subscription.html#request(long))而不是取消：[Flux#limitRequest(long)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#limitRequest-long-)
        -   在序列的末尾：[Flux#takeLast](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#takeLast-int-)
        -   直到满足条件(包括)：[Flux#takeUntil](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#takeUntil-java.util.function.Predicate-)(基于谓词)，[Flux#takeUntilOther](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#takeUntilOther-org.reactivestreams.Publisher-)(基于同伴发布者)
        -   满足条件时(不包括)：[Flux#takeWhile](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#takeWhile-java.util.function.Predicate-)
    -   最多取 1 个元素：
        -   在特定位置：[Flux#elementAt](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#elementAt-int-)
        -   最后：[.takeLast(1)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#takeLast-int-)
            -   …如果为空则发出错误：[Flux#last()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#last--)
            -   …如果为空，则发出默认值：[Flux#last(T)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#last-T-)
    -   通过跳过元素：
        -   在序列的开头：[Flux#skip(long)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#skip-long-)
            -   …基于持续时间：[Flux#skip(Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#skip-java.time.Duration-)
        -   在序列的末尾：[Flux#skipLast](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#skipLast-int-)
        -   直到满足条件(包括)：[Flux#skipUntil](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#skipUntil-java.util.function.Predicate-)(基于谓词)，[Flux#skipUntilOther](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#skipUntilOther-org.reactivestreams.Publisher-)(基于同伴发布者)
        -   满足条件时(不包括)：[Flux#skipWhile](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#skipWhile-java.util.function.Predicate-)
    -   按抽样项目：
        -   按持续时间：[通量#sample(持续时间)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#sample-java.time.Duration-)
            -   但保留采样窗口中的第一个元素而不是最后一个元素：[sampleFirst](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#sampleFirst-java.time.Duration-)
        -   通过基于发布者的窗口：[Flux#sample(Publisher)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#sample-org.reactivestreams.Publisher-)
        -   基于发布者“超时”：[Flux#sampleTimeout](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#sampleTimeout-java.util.function.Function-)(每个元素触发一个发布者，如果该发布者不与下一个重叠，则发出)
-   我预计最多 1 个元素(如果超过一个则错误)...
    -   如果序列为空，我想要一个错误：[Flux#single()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#single--)
    -   如果序列为空，我想要一个默认值：[Flux#single(T)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#single-T-)
    -   我也接受一个空序列：[Flux#singleOrEmpty](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#singleOrEmpty--)

### A.5. 处理错误

-   我想创建一个错误序列：( errorFlux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#error-java.lang.Throwable-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#error-java.lang.Throwable-) …
    -   …替换完成的[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)成功：.concat(Flux.error(e))
    -   …替换成功的[Mono的](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)发射：.then(Mono.error(e))
    -   …如果 onNexts 之间的时间过长timeout：( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#timeout-java.time.Duration-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#timeout-java.time.Duration-) )
    -   …懒惰地：(error(Supplier<Throwable>)通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#error-java.util.function.Supplier-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#error-java.util.function.Supplier-))
-   我想要 try/catch 相当于：
    -   投掷：(error通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#error-java.lang.Throwable-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#error-java.lang.Throwable-))
    -   捕获异常：
        -   并回退到默认值onErrorReturn：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorReturn-java.lang.Class-T-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorReturn-java.lang.Class-T-))
        -   并吞下错误(即完成)onErrorComplete：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorComplete-java.lang.Class-T-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorComplete-java.lang.Class-T-))
        -   并回退到另一个[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)或[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html) : onErrorResume( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorResume-java.lang.Class-java.util.function.Function-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorResume-java.lang.Class-java.util.function.Function-) )
        -   包装和重新投掷：(.onErrorMap(t → new RuntimeException(t))通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorMap-java.util.function.Function-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorMap-java.util.function.Function-))
    -   finally 块doFinally：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doFinally-java.util.function.Consumer-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#doFinally-java.util.function.Consumer-))
    -  Java7 的使用模式：( usingFlux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#using-java.util.concurrent.Callable-java.util.function.Function-java.util.function.Consumer-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#using-java.util.concurrent.Callable-java.util.function.Function-java.util.function.Consumer-)工厂方法
-   我想从错误中恢复……
    -   通过后退：
        -   到一个值：(onErrorReturn通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorReturn-java.lang.Class-T-)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorReturn-java.lang.Class-T-))
        -   完成(“吞下”错误)onErrorComplete：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorComplete-java.lang.Class-T-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorComplete-java.lang.Class-T-))
        -   到[Publisher](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true)或[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)，根据错误可能不同：[Flux#onErrorResume](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorResume-java.lang.Class-java.util.function.Function-)和[Mono#onErrorResume](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#onErrorResume-java.lang.Class-java.util.function.Function-)
    -   通过重试……
        -   …使用简单的策略(最大尝试次数)：(retry()Flux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#retry--) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#retry--)，retry(long)([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#retry-long-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#retry-long-))
        -   …由配套控件 Flux 触发retryWhen：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#retryWhen-reactor.util.retry.Retry-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#retryWhen-reactor.util.retry.Retry-))
        -   …使用标准退避策略(带抖动的指数退避)：(retryWhen(Retry.backoff(…))Flux [|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#retryWhen-reactor.util.retry.Retry-) Mono [)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#retryWhen-reactor.util.retry.Retry-)(另请参阅[Retry](https://projectreactor.io/docs/core/release/api/reactor/util/retry/Retry.html)中的其他工厂方法)
-   我想处理背压“错误”(从上游请求最大值并在下游没有产生足够请求时应用该策略)......
    -   通过抛出一个特殊的[IllegalStateException](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalStateException.html?is-external=true)：[Flux#onBackpressureError](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onBackpressureError--)
    -   通过删除多余的值：[Flux#onBackpressureDrop](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onBackpressureDrop--)
        -   …除了最后一个：[Flux#onBackpressureLatest](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onBackpressureLatest--)
    -   通过缓冲多余的值(有界或无界)：[Flux#onBackpressureBuffer](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onBackpressureBuffer--)
        -   …并在有界缓冲区也溢出时应用策略：[Flux#onBackpressureBuffer](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onBackpressureBuffer-int-reactor.core.publisher.BufferOverflowStrategy-)和[BufferOverflowStrategy](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/BufferOverflowStrategy.html)

### A.6. 与时间一起工作

-   我想将排放与测量的时间联系起来……
    -   …提供数据的最佳精度和多功能性：(timed通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#timed--)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#timed--))
        -   [Timed#elapsed()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Timed.html#elapsed--)自上次以来的[持续时间](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html?is-external=true)onNext
        -   [Timed#timestamp()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Timed.html#timestamp--)用于纪元时间戳的[即时](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html?is-external=true)表示(毫秒分辨率)
        -   [Timed#elapsedSinceSubcription()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Timed.html#elapsedSinceSubscription--)自订阅以来的[持续时间](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html?is-external=true)(而不是最后一个 onNext)
        -   对于经过的[Duration](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html?is-external=true) s可以有纳秒级的分辨率
    -   …作为(遗留)[Tuple2](https://projectreactor.io/docs/core/release/api/reactor/util/function/Tuple2.html) …
        -   自上次 onNext: elapsed( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#elapsed--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#elapsed--) )
        -   自古以来(嗯，计算机时间)：(timestamp通量[|](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#timestamp--)单[声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#timestamp--))
-   如果发射之间的延迟太大，我希望我的序列被打断timeout：( [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#timeout-java.time.Duration-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#timeout-java.time.Duration-) )
-   我想从时钟中获取滴答声，有规律的时间间隔：[Flux#interval](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#interval-java.time.Duration-)
-   我想0在初始延迟后发出一个：静态[Mono.delay](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#delay-java.time.Duration-)。
-   我想介绍一个延迟：
    -   在每个 onNext 信号之间：[Mono#delayElement](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#delayElement-java.time.Duration-)，[Flux#delayElements](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#delayElements-java.time.Duration-)
    -   在订阅发生之前delaySubscription：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#delaySubscription-java.time.Duration-) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#delaySubscription-java.time.Duration-))

### A.7. 拆分[通量](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)

-   我想通过边界标准将[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)拆分为 a Flux<Flux<T>>：
    -   大小：[窗口(int)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#window-int-)
        -   …有重叠或掉落的窗口：[window(int, int)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#window-int-int-)
    -   时间[窗口(持续时间)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#window-java.time.Duration-)
        -   …有重叠或掉落的窗口：[window(Duration, Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#window-java.time.Duration-java.time.Duration-)
    -   大小或时间(达到计数或超时时窗口关闭)：[windowTimeout(int, Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#windowTimeout-int-java.time.Duration-)
    -   基于元素的谓词：[windowUntil](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#windowUntil-java.util.function.Predicate-)
        -   ……在下一个窗口(cutBefore变体)中发出触发边界的元素：[.windowUntil(predicate, true)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#windowUntil-java.util.function.Predicate-boolean-)
        -   …在元素匹配谓词时保持窗口打开：[windowWhile](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#windowWhile-java.util.function.Predicate-)(不匹配的元素不发出)
    -   由控件中的 onNexts 表示的任意边界驱动 Publisher: [window(Publisher)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#window-org.reactivestreams.Publisher-) , [windowWhen](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#windowWhen-org.reactivestreams.Publisher-java.util.function.Function-)
-   我想将[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)和边界内的缓冲区元素拆分在一起......
    -   进入[列表](https://docs.oracle.com/javase/8/docs/api/java/util/List.html?is-external=true)：
        -   通过大小边界：[缓冲区(int)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#buffer-int-)
            -   …有重叠或丢弃缓冲区：[buffer(int, int)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#buffer-int-int-)
        -   按持续时间边界：[缓冲区(持续时间)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#buffer-java.time.Duration-java.time.Duration-)
            -   ...有重叠或丢弃缓冲区：[缓冲区(持续时间，持续时间)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#buffer-java.time.Duration-java.time.Duration-)
        -   按大小或持续时间边界：[bufferTimeout(int, Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#bufferTimeout-int-java.time.Duration-)
        -   通过任意标准边界：[bufferUntil(Predicate)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#bufferUntil-java.util.function.Predicate-)
            -   …将触发边界的元素放入下一个缓冲区：[.bufferUntil(predicate, true)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#bufferUntil-java.util.function.Predicate-boolean-)
            -   …在谓词匹配时缓冲并删除触发边界的元素：[bufferWhile(Predicate)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#bufferWhile-java.util.function.Predicate-)
        -   由控件 Publisher 中的 onNexts 表示的任意边界驱动：[buffer(Publisher)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#buffer-org.reactivestreams.Publisher-) , [bufferWhen](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#bufferWhen-org.reactivestreams.Publisher-java.util.function.Function-)
    -   进入任意“集合”类型C：使用[缓冲区(int，Supplier)之类的变体](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#buffer-int-java.util.function.Supplier-)
-   我想拆分[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)以便共享特征的元素最终位于相同的子通量中：[groupBy(Function)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#groupBy-java.util.function.Function-) 提示：请注意，这会返回 a Flux<GroupedFlux<K, T>>，每个内部[GroupedFlux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/GroupedFlux.html)共享相同的K可访问键通过[key()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/GroupedFlux.html#key--)。

### A.8. 回到同步世界

注意：如果从标记为“仅非阻塞”的[调度程序(默认为](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Scheduler.html)[parallel()](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#parallel--)和[single() )中调用，除了](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#single--)[Mono#toFuture](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#toFuture--)之外的所有这些方法都将抛出[UnsupportedOperatorException](https://docs.oracle.com/javase/8/docs/api/java/lang/UnsupportedOperationException.html?is-external=true)。

-   我有一个[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)并且我想：
    -   阻塞直到我可以得到第一个元素：[Flux#blockFirst](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#blockFirst--)
        -   …有超时：[Flux#blockFirst(Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#blockFirst-java.time.Duration-)
    -   阻塞，直到我可以得到最后一个元素(如果为空，则为 null)：[Flux#blockLast](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#blockLast--)
        -   …有超时：[Flux#blockLast(Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#blockLast-java.time.Duration-)
    -   同步切换到[Iterable](https://docs.oracle.com/javase/8/docs/api/java/lang/Iterable.html?is-external=true)：[Flux#toIterable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#toIterable--)
    -   同步切换到Java8 [Stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html)：[Flux#toStream](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#toStream--)
-   我有一个[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)我想要：
    -   阻止，直到我可以得到值：[Mono#block](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#block--)
        -   …有超时：[Mono#block(Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#block-java.time.Duration-)
    -   一个[CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) : [Mono#toFuture](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#toFuture--)

### A.9. [将Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)多播到多个[订阅者](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Subscriber.html?is-external=true)

-   我想将多个[订阅者](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Subscriber.html?is-external=true)连接到一个[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)：
    -   并决定何时使用[connect()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html#connect--)触发源：[publish()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#publish--)(返回[ConnectableFlux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html))
    -   并立即触发源(后期订阅者看到后面的数据)share()：([Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#share--) | [Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#share--))
    -   并在足够多的订阅者注册后永久连接源：[.publish().autoConnect(n)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html#autoConnect-int-)
    -   并在订阅者高于/低于阈值时自动连接和取消源：[.publish().refCount(n)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html#refCount-int-)
        -   …但在取消之前让新订阅者有机会加入：[.publish().refCount(n, Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html#refCount-int-java.time.Duration-)
-   我想缓存来自[发布](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true)者的数据并将其重播给以后的订阅者：
    -   最多n元素：[缓存(int)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#cache-int-)
    -   [缓存在持续时间](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html?is-external=true)(生存时间)内看到的最新元素cache(Duration)：([通量](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#cache-java.time.Duration-)|[单声道](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#cache-java.time.Duration-))
        -   …但只保留n元素：[cache(int, Duration)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#cache-int-java.time.Duration-)
    -   但没有立即触发源：[Flux#replay](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#replay--)(返回[ConnectableFlux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html))

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/apdx-operatorChoice.adoc) 到“[我需要哪个运算符？](https://projectreactor.io/docs/core/release/reference/#which-operator) ”

## 附录 B：如何阅读弹珠图？

当我们介绍Flux和Mono时，我们展示了一个“大理石图”的例子。这些在整个 javadoc 中都可以找到，以便以更直观的方式解释运算符的行为。

在本节中，我们将更深入地研究Reactor文档中用于这些大理石图的约定。首先，让我们看看最常见的运算符模式是如何表示的。

一些运算符是实例方法：它们的输出是通过调用源Flux实例上的方法产生的(如Flux<T> output = source.fluxOperator())：

![普通运算符](https://projectreactor.io/docs/core/release/reference/images/legend-operator-method.svg)

其他运算符是静态方法。它们仍然可以将源作为输入参数，例如 in Flux<T> output = Flux.merge(sourceFlux1, sourcePublisher2)。这些表示如下：

![静态运算符](https://projectreactor.io/docs/core/release/reference/images/legend-operator-static.svg)

请注意，有时我们会根据运算符的输入表示多个变体或行为，在这种情况下，只有一个运算符“框”，但源和输出变体是分开的，如下所示：

![具有两个输入示例的运算符](https://projectreactor.io/docs/core/release/reference/images/legend-operator-double-source.svg)

这些是基本情况，但一些运算符显示了更高级的模式。

例如，ParallelFlux创建多个 rails 以便它们具有多个 output Flux。这些在另一个下方表示，如下图所示：

![并行算子](https://projectreactor.io/docs/core/release/reference/images/legend-operator-parallel.svg)

窗口操作符产生Flux<Flux<T>>：每个窗口打开的主要Flux通知，而内部Flux表示窗口内容和终止。Windows 表示为从 main 分支出来的Flux，如下图所示：

![窗口运算符的输出](https://projectreactor.io/docs/core/release/reference/images/legend-operator-windowing.svg)

有时，操作员将“伴随发布者”作为输入(aFlux或Mono任意 Reactive Stream Publisher)。这样的伴随发布者帮助定制运营商的行为，这将使用一些伴随的信号作为其自身内部行为的触发器。它们如下图所示：

![具有伴随发布者的操作员](https://projectreactor.io/docs/core/release/reference/images/legend-operator-companion.svg)

现在我们已经看到了最常见的运算符模式，让我们展示一下所有不同信号、事件和元素的图形表示，这些信号、事件和元素可能出现在 a Fluxor中Mono：

![所有类型的信号和事件](https://projectreactor.io/docs/core/release/reference/images/legend-events.svg)

最后，同样地，我们有副作用的图形表示，它与响应流信号一起出现：

![副作用：doOn 处理程序的表示](https://projectreactor.io/docs/core/release/reference/images/legend-sideEffects1.svg)

![副作用：在图表中](https://projectreactor.io/docs/core/release/reference/images/legend-sideEffects2.svg)

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/apdx-howtoReadMarbles.adoc) 到“[如何阅读大理石图？](https://projectreactor.io/docs/core/release/reference/#howtoReadMarbles) ”

## 附录 C：常见问题解答、最佳实践和“我如何……？”

本节涵盖以下内容：

-   [如何打包同步阻塞呼叫？](https://projectreactor.io/docs/core/release/reference/#faq.wrap-blocking)
-   [我在我的身上使用了一个运算符，Flux但它似乎不适用。是什么赋予了？](https://projectreactor.io/docs/core/release/reference/#faq.chain)
-   [我的Mono zipWithorzipWhen从未被调用](https://projectreactor.io/docs/core/release/reference/#faq.monoThen)
-   [如何使用retryWhen来模拟retry(3)？](https://projectreactor.io/docs/core/release/reference/#faq.retryWhen)
-   [如何使用retryWhen指数退避？](https://projectreactor.io/docs/core/release/reference/#faq.exponentialBackoff)
-   [使用时如何确保线程关联publishOn()？](https://projectreactor.io/docs/core/release/reference/#faq.thread-affinity-publishon)
-   [什么是上下文日志记录的好模式？(MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc)

### C.1. 如何打包同步阻塞呼叫？

信息源通常是同步和阻塞的。要在你的Reactor应用程序中处理此类来源，请应用以下模式：

```java
Mono blockingWrapper = Mono.fromCallable(() -> { 
    return / make a remote synchronous call / 
});
blockingWrapper = blockingWrapper.subscribeOn(Schedulers.boundedElastic()); 
```

|      | Mono使用fromCallable. _                            |
| ---- | ------------------------------------------------------ |
|      | 返回异步的阻塞资源。                                   |
|      | 确保每个订阅都发生在来自Schedulers.boundedElastic(). |

你应该使用 a Mono，因为源返回一个值。你应该使用 Schedulers.boundedElastic, 因为它创建了一个专用线程来等待阻塞资源而不影响其他非阻塞处理，同时还确保可以创建的线程数量以及可以排队的阻塞任务和在峰值期间推迟。

请注意，subscribeOn不订阅Mono. Scheduler它指定发生订阅调用时使用哪种类型。

### C.2. 我在我的身上使用了一个运算符，Flux但它似乎不适用。是什么赋予了？

确保你的变量.subscribe()受到你认为应该应用的运算符的影响。

响应器操作员是装饰者。它们返回包装源序列并添加行为的不同实例。这就是为什么使用运算符的首选方式是链接调用。

比较以下两个示例：

示例 25. 没有链接(不正确)

```java
Flux<String> flux = Flux.just("something", "chain");
flux.map(secret -> secret.replaceAll(".", "")); 
flux.subscribe(next -> System.out.println("Received: " + next));
```

|      | 错误就在这里。结果未附加到flux变量。 |
| ---- | -------------------------------------- |
|      |                                        |

示例 26. 没有链接(正确)

```java
Flux<String> flux = Flux.just("something", "chain");
flux = flux.map(secret -> secret.replaceAll(".", ""));
flux.subscribe(next -> System.out.println("Received: " + next));
```

以下示例甚至更好(因为它更简单)：

示例 27. 使用链接(最佳)

```java
Flux.just("something", "chain")
    .map(secret -> secret.replaceAll(".", ""))
    .subscribe(next -> System.out.println("Received: " + next));
```

第一个版本输出以下内容：

```none
Received: something
Received: chain
```

其他两个版本输出预期值，如下：

```none
Received: `````````
Received: ```
```

### C.3. 我的Mono zipWithorzipWhen从未被调用

考虑以下示例：

```java
myMethod.process("a") // this method returns Mono<Void>
        .zipWith(myMethod.process("b"), combinator) //this is never called
        .subscribe();
```

如果源Mono是要么empty或 a Mono<Void>(Mono<Void>对于所有意图和目的，a 都是空的)，则永远不会调用某些组合。

这是任何转换器的典型情况，例如zip静态方法或zipWith zipWhen运算符，它们(根据定义)需要来自每个源的元素来产生它们的输出。

因此，在来源上使用数据抑制运算符zip是有问题的。数据抑制运算符的示例包括then(), thenEmpty(Publisher<Void>), ignoreElements()and ignoreElement(), and when(Publisher…).

类似地，使用 aFunction<T,?>来调整其行为的运算符，例如flatMap，需要至少发出一个元素，Function以便 有机会应用。将这些应用于空(或<Void>)序列永远不会产生元素。

你可以使用.defaultIfEmpty(T)and用默认值或后备(分别).switchIfEmpty(Publisher<T>)替换空序列，这可以帮助避免其中一些情况。请注意，这不适用于 /来源，因为你只能切换到另一个，它仍然保证为空。以下示例使用：TPublisher<T>Flux<Void>Mono<Void>Publisher<Void>defaultIfEmpty

示例 28. 使用defaultIfEmpty之前zipWhen

```java
myMethod.emptySequenceForKey("a") // this method returns empty Mono<String>
        .defaultIfEmpty("") // this converts empty sequence to just the empty String
        .zipWhen(aString -> myMethod.process("b")) //this is called with the empty String
        .subscribe();
```

### C.4. 如何使用retryWhen来模拟retry(3)？

retryWhen运算符可能非常复杂。希望下面的代码片段可以通过尝试模拟一个更简单的代码来帮助你理解它的工作原理 retry(3)：

```java
AtomicInteger errorCount = new AtomicInteger();
Flux<String> flux =
		Flux.<String>error(new IllegalArgumentException())
				.doOnError(e -> errorCount.incrementAndGet())
				.retryWhen(Retry.from(companion -> 
						companion.map(rs -> { 
							if (rs.totalRetries() < 3) return rs.totalRetries(); 
							else throw Exceptions.propagate(rs.failure()); 
						})
				));
```

|      | 我们Retry通过适应Functionlambda 而不是提供具体类来定制   |
| ---- | ------------------------------------------------------------ |
|      | 同伴发出RetrySignal对象，这些对象承担到目前为止的重试次数和最后一次失败 |
|      | 为了允许重试三次，我们考虑索引 < 3 并返回一个要发出的值(这里我们简单地返回索引)。 |
|      | 为了终止错误的序列，我们在这三次重试后抛出原始异常。         |

### C.5. 如何使用retryWhen指数退避？

指数退避会产生重试尝试，每次尝试之间的延迟会增加，以免源系统过载并冒全面崩溃的风险。基本原理是，如果源产生错误，它已经处于不稳定状态，不太可能立即从中恢复。所以盲目地立即重试很可能会产生另一个错误并增加不稳定性。

由于3.3.4.RELEASE，Reactor 带有用于此类重试的构建器，可与Flux#retryWhen:一起使用Retry.backoff。

以下示例展示了构建器的简单使用，在重试尝试延迟之前和之后使用钩子记录消息。它延迟重试并增加每次尝试之间的延迟(伪代码：延迟 = 100ms  2^attempt_number_starting_at_zero)：

```java
AtomicInteger errorCount = new AtomicInteger();
Flux<String> flux =
Flux.<String>error(new IllegalStateException("boom"))
		.doOnError(e -> { 
			errorCount.incrementAndGet();
			System.out.println(e + " at " + LocalTime.now());
		})
		.retryWhen(Retry
				.backoff(3, Duration.ofMillis(100)).jitter(0d) 
				.doAfterRetry(rs -> System.out.println("retried at " + LocalTime.now() + ", attempt " + rs.totalRetries())) 
				.onRetryExhaustedThrow((spec, rs) -> rs.failure()) 
		);
```

|      | 我们将记录源发出的错误的时间并计算它们。                     |
| ---- | ------------------------------------------------------------ |
|      | 我们配置了最多 3 次尝试且无抖动的指数退避重试。              |
|      | 我们还记录重试发生的时间，以及重试次数(从 0 开始)。        |
|      | 默认情况下，Exceptions.retryExhausted将抛出异常，最后一个failure()作为原因。在这里，我们将其自定义为直接将原因发出为onError. |

订阅后，这将失败并在打印出以下内容后终止：

```
java.lang.IllegalStateException：00:00:00.0 的繁荣在 00: 
00:00.101 重试，尝试 0
java.lang.IllegalStateException：00:00:00.101 的繁荣在 00:00:00.304
重试，尝试 1
java.lang.IllegalStateException：00:00:00.304 的繁荣在 00:00:00.702
重试，尝试 2
java.lang.IllegalStateException：00:00:00.702 处的繁荣
```

|      | 大约 100 毫秒后第一次重试 |
| ---- | ------------------------- |
|      | 约 200 毫秒后第二次重试   |
|      | 约 400 毫秒后第三次重试   |

### C.6. 使用时如何确保线程关联publishOn()？

如[调度程序](https://projectreactor.io/docs/core/release/reference/#schedulers)中所述，publishOn()可用于切换执行上下文。运算符会影响其publishOn下方链中的其余运算符运行的线程上下文，直到新出现的publishOn. 所以摆放位置publishOn很重要。

考虑以下示例：

```java
Flux<Integer> source = Sinks.many().unicast().onBackpressureBuffer().asFlux();
source.publishOn(scheduler1)
	  .map(i -> transform(i))
	  .publishOn(scheduler2)
	  .doOnNext(i -> processNext(i))
	  .subscribe();
```

in的transform函数在map()的 worker 上运行， inscheduler1的processNext方法在 doOnNext()的 worker 上运行scheduler2。每个订阅都有自己的工作者，因此推送到相应订阅者的所有元素都发布在同一个Thread.

你可以使用单线程调度程序来确保链中不同阶段或不同订阅者的线程亲和性。

### C.7. 什么是上下文日志记录的好模式？(MDC)

大多数日志框架允许上下文日志，让用户存储反映在日志模式中的变量，通常通过Map称为 MDC(“映射诊断上下文”)的方式。这是Java中最常见的用法之一ThreadLocal，因此该模式假设被记录的代码与Thread.

在Java8 之前，这可能是一个安全的假设，但随着Java语言中函数式编程元素的出现，情况发生了一些变化……

让我们以一个命令式的 API 为例，它使用模板方法模式，然后切换到更实用的风格。在模板方法模式中，继承发挥了作用。现在在其更实用的方法中，传递更高阶的函数来定义算法的“步骤”。现在的事情是声明性的而不是命令性的，这使库可以自由地决定每个步骤应该在哪里运行。例如，知道底层算法的哪些步骤可以并行化，库可以使用 anExecutorService来并行执行一些步骤。

这种函数式 API 的一个具体示例是StreamJava 8 中引入的 API 及其parallel()风格。使用 MDC 并行记录Stream不是免费的午餐：需要确保在每个步骤中捕获并重新应用 MDC。

函数式风格支持这样的优化，因为每个步骤都是线程不可知的并且引用透明，但它可以打破 MDC 假设单个Thread. 确保所有阶段都可以访问任何类型的上下文信息的最惯用的方法是通过组合链传递该上下文。在Reactor的开发过程中，我们遇到了同样的一般问题，我们希望避免这种非常简单明了的方法。这就是Context引入 的原因：它通过执行链传播，只要Flux和Mono用作返回值，通过让阶段(操作员)窥视Context其下游阶段的 。因此，Reactor 没有使用 ，而是ThreadLocal提供了这个类似于地图的对象，该对象与Subscription而不是Thread.

既然我们已经确定 MDC“正常工作”并不是在声明式 API 中做出的最佳假设，那么我们如何执行与响应式流(、、和)中的事件相关的上下文onNext日志onError语句onComplete？

当人们想要以直接和明确的方式记录与这些信号相关的情况时，FAQ 的这个条目提供了一种可能的中间解决方案。确保事先阅读[将上下文添加到响应序列](https://projectreactor.io/docs/core/release/reference/#context)部分，尤其是必须如何在操作员链的底部进行写入，以便在其上方的操作员看到它。

要从 MDC 获取上下文信息，最简单的方法是使用一些样板代码Context将日志记录语句包装在运算符中。doOnEach此样板文件取决于你选择的日志框架/抽象以及你想要放入 MDC 的信息，因此它必须在你的代码库中。

以下是这样一个围绕单个 MDC 变量并专注于使用Java9 增强API记录onNext事件的辅助函数的示例：Optional

```java
public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
	return signal -> {
		if (!signal.isOnNext()) return; 
		Optional<String> toPutInMdc = signal.getContext().getOrEmpty("CONTEXT_KEY"); 

		toPutInMdc.ifPresentOrElse(tpim -> {
			try (MDC.MDCCloseable cMdc = MDC.putCloseable("MDC_KEY", tpim)) { 
				logStatement.accept(signal.get()); 
			}
		},
		() -> logStatement.accept(signal.get())); 
	};
}
```

|      | doOnEach信号包括onComplete和onError。在这个例子中，我们只对记录感兴趣onNext |
| ---- | ------------------------------------------------------------ |
|      | 我们将从Reactor中提取一个有趣的值[(](https://projectreactor.io/docs/core/release/reference/#context.api)Context参见[API](https://projectreactor.io/docs/core/release/reference/#context.api)部分)[Context](https://projectreactor.io/docs/core/release/reference/#context.api) |
|      | 我们MDCCloseable在这个例子中使用了 from SLF4J 2，允许 try-with-resource 语法在日志语句执行后自动清理 MDC |
|      | 调用者提供正确的日志语句作为Consumer<T>(onNext 值的消费者) |
|      | 如果未在其中设置预期的密钥，Context我们将使用在 MDC 中没有放置任何内容的替代路径 |

使用此样板代码可确保我们成为 MDC 的好公民：我们在执行日志记录语句之前设置一个密钥并在之后立即将其删除。对于后续的日志记录语句，没有污染 MDC 的风险。

当然，这是一个建议。你可能有兴趣从 中提取多个值Context或 在onError. 你可能想要为这些情况创建额外的辅助方法，或者制作一个使用额外 lambda 的单一方法来覆盖更多领域。

在任何情况下，前面的辅助方法的用法都可能类似于下面的响应式 Web 控制器：

```java
@GetMapping("/byPrice")
public Flux<Restaurant> byPrice(@RequestParam Double maxPrice, @RequestHeader(required = false, name = "X-UserId") String userId) {
	String apiId = userId == null ? "" : userId; 

	return restaurantService.byPrice(maxPrice))
			   .doOnEach(logOnNext(r -> LOG.debug("found restaurant {} for ${}", 
					r.getName(), r.getPricePerPerson())))
			   .contextWrite(Context.of("CONTEXT_KEY", apiId)); 
}
```

|      | 我们需要从请求头中获取上下文信息，将其放入Context          |
| ---- | ------------------------------------------------------------ |
|      | 在这里，我们将我们的辅助方法应用到Flux, 使用doOnEach. 请记住：操作员会看到Context在其下方定义的值。 |
|      | Context我们使用选择的 key将 header 中的值写入CONTEXT_KEY。 |

在此配置中，restaurantService可以在共享线程上发出其数据，但日志仍将X-UserId为每个请求引用正确的。

为了完整起见，我们还可以查看错误记录助手的样子：

```java
public static Consumer<Signal<?>> logOnError(Consumer<Throwable> errorLogStatement) {
	return signal -> {
		if (!signal.isOnError()) return;
		Optional<String> toPutInMdc = signal.getContext().getOrEmpty("CONTEXT_KEY");

		toPutInMdc.ifPresentOrElse(tpim -> {
			try (MDC.MDCCloseable cMdc = MDC.putCloseable("MDC_KEY", tpim)) {
				errorLogStatement.accept(signal.getThrowable());
			}
		},
		() -> errorLogStatement.accept(signal.getThrowable()));
	};
}
```

没有什么太大的改变，除了我们检查了它实际上Signal是一个onError，并且我们提供了错误(a Throwable)给日志语句 lambda。

在控制器中应用这个助手与我们之前所做的非常相似：

```java
@GetMapping("/byPrice")
public Flux<Restaurant> byPrice(@RequestParam Double maxPrice, @RequestHeader(required = false, name = "X-UserId") String userId) {
	String apiId = userId == null ? "" : userId;

	return restaurantService.byPrice(maxPrice))
			   .doOnEach(logOnNext(v -> LOG.info("found restaurant {}", v))
			   .doOnEach(logOnError(e -> LOG.error("error when searching restaurants", e)) 
			   .contextWrite(Context.of("CONTEXT_KEY", apiId));
}
```

|      | 如果restaurantService发出错误，它将在此处与 MDC 上下文一起记录 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/faq.adoc) 转至“[常见问题解答、最佳实践和“我如何……？](https://projectreactor.io/docs/core/release/reference/#faq) ”

## 附录 D：Reactor-Extra

该reactor-extra工件包含额外的运算符和实用程序，适用于reactor-core具有高级需求或孵化运算符的用户。

由于这是一个单独的工件，你需要将其显式添加到你的构建中。以下示例显示了如何在 Gradle 中执行此操作：

```groovy
dependencies {
     compile 'io.projectreactor:reactor-core'
     compile 'io.projectreactor.addons:reactor-extra' 
}
```

|      | 除了核心之外，还添加了响应堆额外的工件。有关使用 BOM 时为什么不需要指定版本、Maven 中的用法和其他详细信息的详细信息，请参阅[获取Reactor。](https://projectreactor.io/docs/core/release/reference/#getting) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### D.1. TupleUtils和功能接口

该reactor.function包包含补充Java8 Function、Predicate和Consumer接口的功能接口，用于三到八个值。

TupleUtils提供静态方法，充当这些功能接口的 lambda 与相应Tuple.

这使你可以轻松地使用 any 的独立部分Tuple，如以下示例所示：

```java
.map(tuple -> {
  String firstName = tuple.getT1();
  String lastName = tuple.getT2();
  String address = tuple.getT3();

  return new Customer(firstName, lastName, address);
});
```

你可以将前面的示例重写如下：

```java
.map(TupleUtils.function(Customer::new)); 
```

|      | (因为Customer构造函数符合Function3功能接口签名) |
| ---- | ----------------------------------------------------- |
|      |                                                       |

### D.2. 数学运算符MathFlux

该reactor.math软件包包含一个提供数学运算符的MathFlux专用版本Flux，包括max、min、sumInt、averageDouble等。

### D.3. 调度器

Reactor-extra 附带ForkJoinPoolScheduler(在reactor.scheduler.forkjoin包中)：它使用 JavaForkJoinPool来执行任务。

[建议编辑](https://github.com/reactor/reactor-core/edit/main/docs/asciidoc/apdx-reactorExtra.adoc) 到“ [Reactor-Extra](https://projectreactor.io/docs/core/release/reference/#reactor-extra) ”