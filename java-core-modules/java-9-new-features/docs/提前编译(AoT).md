## 1. 简介

在本文中，我们介绍Java Ahead of Time(AOT)编译器，它在[JEP-295](https://openjdk.java.net/jeps/295)中进行了描述，并作为Java 9中的实验特性添加。

首先，我们将了解AOT是什么，其次，我们看一个简单的示例。第三，我们介绍AOT的一些限制，最后，我们将讨论一些可能的用例。

## 2. 什么是提前编译？

AOT编译是提高Java程序性能，尤其是JVM启动时间的一种方法。JVM执行Java字节码并将经常执行的代码编译为本机代码。这称为即时(JIT)编译。JVM根据执行期间收集的分析信息决定要JIT编译哪些代码。

虽然这种技术使JVM能够生成高度优化的代码并提高峰值性能，但启动时间可能不是最佳的，因为执行的代码尚未JIT 编译。AOT旨在改善这个所谓的预热期，用于AOT的编译器是Graal。

在本文中，我们不会详细介绍JIT和Graal。[请参阅我们的其他文章，了解Java9和10](https://www.baeldung.com/java-10-performance-improvements)的性能改进概述，以及[对 Graal JIT编译器的深入了解](https://www.baeldung.com/graal-java-jit-compiler)。

## 3. 例子

对于这个例子，我们将使用一个非常简单的类，编译它，然后看看如何使用生成的库。

### 3.1 AOT编译

下面是一个简单的类：

```java
public class JaotCompilation {

    public static void main(String[] argv) {
        System.out.println(message());
    }

    public static String message() {
        return "The JAOT compiler says 'Hello'";
    }
}

```

在使用AOT编译器之前，我们需要使用Java编译器编译该类：

```shell
javac JaotCompilation.java
```

然后我们将生成的JaotCompilation.class传递给AOT编译器，该编译器与标准Java编译器位于同一目录中：

```shell
jaotc --output jaotCompilation.so JaotCompilation.class
```

这会在当前目录中生成库jaotCompilation.so。

### 3.2 运行程序

然后我们可以执行程序：

```shell
java -XX:AOTLibrary=./jaotCompilation.so JaotCompilation
```

参数-XX:AOTLibrary接收库的相对或完整路径。或者，我们可以将库复制到Java主目录中的lib文件夹中，并且只传递库的名称。

### 3.3 验证库是否被调用和使用

通过添加-XX:+PrintAOT作为JVM参数，我们可以看到该库确实已加载：

```shell
java -XX:+PrintAOT -XX:AOTLibrary=./jaotCompilation.so JaotCompilation
```

输出将如下所示：

```shell
77    1     loaded    ./jaotCompilation.so  aot library
```

但是，这只是告诉我们库已被加载，而不是实际被使用。通过传递参数-verbose，我们可以看到库中的方法确实被调用：

```shell
java -XX:AOTLibrary=./jaotCompilation.so -verbose -XX:+PrintAOT JaotCompilation 
```

输出将包含以下行：

```shell
11    1     loaded    ./jaotCompilation.so  aot library
116    1     aot[ 1]   jaotc.JaotCompilation.<init>()V
116    2     aot[ 1]   jaotc.JaotCompilation.message()Ljava/lang/String;
116    3     aot[ 1]   jaotc.JaotCompilation.main([Ljava/lang/String;)V
The JAOT compiler says 'Hello'
```

AOT编译库包含一个类指纹，它必须与.class文件的指纹匹配。

让我们更改JaotCompilation.java类中的代码以返回不同的消息：

```java
public static String message() {
    return "The JAOT compiler says 'Good morning'";
}
```

如果我们在没有AOT编译修改后的类的情况下执行程序：

```shell
java -XX:AOTLibrary=./jaotCompilation.so -verbose -XX:+PrintAOT JaotCompilation
```

那么输出将仅包含：

```shell
11 1 loaded ./jaotCompilation.so aot library
The JAOT compiler says 'Good morning'
```

我们可以看到库中的方法不会被调用，因为类的字节码已经改变。这背后的想法是，无论是否加载了AOT编译库，程序总是会产生相同的结果。

## 4. 更多AOT和JVM参数

### 4.1 Java模块的AOT编译

AOT也可以编译一个模块：

```shell
jaotc --output javaBase.so --module java.base
```

生成的库javaBase.so大小约为320MB，并且加载需要一些时间，可以通过选择要AOT编译的包和类来减小大小。我们将在下面介绍如何做到这一点，但是，我们不会深入讨论所有细节。

### 4.2 使用编译命令进行选择性编译

为了防止Java模块的AOT编译库变得太大，我们可以添加编译命令来限制AOT编译的范围。这些命令需要在一个文本文件中，在我们的示例中，我们将使用文件complileCommands.txt：

```shell
compileOnly java.lang.
```

然后，我们将其添加到compile命令中：

```shell
jaotc --output javaBaseLang.so --module java.base --compile-commands compileCommands.txt
```

生成的库将仅包含java.lang包中的AOT编译类。

为了获得真正的性能改进，我们需要找出在JVM预热期间调用了哪些类。这可以通过添加几个JVM参数来实现：

```shell
java -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -XX:+PrintTouchedMethodsAtExit JaotCompilation
```

在本文中，我们不会深入研究这种技术。

### 4.3 单个类的AOT编译

我们可以使用参数–class-name编译单个类：

```shell
jaotc --output javaBaseString.so --class-name java.lang.String
```

生成的库将仅包含String类。

### 4.4 compile-for-tiered

默认情况下，将始终使用AOT编译的代码，并且不会对库中包含的类进行JIT编译。如果我们想在库中包含分析信息，我们可以添加参数compile-for-tiered：

```shell
jaotc --output jaotCompilation.so --compile-for-tiered JaotCompilation.class
```

库中的预编译代码将一直使用，直到字节码符合JIT编译条件。

## 5. AOT编译的可能用例

AOT的一个用例是短时间运行的程序，它在任何JIT编译发生之前完成执行。另一个用例是嵌入式环境，其中JIT是不可能的。

此时，我们还需要注意的是，AOT编译库只能从具有相同字节码的Java类中加载，因此无法通过JNI加载。

## 6. AOT和亚马逊Lambda

AOT编译代码的一个可能用例是短寿命的lambda函数，其中短启动时间很重要。在本节中，我们将了解如何在AWS Lambda上运行AOT编译的Java代码。

将AOT编译与AWS Lambda一起使用需要在与AWS上使用的操作系统兼容的操作系统上构建库。在撰写本文时，这是Amazon Linux 2。

此外，Java版本需要匹配，AWS提供了Amazon CorrettoJava11 JVM。为了有一个编译库的环境，我们将在 Docker中安装Amazon Linux 2和Amazon Corretto。

我们不会讨论使用Docker和AWS Lambda的所有细节，而只会概述最重要的步骤。有关如何使用Docker的更多信息，请参阅[此处](https://www.docker.com/get-started)的官方文档。

有关使用Java创建Lambda函数的更多详细信息，你可以查看我们的文章[AWS Lambda With Java](https://www.baeldung.com/java-aws-lambda)。

### 6.1 开发环境的配置

首先，我们需要为Amazon Linux 2拉取Docker映像并安装Amazon Corretto：

```shell
# download Amazon Linux 
docker pull amazonlinux 

# inside the Docker container, install Amazon Corretto
yum install java-11-amazon-corretto

# some additional libraries needed for jaotc
yum install binutils.x86_64
```

### 6.2 编译类和库

在我们的Docker容器中，执行以下命令：

```shell
# create folder aot
mkdir aot
cd aot
mkdir jaotc
cd jaotc
```

文件夹的名称只是一个示例，当然可以是任何其他名称。

```java
package jaotc;

public class JaotCompilation {
    public static int message(int input) {
        return input  2;
    }
}
```

下一步是编译类和库：

```shell
javac JaotCompilation.java
cd ..
jaotc -J-XX:+UseSerialGC --output jaotCompilation.so jaotc/JaotCompilation.class
```

在这里，使用与AWS上相同的垃圾收集器很重要。如果我们的库无法在AWS Lambda上加载，我们可能想通过以下命令检查实际使用的垃圾收集器：

```shell
java -XX:+PrintCommandLineFlags -version
```

现在，我们可以创建一个包含库和类文件的zip文件：

```shell
zip -r jaot.zip jaotCompilation.so jaotc/
```

### 6.3 配置AWS Lambda

最后一步是登录AWS Lamda控制台，上传zip文件并使用以下参数配置Lambda：

-   Runtime：Java 11
-   Handler：jaotc.JaotCompilation::message

此外，我们需要创建一个名为JAVA_TOOL_OPTIONS的环境变量并将其值设置为：

```shell
-XX:+UnlockExperimentalVMOptions -XX:+PrintAOT -XX:AOTLibrary=./jaotCompilation.so
```

这个变量允许我们向JVM传递参数。

最后一步是为我们的Lambda配置输入。默认是JSON输入，不能传递给我们的函数，因此我们需要将其设置为包含整数的字符串，例如“1”。

最后，我们可以执行Lambda函数，并且应该在日志中看到我们的AOT编译库已加载：

```shell
57    1     loaded    ./jaotCompilation.so  aot library
```

## 7. 总结

在本文中，我们了解了如何AOT编译Java类和模块。由于这仍然是一个实验性功能，AOT编译器并不是所有发行版的一部分；真正的例子仍然很少见，这将取决于Java社区来找出应用AOT的最佳用例。