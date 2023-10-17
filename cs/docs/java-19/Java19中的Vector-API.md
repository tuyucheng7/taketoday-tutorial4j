---
layout: post
title:  Java 19中的Vector API
category: java-new
copyright: java-new
excerpt: Java 19
---

## 1. 简介

[Vector API](https://download.java.net/java/early_access/loom/docs/api/jdk.incubator.vector/jdk/incubator/vector/Vector.html)是Java生态系统中的孵化器API，用于在受支持的CPU架构上表达Java中的向量计算。它旨在为矢量计算提供优于等效标量替代方案的性能增益。

在Java19中，作为JEP426的一部分，提议对Vector API进行第四轮孵化。

在本教程中，我们将探讨Vector API、其相关术语以及如何利用该API。

## 2. 标量、向量和并行性

在深入研究Vector API之前，了解CPU运算中标量和向量的概念非常重要。

### 2.1 处理单元和CPU

CPU利用一组处理单元来执行操作。处理单元通过操作一次只能计算一个值。该值称为标量值，因为它就是一个值。运算可以是对单个操作数进行操作的一元运算，也可以是对两个操作数进行操作的二元运算。将数字加1是一元运算的示例，而将两个数字相加是二元运算。

处理单元需要一定的时间来执行这些操作。我们以周期来衡量时间。处理单元可能需要0个周期来执行一项操作，并需要许多周期来执行另一项操作，例如添加数字。

### 2.2 并行性

传统的现代CPU具有多个核心，每个核心容纳多个能够执行操作的处理单元。这提供了同时并行地在这些处理单元上执行操作的能力。我们可以让多个线程在它们的核心中运行它们的程序，我们可以并行执行操作。

当我们进行大规模计算时，例如从海量数据源中添加大量数字，我们可以将数据拆分为更小的数据块，并将它们分布在多个线程中，希望我们能够获得更快的处理速度。这是进行并行计算的方法之一。

### 2.3 SIMD处理器

我们可以使用SIMD处理器以不同的方式进行并行计算。SIMD代表单指令多数据。在这些处理器中，没有多线程的概念。这些SIMD处理器依赖于多个处理单元，并且这些单元在单个CPU周期中（即同时）执行相同的操作。它们共享执行的程序（指令），但不共享底层数据，因此得名。它们具有相同的操作，但对不同的操作数进行操作。

与处理器从内存加载标量值的方式不同，SIMD机器在操作之前将内存中的整数数组加载到寄存器中。SIMD硬件的组织方式使得值数组的加载操作能够在单个周期内进行。SIMD机器允许我们并行地对数组执行计算，而无需实际依赖[并发编程](https://www.baeldung.com/cs/concurrency-vs-parallelism#:~:text=Parallelismistheabilityto,canbeadistributedsystem.)。

由于SIMD机器将内存视为数组或一系列值，因此我们将其称为向量，并且SIMD机器执行的任何操作都成为向量操作。因此，这是一种利用SIMD架构原理来执行并行处理任务的非常强大且高效的方法。  

## 3. Vector API

现在我们知道了什么是向量，让我们尝试了解Java提供的Vector API的基础知识。Java中的Vector由抽象类Vector<E>表示。这里，E是以下标量原始整数类型（byte、Short、int、long）和浮点类型（float、double）的装箱类型。

### 3.1 形状、物种和泳道

我们只有一个预定义的空间来存储和使用向量，目前范围为64到512位。想象一下，如果我们有一个整数值向量，并且有256位来存储它，那么我们总共将有8个分量。这是因为原始int值的大小是32位。这些组件在Vector API的上下文中称为通道。

向量的形状是向量的按位大小或位数。512位形状的向量将有16个通道，一次可以对16个整数进行操作，而64位向量只有4个。这里，我们使用术语通道来表示数据在通道中流动的相似性在SIMD机器内。

向量的种类是向量的形状和数据类型的组合，例如int、float等。它由VectorSpecies<E>表示。

### 3.2 向量的车道运算

向量运算大致有两种类型，分为车道运算和跨车道运算。

顾名思义，逐通道操作一次仅对一个或多个向量的单个通道执行标量操作。这些操作可以将一个向量的一个通道与第二个向量的一个通道组合起来，例如在加法操作期间。

另一方面，跨通道操作可以计算或修改来自向量的不同通道的数据。对向量的分量进行排序是跨通道操作的一个示例。跨通道操作可以从源向量产生不同形状的标量或向量。跨车道操作又可以分为排列操作和归约操作。

### 3.3 Vector<E\> API的层次结构

Vector<E>类对于六种支持类型中的每一种都有六个抽象子类：ByteVector、ShortVector、IntVector、LongVector、FloatVector和DoubleVector。对于SIMD机器来说，特定的实现非常重要，这就是为什么形状特定的子类进一步扩展了每种类型的这些类。例如Int128Vector、Int512Vector等。

## 4. 使用Vector API进行计算

最后让我们看一些Vector API代码。我们将在接下来的部分中讨论车道方向和跨车道操作。

### 4.1 两个数组相加

我们想要将两个整数数组相加并将信息存储在第三个数组中。传统的标量方法是：

```java
public int[] addTwoScalarArrays(int[] arr1, int[] arr2) {
    int[] result = new int[arr1.length];
    for(int i = 0; i< arr1.length; i++) {
        result[i] = arr1[i] + arr2[i];
    }
    return result;
}
```

现在让我们以向量方式编写相同的代码。Vector API包可在jdk.incubator.vector下找到，我们需要将其导入到我们的类中。

由于我们要处理向量，因此我们需要做的第一件事就是从两个数组创建向量。我们在此步骤中使用Vector API的fromArray()方法。此方法要求我们提供要创建的向量的种类以及开始加载的数组的起始偏移量。

在我们的例子中，偏移量将为0，因为我们希望从头开始加载整个数组。我们可以为我们的物种使用默认的SPECIES_PREFERRED，它使用适合其平台的最大位大小：

```java
static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
var v1 = IntVector.fromArray(SPECIES, arr1, 0);
var v2 = IntVector.fromArray(SPECIES, arr2, 0);
```

一旦我们从数组中获得了两个向量，我们就可以通过传递第二个向量来对其中一个向量使用add()方法：

```java
var result = v1.add(v2);
```

最后，我们将向量结果转换为数组并返回：

```java
public int[] addTwoVectorArrays(int[] arr1, int[] arr2) {
    var v1 = IntVector.fromArray(SPECIES, arr1, 0);
    var v2 = IntVector.fromArray(SPECIES, arr2, 0);
    var result = v1.add(v2);
    return result.toArray();
}
```

考虑到上述代码在SIMD机器上运行，加法操作在同一CPU周期中将两个向量的所有通道相加。

### 4.2 矢量蒙版

上面演示的代码也有其局限性。仅当通道数量与SIMD机器可以处理的向量大小相匹配时，它才能正常运行并提供所宣传的性能。这向我们介绍了使用向量掩码的想法，由VectorMasks<E>表示，它就像一个布尔值数组。当我们无法将整个输入数据填充到向量中时，我们会借助VectorMasks。

掩码选择要应用操作的通道。如果通道中的相应值为true，则应用该操作；如果为false，则执行不同的回退操作。

这些掩码帮助我们执行独立于矢量形状和大小的操作。我们可以使用预定义的length()方法，它将在运行时返回向量的形状。

这是一个稍微修改过的代码，带有掩码，可帮助我们以向量长度的步长迭代输入数组，然后进行尾部清理：

```java
public int[] addTwoVectorsWithMasks(int[] arr1, int[] arr2) {
    int[] finalResult = new int[arr1.length];
    int i = 0;
    for (; i < SPECIES.loopBound(arr1.length); i += SPECIES.length()) {
        var mask = SPECIES.indexInRange(i, arr1.length);
        var v1 = IntVector.fromArray(SPECIES, arr1, i, mask);
        var v2 = IntVector.fromArray(SPECIES, arr2, i, mask);
        var result = v1.add(v2, mask);
        result.intoArray(finalResult, i, mask);
    }

    // tail cleanup loop
    for (; i < arr1.length; i++) {
        finalResult[i] = arr1[i] + arr2[i];
    }
    return finalResult;
}
```

该代码现在执行起来更加安全，并且独立于向量的形状运行。

### 4.3 计算向量的范数

在本节中，我们将讨论另一个简单的数学计算，即两个值的法线。范数是我们将两个值的平方相加，然后对总和求平方根时得到的值。

让我们先看看标量运算是什么样子的：

```java
public float[] scalarNormOfTwoArrays(float[] arr1, float[] arr2) {
    float[] finalResult = new float[arr1.length];
    for (int i = 0; i < arr1.length; i++) {
        finalResult[i] = (arr1[i] * arr1[i] + arr2[i] * arr2[i]) * -1.0f;
    }
    return finalResult;
}
```

我们现在将尝试编写上述代码的向量替代方案。

首先，我们获得FloatVector类型的首选物种，它在这种情况下是最佳的：

```java
static final VectorSpecies<Float> PREFERRED_SPECIES = FloatVector.SPECIES_PREFERRED;
```

我们将使用掩码的概念，正如我们在本示例的上一节中讨论的那样。我们的循环运行直到第一个数组的loopBound值，并且以物种长度的步幅执行。在每个步骤中，我们将浮点值加载到向量中，并执行与标量版本中相同的数学运算。

最后，我们使用普通标量循环对剩余元素执行尾部清理。最终的代码与我们之前的示例非常相似：

```java
public float[] vectorNormalForm(float[] arr1, float[] arr2) {
    float[] finalResult = new float[arr1.length];
    int i = 0;
    int upperBound = SPECIES.loopBound(arr1.length);
    for (; i < upperBound; i += SPECIES.length()) {
        var va = FloatVector.fromArray(PREFERRED_SPECIES, arr1, i);
        var vb = FloatVector.fromArray(PREFERRED_SPECIES, arr2, i);
        var vc = va.mul(va)
            .add(vb.mul(vb))
            .neg();
        vc.intoArray(finalResult, i);
    }
    
    // tail cleanup
    for (; i < arr1.length; i++) {
        finalResult[i] = (arr1[i] * arr1[i] + arr2[i] * arr2[i]) * -1.0f;
    }
    return finalResult;
}
```

### 4.4 减少操作

Vector API中的归约操作是指将向量的多个元素组合成单个结果的操作。它允许我们执行计算，例如对向量的元素求和或查找向量内的最大值、最小值和平均值。

Vector API提供了可以利用SIMD架构机器的多种归约运算功能。一些常见的API包括：

-   reduceLanes()：此方法接受数学运算，例如ADD，并将向量的所有元素组合成单个值
-   reduceAll()：此方法与上面的类似，不同之处在于，它需要一个可以接受两个值并输出单个值的二进制归约操作
-   reduceLaneWise()：此方法减少特定车道中的元素并生成具有减少的车道值的向量。

我们将看到一个计算向量平均值的示例。

我们可以使用reduceLanes(ADD)来计算所有元素的总和，然后执行标量除以数组的长度：

```java
public double averageOfaVector(int[] arr) {
    double sum = 0;
    for (int i = 0; i< arr.length; i += SPECIES.length()) {
        var mask = SPECIES.indexInRange(i, arr.length);
        var V = IntVector.fromArray(SPECIES, arr, i, mask);
        sum += V.reduceLanes(VectorOperators.ADD, mask);
    }
    return sum / arr.length;
}
```

## 5. 与Vector API相关的注意事项

虽然我们可以欣赏Vector API的好处，但我们应该对它持保留态度。首先，该API仍处于孵化阶段。然而，有一个计划将向量类声明为原始类。

如上所述，Vector API具有硬件依赖性，因为它依赖于SIMD指令。许多功能可能在其他平台和架构上不可用。此外，与传统标量操作相比，维护矢量化操作总是存在开销。

在不了解底层架构的情况下，也很难在通用硬件上执行向量运算的基准比较。不过，JEP对此提供了一些指导。

## 6. 总结

尽管需要谨慎使用，但使用Vector API的好处是巨大的。性能提升和操作的简化矢量化为图形行业、大规模计算等带来了好处。我们研究了与Vector API相关的重要术语。我们还深入研究了一些代码示例。

与往常一样，示例代码可以在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-19)上找到。