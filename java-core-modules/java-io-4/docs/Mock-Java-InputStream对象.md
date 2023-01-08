## 一、简介

InputStream是用于处理数据的通用抽象类。数据可以来自非常不同的来源，但使用类允许我们从来源中抽象出来并独立于特定来源进行处理。

然而，当我们编写测试时，我们实际上需要提供一些可靠的实现。在本教程中，我们将了解我们应该选择哪些可用的实现，或者什么时候最好自己编写。

## 2. InputStream接口基础

在我们着手编写自己的代码之前，我们最好先了解一下InputStream接口的构建方式。幸运的是，它非常简单。要实现一个简单的InputStream，我们只需要考虑一个方法——[读取](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/io/InputStream.html#read())。它不接受任何参数，并将流的下一个字节作为int返回。如果InputStream已经结束，它返回 -1，向我们发出停止处理的信号。

### 2.1. 测试用例

在本教程中，我们将测试一种处理InputStream形式的文本消息并返回已处理字节数的方法。然后我们将断言读取了正确数量的字节：

```java
int bytesCount = processInputStream(someInputStream);
assertThat(bytesCount).isEqualTo(expectedNumberOfBytes);
```

processInputStream()方法在内部做什么在这里不太相关，所以我们只使用一个非常简单的实现：

```java
public class MockingInputStreamUnitTest { 
    int processInputStream(InputStream inputStream) throws IOException {
        int count = 0;
        while(inputStream.read() != -1) {
            count++;
        }
        return count;
    }
}
```

### 2.2. 使用朴素的实现

为了更好地理解InputStream的工作原理，我们将编写一个带有硬编码消息的简单实现。除了消息之外，我们的实现还有一个索引，指向我们接下来应该读取消息的哪个字节。每次调用 read 方法时，我们将从消息中获取一个字节，然后递增索引。

在我们这样做之前，我们还需要检查我们是否还没有从消息中读取所有字节。如果是这样，我们需要返回 -1：

```java
public class MockingInputStreamUnitTest {

@Test
public void givenSimpleImplementation_shouldProcessInputStream() throws IOException {
    int byteCount = processInputStream(new InputStream() {
        private final byte[] msg = "Hello World".getBytes();
        private int index = 0;
        @Override
        public int read() {
            if (index >= msg.length) {
                return -1;
            }
            return msg[index++];
        }
    });
    assertThat(byteCount).isEqualTo(11);
}
```

## 3. 使用字节数组输入流

如果我们绝对确定整个数据负载都适合内存，最简单的选择是ByteArrayInputStream。我们向构造函数提供一个字节数组，然后流以与上一节中的示例类似的方式逐字节迭代它：

```java
String msg = "Hello World";
int bytesCount = processInputStream(new ByteArrayInputStream(msg.getBytes()));
assertThat(bytesCount).isEqualTo(11);
```

## 4.使用文件输入流

如果我们可以将数据保存为文件，我们也可以以FileInputStream的形式加载它。这种方法的优点是数据不会作为一个整体加载到内存中，而是在需要时从磁盘中读取。如果我们将文件放在资源文件夹中，我们可以使用方便的getResourceAsStream 方法直接从一行代码中的路径创建InputStream ：

```java
InputStream inputStream = MockingInputStreamUnitTest.class.getResourceAsStream("/mockinginputstreams/msg.txt");
int bytesCount = processInputStream(inputStream);
assertThat(bytesCount).isEqualTo(11);
```

请注意，在此示例中，InputStream的实际实现将是BufferedFileInputStream。顾名思义，它读取更大的数据块并将它们存储在缓冲区中。因此它限制了从磁盘读取的次数。

## 5. 即时生成数据

有时我们想测试我们的系统是否能正常处理大量数据。我们可以只使用从磁盘加载的大文件，但这种方法有一些严重的缺点。这不仅是一种潜在的空间浪费，而且像git这样的版本控制系统也不能很好地处理大的二进制文件。幸运的是，我们不需要事先拥有所有数据。相反，我们可以即时生成它。

为此，我们需要实施我们的InputStream。让我们从定义字段和构造函数开始：

```java
public class GeneratingInputStream extends InputStream {
    private final int desiredSize;
    private final byte[] seed;
    private int actualSize = 0;

    public GeneratingInputStream(int desiredSize, String seed) {
        this.desiredSize = desiredSize;
        this.seed = seed.getBytes();
    }
}
```

“desiredSize”变量会告诉我们什么时候应该停止生成数据。“种子”变量将是将被重复的一大块数据。最后，“actualSize”变量将帮助我们跟踪返回了多少字节。我们需要它，因为我们实际上并不保存任何数据。我们只返回“当前”字节。

使用我们定义的变量，我们可以实现读取方法：

```java
@Override
public int read() {
    if (actualSize >= desiredSize) {
        return -1;
    }
    return seed[actualSize++ % seed.length];
}
```

首先，我们检查是否达到了所需的大小。如果我们这样做了，我们应该返回 -1 以便流的消费者知道停止阅读。如果我们不这样做，我们应该从种子中返回一个字节。为了确定它应该是哪个字节，我们使用[模运算符](https://www.baeldung.com/modulo-java)来获得生成数据的实际大小除以种子长度的余数。

## 6.总结

在本教程中，我们研究了如何在测试中处理InputStreams 。我们了解了该类的构建方式以及我们可以在各种场景中使用哪些实现。最后，我们学习了如何编写自己的实现来动态生成数据。

与往常一样，代码示例[在 GitHub 上可用。](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-io-4)