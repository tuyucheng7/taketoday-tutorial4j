## 1. 概述

[R](https://www.r-project.org/)是一种用于统计的流行编程语言。由于它具有多种可用的函数和包，因此将 R 代码嵌入其他语言的要求并不少见。

在本文中，我们将了解一些将 R 代码集成到Java中的最常见方法。

## 2.R脚本

对于我们的项目，我们将从实现一个非常简单的 R 函数开始，该函数将向量作为输入并返回其值的平均值。我们将在专用文件中定义它：

```plaintext
customMean <- function(vector) {
    mean(vector)
}
```

在本教程中，我们将使用Java辅助方法来读取此文件并将其内容作为String返回：

```java
String getMeanScriptContent() throws IOException, URISyntaxException {
    URI rScriptUri = RUtils.class.getClassLoader().getResource("script.R").toURI();
    Path inputScript = Paths.get(rScriptUri);
    return Files.lines(inputScript).collect(Collectors.joining());
}
```

现在，让我们看一下从Java调用此函数的不同选项。

## 3. 来电

我们要考虑的第一个库是[RCaller](https://github.com/jbytecode/rcaller)，它可以通过在本地机器上生成一个专用的 R 进程来执行代码。

由于 RCaller 可以从[Maven Central](https://search.maven.org/classic/#artifactdetails|com.github.jbytecode|RCaller|3.0|jar)获得，我们可以将它包含在我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.jbytecode</groupId>
    <artifactId>RCaller</artifactId>
    <version>3.0</version>
</dependency>
```

接下来，让我们编写一个自定义方法，使用我们的原始 R 脚本返回我们的值的平均值：

```java
public double mean(int[] values) throws IOException, URISyntaxException {
    String fileContent = RUtils.getMeanScriptContent();
    RCode code = RCode.create();
    code.addRCode(fileContent);
    code.addIntArray("input", values);
    code.addRCode("result <- customMean(input)");
    RCaller caller = RCaller.create(code, RCallerOptions.create());
    caller.runAndReturnResult("result");
    return caller.getParser().getAsDoubleArray("result")[0];
}
```

在这个方法中，我们主要使用两个对象：

-   RCode，它代表我们的代码上下文，包括我们的函数、它的输入和调用语句
-   RCaller，它让我们运行我们的代码并取回结果

重要的是要注意RCaller 不适合小而频繁的计算，因为启动 R 进程需要时间。这是一个明显的缺点。

此外，RCaller 仅适用于安装在本地计算机上的R。

## 4. Renjin

[Renjin](https://www.renjin.org/)是 R 集成领域中另一个流行的解决方案。它被更广泛地采用，并且还提供企业支持。

将 Renjin 添加到我们的项目中并不那么简单，因为我们必须添加[Mulesoft](https://repository.mulesoft.org/nexus/content/repositories/public/)存储库以及 Maven 依赖项：

```xml
<repositories>
    <repository>
        <id>mulesoft</id>
        <name>Mulesoft Repository</name>
        <url>https://repository.mulesoft.org/nexus/content/repositories/public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.renjin</groupId>
        <artifactId>renjin-script-engine</artifactId>
        <version>RELEASE</version>
    </dependency>
</dependencies>
```

再一次，让我们为我们的 R 函数构建一个Java包装器：

```java
public double mean(int[] values) throws IOException, URISyntaxException, ScriptException {
    RenjinScriptEngine engine = new RenjinScriptEngine();
    String meanScriptContent = RUtils.getMeanScriptContent();
    engine.put("input", values);
    engine.eval(meanScriptContent);
    DoubleArrayVector result = (DoubleArrayVector) engine.eval("customMean(input)");
    return result.asReal();
}
```

正如我们所见，这个概念与 RCaller 非常相似，尽管不那么冗长，因为我们可以使用eval方法直接按名称调用函数。

Renjin 的主要优点是它不需要安装 R，因为它使用基于 JVM 的解释器。然而，Renjin 目前并不能 100% 兼容 GNU R。

## 5. 保留

到目前为止，我们审查过的库是在本地运行代码的不错选择。但是，如果我们想让多个客户端调用我们的 R 脚本怎么办？这就是[Rserve](https://www.rforge.net/Rserve/index.html)发挥作用的地方，让我们通过 TCP 服务器在远程机器上运行 R 代码。

设置 Rserve 包括安装相关包和启动服务器加载我们的脚本，通过 R 控制台：

```plaintext
> install.packages("Rserve")
...
> library("Rserve")
> Rserve(args = "--RS-source ~/script.R")
Starting Rserve...
```

接下来，我们现在可以像往常一样通过添加[Maven 依赖](https://search.maven.org/classic/#artifactdetails|org.rosuda.REngine|Rserve|1.8.1|jar)项将 Rserve 包含在我们的项目中：

```xml
<dependency>
    <groupId>org.rosuda.REngine</groupId>
    <artifactId>Rserve</artifactId>
    <version>1.8.1</version>
</dependency>
```

最后，让我们将 R 脚本包装到Java方法中。在这里，我们将使用带有服务器地址的RConnection对象，如果未提供，则默认为 127.0.0.1:6311：

```java
public double mean(int[] values) throws REngineException, REXPMismatchException {
    RConnection c = new RConnection();
    c.assign("input", values);
    return c.eval("customMean(input)").asDouble();
}
```

## 6.FastR

我们要讨论的最后一个库是[FastR](https://github.com/oracle/fastr)。[一个基于GraalVM](https://www.graalvm.org/)的高性能 R 实现。在撰写本文时，FastR 仅适用于 Linux 和 Darwin x64 系统。

为了使用它，我们首先需要从官方网站安装GraalVM。之后，我们需要使用 Graal Component Updater 安装 FastR 本身，然后运行它附带的配置脚本：

```bash
$ bin/gu install R
...
$ languages/R/bin/configure_fastr
```

这次我们的代码将依赖于[Polyglot](https://www.graalvm.org/reference-manual/polyglot-programming/)，GraalVM 内部 API，用于在Java中嵌入不同的来宾语言。由于 Polyglot 是通用 API，我们指定要运行的代码的语言。此外，我们将使用c R 函数将输入转换为向量：

```java
public double mean(int[] values) {
    Context polyglot = Context.newBuilder().allowAllAccess(true).build();
    String meanScriptContent = RUtils.getMeanScriptContent(); 
    polyglot.eval("R", meanScriptContent);
    Value rBindings = polyglot.getBindings("R");
    Value rInput = rBindings.getMember("c").execute(values);
    return rBindings.getMember("customMean").execute(rInput).asDouble();
}
```

遵循这种方法时，请记住它使我们的代码与 JVM 紧密耦合。要了解有关 GraalVM 的更多信息，请查看我们关于[GraalJavaJIT 编译器](https://www.baeldung.com/graal-java-jit-compiler)的文章。

## 七. 总结

在本文中，我们介绍了一些在Java中集成 R 的最流行的技术. 总结一下：

-   RCaller 更易于集成，因为它在 Maven Central 上可用
-   Renjin 提供企业支持，不需要在本地机器上安装 R，但它不是 100% 兼容 GNU R
-   Rserve 可用于在远程服务器上执行 R 代码
-   FastR 允许与Java无缝集成，但使我们的代码依赖于 VM，并且并非适用于所有操作系统