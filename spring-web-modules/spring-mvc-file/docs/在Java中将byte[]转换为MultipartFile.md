## 一、概述

在本教程中，我们将了解如何将*字节数组*转换为*[MultipartFile](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html)*。

*MutlipartFile*是 Spring 提供的接口，用于接收多个请求块中的文件，因此我们需要一些实现来实例化一个*MultipartFile*对象。Spring 不为代码提供任何默认实现，但它确实提供了一个用于测试目的。

## 2. 实现*MultipartFile*接口

*让我们为MultipartFile*接口创建我们自己的实现并包装输入*字节数组*：

```java
public class CustomMultipartFile implements MultipartFile {

    private byte[] input;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }
    //We've defined the rest of the interface methods in the next snippet
}
复制
```

**我们在我们的类中定义了一个\*字节数组\*属性，以便我们可以捕获输入的值**。此外，我们已经覆盖了上面接口中的方法，这取决于实现细节。因此，有关文件名或内容类型的详细信息可以作为自定义逻辑提供。结果，我们在这里返回了*null*。

我们还为接口中的其他所需方法提供了我们自己的实现：

```java
public class CustomMultipartFile implements MultipartFile {

    //previous methods
    @Override
    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    @Override
    public long getSize() {
        return input.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return input;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(input);
    }

    @Override
    public void transferTo(File destination) throws IOException, IllegalStateException {
        try(FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(input);
        }
    }
}复制
```

这些方法可能不需要任何自定义逻辑，因此我们在类中定义了它们。有几种不同的方式来实现*transferTo(File destination)* 方法。让我们看看下面的几个：

我们可以使用 Java NIO：

```java
@Override
public void transferTo(File destination) throws IOException, IllegalStateException {
    Path path = Paths.get(destination.getPath());
    Files.write(path, input);
}复制
```

另一种选择是将[Apache commons IO 依赖](https://mvnrepository.com/artifact/commons-io/commons-io)项添加到我们的 POM 并使用*FileUtils*类：

```java
@Override
public void transferTo(File destination) throws IOException, IllegalStateException {
    FileUtils.writeByteArrayToFile(destination, input);
}复制
```

***transferTo(File destination)\* 方法在 MultipartFile 只需要写入 File 时很有** 用 ***，\*** **并且** ***有\*** 多种方法可以将[*MultipartFile*写入*File*](https://www.baeldung.com/spring-multipartfile-to-file)*。*

现在我们已经定义了我们的类，让我们用一个小的测试用例来测试这个实现：

```java
@Test
void whenProvidingByteArray_thenMultipartFileCreated() throws IOException {
    byte[] inputArray = "Test String".getBytes();
    CustomMultipartFile customMultipartFile = new CustomMultipartFile(inputArray);
    Assertions.assertFalse(customMultipartFile.isEmpty());
    Assertions.assertArrayEquals(inputArray, customMultipartFile.getBytes());
    Assertions.assertEquals(inputArray.length,customMultipartFile.getSize());
}复制
```

**在上面的测试用例中，我们已经成功地将字节数组转换为 MultipartFile 实例。**

## 3.模拟多部分文件

Spring 提供开箱即用的*[MockMultipartFile](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mock/web/MockMultipartFile.html)*用于测试目的以访问 Multipart 请求。

让我们创建一个测试看看它是如何工作的：

```java
@Test
void whenProvidingByteArray_thenMockMultipartFileCreated() throws IOException {
    byte[] inputArray = "Test String".getBytes();
    MockMultipartFile mockMultipartFile = new MockMultipartFile("tempFileName",inputArray);
    Assertions.assertFalse(mockMultipartFile.isEmpty());
    Assertions.assertArrayEquals(inputArray, mockMultipartFile.getBytes());
    Assertions.assertEquals(inputArray.length,mockMultipartFile.getSize());
}复制
```

我们已经成功地使用 Spring 提供的*MockMultipartFile*对象将*字节数组*转换为*MultipartFile*对象。

## 4。结论

在本教程中，我们介绍了如何将*字节数组*转换为*MultipartFile*对象。