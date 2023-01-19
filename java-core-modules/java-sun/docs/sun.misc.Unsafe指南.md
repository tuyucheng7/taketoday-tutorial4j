## 1. 概述

在本文中，我们将了解 JRE 提供的一个有趣的类——来自sun.misc包的Unsafe。此类为我们提供了低级机制，这些机制旨在仅供核心Java库使用，而不供标准用户使用。

这为我们提供了主要为核心库内部使用而设计的低级机制。

## 2.获取不安全实例 

首先，为了能够使用Unsafe类，我们需要获取一个实例——鉴于该类仅供内部使用而设计，这并不简单。

获取实例的方式是通过静态方法getUnsafe()。需要注意的是，默认情况下 - 这将抛出 SecurityException 。

幸运的是，我们可以使用反射获取实例：

```java
Field f = Unsafe.class.getDeclaredField("theUnsafe");
f.setAccessible(true);
unsafe = (Unsafe) f.get(null);
```

## 3. 使用Unsafe实例化一个类

假设我们有一个带有构造函数的简单类，该构造函数在创建对象时设置变量值：

```java
class InitializationOrdering {
    private long a;

    public InitializationOrdering() {
        this.a = 1;
    }

    public long getA() {
        return this.a;
    }
}
```

当我们使用构造函数初始化该对象时，getA()方法将返回值 1：

```java
InitializationOrdering o1 = new InitializationOrdering();
assertEquals(o1.getA(), 1);
```

但是我们可以使用Unsafe 的allocateInstance()方法。它只会为我们的类分配内存，不会调用构造函数：

```java
InitializationOrdering o3 
  = (InitializationOrdering) unsafe.allocateInstance(InitializationOrdering.class);
 
assertEquals(o3.getA(), 0);
```

请注意，构造函数未被调用，因此，getA()方法返回了long类型的默认值——即 0。

## 4. 改变私人领域

假设我们有一个包含秘密私有值的类：

```java
class SecretHolder {
    private int SECRET_VALUE = 0;

    public boolean secretIsDisclosed() {
        return SECRET_VALUE == 1;
    }
}
```

使用Unsafe 的putInt()方法，我们可以更改私有SECRET_VALUE字段的值，从而更改/破坏该实例的状态：

```java
SecretHolder secretHolder = new SecretHolder();

Field f = secretHolder.getClass().getDeclaredField("SECRET_VALUE");
unsafe.putInt(secretHolder, unsafe.objectFieldOffset(f), 1);

assertTrue(secretHolder.secretIsDisclosed());
```

一旦我们通过反射调用获得一个字段，我们就可以使用Unsafe将其值更改为任何其他int值。

## 5.抛出异常

编译器不会以与常规Java代码相同的方式检查通过Unsafe调用的代码。我们可以使用throwException()方法抛出任何异常而不限制调用者处理该异常，即使它是一个已检查的异常：

```java
@Test(expected = IOException.class)
public void givenUnsafeThrowException_whenThrowCheckedException_thenNotNeedToCatchIt() {
    unsafe.throwException(new IOException());
}
```

抛出一个IOException 后，它被检查，我们不需要捕获它，也不需要在方法声明中指定它。

## 6. 堆外内存

如果一个应用程序用完了 JVM 上的可用内存，我们最终可能会迫使 GC 进程过于频繁地运行。理想情况下，我们需要一个特殊的内存区域，堆外且不受 GC 进程控制。

Unsafe类的allocateMemory ()方法使我们能够从堆上分配巨大的对象，这意味着GC 和 JVM 不会看到和考虑此内存。

这可能非常有用，但我们需要记住，需要手动管理此内存，并在不再需要时使用freeMemory()正确回收。

假设我们要创建大型堆外内存字节数组。我们可以使用allocateMemory()方法来实现：

```java
class OffHeapArray {
    private final static int BYTE = 1;
    private long size;
    private long address;

    public OffHeapArray(long size) throws NoSuchFieldException, IllegalAccessException {
        this.size = size;
        address = getUnsafe().allocateMemory(size  BYTE);
    }

    private Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    public void set(long i, byte value) throws NoSuchFieldException, IllegalAccessException {
        getUnsafe().putByte(address + i  BYTE, value);
    }

    public int get(long idx) throws NoSuchFieldException, IllegalAccessException {
        return getUnsafe().getByte(address + idx  BYTE);
    }

    public long size() {
        return size;
    }
    
    public void freeMemory() throws NoSuchFieldException, IllegalAccessException {
        getUnsafe().freeMemory(address);
    }
}
```

