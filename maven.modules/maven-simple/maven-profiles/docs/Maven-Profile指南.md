## 1. 概述

[Maven](https://www.baeldung.com/maven) **Profile可用于创建自定义构建配置**，例如针对测试粒度级别或特定部署环境。

在本教程中，我们将学习如何使用Maven Profile。

## 2. 一个基本的例子

通常，**当我们运行mvn package时，单元测试也会被执行**，但是如果我们**想快速打包工件并运行它以查看它是否有效**怎么办？

首先，我们将创建一个no-tests Profile，将maven.test.skip属性设置为true：

```xml
<profile>
    <id>no-tests</id>
    <properties>
        <maven.test.skip>true</maven.test.skip>
    </properties>
</profile>
```

接下来，我们将通过运行mvn package -Pno-tests命令来执行Profile。**现在创建了工件并跳过了测试**，在这种情况下，mvn package -Dmaven.test.skip命令会更容易。

但是，这只是对Maven Profile的介绍，让我们来看看一些更复杂的设置。

## 3. 声明Profile

在上一节中，我们了解了如何创建一个Profile，**我们可以通过给它们提供唯一的ID来配置任意数量的Profile**。

假设我们想创建一个只运行我们的集成测试的Profile，另一个Profile用于一组突变测试。

首先我们在pom.xml文件中为每个Profile指定一个id：

```xml
<profiles>
    <profile>
        <id>integration-tests</id>
    </profile>
    <profile>
        <id>mutation-tests</id>
    </profile>
</profiles>
```

在每个Profile元素中，**我们可以配置许多元素，例如dependencies、plugins、resources、finalName**。

因此，对于上面的示例，我们可以分别为integration-tests和mutation-tests添加插件及其依赖项。

将测试分离到Profile中可以使默认构建更快，因为它只关注单元测试。

### 3.1 Profile范围

现在，我们只是将这些Profile放在我们的pom.xml文件中，该文件仅为我们的项目声明它们。

但是，在Maven 3中，我们实际上可以将Profile添加到三个位置中的任何一个：

1.  项目特定的Profile进入项目的pom.xml文件
2.  用户特定的Profile进入用户的settings.xml文件
3.  全局Profile进入全局settings.xml文件

请注意，Maven 2确实支持第四个位置，但这在Maven 3中被删除了。

**我们尽可能在pom.xml中配置Profile**，原因是我们希望在我们的开发机器和构建机器上使用Profile，使用settings.xml更加困难且容易出错，因为我们必须自己将其分发到构建环境中。

## 4. 激活Profile

在我们创建一个或多个Profile后，**我们可以开始使用它们，或者换句话说，激活它们**。

### 4.1 查看哪些Profile处于激活状态

**让我们使用help:active-profiles目标来查看哪些Profile在我们的默认构建中处于激活状态**：

```bash
mvn help:active-profiles
```

实际上，由于我们还没有激活任何东西，因此我们得到：

```bash
The following profiles are active:
```

好吧，没什么。

我们稍后会激活它们，但很快，另一种查看激活内容的方法是**在我们的pom.xml中包含maven-help-plugin并将active-profiles目标绑定到编译阶段**：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-help-plugin</artifactId>
            <version>3.2.0</version>
            <executions>
                <execution>
                    <id>show-profiles</id>
                    <phase>compile</phase>
                    <goals>
                        <goal>active-profiles</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

现在，让我们开始使用它们吧！我们将研究几种不同的方法。

### 4.2 使用-P

其实，我们一开始就已经看到了一种方法，那就是我们**可以使用-P参数激活Profile**。

因此，让我们从启用集成测试Profile开始：

```bash
mvn package -P integration-tests
```

如果我们使用maven-help-plugin或mvn help:active-profiles -P integration-tests命令验证激活的Profile，我们将得到以下结果：

```bash
The following profiles are active:

 - integration-tests
```

如果我们想同时激活多个Profile，我们使用逗号分隔的Profile列表：

```bash
mvn package -P integration-tests,mutation-tests
```

### 4.3 默认激活

如果我们总是想执行一个Profile，我们可以默认激活一个Profile：

```xml
<profile>
    <id>integration-tests</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
</profile>
```

然后，我们可以在不指定Profile的情况下运行mvn package，并且我们可以验证集成测试Profile是否处于激活状态。

**但是，如果我们运行Maven命令并启用另一个Profile，则会跳过activeByDefault Profile**。因此，当我们运行mvn package -P mutation-tests时，只有mutation-tests Profile处于激活状态。

当我们以其他方式激活时，activeByDefault Profile也会被跳过，我们将在下一节中看到。

### 4.4 基于属性

我们可以在命令行上激活Profile，但是，有时自动激活它们会更方便。例如，**我们可以将其基于-D系统属性**：

```xml
<profile>
    <id>active-on-property-environment</id>
    <activation>
        <property>
            <name>environment</name>
        </property>
    </activation>
</profile>
```

我们现在使用mvn package -Denvironment命令激活Profile。

也可以在属性不存在的情况下激活该Profile：

```xml
<property>
    <name>!environment</name>
</property>
```

或者，**在属性具有特定值的情况下我们才激活Profile**：

```xml
<property>
    <name>environment</name>
    <value>test</value>
</property>
```

我们现在可以使用mvn package -Denvironment=test运行Profile。

最后，如果属性的值不是指定值，我们可以激活Profile：

```xml
<property>
    <name>environment</name>
    <value>!test</value>
</property>
```

### 4.5 基于JDK版本

另一种选择是基于机器上运行的JDK启用Profile，在这种情况下，如果JDK版本以11开头，我们希望启用Profile：

```xml
<profile>
    <id>active-on-jdk-11</id>
    <activation>
        <jdk>11</jdk>
    </activation>
</profile>
```

我们还可以使用JDK版本的范围，如[Maven版本范围语法](https://www.baeldung.com/maven-dependency-latest-version)中所述。

### 4.6 基于操作系统

或者，我们可以根据一些操作系统信息激活Profile。

如果我们不确定这一点，我们可以首先使用mvn enforcer:display-info命令，它会在我的机器上提供以下输出：

```bash
Maven Version: 3.8.4
JDK Version: 17.0.5 normalized as: 17.0.5
OS Info: Arch: amd64 Family: windows Name: windows 11 Version: 11.0
```

之后，我们可以配置一个仅在Windows 11上激活的Profile：

```xml
<profile>
    <id>active-on-windows-11</id>
    <activation>
        <os>
            <name>windows 11</name>
            <family>Windows</family>
            <arch>amd64</arch>
            <version>11.0</version>
        </os>
    </activation>
</profile>
```

### 4.7 基于文件

另一种选择是**在文件存在或缺失时运行Profile**。

因此，让我们创建一个测试Profile，该Profile仅在testreport.html尚不存在时才执行：

```xml
<activation>
    <file>
        <missing>target/testreport.html</missing>
    </file>
</activation>
```

## 5. 停用Profile

我们已经看到了许多激活Profile的方法，但有时我们也需要禁用一种。

**要禁用Profile，我们可以使用“!”或者”-“**。

因此，要禁用active-on-jdk-11 Profile，我们执行mvn compile -P -active-on-jdk-11命令。

## 6. 总结

在本文中，我们了解了如何使用Maven Profile，因此我们可以创建不同的构建配置。

Profile有助于在我们需要时执行构建的特定元素，这优化了我们的构建过程，并有助于更快地向开发人员提供反馈。