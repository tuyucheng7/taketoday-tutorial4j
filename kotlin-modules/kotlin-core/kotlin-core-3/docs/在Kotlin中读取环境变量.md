## 1. 概述

在这个简洁的教程中，我们将了解如何在Kotlin中访问系统环境变量。

## 2. 读取环境变量

要在Kotlin中读取环境变量，**我们可以使用**[System.getenv(String)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#getenv(java.lang.String))**方法**，类似于[我们通常在Java中所做的](https://www.baeldung.com/java-system-get-property-vs-system-getenv)，我们可以这样写：

```kotlin
val env = System.getenv("HOME")
assertNotNull(env)
```

假设系统上有一个名为HOME的环境变量，此验证应该会通过。此外，如果给定的环境变量不存在，此方法将返回null：

```kotlin
assertNull(System.getenv("INVALID_ENV_NAME"))
```

最后，如果我们不向[getenv()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#getenv())方法传递任何内容，**它会以Map<String, String>的形式返回系统上所有可用的环境变量**：

```kotlin
val allEnvs = System.getenv()
allEnvs.forEach { (k, v) -> println("$k => $v") }
assertThat(allEnvs).isNotEmpty
```

这应该打印所有环境变量：

```shell
LOCALAPPDATA => C:\Users\tuyuc\AppData\Local
PROCESSOR_LEVEL => 6
USERDOMAIN => TUYUCHENG
// omitted
```

## 3. 总结

在本教程中，我们看到在Java和Kotlin中访问系统环境变量没有区别。