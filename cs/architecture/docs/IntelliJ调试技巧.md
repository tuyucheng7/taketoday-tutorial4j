## 1. 概述

在本教程中，我们将研究一些高级 IntelliJ 调试工具。

假定调试基础知识已为人所知(如何开始调试、Step Into、Step Over 操作等)。如果没有，请参阅[本文](https://www.baeldung.com/intellij-basics)以获取更多详细信息。

## 2. 智能步入

有时会在一行源代码上调用多个方法，例如 doJob(getArg1(), getArg2())。如果我们调用Step Into action (F7)，调试器将按照 JVM 用于评估的顺序进入方法： getArg1 – getArg2 – doJob。

但是，我们可能希望跳过所有中间调用并直接进入目标方法。Smart Step Into action 允许这样做。

它默认绑定到Shift + F7，调用时看起来像这样：

[![技巧1](https://www.baeldung.com/wp-content/uploads/2018/12/trick1.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick1.png)

现在我们可以选择要继续的目标方法。另请注意，IntelliJ 始终将最外层的方法放在列表的顶部。这意味着我们可以通过按Shift + F7 |快速转到它。输入。

## 3.掉帧

我们可能意识到我们感兴趣的一些处理已经发生(例如当前方法参数的计算)。在这种情况下，可以删除当前的 JVM 堆栈帧以便重新处理它们。

考虑以下情况：

[![技巧2](https://www.baeldung.com/wp-content/uploads/2018/12/trick2.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick2.png)

假设我们有兴趣调试getArg1处理，所以我们删除当前帧(doJob方法)：

[![技巧3](https://www.baeldung.com/wp-content/uploads/2018/12/trick3.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick3.png)

现在我们在以前的方法中：

[![技巧4](https://www.baeldung.com/wp-content/uploads/2018/12/trick4.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick4.png)

但是，调用参数此时已经计算完毕，因此，我们还需要删除当前帧：

[![技巧5](https://www.baeldung.com/wp-content/uploads/2018/12/trick5.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick5.png)

现在我们可以通过调用Step Into重新运行处理。

## 4. 字段断点

有时非私有字段被其他类修改，不是通过 setter 而是直接修改(第三方库就是这种情况，我们无法控制源代码)。

在这种情况下，可能很难理解修改何时完成。IntelliJ 允许创建字段级断点来跟踪它。

它们像往常一样设置——左键单击字段行上的左侧编辑器间距。之后，可以打开断点属性(右键单击断点标记)并配置我们是否对该字段的读取、写入或两者感兴趣：

[![技巧6](https://www.baeldung.com/wp-content/uploads/2018/12/trick6.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick6.png)

## 5.记录断点

有时我们知道应用程序中存在竞争条件，但不知道它到底在哪里。确定它可能是一个挑战，尤其是在使用新代码时。

我们可以将调试语句添加到程序的源代码中。但是，第三方库没有这种能力。

IDE 在这里可以提供帮助——它允许设置断点，这些断点一旦命中就不会阻止执行，而是生成日志语句。

考虑以下示例：

```java
public static void main(String[] args) {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    int count = 0;
    for (int i = 0; i < 5; i++) {
        if (isInterested(random.nextInt(10))) {
            count++;
        }
    }
    System.out.printf("Found %d interested values%n", count);
}

private static boolean isInterested(int i) {
    return i % 2 == 0;
}复制
```

假设我们有兴趣记录实际的 isInterested 调用的参数。

让我们在目标方法中创建一个非阻塞断点(按住Shift键并左键单击左侧编辑器间距)。之后让我们打开它的属性(右键单击断点)并定义要记录的目标表达式：

[![技巧7](https://www.baeldung.com/wp-content/uploads/2018/12/trick7.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick7.png)

运行应用程序时(注意仍然需要使用 Debug 模式)，我们将看到输出：

```bash
isInterested(1)
isInterested(4)
isInterested(3)
isInterested(1)
isInterested(6)
Found 2 interested values复制
```

## 6.条件断点

我们可能会遇到这样一种情况，即同时从多个线程调用特定方法，我们需要调试仅针对特定参数的处理。

IntelliJ 允许创建断点，仅当满足用户定义的条件时才暂停执行。

下面是一个使用上述源代码的示例：

[![trcik8](https://www.baeldung.com/wp-content/uploads/2018/12/trcik8.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trcik8.png)

现在，仅当给定参数大于 3 时，调试器才会在断点处停止。

## 7. 物体标记

这是最强大和最不为人知的 IntelliJ 功能。这在本质上非常简单——我们可以将自定义标签附加到 JVM 对象上。

让我们看一下我们将用于演示它们的应用程序：

```java
public class Test {

    public static void main(String[] args) {
        Collection<Task> tasks = Arrays.asList(new Task(), new Task());
        tasks.forEach(task -> new Thread(task).start());
    }

    private static void mayBeAdd(Collection<Integer> holder) {
        int i = ThreadLocalRandom.current().nextInt(10);
        if (i % 3 == 0) {
            holder.add(i);
        }
    }

    private static class Task implements Runnable {

        private final Collection<Integer> holder = new ArrayList<>();

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                mayBeAdd(holder);
            }
        }
    }
}复制
```

### 7.1. 创建标记

当应用程序在断点处停止并且可以从堆栈帧访问目标时，可以标记对象。

选择它，按F11(标记对象操作)并定义目标名称：

[![技巧9](https://www.baeldung.com/wp-content/uploads/2018/12/trick9.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick9.png)

### 7.2. 查看标记

现在我们甚至可以在应用程序的其他部分看到我们的自定义对象标签：

[![技巧10](https://www.baeldung.com/wp-content/uploads/2018/12/trick10.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick10.png)

很酷的是，即使此时无法从堆栈帧访问标记的对象，我们仍然可以看到它的状态——打开一个Evaluate Expression对话框或添加一个新的 watch 并开始输入标记的名称。

IntelliJ 提供用_DebugLabel后缀来完成它：

[![技巧11](https://www.baeldung.com/wp-content/uploads/2018/12/trick11.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick11.png)

当我们评估它时，会显示目标对象的状态：

[![技巧 12](https://www.baeldung.com/wp-content/uploads/2018/12/trick12.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick12.png)

### 7.3. 标记为条件

也可以在断点条件下使用标记：

[![技巧13](https://www.baeldung.com/wp-content/uploads/2018/12/trick13.png)](https://www.baeldung.com/wp-content/uploads/2018/12/trick13.png)

## 八、总结

我们检查了一些在调试多线程应用程序时可以大大提高生产率的技术。

这通常是一项具有挑战性的任务，在这里我们不能低估工具帮助的重要性。