## 一、简介

在本教程中，我们将快速了解 Maven Reactor 的基本概念及其在[Maven](https://www.baeldung.com/maven)生态系统中的位置。

我们将从介绍 Maven Reactor 开始。接下来，我们将设置一个具有模块间依赖关系的[多模块 Maven 项目](https://www.baeldung.com/maven-multi-module)的简单示例，并查看 Reactor 的运行情况以整理构建依赖关系。我们将触及一些可以微调 Maven Reactor 行为的可用标志。最后，我们将总结使用 Reactor 的一些好处。

## 2. Maven Reactor 基础知识

**Maven Reactor 是 Maven 的内置部分，负责管理项目依赖和构建**。**它负责 Maven 构建的执行，并确保项目以正确的顺序构建以满足依赖关系。Maven Reactor 的真正好处可以在具有许多模块间依赖关系的复杂多模块项目中得到体现。** 

**Reactor 使用[有向无环图\*(DAG)\*](https://www.baeldung.com/cs/dag-applications)来确定项目的构建顺序。**

它作为 Maven 核心的一部分执行以下功能：

-   收集所有可用于构建的模块
-   将项目组织成适当的构建顺序
-   依次执行选定的项目

## 4. 示例用例

让我们考虑一个涉及开发用于管理患者信息的基于 Web 的应用程序的项目。该项目由三个模块组成：

1.  patient *-web*模块——该模块用作应用程序的用户界面
2.  患者*数据*模块——该模块处理所有数据库 CRUD 操作
3.  患者*域*模块——该模块包含应用程序使用的域实体

在这个项目中，*patient-web*模块依赖于其他两个模块，因为它从持久存储中检索和显示数据。另一方面，*患者数据*模块依赖于*患者域*模块，因为它需要访问域实体以执行 CRUD 操作。请务必注意，*患者数据*模块独立于其他两个模块。

### 4.1. Maven 设置

为了实现我们的简单示例，让我们建立一个名为*sample-reactor-project 的多模块项目，它*包含三个模块。这些模块中的每一个都将用于前面提到的目的：

[![img](https://www.baeldung.com/wp-content/uploads/2023/03/Maven-reactor-project-300x169.png)](https://www.baeldung.com/wp-content/uploads/2023/03/Maven-reactor-project.png)

 

此时，让我们窥视一下 Project *POM：*

```java
<artifactId>maven-reactor</artifactId>
<version>1.0-SNAPSHOT</version>
<name>maven-reactor</name>
<packaging>pom</packaging>
<modules>
    <module>patient-web</module>
    <module>patient-data</module>
    <module>patient-domain</module>
</modules>复制
```

**本质上，我们在这里定义了一个多模块项目，所有三个模块都在项目pom.xml 的\*<module> ..</module>标签内声明\**。\***

现在，让我们看一下*患者数据*模块的*POM*：

```java
<artifactId>com.baeldung</artifactId>
<name>patient-data</name>
<parent>
    <groupId>com.baeldung</groupId>
    <artifactId>maven-reactor</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>
<dependencies>
    <dependency>
        <groupId>com.baeldung</groupId>
        <artifactId>patient-domain</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>复制
```

**在这里，我们可以看到\*患者数据\* 取决于 \*患者域。\***

对于我们的用例，我们假设*患者域*模块独立于其余模块并且可以独立构建。它的*POM*看起来像这样：

```java
<artifactId>patient-domain</artifactId>
<name>patient-domain</name>
    <parent>
        <groupId>com.baeldung</groupId>
        <artifactId>maven-reactor</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
</project>复制
```

**最后，\*patient-web\*应该同时依赖于\*patient-data\*和\*patient-domain：\***

```xml
<artifactId>patient-web</artifactId>
<version>1.0-SNAPSHOT</version>
<name>patient-web</name>
    <parent>
        <groupId>com.baeldung</groupId>
        <artifactId>maven-reactor</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>com.baeldung</groupId>
            <artifactId>patient-data</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    <dependency>
        <groupId>com.baeldung</groupId>
        <artifactId>patient-domain</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>复制
```

### 4.2. Maven 反应堆在行动

要查看 Reactor 的运行情况，让我们转到项目的父目录*(maven-reactor)*并执行*mvn clean install*。

此时，Maven 将使用 Reactor 执行以下任务：

1.  收集项目中的所有可用模块（在本例中为patient *-web*、*patient-data**和**patient-domain ）*
2.  **根据模块的依赖关系确定构建模块的正确顺序（在这种情况下，\*patient-domain必须在\**patient-data\*之前构建，而 patient-data 必须在\*patient-web\*之前构建）**
3.  以正确的顺序构建每个模块，确保正确解决依赖关系

这是成功构建的 Reactor 构建顺序：

[![img](https://www.baeldung.com/wp-content/uploads/2023/03/build-order.png)](https://www.baeldung.com/wp-content/uploads/2023/03/build-order.png)

 

## 5. 配置 Reactor

虽然默认情况下 Reactor 是 Maven 的一部分，但我们仍然可以通过使用几个[命令行开关](https://www.baeldung.com/maven-arguments)来修改它的行为。这些开关被认为是必不可少的，因为它们允许我们控制 Reactor 如何构建我们的项目。一些需要考虑的基本开关是：

-   *–resume-from：*允许我们从特定项目恢复反应器，以防它在构建过程中失败
-   *–also-make：*在反应器中构建指定的项目及其任何依赖项
-   *--also-make-dependents：*构建指定的项目和任何依赖它们的项目
-   ***–fail-fast：\* 每当模块构建失败时立即停止整体构建（默认）**
-   *–fail-at-end：* 即使特定模块构建失败，此选项也会继续构建反应堆，并在最后报告所有失败的模块
-   *–non-recursive：* 使用这个选项，我们可以禁用Reactor构建，只构建当前目录下的项目，即使项目的pom声明了其他模块

通过使用这些选项，我们可以微调反应器的行为，并按照我们的需要构建我们的项目。

## 六，结论

在本文中，我们快速了解了使用 Maven Reactor 作为 Apache Maven 生态系统的一部分来构建多模块复杂项目的好处，它消除了开发人员解决依赖关系和构建顺序的责任，并减少了建造时间。