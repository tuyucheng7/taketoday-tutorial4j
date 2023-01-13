## 1. 概述

在本教程中，我们介绍Hamcrest文件匹配器。

## 2. Maven配置

首先，我们需要在pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 文件属性

Hamcrest提供了几个匹配器来验证常用的文件属性，让我们看看如何使用aFileNamed()结合字符串匹配器来验证文件名：

```java
@Test
void whenVerifyingFileName_thenCorrect() {
    File file = new File("src/test/resources/test1.in");
 
    assertThat(file, aFileNamed(equalToIgnoringCase("test1.in")));
}
```

我们还可以断言文件路径：

```java
@Test
void whenVerifyingFilePath_thenCorrect() {
    File file = new File("src/test/resources/test1.in");
    
    assertThat(file, aFileWithCanonicalPath(containsString("src/test/resources")));
    assertThat(file, aFileWithAbsolutePath(containsString("src/test/resources")));
}
```

也可以检查文件的大小(以字节为单位)：

```java
@Test
void whenVerifyingFileSize_thenCorrect() {
    File file = new File("src/test/resources/test1.in");

    assertThat(file, aFileWithSize(11));
    assertThat(file, aFileWithSize(greaterThan(1L)));;
}
```

最后，我们可以检查一个File是否可读可写：

```java
@Test
void whenVerifyingFileIsReadableAndWritable_thenCorrect() {
    File file = new File("src/test/resources/test1.in");

    assertThat(file, aReadableFile());
    assertThat(file, aWritableFile());        
}
```

## 4. 现有文件匹配器

如果我们想验证文件或目录是否存在，我们可以使用anExistingFile()或anExistingDirectory()匹配器：

```java
@Test
void whenVerifyingFileOrDirExist_thenCorrect() {
    File file = new File("src/test/resources/test1.in");
    File dir = new File("src/test/resources");
    
    assertThat(file, anExistingFile());
    assertThat(dir, anExistingDirectory());
    assertThat(file, anExistingFileOrDirectory());
    assertThat(dir, anExistingFileOrDirectory());
}
```

还提供了将两者结合的ExistingFileOrDirectory()匹配器可供使用。

## 5. 总结

在这篇快速文章中，我们介绍了Hamcrest文件匹配器及其使用方法。