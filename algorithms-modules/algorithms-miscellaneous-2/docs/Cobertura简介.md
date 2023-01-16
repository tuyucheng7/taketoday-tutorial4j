## 1. 概述

在本文中，我们将演示使用[Cobertura](https://cobertura.github.io/cobertura/)生成代码覆盖率报告的几个方面。

简单地说，Cobertura 是一个报告工具，可以计算代码库的测试覆盖率——Java 项目中单元测试访问的分支/行的百分比。

## 2. Maven 插件

### 2.1. Maven 配置

为了开始计算Java 项目中的[代码覆盖率](https://www.baeldung.com/cs/code-coverage)，你需要在报告部分下的pom.xml文件中声明 Cobertura Maven 插件：

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>cobertura-maven-plugin</artifactId>
            <version>2.7</version>
        </plugin>
    </plugins>
</reporting>
```

你始终可以在[Maven 中央存储库](https://search.maven.org/classic/#search|gav|1|g%3A"org.codehaus.mojo" AND a%3A"cobertura-maven-plugin")中查看插件的最新版本。

完成后，继续运行 Maven，将cobertura:cobertura指定为目标。

这将创建一个详细的 HTML 样式报告，显示通过代码检测收集的代码覆盖率统计信息：

[![cob-e1485730773190](https://www.baeldung.com/wp-content/uploads/2017/02/cob-e1485730773190.png)](https://www.baeldung.com/wp-content/uploads/2017/02/cob-e1485730773190.png)

行覆盖率指标显示单元测试运行中执行了多少语句，而分支覆盖率指标则关注这些测试覆盖了多少分支。

对于每个条件，你有两个分支，所以基本上，你最终会拥有两倍于条件的分支。

复杂性因子反映了代码的复杂性——当代码中的分支数量增加时，它会增加。

从理论上讲，你拥有的分支越多，你需要实施的测试就越多，以提高分支覆盖率分数。

### 2.2. 配置代码覆盖率计算和检查

你可以使用ignore和exclude标记从代码检测中忽略/排除一组特定的类：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>cobertura-maven-plugin</artifactId>
    <version>2.7</version>
    <configuration>
        <instrumentation>
            <ignores>
                <ignore>com/baeldung/algorithms/dijkstra/</ignore>
            </ignores>
            <excludes>
                <exclude>com/baeldung/algorithms/dijkstra/</exclude>
            </excludes>
        </instrumentation>
    </configuration>
</plugin>
```

计算代码覆盖率后进入检查阶段。检查阶段确保达到一定程度的代码覆盖率。

下面是一个关于如何配置检查阶段的基本示例：

```xml
<configuration>
    <check>
        <haltOnFailure>true</haltOnFailure>
        <branchRate>75</branchRate>
        <lineRate>85</lineRate>
        <totalBranchRate>75</totalBranchRate>
        <totalLineRate>85</totalLineRate>
        <packageLineRate>75</packageLineRate>
        <packageBranchRate>85</packageBranchRate>
        <regexes>
            <regex>
                <pattern>com.baeldung.algorithms.dijkstra.</pattern>
                <branchRate>60</branchRate>
                <lineRate>50</lineRate>
             </regex>
        </regexes>
    </check>
</configuration>
```

使用haltOnFailure标志时，如果指定检查之一失败，Cobertura 将导致构建失败。

branchRate /lineRate标记指定代码检测后所需的最低可接受分支/线路覆盖率分数。这些检查可以使用packageLineRate/packageBranchRate标签扩展到包级别。

也可以使用regex标记为名称遵循特定模式的类声明特定规则检查。在上面的示例中，我们确保com.baeldung.algorithms.dijkstra包及以下包中的类必须达到特定的行/分支覆盖率分数。

## 3.日食插件

### 3.1. 安装

Cobertura 也可用作名为eCobertura的 Eclipse 插件。为了安装eCobertura for Eclipse，你需要按照以下步骤操作并安装 Eclipse 3.5 或更高版本：

第 1 步：从 Eclipse 菜单中，选择帮助→安装新软件。然后，在处理该字段时，输入http://ecobertura.johoop.de/update/：

[![cob3-e1485814235220](https://www.baeldung.com/wp-content/uploads/2017/02/cob3-e1485814235220.png)](https://www.baeldung.com/wp-content/uploads/2017/02/cob3-e1485814235220.png)

第二步：选择eCobertura Code Coverage，点击“下一步”，然后按照安装向导中的步骤进行。

现在安装了eCobertura ，重新启动 Eclipse 并在Windows → Show View → Other → Cobertura下显示覆盖会话视图。

[![cob3-e1485814235220-1](https://www.baeldung.com/wp-content/uploads/2017/02/cob3-e1485814235220-1.png)](https://www.baeldung.com/wp-content/uploads/2017/02/cob3-e1485814235220-1.png)

### 3.2. 使用 Eclipse Kepler 或更高版本

对于较新版本的 Eclipse(Kepler、Luna 等)，安装eCobertura可能会导致一些与 JUnit 相关的问题——与 Eclipse 一起打包的较新版本的 JUnit 与eCobertura的依赖项检查器不完全兼容：

```plaintext
Cannot complete the install because one or more required items could not be found.
  Software being installed: eCobertura 0.9.8.201007202152 (ecobertura.feature.group
     0.9.8.201007202152)
  Missing requirement: eCobertura UI 0.9.8.201007202152 (ecobertura.ui 
     0.9.8.201007202152) requires 'bundle org.junit4 0.0.0' but it could not be found
  Cannot satisfy dependency:
    From: eCobertura 0.9.8.201007202152 
    (ecobertura.feature.group 0.9.8.201007202152)
    To: ecobertura.ui [0.9.8.201007202152]
```

作为解决方法，你可以下载旧版本的 JUnit 并将其放入 Eclipse 插件文件夹中。

这可以通过从%ECLIPSE_HOME%/plugins中删除文件夹org.junit.来完成，然后从与eCobertura兼容的旧 Eclipse 安装中相同的文件夹。

完成后，重新启动 Eclipse IDE 并使用相应的更新站点重新安装插件。

### 3.3. Eclipse 中的代码覆盖率报告

为了通过单元测试计算代码覆盖率，右键单击你的项目/测试以打开上下文菜单，然后选择选项Cover As → JUnit Test。

在Coverage Session视图下，你可以查看每个类的线路/分支覆盖报告：

[![无题-e1487178259898](https://www.baeldung.com/wp-content/uploads/2017/02/Sans-titre-e1487178259898.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Sans-titre-e1487178259898.png)

Java 8 用户在计算代码覆盖率时可能会遇到一个常见的错误：

```plaintext
java.lang.VerifyError: Expecting a stackmap frame at branch target ...
```

在这种情况下，由于新版本的Java中引入了更严格的字节码验证器，Java 抱怨某些方法没有正确的堆栈映射。

这个问题可以通过在Java虚拟机中禁用验证来解决。

为此，请右键单击你的项目以打开上下文菜单，选择Cover As，然后打开Coverage Configurations视图。在参数选项卡中，将-noverify标志添加为 VM 参数。最后，点击覆盖率按钮启动覆盖率计算。

你还可以使用标志-XX:-UseSplitVerifier，但这仅适用于Java6 和 7，因为Java8 不再支持拆分验证器。

## 4. 总结

在本文中，我们简要展示了如何使用 Cobertura 计算Java项目中的代码覆盖率。我们还描述了在 Eclipse 环境中安装eCobertura所需的步骤。

Cobertura 是一个很棒但简单的代码覆盖工具，但没有得到积极维护，因为它目前被[JaCoCo](https://www.baeldung.com/jacoco)等更新更强大的工具超越。