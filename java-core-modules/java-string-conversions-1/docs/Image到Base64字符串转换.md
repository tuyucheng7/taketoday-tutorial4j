## 1. 概述

在本快速教程中，我们将介绍如何将图像文件编码为 Base64字符串，然后使用 Apache Common IO 和Java8 本机 Base64 功能对其进行解码以检索原始图像。

此操作可应用于任何二进制文件或二进制数组。当我们需要以 JSON 格式传输二进制内容(例如从移动应用程序到 REST 端点)时，它很有用。

有关 Base64 转换的更多信息，请[在此处查看这篇文章](https://www.baeldung.com/java-base64-encode-and-decode)。

## 2.Maven依赖

让我们将以下依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

[你可以在Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"commons-io" AND a%3A"commons-io")上找到最新版本的 Apache Commons IO 。

## 3.将图像文件转换为Base64字符串

首先，让我们将文件内容读取到一个字节数组中，并使用Java8 Base64类对其进行编码：

```java
byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
String encodedString = Base64.getEncoder().encodeToString(fileContent);
```

encodedString是A-Za-z0-9+/集合中的一个字符串，解码器拒绝该集合之外的任何字符。

## 4.将Base64字符串转换成图片文件

现在我们有了一个 Base64字符串，让我们将它解码回二进制内容并写入一个新文件：

```java
byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
FileUtils.writeByteArrayToFile(new File(outputFileName), decodedBytes);
```

## 5. 测试我们的代码

最后，我们可以通过读取文件、将其编码为 Base64 String并将其解码回新文件来验证代码是否正常工作：

```java
public class FileToBase64StringConversionUnitTest {

    private String inputFilePath = "test_image.jpg";
    private String outputFilePath = "test_image_copy.jpg";

    @Test
    public void fileToBase64StringConversion() throws IOException {
        // load file from /src/test/resources
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(classLoader
          .getResource(inputFilePath)
          .getFile());

        byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
        String encodedString = Base64
          .getEncoder()
          .encodeToString(fileContent);

        // create output file
        File outputFile = new File(inputFile
          .getParentFile()
          .getAbsolutePath() + File.pathSeparator + outputFilePath);

        // decode the string and write to file
        byte[] decodedBytes = Base64
          .getDecoder()
          .decode(encodedString);
        FileUtils.writeByteArrayToFile(outputFile, decodedBytes);

        assertTrue(FileUtils.contentEquals(inputFile, outputFile));
    }
}
```

## 六，总结

这篇切中要点的文章解释了将任何文件的内容编码为 Base64 String以及将 Base64 String解码 为字节数组并将其保存到使用 Apache Common IO 和Java8 功能的文件的基础知识。