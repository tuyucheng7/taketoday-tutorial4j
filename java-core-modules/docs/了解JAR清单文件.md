## **一、简介**

Java 存档 (JAR) 由其清单文件描述。本文探讨了它的许多功能，包括添加属性、使 JAR 可执行以及嵌入版本控制信息。

不过，让我们首先快速回顾一下什么是清单文件。

## **2.清单文件**

清单文件名为*MANIFEST.MF*，位于JAR 中的*META-INF目录下。*它只是**键和值对的列表，称为\*headers\*或\*attributes\*，分组为部分。**

这些*标头*提供的元数据可帮助我们描述 JAR 的各个方面，例如包的版本、要执行的应用程序类、类路径、签名材料等等。

## **3. 添加清单文件**

### **3.1. 默认清单**

[每当我们创建 JAR](https://www.baeldung.com/java-create-jar)时，清单文件都会自动添加。

例如，如果我们在 OpenJDK 11 中构建一个 JAR：

```bash
jar cf MyJar.jar classes/复制
```

它生成一个非常简单的清单文件：

```bash
Manifest-Version: 1.0
Created-By: 11.0.3 (AdoptOpenJDK)复制
```

### **3.2. 自定义清单**

或者，我们可以指定我们自己的清单文件。

例如，假设我们有一个名为*manifest.txt*的自定义清单文件：

```bash
Built-By: baeldung复制
```

我们可以包含此文件，当我们使用*m选项时，* *jar*会将**其与默认清单文件合并**：

```bash
jar cfm MyJar.jar manifest.txt classes/复制
```

然后，生成的清单文件是：

```bash
Manifest-Version: 1.0
Built-By: baeldung
Created-By: 11.0.3 (AdoptOpenJDK)复制
```

### **3.3. 行家**

现在，默认清单文件的内容**会根据我们使用的工具而变化。**

例如，Maven 添加了一些额外的标头：

```bash
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven 3.3.9
Built-By: baeldung
Build-Jdk: 11.0.3复制
```

我们实际上可以在我们的 pom.xml 中自定义这些标头。

比方说，我们想要指出 JAR 的创建者和包：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <archive>
            <manifest>
                <packageName>com.baeldung.java</packageName>
            </manifest>
            <manifestEntries>
                <Created-By>baeldung</Created-By>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>复制
```

这将生成一个带有自定义*包*和*创建*者标头的清单文件：

```bash
Manifest-Version: 1.0
Build-Jdk-Spec: 11
Package: com.baeldung.java
Created-By: baeldung
复制
```

有关选项的完整列表，请参阅[Maven JAR 插件文档。](https://maven.apache.org/plugins/maven-jar-plugin/)

## **4.标题**

标头必须遵循特定格式并以新行分隔：

```bash
key1: value1
Key2: value2复制
```

**有效的标头必须在冒号和值之间有一个空格**。另一个重要的一点是**文件末尾必须有一个新行**。否则，最后一个标头将被忽略。

让我们看看[规范](https://docs.oracle.com/en/java/javase/11/docs/specs/jar/jar.html#jar-manifest)中的一些标准标头和一些常见的自定义标头。

### **4.1. 主标题**

主标头通常提供一般信息：

-   *Manifest-Version*：规范的版本
-   *Created-By*：创建清单文件的工具版本和供应商
-   *Multi-Release*：如果为*true*，那么这是一个[Multi-Release Jar](https://www.baeldung.com/java-multi-release-jar)
-   *Built-By*：此自定义标头提供创建清单文件的用户的名称

### **4.2. 入口点和类路径**

如果我们的 JAR 包含可运行的应用程序，那么我们可以指定入口点。同样，我们可以提供*类路径*。通过这样做，我们避免了在我们想要运行它时必须指定它。

-   *Main-Class*：具有 main 方法的类的包和名称（无 .class 扩展名）
-   *Class-Path*：库或资源的相对路径的空格分隔列表

例如，如果我们的应用程序入口点在*Application.class*中并且它使用库和资源，那么我们可以添加所需的标头：

```bash
Main-Class: com.baeldung.Application
Class-Path: core.jar lib/ properties/复制
```

类路径包括*core.jar以及**lib*和*properties*目录中的所有文件。**这些资产是相对于 JAR 执行的位置加载的，而不是从 JAR 本身内部加载的**。换句话说，它们必须存在于 JAR 之外。

### **4.3. 包装版本和密封**

这些标准标头描述了 JAR 中的包。

-   *名称*：包
-   *Implementation-Build-Date*：实施的构建日期
-   *Implementation-Title* : 实现的标题
-   *Implementation-Vendor*：实施的供应商
-   *Implementation-Version* : 实现版本
-   *规范标题*：规范的标题
-   *规范供应商*：规范的供应商
-   *规范版本*：规范版本
-   *Sealed*：如果为 true，则包的所有类都来自同一个 JAR（默认为 false）

例如，我们在 MySQL 驱动程序 Connector/J [JAR](https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar)中找到这些清单标头。它们描述了 JAR 满足的 JDBC 规范的版本，以及驱动程序本身的版本：

```bash
Specification-Title: JDBC
Specification-Version: 4.2
Specification-Vendor: Oracle Corporation
Implementation-Title: MySQL Connector/J
Implementation-Version: 8.0.16
Implementation-Vendor: Oracle复制
```

### **4.4. 签名罐**

我们可以对 JAR 进行数字签名以增加额外的安全性和验证。虽然此过程超出了本文的范围，**但这样做会将显示每个已签名类及其编码签名的标准标头添加到清单文件中**。有关详细信息，请参阅[JAR 签名文档。](https://docs.oracle.com/en/java/javase/11/docs/specs/jar/jar.html#signed-jar-file)

### **4.5. 操作系统接口**

通常还可以看到[OSGI 包的自定义标头：](http://docs.osgi.org/reference/bundle-headers.html)

-   *捆绑名称*：标题
-   *Bundle-SymbolicName*：唯一标识符
-   *捆绑版本*：版本
-   *Import-Package*：包依赖的包和版本
-   *Export-Package*：捆绑可供使用的包和版本

请参阅我们的[OSGI 简介](https://www.baeldung.com/osgi)文章以了解有关 OSGI 包的更多信息。

## **5. 章节**

清单文件中有两种类型的部分，主要部分和每个条目部分。**主要部分中出现的标头适用于 JAR 中的所有内容**。而出**现在每个条目部分中的标头仅适用于指定的包或类**。

此外，每个条目部分中出现的标题会覆盖主要部分中的相同标题。每个条目部分通常包含有关包版本和密封以及数字签名的信息。

让我们看一个每个条目部分的简单示例：

```bash
Implementation-Title: baeldung-examples 
Implementation-Version: 1.0.1
Implementation-Vendor: Baeldung
Sealed: true

Name: com/baeldung/utils/
Sealed: false复制
```

顶部的主要部分密封了我们 JAR 中的所有包。但是，包*com.baeldung.utils*由 per-entry 部分解封。

## **六，结论**

本文概述了如何将清单文件添加到 JAR、如何使用部分和一些常用标头。清单文件的结构允许我们提供标准信息，例如版本信息。

然而，它的灵活性允许我们定义我们发现的任何相关信息来描述我们的 JAR 的内容。