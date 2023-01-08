## 1. 概述

在这个非常快速的教程中，我们将讨论如何使用纯 Java、Guava 和 Commons IO 将byte[] 转换为 Writer。

## 2. 使用纯 Java

让我们从一个简单的Java解决方案开始：

```java
@Test
public void givenPlainJava_whenConvertingByteArrayIntoWriter_thenCorrect() 
  throws IOException {
    byte[] initialArray = "With Java".getBytes();
    Writer targetWriter = new StringWriter().append(new String(initialArray));

    targetWriter.close();
    
    assertEquals("With Java", targetWriter.toString());
}
```

请注意，我们通过中间字符串将byte[]转换为Writer。

## 3.番石榴

接下来——让我们研究一个更复杂的 Guava 解决方案：

```java
@Test
public void givenUsingGuava_whenConvertingByteArrayIntoWriter_thenCorrect() 
  throws IOException {
    byte[] initialArray = "With Guava".getBytes();

    String buffer = new String(initialArray);
    StringWriter stringWriter = new StringWriter();
    CharSink charSink = new CharSink() {
        @Override
        public Writer openStream() throws IOException {
            return stringWriter;
        }
    };
    charSink.write(buffer);

    stringWriter.close();

    assertEquals("With Guava", stringWriter.toString());
}
```

请注意，在这里，我们使用CharSink将byte[]转换为Writer。

## 4. 与公共 IO

最后，让我们检查一下我们的 Commons IO 解决方案：

```java
@Test
public void givenUsingCommonsIO_whenConvertingByteArrayIntoWriter_thenCorrect() 
  throws IOException {
    byte[] initialArray = "With Commons IO".getBytes();
    
    Writer targetWriter = new StringBuilderWriter(
      new StringBuilder(new String(initialArray)));

    targetWriter.close();

    assertEquals("With Commons IO", targetWriter.toString());
}
```

注意：我们使用StringBuilder将byte[]转换为StringBuilderWriter。

## 5.总结

在这个简短而切题的教程中，我们展示了将byte[]转换为Writer 的3 种不同方法。