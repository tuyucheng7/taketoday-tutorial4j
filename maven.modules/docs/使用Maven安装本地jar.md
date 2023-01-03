## 1. 问题与选择

Maven是一个非常通用的工具，其可用的公共仓库是首屈一指的。但是，**总会有一个工件没有托管在任何地方**，或者依赖托管它的仓库存在风险，因为它可能不会在你需要时启动。

当发生这种情况时，有几种选择：

-   硬着头皮安装一个成熟的**仓库管理解决方案**，例如[Nexus](http://www.sonatype.org/nexus/)
-   尝试将工件上传到信誉更良好的公共仓库之一
-   使用Maven插件在**本地安装工件**

**Nexus**当然是更成熟的解决方案，但它也**更复杂**。提供一个实例来运行Nexus，设置Nexus本身，配置和维护它对于像使用单个jar这样简单的问题来说可能是多余的。但是，如果这种情况(托管自定义工件)很常见，那么仓库管理器就很有意义。

将工件直接上传到**公共仓库**或Maven Central也是一个很好的解决方案，但通常[是一个漫长的解决方案](https://maven.apache.org/guides/mini/guide-central-repository-upload.html)。此外，该库可能根本没有启用Maven，这使得该过程变得更加困难，因此这不是现在能够使用该工件的现实解决方案。

这就只剩下第三个选项，在源代码控制中添加工件并使用maven插件，在这种情况下，[maven-install-plugin](https://maven.apache.org/plugins/maven-install-plugin/)在构建过程需要它之前将其安装在本地，这是迄今为止最简单和最可靠的选择。

## 2. 使用maven-install-plugin安装本地Jar 

让我们从将工件安装到本地仓库所需的完整配置开始：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-install-plugin</artifactId>
    <version>2.5.1</version>
    <configuration>
        <groupId>org.somegroup</groupId>
        <artifactId>someartifact</artifactId>
        <version>1.0</version>
        <packaging>jar</packaging>
        <file>${basedir}/dependencies/someartifact-1.0.jar</file>
        <generatePom>true</generatePom>
    </configuration>
    <executions>
        <execution>
            <id>install-jar-lib</id>
            <goals>
                <goal>install-file</goal>
            </goals>
            <phase>validate</phase>
        </execution>
    </executions>
</plugin>
```

现在，让我们分解并分析此配置的详细信息。

### 2.1 工件信息

工件信息被定义为<configuration\>元素的一部分，实际语法与声明依赖项非常相似，一个groupId、artifactId和version元素。

配置的下一部分需要定义工件的打包类型，这被指定为jar。

接下来，我们需要提供**要安装的实际jar文件的位置**，这可以是绝对文件路径，也可以是相对的，使用Maven中可用的[属性](https://cwiki.apache.org/confluence/display/MAVEN/Maven+Properties+Guide)。在这种情况下，${basedir}属性表示项目的根目录，即pom.xml文件所在的位置，这意味着someartifact-1.0.jar文件需要放在根目录下的/dependencies/目录中。

最后，还可以配置其他几个[可选详细信息](https://maven.apache.org/plugins/maven-install-plugin/install-file-mojo.html)。

### 2.2 执行

**install-file目标**的执行绑定到标准Maven[构建生命周期](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)的**validate阶段**，因此，在尝试编译之前，你需要明确地运行validate阶段：

```bash
mvn validate
```

完成此步骤后，标准编译就可以工作了：

```bash
mvn clean install
```

一旦compile阶段确实执行，我们的someartifact-1.0.jar就会正确安装在我们的本地仓库中，就像可能已经从Maven Central本身检索到的任何其他工件一样。

### 2.3 生成POM与提供POM 

我们是否需要为工件提供pom.xml文件的问题主要取决于工件本身的**运行时依赖性**。简而言之，如果工件对其他jar具有运行时依赖项，则这些jar也需要在运行时**存在于类路径中**。使用一个简单的工件，这应该不是问题，因为它在运行时可能没有依赖关系(依赖关系图中的叶子)。

install-file目标中的generatePom选项应该足以满足这些类型的工件：

```xml
<generatePom>true</generatePom>
```

但是，如果工件更复杂并且确实具有重要的依赖项，那么，如果这些依赖项尚未在类路径中，则必须添加它们。一种方法是在项目的pom文件中手动定义这些新的依赖项，更好的解决方案是提供自定义pom.xml文件以及已安装的工件：

```xml
<generatePom>false</generatePom>
<pomFile>${basedir}/dependencies/someartifact-1.0.pom</pomFile>
```

这将允许Maven解析此自定义pom.xml中定义的工件的所有依赖项，而无需在项目的主pom文件中手动定义它们。

## 3. 总结

本文介绍了如何通过使用maven-install-plugin在本地安装jar，来在Maven项目中使用未托管在任何位置的jar。