在 OffHeapArray 的构造函数中，我们正在初始化给定大小的数组。我们将数组的起始地址存储在地址字段中。set ()方法获取将存储在数组中的索引和给定值。get ()方法使用其索引(距数组起始地址的偏移量)检索字节值。

接下来，我们可以使用其构造函数分配堆外数组：

```java
long SUPER_SIZE = (long) Integer.MAX_VALUE  2;
OffHeapArray array = new OffHeapArray(SUPER_SIZE);
```

我们可以将 N 个字节值放入此数组，然后检索这些值，将它们相加以测试我们的寻址是否正确：

```java
int sum = 0;
for (int i = 0; i < 100; i++) {
    array.set((long) Integer.MAX_VALUE + i, (byte) 3);
    sum += array.get((long) Integer.MAX_VALUE + i);
}

assertEquals(array.size(), SUPER_SIZE);
assertEquals(sum, 300);
```

最后，我们需要通过调用freeMemory() 将内存释放回操作系统。

## 7.CompareAndSwap操作_

来自java.concurrent包的非常高效的构造，如AtomicInteger，正在使用Unsafe底层的compareAndSwap()方法，以提供最佳性能。与Java中的标准悲观同步机制相比，这种结构广泛用于无锁算法中，这些算法可以利用 CAS 处理器指令提供极大的加速。

我们可以使用Unsafe的compareAndSwapLong()方法构建基于 CAS 的计数器：

```java
class CASCounter {
    private Unsafe unsafe;
    private volatile long counter = 0;
    private long offset;

    private Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    public CASCounter() throws Exception {
        unsafe = getUnsafe();
        offset = unsafe.objectFieldOffset(CASCounter.class.getDeclaredField("counter"));
    }

    public void increment() {
        long before = counter;
        while (!unsafe.compareAndSwapLong(this, offset, before, before + 1)) {
            before = counter;
        }
    }

    public long getCounter() {
        return counter;
    }
}
```

在CASCounter构造函数中，我们获取计数器字段的地址，以便稍后在increment()方法中使用它。该字段需要声明为 volatile，以便对写入和读取该值的所有线程可见。我们正在使用objectFieldOffset()方法来获取偏移字段的内存地址。

这个类最重要的部分是increment()方法。我们在while循环中使用compareAndSwapLong()来增加之前获取的值，检查之前的值在我们获取它之后是否发生了变化。

如果是这样，那么我们将重试该操作，直到我们成功为止。这里没有阻塞，这就是为什么这被称为无锁算法。

我们可以通过增加多个线程的共享计数器来测试我们的代码：

```java
int NUM_OF_THREADS = 1_000;
int NUM_OF_INCREMENTS = 10_000;
ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
CASCounter casCounter = new CASCounter();

IntStream.rangeClosed(0, NUM_OF_THREADS - 1)
  .forEach(i -> service.submit(() -> IntStream
    .rangeClosed(0, NUM_OF_INCREMENTS - 1)
    .forEach(j -> casCounter.increment())));
```

接下来，为了断言计数器的状态是正确的，我们可以从中获取计数器值：

```java
assertEquals(NUM_OF_INCREMENTS  NUM_OF_THREADS, casCounter.getCounter());
```

## 8. 停车/下车

JVM 使用Unsafe API中的两个有趣的方法来上下文切换线程。当线程正在等待某个动作时，JVM 可以使用Unsafe类中的park()方法使该线程阻塞。

它与Object.wait()方法非常相似，但它调用本机操作系统代码，从而利用一些体系结构细节来获得最佳性能。

当线程被阻塞并需要再次运行时，JVM 使用unpark()方法。我们经常会在线程转储中看到这些方法调用，尤其是在使用线程池的应用程序中。

## 9.总结

在本文中，我们研究了Unsafe类及其最有用的构造。

我们看到了如何访问私有字段，如何分配堆外内存，以及如何使用比较和交换结构来实现无锁算法。