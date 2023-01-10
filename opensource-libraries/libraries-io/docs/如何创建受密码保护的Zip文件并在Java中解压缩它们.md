## 1. 概述

在之前的教程中，我们展示了如何借助java.util.zip包[在Java中压缩和解压缩。](https://www.baeldung.com/java-compress-and-uncompress)但是我们没有任何标准的Java库来创建受密码保护的 zip 文件。

在本教程中，我们将学习如何创建受密码保护的 zip 文件并使用[Zip4j](https://github.com/srikanth-lingala/zip4j)库将其解压缩。它是用于 zip 文件的最全面的Java库。

## 2.依赖关系

让我们首先将[zip4j](https://search.maven.org/search?q=g: net.lingala.zip4j a:zip4j)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>net.lingala.zip4j</groupId>
    <artifactId>zip4j</artifactId>
    <version>2.9.0</version>
</dependency>
```

## 3.压缩文件

首先，我们将使用ZipFile addFile()方法 将名为aFile.txt的文件压缩到名为compressed.zip的受密码保护的存档中：

```java
ZipParameters zipParameters = new ZipParameters();
zipParameters.setEncryptFiles(true);
zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
zipParameters.setEncryptionMethod(EncryptionMethod.AES);

ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
zipFile.addFile(new File("aFile.txt"), zipParameters);
```

setCompressionLevel行是可选的。我们可以选择FASTEST到ULTRA级别(默认为NORMAL)。

在此示例中，我们使用了 AES 加密。如果我们想使用 Zip 标准加密，只需将AES替换为ZIP_STANDARD 即可。

请注意，如果文件“aFile.txt”在磁盘上不存在，该方法将抛出异常：“net.lingala.zip4j.exception.ZipException: File does not exist: …”

要解决此问题，我们必须确保该文件是手动创建并放置在项目文件夹中，或者我们必须从Java创建它：

```java
File fileToAdd = new File("aFile.txt");
if (!fileToAdd.exists()) {
    fileToAdd.createNewFile();
}
```

此外，在我们完成新的ZipFile之后，最好关闭资源：

```java
zipFile.close();

```

## 4.压缩多个文件

让我们稍微修改一下代码，以便我们可以一次压缩多个文件：

```java
ZipParameters zipParameters = new ZipParameters();
zipParameters.setEncryptFiles(true);
zipParameters.setEncryptionMethod(EncryptionMethod.AES);

List<File> filesToAdd = Arrays.asList(
  new File("aFile.txt"),
  new File("bFile.txt")
);

ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
zipFile.addFiles(filesToAdd, zipParameters);
```

我们不使用addFile方法，而是使用addFiles()并传入一个文件列表。

## 5.压缩目录

我们可以简单地通过将addFile方法替换为addFolder来压缩文件夹：

```java
ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
zipFile.addFolder(new File("/users/folder_to_add"), zipParameters);
```

## 6. 创建一个拆分 Zip 文件

当大小超过特定限制时，我们可以使用createSplitZipFile和createSplitZipFileFromFolder方法将 zip 文件拆分为多个文件：

```java
ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
int splitLength = 1024  1024  10; //10MB
zipFile.createSplitZipFile(Arrays.asList(new File("aFile.txt")), zipParameters, true, splitLength);
zipFile.createSplitZipFileFromFolder(new File("/users/folder_to_add"), zipParameters, true, splitLength);
```

splitLength的单位是字节。

## 7. 提取所有文件

提取文件同样简单。我们可以使用extractAll()方法从compressed.zip中提取所有文件：

```java
ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
zipFile.extractAll("/destination_directory");
```

## 8. 提取单个文件

如果我们只想从compressed.zip中提取单个文件，我们可以使用extractFile()方法：

```java
ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
zipFile.extractFile("aFile.txt", "/destination_directory");
```

## 9.总结

总之，我们学习了如何创建受密码保护的 zip 文件并使用 Zip4j 库在Java中解压缩它们。