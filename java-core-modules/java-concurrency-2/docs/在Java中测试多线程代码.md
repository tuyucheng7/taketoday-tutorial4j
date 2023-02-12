## 1. 简介

在本教程中，我们将介绍测试并发程序的一些基础知识。我们将主要关注基于线程的并发及其在测试中出现的问题。

我们还将了解如何在Java中解决其中一些问题并有效地测试多线程代码。

## 2. 并发编程

并发编程是指我们**将大块计算分解为更小的、相对独立的计算的编程**。

本练习的目的是同时运行这些较小的计算，甚至可能是并行运行。虽然有多种方法可以实现这一点，但目标始终是更快地运行程序。

### 2.1 线程和并发编程

随着处理器封装的内核比以往任何时候都多，并发编程处于最前沿，可以有效地利用它们。然而，**事实仍然是并发程序更难设计、编写、测试和维护**。因此，如果我们能够为并发程序编写有效且自动化的测试用例，那么我们就可以解决其中的大部分问题。

那么，是什么让为并发代码编写测试如此困难呢？要理解这一点，我们必须理解我们如何在我们的程序中实现并发。最流行的并发编程技术之一涉及使用线程。

现在，线程可以是原生的，在这种情况下，它们由底层操作系统调度。我们还可以使用所谓的绿色线程，它们由运行时直接调度。

### 2.2 测试并发程序的困难

不管我们使用什么类型的线程，使它们难以使用的原因是线程通信。如果我们确实设法编写了一个涉及线程但没有线程通信的程序，那就再好不过了！更现实地说，线程通常必须进行通信。有两种方法可以实现这一点-共享内存和消息传递。

**与并发编程相关的大部分问题都源于使用具有共享内存的本机线程**。出于同样的原因，测试此类程序也很困难。访问共享内存的多个线程通常需要互斥。我们通常通过一些使用锁的保护机制来实现这一点。

