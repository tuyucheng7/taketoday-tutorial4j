## 1. 概述

在本文中，我们将演示**使用[Cobertura](https://cobertura.github.io/cobertura/)生成代码覆盖率报告的几个方面**。

简单地说，Cobertura是一个报告工具，可以计算代码库的测试覆盖率-Java项目中单元测试访问的分支/行的百分比。

## 2. Maven插件

### 2.1 Maven配置

为了开始计算Java项目中的[代码覆盖率](https://www.baeldung.com/cs/code-coverage)，你需要**在pom.xml文件中的reporting部分下声明Cobertura Maven插件**：

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

你始终可以在[Maven中央仓库](https://central.sonatype.com/artifact/org.codehaus.mojo/cobertura-maven-plugin/2.7)中查看插件的最新版本。

完成后，继续运行Maven，将cobertura:cobertura指定为目标。

这将创建一个详细的HTML样式报告，显示通过代码检测收集的代码覆盖率统计信息：

<img src="../assets/img.png">

**行覆盖率指标显示在单元测试运行中执行的语句数，而分支覆盖率指标则侧重这些测试覆盖了多少分支**。

对于每个条件，你有两个分支，所以基本上，你最终会拥有两倍于条件的分支。

**复杂性因子反映了代码的复杂性**-当代码中的分支数量增加时，复杂性因子也会增加。

从理论上讲，你拥有的分支越多，你需要实施的测试就越多，以提高分支覆盖率分数。

### 2.2 配置代码覆盖率计算和检查

你可以使用ignore和exclude标签从代码检测中忽略/排除一组特定的类：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>cobertura-maven-plugin</artifactId>
    <version>2.7</version>
    <configuration>
        <instrumentation>
            <ignores>
                <ignore>cn/tuyucheng/taketoday/algorithms/dijkstra/*</ignore>
            </ignores>
            <excludes>
                <exclude>cn/tuyucheng/taketoday/algorithms/dijkstra/*</exclude>
            </excludes>
        </instrumentation>
    </configuration>
</plugin>
```

计算代码覆盖率后进入check阶段。**check阶段确保达到一定程度的代码覆盖率**。

下面是一个关于如何配置check阶段的基本示例：

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
                <pattern>cn.tuyucheng.taketoday.algorithms.dijkstra.*</pattern>
                <branchRate>60</branchRate>
                <lineRate>50</lineRate>
            </regex>
        </regexes>
    </check>
</configuration>
```

使用haltOnFailure标志时，如果指定的检查之一失败，Cobertura将导致构建失败。

branchRate/lineRate标签指定代码检测后所需的最低可接受分支/线路覆盖率分数。这些检查可以使用packageLineRate/packageBranchRate标签扩展到包级别。

也可以使用regex标签为名称遵循特定模式的类声明特定规则检查。在上面的示例中，我们确保cn.tuyucheng.taketoday.algorithms.dijkstra包及以下包中的类必须达到特定的行/分支覆盖率分数。

## 3. Eclipse插件

### 3.1 安装

Cobertura也可以作为一个名为eCobertura的Eclipse插件使用。为了安装eCobertura for Eclipse，你需要按照以下步骤操作并安装Eclipse 3.5或更高版本：

**第1步**：从Eclipse菜单中，选择Help → Install New Software。然后，在Work with字段中，输入http://ecobertura.johoop.de/update/：

<img src="../assets/img_1.png">

**第2步**：选择eCobertura Code Coverage，点击“Next”，然后按照安装向导中的步骤操作。

现在eCobertura已安装，重新启动Eclipse并在Windows → Show View → Other → Cobertura下显示覆盖率会话视图。

<img src="../assets/img_2.png">

### 3.2 使用EclipseKepler或更高版本

对于较新版本的Eclipse(Kepler、Luna等)，安装eCobertura可能会导致一些与JUnit相关的问题-**与Eclipse一起打包的较新版本的JUnit与eCobertura的依赖项检查器不完全兼容**：

```shell
Cannot complete the install because one or more required items could not be found.
  Software being installed: eCobertura 0.9.8.201007202152 (ecobertura.feature.group 0.9.8.201007202152)
  Missing requirement: eCobertura UI 0.9.8.201007202152 (ecobertura.ui 0.9.8.201007202152) requires 'bundle org.junit4 0.0.0' but it could not be found
  Cannot satisfy dependency:
    From: eCobertura 0.9.8.201007202152 (ecobertura.feature.group 0.9.8.201007202152)
    To: ecobertura.ui [0.9.8.201007202152]
```

**作为一种解决方法，你可以下载旧版本的JUnit并将其放入Eclipse插件文件夹中**。

这可以通过从%ECLIPSE_HOME%/plugins中删除文件夹org.junit.\*\*\*来完成，然后从与eCobertura兼容的旧Eclipse安装中复制相同的文件夹。

完成后，**重新启动Eclipse IDE**并使用相应的更新站点重新安装插件。

### 3.3 Eclipse中的代码覆盖率报告

要计算单元测试的代码覆盖率，右键单击你的项目/测试以打开上下文菜单，然后选择选项Cover As → JUnit Test。

在Coverage Session视图下，你可以查看每个类的行/分支覆盖率报告：

<img src="../assets/img_3.png">

**Java 8用户在计算代码覆盖率时可能会遇到一个常见的错误**：

```shell
java.lang.VerifyError: Expecting a stackmap frame at branch target ...
```

在这种情况下，由于新版本的Java中引入了更严格的字节码验证器，Java抱怨某些方法没有正确的堆栈映射。

**这个问题可以通过在Java虚拟机中禁用验证来解决**。

为此，请右键单击你的项目以打开上下文菜单，选择Cover As，然后打开Coverage Configurations视图。在参数选项卡中，将-noverify标志添加为VM参数。最后，点击coverage按钮启动覆盖率计算。

你也可以使用标志-XX:-UseSplitVerifier，但这仅适用于Java 6和7，因为Java 8不再支持拆分验证器。

## 4. 总结

在本文中，我们简要展示了如何使用Cobertura计算Java项目中的代码覆盖率。我们还描述了在Eclipse环境中安装eCobertura所需的步骤。

Cobertura是一个很棒且简单的代码覆盖工具，但没有得到积极的维护，因为它目前被[JaCoCo](https://www.baeldung.com/jacoco)等更新更强大的工具超越。