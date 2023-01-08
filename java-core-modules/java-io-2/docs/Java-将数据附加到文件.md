## 1. 概述

在本快速教程中，我们将了解如何使用Java以几种简单的方式将数据附加到文件的内容中。

让我们从如何使用核心Java的FileWriter 来做到这一点开始。

## 2.使用FileWriter

这是一个简单的测试——读取现有文件，附加一些文本，然后确保正确附加：

```java
@Test
public void whenAppendToFileUsingFileWriter_thenCorrect()
  throws IOException {
 
    FileWriter fw = new FileWriter(fileName, true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write("Spain");
    bw.newLine();
    bw.close();
    
    assertThat(getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UKrn" + "USrn" + "Germanyrn" + "Spainrn");
}
```

请注意，如果我们想将数据附加到现有文件，FileWriter 的构造函数接受布尔标记。

如果我们将其设置为false，则现有内容将被替换。

## 3.使用文件输出流

接下来——让我们看看如何使用FileOutputStream执行相同的操作：

```java
@Test
public void whenAppendToFileUsingFileOutputStream_thenCorrect()
 throws Exception {
 
    FileOutputStream fos = new FileOutputStream(fileName, true);
    fos.write("Spainrn".getBytes());
    fos.close();
    
    assertThat(StreamUtils.getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UKrn" + "USrn" + "Germanyrn" + "Spainrn");
}
```

同样，FileOutputStream构造函数接受一个布尔值，该值应设置为 true 以标记我们要将数据附加到现有文件。

## 4.使用java.nio.file

接下来——我们还可以使用java.nio.file中的功能将内容附加到文件——这是在 JDK 7 中引入的：

```java
@Test
public void whenAppendToFileUsingFiles_thenCorrect() 
 throws IOException {
 
    String contentToAppend = "Spainrn";
    Files.write(
      Paths.get(fileName), 
      contentToAppend.getBytes(), 
      StandardOpenOption.APPEND);
    
    assertThat(StreamUtils.getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UKrn" + "USrn" + "Germanyrn" + "Spainrn");
}
```

## 5.使用番石榴

要开始使用 Guava，我们需要将其依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

现在，让我们看看如何开始使用 Guava 将内容附加到现有文件：

```java
@Test
public void whenAppendToFileUsingFileWriter_thenCorrect()
 throws IOException {
 
    File file = new File(fileName);
    CharSink chs = Files.asCharSink(
      file, Charsets.UTF_8, FileWriteMode.APPEND);
    chs.write("Spainrn");
	
    assertThat(StreamUtils.getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UKrn" + "USrn" + "Germanyrn" + "Spainrn");
}
```

## 6. 使用 Apache Commons IO FileUtils

最后，让我们看看如何使用 Apache Commons IO FileUtils将内容附加到现有文件。

首先，让我们将 Apache Commons IO 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

现在，让我们看一个快速示例，演示如何使用FileUtils将内容附加到现有文件：

```java
@Test
public void whenAppendToFileUsingFiles_thenCorrect()
 throws IOException {
    File file = new File(fileName);
    FileUtils.writeStringToFile(
      file, "Spainrn", StandardCharsets.UTF_8, true);
    
    assertThat(StreamUtils.getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UKrn" + "USrn" + "Germanyrn" + "Spainrn");
}
```

## 七、总结

在本文中，我们了解了如何以多种方式附加内容。