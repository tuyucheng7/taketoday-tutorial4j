## 1. 概述

Java对象驻留在堆上。然而，这有时会导致内存**使用效率低下、性能低下和垃圾回收错误等问题**。在这些情况下，本地内存可以更高效，但使用本地内存传统上非常困难且容易出错。

Java 14引入了外部内存访问API**以更安全、更高效地访问本地内存**。

在本教程中，我们将介绍此API。

## 2. 动机

有效地使用内存一直是一项具有挑战性的任务，这主要是由于对内存及其组织结构、复杂的内存寻址技术认识不足等因素造成的。

例如，不正确实现的内存缓存可能会导致频繁的垃圾收集，这会大大降低应用程序的性能。

在Java中引入外部内存访问API之前，Java中访问本地内存的方式主要有两种：这些是**java.nio.ByteBuffer**和**sun.misc.Unsafe**类。

让我们快速浏览一下这些API的优缺点。

### 2.1 ByteBuffer API

ByteBuffer API**允许创建直接的堆外字节缓冲区**，这些缓冲区可以直接从Java程序访问，但是存在一些限制：

-   缓冲区大小不能超过2GB
-   垃圾收集器负责内存回收

此外，不正确地使用ByteBuffer会导致内存泄漏和OutOfMemory错误，这是因为未使用的内存引用可能会阻止垃圾收集器释放内存。

### 2.2 Unsafe API

