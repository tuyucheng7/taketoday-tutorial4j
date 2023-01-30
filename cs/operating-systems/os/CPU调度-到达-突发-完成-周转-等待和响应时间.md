## 1. 概述

[CPU 调度](https://www.baeldung.com/cs/scheduling-types)是操作系统的一项重要功能，它控制在计算机上运行的众多任务之间共享处理器时间。因此，必须确保执行流程的效率和公平性。它还确保系统能够满足其用户的性能和响应要求。

CPU 调度包括许多基本概念。

在本教程中，我们将讨论 CPU 调度的核心概念，包括到达、突发、完成、周转、等待和响应时间。通过了解这些概念以及它们如何在不同的调度算法中使用，我们可以更深入地了解操作系统如何管理计算机上进程的执行。

## 2. CPU调度中的关键概念

### 2.1. 到达时间

在CPU调度中，到达时间是指[进程](https://www.baeldung.com/cs/process-scheduling)进入就绪队列等待CPU执行的时刻。换句话说，它是进程有资格进行调度的时间点。

许多 CPU 调度算法在选择下一个进程执行时会考虑到达时间。例如，调度程序可能会优先于到达时间较晚的进程而不是到达时间较晚的进程，以减少就绪队列中进程的等待时间。因此，它可以帮助确保流程的有效执行。

### 2.2. 突发时间

突发时间，也称为“执行时间”。它是进程完成其执行所需的 CPU 时间量。它是进程执行特定任务或作业单元所需的处理时间。任务的复杂性、代码效率和系统资源等因素决定了进程的突发时间。

突发时间也是 CPU 调度中的一个重要因素。例如，一个调度程序可能更喜欢突发持续时间较短的进程，而不是突发时间较长的进程。这将减少进程在 CPU 上运行的时间。因此，它可以帮助确保系统可以最佳地利用处理器的资源。

### 2.3. 完成时间

完成时间是指进程完成执行并且不再由 CPU 处理的时间。它是到达时间、等待时间和突发时间的总和。

完成时间是 CPU 调度中的一个重要指标，因为它可以帮助确定调度算法的效率。它还有助于确定进程的等待时间。

例如，一种调度算法始终能够缩短进程的完成时间，被认为比始终能够导致更长的完成时间的调度算法更有效。

### 2.4. 周转时间

进程到达和完成之间经过的时间称为周转时间。即进程完成执行并离开系统所花费的时间。

```plaintext
Turnaround Time = Completion Time – Arrival Time
```

定期为进程产生更短周转时间的调度算法被认为比周转时间更长的算法更有效。

### 2.5. 等待的时间

这是进程在开始执行之前在就绪队列中的持续时间。它有助于评估调度算法的效率。例如，一种持续减少进程等待时间的调度方法被认为比定期导致更长等待时间的方法更有效。

```plaintext
Waiting Time = Turnaround Time – Burst Time
```

此外，它有助于衡量调度算法的效率。此外，它还有助于确定系统对用户查询的感知响应能力。长时间的等待会导致负面的用户体验。这是因为消费者可能认为系统对他们的请求反应缓慢。

### 2.6. 响应时间

响应时间是 CPU 响应进程发出的请求所花费的时间。它是进程到达和它第一次运行之间的持续时间。 它是 CPU 调度中的一个基本参数，因为它可以帮助确定系统对用户请求的感知响应能力。

```plaintext
Response Time = Time it Started Executing – Arrival Time
```

在就绪队列中等待的进程数、进程的优先级以及调度算法的特性都是可能影响响应时间的变量。例如，优先处理突发时间较短的进程的调度算法可能会导致这些进程的响应时间更快。

## 3. 举例说明

为了进一步说明这个概念以及它们是如何计算的，让我们考虑一个包含四个进程的示例，如表中所示，包括到达时间和突发时间。使用[非抢占式](https://www.baeldung.com/cs/scheduling-types) [最短作业优先](https://www.baeldung.com/cs/process-scheduling)算法，我们可以看到流程是如何完成的：

![documentclass{} setlength{arrayrulewidth}{0.1mm} setlength{tabcolsep}{10pt} renewcommand{arraystretch}{2.0} begin{document} begin{center} begin{tabular}{ p{ 4厘米} |  p{4cm} |p{4cm}} hline rowcolor[RGB]{85,123,220} textbf{进程} & textbf{到达时间} &textbf{连发时间} [1pt] hline P1 & 3 & 3 [0.5pt] hline rowcolor[RGB]{240, 245, 253} P2 & 6 & 3 [0.5pt] hline P3 & 0 & 4 [0.5pt] hline rowcolor[ RGB]{240, 245, 253} P4 & 2 & 5 [0.5pt] hline end{tabular} end{center} end{document}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2a6d30e6d3ad628923f796e30739e6f4_l3.svg)

time=0时： P3到达并开始执行，无需等待。

让我们注意 P3 在 time=0 时第一次被注意到：

![SJF 步骤 0 图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_0.png)

time=2： P4到达，P3继续执行。

因此，P4 在队列中等待：

![SJF 第一步图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_1.png)

时间=3： P1到达，P3继续执行：

![SJF 步骤 2 图](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_2.png)

time=4： P3执行完毕。比较 P4 和 P1 的突发时间。因此 P1 开始执行：

![SJF 第 3 步图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_3.png)

此时，我们可以计算 P3 的周转时间、等待时间和响应时间：

```plaintext
Completion Time (P3) = 4
Turnaround Time (P3) = 4 - 0 = 4
Wait time (P3) = 4 - 4 = 0
Response Time (P3) = 0 - 0 = 0
```

time=6时： P2到达，P1还在执行：

![SJF 第 4 步图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_4.png)

时间=7： P1执行完毕。比较 P4 和 P2 的突发时间。因此，P2 开始执行：

![SJF 第 5 步图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_5.png)

现在，我们可以对 P1 进行计算：

```plaintext
Completion Time (P1) = 7 
Turnaround Time (P1) = 7 - 3 = 4 
Wait time (P1) = 4 - 3 = 1 
Response Time (P1) = 4 - 3 = 1
```

time=10时： P2执行完毕，等待队列中只有P4。因此，P4 开始执行：

![SJF 第 6 步图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_6.png)

此时，我们可以对P2进行计算：

```plaintext
Completion Time (P2) = 10 
Turnaround Time (P2) = 10 - 6 = 4 
Wait Time (P2) = 4 - 3 = 1 
Response Time (P2) = 7 - 6 = 1
```

在时间=15： P4 完成执行。因此，我们有甘特图：

![SJF 第 7 步图表](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/SJF_step_7.png)

我们现在可以对 P4 进行计算：

```plaintext
Completion Time (P4) = 15 
Turnaround Time (P4) = 15 - 2 = 13 
Wait Time (P4) = 13 - 5 = 8
Response Time (P4) = 10 - 2 = 8
```

## 4。总结

本文讨论了 CPU 调度概念：到达、突发、完成、周转、等待和响应时间。我们还讨论了如何计算它们，并提供了一个示例进行说明。

通过考虑这些因素，操作系统可以有效地平衡各种活动的需求，以提高系统的整体性能和效率。