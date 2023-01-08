## 1. 概述

在这个快速教程中，我们将学习如何将文件压缩到存档中以及如何解压缩存档，所有这些都使用Java提供的核心库。

这些核心库是java.util.zip包的一部分，我们可以在其中找到所有与压缩和解压缩相关的实用程序。

## 2.压缩文件

首先，让我们看一个简单的操作，压缩单个文件。

例如，我们会将名为test1.txt的文件压缩到名为compressed.zip 的存档中。


当然，我们首先从磁盘访问文件：

```java
public class ZipFile {

    public static void main(String[] args) throws IOException {
        String sourceFile = "test1.txt";
        FileOutputStream fos = new FileOutputStream("compressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        zipOut.close();
        fis.close();
        fos.close();
    }
}
```

## 3.压缩多个文件

接下来，让我们看看如何将多个文件压缩到一个 zip 文件中。我们将test1.txt和test2.txt压缩到multiCompressed.zip中：

```java
public class ZipMultipleFiles {

    public static void main(String[] args) throws IOException {
        String file1 = "src/main/resources/zipTest/test1.txt";
        String file2 = "src/main/resources/zipTest/test2.txt";
        final List<String> srcFiles = Arrays.asList(file1, file2);

        final FileOutputStream fos = new FileOutputStream(Paths.get(file1).getParent().toAbsolutePath() + "/compressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

        zipOut.close();
        fos.close();
    }
}
```

## 4.压缩目录

现在，让我们讨论如何压缩整个目录。在这里，我们将zipTest文件夹压缩到dirCompressed.zip文件中：

```java
public class ZipDirectory {
    public static void main(String[] args) throws IOException {
        String sourceFile = "zipTest";
        FileOutputStream fos = new FileOutputStream("dirCompressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(sourceFile);
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
```

注意：

-   为了压缩子目录，我们递归地遍历它们。
-   每次我们找到一个目录时，我们将其名称附加到后代的ZipEntry名称以保存层次结构。
-   我们还为每个空目录创建一个目录条目。

## 5. 将新文件附加到 Zip 文件

接下来，我们将向现有的 zip 文件中添加一个文件。例如，让我们将file3.txt添加到compressed.zip中：

```java
String file3 = "src/main/resources/zipTest/file3.txt";
Map<String, String> env = new HashMap<>();
env.put("create", "true");

Path path = Paths.get(Paths.get(file3).getParent() + "/compressed.zip");
URI uri = URI.create("jar:" + path.toUri());

try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
    Path nf = fs.getPath("newFile3.txt");
    Files.write(nf, Files.readAllBytes(Paths.get(file3)), StandardOpenOption.CREATE);
}
```

简而言之，我们使用FileSystems类提供的.newFileSystem()挂载了 zip 文件，该类从 JDK 1.7 开始可用。然后，我们在压缩文件夹中创建了一个newFile3.txt并添加了file3.txt中的所有内容。

## 6.解压缩档案

现在，让我们解压缩存档并提取其内容。

对于这个例子，我们将把compressed.zip解压到一个名为unzipTest 的新文件夹中：

```java
public class UnzipFile {

    public static void main(String[] args) throws IOException {
        String fileZip = "src/main/resources/unzipTest/compressed.zip";
        File destDir = new File("src/main/resources/unzipTest");

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
           // ...
        }

        zis.closeEntry();
        zis.close();
    }
}
```

在while循环中，我们将遍历每个ZipEntry并首先检查它是否是一个目录。如果是，那么我们将使用mkdirs()方法创建目录；否则，我们将继续创建文件：

```java
while (zipEntry != null) {
    File newFile = newFile(destDir, zipEntry);
    if (zipEntry.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
            throw new IOException("Failed to create directory " + newFile);
        }
    } else {
        // fix for Windows-created archives
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent);
        }

        // write file content
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();
    }
    zipEntry = zis.getNextEntry();
}
```

这里需要注意的是，在else分支上，我们还检查文件的父目录是否存在。这对于在 Windows 上创建的存档是必需的，其中根目录在 zip 文件中没有相应的条目。

另一个关键点在newFile()方法中：

```java
public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
}
```

newFile ()方法防止将文件写入目标文件夹之外的文件系统。[此漏洞称为 Zip Slip](https://snyk.io/research/zip-slip-vulnerability)。

## 七、总结

在本文中，我们说明了如何使用Java库来压缩和解压缩文件。