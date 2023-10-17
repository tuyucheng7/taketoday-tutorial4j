## 1. 概述

在这个简短的教程中，我们将学习如何获取当前 JVM 中所有正在运行的线程，包括我们类未启动的线程。

## 2.使用线程类

Thread类的getAllStackTrace()方法提供所有正在运行的线程的堆栈跟踪。它返回一个Map，其键是Thread对象，因此我们可以获得键集并简单地遍历其元素以获取有关线程的信息。

让我们使用[printf()](https://www.baeldung.com/java-printstream-printf)方法使输出更具可读性：

```java
Set<Thread> threads = Thread.getAllStackTraces().keySet();
System.out.printf("%-15s t %-15s t %-15s t %sn", "Name", "State", "Priority", "isDaemon");
for (Thread t : threads) {
    System.out.printf("%-15s t %-15s t %-15d t %sn", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
}
```

输出将如下所示：

```plaintext
Name            	 State           	 Priority        	 isDaemon
main            	 RUNNABLE        	 5               	 false
Signal Dispatcher 	 RUNNABLE        	 9               	 true
Finalizer       	 WAITING         	 8               	 true
Reference Handler 	 WAITING         	 10              	 true
```

如我们所见，除了运行主程序的线程main之外，我们还有另外三个线程。此结果可能因不同的 Java 版本而异。

让我们更多地了解这些其他线程：

-   Signal Dispatcher：这个线程处理操作系统发送给 JVM 的信号。
-   Finalizer：该线程对不再需要释放系统资源的对象执行终结。
-   Reference Handler：该线程将不再需要的对象放入队列中，由Finalizer线程处理。

如果主程序退出，所有这些线程将被终止。

## 3. 使用Apache Commons的ThreadUtils类

我们还可以使用[Apache Commons Lang库中的](https://search.maven.org/search?q=g:org.apache.commons a:commons-lang3)ThreadUtils类来实现相同的目标：

让我们向我们的pom.xml文件添加一个依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.10</version>
</dependency>
```

只需使用getAllThreads()方法获取所有正在运行的线程：

```java
System.out.printf("%-15s t %-15s t %-15s t %sn", "Name", "State", "Priority", "isDaemon");
for (Thread t : ThreadUtils.getAllThreads()) {
    System.out.printf("%-15s t %-15s t %-15d t %sn", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
}
```

输出与上面相同。

## 4。总结

总之，我们学习了两种方法来获取当前 JVM 中所有正在运行的线程。