由于其寻址模型，[Unsafe API](https://www.baeldung.com/java-unsafe)非常高效。然而，顾名思义，这个API是不安全的，并且有几个缺点：

-   **它经常允许Java程序由于非法内存使用而使JVM崩溃**
-   这是一个非标准的Java API

### 2.3 对新API的需求

总之，访问外部内存对我们来说是一个进退两难的问题。我们是否应该使用安全但受限的ByteBuffer？还是我们应该冒险使用不受支持且危险的Unsafe API？

因此，新的外部内存访问API旨在解决这些问题。

## 3. 外部内存API

外部内存访问API提供一个受支持的、安全且高效的API来访问堆内存和本地内存。它建立在三个主要的抽象之上：

-   MemorySegment：对连续的内存区域进行建模
-   MemoryAddress：内存段中的位置
-   MemoryLayout：一种以语言中立的方式定义内存段布局的方法

### 3.1 MemorySegment

内存段是**连续的内存区域**，这可以是堆内存或堆外内存，并且有几种方法可以获得内存段。

由本地内存支持的内存段称为本地内存段，它是使用重载的allocateNative方法之一创建的。

让我们创建一个200字节的本地内存段：

```java
MemorySegment memorySegment = MemorySegment.allocateNative(200);
```

内存段也可以由现有的堆分配的Java数组支持。例如，我们可以从long数组创建一个数组内存段：

```java
MemorySegment memorySegment = MemorySegment.ofArray(new long[100]);
```

此外，内存段可以由现有的Java ByteBuffer支持，这被称为缓冲内存段：

```java
MemorySegment memorySegment = MemorySegment.ofByteBuffer(ByteBuffer.allocateDirect(200));
```

或者，我们可以使用内存映射文件，这称为映射内存段。让我们使用具有读写访问权限的文件路径定义一个200字节的内存段：

```java
MemorySegment memorySegment = MemorySegment.mapFromPath(Path.of("/tmp/memory.txt"), 200, FileChannel.MapMode.READ_WRITE);
```

**内存段附加到特定线程**。因此，如果任何其他线程需要访问内存段，它必须使用acquire方法获得访问权限。

此外，内存段在内存访问方面具有空间和时间边界：

-   **空间边界**：内存段有下限和上限
-   **时间边界**：管理创建、使用和关闭内存段

空间和时间检查共同确保了JVM的安全性。

### 3.2 MemoryAddress

**内存地址是内存段内的偏移量**，它通常使用baseAddress方法获取：

```java
MemoryAddress address = MemorySegment.allocateNative(100).baseAddress();
```

内存地址用于执行操作，例如从底层内存段上的内存中检索数据。

### 3.3 MemoryLayout

**MemoryLayout类允许我们描述内存段的内容**。具体来说，它允许我们定义如何将内存分解为元素，其中提供了每个元素的大小。

这有点像将内存布局描述为具体类型，但没有提供Java类。这类似于C++等语言将其结构映射到内存的方式。

让我们举一个用坐标x和y定义的笛卡尔坐标点的例子：

```java
int numberOfPoints = 10;
MemoryLayout pointLayout = MemoryLayout.ofStruct(
    MemoryLayout.ofValueBits(32, ByteOrder.BIG_ENDIAN).withName("x"),
    MemoryLayout.ofValueBits(32, ByteOrder.BIG_ENDIAN).withName("y")
);
SequenceLayout pointsLayout = 
    MemoryLayout.ofSequence(numberOfPoints, pointLayout);
```

在这里，我们定义了一个由两个名为x和y的32位值组成的布局。该布局可以与SequenceLayout一起使用以创建类似于数组的内容，在本例中为10个索引。

## 4. 使用本地内存

### 4.1 MemoryHandles

MemoryHandles类允许我们构造VarHandles，**而VarHandle允许访问内存段**：

```java
long value = 10;
MemoryAddress memoryAddress = MemorySegment.allocateNative(8).baseAddress();
VarHandle varHandle = MemoryHandles.varHandle(long.class, ByteOrder.nativeOrder());
varHandle.set(memoryAddress, value);
 
assertThat(varHandle.get(memoryAddress), is(value));
```

在上面的示例中，我们创建了一个8字节的MemorySegment，我们需要8个字节来表示内存中的一个long值。然后，我们使用VarHandle来存储和检索它。

### 4.2 使用带偏移量的MemoryHandles

我们还可以将偏移量与MemoryAddress结合使用来访问内存段，这类似于使用索引从数组中获取元素：

```java
VarHandle varHandle = MemoryHandles.varHandle(int.class, ByteOrder.nativeOrder());
try (MemorySegment memorySegment = MemorySegment.allocateNative(100)) {
    MemoryAddress base = memorySegment.baseAddress();
    for(int i=0; i<25; i++) {
        varHandle.set(base.addOffset((i * 4)), i);
    }
    for(int i=0; i<25; i++) {
        assertThat(varHandle.get(base.addOffset((i * 4))), is(i));
    }
}
```

在上面的例子中，我们将整数0到24存储在内存段中。

首先，我们创建一个100字节的MemorySegment，这是因为在Java中，每个整数占用4个字节。因此，要存储25个整数值，我们需要100个字节(4 * 25)。

为了访问每个索引，我们在基地址上使用addOffset将varHandle设置为指向右偏移。

### 4.3 MemoryLayouts

**MemoryLayouts类定义了各种有用的布局常量**。

例如，在前面的示例中，我们创建了一个SequenceLayout：

```java
SequenceLayout sequenceLayout = MemoryLayout.ofSequence(25, MemoryLayout.ofValueBits(64, ByteOrder.nativeOrder()));
```

这可以使用JAVA_LONG常量更简单地表示：

```java
SequenceLayout sequenceLayout = MemoryLayout.ofSequence(25, MemoryLayouts.JAVA_LONG);
```

### 4.4 ValueLayout

**ValueLayout为基本数据类型(如整数和浮点类型)建模内存布局**。每个ValueLayout都有一个大小和一个字节顺序，我们可以使用ofValueBits方法创建一个ValueLayout：

```java
ValueLayout valueLayout = MemoryLayout.ofValueBits(32, ByteOrder.nativeOrder());
```

### 4.5 SequenceLayout

**SequenceLayout表示给定布局的重复**。换句话说，这可以被认为是类似于具有已定义元素布局的数组的元素序列。

例如，我们可以为每个64位的25个元素创建一个序列布局：

```java
SequenceLayout sequenceLayout = MemoryLayout.ofSequence(25, MemoryLayout.ofValueBits(64, ByteOrder.nativeOrder()));
```

### 4.6 GroupLayout

**一个GroupLayout可以组合多个成员布局**，成员布局可以是相似类型或不同类型的组合。

有两种可能的方法来定义组布局。例如，当成员布局依次组织时，它被定义为一个结构；另一方面，如果成员布局从相同的起始偏移量开始布局，则称为联合。

下面我们用一个整数和一个长整数创建一个结构类型的GroupLayout：

```java
GroupLayout groupLayout = MemoryLayout.ofStruct(MemoryLayouts.JAVA_INT, MemoryLayouts.JAVA_LONG);
```

我们还可以使用ofUnion方法创建联合类型的GroupLayout：

```java
GroupLayout groupLayout = MemoryLayout.ofUnion(MemoryLayouts.JAVA_INT, MemoryLayouts.JAVA_LONG);
```

其中第一个是包含每种类型的一种结构。并且，第二个是可以包含一种类型或另一种类型的结构。

组布局允许我们创建由多个元素组成的复杂内存布局。例如：

```java
MemoryLayout memoryLayout1 = MemoryLayout.ofValueBits(32, ByteOrder.nativeOrder());
MemoryLayout memoryLayout2 = MemoryLayout.ofStruct(MemoryLayouts.JAVA_LONG, MemoryLayouts.PAD_64);
MemoryLayout.ofStruct(memoryLayout1, memoryLayout2);
```

## 5. 切片内存段

我们可以将一个内存段分割成多个更小的块，如果我们想存储具有不同布局的值，这避免了我们必须分配多个块：

```java
MemoryAddress memoryAddress = MemorySegment.allocateNative(12).baseAddress();
MemoryAddress memoryAddress1 = memoryAddress.segment().asSlice(0,4).baseAddress();
MemoryAddress memoryAddress2 = memoryAddress.segment().asSlice(4,4).baseAddress();
MemoryAddress memoryAddress3 = memoryAddress.segment().asSlice(8,4).baseAddress();

VarHandle intHandle = MemoryHandles.varHandle(int.class, ByteOrder.nativeOrder());
intHandle.set(memoryAddress1, Integer.MIN_VALUE);
intHandle.set(memoryAddress2, 0);
intHandle.set(memoryAddress3, Integer.MAX_VALUE);

assertThat(intHandle.get(memoryAddress1), is(Integer.MIN_VALUE));
assertThat(intHandle.get(memoryAddress2), is(0));
assertThat(intHandle.get(memoryAddress3), is(Integer.MAX_VALUE));
```

## 6. 总结

在本文中，我们了解了Java 14中新的外部内存访问API。

首先，我们介绍了对外部内存访问的需求以及Java 14之前的API的局限性。然后，我们了解了外部内存访问API如何成为访问堆内存和非堆内存的安全抽象。

最后，我们探讨了如何使用API在堆上和堆外读取和写入数据。