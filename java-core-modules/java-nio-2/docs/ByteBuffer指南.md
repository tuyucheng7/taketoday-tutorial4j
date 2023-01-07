## 1. 概述

Buffer类是构建JavaNIO 的基础。但是，在这些类中，ByteBuffer类是最受欢迎的。那是因为字节类型是最通用的一种。例如，我们可以在 JVM 中使用 bytes 来组合其他非 boolean 原始类型。此外，我们可以使用字节在 JVM 和外部 I/O 设备之间传输数据。

在本教程中，我们将检查ByteBuffer类的不同方面。

## 2.字节缓冲区的创建

ByteBuffer是一个抽象类，所以我们不能直接构造一个新的 实例。但是，它提供静态工厂方法以方便创建实例。简而言之，有两种方法可以通过分配或包装来创建ByteBuffer实例：

[![字节缓冲区创建 1](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-Creation-1.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-Creation-1.png)

### 2.1. 分配

Allocation 会创建一个实例并分配一个特定容量的私有空间。准确地说，ByteBuffer类有两种分配方法：allocate和allocateDirect。

使用allocate方法，我们将获得一个非直接缓冲区——也就是说，一个带有底层字节数组的缓冲区实例：

```java
ByteBuffer buffer = ByteBuffer.allocate(10);
```

当我们使用allocateDirect方法时，它会生成一个直接缓冲区：

```java
ByteBuffer buffer = ByteBuffer.allocateDirect(10);
```

为简单起见，让我们关注非直接缓冲区并将直接缓冲区的讨论留到以后再讨论。

### 2.2. 包装

包装允许实例重用现有的字节数组：

```java
byte[] bytes = new byte[10];
ByteBuffer buffer = ByteBuffer.wrap(bytes);
```

而上面的代码等价于：

```java
ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, bytes.length);
```

对现有字节数组中的数据元素所做的任何更改都将反映在缓冲区实例中，反之亦然。

### 2.3. 洋葱模型

现在，我们知道如何获取ByteBuffer实例了。接下来我们把ByteBuffer这个类看成一个三层的洋葱模型，从里到外一层层理解：

-   数据和索引层
-   传输数据层
-   查看图层

[![字节缓冲区洋葱](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-Onion.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-Onion.png)

在最内层，我们将ByteBuffer类视为具有额外索引的字节数组的容器。在中间层，我们专注于使用ByteBuffer实例与其他数据类型进行数据传输。我们在最外层使用不同的基于缓冲区的视图检查相同的底层数据。

## 3. ByteBuffer索引

从概念上讲，ByteBuffer类是一个包装在对象中的字节数组。它提供了许多方便的方法来促进对底层数据的读取或写入操作。而且，这些方法高度依赖于维护的索引。

现在，让我们特意将ByteBuffer类简化为一个带有额外索引的字节数组容器：

```plaintext
ByteBuffer = byte array + index
```

考虑到这个概念，我们可以将与索引相关的方法分为四类：

-   基本的
-   标记并重置
-   清除、翻转、倒带和压缩
-   保持

[![字节缓冲区索引 2](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-Indices-2.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-Indices-2.png)

### 3.1. 四项基本指标

Buffer类中定义了四个索引。这些索引记录了底层数据元素的状态：

-   容量：缓冲区可以容纳的最大数据元素数
-   限制：停止读取或写入的索引
-   position：当前要读或写的索引
-   标记：记住的位置

此外，这些指标之间存在不变的关系：

```plaintext
0 <= mark <= position <= limit <= capacity
```

并且，我们应该注意到，所有与索引相关的方法都围绕这四个索引展开。

当我们创建一个新的ByteBuffer实例时，mark是未定义的，position为 0，limit等于capacity。例如，让我们分配一个包含 10 个数据元素的ByteBuffer：

```java
ByteBuffer buffer = ByteBuffer.allocate(10);
```

或者，让我们用 10 个数据元素包装一个现有的字节数组：

```java
byte[] bytes = new byte[10];
ByteBuffer buffer = ByteBuffer.wrap(bytes);
```

结果，mark将为 -1，position 将为 0，limit和capacity均为10：

```java
int position = buffer.position(); // 0
int limit = buffer.limit();       // 10
int capacity = buffer.capacity(); // 10
```

容量是只读的，不能更改。但是，我们可以使用position(int)和limit(int)方法来改变相应的position和limit：

```java
buffer.position(2);
buffer.limit(5);
```

然后，position为 2，limit为 5。

### 3.2. 标记并重置

mark ()和reset()方法让我们记住一个特定的位置并稍后返回。

当我们第一次创建一个ByteBuffer实例时，标记是未定义的。然后，我们可以调用mark()方法，将标记设置到当前位置。经过一些操作后，调用reset()方法会将位置更改回标记。

```java
ByteBuffer buffer = ByteBuffer.allocate(10); // mark = -1, position = 0
buffer.position(2);                          // mark = -1, position = 2
buffer.mark();                               // mark = 2,  position = 2
buffer.position(5);                          // mark = 2,  position = 5
buffer.reset();                              // mark = 2,  position = 2
```

需要注意一件事：如果标记未定义，则调用reset()方法将导致InvalidMarkException。

### 3.3. 清除、翻转、倒带和压缩

clear ()、flip()、rewind()和compact()方法有一些共同点和细微差别：

[![ByteBuffer clear clip rewind compact](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-clear-clip-rewind-compact-1.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-clear-clip-rewind-compact-1.png)
为了比较这些方法，让我们准备一个代码片段：

```java
ByteBuffer buffer = ByteBuffer.allocate(10); // mark = -1, position = 0, limit = 10
buffer.position(2);                          // mark = -1, position = 2, limit = 10
buffer.mark();                               // mark = 2,  position = 2, limit = 10
buffer.position(5);                          // mark = 2,  position = 5, limit = 10
buffer.limit(8);                             // mark = 2,  position = 5, limit = 8
```

clear ()方法会将limit更改为capacity，将position更改为 0，将mark更改为 -1：

```java
buffer.clear();                              // mark = -1, position = 0, limit = 10
```

flip ()方法会将limit更改为position，将position更改为 0，将mark更改为 -1：

```java
buffer.flip();                               // mark = -1, position = 0, limit = 5
```

rewind ()方法保持limit不变，并将位置更改为 0，将标记更改为 -1：

```java
buffer.rewind();                             // mark = -1, position = 0, limit = 8
```

compact ()方法会将limit更改为capacity，将position更改为 remaining ( limit – position )，并将mark更改为 -1：

```java
buffer.compact();                            // mark = -1, position = 3, limit = 10
```

以上四种方法都有各自的用例：

-   要重用缓冲区，clear()方法很方便。它将索引设置为初始状态并准备好进行新的写入操作。
-   调用flip()方法后，缓冲区实例从写入模式切换到读取模式。但是，我们应该避免调用flip()方法两次。这是因为第二次调用会将限制设置为0，并且无法读取任何数据元素。
-   如果我们想多次读取底层数据，rewind()方法就派上用场了。
-   compact ()方法适用于缓冲区的部分重用。例如，假设我们想要读取一些但不是全部的底层数据，然后我们想要将数据写入缓冲区。compact ()方法会将未读数据到缓冲区的开头，并更改缓冲区索引以准备写入操作。

### 3.4. 保持

hasRemaining ()和remaining()方法计算限制和位置的关系：

[![ByteBuffer 保留](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-remain.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-remain.png)

当limit大于position时，hasRemaining()将返回true。此外，remaining()方法返回limit和position之间的差值。

例如，如果缓冲区的位置为 2，限制为 8，则其剩余将为 6：

```java
ByteBuffer buffer = ByteBuffer.allocate(10); // mark = -1, position = 0, limit = 10
buffer.position(2);                          // mark = -1, position = 2, limit = 10
buffer.limit(8);                             // mark = -1, position = 2, limit = 8
boolean flag = buffer.hasRemaining();        // true
int remaining = buffer.remaining();          // 6
```

## 4.传输数据

洋葱模型的第二层与传输数据有关。具体来说，ByteBuffer类提供了从/向其他数据类型(byte、char、short、int、long、float和double)传输数据的方法：

[![ByteBuffer 传输数据](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-transfer-data.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-transfer-data.png)

### 4.1. 传输字节数据

为了传输字节数据，ByteBuffer类提供了单个和批量操作。

我们可以在单个操作中从缓冲区的基础数据读取或写入单个字节。这些操作包括：

```java
public abstract byte get();
public abstract ByteBuffer put(byte b);
public abstract byte get(int index);
public abstract ByteBuffer put(int index, byte b);
```

我们可能会从上述方法中注意到get() / put()方法的两个版本：一个没有参数，另一个接受index。那么，有什么区别呢？

不带索引的是相对操作，对当前位置的数据元素进行操作，然后将位置加1。而带索引的是整体操作，对索引处的数据元素进行操作，不会改变立场。

相反，批量操作可以从缓冲区的基础数据读取或写入多个字节。这些操作包括：

```java
public ByteBuffer get(byte[] dst);
public ByteBuffer get(byte[] dst, int offset, int length);
public ByteBuffer put(byte[] src);
public ByteBuffer put(byte[] src, int offset, int length);
```

以上方法均属于相关操作。也就是说，他们将分别从/到当前位置读取或写入并更改位置值。

还有另一个put()方法，它接受一个ByteBuffer参数：

```java
public ByteBuffer put(ByteBuffer src);
```

### 4.2. 传输int数据

除了读写字节数据，ByteBuffer类还支持除boolean类型之外的其他基本类型。我们以int类型为例。相关方法包括：

```java
public abstract int getInt();
public abstract ByteBuffer putInt(int value);
public abstract int getInt(int index);
public abstract ByteBuffer putInt(int index, int value);
```

同样，带索引参数的getInt()和putInt()方法是绝对操作，否则是相对操作。

## 5. 不同的观点

洋葱模型的第三层是关于从不同的角度读取相同的底层数据。

[![字节缓冲区视图](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-view.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-view.png)
上图中的每个方法都会生成一个新视图，该视图与原始缓冲区共享相同的底层数据。要理解一个新的观点，我们应该关注两个问题：

-   新视图将如何解析底层数据？
-   新视图将如何记录其索引？

### 5.1. 字节缓冲区视图

要将一个ByteBuffer实例作为另一个ByteBuffer视图读取，它具有三个方法：duplicate()、slice()和asReadOnlyBuffer()。

让我们看一下这些差异的示例：

```java
ByteBuffer buffer = ByteBuffer.allocate(10); // mark = -1, position = 0, limit = 10, capacity = 10
buffer.position(2);                          // mark = -1, position = 2, limit = 10, capacity = 10
buffer.mark();                               // mark = 2,  position = 2, limit = 10, capacity = 10
buffer.position(5);                          // mark = 2,  position = 5, limit = 10, capacity = 10
buffer.limit(8);                             // mark = 2,  position = 5, limit = 8,  capacity = 10
```

duplicate ()方法创建一个新的ByteBuffer实例，就像原始实例一样。但是，这两个缓冲区中的每一个都有其独立的limit、position和mark：

```java
ByteBuffer view = buffer.duplicate();        // mark = 2,  position = 5, limit = 8,  capacity = 10
```

slice ()方法创建底层数据的共享子视图。视图的位置将为 0，其限制和容量将为原始缓冲区的剩余部分：

```java
ByteBuffer view = buffer.slice();            // mark = -1, position = 0, limit = 3,  capacity = 3
```

与duplicate()方法相比，asReadOnlyBuffer()方法的工作原理类似，但会生成一个只读缓冲区。这意味着我们不能使用这个只读视图来更改底层数据：

```java
ByteBuffer view = buffer.asReadOnlyBuffer(); // mark = 2,  position = 5, limit = 8,  capacity = 10
```

### 5.2. 其他视图

ByteBuffer还提供其他视图：asCharBuffer()、asShortBuffer()、asIntBuffer() 、asLongBuffer()、asFloatBuffer ()和asDoubleBuffer()。这些方法类似于slice()方法，即它们提供与底层数据的当前位置和限制相对应的切片视图。它们之间的主要区别在于将基础数据解释为其他原始类型值。

我们应该关心的问题是：

-   如何解读基础数据
-   从哪里开始解释
-   新生成的视图中将显示多少元素

新视图将多个字节组成目标原始类型，并从原始缓冲区的当前位置开始解释。新视图的容量等于原始缓冲区中剩余元素的数量除以构成视图原始类型的字节数。末尾的任何剩余字节在视图中都将不可见。

现在，让我们以asIntBuffer()为例：

```java
byte[] bytes = new byte[]{
  (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE, // CAFEBABE ---> cafebabe
  (byte) 0xF0, (byte) 0x07, (byte) 0xBA, (byte) 0x11, // F007BA11 ---> football
  (byte) 0x0F, (byte) 0xF1, (byte) 0xCE               // 0FF1CE   ---> office
};
ByteBuffer buffer = ByteBuffer.wrap(bytes);
IntBuffer intBuffer = buffer.asIntBuffer();
int capacity = intBuffer.capacity();                         // 2
```

上面的代码片段中，buffer有11个数据元素，int类型占4个字节。因此，intBuffer将有 2 个数据元素 (11 / 4 = 2) 并省去额外的 3 个字节 (11 % 4 = 3)。

## 6.直接缓冲

什么是直接缓冲区？直接缓冲区是指缓冲区的底层数据分配在操作系统功能可以直接访问它的内存区域。非直接缓冲区是指底层数据为字节数组的缓冲区，分配在Java堆区。

那么，我们如何创建直接缓冲区呢？通过调用具有所需容量的allocateDirect()方法来创建直接的ByteBuffer ：

```java
ByteBuffer buffer = ByteBuffer.allocateDirect(10);
```

为什么我们需要直接缓冲区？答案很简单：非直接缓冲区总是会引发不必要的操作。将非直接缓冲区的数据发送到 I/O 设备时，本机代码必须“锁定”底层字节数组，将其到Java堆之外，然后调用 OS 函数刷新数据。但是，本机代码可以直接访问底层数据并调用 OS 函数来刷新数据，而无需使用直接缓冲区的任何额外开销。

鉴于上述情况，直接缓冲区是否完美？不，主要问题是分配和释放直接缓冲区的成本很高。那么，实际上，直接缓冲区是否总是比非直接缓冲区运行得更快？不必要。这是因为有很多因素在起作用。而且，性能权衡因 JVM、操作系统和代码设计而异。

最后，有一个实用的软件格言可以遵循：首先，让它工作，然后让它更快。这意味着，让我们首先关注代码的正确性。如果代码运行速度不够快，那我们就做相应的优化吧。

## 7.杂项

ByteBuffer类还提供了一些辅助方法：

[![ByteBuffer杂项](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-miscellaneous.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ByteBuffer-miscellaneous.png)

### 7.1. 相关方法

isDirect ()方法可以告诉我们缓冲区是直接缓冲区还是非直接缓冲区。请注意，包装缓冲区——那些使用wrap()方法创建的缓冲区——始终是非直接的。

所有缓冲区都是可读的，但并非所有缓冲区都是可写的。isReadOnly ()方法指示我们是否可以写入底层数据。

比较这两个方法，isDirect ()方法关心的是底层数据存在于何处，是在Java堆中还是在内存区域中。但是，isReadOnly ()方法关心底层数据元素是否可以更改。

如果原始缓冲区是直接缓冲区或只读缓冲区，则新生成的视图将继承这些属性。

### 7.2. 数组相关方法

如果ByteBuffer实例是直接的或只读的，我们就无法获取其底层字节数组。但是，如果缓冲区是非直接的且不是只读的，那并不一定意味着它的底层数据是可访问的。

准确地说，hasArray ()方法可以告诉我们缓冲区是否有可访问的后备数组。如果hasArray()方法返回true，那么我们可以使用 array()和arrayOffset()方法来获取更多相关信息。

### 7.3. 字节顺序

默认情况下， ByteBuffer类的字节顺序始终为ByteOrder.BIG_ENDIAN。并且，我们可以使用order()和order(ByteOrder)方法分别获取和设置当前字节顺序。

字节顺序影响如何解释底层数据。例如，假设我们有一个缓冲区实例：

```java
byte[] bytes = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
ByteBuffer buffer = ByteBuffer.wrap(bytes);
```

使用ByteOrder.BIG_ENDIAN，val将为 -889275714 (0xCAFEBABE)：

```java
buffer.order(ByteOrder.BIG_ENDIAN);
int val = buffer.getInt();
```

但是，使用ByteOrder.LITTLE_ENDIAN时，val将为 -1095041334 (0xBEBAFECA)：

```java
buffer.order(ByteOrder.LITTLE_ENDIAN);
int val = buffer.getInt();
```

### 7.4. 比较

ByteBuffer类提供了equals()和compareTo()方法来比较两个缓冲区实例。这两种方法都根据[position, limit)范围内的剩余数据元素执行比较。

例如，具有不同底层数据和索引的两个缓冲区实例可以相等：

```java
byte[] bytes1 = "World".getBytes(StandardCharsets.UTF_8);
byte[] bytes2 = "HelloWorld".getBytes(StandardCharsets.UTF_8);

ByteBuffer buffer1 = ByteBuffer.wrap(bytes1);
ByteBuffer buffer2 = ByteBuffer.wrap(bytes2);
buffer2.position(5);

boolean equal = buffer1.equals(buffer2); // true
int result = buffer1.compareTo(buffer2); // 0
```

## 八、总结

在本文中，我们尝试将ByteBuffer类视为洋葱模型。首先，我们将其简化为一个带有额外索引的字节数组容器。然后，我们讨论了如何使用ByteBuffer类从/向其他数据类型传输数据。

接下来，我们以不同的视角查看相同的底层数据。最后，我们讨论了直接缓冲区和一些不同的方法。