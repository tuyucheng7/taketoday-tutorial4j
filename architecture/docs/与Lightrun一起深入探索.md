## 1. 概述

在我们[之前的文章](https://www.baeldung.com/java-lightrun)中，我们介绍了[Lightrun——](https://www.baeldung.com/lightrun)一个开发人员可观察性平台。在本文中，我们将更深入地研究它提供的功能，我们如何才能最好地将它们用于我们的应用程序，以及我们可以从中获得什么。

## 2.快照

在上一篇文章中，我们简要介绍了什么是[快照](https://www.baeldung.com/lightrun-snapshots)以及它们可以为我们做什么。在这里，我们将更深入地了解它们是什么、我们如何最好地使用它们以及它们可以为我们做些什么。

快照类似于调试器断点。我们可以在应用程序的任何代码行上注册一个快照。每次触发时，它都会自动记录完整的堆栈跟踪和每个可见变量的值。就像普通的断点一样，这将包括局部变量、方法参数和类字段，并将在整个堆栈框架中这样做。

快照和调试器断点之间的主要区别是快照是非侵入式的。它们不会导致任何阻塞——无论是整个应用程序还是只是正在执行的线程。它们记录当前的执行状态，应用程序继续运行而不会以任何方式中断应用程序。

在我们查看细节时，传统的阻塞断点(例如在调试器中)会导致单个线程或整个应用程序停止暂停。Lightrun 允许我们在完全不影响实时应用程序的情况下完成所有这些工作。

### 2.1. 放置快照

快照直接从我们的代码编辑器放置到我们的应用程序中。在本文中，我们使用 IntelliJ IDEA，但也可以通过 Visual Studio Code 实现所有内容。我们需要确定要在应用程序中放置快照的位置。然后我们可以右键单击这行代码并从菜单中选择“Lightrun > Snapshot (Virtual Breakpoint)”：

[![放置快照](https://www.baeldung.com/wp-content/uploads/2022/09/place-snapshot.png)](https://www.baeldung.com/wp-content/uploads/2022/09/place-snapshot.png)

这样做将打开一个对话框，允许我们指定快照的详细信息：

[![创建快照](https://www.baeldung.com/wp-content/uploads/2022/09/create-snapshot.png)](https://www.baeldung.com/wp-content/uploads/2022/09/create-snapshot.png)

它的默认行为相对简单，但通常这是最有用的。它会：

-   对所选的确切行进行快照。
-   没有关于何时触发快照的条件。
-   没有多余的表情来记录快照。
-   仅在第一次触发快照时记录。
-   添加后一小时过期。

这意味着它会在下次执行这行代码时记录准确的执行状态，只要是在接下来的一个小时内。当我们诊断问题时，这通常是最有用的设置，因为我们希望控制录制的内容，而不会在录音中产生任何噪音。我们将在本文后面看到更多关于所有这些的信息。

完成此操作后，将在注册快照的代码行旁边放置一个蓝色相机图标。这表示我们的快照已经放置成功，触发时会记录：

[![已添加快照](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-added.png)](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-added.png)

当快照被触发时，我们将自动在我们的编辑器中查看详细信息。这看起来和功能几乎与 IntelliJ 断点面板完全相同，因为它被设计用于相同的目的：

[![快照触发](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-triggered.png)](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-triggered.png)

在这里我们可以立即看到执行的完整堆栈跟踪，以及我们可以访问的变量。这包括变量this，在其中我们可以看到当前类实例的字段。我们还可以根据需要深入研究这些方法，并以与传统调试器完全相同的方式单击堆栈框架中的其他方法。

### 2.2. 条件快照

在某些情况下，我们希望仅在满足某些条件时才记录快照。例如，仅当当前用户是特定用户名时。

Lightrun 快照允许我们在设置快照时指定一个条件。这与条件断点在我们的调试器中的工作方式非常相似。我们的条件被指定为 Java 表达式，其计算结果为true或false。这可以访问在触发快照时可见的任何内容，这意味着任何局部变量、参数、类字段或其他任何内容。

例如，假设我们有一个带有参数id的方法。我们希望在调用时记录快照，但前提是提供的 ID 是特定值。我们可以设置一个完全根据需要触发的条件：

[![条件快照](https://www.baeldung.com/wp-content/uploads/2022/09/conditional-snapshot.png)](https://www.baeldung.com/wp-content/uploads/2022/09/conditional-snapshot.png)

这意味着快照只会在使用我们的特定测试值调用时触发，但该服务的任何其他实时使用将被忽略，除非它们碰巧使用相同的值。这有助于确保我们在快照窗格中看到的正是我们想要的，没有任何额外的噪音使其混乱并使我们的诊断更加困难。

### 2.3. 附加表达式

在某些情况下，我们可能有额外的值要记录为快照的一部分。

这些可能是其他值的计算，只是为了让生活更轻松——例如，钻取嵌套值以更容易地显示它们。这些也可能是获取值的调用，否则不会被记录——例如，从[RequestContextHolder](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/context/request/RequestContextHolder.html)或[SecurityContextHolder](https://www.baeldung.com/get-user-in-spring-security#interface)等静态变量。它们甚至可以是对我们可以看到的任何值的方法调用，并记录这些方法的结果。

通过在快照对话框中输入要记录的表达式，表达式的添加方式与条件非常相似：

[![快照表达式](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-expressions.png)](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-expressions.png)

我们可以根据需要在单个快照中添加任意多个表达式，并且在触发快照时将计算并记录所有这些表达式。

然后，这些值作为“变量”窗格的一部分出现在记录的快照中，并使用不同的图标表示它们是手动添加的表达式而不是自动检测到的变量：

[![快照变量](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-variables.png)](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-variables.png)

### 2.4. 记录多张快照

在某些情况下，我们可能希望从同一个地方记录多个快照。例如，我们可能希望通过系统运行几个略有不同的请求，并能够比较它们的快照以识别差异。

Lightrun Snapshots 默认情况下只会记录一个快照，但我们可以通过在快照对话框中输入最大命中数来将其配置为记录任意数量的快照：

[![多重快照](https://www.baeldung.com/wp-content/uploads/2022/09/multiple-snapshot.png)](https://www.baeldung.com/wp-content/uploads/2022/09/multiple-snapshot.png)

这样做将记录这些执行次数的快照，并在我们的编辑器中提供给我们：

[![查看多个快照](https://www.baeldung.com/wp-content/uploads/2022/09/view-multiples-snapshots.png)](https://www.baeldung.com/wp-content/uploads/2022/09/view-multiples-snapshots.png)

现在我们有多个快照可以使用，我们需要知道哪些是哪些。通过单击“快照”选项卡旁边的“i”图标，我们将获得有关此确切快照的信息对话框：

[![快照信息](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-info.png)](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-info.png)

在这里我们可以看到记录快照的服务器实例和记录时间。我们现在可以根据需要记录尽可能多的快照，并确定哪些快照是哪些快照，以便我们可以更好地诊断正在发生的事情。

### 2.5. 自动过期快照

记录快照确实会对我们的应用程序产生很小的性能成本。它们还会导致数据从我们的应用程序传输到 Lightrun 服务器，这可能会产生费用。这意味着虽然快照对于诊断问题非常有用，但我们希望确保它们不会停留超过所需的时间。Lightrun 通过自动使快照过期来为我们解决这个问题，这样它们只会影响我们的应用程序以满足我们的确切需求，而不会影响更多。

默认情况下，快照将在 1 小时后自动禁用。如果我们想以最小的影响自己进行一些集中测试，我们可以将其设置为非常短的时间。或者，我们可以将其设置为很长的时间段，例如，以捕获在一夜之间、周末甚至更长时间内发生的任何特定问题。

我们可以从“创建快照”对话框的“高级”部分调整快照处于活动状态的持续时间。这为我们提供了一个额外的到期时间选项，让我们以小时、分钟和秒为单位指定快照的活动时间：

[![快照到期](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-expiry.png)](https://www.baeldung.com/wp-content/uploads/2022/09/snapshot-expiry.png)

在此时间之后，快照将保持存在，以便记录的快照仍然可用。然而，它会停止记录任何东西——即使我们还没有达到最大命中数。如果我们不更改它，则默认值为 1 小时。这段时间过去后，快照的相机图标变为红色，表示它不再处于活动状态：

[![停止录音](https://www.baeldung.com/wp-content/uploads/2022/09/stop-recording.png)](https://www.baeldung.com/wp-content/uploads/2022/09/stop-recording.png)

请注意，快照保留在我们的系统中，否则记录的数据将不可用。但是，除非重新启用，否则它不会再记录任何数据。

## 3.日志

Lightrun 提供的另一个功能是能够动态地将[日志记录语句](https://www.baeldung.com/lightrun-logs)添加到我们的应用程序中，而无需更改或重新启动任何东西。

日志在配置方式上与快照类似，但用途不同。快照通过记录线程在触发时的确切状态来工作。相反，日志会将所需信息写入日志流。

这意味着许多日志消息——无论是内置到应用程序中还是由 Lightrun 动态添加——将在日志流中混合在一起，并提供正在发生的事情的更大画面。我们将看到来自我们应用程序的日志消息和 Lightrun 添加的任何日志合并到同一个流中，让我们全面了解正在发生的事情。

### 3.1. 添加动态日志

使用 Lightrun 添加动态日志的方式与添加快照的方式非常相似。我们右键单击我们要在前面添加日志语句的行，然后从菜单中选择“Lightrun > Log”：

[![动态日志](https://www.baeldung.com/wp-content/uploads/2022/09/dynamic-log.png)](https://www.baeldung.com/wp-content/uploads/2022/09/dynamic-log.png)

然后，这会为我们提供一个对话框来配置动态日志语句，然后将其添加到我们正在运行的应用程序中：

[![创建日志](https://www.baeldung.com/wp-content/uploads/2022/09/create-log.png)](https://www.baeldung.com/wp-content/uploads/2022/09/create-log.png)

这使我们能够指定将输出的日志消息——其中可以包含动态表达式作为消息的一部分。我们还可以指定触发日志消息所需的条件，其方式与快照条件的工作方式完全相同。

默认情况下，这些日志消息将在 1 小时后过期，但也可以像快照一样通过单击“高级”按钮进行更改。

日志消息也有一个日志记录级别，默认为 INFO，但我们可以根据需要将它们更改为 DEBUG、WARN 或 ERROR。

一旦我们添加了日志语句，编辑器将在代码视图中指出这一点，以显示日志语句的位置和作用：

[![日志语句](https://www.baeldung.com/wp-content/uploads/2022/09/log-statement.png)](https://www.baeldung.com/wp-content/uploads/2022/09/log-statement.png)

### 3.2. 查看日志

默认情况下，我们的动态日志消息是使用 Java Util Logging 写出的。在这种情况下，我们能够看到它们与应用程序生成的任何其他日志消息交织在一起，这可以提供更多信息：

[![查看日志](https://www.baeldung.com/wp-content/uploads/2022/09/view-logs.png)](https://www.baeldung.com/wp-content/uploads/2022/09/view-logs.png)

也可以将日志消息发送到我们的编辑器以在本地查看。这些可以在 Lightrun 控制台中看到，类似于我们看到快照的方式。如果我们想在不向输出日志文件添加额外噪音的情况下向系统添加日志记录，这将非常有用，尤其是当其他团队成员或其他系统正在使用这些日志时：

[![lightrun 控制台日志](https://www.baeldung.com/wp-content/uploads/2022/09/lightrun-console-logs.png)](https://www.baeldung.com/wp-content/uploads/2022/09/lightrun-console-logs.png)

我们可以通过打开侧栏中的代理菜单并选择日志管道来更改日志消息输出的位置：

[![日志管道](https://www.baeldung.com/wp-content/uploads/2022/09/log-piping.png)](https://www.baeldung.com/wp-content/uploads/2022/09/log-piping.png)

从这里我们可以选择应用程序——这意味着写入应用程序的已配置 Java Util 日志记录设置，插件——这意味着写入我们编辑器中活动的 Lightrun 插件，或两者。请注意，这是针对整个 Lightrun 代理而不是针对单个日志消息完成的。

由于 Lightrun 代理的工作方式，Java Util Logging 配置不是应用程序的标准配置。相反，在写入 Java Util Logging 时，需要一些[Lightrun 代理标志来配置 Lightrun 动态记录器的目标和输出格式。](https://www.baeldung.com/lightrun-docs-jvm)

### 3.3. 记录表达式

记录简单的字符串已经很有用了。但是，从应用程序中记录值要有用得多。就像我们的快照可以包含自定义表达式一样，我们可以对日志执行此操作。

当我们对日志执行此操作时，我们将表达式直接添加到日志消息中。这是通过将表达式括在花括号中来完成的：

```plaintext
Searching tasks: status={status}, createdBy={createdBy}复制
```

当我们这样做时，这些表达式中的任何一个都会在生成日志语句时自动展开：

[![记录表达式](https://www.baeldung.com/wp-content/uploads/2022/09/logging-expressions.png)](https://www.baeldung.com/wp-content/uploads/2022/09/logging-expressions.png)

这些表达式可以是在生成日志语句时可以确定的任何内容，其方式与快照完全相同。

这些表达式有时会占用大量 CPU 时间来计算。如果发生这种情况，Lightrun 可能会自动暂停特定日志，以免它们干扰应用程序的运行。因此，建议使日志记录表达式尽可能简单。

## 4.指标

我们可以使用 Lightrun 执行的最后一个操作是记录有关我们应用程序的一些[指标](https://www.baeldung.com/lightrun-metrics)。这使我们能够查看应用程序的使用细节——例如，某些事情发生的频率或需要多长时间。

与快照和日志一样，通过右键单击相应的代码行并选择“Lightrun > Metrics”来添加指标：

[![轻量级指标](https://www.baeldung.com/wp-content/uploads/2022/09/lightrun-metrics.png)](https://www.baeldung.com/wp-content/uploads/2022/09/lightrun-metrics.png)

我们可以立即看到这略有不同——我们可以添加不同类型的指标：

-   计数器——这记录了一行代码被执行的次数的简单计数。
-   时间测量——这记录了两行代码之间的时间。
-   方法持续时间——这记录了进入和退出方法之间花费的时间。
-   自定义指标 – 这使用自定义表达式根据代码中可用的值生成指标。

在每种情况下，我们都会获得用于创建指标的标准 Lightrun 对话框。这让我们可以配置指标，包括为指标添加名称、触发条件以及停止工作的到期时间——与我们为快照和日志所做的完全相同。

这些指标默认输出到日志记录过程，但如果需要，也可以集成到 StatsD、Prometheus 和其他工具中。

### 4.1. 柜台

计数器是对某些代码执行次数的简单度量。每次到达我们的代码行时，计数器都会递增 1，然后我们可以看到这种情况发生的频率。

通过从 Lightrun 菜单中选择 Counter 然后填写对话框来添加计数器：

[![光跑计数器](https://www.baeldung.com/wp-content/uploads/2022/09/lightrun-counter.png)](https://www.baeldung.com/wp-content/uploads/2022/09/lightrun-counter.png)

其中大部分是相当标准的。唯一不寻常的一点是“名称”字段——我们需要给每个计数器一个唯一的名称，以便我们可以跟踪它们。

特别是，这里的一个强大功能是我们可以设置计数器 - 与所有指标一样 - 具有附加条件。这使我们能够仅在满足其他条件时才计算到达特定代码行的次数，例如仅针对特定用户或影响特定记录。

添加计数器不会立即执行任何操作。但是，一旦它第一次被触发，它将开始以与日志输出方式类似的方式报告度量值——基于管道设置的日志输出。

我们的编辑器每 10 秒显示一次指标值：

[![日志指标](https://www.baeldung.com/wp-content/uploads/2022/09/log-metrics.png)](https://www.baeldung.com/wp-content/uploads/2022/09/log-metrics.png)

而日志输出每秒显示一次指标：

[![日志输出指标](https://www.baeldung.com/wp-content/uploads/2022/09/log-output-metrics.png)](https://www.baeldung.com/wp-content/uploads/2022/09/log-output-metrics.png)

此输出由与日志输出完全相同的设置控制，并将我们的指标与日志交织在一起，以更好地了解正在发生的事情。这让我们可以随着时间的推移跟踪指标的变化情况，以查看触发代码的速率。

### 4.2. 持续时间

计数器用于衡量一行代码被执行的次数，而持续时间用于衡量代码运行了多长时间。这些也称为“TicToc”指标 - Tic 开始记录，Toc 停止记录，类似于时钟的噪音。

创建持续时间时，我们需要准确配置要测量的代码段。这是通过指定开始和停止记录的行来完成的——在打开对话框之前选择一段代码，或者在对话框中输入行号：

[![持续时间](https://www.baeldung.com/wp-content/uploads/2022/09/time-duration.png)](https://www.baeldung.com/wp-content/uploads/2022/09/time-duration.png)

除此之外，创建持续时间与创建计数器相同。

一旦创建，这些指标将立即开始输出，而不是等待它们第一次被触发。这些输出是插件和应用程序的标准输出，与计数器完全相同，并将向我们显示代码部分的运行频率以及代码运行的最快、最慢和平均时间：

[![持续时间指标](https://www.baeldung.com/wp-content/uploads/2022/09/time-duration-metrics.png)](https://www.baeldung.com/wp-content/uploads/2022/09/time-duration-metrics.png)

### 4.3. 方法持续时间

Method Duration 指标与 Time Duration 基本相同，但我们没有指定开始和结束行，而是指定了完整的方法：

[![方法持续时间](https://www.baeldung.com/wp-content/uploads/2022/09/method-duration.png)](https://www.baeldung.com/wp-content/uploads/2022/09/method-duration.png)

添加后，它的功能与覆盖整个方法主体的持续时间指标完全相同。开始时间被认为是进入方法的时间，结束时间是离开方法的时间，无论是返回还是异常：

[![方法持续时间日志](https://www.baeldung.com/wp-content/uploads/2022/09/method-duration-logs.png)](https://www.baeldung.com/wp-content/uploads/2022/09/method-duration-logs.png)

我们从这里的输出可以看出，这些实际上也是“TicToc Log”条目，只是Lightrun根据方法本身而不是根据代码行自动为我们确定了起点和终点。

### 4.4. 自定义指标

我们的最终指标只是一个自定义指标。这让我们可以以某种方式聚合代码中存在的数字，无论这些数字是什么。例如，我们可能想要计算某些搜索结果中返回的记录数。

创建此类指标时，我们需要指定一个表达式。这个表达式返回我们的指标将聚合的数字，和其他地方一样，它可以是我们代码中此时可以计算的任何表达式：

[![自定义指标](https://www.baeldung.com/wp-content/uploads/2022/09/custom-metrics.png)](https://www.baeldung.com/wp-content/uploads/2022/09/custom-metrics.png)

当这些指标输出时，它会向我们显示它被触发的次数以及表达式的最大值、最小值和平均值：

[![自定义指标日志](https://www.baeldung.com/wp-content/uploads/2022/09/custom-metrics-logs.png)](https://www.baeldung.com/wp-content/uploads/2022/09/custom-metrics-logs.png)

## 5.总结

在这里，我们详细介绍了 Lightrun 可以让我们更深入地了解我们的应用程序并更好地了解它们的工作方式的主要方式。

为什么不在你的下一个应用程序中使用它，以更好地了解正在发生的事情，甚至帮助诊断可能正在发生的任何问题？