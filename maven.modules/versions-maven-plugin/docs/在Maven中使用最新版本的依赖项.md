## 1. 概述

手动升级Maven依赖一直是一项繁琐的工作，尤其是在频繁发布大量库的项目中。

在本教程中，我们将学习**如何利用Versions Maven插件来保持我们的依赖项是最新的**。

最重要的是，这在实现持续集成管道时非常有用，这些管道会自动升级依赖项、测试一切是否仍然正常工作，并提交或回滚结果(以适当的为准)。

## 2. Maven版本范围语法

**早在Maven 2时代，开发人员可以指定版本范围，在该范围内无需手动干预即可升级工件**。

此语法仍然有效，已在多个项目中使用，因此值得了解：

<img src="../assets/img.png">

尽管如此，我们应该尽可能避免它，转而使用Versions Maven插件，因为与让Maven自己处理整个操作相比，从外部推进具体版本无疑给了我们更多的控制权。

### 2.1 弃用的语法

Maven 2还提供了两个特殊的元版本值来实现结果：LATEST和RELEASE。

LATEST查找可能的最新版本，而RELEASE查找最新的非SNAPSHOT版本。

实际上，**它们对于常规依赖项解析仍然绝对有效**。

然而，这种遗留的升级方法在CI需要可再现性的地方导致了不可预测性。因此，[它们已被弃用用于插件依赖项](https://cwiki.apache.org/confluence/display/MAVEN/Maven+3.x+Compatibility+Notes#Maven3.xCompatibilityNotes-PluginMetaversionResolution)解析。

## 3. Versions Maven插件

[Versions Maven插件](https://www.mojohaus.org/versions-maven-plugin/index.html)是当今处理版本管理的事实上的标准方法。

从远程仓库之间的高级比较到SNAPSHOT版本的低级时间戳锁定，其庞大的目标列表使我们能够处理涉及依赖项的项目的各个方面。

虽然其中许多不在本教程的讨论范围内，但让我们仔细看看那些对我们升级过程有帮助的内容。

### 3.1 测试用例

在开始之前，让我们定义我们的测试用例：

-   三个带有硬编码版本的RELEASE
-   一个带有属性版本的RELEASE，以及
-   一个快照

```xml
<dependencies>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.3</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-collections4</artifactId>
        <version>4.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-compress</artifactId>
        <version>${commons-compress-version}</version>
    </dependency>
    <dependency>
        <groupId>commons-beanutils</groupId>
        <artifactId>commons-beanutils</artifactId>
        <version>1.9.1-SNAPSHOT</version>
    </dependency>
</dependencies>

<properties>        
    <commons-compress-version>1.15</commons-compress-version>
</properties>
```

最后，让我们在定义插件时从流程中排除一个工件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>versions-maven-plugin</artifactId>
            <version>2.7</version>
            <configuration>
                <excludes>
                    <exclude>org.apache.commons:commons-collections4</exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 4. 显示可用更新

首先，**要简单地了解我们是否可以以及如何更新我们的项目，适合这项工作的工具是**[versions:display-dependency-updates](https://www.mojohaus.org/versions-maven-plugin/display-dependency-updates-mojo.html)：

```bash
mvn versions:display-dependency-updates
```

<img src="../assets/img_1.png">

正如我们所看到的，**该过程包括每个RELEASE版本，它甚至包括commons-collections4，因为配置中的排除指的是更新过程，而不是发现过程**。

相比之下，它忽略了SNAPSHOT，因为它是一个开发版本，自动更新通常不安全。

## 5. 更新依赖

第一次运行更新时，该插件会创建名为pom.xml.versionsBackup的pom.xml备份。

虽然每次迭代都会更改pom.xml，但备份文件将保留项目的原始状态，直到用户提交(通过mvn versions:commit)或恢复(通过mvn versions:revert)整个过程。

### 5.1 将SNAPSHOT转换为RELEASE

有时项目会包含一个SNAPSHOT(仍在大量开发中的版本)。

我们可以使用[versions:use-releases](https://www.mojohaus.org/versions-maven-plugin/use-releases-mojo.html)来检查对应的RELEASE是否已经发布，甚至可以同时将我们的SNAPSHOT转换成那个RELEASE：

```bash
mvn versions:use-releases
```

<img src="../assets/img_2.png">

### 5.2 更新到下一个版本

**我们可以使用**[versions:use-next-releases](https://www.mojohaus.org/versions-maven-plugin/use-next-releases-mojo.html)**将每个非SNAPSHOT依赖项移植到它最近的版本**：

```bash
mvn versions:use-next-releases
```

<img src="../assets/img_3.png">

我们可以清楚地看到插件更新了commons-io、commons-lang3，甚至commons-beanutils，它不再是SNAPSHOT，而是他们的下一个版本。

**最重要的是，它忽略了插件配置中排除的commons-collections4和通过属性动态指定版本号的commons-compress**。

### 5.3 更新到最新版本

将每个非SNAPSHOT依赖项更新到其最新版本的工作方式相同，只需将目标更改为[versions:use-latest-releases](https://www.mojohaus.org/versions-maven-plugin/use-latest-releases-mojo.html)：

```bash
mvn versions:use-latest-releases
```

<img src="../assets/img_4.png">

## 6. 过滤掉不需要的版本

**如果我们想忽略某些版本，**[可以调整插件配置](https://www.mojohaus.org/versions-maven-plugin/version-rules.html#Ignoring_certain_versions)**以从外部文件动态加载规则**：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>2.7</version>
    <configuration>
        <rulesUri>http://www.mycompany.com/maven-version-rules.xml</rulesUri>
    </configuration>
</plugin>
```

最值得注意的是，<rulesUri\>还可以引用本地文件：

```xml
<rulesUri>file:///home/andrea/maven-version-rules.xml</rulesUri>
```

### 6.1 全局忽略版本

我们可以配置我们的规则文件，以便它**忽略与特定正则表达式匹配的版本**：

```xml
<ruleset comparisonMethod="maven"
         xmlns="http://mojo.codehaus.org/versions-maven-plugin/rule/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://mojo.codehaus.org/versions-maven-plugin/rule/2.0.0 
         http://mojo.codehaus.org/versions-maven-plugin/xsd/rule-2.0.0.xsd">
    <ignoreVersions>
        <ignoreVersion type="regex">.*-beta</ignoreVersion>
    </ignoreVersions>
</ruleset>
```

### 6.2 在每条规则的基础上忽略版本

最后，如果我们的需求更具体，我们可以构建一组规则：

```xml

<ruleset comparisonMethod="maven"
         xmlns="http://mojo.codehaus.org/versions-maven-plugin/rule/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://mojo.codehaus.org/versions-maven-plugin/rule/2.0.0  
         http://mojo.codehaus.org/versions-maven-plugin/xsd/rule-2.0.0.xsd">
    <rules>
        <rule groupId="com.mycompany.maven" comparisonMethod="maven">
            <ignoreVersions>
                <ignoreVersion type="regex">.*-RELEASE</ignoreVersion>
                <ignoreVersion>2.1.0</ignoreVersion>
            </ignoreVersions>
        </rule>
    </rules>
</ruleset>
```

## 7. 总结

我们已经了解了如何以安全、自动且符合Maven 3的方式检查和更新项目的依赖项。

与往常一样，源代码可在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/maven-modules/versions-maven-plugin)获得，还有一个脚本可以帮助你逐步展示所有内容，而且并不复杂。

要查看它的实际效果，只需下载项目并在终端中运行(如果使用Windows，则在Git Bash中运行)：

```bash
./run-the-demo.sh
```