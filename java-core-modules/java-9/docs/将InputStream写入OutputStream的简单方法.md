## 1. 概述

在这个教程中，我们学习如何将Java InputStream写入Java OutputStream。首先我们使用Java 8和Java 9 的核心功能。然后，我们使用两个外部库：Guava和Apache Commons IO。

Java 9、Guava和Apache Commons IO提供的工具方法不会刷新或关闭流。因此，我们需要使用try-with-resources或finally块来管理这些资源。

## 2. 使用Java 8

首先，我们创建一个简单的方法，使用Java原生API将内容从InputStream复制到OutputStream：

```java
void copy(InputStream source, OutputStream target) throws IOException {
    byte[] buf = new byte[8192];
    int length;
    while ((length = source.read(buf)) != -1) {
        target.write(buf, 0, length);
    }
}
```

这段代码非常简单，我们只是读取一些字节，然后将它们写出来。

## 3. 使用Java 9

Java 9为此提供了一个工具方法InputStream.transferTo()，让我们看看如何使用该方法：

```java
@Test
void givenUsingJavaNine_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
	String initialString = "Hello World!";
    
	try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
		 ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
		inputStream.transferTo(targetStream);
        
		assertEquals(initialString, targetStream.toString());
	}
}
```

请注意，在处理文件流时，使用Files.copy()比使用transferTo()方法更有效。

## 4. 使用Guava

接下来，我们看看如何使用Guava的工具方法ByteStreams.copy()。

首先我们需要在pom.xml中包含guava依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

我们通过一个简单的测试用例来演示它的用法：

```java
@Test
void givenUsingGuava_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
	String initialString = "Hello World!";
    
	try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
		 ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
		ByteStreams.copy(inputStream, targetStream);
        
		assertEquals(initialString, targetStream.toString());
	}
}
```

## 5. 使用Commons IO

最后，让我们看看如何使用Commons IO中的IOUtils.copy()方法来完成此任务。

当然，我们需要将commons-io依赖添加到pom.xml中：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

让我们创建一个简单的测试用例，使用IOUtils将数据从输入流复制到输出流：

```java
@Test
void givenUsingCommonsIO_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
	String initialString = "Hello World!";
    
	try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
		 ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
		IOUtils.copy(inputStream, targetStream);
        
		assertEquals(initialString, targetStream.toString());
	}
}
```

注意：Commons IO提供了使用InputStream和OutputStream的其他方法。当需要复制2GB或更多数据时，应使用IOUtils.copyLarge()。

## 6. 总结

在本文中，我们介绍了将数据从InputStream复制到OutputStream的简单方法。