## 一、EJDK介绍

EJDK（嵌入式 Java 开发工具包）由 Oracle 引入，以解决为所有可用的嵌入式平台提供二进制文件的问题。[我们可以在这里从 Oracle 的站点](http://www.oracle.com/technetwork/java/embedded/downloads/java-embedded-java-se-download-359230.html)下载最新的 EJDK 。

简而言之，它包含用于创建特定于平台的 JRE 的工具。

## 2.*重新创建*

*EJDK*为 Windows提供了*jrecreate.bat* ，为 Unix/Linux 平台提供了*jrecreate.sh 。*该工具有助于为我们希望使用的平台组装自定义 JRE，并被引入到：

-   尽量减少 Oracle 为每个平台发布的二进制文件
-   使为其他平台创建自定义 JRE 变得容易

以下语法用于执行*jrecreate*命令；在 Unix/Linux 中：

```bash
$jrecreate.sh -<option>/--<option> <argument-if-any>复制
```

在 Windows 中：

```bash
$jrecreate.bat -<option>/--<option> <argument-if-any>复制
```

请注意，我们可以为单个 JRE 创建添加多个选项。现在，让我们看一下该工具可用的一些选项。

## *3. jrecreate*选项

### **3.1. 目的地**

destination选项是必需的，它指定*应该*在其中创建目标 JRE 的目录：

```bash
$jrecreate.sh -d /SampleJRE复制
```

运行上述命令时，将在指定位置创建默认 JRE。命令行输出将是：

```bash
Building JRE using Options {
    ejdk-home: /installDir/ejdk1.8.0/bin/..
    dest: /SampleJRE
    target: linux_i586
    vm: all
    runtime: jre
    debug: false
    keep-debug-info: false
    no-compression: false
    dry-run: false
    verbose: false
    extension: []
}

Target JRE Size is 55,205 KB (on disk usage may be greater).
Embedded JRE created successfully复制
```

从上面的结果可以看出，目标JRE是在指定的目标目录下创建的。所有其他选项均采用默认值。

### **3.2. 简介**

*配置文件*选项用于管理目标 JRE 的大小。配置文件定义了要包含的 API 的功能。如果未指定配置文件选项，该工具将默认包含所有 JRE API：

```bash
$jrecreate.sh -d /SampleJRECompact1/ -p compact1复制
```

将创建具有*compact1*配置文件的 JRE 。我们也可以在命令中使用*––profile*代替*-p 。*命令行输出将显示以下结果：

```bash
Building JRE using Options {
    ejdk-home: /installDir/ejdk1.8.0/bin/..
    dest: /SampleJRECompact1
    target: linux_i586
    vm: minimal
    runtime: compact1 profile
    debug: false
    keep-debug-info: false
    no-compression: false
    dry-run: false
    verbose: false
    extension: []
}

Target JRE Size is 10,808 KB (on disk usage may be greater).
Embedded JRE created successfully复制
```

在上面的结果中，请注意*运行时*选项的值为*compact1*。另请注意，结果 JRE 的大小略低于 11MB，低于上一个示例中的 55MB。

配置文件设置有三个可用选项：*compact1*、*compact2*和*compact3。*

### **3.3. JVM**

*jvm*选项用于根据用户的需要使用特定的 JVM 自定义我们的目标 JRE。默认情况下，如果未指定*配置文件*和*jvm*选项，它包括所有可用的 JVM（客户端、服务器和最小） ：

```bash
$jrecreate.sh -d /SampleJREClientJVM/ --vm client复制
```

将创建一个带有*客户端jvm 的 JRE。*命令行输出将显示以下结果：

```bash
Building JRE using Options {
    ejdk-home: /installDir/ejdk1.8.0/bin/..
    dest: /SampleJREClientJVM
    target: linux_i586
    vm: Client
    runtime: jre
    debug: false
    keep-debug-info: false
    no-compression: false
    dry-run: false
    verbose: false
    extension: []
}

Target JRE Size is 46,217 KB (on disk usage may be greater).
Embedded JRE created successfully复制
```

在上面的结果中，请注意*vm*选项的值为*Client*。我们还可以使用此选项指定其他 JVM，如*server*和*minimal*。

### **3.4. 扩大**

扩展选项用于包含对目标 JRE 的各种允许的扩展*。*默认情况下，没有添加任何扩展：

```bash
$jrecreate.sh -d /SampleJRESunecExt/ -x sunec复制
```

将创建具有*扩展名*sunec（椭圆曲线密码术的安全提供程序）的 JRE 。我们也可以在命令中使用*––extension*代替*-x 。*命令行输出将显示以下结果：

```bash
Building JRE using Options {
    ejdk-home: /installDir/ejdk1.8.0/bin/..
    dest: /SampleJRESunecExt
    target: linux_i586
    vm: all
    runtime: jre
    debug: false
    keep-debug-info: false
    no-compression: false
    dry-run: false
    verbose: false
    extension: [sunec]
}

Target JRE Size is 55,462 KB (on disk usage may be greater).
Embedded JRE created successfully复制
```

在上面的结果中，请注意*扩展*选项的值为*sunec*。使用此选项可以添加多个扩展。

### **3.5. 其他选项**

除了上面讨论的主要选项之外，*jrecreate*还为用户提供了更多选项：

-   *––help*：显示 jrecreate 工具的命令行选项摘要
-   *––debug* : 创建具有调试支持的 JRE
-   *––keep-debug-info* : 保留来自类和未签名的 JAR 文件的调试信息
-   *––dry-run*：在不实际创建 JRE 的情况下执行试运行
-   *––no-compression* : 使用未签名的 JAR 文件以未压缩格式创建 JRE
-   *––verbose*：显示所有*jrecreate*命令的详细输出

## 4。结论

在本教程中，我们学习了 EJDK 的基础知识，以及如何使用*jrecreate*工具生成特定于平台的 JRE。