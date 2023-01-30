## 1. 概述

在操作系统中，[内存管理](https://www.baeldung.com/java-memory-management-interview-questions)是一个至关重要的话题。它提供了动态控制和协调计算机内存的方法。内存管理允许在程序请求时分配一部分内存。当程序不再使用时，它还会自动从程序中释放内存。

内存管理中使用了多种技术。一种这样的方法是[分页](https://www.baeldung.com/rest-api-pagination-in-spring)。在分页中，页面替换算法起着重要的作用，当新页面进来时，它决定将哪个页面保留在主内存中。

先进先出 (FIFO) 是页面替换算法中最简单的。在本教程中，我们将全面介绍 FIFO。

## 2.总体思路

让我们首先讨论页面替换算法的一些背景。

页面替换算法属于分页技术。操作系统中的分页主要用于[虚拟内存管理](https://en.wikipedia.org/wiki/Virtual_memory)。分页是一种允许计算机从辅助存储器(例如 HDD 或磁鼓存储器)检索数据并将其存储到主存储器 (RAM) 中的方法。从辅助内存中提取的数据在内存管理中称为[页面。](https://en.wikipedia.org/wiki/Page_(computer_memory))

当有新的页面请求，并且主存中没有足够的空间来分配新页面时，使用像 FIFO 这样的页面替换算法。

因此，页面替换算法决定它应该替换哪个页面，以便它可以为新页面分配内存。页面替换的步骤可以用流程图总结：

![先进先出](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/FIFO.png)

先进先出 (FIFO) 算法对这个问题有一个简单的方法。我们通过使用主内存中的队列来跟踪所有页面。一旦有页面进来，我们就会将其放入队列并继续。这样，最旧的页面将始终位于队列的第一位。

现在当一个新页面进来并且内存中没有空间时，我们删除队列中的第一个页面，它也是最旧的页面。它重复这个过程，直到操作系统在系统中有页面流。

## 3.页面错误

页面错误是页面替换算法中的另一个重要概念。当程序请求的页面不在主内存中时，就会发生页面错误。

页面错误会为操作系统生成警报。然后操作系统将页面从辅助或虚拟内存检索到主内存。所有进程都在后台运行。

通常，这需要几毫秒；尽管如此，它仍然对操作系统的性能产生重大影响。大量页面错误会降低整个系统的速度。尽管页面错误在现代操作系统中很常见，但是大量的页面错误可能会导致程序崩溃或意外终止。

页面替换算法的有效性是用它产生的页面错误数来衡量的。页面替换算法越有效，该算法产生的页面错误次数就越少。

## 4. FIFO 伪代码

现在我们知道了 FIFO 页面替换算法背后的理论，让我们看一下伪代码：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dbcb178160c7c5f3cc5dff1ce288a146_l3.svg)

这里![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)表示系统中当前页面的集合。我们创建了一个队列并将其命名为![Q页面](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9791fed3b7c6652e81de078e10f74ff4_l3.svg)以 FIFO 方式存储传入页面。最初，我们将页面错误数设置![PF](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77bdbcd2f55c0ff829ff785fd5a79cad_l3.svg)为零。

现在当一个新页面进来时，我们检查 set 的容量![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。在这里，我们有三种情况。第一个是集合可以存储新的传入页面，而该页面尚未出现在集合中。在这种情况下，我们通常将新页面存储在 set 中![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。

第二种情况是集合可以存储新的传入页面，但该页面已经存在于集合中。在这种情况下，我们会将其标记为[页面命中](https://t4tutorials.com/difference-between-page-fault-page-hit-and-page-miss-examples-diagram/)并且不会增加变量的计数![PF](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77bdbcd2f55c0ff829ff785fd5a79cad_l3.svg)。

第三种情况发生在集合 ![mathbf{S}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cb469d8d370c1f9c048c115200346fae_l3.svg) 已满时 。这里我们需要执行 FIFO 来从队列中移除页面。

最后，当所有页面都被存储或从集合中删除时![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)，算法返回页面错误的总数![PF](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77bdbcd2f55c0ff829ff785fd5a79cad_l3.svg)并终止。

## 5. 一个例子

在本节中，我们将举一个例子并在其上运行 FIFO 页面替换算法。

我们正在使用![数学{12}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-91b154b72a4b3bddaf9c8e9ebb2d119a_l3.svg)页面引用字符串：![4 , 7, 6, 1, 7, 6, 1, 2, 7, 2, 7, 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4f24891cb0bb638bb4c9435cebae8dc5_l3.svg)在我们的示例中。此外，我们正在考虑队列的容量是![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)。最初，我们的队列是空的。现在让我们用页面填充队列并应用 FIFO 算法：

![先进先出3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/fifo3.png)

这里每个表顶部的数字代表新传入页面的参考编号。在执行此过程时，我们还会计算页面错误的数量。

在我们的示例中，使用 FIFO 页面替换算法发生的页面错误总数为![数学{6}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d9b051dc69b64a5c932db6cb6b9d651f_l3.svg).

## 六、优缺点

FIFO 页面替换算法的主要优点是它的简单性。它易于理解和实施。它还使用队列数据结构。队列中的操作数量有限，使实现变得简单。

现在让我们谈谈一些缺点。当传入的页面数量很大时，它可能无法提供出色的性能。

当我们增加队列中存储页面的帧数或容量时，它应该给我们带来更少的页面错误。有时 FIFO 可能会表现异常，并且可能会增加页面错误的数量。FIFO 的这种行为称为[Belady 异常](https://en.wikipedia.org/wiki/Bélády's_anomaly)。

在 FIFO 中，系统应跟踪所有帧。有时它会导致进程执行缓慢。

## 七、总结

在本教程中，我们详细讨论了 FIFO 页面替换。

我们已经讨论了总体思路并通过示例演示了该算法。

FIFO 页面替换算法绝对不是实际使用的最佳页面替换算法。当传入页面的数量较少并且用户正在寻找一种简单的方法时，FIFO 可能是一个合理的选择。