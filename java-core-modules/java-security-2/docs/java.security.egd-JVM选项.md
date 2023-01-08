## 1. 概述

启动Java虚拟机 (JVM) 时，我们可以定义各种属性来改变 JVM 的行为方式。一个这样的属性是java.security.egd。

在本教程中，我们将研究它是什么、如何使用它以及它有什么作用。

## 2. java.security.egd是什么？

作为 JVM 属性，我们可以使用java.security.egd来影响[SecureRandom](https://www.baeldung.com/java-secure-random)类的初始化方式。

与所有 JVM 属性一样，我们在启动 JVM 时在命令行中使用-D参数声明它：

```java
java -Djava.security.egd=file:/dev/urandom -cp . com.baeldung.java.security.JavaSecurityEgdTester
```

通常，如果我们运行Java8 或更高版本并且在 Linux 上运行，那么我们的 JVM 将默认使用file:/dev/urandom 。

## 3、 java.security.egd有什么作用？

当我们第一次调用从SecureRandom读取字节时，我们会使其初始化并读取 JVM 的java.security配置文件。 此文件包含一个securerandom.source属性：

```java
securerandom.source=file:/dev/random
```

Security Provider比如默认的sun.security.provider.Sun在初始化的时候会读取这个属性。

当我们设置java.security.egd JVM 属性时，安全提供程序可能会使用它来覆盖在securerandom.source中配置的那个。

当我们使用SecureRandom生成随机数时，java.security.egd和securerandom.source控制哪个熵收集设备(EGD) 将被用作种子数据的主要来源。

直到Java8，我们 在$JAVA_HOME/jre/lib/security中找到java.security，但在以后的实现中，它在$JAVA_HOME/conf/security中。

egd选项是否有效取决于安全提供程序的实现。

## 4. java.security.egd可以取什么值？

我们可以用 URL 格式指定java.security.egd，其值如下：

-   文件：/开发/随机
-   文件：/dev/urandom
-   文件：/dev/./urandom

此设置是否有任何影响，或任何其他值是否有所不同，取决于我们使用的平台和Java版本，以及我们的 JVM 安全性是如何配置的。

在基于 Unix 的操作系统 (OS) 上，/dev/random是一个特殊的文件路径，它作为普通文件出现在文件系统中，但从中读取实际上与操作系统的设备驱动程序交互以生成随机数。一些设备实现还提供通过/dev/urandom甚至 /dev/arandom URI 的访问。

## 5. file:/dev/./urandom有什么特别之处？

首先，让我们了解文件/dev/random和/dev/urandom 之间的区别：

-   /dev/random从各种来源收集熵；/dev/random将阻塞，直到它有足够的熵来满足我们对不可预测数据的读取请求
-   /dev/urandom将从可用的任何内容中获取伪随机性，而不会阻塞。

当我们第一次使用SecureRandom时，我们的默认 Sun SeedGenerator会初始化。

当我们使用特殊值file:/dev/random或 file:/dev/urandom之一时，我们会导致 Sun SeedGenerator使用本机(平台)实现。

Unix 上的提供程序实现可能会因仍然从/dev/random读取而阻塞。在Java1.4 中，一些实现被发现有这个问题。该错误随后在Java8 的[JDK 增强提案 (JEP 123)](https://openjdk.java.net/jeps/123)下得到修复。

使用file:/dev/./urandom等 URL或任何其他值，会使SeedGenerator将其视为我们要使用的种子源的 URL。

在类 Unix 系统上，我们的file:/dev/./urandom URL 解析为相同的非阻塞/dev/urandom文件。

然而，我们并不总是想使用这个值。在 Windows 上，我们没有此文件，因此我们的 URL 无法解析。这会触发生成随机性的最终机制，并且可以将我们的初始化延迟大约 5 秒。

## 6. SecureRandom的演变

java.security.egd的作用在不同的Java版本中发生了变化。

那么，让我们看看影响SecureRandom行为的一些更重要的事件：

-   Java 1.4

    -   JDK-4705093下出现的 /dev/random 阻塞问题[：使用 /dev/urandom 而不是 /dev/random(如果存在)](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4705093)

-   Java 5

    -   修复

        JDK-4705093

        -   添加NativePRNG算法以遵守java.security.egd设置，但我们需要手动配置它
        -   如果使用SHA1PRNG，那么如果我们使用 file:/dev/urandom 以外的任何东西，它可能会阻塞。 换句话说，如果我们使用file:/dev/./urandom它可能会阻塞

-   Java 8

    -   JEP123：可配置的安全随机数生成
        -   添加新的SecureRandom实现，它尊重安全属性
        -   添加一个新的getInstanceStrong()方法，用于平台原生的强随机数。非常适合生成高价值和长期存在的秘密，例如 RSA 私钥/公钥对
        -   [我们不再需要file:/dev/./urandom解决方法](https://docs.oracle.com/javase/8/docs/technotes/guides/security/enhancements-8.html)

-   Java 9

    -   JEP273：基于 DRBG 的 SecureRandom 实现
        -   实现三种确定性随机位生成器 (DRBG) 机制，如[Recommendation for Random Number Generation Using Deterministic Random Bit Generators中所述](http://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-90Ar1.pdf)

了解SecureRandom 的变化方式让我们深入了解java.security.egd属性的可能影响。

## 7.测试java.security.egd的效果

确定 JVM 属性效果的最佳方法是尝试它。因此，让我们通过运行一些代码来创建新的SecureRandom并计算获取一些随机字节所需的时间来查看java.security.egd的效果。 

首先，让我们创建一个带有main()方法的JavaSecurityEgdTester类。我们将使用System.nanoTime()对secureRandom.nextBytes() 的调用计时并显示结果：

```java
public class JavaSecurityEgdTester {
    public static final double NANOSECS = 1000000000.0;

    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        long start = System.nanoTime();
        byte[] randomBytes = new byte[256];
        secureRandom.nextBytes(randomBytes);
        double duration = (System.nanoTime() - start) / NANOSECS;

        System.out.println("java.security.egd = " + System.getProperty("java.security.egd") + " took " + duration + " seconds and used the " + secureRandom.getAlgorithm() + " algorithm");
    }
}
```

现在，让我们通过启动一个新的Java实例并为java.security.egd属性指定一个值来运行JavaSecurityEgdTester测试：

```java
java -Djava.security.egd=file:/dev/random -cp . com.baeldung.java.security.JavaSecurityEgdTester
```

让我们检查一下输出，看看我们的测试花了多长时间以及使用了哪种算法：

```java
java.security.egd=file:/dev/random took 0.692 seconds and used the SHA1PRNG algorithm
```

由于我们的系统属性只在初始化时读取，让我们在新的 JVM 中为java.security.egd的每个不同值启动我们的类：

```java
java -Djava.security.egd=file:/dev/urandom -cp . com.baeldung.java.security.JavaSecurityEgdTester
java -Djava.security.egd=file:/dev/./urandom -cp . com.baeldung.java.security.JavaSecurityEgdTester
java -Djava.security.egd=baeldung -cp . com.baeldung.java.security.JavaSecurityEgdTester

```

在使用Java8 或Java11 的 Windows 上，使用值file:/dev/random或file:/dev/urandom进行的测试 给出亚秒级时间。使用其他任何东西，例如file:/dev/./urandom，甚至baeldung，都会使我们的测试花费超过 5 秒！

有关为什么会发生这种情况的解释，请参阅 [我们之前的部分。](https://www.baeldung.com/java-security-egd#initializationDelay)在 Linux 上，我们可能会得到不同的结果。

## 8. SecureRandom.getInstanceStrong()怎么样？

Java 8 引入了[SecureRandom.getInstanceStrong()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/SecureRandom.html#getInstanceStrong())方法。让我们看看这如何影响我们的结果。

首先，让我们将新的 SecureRandom()替换为SecureRandom。获取实例强()：

```java
SecureRandom secureRandom = SecureRandom.getInstanceStrong();
```

现在，让我们再次运行测试：

```java
java -Djava.security.egd=file:/dev/random -cp . com.baeldung.java.security.JavaSecurityEgdTester
```

在 Windows 上运行时，当我们使用SecureRandom.getInstanceStrong()时， java.security.egd属性的值没有明显的影响。即使是无法识别的值也会给我们快速响应。

让我们再次检查我们的输出并注意不到 0.01 秒的时间。我们还可以观察到该算法现在是 Windows-PRNG：

```java
java.security.egd=baeldung took 0.003 seconds and used the Windows-PRNG algorithm
```

请注意，算法名称中的 PRNG 代表伪随机数生成器。

## 9.播种算法

由于随机数在密码学中大量用于安全密钥，因此它们必须是不可预测的。

因此，我们如何播种我们的算法直接影响它们产生的随机数的可预测性。

为了产生不可预测性，SecureRandom实现使用从累积输入中收集的熵来播种他们的算法。这来自于鼠标和键盘等 IO 设备。

在类 Unix 系统上，我们的熵累积在文件/dev/random中。

Windows 上没有/dev/random文件。 将-Djava.security.egd设置为file:/dev/random或file:/dev/urandom会导致默认算法 (SHA1PRNG)使用本机 Microsoft Crypto API 进行播种。

## 10. 虚拟机呢？

有时，我们的应用程序可能在/dev/random中很少或没有熵收集的虚拟机中运行。

虚拟机没有物理鼠标或键盘来生成数据，因此/dev/random中的熵积累要慢得多。这可能会导致我们的默认SecureRandom调用阻塞，直到有足够的熵来生成不可预测的数字。

我们可以采取一些措施来缓解这种情况。例如，在 RedHat Linux 中运行 VM 时，系统管理员可以[配置虚拟 IO 随机数生成器virtio-rng](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/virtualization_deployment_and_administration_guide/sect-guest_virtual_machine_device_configuration-random_number_generator_device)。这从它托管的物理机器中读取熵。

## 11.故障排除技巧

如果我们的应用程序在它或其依赖项生成SecureRandom数字时挂起，请考虑java.security.egd——特别是当我们在 Linux 上运行时，以及如果我们在Java8 之前的版本上运行。

我们的 Spring Boot 应用程序经常使用嵌入式[Tomcat](https://www.baeldung.com/tomcat)。这使用SecureRandom生成会话密钥。当我们看到 Tomcat 的“创建 SecureRandom 实例”操作需要 5 秒或更长时间时，我们应该为java.security.egd尝试不同的值。

## 12.总结

在本教程中，我们了解了 JVM 属性java.security.egd是什么、如何使用它以及它有什么作用。我们还发现它的效果会根据我们运行的平台和我们使用的Java版本而有所不同。

最后一点，我们可以在[JCA 参考指南](https://docs.oracle.com/en/java/javase/11/security/java-cryptography-architecture-jca-reference-guide.html#GUID-AEB77CD8-D28F-4BBE-B9E5-160B5DC35D36) 和 [SecureRandom API 规范的 SecureRandom 部分阅读更多关于](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/SecureRandom.html)SecureRandom及其工作原理的信息 ，并了解一些[关于 urandom 的神话](https://www.2uo.de/myths-about-urandom/)。