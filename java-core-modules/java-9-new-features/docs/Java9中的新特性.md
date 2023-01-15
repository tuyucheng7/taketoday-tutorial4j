## 1. 概述

Java 9引入了丰富的特性集。虽然没有新的语言概念，但新的API和诊断命令肯定会引起开发人员的兴趣。

在这篇文章中，我们对一些新功能进行快速、高级的了解；[此处](https://openjdk.java.net/projects/jdk9/)提供了新特性的完整列表。

## 2. 模块化系统-拼图项目

让我们从最重要的开始-将模块化引入Java平台,模块化系统提供类似于OSGi框架系统的功能。模块具有依赖关系的概念，可以导出公共API并将实现细节隐藏/私有。

这里的主要动机之一是提供模块化JVM，它可以在可用内存少得多的设备上运行。JVM只能使用应用程序所需的那些模块和API运行。点击[此链接](http://cr.openjdk.java.net/~mr/jigsaw/ea/module-summary.html)以了解这些模块是什么。

此外，无法再从应用程序代码访问com.sun.等JVM内部(实现)API。

简而言之，这些模块将在位于java代码层次结构顶部的名为module-info.java的文件中进行描述：

```java
module cn.tuyucheng.taketoday.java9.modules.car {
    requires cn.tuyucheng.taketoday.java9.modules.engines;
    exports cn.tuyucheng.taketoday.java9.modules.car.handling;
}
```

我们的car模块需要engine模块来运行并导出一个包进行处理。

有关更深入的示例，请查看OpenJDK[项目拼图：模块系统快速入门指南](https://openjdk.java.net/projects/jigsaw/quick-start)。

## 3. 新的HTTP客户端

问世已久的旧HttpURLConnection替代品。新API位于java.net.http包下，

它应该支持[HTTP/2协议](https://http2.github.io/)和[WebSocket](https://en.wikipedia.org/wiki/WebSocket)握手，其性能应该可以与[Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/)、[Netty](http://netty.io/)和[Jetty](https://eclipse.org/jetty)相媲美。

让我们通过创建和发送一个简单的HTTP请求来看看这个新功能。

更新：[HTTP客户端JEP](https://openjdk.java.net/jeps/110)正在移至Incubator模块，因此它不再在包java.net.http中可用，而是在jdk.incubator.http下可用。

### 3.1 快速获取请求

API使用构建器模式，这使得它非常容易快速使用：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .GET()
    .build();

HttpResponse<String> response = HttpClient.newHttpClient()
    .send(request, HttpResponse.BodyHandler.asString());
```

## 4. Process API

Process API已针对控制和管理操作系统进程进行了改进。

### 4.1 进程信息

java.lang.ProcessHandle类包含大部分新功能：

```java
ProcessHandle self = ProcessHandle.current();
long PID = self.getPid();
ProcessHandle.Info procInfo = self.info();
 
Optional<String[]> args = procInfo.arguments();
Optional<String> cmd =  procInfo.commandLine();
Optional<Instant> startTime = procInfo.startInstant();
Optional<Duration> cpuUsage = procInfo.totalCpuDuration();
```

current方法返回一个对象，表示当前正在运行的JVM的进程。Info子类提供有关该进程的详细信息。

### 4.2 销毁进程

现在，我们可以使用destroy()停止所有正在运行的子进程：

```java
childProc = ProcessHandle.current().children();
childProc.forEach(procHandle -> {
    assertTrue("Could not kill process " + procHandle.getPid(), procHandle.destroy());
});
```

## 5. 语言方面的小修改

### 5.1 Try-With-Resources

在Java 7中，try-with-resources语法要求为语句管理的每个资源声明一个新变量。

在Java 9中引入了一个额外的改进：如果资源被final或有效的final变量引用，那么try-with-resources语句可以在不声明新变量的情况下管理资源：

```java
MyAutoCloseable mac = new MyAutoCloseable();
try (mac) {
    // do some stuff with mac
}
 
try (new MyAutoCloseable() { }.finalWrapper.finalCloseable) {
   // do some stuff with finalCloseable
} catch (Exception ex) { }
```

### 5.2 钻石运算符扩展

现在我们可以将钻石运算符与匿名内部类结合使用：

```java
FooClass<Integer> fc = new FooClass<>(1) { // anonymous inner class
};
 
FooClass<? extends Integer> fc0 = new FooClass<>(1) { 
    // anonymous inner class
};
 
FooClass<?> fc1 = new FooClass<>(1) { // anonymous inner class
};
```

### 5.3 接口私有方法

JDK 9版本中的接口可以具有私有方法，可用于拆分冗长的默认方法：

```java
interface InterfaceWithPrivateMethods {
    
    private static String staticPrivate() {
        return "static private";
    }
    
    private String instancePrivate() {
        return "instance private";
    }
    
    default void check() {
        String result = staticPrivate();
        InterfaceWithPrivateMethods pvt = new InterfaceWithPrivateMethods() {
            // anonymous class
        };
        result = pvt.instancePrivate();
    }
}}
```

## 6. JShell命令行工具

JShell是read-eval-print loop，简称REPL。

简而言之，它是一个交互式工具，用于评估Java的声明、语句和表达式，以及一个API。这对于测试小代码段非常方便，否则我们需要使用main方法创建一个新类。

jshell可执行文件本身可以在<JAVA_HOME>/bin文件夹中找到：

```java
jdk-9bin>jshell.exe
|  Welcome to JShell -- Version 9
|  For an introduction type: /help intro
jshell> "This is my long string. I want a part of it".substring(8,19);
$5 ==> "my long string"
```

交互式shell带有历史记录和自动完成功能；它还提供诸如保存和加载文件、所有或部分书面语句的功能：

```java
jshell> /save c:developJShell_hello_world.txt
jshell> /open c:developJShell_hello_world.txt
Hello JShell!
```

代码片段在文件加载时执行。

## 7. JCMD子命令

让我们探索jcmd命令行实用程序中的一些新子命令，我们将得到JVM中加载的所有类及其继承结构的列表。

在下面的示例中，我们可以看到运行Eclipse Neon的JVM中加载的java.lang.Socket的层次结构：

```java
jdk-9bin>jcmd 14056 VM.class_hierarchy -i -s java.net.Socket
14056:
java.lang.Object/null
|--java.net.Socket/null
|  implements java.io.Closeable/null (declared intf)
|  implements java.lang.AutoCloseable/null (inherited intf)
|  |--org.eclipse.ecf.internal.provider.filetransfer.httpclient4.CloseMonitoringSocket
|  |  implements java.lang.AutoCloseable/null (inherited intf)
|  |  implements java.io.Closeable/null (inherited intf)
|  |--javax.net.ssl.SSLSocket/null
|  |  implements java.lang.AutoCloseable/null (inherited intf)
|  |  implements java.io.Closeable/null (inherited intf)
```

jcmd命令的第一个参数是我们要在其上运行命令的JVM的进程ID(PID)。

另一个有趣的子命令是set_vmflag。我们可以在线修改一些JVM参数，而不需要重启JVM进程和修改它的启动参数。

你可以使用子命令jcmd 14056 VM.flags -all找出所有可用的VM.flags。

## 8. 多分辨率图像API

接口java.awt.image.MultiResolutionImage将一组具有不同分辨率的图像封装到单个对象中，我们可以根据给定的DPI指标和图像转换集检索特定于分辨率的图像变体，或者检索图像中的所有变体。

java.awt.Graphics类根据当前显示DPI指标和任何应用的转换从多分辨率图像中获取变体。

java.awt.image.BaseMultiResolutionImage类提供了基本实现：

```java
BufferedImage[] resolutionVariants = ....
MultiResolutionImage bmrImage
  = new BaseMultiResolutionImage(baseIndex, resolutionVariants);
Image testRVImage = bmrImage.getResolutionVariant(16, 16);
assertSame("Images should be the same", testRVImage, resolutionVariants[3]);
```

## 9. Variable Handles

该API位于java.lang.invoke包下，由VarHandle和MethodHandles组成。它在对象字段和数组元素上提供等效的java.util.concurrent.atomic和sun.misc.Unsafe操作，具有相似的性能。

使用Java 9模块化系统无法从应用程序代码访问sun.misc.Unsafe。

## 10. 发布-订阅框架

java.util.concurrent.Flow类提供了支持[Reactive Streams](http://www.reactive-streams.org/)发布订阅框架的接口，这些接口支持在JVM上运行的多个异步系统的互操作性。

我们可以使用工具类SubmissionPublisher来创建自定义组件。

## 11. 统一的JVM日志记录

此功能为JVM的所有组件引入了一个通用的日志记录系统。它提供了进行日志记录的基础结构，但它没有添加来自所有 JVM组件的实际日志记录调用；它也没有将日志记录添加到JDK中的Java代码中。

日志框架定义了一组标签，例如gc、编译器、线程等。我们可以使用命令行参数-Xlog在启动期间打开日志。

让我们使用'debug'级别将带有'gc'标签的消息记录到一个名为'gc.txt'的文件中，没有任何修饰：

```shell
java -Xlog:gc=debug:file=gc.txt:none ...
```

-Xlog:help将输出可能的选项和示例。可以使用jcmd命令在运行时修改日志记录配置，我们将设置GC日志为info 并将它们重定向到一个文件gc_logs：

```shell
jcmd 9615 VM.log output=gc_logs what=gc
```

## 12. 新的API

### 12.1 不可变集合

java.util.Set.of()创建给定元素的不可变集合。在Java 8中，创建一个由多个元素组成的Set需要几行代码。现在我们可以简单地做到这一点：

```java
Set<String> strKeySet = Set.of("key1", "key2", "key3");
```

该方法返回的Set是JVM内部类：java.util.ImmutableCollections.SetN，它扩展了public java.util.AbstractSet。它是不可变的，如果我们尝试添加或删除元素，则会抛出UnsupportedOperationException 。

你也可以使用相同的方法将整个数组转换为Set。

### 12.2 Optional与Stream

java.util.Optional.stream()为我们提供了一种在Optional元素上使用Streams功能的简单方法：

```java
List<String> filteredList = listOfOptionals.stream()
    .flatMap(Optional::stream)
    .collect(Collectors.toList());
```

## 13. 总结

Java 9包含带有一个模块化的JVM和许多其他新的和多样化的改进和特性。