但这仍然可能导致许多问题，例如竞争条件、[活锁、死锁和线程饥饿](https://www.baeldung.com/cs/deadlock-livelock-starvation)，仅举几例。此外，这些问题是间歇性的，因为本机线程的线程调度是完全不确定的。

因此，为并发程序编写能够以确定性方式检测这些问题的有效测试确实是一个挑战！

### 2.3 线程交错剖析

我们知道本机线程可以由操作系统不可预测地调度。**如果这些线程访问和修改共享数据，就会引起有趣的线程交错**。虽然其中一些交错可能是完全可以接受的，但其他交错可能会使最终数据处于不希望的状态。

让我们举个例子。假设我们有一个由每个线程递增的全局计数器。在处理结束时，我们希望此计数器的状态与已执行的线程数完全相同：

```java
private int counter;
public void increment() {
    counter++;
}
```

现在，**在Java中递增原始整数不是原子操作**。它包括读取值，自增值，最后保存值。当多个线程执行同样的操作时，可能会产生许多可能的交错：

<img src="../assets/img.png">

虽然这种特殊的交错产生了完全可以接受的结果，但这个交错怎么样：

<img src="../assets/img_1.png">

这不是我们所期望的。现在，想象一下数百个线程运行比这复杂得多的代码。这将产生难以想象的线程交错方式。

有几种方法可以编写避免此问题的代码，但这不是本教程的主题。使用锁的同步是常见的同步之一，但它存在与争用条件相关的问题。

## 3. 测试多线程代码

现在我们了解了测试多线程代码的基本挑战，我们将看看如何克服它们。我们将构建一个简单的用例并尝试模拟尽可能多的与并发相关的问题。

让我们从定义一个简单的类开始，该类可以对可能的任何事物进行计数：

```java
public class MyCounter {
	private int count;

	public void increment() {
		int temp = count;
		count = temp + 1;
	}
	// Getter for count
}
```

这是一段看似无害的代码，但**不难理解它不是线程安全的**。如果我们碰巧用这个类编写了一个并发程序，它必然是有缺陷的。此处测试的目的是识别此类缺陷。

### 3.1 测试非并发部分

根据经验，**始终建议通过将代码与任何并发行为隔离开来测试代码**。这是为了合理地确定代码中没有与并发无关的其他缺陷。让我们看看我们该怎么做：

```java
@Test
public void testCounter() {
    MyCounter counter = new MyCounter();
    for (int i = 0; i < 500; i++) {
        counter.increment();
    }
    assertEquals(500, counter.getCount());
}
```

虽然这里没有太多内容，但这个测试让我们确信它至少在没有并发的情况下是有效的。

### 3.2 首次尝试并发测试

让我们继续测试相同的代码，这次是在并发设置中。我们将尝试使用多个线程访问此类的同一个实例，并查看它的行为方式：

```java
@Test
public void testCounterWithConcurrency() throws InterruptedException {
    int numberOfThreads = 10;
    ExecutorService service = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    MyCounter counter = new MyCounter();
    for (int i = 0; i < numberOfThreads; i++) {
        service.execute(() -> {
            counter.increment();
            latch.countDown();
        });
    }
    latch.await();
    assertEquals(numberOfThreads, counter.getCount());
}
```

这个测试是合理的，因为我们正在尝试使用多个线程对共享数据进行操作。当我们将线程数保持在较低水平时(比如10)，我们会注意到它几乎一直在通过。有趣的是，**如果我们开始增加线程数，比如增加到100，我们会发现测试在大多数情况下都开始失败**。

### 3.3 更好的并发测试尝试

虽然之前的测试确实表明我们的代码不是线程安全的，但这个测试存在问题。此测试不是确定性的，因为底层线程以非确定性方式交错。我们的程序真的不能依赖这个测试。

**我们需要的是一种控制线程交错的方法，以便我们可以用更少的线程以确定性的方式揭示并发问题**。我们将从稍微调整我们正在测试的代码开始：

```java
public synchronized void increment() throws InterruptedException {
    int temp = count;
    wait(100);
    count = temp + 1;
}
```

在这里，我们使方法同步并在方法中的两个步骤之间引入了等待。synchronized关键字确保一次只有一个线程可以修改count变量，并且wait在每个线程执行之间引入了延迟。

请注意，我们不一定要修改我们打算测试的代码。但是，由于我们可以影响线程调度的方法不多，因此我们采用这种方法。

在后面的部分中，我们将看到如何在不更改代码的情况下做到这一点。

现在，让我们像之前一样测试这段代码：

```java
@Test
public void testSummationWithConcurrency() throws InterruptedException {
    int numberOfThreads = 2;
    ExecutorService service = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    MyCounter counter = new MyCounter();
    for (int i = 0; i < numberOfThreads; i++) {
        service.submit(() -> {
            try {
                counter.increment();
            } catch (InterruptedException e) {
                // Handle exception
            }
            latch.countDown();
        });
    }
    latch.await();
    assertEquals(numberOfThreads, counter.getCount());
}
```

在这里，我们只用两个线程运行它，而且我们很可能能够找到我们遗漏的缺陷。我们在这里所做的是尝试实现特定的线程交错，我们知道这会影响我们。虽然有利于演示，但**我们可能发现这对实际用途没有用**。

## 4. 可用的测试工具

随着线程数量的增加，它们可能交错的方式数量呈指数级增长。**不可能找出所有这些交错并对其进行测试**。我们必须依靠工具来为我们承担相同或相似的努力。幸运的是，有几个可以让我们的生活更轻松。

我们可以使用两大类工具来测试并发代码。第一个使我们能够对具有许多线程的并发代码产生相当高的压力。压力会增加罕见交错的可能性，从而增加我们发现缺陷的机会。

第二个使我们能够模拟特定的线程交错，从而帮助我们更确定地发现缺陷。

### 4.1 tempus-fugit

[tempus-fugit](http://tempusfugitlibrary.org/) Java库帮**助我们轻松编写和测试并发代码**。我们在这里只关注这个库的测试部分。我们之前看到，对具有多线程的代码产生压力会增加发现与并发相关的缺陷的机会。

虽然我们可以自己编写实用程序来产生压力，但tempus-fugit提供了实现相同目的的便捷方法。

让我们重新审视我们之前尝试产生压力的相同代码，并了解我们如何使用tempus-fugit实现相同的目标：

```java
public class MyCounterTests {
    @Rule
    public ConcurrentRule concurrently = new ConcurrentRule();
    @Rule
    public RepeatingRule rule = new RepeatingRule();
    private static MyCounter counter = new MyCounter();
	
    @Test
    @Concurrent(count = 10)
    @Repeating(repetition = 10)
    public void runsMultipleTimes() {
        counter.increment();
    }

    @AfterClass
    public static void annotatedTestRunsMultipleTimes() throws InterruptedException {
        assertEquals(counter.getCount(), 100);
    }
}
```

在这里，我们使用了tempus-fugit提供的两个Rule。这些规则拦截测试并帮助我们应用所需的行为，如重复和并发。因此，实际上，我们从十个不同的线程中每次重复被测操作十次。

随着我们增加重复和并发，我们检测与并发相关的缺陷的机会也会增加。

### 4.2 Thread Weaver

[Thread Weaver](https://github.com/google/thread-weaver)本质上是**一个用于测试多线程代码的Java框架**。我们之前已经看到线程交错是非常不可预测的，因此，我们可能永远无法通过常规测试发现某些缺陷。我们实际上需要的是一种控制交错并测试所有可能交错的方法。在我们之前的尝试中，这已被证明是一项相当复杂的任务。

让我们看看Thread Weaver如何在这里帮助我们。Thread Weaver允许我们以多种方式交错执行两个单独的线程，而不必担心如何交错。它还使我们有可能对我们希望线程交错的方式进行细粒度控制。

让我们看看如何改进我们之前天真的尝试：

```java
public class MyCounterTests {
	private MyCounter counter;

	@ThreadedBefore
	public void before() {
		counter = new MyCounter();
	}
	@ThreadedMain
	public void mainThread() {
		counter.increment();
	}
	@ThreadedSecondary
	public void secondThread() {
		counter.increment();
	}
	@ThreadedAfter
	public void after() {
		assertEquals(2, counter.getCount());
	}

	@Test
	public void testCounter() {
		new AnnotatedTestRunner().runTests(this.getClass(), MyCounter.class);
	}
}
```

在这里，我们定义了两个尝试增加计数器的线程。Thread Weaver将尝试在所有可能的交错场景中使用这些线程运行此测试。可能在其中一个交错中，我们会发现缺陷，这在我们的代码中非常明显。

### 4.3 MultithreadedTC

[MultithreadedTC](http://www.cs.umd.edu/projects/PL/multithreadedtc/overview.html)是另一个**用于测试并发应用程序的框架**。它具有一个节拍器，用于对多线程中的活动顺序进行精细控制。它支持执行特定线程交错的测试用例。因此，理想情况下，我们应该能够确定性地测试单独线程中的每个重要交错。

现在，对这个功能丰富的库的完整介绍超出了本教程的范围。但是，我们当然可以看到如何快速设置测试，为我们提供执行线程之间可能的交错。

让我们看看如何使用MultithreadedTC更确定地测试我们的代码：

```java
public class MyTests extends MultithreadedTestCase {
	private MyCounter counter;
	@Override
	public void initialize() {
		counter = new MyCounter();
	}
	public void thread1() throws InterruptedException {
		counter.increment();
	}
	public void thread2() throws InterruptedException {
		counter.increment();
	}
	@Override
	public void finish() {
		assertEquals(2, counter.getCount());
	}

	@Test
	public void testCounter() throws Throwable {
		TestFramework.runManyTimes(new MyTests(), 1000);
	}
}
```

在这里，我们设置了两个线程来操作共享计数器并递增它。我们已将MultithreadedTC配置为使用这些线程对多达一千种不同的交错执行此测试，直到它检测到一个失败。

### 4.4 Java jcstress

OpenJDK维护代码工具项目以提供用于处理OpenJDK项目的开发人员工具。这个项目下有几个有用的工具，包括[Java Concurrency Stress Tests(jcstress)](https://openjdk.java.net/projects/code-tools/jcstress/)。这是作为实验工具和测试套件开发的，用于研究Java中并发支持的正确性。

尽管这是一个实验性工具，但我们仍然可以利用它来分析并发代码并编写测试来弥补与之相关的缺陷。让我们看看如何测试本教程中到目前为止使用的代码。从使用的角度来看，这个概念非常相似：

```java
@JCStressTest
@Outcome(id = "1", expect = ACCEPTABLE_INTERESTING, desc = "One update lost.")
@Outcome(id = "2", expect = ACCEPTABLE, desc = "Both updates.")
@State
public class MyCounterTests {

	private MyCounter counter;

	@Actor
	public void actor1() {
		counter.increment();
	}

	@Actor
	public void actor2() {
		counter.increment();
	}

	@Arbiter
	public void arbiter(I_Result r) {
		r.r1 = counter.getCount();
	}
}
```

在这里，我们用注解@State标记了该类，这表明它包含由多个线程改变的数据。此外，我们正在使用一个注解@Actor，它标记了保存由不同线程完成的操作的方法。

最后，我们有一个标有注解@Arbiter的方法，它基本上只在所有Actor都访问过状态后才访问它。我们还使用注解结果来定义我们的期望。

总的来说，设置非常简单直观。我们可以使用框架提供的测试工具来运行它，它会找到所有用@JCStressTest标注的类，并在几次迭代中执行它们以获得所有可能的交错。

## 5. 检测并发问题的其他方法

为并发代码编写测试很困难但有可能。我们已经看到了挑战和一些克服这些挑战的流行方法。然而，**我们可能无法仅通过测试来识别所有可能的并发问题**-尤其是当编写更多测试的增量成本开始超过它们的好处时。

因此，结合合理数量的自动化测试，我们可以使用其他技术来识别并发问题。这将增加我们发现并发问题的机会，而无需深入了解自动化测试的复杂性。我们将在本节中介绍其中的一些内容。

### 5.1 静态分析

**静态分析是指在不实际执行程序的情况下对程序进行分析**。现在，这样的分析有什么用呢？我们会谈到这一点，但让我们首先了解它与动态分析的对比。到目前为止，我们编写的单元测试需要在实际执行它们测试的程序时运行。这就是它们成为我们主要称为动态分析的一部分的原因。

请注意，静态分析绝不能替代动态分析。但是，它提供了一种非常宝贵的工具，可以在我们执行代码之前很久就检查代码结构并识别可能的缺陷。**静态分析使用了大量根据经验和理解精心策划的模板**。

虽然很可能只查看代码并与我们策划的最佳实践和规则进行比较，但我们必须承认这对于大型程序来说是不合理的。但是，有几种工具可以为我们执行此分析。它们相当成熟，对大多数流行的编程语言都有大量的规则。

一种流行的Java静态分析工具是[FindBugs](http://findbugs.sourceforge.net/)。FindBugs寻找“错误模式”的实例。错误模式是一种代码习惯用法，通常是错误。这可能是由于多种原因造成的，例如困难的语言特性、被误解的方法和被误解的不变量。

**FindBugs检查Java字节码是否出现错误模式，而无需实际执行字节码**。这使用起来非常方便，运行速度也很快。FindBugs报告属于许多类别的错误，例如条件、设计和重复代码。

它还包括与并发相关的缺陷。但是，必须注意FindBugs可能报告误报。这些在实践中较少，但必须与手动分析相关联。

### 5.2 模型检查

**模型检查是一种检查系统的有限状态模型是否满足给定规范的方法**。现在，这个定义可能听起来太学术化了，但请忍耐一下！

我们通常可以将计算问题表示为有限状态机。虽然这本身是一个广阔的领域，但它为我们提供了一个模型，其中包含一组有限的状态和它们之间的转换规则，并具有明确定义的开始和结束状态。

现在，**规范定义了模型应该如何表现才能被认为是正确的**。本质上，该规范包含模型所代表的系统的所有要求。获取规范的方法之一是使用由Amir Pnueli开发的时间逻辑公式。

虽然手动执行模型检查在逻辑上是可能的，但这是非常不切实际的。幸运的是，这里有很多工具可以帮助我们。可用于Java的此类工具之一是[Java PathFinder](http://javapathfinder.sourceforge.net/)(JPF)。JPF是根据NASA多年的经验和研究开发的。

具体来说，**JPF是Java字节码的模型检查器**。它以所有可能的方式运行程序，从而沿着所有可能的执行路径检查属性违规，例如死锁和未处理的异常。因此，它可以证明在查找任何程序中与并发相关的缺陷方面非常有用。

## 6. 事后思考

到目前为止，**最好尽可能避免与多线程代码相关的复杂性**，这对我们来说应该不足为奇了。开发设计更简单、更易于测试和维护的程序应该是我们的首要目标。我们必须承认，现代应用程序通常需要并发编程。

但是，**在开发可以让我们的生活更轻松的并发程序时，我们可以采用一些最佳实践和原则**。在本节中，我们将介绍其中一些最佳实践，但我们应该记住，这个列表还远未完成！

### 6.1 降低复杂性

即使没有任何并发元素，复杂性也会使测试程序变得困难。面对并发，这只是复合。不难理解为什么**更简单和更小的程序更容易推理并因此更有效地进行测试**。有几种最佳模式可以在这里帮助我们，例如SRP(单一职责模式)和KISS(保持简单)，仅举几例。

现在，虽然这些并没有直接解决为并发代码编写测试的问题，但它们使这项工作更容易尝试。

### 6.2 考虑原子操作

**原子操作是彼此完全独立运行的操作**。因此，可以简单地避免预测和测试交织的困难。比较和交换就是这样一种广泛使用的原子指令。简单地说，它将内存位置的内容与给定值进行比较，只有当它们相同时，才修改该内存位置的内容。

大多数现代微处理器都提供该指令的一些变体。Java提供了一系列原子类，如AtomicInteger和AtomicBoolean，提供了下面的比较和交换指令的好处。

### 6.3 拥抱不变性

在多线程编程中，可以更改的共享数据总是为错误留下空间。**不变性是指数据结构在实例化后无法修改的情况**。这是并行程序的天作之合。如果一个对象的状态在创建后不能改变，则竞争线程不必对它们申请互斥。这大大简化了并发程序的编写和测试。

但是，请注意，我们可能并不总是有选择不变性的自由，但我们必须在可能的情况下选择它。

### 6.4 避免共享内存

大多数与多线程编程相关的问题都可以归因于我们在竞争线程之间共享内存这一事实。如果我们能摆脱它们该多好！好吧，我们仍然需要一些线程通信的机制。

**并发应用程序的替代设计模式为我们提供了这种可能性。其中一个流行的模型是Actor模型**，它将Actor规定为并发的基本单位。在这个模型中，参与者通过发送消息来相互交互。

Akka是一个用Scala编写的框架，它利用[Actor模型](https://doc.akka.io//docs/akka/2.5/actors.html#introduction)提供更好的并发原语。

## 7. 总结

在本教程中，我们介绍了一些与并发编程相关的基础知识。我们特别详细地讨论了Java中的多线程并发。我们在测试此类代码时遇到了它给我们带来的挑战，尤其是共享数据。此外，我们还介绍了一些可用于测试并发代码的工具和技术。

我们还讨论了避免并发问题的其他方法，包括自动化测试之外的工具和技术。最后，我们介绍了一些与并发编程相关的编程最佳实践。