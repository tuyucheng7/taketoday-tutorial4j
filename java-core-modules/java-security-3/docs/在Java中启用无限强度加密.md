## 1. 概述

在本教程中，我们将了解为什么默认情况下并不总是启用Java密码术扩展 (JCE) 无限强度策略文件。此外，我们将解释如何检查加密强度。之后，我们将展示如何在不同版本的Java中启用无限加密。

## 2. JCE Unlimited Strength 策略文件

让我们了解[密码强度](https://www.baeldung.com/cs/cryptographic-algorithm-complexity)的含义。它由发现密钥的难度来定义，这取决于所使用的[密码](https://www.baeldung.com/java-list-cipher-algorithms)和密钥的长度。通常，更长的密钥提供更强的加密。有限的加密强度使用最大 128 位密钥。另一方面，无限密钥使用最大长度为 2147483647 位的密钥。

正如我们所知，JRE 本身包含加密功能。JCE 使用权限策略文件来控制密码强度。策略文件由两个 jar 组成：local_policy.jar和US_export_policy.jar。由于这一点，Java 平台具有对加密强度的内置控制。

## 3.为什么默认不包含JCE Unlimited Strength策略文件

首先，只有旧版本的 JRE 不包含无限强度策略文件。JRE 版本 8u151 及更早版本仅捆绑了有限的策略文件。相反，从Java版本 8u151 开始，JRE 提供了无限和有限的策略文件。原因很简单，一些国家/地区要求限制密码强度。如果一个国家/地区的法律允许无限的加密强度，则可以根据Java版本捆绑或启用它。

## 4. 如何检查密码强度

让我们看看如何检查密码强度。我们可以通过检查最大允许密钥长度来做到这一点：

```java
int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
```

如果策略文件有限，它会返回 128。另一方面，如果它返回 2147483647，则 JCE 使用无限的策略文件。

## 5.策略文件位于何处

Java 版本 8u151 及更早版本包含 JAVA_HOME/jre/lib/security 目录中的策略文件。

从版本 8u151 开始，JRE 提供了不同的策略文件集。 因此，在 JRE 目录JAVA_HOME/jre/lib/security/policy中有 2 个子目录：limited和unlimited。第一个包含强度有限的策略文件。第二个包含无限个。

## 6. 如何启用无限强度加密

现在让我们看看如何启用最大的加密强度。根据我们使用的Java版本，有不同的方法可以做到这一点。

### 6.1.Java版本 8u151 之前的处理

在版本 8u151 之前，JRE 仅包含强度有限的策略文件。我们必须用来自 Oracle 站点的无限制版本替换它。

首先，我们下载适用于Java8 的文件，可[在此处获取。](https://www.oracle.com/java/technologies/javase-jce8-downloads.html)接下来，我们解压下载的包，其中包含local_policy.jar和US_export_policy.jar。

最后，我们将这些文件到JAVA_HOME/jre/lib/security。

### 6.2.Java版本 8u151 之后的处理

在Java8u151 及更高版本中，JCE 框架默认使用无限强度策略文件。此外，如果我们想定义要使用的版本，则有一个安全属性crypto.policy：

```java
Security.setProperty("crypto.policy", "unlimited");
```

我们必须在 JCE 框架初始化之前设置该属性。它为策略文件在JAVA_HOME/jre/lib/security/policy下定义了一个目录。

首先，当未设置安全属性时，框架会检查旧位置JAVA_HOME/jre/lib/security中的策略文件。尽管默认情况下在新版本的Java中，旧位置中没有策略文件。JCE 将其作为第一个与旧版本兼容的检查。

其次，如果旧位置不存在 jar 文件并且未定义该属性，则默认情况下 JRE 使用无限制的策略文件。

## 七、总结

在这篇简短的文章中，我们了解了 JCE 无限强度策略文件。首先，我们研究了为什么在旧版本的Java中默认情况下不启用无限加密强度。接下来，我们学习了如何通过检查最大密钥长度来确定密码强度。最后，我们看到了如何在不同版本的Java中启用它。