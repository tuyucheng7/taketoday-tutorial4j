## 1. 概述

在本快速教程中，我们将介绍将Spring [MultipartFile](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html)转换为[File](https://www.baeldung.com/java-io-file)的各种方法。

## 2. MultipartFile#getBytes

MultipartFile有一个getBytes()方法，它返回文件内容的字节数组。我们可以使用此方法将字节写入文件：

```java
MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());

File file = new File("src/main/resources/targetFile.tmp");

try (OutputStream os = new FileOutputStream(file)) {
    os.write(multipartFile.getBytes());
}

assertThat(FileUtils.readFileToString(new File("src/main/resources/targetFile.tmp"), "UTF-8"))
      .isEqualTo("Hello World");
```

getBytes()方法对于我们希望在写入磁盘之前对文件执行额外操作的实例很有用，例如计算文件哈希。

## 3. MultipartFile#getInputStream

接下来，让我们看看MultipartFile的getInputStream()方法：

```java
MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());

InputStream initialStream = multipartFile.getInputStream();
byte[] buffer = new byte[initialStream.available()];
initialStream.read(buffer);

File targetFile = new File("src/main/resources/targetFile.tmp");

try (OutputStream outStream = new FileOutputStream(targetFile)) {
    outStream.write(buffer);
}

assertThat(FileUtils.readFileToString(new File("src/main/resources/targetFile.tmp"), "UTF-8"))
      .isEqualTo("Hello World");
```

这里我们使用getInputStream()方法获取InputStream，从InputStream中读取字节，并将它们存储在byte[] buffer中。然后我们创建一个File和OutputStream来写入缓冲区内容。

getInputStream()方法在我们需要将InputStream包装在另一个InputStream中的情况下很有用，例如，如果上传的文件被gzip压缩，则为GZipInputStream。

## 4. MultipartFile#transferTo

最后，让我们看看MultipartFile的transferTo()方法：

```java
MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());

File file = new File("src/main/resources/targetFile.tmp");

multipartFile.transferTo(file);

assertThat(FileUtils.readFileToString(new File("src/main/resources/targetFile.tmp"), "UTF-8"))
      .isEqualTo("Hello World");
```

使用transferTo()方法，我们只需创建要写入字节的文件，然后将该文件传递给transferTo()方法。

当MultipartFile只需要写入File时，transferTo()方法很有用。

## 5. 总结

在本教程中，我们探讨了将Spring MultipartFile转换为File的方法。