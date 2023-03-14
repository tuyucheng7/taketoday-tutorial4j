## 1. 概述

尽管Java是平台无关的，但有时我们不得不使用本地库。在这些情况下，我们可能需要识别底层平台并在启动时加载适当的本机库。

在本教程中，我们将学习检查Java程序是在[64 位还是 32 位 JVM](https://www.baeldung.com/cs/32-bit-vs-64-bit-os)上运行的不同方法。

首先，我们将展示如何使用System类实现此目的。

然后，我们将了解如何使用[Java Native Access](https://github.com/java-native-access/jna) (JNA) API 检查 JVM 的位数。JNA 是一个社区开发的库，支持所有本机访问。

## 2. 使用sun.arch.data.model系统属性

Java 中的System类提供对外部定义的属性和环境变量的访问。它维护一个Properties对象，该对象描述当前工作环境的配置。

我们可以使用“ sun.arch.data.model ”系统属性来识别 JVM 位数：

```java
System.getProperty("sun.arch.data.model");

```

它包含“32”或“64”，分别表示 32 位或 64 位 JVM。尽管这种方法易于使用，但如果该属性不存在，它会返回“未知”。因此，它只适用于 OracleJava版本。

让我们看看代码：

```java
public class JVMBitVersion {
    public String getUsingSystemClass() {
        return System.getProperty("sun.arch.data.model") + "-bit";
    }
 
    //... other methods
}

```

让我们通过单元测试来检查这种方法：

```java
@Test
public void whenUsingSystemClass_thenOutputIsAsExpected() {
    if ("64".equals(System.getProperty("sun.arch.data.model"))) {
        assertEquals("64-bit", jvmVersion.getUsingSystemClass());
    } else if ("32".equals(System.getProperty("sun.arch.data.model"))) {
        assertEquals("32-bit", jvmVersion.getUsingSystemClass());
    }
}
```

## 3. 使用 JNA API

JNA ( [Java Native Access](https://github.com/java-native-access/jna) ) 支持各种平台，例如 macOS、Microsoft Windows、Solaris、GNU 和 Linux。

它使用本机函数按名称加载库并检索指向该库中函数的指针。

### 3.1. 母语班

我们可以使用Native类中的POINTER_SIZE 。此常量指定当前平台上本机指针的大小(以字节为单位)。

值 4 表示 32 位本机指针，而值 8 表示 64 位本机指针：

```java
if (com.sun.jna.Native.POINTER_SIZE == 4) {
    // 32-bit
} else if (com.sun.jna.Native.POINTER_SIZE == 8) {
    // 64-bit
}
```

### 3.2. 平台类

或者，我们可以使用Platform类，它提供简化的平台信息。

它包含检测 JVM 是否为 64 位的is64Bit()方法。

让我们看看它如何识别位数：

```java
public static final boolean is64Bit() {
    String model = System.getProperty("sun.arch.data.model",
                                      System.getProperty("com.ibm.vm.bitmode"));
    if (model != null) {
        return "64".equals(model);
    }
    if ("x86-64".equals(ARCH)
        || "ia64".equals(ARCH)
        || "ppc64".equals(ARCH) || "ppc64le".equals(ARCH)
        || "sparcv9".equals(ARCH)
        || "mips64".equals(ARCH) || "mips64el".equals(ARCH)
        || "amd64".equals(ARCH)
        || "aarch64".equals(ARCH)) {
        return true;
    }
    return Native.POINTER_SIZE == 8;
}
```

在这里，ARCH常量是通过System类从“ os.arch ”属性派生的。它用于获取操作系统架构：

```java
ARCH = getCanonicalArchitecture(System.getProperty("os.arch"), osType);
```

这种方法适用于不同的操作系统，也适用于不同的 JDK 供应商。因此，它比“ sun.arch.data.model ”系统属性更可靠。

## 4. 总结

在本教程中，我们学习了如何检查 JVM 位版本。我们还观察了 JNA 如何在不同平台上为我们简化解决方案。