## 1. 概述

[Base64](https://www.baeldung.com/java-base64-encode-and-decode)是一种文本编码方案，可为应用程序和平台之间的二进制数据提供可移植性。Base64 可用于将二进制数据存储在数据库字符串列中，从而避免乱文件操作。当与[数据 URI 方案](https://en.wikipedia.org/wiki/Data_URI_scheme)结合使用时，Base64 可用于在网页和电子邮件中嵌入图像，符合 HTML 和多用途互联网邮件扩展 (MIME) 标准。

在这个简短的教程中，我们将演示JavaStreaming IO 函数和内置的JavaBase64类，以将二进制数据作为InputStream加载，然后将其转换为String。

## 2.设置

让我们看看代码所需的依赖项和测试数据。

### 2.1. 依赖关系

我们将使用[Apache IOUtils库通过将其依赖项添加到我们的](https://mvnrepository.com/artifact/commons-io/commons-io/2.11.0)pom.xml来方便地访问测试数据文件：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>

```

### 2.2. 测试数据

这里需要一个二进制测试数据文件。所以我们将添加一个logo.png图像文件到我们的标准src/test/resources文件夹。

## 3. InputStream转Base64字符串

Java 在java.util.Base64 类中内置了对 Base64 编码和解码的支持。所以我们将使用那里的静态方法来完成繁重的工作。 

Base64.encode()方法需要一个字节数组，我们的图像在一个文件中。因此，我们需要先将文件转换为 InputStream ，然后将流逐字节读取到数组中。

我们正在使用 Apache commons-io包中的IOUtils.toByteArray()方法作为其他冗长的仅Java方法的便捷替代方法。

首先，我们将编写一个简单的方法来生成“穷人的”校验和：

```java
int calculateChecksum(byte[] bytes) {
    int checksum = 0; 
    for (int index = 0; index < bytes.length; index++) {
        checksum += bytes[index]; 
    }
    return checksum; 
}

```

我们将使用它来比较两个数组，验证它们是否匹配。

接下来的几行打开文件，将其转换为字节数组，然后 Base64 将其编码为String：

```java
InputStream sourceStream  = getClass().getClassLoader().getResourceAsStream("logo.png");
byte[] sourceBytes = IOUtils.toByteArray(sourceStream);

String encodedString = Base64.getEncoder().encodeToString(sourceBytes); 
assertNotNull(encodedString);

```

字符串看起来像一块随机字符。事实上，它不是随机的，正如我们在验证步骤中看到的那样：

```java
byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
assertNotNull(decodedBytes);
assertTrue(decodedBytes.length == sourceBytes.length);
assertTrue(calculateChecksum(decodedBytes) == calculateChecksum(sourceBytes));

```

## 4。总结

在本文中，我们演示了将InputStream编码为 Base64 字符串以及将该字符串成功解码回二进制数组。