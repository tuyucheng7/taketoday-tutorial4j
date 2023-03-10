## 1. 概述

Spring 6带来了一项有望优化应用程序性能的新功能：提前(AOT)编译支持。

在本文中，我们将探讨Spring 6的AOT优化功能的工作原理、它的好处以及如何使用它。

## 2. 提前编译

### 2.1 即时编译器(JIT)

对于使用最多的Java虚拟机(JVM)，如Oracle的HotSpot JVM和OpenJDK，当我们编译源代码(.java文件)时，生成的字节码存储在.class文件中。这样，**JVM使用JIT编译器将字节码转换为机器码**。

此外，JIT编译不仅涉及JVM对字节码的解释，还涉及在运行时将频繁执行的代码动态编译为本机机器代码。

### 2.2 提前编译器(AOT)

**提前(AOT)编译是一种在应用程序运行之前将字节码预编译为本机机器代码的技术**。

Java虚拟机通常不支持此功能。但是，Oracle在OpenJDK项目中为HotSpot JVM发布了一个名为“GraalVM Native Image”的实验性AOT功能，允许提前编译。

代码预编译后，计算机的处理器可以直接执行，省去了JVM解释字节码的麻烦，提高了启动时间。

在本文中，我们不会详细介绍AOT编译器。请参阅我们的另一篇文章，[了解提前编译(AOT)](https://www.baeldung.com/ahead-of-time-compilation)的概述。

## 3. Spring 6中的AOT

### 3.1 AOT优化

当我们构建Spring 6应用程序时，需要考虑三种不同的运行时选项：

-   在JRE上运行的传统Spring应用程序
-   在编译的AOT阶段生成代码并在JRE上运行
-   在编译的AOT阶段生成代码并在GraalVM本机镜像中运行

让我们考虑第二种选择，它是Spring 6的全新特性(第一种是传统构建，第二种是[原生镜像](https://www.baeldung.com/spring-native-intro))。

首先，我们需要搭建好[AOT编译的环境](https://www.graalvm.org/java/quickstart/)。

**通过AOT编译构建应用程序在性能和资源消耗方面具有多重优势**：

-   死代码消除：AOT编译器可以消除在运行时从未执行过的代码。这可以通过减少需要执行的代码量来提高性能。
-   内联：内联是一种AOT编译器用函数的实际代码替换函数调用的技术。这可以通过减少函数调用的开销来提高性能。
-   常量传播：AOT编译器通过将变量替换为它可以在编译时确定的常量值来优化性能。这消除了运行时计算的需要并提高了性能。
-   过程间优化：AOT编译器可以通过分析程序的调用图来优化跨多个函数的代码。这可以通过减少函数调用的开销和识别公共子表达式来提高性能。
-   Bean定义：Spring 6中的AOT编译器通过剔除不必要的BeanDefinition实例来提高应用程序效率。

因此，让我们使用以下命令构建具有AOT优化的应用程序：

```shell
mvn clean compile spring-boot:process-aot package
```

然后使用以下命令运行应用程序：

```shell
java -DspringAot=true -jar <jar-name>
```

我们可以设置构建插件来默认启用AOT编译：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <springAot>true</springAot>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

### 3.2 AOT优化中的问题

当我们决定使用AOT编译来构建我们的应用程序时，我们可能会遇到一些问题，例如：

-   反射：它允许代码在编译时动态调用方法和访问未知的字段。AOT编译器无法确定动态调用的类和方法。
-   属性文件：属性文件的内容可以在运行时更改。AOT编译器无法确定动态使用的属性文件。
-   代理：代理通过为其提供代理或占位符来控制对另一个对象的访问。由于代理可用于将方法调用动态重定向到其他对象，因此它会使AOT编译器难以确定将在运行时调用哪些类和方法。
-   序列化：序列化将对象的状态转换为字节流，反之亦然。总体而言，它会使AOT编译器难以确定将在运行时调用哪些类和方法。

为了确定哪些类在Spring应用程序中导致问题，我们可以运行一个提供反射操作信息的代理。

因此，让我们配置Maven插件以包含一个JVM参数来帮助解决这个问题。

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>
            -agentlib:native-image-agent=config-output-dir=target/native-image
        </jvmArguments>
        <!-- ... -->
    </configuration>
</plugin>
```

让我们使用以下命令运行它：

```shell
./mvnw -DskipTests clean package spring-boot:run
```

在`target/native-image/`中我们会找到生成的文件，如reflect-config.json、resource-config.json等。

如果在此文件中定义了某些内容，则需要定义[RuntimeHints](https://docs.spring.io/spring-framework/docs/6.0.3/javadoc-api/org/springframework/aot/hint/RuntimeHints.html)，以便正确编译可执行文件。

## 4. 总结

在本文中，我们介绍了Spring 6新的AOT优化特性。