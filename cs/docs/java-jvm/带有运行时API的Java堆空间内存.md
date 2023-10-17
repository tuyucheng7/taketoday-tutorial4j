## 1. 概述

在本文中，我们将讨论 Java 提供的 API，这些 API 可以帮助我们了解与[Java 堆空间](https://www.baeldung.com/java-stack-heap)相关的几个方面。

这对于了解 JVM 的当前内存状态并将其外包给监视服务(例如[StatsD](https://www.datadoghq.com/blog/statsd/) 和[Datadog](https://docs.datadoghq.com/developers/dogstatsd/) )非常有用，这些服务随后可以配置为采取先发制人的操作并避免应用程序故障。

## 2.访问内存参数

每个 Java 应用程序都有一个java.lang.Runtime实例，可以帮助我们了解应用程序当前的内存状态。可以调用Runtime#getRuntime静态方法来获取单例运行时实例。

### 2.1. 总内存

Runtime#getTotalMemory方法返回 JVM 当前保留的总堆空间(以字节为单位)。它包括为当前和未来对象保留的内存。因此，不能保证它在程序执行期间保持不变，因为随着分配更多对象，Java 堆空间可以扩展或减少。

此外，此值不一定是正在使用的值或可用的最大内存。

### 2.2. 空闲内存

Runtime#freeMemory方法返回可用于新对象分配的空闲堆空间(以字节为单位)。它可能会由于垃圾收集操作而增加，之后有更多可用内存可用。

### 2.3. 最大内存

Runtime#maxMemory方法返回 JVM 将尝试使用的最大内存。一旦 JVM 内存使用量达到这个值，它就不会分配更多的内存，而是会更频繁地进行垃圾收集。

如果即使在垃圾收集器运行后 JVM 对象仍然需要更多内存，那么 JVM 可能会抛出java.lang.OutOfMemoryErro运行时异常。

## 3.例子

在下面的示例中，我们初始化一个ArrayList并向其添加元素，同时使用上述三种方法跟踪 JVM 堆空间：

```java
ArrayList<Integer> arrayList = new ArrayList<>();
System.out.println("i t Free Memory t Total Memory t Max Memory");
for (int i = 0; i < 1000000; i++) {
    arrayList.add(i);
    System.out.println(i + " t " + Runtime.getRuntime().freeMemory() + 
      " t t " + Runtime.getRuntime().totalMemory() + 
      " t t " + Runtime.getRuntime().maxMemory());
}

// ...
Output:
i 	   Free Memory 	   Total Memory 	 Max Memory
0 	     254741016 	 	 257425408 	 	 3817865216
1 	     254741016 	 	 257425408 	 	 3817865216
...
1498 	 254741016 	 	 257425408 	 	 3817865216
1499 	 253398840 	 	 257425408 	 	 3817865216
1500 	 253398840 	 	 257425408 	 	 3817865216
...
900079 	 179608120 	 	 260046848 	 	 3817865216
900080 	 302140152 	 	 324534272 	 	 3817865216
900081 	 302140152 	 	 324534272 	 	 3817865216
...
```

-   第 1498 行：当在 Java 堆中分配足够的空间时，Runtime#freeMemory值会减少。
-   第 900080 行：此时，JVM 有更多可用空间，因为 GC 已运行，因此Runtime#freeMemory和Runtime#totalMemory的值增加。

上面显示的值预计在每次运行 Java 应用程序时都会有所不同。

## 4.自定义内存参数

[我们可以通过在运行 Java 程序时将自定义值设置为某些标志来覆盖JVM 内存参数](https://www.baeldung.com/jvm-parameters)的默认值，以实现所需的内存性能：

-   -Xms：分配给-Xms标志的值设置 Java 堆的初始值和最小值。它可以用于我们的应用程序在启动 JVM 时需要比默认最小内存更多的情况
-   -Xmx：同样，我们可以通过将堆空间分配给-Xmx标志来设置堆空间的最大值。当我们想要有意限制应用程序将使用的内存量时，可以使用它。

另请注意，-Xms值需要等于或小于-Xmx值。

### 4.1. 用法

```shell
java -Xms32M -Xmx64M Main                                                                                        
Free Memory   : 31792664 bytes
Total Memory  : 32505856 bytes
Max Memory    : 59768832 bytes

java -Xms64M -Xmx64M Main
Free Memory   : 63480640 bytes
Total Memory  : 64487424 bytes
Max Memory    : 64487424 bytes

java -Xms64M -Xmx32M Main                                                                                        
Error occurred during initialization of VM
Initial heap size set to a larger value than the maximum heap size
```

## 5.总结

在本文中，我们了解了如何通过Runtime类检索 JVM 内存指标。[这些方法在调查JVM 内存泄漏](https://www.baeldung.com/java-memory-leaks)和其他与 JVM 内存性能相关的问题时很有用。

我们还展示了如何为某些标志分配自定义值，从而导致不同场景的不同 JVM 内存行为。