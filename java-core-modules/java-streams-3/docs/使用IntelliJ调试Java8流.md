## 1. 概述

自从引入Java8 以来，很多人开始使用(新的)流功能。当然，有时我们的流操作不会按预期工作。

[IntelliJ](https://www.baeldung.com/intellij-basics)除了其[正常的调试选项](https://www.baeldung.com/intellij-debugging-tricks)外，还有一个专用的流调试功能。在这个简短的教程中，我们将探索这个很棒的功能。

## 2. 流跟踪对话框

让我们首先展示如何打开 Stream Trace 对话框。在调试窗口的工具栏中，有一个Trace Current Stream Chain 图标，只有当我们的应用程序在流 API 调用中的断点处暂停时才会启用：

[![调试流图标](https://www.baeldung.com/wp-content/uploads/2019/11/debug-stream-icon.png)](https://www.baeldung.com/wp-content/uploads/2019/11/debug-stream-icon.png)

单击该图标将打开 Stream Trace 对话框。

该对话框有两种模式。我们将在第一个示例中查看平面模式。并且，在第二个示例中，我们将展示默认模式，即拆分模式。

## 3.例子

现在我们已经在 IntelliJ 中引入了流调试功能，是时候处理一些代码示例了。

### 3.1. 排序流的基本示例

让我们从一个简单的代码片段开始，以习惯 Stream Trace 对话框：

```java
int[] listOutputSorted = IntStream.of(-3, 10, -4, 1, 3)
  .sorted()
  .toArray();
```

最初。我们有一个 unordered int流。接下来，我们对该流进行排序并将其转换为数组。

当我们在 Flat Mode 中查看 Stream Trace 时，它向我们展示了发生的步骤的概览：

[![流跟踪对话框平面 1](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-flat-1-e1572447263959.png)](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-flat-1-e1572447263959.png)

在最左边，我们看到了初始流。它按照我们编写它们的顺序包含int 。

第一组箭头向我们展示了排序后所有元素的新位置。在最右边，我们看到了我们的输出。所有项目都按排序顺序出现在那里。

现在我们已经了解了基础知识，是时候举一个更复杂的例子了。

### 3.2. 使用flatMap和过滤器的示例

下一个示例使用[flatMap](https://www.baeldung.com/java-difference-map-and-flatmap)。 例如，Stream.flatMap可以帮助我们将[Optional](https://www.baeldung.com/java-optional)列表转换为普通列表。在下一个示例中，我们从一个Optional Customer列表开始。然后我们将其映射到Customer的列表并应用一些过滤：

```java
List<Optional<Customer>> customers = Arrays.asList(
    Optional.of(new Customer("John P.", 15)),
    Optional.of(new Customer("Sarah M.", 78)),
    Optional.empty(),
    Optional.of(new Customer("Mary T.", 20)),
    Optional.empty(),
    Optional.of(new Customer("Florian G.", 89)),
    Optional.empty()
);

long numberOf65PlusCustomers = customers
  .stream()
  .flatMap(c -> c
    .map(Stream::of)
    .orElseGet(Stream::empty))
  .mapToInt(Customer::getAge)
  .filter(c -> c > 65)
  .count();
```

接下来，让我们在拆分模式下查看 Stream Trace，这使我们可以更好地了解此流。

在左侧，我们看到了输入流。接下来，我们看到可选客户流到实际现有客户流的平面映射：

[![流跟踪对话框平面图](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-flatmap-e1572447285933.png)](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-flatmap-e1572447285933.png)

之后，我们将客户流映射到他们的年龄：

[![流跟踪对话框映射到 int](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-map-to-int-e1572447305625.png)](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-map-to-int-e1572447305625.png)

下一步将我们的年龄流过滤为大于 65 岁的年龄流：

[![流跟踪对话框过滤器](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-filter-e1572447334818.png)](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-filter-e1572447334818.png)

最后，我们计算年龄流中的项目数：

[![流跟踪对话计数](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-count-e1572447345565.png)](https://www.baeldung.com/wp-content/uploads/2019/11/stream-trace-dialog-count-e1572447345565.png)

## 4. 注意事项

在上面的示例中，我们已经看到了 Stream Trace 对话框提供的一些可能性。但是，有一些重要的细节需要注意。它们中的大多数是流工作方式的直接结果。

首先，流总是需要执行终端操作。这在使用流跟踪对话框时没有什么不同。此外，我们必须了解不消耗整个流的操作——例如，anyMatch。在这种情况下，它不会显示所有元素——仅显示已处理的元素。

其次，请注意流将被消耗。如果我们将Stream与其操作分开声明，我们可能会遇到错误[“Stream 已经被操作或关闭”](https://www.baeldung.com/java-stream-operated-upon-or-closed-exception)。我们可以通过将流的声明与其用法结合起来来防止此错误。

## 5.总结

在本快速教程中，我们了解了如何使用 IntelliJ 的 Stream Trace 对话框。

首先，我们查看了一个显示排序和收集的简单案例。然后，我们研究了一个更复杂的场景，涉及平面映射、映射、过滤和计数。

最后，我们研究了在使用流调试功能时可能会遇到的一些注意事项。