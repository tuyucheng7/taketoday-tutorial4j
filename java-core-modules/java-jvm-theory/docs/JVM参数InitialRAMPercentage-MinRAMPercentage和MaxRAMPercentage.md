## 1. 概述

在本教程中，我们将讨论一些可用于设置 JVM 的 RAM 百分比的 JVM[参数。](https://www.baeldung.com/jvm-parameters)

在 Java 8 中引入的参数InitialRAMPercentage、MinRAMPercentage和MaxRAMPercentage有助于配置 Java 应用程序的堆大小。

## 2. -XX:初始内存百分比

InitialRAMPercentage JVM 参数允许我们配置 Java 应用程序的初始堆大小。它是物理服务器或容器总内存的百分比，作为双精度值传递。

例如，如果我们为 1 GB 全内存的物理服务器设置- XX:InitialRAMPercentage=50.0，则初始堆大小将约为 500 MB(1 GB 的 50%)。

首先，让我们检查一下 JVM 中IntialRAMPercentage的默认值：

```powershell
$ docker run openjdk:8 java -XX:+PrintFlagsFinal -version | grep -E "InitialRAMPercentage"
   double InitialRAMPercentage                      = 1.562500                            {product}

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
```

然后，让我们将 JVM 的初始堆大小设置为 50%：

```powershell
$ docker run -m 1GB openjdk:8 java -XX:InitialRAMPercentage=50.0 -XX:+PrintFlagsFinal -version | grep -E "InitialRAMPercentage"
   double InitialRAMPercentage                     := 50.000000                           {product}

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)

```

重要的是要注意，当我们配置-Xms选项时，JVM 会忽略InitialRAMPercentage。

## 3. -XX:MinRAM百分比

MinRAMPercentage参数与其名称不同，它允许为使用少量内存(小于 200MB)运行的 JVM 设置最大堆大小。

首先，我们将探讨MinRAMPercentage的默认值：

```powershell
$ docker run openjdk:8 java -XX:+PrintFlagsFinal -version | grep -E "MinRAMPercentage"
   double MinRAMPercentage                      = 50.000000                            {product}

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
```

然后，让我们使用参数来设置总内存为 100MB 的 JVM 的最大堆大小：

```powershell
$ docker run -m 100MB openjdk:8 java -XX:MinRAMPercentage=80.0 -XshowSettings:VM -version

VM settings:
    Max. Heap Size (Estimated): 77.38M
    Ergonomics Machine Class: server
    Using VM: OpenJDK 64-Bit Server VM

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)

```

此外，JVM 在为小型内存服务器/容器设置最大堆大小时会忽略MaxRAMPercentage参数：

```powershell
$ docker run -m 100MB openjdk:8 java -XX:MinRAMPercentage=80.0 -XX:MaxRAMPercentage=50.0 -XshowSettings:vm -version
VM settings:
    Max. Heap Size (Estimated): 77.38M
    Ergonomics Machine Class: server
    Using VM: OpenJDK 64-Bit Server VM

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
```

## 4. -XX:最大内存百分比

MaxRAMPercentage参数允许为使用大量内存(大于 200 MB)运行的JVM设置最大堆大小。

首先，让我们探讨一下MaxRAMPercentage的默认值：

```powershell
$ docker run openjdk:8 java -XX:+PrintFlagsFinal -version | grep -E "MaxRAMPercentage"
   double MaxRAMPercentage                      = 25.000000                            {product}

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
```

然后，我们可以使用该参数将总内存为 500 MB 的 JVM 的最大堆大小设置为 60%：

```powershell
$ docker run -m 500MB openjdk:8 java -XX:MaxRAMPercentage=60.0 -XshowSettings:vm -version
VM settings:
    Max. Heap Size (Estimated): 290.00M
    Ergonomics Machine Class: server
    Using VM: OpenJDK 64-Bit Server VM

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
```

同样，JVM 忽略大内存服务器/容器的MinRAMPercentage参数：

```powershell
$ docker run -m 500MB openjdk:8 java -XX:MaxRAMPercentage=60.0 -XX:MinRAMPercentage=30.0 -XshowSettings:vm -version
VM settings:
    Max. Heap Size (Estimated): 290.00M
    Ergonomics Machine Class: server
    Using VM: OpenJDK 64-Bit Server VM

openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b10)
```

## 5.总结

在这篇简短的文章中，我们讨论了使用 JVM 参数InitialRAMPercentage、 MinRAMPercentage和 MaxRAMPercentage来设置 JVM 将用于堆的 RAM 百分比。

首先，我们检查了 JVM 上设置的标志的默认值。然后，我们使用 JVM 参数设置初始和最大堆大小。