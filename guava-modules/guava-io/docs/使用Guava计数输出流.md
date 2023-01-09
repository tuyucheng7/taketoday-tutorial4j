## 1. 概述

在本教程中，我们将了解CountingOutputStream类及其使用方法。

该类可以在[Apache Commons](https://commons.apache.org/proper/commons-io/javadocs/api-release/org/apache/commons/io/output/CountingOutputStream.html)或[Google Guava](https://google.github.io/guava/releases/24.0-jre/api/docs/com/google/common/io/CountingOutputStream.html)等流行库中找到。我们将专注于 Guava 库中的实现。

## 2.计数输出流

### 2.1. Maven 依赖

CountingOutputStream是 Google 的 Guava 包的一部分。

让我们从将依赖项添加到pom.xml开始：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")检查最新版本的依赖项。

### 2.2. 课程详情

该类扩展了[java.io.FilterOutputStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FilterOutputStream.html)，覆盖了write()和close()方法，并提供了新方法getCount()。

构造函数将另一个OutputStream对象作为输入参数。在写入数据时，该类会计算写入此OutputStream的字节数。

为了获得计数，我们可以简单地调用getCount()来返回当前的字节数：

```java
/ Returns the number of bytes written. /
public long getCount() {
    return count;
}
```

## 3.用例

让我们在实际用例中使用CountingOutputStream 。为了示例，我们将把代码放入 JUnit 测试中以使其可执行。

在我们的例子中，我们要将数据写入OutputStream并检查我们是否达到了MAX字节的限制。

一旦达到限制，我们想通过抛出异常来中断执行：

```java
public class GuavaCountingOutputStreamUnitTest {
    static int MAX = 5;

    @Test(expected = RuntimeException.class)
    public void givenData_whenCountReachesLimit_thenThrowException()
      throws Exception {
 
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CountingOutputStream cos = new CountingOutputStream(out);

        byte[] data = new byte[1024];
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        
        int b;
        while ((b = in.read()) != -1) {
            cos.write(b);
            if (cos.getCount() >= MAX) {
                throw new RuntimeException("Write limit reached");
            }
        }
    }
}

```

## 4. 总结

在这篇简短的文章中，我们了解了CountingOutputStream类及其用法。该类提供了额外的方法getCount() ，它返回到目前为止写入OutputStream的字节数。