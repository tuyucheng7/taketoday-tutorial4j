## 1. 概述

在本教程中，我们将回顾确定两个文件的内容是否相等的不同方法。我们将使用核心Java I/O流库来读取文件的内容并进行基本比较。

最后，我们将回顾Apache Commons I/O中提供的支持，以检查两个文件的内容是否相等。

## 2. 逐字节比较

**让我们从一个简单的方法开始，从两个文件中读取字节以顺序比较它们**。

为了加快读取文件的速度，我们将使用BufferedInputStream。正如我们将看到的，BufferedInputStream从底层InputStream读取大块字节到内部缓冲区，当客户端读取块中的所有字节时，缓冲区从流中读取另一个字节块。

显然，**使用BufferedInputStream比从底层流一次读取一个字节要快得多**。

让我们编写一个使用BufferedInputStream来比较两个文件的方法：

```java
public static long filesCompareByByte(Path path1, Path path2) throws IOException {
	if (path1.getFileSystem()
			.provider()
			.isSameFile(path1, path2)) {
		return -1;
	}
    
	try (BufferedInputStream fis1 = new BufferedInputStream(new FileInputStream(path1.toFile()));
		 BufferedInputStream fis2 = new BufferedInputStream(new FileInputStream(path2.toFile()))) {
		int ch = 0;
		long pos = 1;
		while ((ch = fis1.read()) != -1) {
			if (ch != fis2.read()) {
				return pos;
			}
			pos++;
		}
		if (fis2.read() == -1) {
			return -1;
		} else {
			return pos;
		}
	}
}
```

我们使用try-with-resources语句来确保两个BufferedInputStream在语句结束时关闭。

使用while循环，我们读取第一个文件的每个字节并将其与第二个文件的相应字节进行比较。如果发现差异，我们返回不匹配的字节位置。否则，代表文件相同并且该方法返回-1L。

我们可以看到，如果文件大小不同但较小文件的字节与较大文件的相应字节匹配，则它返回较小文件的大小(以字节为单位)。

## 3. 逐行比较

**为了比较文本文件，我们可以执行逐行读取文件并检查它们之间是否相等的实现**。

让我们使用BufferedReader，它使用与InputStreamBuffer相同的策略，将数据块从文件复制到内部缓冲区以加快读取过程。

下面是逐行比较的具体方法实现：

```java
public static long filesCompareByLine(Path path1, Path path2) throws IOException {
	if (path1.getFileSystem()
			.provider()
			.isSameFile(path1, path2)) {
		return -1;
	}
    
	try (BufferedReader bf1 = Files.newBufferedReader(path1);
		 BufferedReader bf2 = Files.newBufferedReader(path2)) {
        
		long lineNumber = 1;
		String line1 = "", line2 = "";
		while ((line1 = bf1.readLine()) != null) {
			line2 = bf2.readLine();
			if (line2 == null || !line1.equals(line2)) {
				return lineNumber;
			}
			lineNumber++;
		}
		if (bf2.readLine() == null) {
			return -1;
		} else {
			return lineNumber;
		}
	}
}
```

该代码遵循与上一个示例类似的策略。在while循环中，我们不是读取字节，而是读取每个文件的一行并检查是否相等。如果两个文件的所有行都相同，那么我们返回-1L，但如果存在差异，则返回发现第一个不匹配的行号。

如果文件大小不同但较小的文件与较大文件的相应行匹配，则返回较小文件的行数。

## 4. 使用Files::mismatch比较

**Java 12中添加的Files::mismatch方法用于比较两个文件的内容**。如果文件相同，则返回-1L，否则返回第一个不匹配的位置(以字节为单位)。

**该方法在内部从文件的InputStream中读取数据块，并使用Java 9中引入的Arrays::mismatch来比较它们**。

与我们的第一个示例一样，对于大小不同但小文件的内容与大文件中的相应内容相同的文件，它返回小文件的大小(以字节为单位)。

要查看如何使用此方法的示例，请参阅我们介绍[Java 12新功能](https://www.baeldung.com/java-12-new-features)的文章。

## 5. 使用内存映射文件

内存映射文件是一个内核对象，它将磁盘文件中的字节映射到计算机的内存地址空间。堆内存被绕过了，因为Java代码操纵内存映射文件的内容，就好像我们直接访问内存一样。

**对于大型文件，从内存映射文件中读取和写入数据比使用标准Java I/O库要快得多**。重要的是计算机有足够的内存来处理作业以防止抖动。

让我们写一个非常简单的例子来演示如何使用内存映射文件比较两个文件的内容：

```java
public static boolean compareByMemoryMappedFiles(Path path1, Path path2) throws IOException {
    try (RandomAccessFile randomAccessFile1 = new RandomAccessFile(path1.toFile(), "r"); 
         RandomAccessFile randomAccessFile2 = new RandomAccessFile(path2.toFile(), "r")) {
        
        FileChannel ch1 = randomAccessFile1.getChannel();
        FileChannel ch2 = randomAccessFile2.getChannel();
        if (ch1.size() != ch2.size()) {
            return false;
        }
        long size = ch1.size();
        MappedByteBuffer m1 = ch1.map(FileChannel.MapMode.READ_ONLY, 0L, size);
        MappedByteBuffer m2 = ch2.map(FileChannel.MapMode.READ_ONLY, 0L, size);

        return m1.equals(m2);
    }
}
```

如果文件的内容相同，则该方法返回true，否则返回false。

我们使用RandomAccessFile类打开文件并访问它们各自的FileChannel以获取MappedByteBuffer。这是一个直接字节缓冲区，它是文件的内存映射区域。在这个简单的实现中，我们使用它的equals方法在内存中一次比较整个文件的字节。

## 6. 使用Apache Commons I/O

**IOUtils::contentEquals和IOUtils::contentEqualsIgnoreEOL方法用于比较两个文件的内容以确定是否相等**，它们之间的区别在于**contentEqualsIgnoreEOL忽略换行符(\n)和回车符(\r)**，这样做的动机是由于操作系统使用这些控制字符的不同组合来定义新行。

让我们看一个简单的例子来检查相等性：

```java
@Test
void whenFilesIdentical_thenReturnTrue() throws IOException {
	Path path1 = Files.createTempFile("file1Test", ".txt");
	Path path2 = Files.createTempFile("file2Test", ".txt");
    
	InputStream inputStream1 = new FileInputStream(path1.toFile());
	InputStream inputStream2 = new FileInputStream(path2.toFile());
    
	Files.writeString(path1, "testing line 1" + System.lineSeparator() + "line 2");
	Files.writeString(path2, "testing line 1" + System.lineSeparator() + "line 2");
    
	assertTrue(IOUtils.contentEquals(inputStream1, inputStream2));
}
```

如果我们想忽略换行控制符，并检查内容是否相等：

```java
@Test
public void whenFilesIdenticalIgnoreEOF_thenReturnTrue() throws IOException {
    Path path1 = Files.createTempFile("file1Test", ".txt");
    Path path2 = Files.createTempFile("file2Test", ".txt");

    Files.writeString(path1, "testing line 1 \n line 2");
    Files.writeString(path2, "testing line 1 \r\n line 2");

    Reader reader1 = new BufferedReader(new FileReader(path1.toFile()));
    Reader reader2 = new BufferedReader(new FileReader(path2.toFile()));

    assertTrue(IOUtils.contentEqualsIgnoreEOL(reader1, reader2));
}
```

## 7. 总结

在本文中，我们介绍了几种实现两个文件内容比较以检查是否相等的方法。