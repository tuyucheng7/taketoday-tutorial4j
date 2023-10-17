## 1. 概述

Java happens before保证是一组规则，用于管理如何允许Java VM和CPU重新排序指令以提高性能。happens before保证使得线程可以依赖何时将变量值同步到主内存或从主内存同步，以及同时同步了哪些其他变量。Java happens before保证以访问变量和从同步块中访问的变量为中心。volatile

这个Java happens before guarantee教程会提到happens before保证由Java volatile和Java synchronized声明提供，但我不会在本教程中解释所有关于这些声明的内容。这些术语在此处有更详细的介绍：
[Java volatile教程](http://tutorials.jenkov.com/java-concurrency/volatile.html)
[Java synchronized教程](http://tutorials.jenkov.com/java-concurrency/synchronized.html)。

## 2. 指令重排序

如果指令不相互依赖，现代CPU可以并行执行指令。例如，下面两条指令互不依赖，因此可以并行执行：

```java
a = b + c

d = e + f
```

但是，下面两条指令不能轻易并行执行，因为第二条指令依赖于第一条指令的结果：

```java
a = b + c
d = a + e
```

假设上面的两条指令是更大指令集的一部分，如下所示：

```java
a = b + c
d = a + e

l = m + n
y = x + z
```

这些说明可以像下面这样重新排序。然后CPU至少可以并行执行前3条指令，并且一旦第一条指令执行完，它就可以开始执行第4条指令。

```java
a = b + c

l = m + n
y = x + z

d = a + e
```

如你所见，重新排序指令可以增加CPU中指令的并行执行。增加并行化意味着提高性能。

只要程序的语义不变，就允许Java VM和CPU进行指令重新排序。最终结果必须与按照指令在源代码中列出的确切顺序执行的指令相同。

## 3. 多CPU计算机中的指令重排序问题

指令重新排序在多线程、多CPU系统中提出了一些挑战。我将尝试通过一个代码示例来说明这些问题。请记住，该示例是专门为说明这些问题而构建的。因此，代码示例不是任何推荐！

想象一下，两个线程协作以尽可能快的速度在屏幕上绘制帧。一个框架生成框架，另一个线程在屏幕上绘制框架。

这两个线程需要通过某种通信机制交换帧。在下面的代码示例中，我创建了一个此类通信机制的示例-一个名为FrameExchanger的Java类。

帧生成线程尽可能快地生成帧。框架绘制线程将尽可能快地绘制这些框架。

有时，生产者线程可能会在绘图线程有时间绘制它们之前生成2帧。在这种情况下，应该只绘制最新的帧。我们不希望绘图线程落后于生产线程。如果生产者线程在绘制前一帧之前准备好新帧，则前一帧会被新帧简单地覆盖。换句话说，前一帧被“丢弃”了。

有时绘图线程可能会绘制一个框架，并在生成线程生成新框架之前准备好绘制新框架。在那种情况下，我们希望绘图框架等待新框架。没有理由浪费CPU和GPU资源来重新绘制与刚刚绘制的完全相同的帧！屏幕不会因此发生变化，用户也不会从中看到任何新内容。

FrameExchanger计算存储的帧数和拍摄的帧数，因此我们可以了解丢弃了多少帧。

下面是FrameExchanger的代码。注意：省略了Frame类定义。为了理解FrameExchanger的工作原理，这个类的外观并不重要。生产线程会storeFrame()不断调用，绘图线程会takeFrame()不断调用。

```java
public class FrameExchanger {

    private long framesStoredCount = 0;
    private long framesTakenCount = 0;

    private boolean hasNewFrame = false;

    private Frame frame = null;

    // called by Frame producing thread
    public void storeFrame(Frame frame) {
        this.frame = frame;
        this.framesStoredCount++;
        this.hasNewFrame = true;
    }

    // called by Frame drawing thread
    public Frame takeFrame() {
        while (!hasNewFrame) {
            // busy wait until new frame arrives
        }
        Frame newFrame = this.frame;
        this.framesTakenCount++;
        this.hasNewFrame = false;
        return newFrame;
    }
}
```

请注意方法中的三个指令storeFrame()似乎并不相互依赖。这意味着，对于Java VM和CPU来说，重新排序指令似乎是可以的，以防Java VM或CPU确定这样做是有利的。但是，想象一下如果重新排序指令会发生什么，如下所示：

```java
public void storeFrame(Frame frame) {
    this.hasNewFrame = true;
    this.framesStoredCount++;
    this.frame = frame;
}
```

请注意在分配字段以引用新的Frame对象之前，字段hasNewFrame现在是如何设置的。这意味着，如果绘图线程正在方法的while循环中等待，则绘图线程可以退出while循环，并获取旧的Frame对象。这将导致重新绘制旧的Frame，从而导致资源浪费。true frame takeFrame()

显然，在这种特殊情况下，重绘旧框架不会使应用程序崩溃或出现故障。它只是浪费CPU和GPU资源。然而，在其他情况下，此类指令重新排序可能会使应用程序出现故障。

## Java volatile可见性保证

Java volatile关键字为volatile变量的写入和读取导致变量值与主内存的同步提供了一些可见性保证。这种与主内存的同步使该值对其他线程可见。因此术语可见性保证。

在本节中，我将简要介绍Java volatile可见性保证，并解释指令重新排序如何破坏volatile可见性保证。这就是为什么我们也有Java volatile happens before guarantee，对指令重新排序施加一些限制，这样volatile可见性保证不会被指令重新排序破坏。

## Java volatile写入可见性保证

当你写入Java volatile变量时，该值保证直接写入主内存。此外，写入volatile变量的线程可见的所有变量也将同步到主内存。

为了说明Java volatile写可见性保证，请看这个例子：

```java
this.nonVolatileVarA = 34;
this.nonVolatileVarB = new String("Text");
this.volatileVarC = 300;
```

此示例包含两次写入非易失性变量和一次写入易失性变量。该示例没有明确显示哪个变量被声明为volatile，因此为了清楚起见，假设命名的变量(字段，实际上)volatileVarC被声明为volatile。

当上面例子中的第三条指令写入volatile变量时volatileVarC，两个非volatile变量的值也会同步到主存——因为这些变量在写入volatile变量时对线程是可见的。

### Java volatile读取可见性保证

当你读取Java的值时，volatile该值保证直接从内存中读取。此外，读取volatile变量的线程可见的所有变量也将从主内存中刷新它们的值。

为说明Java易失性读取可见性保证，请看以下示例：

```java
c = other.volatileVarC;
b = other.nonVolatileB;
a = other.nonVolatileA;
```

请注意，第一条指令是读取变量volatile(other.volatileVarC)。当other.volatileVarC从主存中读入时，和other.nonVolatileB也other.nonVolatileA从主存中读入。

## Java Volatile发生在保证之前

Java volatile发生在保证对围绕volatile变量的指令重新排序设置一些限制之前。为了说明为什么需要这种保证，让我们修改本教程前面的FrameExchanger类以hasNewFrame声明变量volatile：

```java
public class FrameExchanger {

    private long framesStoredCount = 0;
    private long framesTakenCount = 0;

    private volatile boolean hasNewFrame = false;

    private Frame frame = null;

    // called by Frame producing thread
    public void storeFrame(Frame frame) {
        this.frame = frame;
        this.framesStoredCount++;
        this.hasNewFrame = true;
    }

    // called by Frame drawing thread
    public Frame takeFrame() {
        while (!hasNewFrame) {
            // busy wait until new frame arrives
        }
        Frame newFrame = this.frame;
        this.framesTakenCount++;
        this.hasNewFrame = false;
        return newFrame;
    }
}
```

现在，当hasNewFrame变量设置为时true，frame和frameStoredCount也将同步到主存。此外，每次绘图线程hasNewFrame在方法内的while循环中读取变量时takeFrame()，frame和framesStoredCount也会从主内存中刷新。framesTakenCount此时Even将从主内存中更新。

想象一下，如果Java VM对方法内的指令重新排序storeFrame()，如下所示：

```java
// called by Frame producing thread
public void storeFrame(Frame frame) {
    this.hasNewFrame = true;
    this.framesStoredCount++;
    this.frame = frame;
}
```

现在，当执行第一条指令时(因为它是易变的)，framesStoredCount和frame字段将同步到主内存hasNewFrame-这是在为它们分配新值之前！

这意味着，执行该方法的绘图线程可能会在新值分配给变量之前takeFrame()退出循环。即使生产线程已经为变量分配了一个新值，也不能保证这个值会同步到主内存，因此它对绘图线程可见！while``frame``frame

### 在保证写入volatile变量之前发生

如你所见，storeFrame()方法内部指令的重新排序可能会使应用程序出现故障。这是在保证出现之前发生易失性写入的地方-限制在写入易失性变量时允许什么样的指令重新排序：

在写入易失性变量之前发生的对非易失性或易失性变量的写入保证在写入该易失性变量之前发生。

在该方法的情况下storeFrame()，这意味着不能将前两个写入指令重新排序为在最后写入之后发生hasNewFrame，因为hasNewFrame是一个易失性变量。

```java
// called by Frame producing thread
public void storeFrame(Frame frame) {
    this.frame = frame;
    this.framesStoredCount++;
    this.hasNewFrame = true;  // hasNewFrame is volatile
}
```

前两条指令不写入volatile变量，因此它们可以由Java VM自由重新排序。因此，允许这种重新排序：

```java
// called by Frame producing thread
public void storeFrame(Frame frame) {
    this.framesStoredCount++;
    this.frame = frame;
    this.hasNewFrame = true;  // hasNewFrame is volatile
}
```

这种重新排序不会破坏方法中的代码takeFrame()，因为在写入变量frame之前仍会写入变量。hasNewFrame整个程序仍然按预期工作。

### 在保证读取volatile变量之前发生

Java中的volatile变量在保证读取volatile变量之前有类似的happens before。只是，方向相反：

volatile变量的读取将发生在任何后续的volatile和非volatile变量的读取之前。

当我说方向不同于写入时，我的意思是对于易失性写入，写入之前的所有指令将保留在易失性写入之前。对于volatile reads，volatile read之后的所有read都会在volatile read之后保留。

看下面的例子：

```java
int a = this.volatileVarA;
int b = this.nonVolatileVarB;
int c = this.nonVolatileVarC;
```

指令2和3都必须保留在第一条指令之后，因为第一条指令读取一个易失性变量。换句话说，volatile变量的读取保证发生在非volatile变量的两个后续读取之前。

最后两条指令可以在它们之间自由地重新排序，而不会违反第一条指令中易失性读取的保证。因此，允许这种重新排序：

```java
int a = this.volatileVarA;
int c = this.nonVolatileVarC;
int b = this.nonVolatileVarB;
```

由于易失性读取可见性保证，当this.volatileVarA从主内存读取时，所有其他变量在那时对线程可见。因此，this.nonVolatileVarB和this.nonVolatileVarC也同时从主存中读入。这意味着，读取的线程也volatileVarA可以依赖主内存nonVolatileVarB并nonVolatileVarC与主内存保持同步。

如果最后两条指令中的任何一条要在第一条易失性读取指令上方重新排序，则该指令在执行时的保证将失效。这就是为什么以后的读取不能重新排序以显示在volatile变量读取之上的原因。

关于该takeFrame()方法，volatile变量的第一次读取是hasNewFrame while循环内字段的读取。这意味着，没有任何阅读指令可以重新排序以位于其上方。在这种特殊情况下，将任何其他读取操作移动到while循环之上也会破坏代码的语义，因此无论如何都不允许重新排序。

```java
// called by Frame drawing thread
public Frame takeFrame() {
    while( !hasNewFrame) {
        //busy wait until new frame arrives
    }

    Frame newFrame = this.frame;
    this.framesTakenCount++;
    this.hasNewFrame = false;
    return newFrame;
}
```

## Java同步可见性保证

Java synchronized块提供类似于Java volatile变量的可见性保证。我将简要解释Java同步可见性保证。

### Java同步条目可见性保证

当线程进入synchronized块时，线程可见的所有变量都从主内存中刷新。

### Java同步退出可见性保证

当线程退出synchronized块时，线程可见的所有变量都被写回主内存。

### Java同步可见性示例

看看这个ValueExchanger类：

```java
public class ValueExchanger {
    private int valA;
    private int valB;
    private int valC;

    public void set(Values v) {
        this.valA = v.valA;
        this.valB = v.valB;

        synchronized (this) {
            this.valC = v.valC;
        }
    }

    public void get(Values v) {
        synchronized (this) {
            v.valC = this.valC;
        }
        v.valB = this.valB;
        v.valA = this.valA;
    }
}
```

set()注意and方法中的两个同步块get()。请注意在这两种方法中块是如何放置在最后和最前面的。

在set()方法中，方法末尾的同步块将强制所有变量在更新后同步到主内存。当线程退出同步块时，会将变量值刷新到主内存。这就是它被放在方法最后的原因-以保证所有更新的变量值都被刷新到主内存。

在get()方法中，同步块放在方法的开头。当线程调用get()进入synchronized块时，所有的变量都从主存中重新读入。这就是为什么将此同步块放在方法开头的原因-以确保所有变量在读取之前都从主内存中刷新。

## Java同步发生在保证之前

Java synchronized块提供两种happens before保证：一种与synchronized块的开始相关，另一种与synchronized块的结束相关。我将在以下部分中介绍两者。

### Java同步块开始发生在保证之前

Java同步块的开头提供可见性保证(本教程前面提到过)，当线程进入同步块时，线程可见的所有变量都将读入(刷新)主内存。

为了能够维持该保证，必须对指令重新排序设置一组限制。为了说明原因，我将使用get()前面显示的ValueExchanger的方法：

```java
public void get(Values v) {
    synchronized(this) {
        v.valC = this.valC;
    }
    v.valB = this.valB;
    v.valA = this.valA;
}
```

如你所见，方法开头的同步块将保证所有变量this.valC,this.valB和this.valA从主内存刷新(读入)。随后对这些变量的读取将使用最新值。

为此，变量的读取都不能重新排序以出现在同步块开始之前。如果变量的读取被重新排序为出现在同步块的开始之前，你将失去从主内存刷新变量值的保证。以下未经许可的指令重新排序就是这种情况：

```java
public void get(Values v) {
    v.valB = this.valB;
    v.valA = this.valA;
    synchronized(this) {
        v.valC = this.valC;
    }
}
```

### Java同步块结束发生在保证之前

同步块的结尾提供了可见性保证，即当线程退出同步块时，所有更改的变量都将写回主内存。

为了能够维持该保证，必须对指令重新排序设置一组限制。为了说明原因，我将使用set()前面显示的ValueExchanger的方法：

```java
public void set(Values v) {
    this.valA = v.valA;
    this.valB = v.valB;

    synchronized(this) {
        this.valC = v.valC;
    }
}
```

如你所见，方法末尾的同步块将保证所有更改的变量this.valA，this.valB并在线程调用退出同步块this.valC时写回(刷新)到主内存。set()

为此，对变量的任何写入都不能重新排序以显示在同步块结束之后。如果对变量的写入被重新排序为出现在同步块结束之后，你将失去变量值被写回主内存的保证。在以下未经许可的指令重新排序中就是这种情况：

```java
public void set(Values v) {
    synchronized(this) {
        this.valC = v.valC;
    }
    this.valA = v.valA;
    this.valB = v.valB;
}
```