## 1. 概述

Spring Roo 是一种快速应用程序开发 (RAD) 工具，旨在提供专注于 Spring Web 应用程序和更新的 Spring 技术的快速、即时的结果。它允许我们使用简单易用的命令为 Spring 应用程序生成样板代码和项目结构。

Roo 可以用作从操作系统命令行运行的独立应用程序。无需使用 Eclipse、Spring Tool Suite (STS) 或任何其他 IDE；事实上，我们可以使用任何文本编辑器来编写代码！

但是，为简单起见，我们将使用带有 Roo 扩展的 STS IDE。

## 2.安装Spring Roo

### 2.1. 要求

要遵循本教程，必须安装这些：

1.  [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2.  [STS](https://spring.io/tools)
3.  [春鲁](https://spring.io/projects/spring-roo)

### 2.2. 安装

下载并安装JavaJDK 和 STS 后，我们需要解压缩 Spring Roo 并将其添加到系统路径。

让我们创建ROO_HOME环境变量并将%ROO_HOME%bin添加到路径中。

为了验证 Roo 是否安装正确，我们可以打开命令行并执行以下命令：

```shell
mkdir baeldung
cd baeldung
roo quit
```

几秒钟后，我们将看到：

```shell
                _
 ___ _ __  _ __(_)_ __   __ _   _ __ ___   ___
/ __| '_ | '__| | '_  / _` | | '__/ _  / _ 
__  |_) | |  | | | | | (_| | | | | (_) | (_) |
|___/ .__/|_|  |_|_| |_|__, | |_|  ___/ ___/
    |_|                 |___/          2.0.0.RC1

Welcome to Spring Roo. For assistance press TAB or type "hint" then hit ENTER.
```

Roo 已安装，并且正在运行。请注意，Spring Roo 版本会有所不同，步骤和说明可能取决于实际使用的版本。

重要提示：Spring Roo 2.0 不向后兼容 1.x。

### 2.3. 添加和配置 STS 扩展

开箱即用的 STS 支持 Spring 应用程序的开发，并包括随时可用的扩展。但是，不包括 Spring Roo 扩展。因此，我们需要手动添加它。

在 STS 中，让我们转到Install New Software并将书签导入Available Software Sites。目前，书签位于%ROO_HOME%conf文件夹中。导入书签后，我们可以简单地搜索roo并安装最新版本的Spring IDE Roo Support。最后，我们会被要求重启STS。

对于详细和最新的步骤，我们可以随时查看[Spring Roo 入门](https://docs.spring.io/spring-roo/docs/2.0.x/reference/html/#getting-started-requirements)文档。

在 STS 中安装 Roo Support 后，我们需要设置扩展。就像将 Roo Support 指向%ROO_HOME%文件夹一样简单。同样，[Spring Roo 入门指南](https://docs.spring.io/spring-roo/docs/2.0.x/reference/html/#getting-started-requirements)提供了详细的操作步骤。

现在我们可以再次进入“Window”应用程序菜单并选择Show View > Roo Shell。

## 3. 第一个项目

### 3.1. 在 STS 中设置项目

在 STS 中，让我们打开Roo Shell窗口并单击“创建新 Roo 项目”图标。这将打开一个新 Roo 项目窗口。

我们将项目命名为roo并使用com.baeldung作为我们的顶级包名称。我们可以保留所有其他默认值并继续到最后使用 Roo 创建一个新项目。

在 STS 中，这将为我们运行以下命令：

```shell
project setup --topLevelPackage com.baeldung --projectName "roo" --java 8 --packaging JAR
```

如前所述，我们不需要 IDE，我们可以自己从 Roo Shell 运行该命令！为简单起见，我们使用 STS 的内置功能。

如果我们得到以下错误：

```shell
Could not calculate build plan: Plugin org.codehaus.mojo:aspectj-maven-plugin:1.8 
  or one of its dependencies could not be resolved: 
  Failed to read artifact descriptor for org.codehaus.mojo:aspectj-maven-plugin:jar:1.8
```

修复它的最简单方法是手动编辑pom.xml文件并将aspectj.plugin.version从1.8更新到1.9：

```xml
<aspectj.plugin.version>1.9</aspectj.plugin.version>
```

在这个阶段，项目中应该没有任何错误，并且会有一些自动生成的文件供我们使用。

### 3.2. 袋鼠壳

现在是时候熟悉 Roo Shell 了。Spring Roo 的主要用户界面实际上是命令提示符！

因此，让我们回到 Roo Shell 窗口。在其中，让我们通过键入“h”并按 CTRL+SPACE 来运行第一个命令：

```shell
roo> h

help    hint
```

Roo 会自动为我们建议和自动完成命令。我们可以键入“hi”，按 CTRL+SPACE，Roo 将自动建议提示命令。

Roo Shell 的另一个重要特性是上下文感知。例如，提示命令的输出将根据先前的输入而改变。

现在让我们执行提示命令，看看会发生什么：

```shell
roo> hint 
Roo requires the installation of a persistence configuration.

Type 'jpa setup' and then hit CTRL+SPACE. We suggest you type 'H'
then CTRL+SPACE to complete "HIBERNATE".

After the --provider, press CTRL+SPACE for database choices.
For testing purposes, type (or CTRL+SPACE) HYPERSONIC_IN_MEMORY.
If you press CTRL+SPACE again, you'll see there are no more options.
As such, you're ready to press ENTER to execute the command.

Once JPA is installed, type 'hint' and ENTER for the next suggestion.
```

它为我们提供了需要执行的后续步骤。现在让我们添加一个数据库：

```shell
roo> jpa setup --provider HIBERNATE --database HYPERSONIC_IN_MEMORY 
Created SRC_MAIN_RESOURCESapplication.properties
Updated SRC_MAIN_RESOURCESapplication.properties
Updated SRC_MAIN_RESOURCESapplication-dev.properties
Updated ROOTpom.xml [added dependencies org.springframework.boot:spring-boot-starter-data-jpa:, org.springframework.boot:spring-boot-starter-jdbc:, org.hsqldb:hsqldb:; added property 'springlets.version' = '1.2.0.RC1'; added dependencies io.springlets:springlets-data-jpa:${springlets.version}, io.springlets:springlets-data-jpa:${springlets.version}; added dependencies io.springlets:springlets-data-commons:${springlets.version}, io.springlets:springlets-data-commons:${springlets.version}]
```

在这个阶段，我们需要执行一些命令。在它们之间，我们总是可以运行hint命令来查看 Roo 的建议。这是一个非常有用的功能。

让我们先运行命令，然后我们将通过它们：

```shell
roo> 
entity jpa --class ~.domain.Book
field string --fieldName title --notNull 
field string --fieldName author --notNull 
field string --fieldName isbn --notNull 
repository jpa --entity ~.domain.Book
service --all 
web mvc setup
web mvc view setup --type THYMELEAF 
web mvc controller --entity ~.domain.Book --responseType THYMELEAF
```

我们现在准备运行我们的应用程序。但是，让我们回过头来看看我们做了什么。

首先，我们在src/main/java文件夹中创建了一个新的 JPA 持久化实体。接下来，我们在Book类中创建了三个String字段，为它们命名并设置为非空。

之后，我们为指定的实体生成了 Spring Data 存储库，并创建了一个新的服务接口。

最后，我们包含了 Spring MVC 配置，安装了 Thymeleaf 并创建了一个新的控制器来管理我们的实体。因为我们已经将 Thymeleaf 作为响应类型传递，所以生成的方法和视图将反映这一点。

### 3.3. 运行应用程序

让我们刷新项目并右键单击roo项目并选择Run As >Spring BootApp。

应用程序启动后，我们可以打开 Web 浏览器并转到[http://localhost:8080](http://localhost:8080/)。接下来，到 Roo 图标，我们将看到Book菜单和下面的两个选项：Create Book和List Books。我们可以使用它来向我们的应用程序添加一本书并查看已添加图书的列表。

### 3.4. 其它功能

当我们打开Book.java类文件时，我们会注意到该类带有@Roo注解。这些由 Roo Shell 添加，用于控制和自定义 AspectJ 类型间声明 (ITD) 文件的内容。我们可以在 STS 的 Package Explorer 中查看文件，方法是在“查看”菜单中取消选择“隐藏生成的 Spring Roo ITD”过滤器，或者我们可以直接从文件系统打开文件。

Roo 注解具有SOURCE保留策略。这意味着注解不会出现在已编译的类字节码中，并且在已部署的应用程序中不会对 Roo 有任何依赖。

Book.java类中另一个明显缺失的部分是getter和setter。如前所述，它们存储在单独的 AspectJ ITD 文件中。Roo 将积极为我们维护此样板代码。因此，任何类中字段的更改都将自动反映在 AspectJ ITD 中，因为 Roo 正在“监视”所有更改——通过 Roo Shell 或直接由开发人员在 IDE 中完成。

Roo 也会处理重复代码，例如toString()或equals()方法。

此外，通过删除注解并将 AspectJ ITD 推入标准Java代码，可以轻松地从项目中删除该框架，从而避免供应商锁定。

## 4. 总结

在这个快速示例中，我们设法在 STS 中安装和配置 Spring Roo，并创建了一个小项目。

我们使用 Roo Shell 来设置它，不需要编写一行实际的Java代码！我们能够在几分钟内获得一个可用的应用程序原型，Roo 为我们处理了所有样板代码。