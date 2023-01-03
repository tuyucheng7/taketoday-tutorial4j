## 1. 概述

在本教程中，我们将探讨不同的Maven构建生命周期及其阶段。

我们还将讨论目标和阶段之间的核心关系。

## 2. Maven构建生命周期 

Maven构建遵循特定的生命周期来部署和分发目标项目。

有三个内置生命周期：

-   default：主生命周期，因为它负责项目部署
-   clean：清理项目并删除之前构建生成的所有文件
-   site：创建项目的站点文档

**每个生命周期都由一系列阶段组成**，default构建生命周期包含23个阶段，因为它是主要的构建生命周期。

另一方面，clean生命周期由3个阶段组成，而site生命周期由4个阶段组成。

## 3. Maven阶段

**Maven阶段(phase)代表Maven构建生命周期中的一个阶段**，每个阶段负责一个特定的任务。

以下是default构建生命周期中的一些最重要的阶段：

-   validate：检查构建所需的所有信息是否可用
-   compile：编译源代码
-   test-compile：编译测试源码
-   test：运行单元测试
-   package：将编译后的源代码打包成可分发的格式(jar，war，...)
-   integration-test：如果需要运行集成测试，则处理和部署包
-   install：将包安装到本地仓库
-   deploy：将包复制到远程仓库

有关每个生命周期阶段的完整列表，请查看[Maven参考](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference)。

阶段按特定顺序执行，这意味着如果我们使用以下命令运行特定阶段：

```bash
mvn <PHASE>
```

**它不仅会执行指定的阶段，还会执行所有前面的阶段**。

例如，如果我们运行deploy阶段，这是default构建生命周期的最后一个阶段，它也将执行deploy阶段之前的所有阶段，即整个default生命周期：

```bash
mvn deploy
```

## 4. Maven目标 

**每个阶段都是一系列目标(goal)，每个目标负责一个特定的任务**。

当我们运行一个阶段时，绑定到这个阶段的所有目标都会按顺序执行。

以下是一些阶段和与它们绑定的默认目标：

-   compiler:compile - compiler插件的compile目标绑定到compile阶段
-   compiler:testCompile绑定到test-compile阶段
-   surefire:test绑定到test阶段
-   install:install绑定到install阶段
-   jar:jar和war:war绑定到package阶段

我们可以使用以下命令列出绑定到特定阶段的所有目标及其插件：

```bash
mvn help:describe -Dcmd=PHASENAME
```

例如，要列出所有绑定到compile阶段的目标，我们可以运行：

```bash
mvn help:describe -Dcmd=compile
```

然后我们会得到示例输出：

```bash
compile' is a phase corresponding to this plugin:
org.apache.maven.plugins:maven-compiler-plugin:3.1:compile
```

如上所述，这意味着compiler插件的compile目标绑定到compile阶段。

## 5. Maven插件 

**一个Maven插件是一组目标**；但是，这些目标不一定都绑定到同一阶段。

例如，这里是Maven Failsafe插件的一个简单配置，它负责运行集成测试：

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>${maven.failsafe.version}</version>
            <executions>
                <execution>
                    <goals>
                        <goal>integration-test</goal>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

正如我们所见，Failsafe插件在这里配置了两个主要目标：

-   integration-test：运行集成测试
-   verify：验证所有集成测试是否通过

我们可以使用以下命令**列出特定插件中的所有目标**：

```bash
mvn <PLUGIN>:help
```

例如，要列出Failsafe插件中的所有目标，我们可以运行：

```bash
mvn failsafe:help
```

输出将是：

```bash
This plugin has 3 goals:

failsafe:help
  Display help information on maven-failsafe-plugin.
  Call mvn failsafe:help -Ddetail=true -Dgoal=<goal-name> to display parameter
  details.

failsafe:integration-test
  Run integration tests using Surefire.

failsafe:verify
  Verify integration tests ran using Surefire.
```

**要运行特定目标而不执行其整个阶段(以及之前的阶段)**，我们可以使用以下命令：

```bash
mvn <PLUGIN>:<GOAL>
```

例如，要从Failsafe插件运行integration-test目标，我们需要运行：

```plaintext
mvn failsafe:integration-test
```

## 6. 构建Maven项目

要构建Maven项目，我们需要通过运行其中一个阶段来执行其中一个生命周期：

```bash
mvn deploy
```

这将执行整个default生命周期，或者，我们可以在install阶段停止：

```bash
mvn install
```

但通常，我们会在新构建之前通过运行clean生命周期来首先清理项目：

```bash
mvn clean install
```

我们也可以只运行插件的特定目标：

```bash
mvn compiler:compile
```

请注意，如果我们尝试在没有指定阶段或目标的情况下构建Maven项目，我们将得到错误：

```bash
[ERROR] No goals have been specified for this build. You must specify a valid lifecycle phase or a goal
```

## 7. 总结

在本文中，我们讨论了Maven构建生命周期，以及Maven阶段和目标之间的关系。