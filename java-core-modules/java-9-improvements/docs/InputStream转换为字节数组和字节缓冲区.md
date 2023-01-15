## 1. 概述

在本教程中，我们介绍如何将InputStream转换为byte[]和ByteBuffer；首先使用纯Java实现，然后使用Guava 和Commons IO工具库。

## 2. 转换为字节数组

字节数组的重要方面是它支持对存储在内存中的每个8位(一个字节)值进行索引(快速)访问。因此，你可以操纵这些字节来控制每一位。首先我们看看如何将简单的输入流转换为字节数组；首先使用纯Java，然后使用Guava和Apache Commons IO。

### 2.1 使用纯Java进行转换

让我们从一个专注于处理固定大小流的Java解决方案开始：

```java
@Test
final void givenUsingPlainJavaOnFixedSizeStream_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
	final InputStream is = new ByteArrayInputStream(new byte[]{0, 1, 2});
	final byte[] targetArray = new byte[is.available()];
    
	is.read(targetArray);
}
```

在缓冲流的情况下，我们处理的是缓冲流，并且不知道底层数据的确切大小，我们需要使实现更加灵活：

```java
@Test
final void givenUsingPlainJavaOnUnknownSizeStream_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
	final InputStream is = new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6});
	final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	int nRead;
	final byte[] data = new byte[4];

	while ((nRead = is.read(data, 0, data.length)) != -1) {
		buffer.write(data, 0, nRead);
	}

	buffer.flush();
	final byte[] targetArray = buffer.toByteArray();
}
```

从Java 9开始，我们可以使用专用的readNbytes方法实现相同的目的：

```java
@Test
final void givenUsingPlainJava9OnUnknownSizeStream_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
	final InputStream is = new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6});
	final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    
	int nRead;
	final byte[] data = new byte[4];
    
	while ((nRead = is.readNBytes(data, 0, data.length)) != 0) {
		System.out.println("here " + nRead);
		buffer.write(data, 0, nRead);
	}
    
	buffer.flush();
	final byte[] targetArray = buffer.toByteArray();
}
```

这两种方法之间的区别非常微妙。对于第一种，read(byte[] b, int off, int len)从输入流中读取最多len个字节的数据，而第二个readNBytes(byte[] b, int off, int len)精确读取请求的字节数。

此外，如果输入流中没有更多可用数据，则read返回-1 。然而，readNbytes总是返回读入缓冲区的实际字节数。

我们还可以一次读取所有字节：

```java
@Test
void givenUsingPlainJava9_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
	final InputStream is = new ByteArrayInputStream(new byte[]{0, 1, 2});
    
	byte[] data = is.readAllBytes();
}
```

### 2.2 使用Guava进行转换

现在让我们看一下基于Guava的简单解决方案，使用方便的ByteStreams工具类：

```java
@Test
final void givenUsingGuava_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
	final InputStream initialStream = ByteSource.wrap(new byte[]{0, 1, 2})
			.openStream();
    
	final byte[] targetArray = ByteStreams.toByteArray(initialStream);
}
```

### 2.3 使用Commons IO进行转换

最后，下面是使用Apache Commons IO的简单解决方案：

```java
@Test
final void givenUsingCommonsIO_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
	final InputStream initialStream = new ByteArrayInputStream(new byte[]{0, 1, 2});
    
	final byte[] targetArray = IOUtils.toByteArray(initialStream);
}
```

IOUtils.toByteArray()方法在内部缓冲InputStream，因此在需要缓冲时无需使用BufferedInputStream实例。

## 3. 转换为ByteBuffer

现在，我们介绍从InputStream中获取[ByteBuffer。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/ByteBuffer.html)当我们需要在内存中进行快速和直接的低级I/O操作时，这很有用。与之前一样，我们通过三种不同的方式来实现。

### 3.1 使用纯Java进行转换

对于字节流，我们知道底层数据的确切大小。因此我们可以使用ByteArrayInputStream#available方法将字节流读入ByteBuffer：

```java
@Test
void givenUsingCoreClasses_whenByteArrayInputStreamToAByteBuffer_thenLengthMustMatch() throws IOException {
	byte[] input = new byte[]{0, 1, 2};
	InputStream initialStream = new ByteArrayInputStream(input);
	ByteBuffer byteBuffer = ByteBuffer.allocate(3);
	while (initialStream.available() > 0) {
		byteBuffer.put((byte) initialStream.read());
	}
    
	assertEquals(byteBuffer.position(), input.length);
}
```

### 3.2 使用Guava进行转换

下面是一个简单的基于Guava的解决方案，通过使用方便的ByteStreams工具类：

```java
@Test
void givenUsingGuava__whenByteArrayInputStreamToAByteBuffer_thenLengthMustMatch() throws IOException {
	InputStream initialStream = ByteSource
			.wrap(new byte[]{0, 1, 2})
			.openStream();
	byte[] targetArray = ByteStreams.toByteArray(initialStream);
	ByteBuffer bufferByte = ByteBuffer.wrap(targetArray);
	while (bufferByte.hasRemaining()) {
		bufferByte.get();
	}
    
	assertEquals(bufferByte.position(), targetArray.length);
}
```

在这里，我们使用带有方法hasRemaining的while循环来显示将所有字节读入ByteBuffer的不同方法。否则，断言将失败，因为ByteBuffer索引位置将为零。

### 3.3 使用Commons IO进行转换

最后，下面是使用Apache Commons IO中IOUtils类的例子：

```java
@Test
void givenUsingCommonsIo_whenByteArrayInputStreamToAByteBuffer_thenLengthMustMatch() throws IOException {
	byte[] input = new byte[]{0, 1, 2};
	InputStream initialStream = new ByteArrayInputStream(input);
	ByteBuffer byteBuffer = ByteBuffer.allocate(3);
	ReadableByteChannel channel = newChannel(initialStream);
	IOUtils.readFully(channel, byteBuffer);
    
	assertEquals(byteBuffer.position(), input.length);
}
```

## 4. 总结

本文演示了使用纯Java、Guava和Apache Commons IO将原始输入流转换为字节数组和ByteBuffer的各种方法。