## 1. 概述

在本教程中，我们将了解如何查找.class文件的 Java 发布版本。此外，我们还将了解如何检查 jar 文件中的 Java 版本。

## 2. Java 中的.class版本

编译 Java 文件时，会生成一个.class文件。在某些情况下，我们需要找到编译后的类文件的 Java 发布版本。每个 Java 主要版本为其生成的.class文件分配一个主要版本。

在此表中，我们将.class的主版本号映射到引入该类版本的 JDK 版本，并显示该版本号的十六进制表示形式：

| Java 发布  | 类主要版本 | 十六进制 |
| :--------: | :--------: | :------: |
| Java SE 18 |     62     |   003e   |
| Java SE 17 |     61     |   003d   |
| Java SE 16 |     60     |   003c   |
| Java SE 15 |     59     |   003b   |
| Java SE 14 |     58     |   003a   |
| Java SE 13 |     57     |   0039   |
| Java SE 12 |     56     |   0038   |
| Java SE 11 |     55     |   0037   |
| Java SE 10 |     54     |   0036   |
| Java SE 9  |     53     |   0035   |
| Java SE 8  |     52     |   0034   |
| Java SE 7  |     51     |   0033   |
| Java SE 6  |     50     |   0032   |
| Java SE 5  |     49     |   0031   |
|  JDK 1.4   |     48     |   0030   |
|  JDK 1.3   |     47     |   002f   |
|  JDK 1.2   |     46     |   002e   |
|  JDK 1.1   |     45     |   002d   |

## 3. .class版本的javap命令

让我们创建一个简单的类并使用 JDK 8 构建它：

```java
public class Sample {
    public static void main(String[] args) {
        System.out.println("Baeldung tutorials");
    }
}
```

为了识别类文件的版本，我们可以使用Java类文件反汇编器[javap](https://www.baeldung.com/java-class-view-bytecode#javaCommandLine)。

下面是javap命令的语法：

```bash
javap [option] [classname]
```

让我们以Sample.class的版本为例：

```powershell
javap -verbose Sample

//stripped output ..
..
..
Compiled from "Sample.java"
public class test.Sample
  minor version: 0
  major version: 52
..
..

```

正如我们在javap命令的输出中看到的，主要版本是 52，表明它适用于 JDK8。

虽然javap提供了很多细节，但我们只关心主要版本。

对于任何基于 Linux 的系统，我们可以使用以下命令只获取主要版本：

```powershell
javap -verbose Sample | grep major
```

同样，对于 Windows 系统，这是我们可以使用的命令：

```powershell
javap -verbose Sample | findstr major
```

在我们的示例中，这为我们提供了主要版本 52。

请务必注意，此版本值并不表示该应用程序是使用相应的 JDK 构建的。类文件版本可以与用于编译的 JDK 不同。

例如，如果我们使用 JDK11 构建代码，它应该生成版本为 55 的.class文件。但是，如果我们在编译期间传递[-target](https://www.baeldung.com/java-source-target-options) 8 ，则.class文件将具有版本 52。

## 4. .class版本的hexdump

也可以使用任何十六进制编辑器检查版本。Java 类文件遵循[规范](https://en.wikipedia.org/wiki/Java_class_file)。我们看一下它的结构：

```java
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    // other details
}
```

在这里，类型u1、u2和u4分别表示一个无符号的一、二和四字节整数。u4是标识类文件格式的幻数
。它的值为 0xCAFEBABE，u2是主要版本。

对于基于 Linux 的系统，我们可以使用[hexdump](https://www.baeldung.com/linux/create-hex-dump#using-hexdump)实用程序来解析任何.class文件：

```bash
> hexdump -v Sample.class
0000000 ca fe ba be 00 00 00 34 00 22 07 00 02 01 00 0b
0000010 74 65 73 74 2f 53 61 6d 70 6c 65 07 00 04 01 00
...truncated

```

在这个例子中，我们使用JDK8编译。第一行的 7 和 8 索引提供了类文件的主要版本。因此，0034是十六进制表示，JDK8是对应的版本号(从我们前面看到的映射表)。

作为替代方案，我们可以使用hexdump直接获取小数形式的主要发行版本：

```bash
> hexdump -s 7 -n 1 -e '"%d"' Sample.class
52
```

这里，输出52是对应JDK8的类版本。

## 5.罐子版本

Java 生态系统中的 jar 文件由一组捆绑在一起的类文件组成。为了找出构建或编译的 jar 的 Java 版本，我们可以提取 jar 文件并使用[javap](https://www.baeldung.com/java-class-view-bytecode#javaCommandLine)或[hexdump](https://www.baeldung.com/linux/create-hex-dump#using-hexdump)检查.class文件版本。

jar文件中还有一个[MANIFEST.MF](https://www.baeldung.com/java-jar-manifest)文件，里面包含了一些使用的JDK的头信息。

例如，Build-Jdk或Created-By标头根据 jar 的构建方式存储 JDK 值：

```bash
Build-Jdk: 17.0.4
```

或者

```bash
Created-By: 17.0.4
```

## 5.总结

在本文中，我们学习了如何查找 . 类文件。我们看到了javap和hexdump命令及其用于查找版本的用法。此外，我们研究了如何检查 jar 文件中的 Java 版本。